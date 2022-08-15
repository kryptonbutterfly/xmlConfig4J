module de.tinycodecrank.xmlConfig4J
{
	exports de.tinycodecrank.xmlConfig4J.parser;
	exports de.tinycodecrank.xmlConfig4J.utils;
	exports de.tinycodecrank.xmlConfig4J;
	exports de.tinycodecrank.xmlConfig4J.parser.primitiv;
	exports de.tinycodecrank.xmlConfig4J.parser.assignable;
	exports de.tinycodecrank.xmlConfig4J.annotations;
	exports de.tinycodecrank.xmlConfig4J.parser.wrapping;
	
	requires transitive java.xml;
	requires de.tinycodecrank.ReflectionUtils;
}