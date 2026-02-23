package kryptonbutterfly.xmlConfig4J.adapter.boxed;

import static kryptonbutterfly.xmlConfig4J.utils.InternalConstants.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class ByteObjAdapter implements TypeAdapter<Byte>
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
			final String result = HEX_PREFIX + Integer.toUnsignedString(value, 16);
			elem.setAttribute(writer.getTags().valueTag(), result);
		}
	}
	
	@Override
	public Byte read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var valueAttr = XmlReader.getAttribute(node, reader.getTags().valueTag());
		if (valueAttr == null)
			return null;
		final var value = valueAttr.getValue();
		
		final Byte result;
		if (value.startsWith(HEX_PREFIX))
			result = (byte) Integer.parseUnsignedInt(value.substring(1), 16);
		else
			result = (byte) Integer.parseInt(value);
		
		return reader.registerInstance(node, result);
	}
}
