package info.myspoon.service.srcanalyzer;

import info.myspoon.lib.srcanalyzer.generator.GeneratorFactory;
import info.myspoon.lib.srcanalyzer.generator.ISourceXmlGenerator;
import info.myspoon.lib.srcanalyzer.keyword.TokenFactory;
import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.model.Source;
import info.myspoon.service.util.Proxy;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Path("/srcanalyzer")
public class SrcAnalyzerService {
	private static final Log log = LogFactory.getLog(SrcAnalyzerService.class);


	@Path("{type}")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Source getJSON(@Context ServletConfig servletConfig, @PathParam("type") String srcType, @QueryParam("src") String src) {
		if(log.isInfoEnabled())
			log.info("start getJSON");

		Source s = parse(servletConfig, srcType, src);

		if(log.isInfoEnabled())
			log.info("end getJSON");
		return s;
	}

	@Path("{type}")
	@GET
	@Produces({MediaType.TEXT_HTML})
	public String getHtml(@Context ServletConfig servletConfig, @PathParam("type") String srcType, @QueryParam("src") String src) {
		if(log.isInfoEnabled())
			log.info("start getHTML");

		StringBuffer sb = new StringBuffer();
		String css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"/code.css\" />\n";
		sb.append("<html>\n<head>\n" + css + "</head>\n<body>\n<code>");
		sb.append(getHtmlCode(servletConfig, srcType, src));
		sb.append("\n</code>\n</body>\n</html>");

		if(log.isInfoEnabled())
			log.info("end getHTML");

		return sb.toString();
	}

	@Path("{type}")
	@GET
	@Produces({MediaType.TEXT_PLAIN})
	public String getHtmlCode(@Context ServletConfig servletConfig, @PathParam("type") String srcType, @QueryParam("src") String src) {
		final String TAG = "span";
		final int TAB = 4;

		Source s = parse(servletConfig, srcType, new TabParser(TAB).parse(src));

		StringBuilder sb = new StringBuilder();
		HtmlCharactorConverter converter = new HtmlCharactorConverter(true, true);
		for(Block b : s.getBlocks()) {
			String code = converter.convert(b.getSrc());
			String blockType = b.getType();
			if(blockType != null && !blockType.isEmpty())
				sb.append(String.format("<%s class=%s>%s</%s>", TAG, blockType, code, TAG));
			else
				sb.append(code);
		}
		return sb.toString();
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Source getJSON(@Context ServletConfig servletConfig, @QueryParam("url") String url) throws Exception {
		if(log.isInfoEnabled())
			log.info("start getJSON");

		try {
			return getJSON(servletConfig, getSrcType(url), getSrc(url));
		} finally {
			if(log.isInfoEnabled())
				log.info("end getJSON");
		}
	}

	@GET
	@Produces({MediaType.TEXT_HTML})
	public String getHTML(@Context ServletConfig servletConfig, @QueryParam("url") String url) throws Exception {
		if(log.isInfoEnabled())
			log.info("start getHTML");

		try {
			return getHtml(servletConfig, getSrcType(url), getSrc(url));
		} finally {
			if(log.isInfoEnabled())
				log.info("end getHTML");
		}
	}

	@Path("code")
	@GET
	@Produces({MediaType.TEXT_PLAIN})
	public String getHTMLCode(@Context ServletConfig servletConfig, @QueryParam("url") String url) throws Exception {
		if(log.isInfoEnabled())
			log.info("start getHTML");

		try {
			return getHtmlCode(servletConfig, getSrcType(url), getSrc(url));
		} finally {
			if(log.isInfoEnabled())
				log.info("end getHTML");
		}
	}


	private String getSrc(String url) throws IOException, MalformedURLException {
		return new Proxy().getHtml(url);
	}

	private String getSrcType(String url) {
		return url.substring(url.lastIndexOf(".")+ 1);
	}

	private Source parse(ServletConfig config, String srcType, String src) {
		ISourceXmlGenerator generator = createSrcXmlGenerator(config, srcType);
		return generator.parse(src);
	}

	private ISourceXmlGenerator createSrcXmlGenerator(ServletConfig config, String srcType) {
		String keywordDirectory = getXmlPath(config);
		GeneratorFactory factory = new GeneratorFactory(new TokenFactory(keywordDirectory));
		return factory.createGenerator(GeneratorFactory.Type.getType(srcType));
	}

	private String getXmlPath(ServletConfig config) {
		return config.getServletContext().getRealPath("/WEB-INF/xml");
	}
}
