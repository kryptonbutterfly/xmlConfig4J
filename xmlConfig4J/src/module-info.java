module kryptonbutterfly.xmlConfig4J
{
	exports kryptonbutterfly.xmlConfig4J.parser;
	exports kryptonbutterfly.xmlConfig4J.utils;
	exports kryptonbutterfly.xmlConfig4J;
	exports kryptonbutterfly.xmlConfig4J.parser.primitiv;
	exports kryptonbutterfly.xmlConfig4J.parser.assignable;
	exports kryptonbutterfly.xmlConfig4J.annotations;
	exports kryptonbutterfly.xmlConfig4J.parser.wrapping;
	
	requires transitive java.xml;
	requires kryptonbutterfly.ReflectionUtils;
}