package kryptonbutterfly.xmlConfig4J.adapter.misc;

import java.awt.Color;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class AwtColorAdapter implements TypeAdapter<Color>
{
	public static final String	RED		= "red";
	public static final String	GREEN	= "green";
	public static final String	BLUE	= "blue";
	public static final String	ALPHA	= "alpha";
	
	@Override
	public Class<Color> getType()
	{
		return Color.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Color value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
		{
			elem.setAttribute(RED, "#" + Integer.toString(value.getRed(), 16));
			elem.setAttribute(GREEN, "#" + Integer.toString(value.getGreen(), 16));
			elem.setAttribute(BLUE, "#" + Integer.toString(value.getBlue(), 16));
			elem.setAttribute(ALPHA, "#" + Integer.toString(value.getAlpha(), 16));
		}
	}
	
	@Override
	public Color read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		
		final int	red		= colorValue(node, RED);
		final int	green	= colorValue(node, GREEN);
		final int	blue	= colorValue(node, BLUE);
		final int	alpha	= colorValue(node, ALPHA, 0xFF);
		
		return new Color(red, green, blue, alpha);
	}
	
	private static int colorValue(Node node, String attr, int fallback)
	{
		final var valueAttr = XmlReader.getAttribute(node, attr);
		if (valueAttr == null)
			return fallback;
		return colorValue(valueAttr);
	}
	
	private static int colorValue(Attr valueAttr)
	{
		final var value = valueAttr.getValue();
		if (value.startsWith("#"))
			return Integer.parseInt(value.substring(1), 16) & 0xFF;
		return Integer.parseInt(value) % 0xFF;
	}
	
	private static int colorValue(Node node, String attr)
	{
		final var valueAttr = XmlReader.getAttribute(node, attr);
		return colorValue(valueAttr);
	}
}
