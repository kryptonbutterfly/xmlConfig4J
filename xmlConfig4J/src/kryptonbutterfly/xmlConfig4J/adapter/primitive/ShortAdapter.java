package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class ShortAdapter implements TypeAdapter<Short>
{
	@Override
	public Class<Short> getType()
	{
		return short.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Short value)
	{
		write(elem, value);
	}
	
	@Override
	public Short read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(node);
	}
	
	public static void write(Element elem, short value)
	{
		elem.setAttribute(XmlDataBinding.VALUE, Short.toString(value));
	}
	
	public static short read(Node node)
	{
		final var value = XmlReader.getAttribute(node, XmlDataBinding.VALUE).getValue();
		if (value.startsWith("#"))
			return (short) Integer.parseUnsignedInt(value.substring(1), 16);
		else
			return (short) Integer.parseInt(value);
	}
}
