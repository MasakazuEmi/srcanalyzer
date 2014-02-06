package info.myspoon.lib.srcanalyzer.analyzer;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import info.myspoon.lib.srcanalyzer.analyzer.DecimalAnalyzer.DecimalParameter.UseDot;
import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.Block.BlockType;
import info.myspoon.lib.srcanalyzer.reader.IReader;
import info.myspoon.lib.srcanalyzer.reader.IReader.SeekOrigin;

public class DecimalAnalyzer implements IAnalyzer {
	private static final Log log = LogFactory.getLog(DecimalAnalyzer.class);

	public static class DecimalParameter {
		public enum UseDot {
			use, nonUse
		}

		private String[] prefix = null;
		private String useChar = "";
		private UseDot useDot = UseDot.nonUse;
		private String[] suffix = null;
		private String[] expornent = null;

		public String[] getPrefix() {
			return prefix;
		}

		public void setPrefix(String[] prefix) {
			this.prefix = prefix;
		}

		public String getUseChar() {
			return useChar;
		}

		public void setUseChar(String useChar) {
			this.useChar = useChar;
		}

		public UseDot getUseDot() {
			return useDot;
		}

		public void setUseDot(UseDot useDot) {
			this.useDot = useDot;
		}

		public String[] getSuffix() {
			return suffix;
		}

		public void setSuffix(String[] suffix) {
			this.suffix = suffix;
		}

		public String[] getExpornent() {
			return expornent;
		}

		public void setExpornent(String[] expornent) {
			this.expornent = expornent;
		}

		public DecimalParameter(String[] prefix, String useChar, UseDot useDot,
				String[] suffix, String[] exp) {
			this.prefix = prefix;
			this.useChar = useChar;
			this.useDot = useDot;
			this.suffix = suffix;
			this.expornent = exp;

			if (this.suffix != null) {
				Arrays.sort(this.suffix, new Comparator<String>() {
					public int compare(String o1, String o2) {
						return o2.length() - o1.length();
					}
				});
			}
		}
	}

	private BlockType type;
	private DecimalParameter parameter;

	public DecimalAnalyzer(DecimalParameter parameter, BlockType type) {
		this.parameter = parameter;
		this.type = type;
	}

	@Override
	public Block[] parse(IReader reader) {
		StringBuffer buffer = new StringBuffer();
		DecimalParameter p = parameter;

		// プレフィックス
		if (p.getPrefix() != null && p.getPrefix().length > 0) {
			for (String prefix : p.getPrefix()) {
				if (reader.seekWith(prefix))
					buffer.append(prefix);
			}

			if (buffer.length() == 0)
				return null;
		}

		// 整数部
		String integerPart = reader.readIf(p.useChar);
		if (integerPart.length() == 0) {
			reader.seek(-buffer.length(), SeekOrigin.Current);
			return null;
		}
		buffer.append(integerPart);

		if (log.isInfoEnabled())
			log.info("start parse");

		// 小数部
		if (p.useDot == UseDot.use && reader.seekWith(".")) {
			buffer.append(".");
			String fractionPart = reader.readIf(p.useChar);
			if (fractionPart.length() == 0) {
				// '.'分戻して終了
				reader.seek(-1, SeekOrigin.Current);
				buffer.setLength(buffer.length() - 1);
				return new Block[] { new Block(buffer.toString(), type) };
			}
			buffer.append(fractionPart);
		}
		// 指数部
		if (p.expornent != null) {
			boolean exist = false;
			for (String exp : p.expornent)
				if (reader.seekWith(exp)) {
					buffer.append(exp);
					exist = true;
				}
			if (exist) {
				int c = reader.peek();
				if (c != -1 && "+-".indexOf(c) >= 0)
					buffer.append((char) c);
				String expPart = reader.readIf(p.useChar);
				if (expPart.length() == 0)
					return new Block[] { new Block(buffer.toString(), type) };
				buffer.append(expPart);
			}
		}
		// サフィックス
		if (p.suffix != null) {
			for (String suff : p.suffix)
				if (reader.seekWith(suff)) {
					buffer.append(suff);
					break;
				}
		}
		if (log.isInfoEnabled())
			log.info("start parse");
		return new Block[] { new Block(buffer.toString(), type) };
	}

}
