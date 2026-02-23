package kryptonbutterfly.xmlConfig4J.adapter.boxed;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class CharObjAdapter implements TypeAdapter<Character>
{
	@Override
	public Class<Character> getType()
	{
		return Character.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Character value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			elem.setAttribute(writer.getTags().valueTag(), value.toString());
	}
	
	@Override
	public Character read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var valueAttr = XmlReader.getAttribute(node, reader.getTags().valueTag());
		if (valueAttr == null)
			return null;
		return reader.registerInstance(node, Character.valueOf(valueAttr.getValue().charAt(0)));
	}
}
