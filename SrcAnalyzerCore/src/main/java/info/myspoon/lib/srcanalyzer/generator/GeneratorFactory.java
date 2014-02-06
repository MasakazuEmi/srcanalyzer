package info.myspoon.lib.srcanalyzer.generator;

import info.myspoon.lib.srcanalyzer.keyword.TokenFactory;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeneratorFactory {
	private static final Log log = LogFactory.getLog(GeneratorFactory.class);
	public enum Type {
		java,
		jsp,
		cs,
		vb,
		vbs,
		js,
		xml,
		aspx,
		html,
		other;

		public static Type getType(String type) {
			for(Type t : Type.values())
				if(t.name().equals(type))
					return t;
			return other;
		}
	}

	@SuppressWarnings("serial")
	private static Map<Type, Class<? extends SrcToXmlGenerator>> generatorMap = new HashMap<Type, Class<? extends SrcToXmlGenerator>>() {
		{
			put(Type.java, JavaToXmlGenerator.class);
			put(Type.cs, CSToXmlGenerator.class);
			put(Type.vb, VBToXmlGenerator.class);
			put(Type.vbs, VbsToXmlGenerator.class);
			put(Type.js, JSToXmlGenerator.class);
			put(Type.xml, AspxToXmlGenerator.class);
			put(Type.aspx, AspxToXmlGenerator.class);
			put(Type.html, AspxToXmlGenerator.class);
			put(null, NoneToXmlGenerator.class);
		}
		@Override
		public Class<? extends SrcToXmlGenerator> get(Object key) {
			if(containsKey(key))
				return super.get(key);
			return super.get(null);
		}
	};


	private TokenFactory tokenFactory;
	public GeneratorFactory(TokenFactory tokenFactory) {
		this.tokenFactory = tokenFactory;
	}

	public SrcToXmlGenerator createGenerator(Type type) {
		if(log.isInfoEnabled())
			log.info("createGenerator(" + type + ")");

		Class<? extends SrcToXmlGenerator> clazz = generatorMap.get(type);
		SrcToXmlGenerator generator;
		try {
			generator = clazz.getConstructor(new Class[]{ TokenFactory.class }).newInstance(tokenFactory);
		} catch(Exception e) {
			if(log.isWarnEnabled())
				log.warn("getConstructor.newInstance fail.", e);

			generator = new NoneToXmlGenerator(tokenFactory);
		}

		if(log.isInfoEnabled())
			log.info(generator.getClass().getName() + " has been created.");

		return generator;
	}
}
