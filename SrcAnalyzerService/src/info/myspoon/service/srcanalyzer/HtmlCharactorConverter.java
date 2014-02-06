package info.myspoon.service.srcanalyzer;

import org.apache.commons.lang3.StringEscapeUtils;

public class HtmlCharactorConverter {
	private boolean isConvertWhirteSpace;
	private boolean isUseBR;
	public HtmlCharactorConverter(boolean isConvertWhiteSpace, boolean isUseBR) {
		this.isConvertWhirteSpace = isConvertWhiteSpace;
		this.isUseBR = isUseBR;
	}

	String convert(char c) {
		if(c == ' ' && isConvertWhirteSpace) {
			return "&nbsp";
		}
		if(c == '\n' && isUseBR) {
			return "<br/>\n";
		}
		return StringEscapeUtils.escapeHtml4(String.valueOf(c));
	}

	public String convert(String s) {
		StringBuffer sb = new StringBuffer();
		for(char c : s.toCharArray())
			sb.append(convert(c));
		return sb.toString();
	}
}
