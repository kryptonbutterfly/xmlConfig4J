module kryptonbutterfly.xmlConfig4J
{
	exports kryptonbutterfly.xmlConfig4J;
	exports kryptonbutterfly.xmlConfig4J.annotations;
	exports kryptonbutterfly.xmlConfig4J.exceptions;
	exports kryptonbutterfly.xmlConfig4J.adapter;
	exports kryptonbutterfly.xmlConfig4J.adapter.primitive;
	exports kryptonbutterfly.xmlConfig4J.adapter.boxed;
	exports kryptonbutterfly.xmlConfig4J.adapter.arrays;
	exports kryptonbutterfly.xmlConfig4J.adapter.misc;
	exports kryptonbutterfly.xmlConfig4J.adapter.collections;
	
	requires transitive java.xml;
	requires transitive java.desktop;
}