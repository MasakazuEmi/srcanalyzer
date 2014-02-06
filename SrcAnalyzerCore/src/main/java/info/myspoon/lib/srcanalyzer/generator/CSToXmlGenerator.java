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
import java.util.Collection;

public class CSToXmlGenerator extends SrcToXmlGenerator {

	public CSToXmlGenerator(TokenFactory tokenFactory) {
		super(tokenFactory);
	}

	@Override
	Collection<IAnalyzer> setupAnalyzer() {

		Collection<IAnalyzer> analyzers = new ArrayList<IAnalyzer>();
		analyzers.add(new BlockAnalyzer("@\"", "\"", '"', BlockType.Literal));
		analyzers.add(new BlockAnalyzer("\"", "\"", '\\', BlockType.Literal));
		analyzers.add(new BlockAnalyzer("'", "'", '\\', BlockType.Literal));
		analyzers.add(new BlockAnalyzer("/*", "*/", BlockType.Comment));
		analyzers.add(new LineCommentAnalyzer("//", BlockType.Comment));
		analyzers.add(new KeywordAnalyzer(new TokenAnalyzer(), getToken(Type.cs)));

		//数値
		String hexChar = "0123456789abcdefABCDEF";
		String figChar = "0123456789";
		String[] suffix = {
			"u","U","l","L","ul","Ul","uL","UL","lu","lU","Lu","LU",
			"f","F","d","D","m","M"
		};
		DecimalParameter[] parameter = new DecimalParameter[]{
			new DecimalParameter(new String[]{"0x", "0X"}, hexChar, UseDot.nonUse, null, null),
			new DecimalParameter(null, figChar,  UseDot.use, suffix, new String[]{ "e", "E" }),
			new DecimalParameter(new String[]{"."}, figChar, UseDot.use, suffix, new String[]{"e", "E"})
		};
		for(DecimalParameter p : parameter)
			analyzers.add(new DecimalAnalyzer(p, BlockType.Literal));

		return analyzers;
	}
}
