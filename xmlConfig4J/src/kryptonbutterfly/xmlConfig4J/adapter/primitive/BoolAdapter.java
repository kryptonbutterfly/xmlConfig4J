package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class BoolAdapter implements TypeAdapter<Boolean>
{
	@Override
	public Class<Boolean> getType()
	{
		return boolean.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Boolean value)
	{
		write(elem, value);
	}
	
	@Override
	public Boolean read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(node);
	}
	
	public static void write(Element elem, boolean value)
	{
		elem.setAttribute(XmlDataBinding.VALUE, Boolean.toString(value));
	}
	
	public static boolean read(Node node)
	{
		final var value = XmlReader.getAttribute(node, XmlDataBinding.VALUE);
		return Boolean.parseBoolean(value.getValue());
	}
}
