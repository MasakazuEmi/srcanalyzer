package info.myspoon.lib.srcanalyzer.generator;

import info.myspoon.lib.srcanalyzer.analyzer.IAnalyzer;
import info.myspoon.lib.srcanalyzer.keyword.Token;
import info.myspoon.lib.srcanalyzer.keyword.TokenFactory;
import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.ModelFactory;
import info.myspoon.lib.srcanalyzer.model.Source;
import info.myspoon.lib.srcanalyzer.reader.IReader;
import info.myspoon.lib.srcanalyzer.reader.ReaderFactory;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class SrcToXmlGenerator implements ISourceXmlGenerator {
	private static final Log log = LogFactory.getLog(SrcToXmlGenerator.class);

	private TokenFactory tokenFactory;

	public TokenFactory getTokenFactory() {
		return tokenFactory;
	}

	SrcToXmlGenerator(TokenFactory tokenFactory) {
		this.tokenFactory = tokenFactory;
	}

	Map<String, Token> getToken(GeneratorFactory.Type type) {
		return tokenFactory.createTokenLoader(type).load();
	}

	abstract Collection<IAnalyzer> setupAnalyzer();

	Block[] addedBlockNextParse(IReader reader, Block[] blocks) {
		return null;
	}

	/* (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.generator.ISourceXmlGenerator#parse(java.lang.String)
	 */
	@Override
	public Source parse(String src) {
		ModelFactory modelFactory = new ModelFactory();
		Collection<IAnalyzer> analyzers = setupAnalyzer();
		IReader reader = new ReaderFactory().createReader(src);

		Source source = modelFactory.createSource();
		List<Block> blocks = source.getBlocks();
		Block normalBlock = null;
		while(reader.peek() != -1) {
			Block[] analyzedBlocks = null;
			for(IAnalyzer analyzer : analyzers) {
				analyzedBlocks = analyzer.parse(reader);
				if(analyzedBlocks != null && log.isInfoEnabled()) {
					for(Block b : analyzedBlocks)
						log.info(b);
				}
				if(analyzedBlocks != null) {
					for(Block b : analyzedBlocks)
						blocks.add(b);
					Block[] additionBlocks;
					while((additionBlocks = addedBlockNextParse(reader, analyzedBlocks)) != null) {
						for(Block b : additionBlocks) {
							blocks.add(b);
						}
						analyzedBlocks = additionBlocks;
					}
					normalBlock = null;
					break;
				}
			}
			if(analyzedBlocks == null) {
				if(normalBlock == null) {
					normalBlock = modelFactory.createBlock("");;
					source.getBlocks().add(normalBlock);
				}
				normalBlock.setSrc(normalBlock.getSrc() + (char)reader.read());
			}
		}
		return source;
	}

	/* (非 Javadoc)
	 * @see info.myspoon.lib.srcanalyzer.generator.ISourceXmlGenerator#writeXml(info.myspoon.lib.srcanalyzer.model.Source)
	 */
	@Override
	public String writeXml(Source source) {
		Writer w = new StringWriter();
		JAXB.marshal(source, w);
		return w.toString();
	}
}
