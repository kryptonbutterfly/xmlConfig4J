package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
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
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Boolean value)
	{
		write(writer.getTags().valueTag(), elem, value);
	}
	
	@Override
	public Boolean read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(reader.getTags().valueTag(), node);
	}
	
	public static void write(String tag, Element elem, boolean value)
	{
		elem.setAttribute(tag, Boolean.toString(value));
	}
	
	public static boolean read(String tag, Node node)
	{
		final var value = XmlReader.getAttribute(node, tag);
		return Boolean.parseBoolean(value.getValue());
	}
}
