package info.myspoon.lib.srcanalyzer.analyzer;

import java.util.List;

import info.myspoon.lib.srcanalyzer.generator.ISourceXmlGenerator;
import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.Block.BlockType;

public class TagAnalyzer extends InnerBlockAnalyzer {

	public TagAnalyzer(BlockAnalyzer analyzer, ISourceXmlGenerator innerGenerator) {
		super(analyzer, innerGenerator);
	}

	@Override
	protected List<Block> convertBlockType(List<Block> blocks) {
		boolean findTagName = false;
		// 最初のトークンをタグ名、2番目以降のトークンはすべてタグ属性とする。
		for (int i = 0; i < blocks.size(); i++) {
			// =の後ろはリテラルにする。
			if (blocks.get(i).getSrc().trim().equals("=")) {
				i++;
				for (; i < blocks.size(); i++) {
					if (BlockType.toBlockType(blocks.get(i).getType()) == BlockType.Identifier) {
						blocks.get(i).setType(BlockType.Literal.toString());
						break;
					}
					if (blocks.get(i).getSrc().trim().length() != 0)
						break;
				}
			}
			// その他はタグ属性
			if (BlockType.toBlockType(blocks.get(i).getType()) == BlockType.Identifier) {
				blocks.get(i).setType(
						!findTagName ? BlockType.TagName.toString()
								: BlockType.TagAttribute.toString());
				findTagName = true;
			}
		}
		return blocks;
	}
}
