package info.myspoon.lib.srcanalyzer.reader;

public interface IReader {
	public enum SeekOrigin {
		Begin,
		Current,
		End,
	}

	public int peek();

	public int read();

	public String readLine();

	public boolean startWith(String start);

	public long seek(long offset, SeekOrigin origin);

	public boolean seekWith(String start);

	public String readIf(String useChar);

	public String readToEndWithoutSeek();
}
