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

public class VbsToXmlGenerator extends SrcToXmlGenerator {

	public VbsToXmlGenerator(TokenFactory tokenFactory) {
		super(tokenFactory);
	}

	@Override
	Collection<IAnalyzer> setupAnalyzer() {
		Collection<IAnalyzer> analyzers = new ArrayList<IAnalyzer>();
		analyzers.add(new BlockAnalyzer("\"", "\"", '\"', BlockType.Literal));
		analyzers.add(new BlockAnalyzer("#", "#", BlockType.Literal));	//日付リテラル
		analyzers.add(new LineCommentAnalyzer("'", BlockType.Comment));
		analyzers.add(new KeywordAnalyzer(new TokenAnalyzer(), getToken(Type.js)));

		//数値
		String figChar = "0123456789";

		DecimalParameter[] parameter = new DecimalParameter[]{
			new DecimalParameter(null, figChar, UseDot.use, null, new String[]{ "e", "E" }),
			new DecimalParameter(new String[]{"."}, figChar, UseDot.use, null, new String[]{"e", "E"})
		};
		for(DecimalParameter p : parameter)
			analyzers.add(new DecimalAnalyzer(p, BlockType.Literal));

		return analyzers;
	}
}
