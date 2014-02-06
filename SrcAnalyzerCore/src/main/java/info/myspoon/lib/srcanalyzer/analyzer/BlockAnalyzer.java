package info.myspoon.lib.srcanalyzer.analyzer;

import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.Block.BlockType;
import info.myspoon.lib.srcanalyzer.reader.IReader;
import info.myspoon.lib.srcanalyzer.reader.IReader.SeekOrigin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BlockAnalyzer implements IAnalyzer {
	private static final Log log = LogFactory.getLog(BlockAnalyzer.class);

	private BlockType type;
	private String start;
	private String end;
	private char escape;

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

	public BlockAnalyzer(String start, String end, BlockType type) {
		this(start, end, '\0', type);
	}

	public BlockAnalyzer(String start, String end, char escape, BlockType type) {
		this.start = start;
		this.end = end;
		this.escape = escape;
		this.type = type;
	}

	@Override
	public Block[] parse(IReader reader) {
		if (!reader.startWith(start))
			return null;

		if (log.isInfoEnabled())
			log.info("start parse");

		reader.seek(start.length(), SeekOrigin.Current);
		StringBuffer span = new StringBuffer(start);

		int n = -1;
		while (!reader.startWith(end) && (n = reader.read()) != -1) {
			char c = (char) n;
			span.append((char) n);

			if (c == escape && (n = reader.read()) != -1)
				span.append((char) n);
		}
		if (reader.seekWith(end)) {
			span.append(end);
		}
		Block block = new Block(span.toString(), type);

		if (log.isInfoEnabled()) {
			log.info("end parse");
		}
		return new Block[] { block };
	}

}
