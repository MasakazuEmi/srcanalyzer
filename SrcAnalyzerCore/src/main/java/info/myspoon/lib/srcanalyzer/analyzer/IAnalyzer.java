package info.myspoon.lib.srcanalyzer.analyzer;

import info.myspoon.lib.srcanalyzer.model.Block;
import info.myspoon.lib.srcanalyzer.reader.IReader;

public interface IAnalyzer {
	Block[] parse(IReader reader);
}
