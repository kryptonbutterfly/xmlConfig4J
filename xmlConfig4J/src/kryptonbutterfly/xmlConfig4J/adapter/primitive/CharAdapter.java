package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import static kryptonbutterfly.xmlConfig4J.utils.InternalConstants.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class CharAdapter implements TypeAdapter<Character>
{
	
	@Override
	public Class<Character> getType()
	{
		return char.class;
	}
	
	@Override
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Character value)
	{
		write(writer.getTags().valueTag(), elem, value);
	}
	
	@Override
	public Character read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(reader.getTags().valueTag(), node);
	}
	
	public static void write(String tag, Element elem, char value)
	{
		final var str = HEX_PREFIX + Integer.toUnsignedString(value & 0xFFFF, 16);
		elem.setAttribute(tag, str);
	}
	
	public static char read(String tag, Node node)
	{
		final var	value	= XmlReader.getAttribute(node, tag).getValue();
		final var	raw		= value.substring(HEX_PREFIX.length());
		return (char) Integer.parseUnsignedInt(raw, 16);
	}
}
