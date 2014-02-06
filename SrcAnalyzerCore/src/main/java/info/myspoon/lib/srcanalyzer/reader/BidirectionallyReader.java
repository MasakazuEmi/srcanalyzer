package info.myspoon.lib.srcanalyzer.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class BidirectionallyReader implements IReader {
	private String original;
	private StringReader reader;

	BidirectionallyReader(String s) {
		reader = new StringReader(s);
		original = s;
	}

	/* (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.reader.IReader#peek()
	 */
	@Override
	public int peek() {
		try {
			reader.mark(0);
			int c = reader.read();
			reader.reset();
			return c;
		} catch (IOException e) {
			return -1;
		}
	}

	/* (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.reader.IReader#read()
	 */
	@Override
	public int read() {
		try {
			return reader.read();
		} catch (IOException e) {
			return -1;
		}
	}

	/* (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.reader.IReader#readLine()
	 */
	@Override
	public String readLine() {
		try {
			reader.mark(0);
			String line = new BufferedReader(reader).readLine();
			reader.reset();
			seek(line.length(), SeekOrigin.Current);
			return line;
		} catch (IOException e) {
			return "";
		}
	}

	/* (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.reader.IReader#startWith(java.lang.String)
	 */
	@Override
	public boolean startWith(String start) {
		try {
			reader.mark(0);
			char[] cbuf = new char[start.length()];
			reader.read(cbuf);
			reader.reset();
			return start.startsWith(new String(cbuf));
		} catch (IOException e) {
			return false;
		}
	}

	/* (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.reader.IReader#seek(long, info.myspoon.lib.srcanalyzer.reader.BidirectionallyReader.SeekOrigin)
	 */
	@Override
	public long seek(long offset, SeekOrigin origin) {
		try {
		switch(origin) {
			case Begin:
				reader = new StringReader(original);
				break;
			case Current:
				reader.skip(offset);
				break;
			case End:
				reader = new StringReader(original);
				reader.skip(original.length() + offset);
				break;
			}
			return offset;
		} catch(IOException e) {
			return 0;
		}
	}

	/*
	 * (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.reader.IReader#seekWith(java.lang.String)
	 */
	public boolean seekWith(String start) {
		if (startWith(start)) {
			seek(start.length(), SeekOrigin.Current);
			return true;
		}
		return false;
	}

	/*
	 * (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.reader.IReader#readIf(java.lang.String)
	 */
	public String readIf(String useChar) {
		if(useChar.indexOf(peek()) < 0)
			return "";
		int c;
		StringBuffer buffer = new StringBuffer();
		while((c = peek()) != -1 && useChar.indexOf(c) >= 0) {
			buffer.append((char)c);
			seek(1, SeekOrigin.Current);
		}
		return buffer.toString();
	}

	/*
	 * (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.reader.IReader#readToEndWithoutSeek()
	 */
	@Override
	public String readToEndWithoutSeek() {
		try {
			final int BUFFSIZE = 255;
			char[] buffer = new char[BUFFSIZE];
			StringBuffer readBuffer = new StringBuffer();
			int size = 0;
			reader.mark(0);
			while ((size = reader.read(buffer)) != -1) {
				readBuffer.append(buffer, 0, size);
				if (size < BUFFSIZE)
					break;
			}
			reader.reset();
			return readBuffer.toString();
		} catch (IOException e) {
			return null;
		}
	}

}
