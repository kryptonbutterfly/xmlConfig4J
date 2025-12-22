package kryptonbutterfly.xmlConfig4J.adapter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class EnumAdapter
{
	public static void writeEnum(XmlWriter writer, Element elem, Enum<?> value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
		{
			final var enumInst = (Enum<?>) value;
			elem.setAttribute(XmlDataBinding.VALUE, Integer.toString(value.ordinal()));
			elem.setAttribute(XmlDataBinding.NAME, enumInst.name());
		}
	}
	
	public static Enum<?> readEnum(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		
		final var	attrOrd		= XmlReader.getAttribute(node, XmlDataBinding.VALUE);
		final int	ordinal		= Integer.parseInt(attrOrd.getValue());
		final var	constants	= classOfT.getEnumConstants();
		if (ordinal >= 0 && ordinal < constants.length)
			return (Enum<?>) constants[ordinal];
		return null;
	}
}
