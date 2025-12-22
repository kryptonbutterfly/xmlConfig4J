package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class ByteAdapter implements TypeAdapter<Byte>
{
	@Override
	public Class<Byte> getType()
	{
		return byte.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Byte value)
	{
		write(elem, value);
	}
	
	@Override
	public Byte read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(node);
	}
	
	public static void write(Element elem, byte value)
	{
		final int		b		= value & 0xFF;
		final String	result	= "#" + Integer.toUnsignedString(b, 16);
		elem.setAttribute(XmlDataBinding.VALUE, result);
	}
	
	public static byte read(Node node)
	{
		final var attr = XmlReader.getAttribute(node, XmlDataBinding.VALUE);
		
		final var value = attr.getValue();
		if (value.startsWith("#"))
			return (byte) Integer.parseUnsignedInt(value.substring(1), 16);
		else
			return (byte) Integer.parseInt(value);
	}
}
