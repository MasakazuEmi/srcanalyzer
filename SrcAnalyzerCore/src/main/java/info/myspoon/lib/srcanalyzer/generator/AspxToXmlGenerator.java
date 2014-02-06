package info.myspoon.lib.srcanalyzer.generator;

import info.myspoon.lib.srcanalyzer.analyzer.BlockAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.IAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.InnerBlockAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.KeywordAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.TagAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.TokenAnalyzer;
import info.myspoon.lib.srcanalyzer.generator.GeneratorFactory.Type;
import info.myspoon.lib.srcanalyzer.keyword.TokenFactory;
import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.Block.BlockType;
import info.myspoon.lib.srcanalyzer.model.Source;
import info.myspoon.lib.srcanalyzer.reader.IReader;
import info.myspoon.lib.srcanalyzer.reader.IReader.SeekOrigin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AspxToXmlGenerator extends SrcToXmlGenerator {

	private GeneratorFactory.Type pageSrcType;

	public static class TagToXmlGenerator extends SrcToXmlGenerator {
		private GeneratorFactory.Type srcType;

		public TagToXmlGenerator(TokenFactory tokenFactory, GeneratorFactory.Type srcType) {
			super(tokenFactory);
			this.srcType = srcType;
		}

		@Override
		Collection<IAnalyzer> setupAnalyzer() {
			Collection<IAnalyzer> analyzers = new ArrayList<IAnalyzer>();
			ISourceXmlGenerator src = createInnerSrcXmlGenerator(getTokenFactory(), srcType);
			analyzers.add(new InnerBlockAnalyzer(new BlockAnalyzer("<%#", "%>", BlockType.Other), src));
			analyzers.add(new InnerBlockAnalyzer(new BlockAnalyzer("<%", "%>", BlockType.Other), src));
			analyzers.add(new BlockAnalyzer("\"", "\"", BlockType.Literal));
			analyzers.add(new BlockAnalyzer("'", "'", BlockType.Literal));

			TokenAnalyzer tokenAnalyzer = new TokenAnalyzer() {
				public boolean isBeginKeyword(IReader reader) {
					char c = (char) reader.peek();
					return Character.isLetterOrDigit(c) || c == '-' || c == '%';
				}

				public boolean isDelim(char c) {
					return !Character.isLetterOrDigit(c) && c != ':' && c != '-' && c != '%';

				}
			};
			analyzers.add(new KeywordAnalyzer(tokenAnalyzer, getToken(Type.aspx)));
			return analyzers;
		}
	}

	public AspxToXmlGenerator(TokenFactory tokenFactory) {
		super(tokenFactory);
		this.pageSrcType = GeneratorFactory.Type.other;
	}

	@Override
	Collection<IAnalyzer> setupAnalyzer() {
		Collection<IAnalyzer> analyzers = new ArrayList<IAnalyzer>();
		analyzers.add(new BlockAnalyzer("<![CDATA[", "]]>", BlockType.Other));
		analyzers.add(new BlockAnalyzer("<!--", "-->", BlockType.Comment));

		ISourceXmlGenerator src = createInnerSrcXmlGenerator(getTokenFactory(), pageSrcType);
		ISourceXmlGenerator tag = new TagToXmlGenerator(getTokenFactory(), pageSrcType);
		analyzers.add(new TagAnalyzer(new BlockAnalyzer("<%@", "%>", BlockType.Normal), tag));
		analyzers.add(new InnerBlockAnalyzer(new BlockAnalyzer("<%#", "%>", BlockType.Normal), src));
		analyzers.add(new InnerBlockAnalyzer(new BlockAnalyzer("<%", "%>", BlockType.Normal), src));
		analyzers.add(new TagAnalyzer(new BlockAnalyzer("</", ">", '%', BlockType.Normal), tag));
		analyzers.add(new TagAnalyzer(new BlockAnalyzer("<", ">", '%', BlockType.Normal), tag));
		return analyzers;
	}

	private static SrcToXmlGenerator createInnerSrcXmlGenerator(TokenFactory tokenFactory, GeneratorFactory.Type srcType) {
		final List<Type> allowTypes = Arrays.asList(new Type[] { Type.vb, Type.vbs, Type.cs });
		if (allowTypes.contains(srcType))
			return new GeneratorFactory(tokenFactory).createGenerator(srcType);
		else
			return new GeneratorFactory(tokenFactory).createGenerator(Type.js);
	}

	@Override
	Block[] addedBlockNextParse(IReader reader, Block[] blocks) {
		String tagName = getTagName(blocks);
		if (tagName != null) {
			tagName = tagName.toLowerCase();

			if (tagName.equals("page")) {
				// ページのソースコード種別を決定
				pageSrcType = convertType(getTagAttributeValue(blocks, "language"));
				return null;
			}
			if (tagName.equals("script") && blocks[0].getSrc().equals("<"))
				// スクリプトブロックの解析
				return parseScriptBlock(reader, blocks);
		}

		return null;
	}

	private Block[] parseScriptBlock(IReader reader, Block[] blocks) {
		// 終了が/>で終わってないことを確認
		if (blocks.length < 3 || !blocks[blocks.length - 1].getSrc().equals(">")
				|| blocks[blocks.length - 2].getSrc().equals("/"))
			return null;

		// ソースコード種別を決定
		GeneratorFactory.Type type = pageSrcType;
		String lang = getTagAttributeValue(blocks, "language");
		if (lang != null)
			type = convertType(lang);
		else {
			String runat = getTagAttributeValue(blocks, "runat");
			if (runat != null && runat.toLowerCase().equals("server"))
				type = pageSrcType;
			else
				type = GeneratorFactory.Type.js;
		}

		// scriptブロックを解析するXML生成器を生成。
		ISourceXmlGenerator generator = createScriptBlockXmlGenerator(type);
		Source source = generator.parse(reader.readToEndWithoutSeek());

		// 読取位置を進める。
		for (Block b : source.getBlocks())
			reader.seek(b.getSrc().length(), SeekOrigin.Current);

		return source.getBlocks().toArray(new Block[source.getBlocks().size()]);
	}

	private ISourceXmlGenerator createScriptBlockXmlGenerator(final GeneratorFactory.Type srcType) {
		// スクリプトブロック用のXML生成器を作成
		final SrcToXmlGenerator generator = createInnerSrcXmlGenerator(getTokenFactory(), srcType);

		// 終了タグ検索用の解析器を追加
		ISourceXmlGenerator newGenerator = new SrcToXmlGenerator(getTokenFactory()) {
			@Override
			Collection<IAnalyzer> setupAnalyzer() {
				Collection<IAnalyzer> analyzers = generator.setupAnalyzer();
				analyzers.add(new TagAnalyzer(new BlockAnalyzer("</", ">", '%', BlockType.Normal),
						new TagToXmlGenerator(getTokenFactory(), srcType)));
				return analyzers;
			}

			@Override
			Block[] addedBlockNextParse(IReader innerReader, Block[] innerBlocks) {
				if (innerBlocks[0].getSrc().equals("</"))
					// 終了させるために末尾へ移動
					innerReader.seek(0, SeekOrigin.End);
				return null;
			}
		};
		return newGenerator;
	}

	private static GeneratorFactory.Type convertType(String type) {
		@SuppressWarnings("serial")
		final Map<String, Type> convMap = new HashMap<String, Type>() {
			{
				put("vb", Type.vb);
				put("vbscript", Type.vbs);
				put("c#", Type.cs);
			}
		};
		Type t = convMap.get(type.toLowerCase());
		if(t == null)
			t = Type.js;
		return t;
	}

	private static String getTagName(Block[] blocks) {
		for (Block b : blocks)
			if (BlockType.toBlockType(b.getType()) == BlockType.TagName)
				return b.getSrc();
		return null;
	}

	private static String getTagAttributeValue(Block[] blocks, String attribute) {
		for (int i = 0; i < blocks.length; i++) {
			Block b = blocks[i];

			if (BlockType.toBlockType(b.getType()) != BlockType.TagAttribute)
				continue;

			if (!b.getSrc().toLowerCase().equals(attribute))
				continue;

			if (i + 2 < blocks.length && blocks[i + 1].getSrc().trim().equals("=")
					&& BlockType.toBlockType(blocks[i + 2].getType()) == BlockType.Literal) {
				String value = blocks[i + 2].getSrc();
				if (value.length() == 0)
					continue;

				// 引用符がついている場合ははずす。
				if ((value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"')
						|| (value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\''))
					value = value.substring(1, value.length() - 1);
				return value;
			}
		}
		return null;
	}
}
