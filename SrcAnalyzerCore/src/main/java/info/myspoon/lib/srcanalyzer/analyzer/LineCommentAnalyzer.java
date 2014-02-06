package info.myspoon.lib.srcanalyzer.analyzer;

import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.Block.BlockType;
import info.myspoon.lib.srcanalyzer.reader.IReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LineCommentAnalyzer implements IAnalyzer {
	private static final Log log = LogFactory.getLog(LineCommentAnalyzer.class);

	private String start;
	private BlockType type;

	public LineCommentAnalyzer(String start, BlockType type) {
		this.start = start;
		this.type = type;
	}

	@Override
	public Block[] parse(IReader reader) {
		if (log.isInfoEnabled())
			log.info("start parse");

		if (!reader.startWith(start))
			return null;

		Block block = new Block(reader.readLine(), type);

		if (log.isInfoEnabled())
			log.info("end parse");
		return new Block[] { block };
	}
}
