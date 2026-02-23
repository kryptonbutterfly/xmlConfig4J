package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import static kryptonbutterfly.xmlConfig4J.utils.InternalConstants.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
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
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Short value)
	{
		write(writer.getTags().valueTag(), elem, value);
	}
	
	@Override
	public Short read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(reader.getTags().valueTag(), node);
	}
	
	public static void write(String tag, Element elem, short value)
	{
		elem.setAttribute(tag, Short.toString(value));
	}
	
	public static short read(String tag, Node node)
	{
		final var value = XmlReader.getAttribute(node, tag).getValue();
		if (value.startsWith(HEX_PREFIX))
			return (short) Integer.parseUnsignedInt(value.substring(1), 16);
		else
			return (short) Integer.parseInt(value);
	}
}
