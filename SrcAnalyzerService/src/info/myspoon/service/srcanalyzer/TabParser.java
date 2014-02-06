package info.myspoon.service.srcanalyzer;


public class TabParser {
	private int tabSize;
	private String tab = "";

	public TabParser(int tabSize) {
		this.tabSize = tabSize;
		for(int i = 0; i < tabSize; i++)
			tab += ' ';
	}

	public String parse(String src) {
		int column = 0;

		String s = src.replaceAll("\r\n", "\n");
		StringBuffer sb = new StringBuffer();
		for(char c : s.toCharArray()) {
			if (c == '\t') {
				column = 0;
				sb.append(tab.substring(0, tabSize - column % tabSize));
			} else if (c == '\n') {
				column = 0;
				sb.append(c);
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
