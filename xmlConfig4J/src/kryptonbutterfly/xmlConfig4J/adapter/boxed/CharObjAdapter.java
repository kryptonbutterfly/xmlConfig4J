package kryptonbutterfly.xmlConfig4J.adapter.boxed;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class CharObjAdapter implements TypeAdapter<Character>
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
			elem.setAttribute(XmlDataBinding.VALUE, value.toString());
	}
	
	@Override
	public Character read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var valueAttr = XmlReader.getAttribute(node, XmlDataBinding.VALUE);
		if (valueAttr == null)
			return null;
		return valueAttr.getValue().charAt(0);
	}
}
