package info.myspoon.lib.srcanalyzer.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.myspoon.lib.srcanalyzer.generator.ISourceXmlGenerator;
import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.Block.BlockType;
import info.myspoon.lib.srcanalyzer.model.Source;
import info.myspoon.lib.srcanalyzer.reader.IReader;

public class InnerBlockAnalyzer implements IAnalyzer {
	private BlockAnalyzer analyzer;
	private ISourceXmlGenerator innerGenerator;

	private enum BlockPart {
		start, middle, end,
	}

	public InnerBlockAnalyzer(BlockAnalyzer analyzer,
			ISourceXmlGenerator innerGenerator) {
		this.analyzer = analyzer;
		this.innerGenerator = innerGenerator;
	}

	@Override
	public Block[] parse(IReader reader) {
		Block[] blocks = analyzer.parse(reader);
		if (blocks == null)
			return null;

		List<Block> tagBlock = new ArrayList<Block>();
		for (Block block : blocks) {
			Map<BlockPart, Block> innerBlocks = parseBlock(block);
			Source src = innerGenerator.parse(innerBlocks.get(BlockPart.middle).getSrc());

			// 変換後の種別置き換えを実施。
			List<Block> convertedBlocks = convertBlockType(src.getBlocks());

			// 変換結果を追加
			tagBlock.add(innerBlocks.get(BlockPart.start));
			tagBlock.addAll(convertedBlocks);
			tagBlock.add(innerBlocks.get(BlockPart.end));
		}
		if (tagBlock.size() == 0)
			return null;

		return tagBlock.toArray(new Block[tagBlock.size()]);
	}

	protected List<Block> convertBlockType(List<Block> blocks) {
		return blocks;
	}

	private Map<BlockPart, Block> parseBlock(Block block) {
		String src = block.getSrc();
		int startWidth = analyzer.getStart().length();
		int endWidth = analyzer.getEnd().length();
		Map<BlockPart, Block> map = new HashMap<BlockPart, Block>();
		map.put(BlockPart.start, new Block(src.substring(0, startWidth), BlockType.StartTag));
		map.put(BlockPart.middle, new Block(src.substring(startWidth, src.length() - endWidth)));
		map.put(BlockPart.end, new Block(src.substring(src.length() - endWidth, src.length()), BlockType.EndTag));
		return map;
	}
}
