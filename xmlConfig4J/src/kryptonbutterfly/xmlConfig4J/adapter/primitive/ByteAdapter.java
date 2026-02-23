package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import static kryptonbutterfly.xmlConfig4J.utils.InternalConstants.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class ByteAdapter implements TypeAdapter<Byte>
{
	@Override
	public Class<Byte> getType()
	{
		return byte.class;
	}
	
	@Override
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Byte value)
	{
		write(writer.getTags().valueTag(), elem, value);
	}
	
	@Override
	public Byte read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(reader.getTags().valueTag(), node);
	}
	
	public static void write(String tag, Element elem, byte value)
	{
		final int		b		= value & 0xFF;
		final String	result	= HEX_PREFIX + Integer.toUnsignedString(b, 16);
		elem.setAttribute(tag, result);
	}
	
	public static byte read(String tag, Node node)
	{
		final var attr = XmlReader.getAttribute(node, tag);
		
		final var value = attr.getValue();
		if (value.startsWith(HEX_PREFIX))
			return (byte) Integer.parseUnsignedInt(value.substring(1), 16);
		else
			return (byte) Integer.parseInt(value);
	}
}
