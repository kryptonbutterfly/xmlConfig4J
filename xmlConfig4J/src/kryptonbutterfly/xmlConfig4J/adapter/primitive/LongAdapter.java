package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class LongAdapter implements TypeAdapter<Long>
{
	@Override
	public Class<Long> getType()
	{
		return long.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Long value)
	{
		write(elem, value);
	}
	
	@Override
	public Long read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(node);
	}
	
	public static void write(Element elem, long value)
	{
		elem.setAttribute(XmlDataBinding.VALUE, Long.toString(value));
	}
	
	public static long read(Node node)
	{
		final var value = XmlReader.getAttribute(node, XmlDataBinding.VALUE).getValue();
		if (value.startsWith("#"))
			return Long.parseUnsignedLong(value.substring(1), 16);
		else
			return Long.parseLong(value);
	}
}
