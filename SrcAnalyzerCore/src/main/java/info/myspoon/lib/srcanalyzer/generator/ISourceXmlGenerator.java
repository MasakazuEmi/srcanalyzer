package info.myspoon.lib.srcanalyzer.generator;

import info.myspoon.lib.srcanalyzer.model.Source;

public interface ISourceXmlGenerator {

	Source parse(String src);

	String writeXml(Source source);

}