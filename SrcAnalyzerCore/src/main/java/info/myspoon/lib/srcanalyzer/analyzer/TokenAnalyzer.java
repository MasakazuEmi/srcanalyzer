package info.myspoon.lib.srcanalyzer.analyzer;

import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.Block.BlockType;
import info.myspoon.lib.srcanalyzer.reader.IReader;

public class TokenAnalyzer implements IAnalyzer {

	public TokenAnalyzer() {
	}

	@Override
	public Block[] parse(IReader reader) {
		if (!isBeginKeyword(reader))
			return null;

		String token = getToken(reader);
		if (token == null)
			return null;

		return new Block[] { new Block(token, BlockType.Identifier) };
	}

	public boolean isBeginKeyword(IReader reader) {
		char c = (char) reader.peek();
		return Character.isLetter(c) || c == '_';
	}

	public boolean isDelim(char c) {
		return !Character.isLetter(c) && !Character.isDigit(c) && c != '_';
	}

	protected String getToken(IReader reader) {
		int c;
		StringBuffer keyword = new StringBuffer();
		while ((c = reader.peek()) != -1) {
			if (isDelim((char) c))
				break;
			keyword.append((char) reader.read());
		}
		return keyword.toString();
	}
}
