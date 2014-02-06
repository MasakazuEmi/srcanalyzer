package info.myspoon.lib.srcanalyzer.keyword;

import info.myspoon.lib.srcanalyzer.generator.GeneratorFactory;

public class TokenFactory {
	private String keywordDirectory;
	public TokenFactory(String keywordDirectory) {
		this.keywordDirectory = keywordDirectory;
	}

	public ITokenLoader createTokenLoader(GeneratorFactory.Type type) {
		return new TokenLoader(type, keywordDirectory);
	}
}