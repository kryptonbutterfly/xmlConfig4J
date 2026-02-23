package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import static kryptonbutterfly.xmlConfig4J.utils.InternalConstants.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class IntAdapter implements TypeAdapter<Integer>
{
	@Override
	public Class<Integer> getType()
	{
		return int.class;
	}
	
	@Override
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Integer value)
	{
		write(writer.getTags().valueTag(), elem, value);
	}
	
	@Override
	public Integer read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(reader.getTags().valueTag(), node);
	}
	
	public static void write(String tag, Element elem, int value)
	{
		elem.setAttribute(tag, Integer.toString(value));
	}
	
	public static int read(String tag, Node node)
	{
		final var value = XmlReader.getAttribute(node, tag).getValue();
		if (value.startsWith(HEX_PREFIX))
			return Integer.parseUnsignedInt(value.substring(1), 16);
		else
			return Integer.parseInt(value);
	}
}
