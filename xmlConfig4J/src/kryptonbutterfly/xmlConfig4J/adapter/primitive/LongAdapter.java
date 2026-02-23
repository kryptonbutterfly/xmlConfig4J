package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import static kryptonbutterfly.xmlConfig4J.utils.InternalConstants.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class LongAdapter implements TypeAdapter<Long>
{
	@Override
	public Class<Long> getType()
	{
		return long.class;
	}
	
	@Override
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Long value)
	{
		write(writer.getTags().valueTag(), elem, value);
	}
	
	@Override
	public Long read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(reader.getTags().valueTag(), node);
	}
	
	public static void write(String tag, Element elem, long value)
	{
		elem.setAttribute(tag, Long.toString(value));
	}
	
	public static long read(String tag, Node node)
	{
		final var value = XmlReader.getAttribute(node, tag).getValue();
		if (value.startsWith(HEX_PREFIX))
			return Long.parseUnsignedLong(value.substring(1), 16);
		else
			return Long.parseLong(value);
	}
}
