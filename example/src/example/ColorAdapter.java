package example;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;

public final class ColorAdapter implements TypeAdapter<Color>
{
	private static final String RED = "red", GREEN = "green", BLUE = "blue", ALPHA = "alpha";
	
	@Override
	public Class<Color> getType()
	{
		return Color.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Color value) throws IllegalAccessException
	{
		if (elem == null)
			writer.writeNull(elem);
		else
		{
			elem.setAttribute(RED, "#" + Integer.toUnsignedString(value.red, 16));
			elem.setAttribute(GREEN, "#" + Integer.toUnsignedString(value.green, 16));
			elem.setAttribute(BLUE, "#" + Integer.toUnsignedString(value.blue, 16));
			elem.setAttribute(ALPHA, "#" + Integer.toUnsignedString(value.alpha, 16));
		}
	}
	
	@Override
	public Color read(XmlReader reader, Node node, Class<?> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		NoSuchFieldException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException
	{
		if (reader.isNull(node))
			return null;
		final int	red		= intFromAttr(node, RED, 0x00);
		final int	green	= intFromAttr(node, GREEN, 0x00);
		final int	blue	= intFromAttr(node, BLUE, 0x00);
		final int	alpha	= intFromAttr(node, ALPHA, 0xFF);
		return new Color(red, green, blue, alpha);
	}
	
	private int intFromAttr(Node node, String name, int fallback)
	{
		final var attr = XmlReader.getAttribute(node, name);
		if (attr == null)
			return fallback;
		final var value = attr.getValue();
		if (value.startsWith("#"))
			return Integer.parseUnsignedInt(value.substring(1), 16);
		return Integer.parseUnsignedInt(value);
	}
}
