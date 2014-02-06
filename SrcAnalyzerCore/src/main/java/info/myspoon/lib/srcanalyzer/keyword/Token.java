package info.myspoon.lib.srcanalyzer.keyword;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="Token")
public class Token {
	@XmlElement(name="Word")
	private String word;
	@XmlElement(name="ClassName")
	private String className;

	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	Token() {}
	Token(String word, String className) {
		this.word = word;
		this.className = className;
	}
}

@XmlRootElement(name="ArrayOfToken")
class TokenArray {
	@XmlElement(name="Token")
	Token[] tokens;
}