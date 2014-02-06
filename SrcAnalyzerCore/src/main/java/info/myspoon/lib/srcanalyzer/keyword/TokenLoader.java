package info.myspoon.lib.srcanalyzer.keyword;

import info.myspoon.lib.srcanalyzer.generator.GeneratorFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TokenLoader implements ITokenLoader {
	private static final Log log = LogFactory.getLog(TokenLoader.class);

	protected final String FILE_NAME_FORMAT = "Token_%s.xml";

	private GeneratorFactory.Type type;
	private String directoryName;

	TokenLoader(GeneratorFactory.Type type, String directoryName) {
		this.type = type;
		this.directoryName = directoryName;
	}

	/* (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.keyword.ITokenLoader#load()
	 */
	@Override
	public Map<String, Token> load() {
		File file = new File(directoryName, String.format(FILE_NAME_FORMAT, type.toString()));

		if(log.isInfoEnabled())
			log.info(String.format("start load. filename is %s. exist returns %s", file.getPath(), file.exists()));

		if(file.exists()) {
			Token[] tokens = JAXB.unmarshal(file, TokenArray.class).tokens;
			if(tokens != null) {
				Map<String, Token> map = new HashMap<String, Token>();
				for(Token t : tokens)
					map.put(t.getWord(), t);
				return map;
			}
		}
		return new HashMap<String, Token>();
	}
}
