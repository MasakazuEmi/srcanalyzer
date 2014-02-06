package info.myspoon.lib.srcanalyzer.generator;

import info.myspoon.lib.srcanalyzer.analyzer.IAnalyzer;
import info.myspoon.lib.srcanalyzer.keyword.TokenFactory;

import java.util.ArrayList;
import java.util.Collection;

public class NoneToXmlGenerator extends SrcToXmlGenerator {

	public NoneToXmlGenerator(TokenFactory tokenFactory) {
		super(tokenFactory);
	}

	@Override
	Collection<IAnalyzer> setupAnalyzer() {
		return new ArrayList<IAnalyzer>();
	}
}
