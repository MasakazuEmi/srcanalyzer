package info.myspoon.lib.srcanalyzer.generator;

import info.myspoon.lib.srcanalyzer.analyzer.BlockAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.DecimalAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.DecimalAnalyzer.DecimalParameter;
import info.myspoon.lib.srcanalyzer.analyzer.DecimalAnalyzer.DecimalParameter.UseDot;
import info.myspoon.lib.srcanalyzer.analyzer.IAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.KeywordAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.LineCommentAnalyzer;
import info.myspoon.lib.srcanalyzer.analyzer.TokenAnalyzer;
import info.myspoon.lib.srcanalyzer.generator.GeneratorFactory.Type;
import info.myspoon.lib.srcanalyzer.keyword.TokenFactory;
import info.myspoon.lib.srcanalyzer.model.Block.BlockType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class VBToXmlGenerator extends SrcToXmlGenerator {

	public VBToXmlGenerator(TokenFactory tokenFactory) {
		super(tokenFactory);
	}

	@Override
	Collection<IAnalyzer> setupAnalyzer() {
		Collection<IAnalyzer> analyzers = new ArrayList<IAnalyzer>();
		analyzers.add(new BlockAnalyzer("\"", "\"", '\"', BlockType.Literal));
		analyzers.add(new BlockAnalyzer("#", "#", BlockType.Literal));	//日付リテラル
		analyzers.add(new LineCommentAnalyzer("'", BlockType.Comment));
		analyzers.add(new KeywordAnalyzer(new TokenAnalyzer(), getToken(Type.vb)));

		//数値
		String hexChar = "0123456789abcdefABCDEF";
		String octChar = "012345678";
		String figChar = "0123456789";
		String[] integerSuffix = {
			"S","I","L","s","i","l"
		};
		String[] floatingSuffix = {
			"F","R","D","f","r","d"
		};
		String[] suffix = new String[integerSuffix.length + floatingSuffix.length];
		List<String> suffixList = Arrays.asList(integerSuffix);
		suffixList.addAll(Arrays.asList(floatingSuffix));
		suffix = suffixList.toArray(suffix);

		DecimalParameter[] parameter = new DecimalParameter[]{
			new DecimalParameter(new String[]{"&h", "&H"}, hexChar, UseDot.nonUse, integerSuffix, null),
			new DecimalParameter(new String[]{"&o", "&O"}, octChar, UseDot.nonUse, integerSuffix, null),
			new DecimalParameter(null, figChar, UseDot.use, suffix, new String[]{ "e", "E" }),
			new DecimalParameter(new String[]{"."}, figChar, UseDot.use, suffix, new String[]{"e", "E"})
		};
		for(DecimalParameter p : parameter)
			analyzers.add(new DecimalAnalyzer(p, BlockType.Literal));

		return analyzers;
	}
}
