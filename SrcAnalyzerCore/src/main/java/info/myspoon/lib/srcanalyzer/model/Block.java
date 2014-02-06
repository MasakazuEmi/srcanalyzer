package info.myspoon.lib.srcanalyzer.model;

import java.io.StringWriter;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="Block")
public class Block {
	public enum BlockType {
		/**
		 * 通常の字句
		 */
		Normal(""),
		/**
		 * コメント
		 */
		Comment("comment"),
		/**
		 * 文字リテラル
		 */
		Literal("literal"),
		/**
		 * 識別子
		 */
		Identifier("identifier"),
		/**
		 * 開始タグ
		 */
		StartTag("startTag"),
		/**
		 * 終了タグ
		 */
		EndTag("endTag"),
		/**
		 * タグ名
		 */
		TagName("tagName"),
		/**
		 * タグ属性
		 */
		TagAttribute("tagAttribute"),
		/**
		 * その他
		 */
		Other("other");

		private String typeName;
		private BlockType(String name) {
			this.typeName = name;
		}
		@Override
		public String toString() {
			return typeName;
		}
		public static BlockType toBlockType(String typeName) {
			for(BlockType type : values()) {
				if(type.toString().equals(typeName))
					return type;
			}
			return null;
		}
	}

	@XmlElement(name = "Source")
	private String src;
	@XmlElement(name = "Type")
	private String type;
	@XmlElement(name = "InnerBlock")
	private Block[] blocks;

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Block[] getBlocks() {
		return blocks;
	}

	public void setBlocks(Block[] blocks) {
		this.blocks = blocks;
	}

	public Block() {}

	public Block(String src) {
		this.src = src;
	}

	public Block(String src, String type) {
		this(src);
		this.type = type;
	}

	public Block(String src, BlockType type) {
		this(src);
		this.type = type.toString();
	}

	public String toString() {
		StringWriter w = new StringWriter();
		JAXB.marshal(this, w);
		return w.toString();
	}
}
