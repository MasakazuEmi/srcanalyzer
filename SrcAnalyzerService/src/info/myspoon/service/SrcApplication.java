package info.myspoon.service;

import info.myspoon.service.srcanalyzer.SrcAnalyzerService;
import info.myspoon.service.util.Proxy;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class SrcApplication extends Application {
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		set.add(SrcAnalyzerService.class);
		set.add(Proxy.class);
		return set;
	}
}
