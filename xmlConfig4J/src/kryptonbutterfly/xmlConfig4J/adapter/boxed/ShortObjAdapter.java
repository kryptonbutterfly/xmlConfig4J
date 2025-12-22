package kryptonbutterfly.xmlConfig4J.adapter.boxed;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class ShortObjAdapter implements TypeAdapter<Short>
{
	@Override
	public Class<Short> getType()
	{
		return Short.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Short value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			elem.setAttribute(XmlDataBinding.VALUE, value.toString());
	}
	
	@Override
	public Short read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var valueAttr = XmlReader.getAttribute(node, XmlDataBinding.VALUE);
		if (valueAttr == null)
			return null;
		final var value = valueAttr.getValue();
		if (value.startsWith("#"))
			return (short) Integer.parseUnsignedInt(value.substring(1), 16);
		else
			return (short) Integer.parseInt(value);
	}
}
