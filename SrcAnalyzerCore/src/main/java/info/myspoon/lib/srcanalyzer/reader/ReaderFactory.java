package info.myspoon.lib.srcanalyzer.reader;

public class ReaderFactory {
	public ReaderFactory() {}

	public IReader createReader(String s) {
		return new BidirectionallyReader(s);
	}
}
