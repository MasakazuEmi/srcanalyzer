package info.myspoon.lib.srcanalyzer.model;

import info.myspoon.lib.srcanalyzer.model.Block.BlockType;

/*
 * TODO 各Modelのコンストラクタをpackage privateとし、Modelのインスタンス生成を局所化すること
 */
public class ModelFactory {
	public Block createBlock() {
		return new Block();
	}
	public Block createBlock(String src) {
		return new Block(src);
	}
	public Block createBlock(String src, BlockType type) {
		return new Block(src, type);
	}

	public Source createSource() {
		return new Source();
	}
}
