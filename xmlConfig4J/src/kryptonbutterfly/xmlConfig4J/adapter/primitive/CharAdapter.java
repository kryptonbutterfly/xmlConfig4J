package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class CharAdapter implements TypeAdapter<Character>
{
	@Override
	public Class<Character> getType()
	{
		return char.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Character value)
	{
		write(elem, value);
	}
	
	@Override
	public Character read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(node);
	}
	
	public static void write(Element elem, char value)
	{
		final var str = "#x" + Integer.toUnsignedString(value & 0xFFFF, 16);
		elem.setAttribute(XmlDataBinding.VALUE, str);
	}
	
	public static char read(Node node)
	{
		final var	value	= XmlReader.getAttribute(node, XmlDataBinding.VALUE).getValue();
		final var	raw		= value.substring(2);
		return (char) Integer.parseUnsignedInt(raw, 16);
	}
}
