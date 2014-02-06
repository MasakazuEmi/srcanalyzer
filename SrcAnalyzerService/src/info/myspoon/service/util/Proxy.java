package info.myspoon.service.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/proxy")
public class Proxy {
	@GET
	@Produces({MediaType.TEXT_HTML})
	public String getHtml(@QueryParam("url") String url) throws IOException, MalformedURLException {
		return getHtml(url, "UTF-8");
	}

	@Path("{charset}")
	@GET
	@Produces({MediaType.TEXT_HTML})
	public String getHtml(@QueryParam("url") String url, @PathParam("charset") String charset) throws IOException, MalformedURLException {
		URLConnection connection = new URL(url).openConnection();
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));

		final int BUFF_SIZE = 1024;
		char[] buffer = new char[BUFF_SIZE];
		int length = 0;
		while ((length = reader.read(buffer)) != -1)
			builder.append(buffer, 0, length);
		return builder.toString();
	}
}
