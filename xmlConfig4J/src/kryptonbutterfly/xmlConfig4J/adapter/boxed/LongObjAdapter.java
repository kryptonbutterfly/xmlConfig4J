package kryptonbutterfly.xmlConfig4J.adapter.boxed;

import static kryptonbutterfly.xmlConfig4J.utils.InternalConstants.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class LongObjAdapter implements TypeAdapter<Long>
{
	@Override
	public Class<Long> getType()
	{
		return Long.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Long value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			elem.setAttribute(writer.getTags().valueTag(), value.toString());
	}
	
	@Override
	public Long read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var valueAttr = XmlReader.getAttribute(node, reader.getTags().valueTag());
		if (valueAttr == null)
			return null;
		final var value = valueAttr.getValue();
		
		final Long result;
		if (value.startsWith(HEX_PREFIX))
			result = Long.parseUnsignedLong(value.substring(1), 16);
		else
			result = Long.parseLong(value);
		return reader.registerInstance(node, result);
	}
}
