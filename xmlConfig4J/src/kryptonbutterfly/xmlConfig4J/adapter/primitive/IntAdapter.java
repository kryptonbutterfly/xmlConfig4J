package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class IntAdapter implements TypeAdapter<Integer>
{
	@Override
	public Class<Integer> getType()
	{
		return int.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Integer value)
	{
		write(elem, value);
	}
	
	@Override
	public Integer read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(node);
	}
	
	public static void write(Element elem, int value)
	{
		elem.setAttribute(XmlDataBinding.VALUE, Integer.toString(value));
	}
	
	public static int read(Node node)
	{
		final var value = XmlReader.getAttribute(node, XmlDataBinding.VALUE).getValue();
		if (value.startsWith("#"))
			return Integer.parseUnsignedInt(value.substring(1), 16);
		else
			return Integer.parseInt(value);
	}
}
