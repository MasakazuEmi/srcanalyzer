package info.myspoon.lib.srcanalyzer.analyzer;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import info.myspoon.lib.srcanalyzer.keyword.Token;
import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.reader.IReader;

public class KeywordAnalyzer implements IAnalyzer {
	private static final Log log = LogFactory.getLog(KeywordAnalyzer.class);

	private TokenAnalyzer analyzer;
	private Map<String, Token> tokens;

	public KeywordAnalyzer(TokenAnalyzer analyzer, Map<String, Token> tokens) {
		this.analyzer = analyzer;
		this.tokens = tokens;
	}

	@Override
	public Block[] parse(IReader reader) {
		Block[] blocks = analyzer.parse(reader);
		if (blocks == null)
			return null;

		if (log.isInfoEnabled())
			log.info("start parse");

		for (Block b : blocks) {
			String keywordType = getKeywordType(b.getSrc());
			if (keywordType != null)
				b.setType(keywordType);
		}
		if (log.isInfoEnabled())
			log.info("end parse");
		return blocks;
	}

	protected String getKeywordType(String keyword) {
		if (!tokens.containsKey(keyword))
			return null;
		return tokens.get(keyword).getClassName();
	}

}
