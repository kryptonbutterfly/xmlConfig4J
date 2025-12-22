package kryptonbutterfly.xmlConfig4J.adapter.boxed;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class ByteObjAdapter implements TypeAdapter<Byte>
{
	@Override
	public Class<Byte> getType()
	{
		return Byte.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Byte value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
		{
			final String result = "#" + Integer.toUnsignedString(value, 16);
			elem.setAttribute(XmlDataBinding.VALUE, result);
		}
	}
	
	@Override
	public Byte read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var valueAttr = XmlReader.getAttribute(node, XmlDataBinding.VALUE);
		if (valueAttr == null)
			return null;
		final var value = valueAttr.getValue();
		if (value.startsWith("#"))
			return (byte) Integer.parseUnsignedInt(value.substring(1), 16);
		else
			return (byte) Integer.parseInt(value);
	}
}
