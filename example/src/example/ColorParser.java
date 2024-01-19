package example;

import static kryptonbutterfly.xmlConfig4J.utils.Utils.*;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.LoadHelper;
import kryptonbutterfly.xmlConfig4J.SaveHelper;
import kryptonbutterfly.xmlConfig4J.parser.Parser;

public class ColorParser implements Parser
{
	private static final String	RED		= "red";
	private static final String	GREEN	= "green";
	private static final String	BLUE	= "blue";
	private static final String	ALPHA	= "alpha";
	
	@Override
	public Class<?> parsedType()
	{
		return Color.class;
	}
	
	@Override
	public void save(Element element, Object container, Document document, SaveHelper saveHelper)
	{
		if (container == null)
		{
			element.setAttribute(NULL, TRUE);
		}
		else
		{
			Color color = (Color) container;
			element.setAttribute(RED, Integer.toString(color.getRed()));
			element.setAttribute(GREEN, Integer.toString(color.getGreen()));
			element.setAttribute(BLUE, Integer.toString(color.getBlue()));
			element.setAttribute(ALPHA, Integer.toString(color.getAlpha()));
		}
	}
	
	@Override
	public Object load(Node node, LoadHelper loadHelper)
	{
		if (loadIsNotNull(node, loadHelper))
		{
			Attr	attrRed		= ((Attr) node.getAttributes().getNamedItem(RED));
			Attr	attrGreen	= ((Attr) node.getAttributes().getNamedItem(GREEN));
			Attr	attrBlue	= ((Attr) node.getAttributes().getNamedItem(BLUE));
			Attr	attrAlpha	= ((Attr) node.getAttributes().getNamedItem(ALPHA));
			
			int	red		= Integer.parseInt(attrRed.getValue());
			int	green	= Integer.parseInt(attrGreen.getValue());
			int	blue	= Integer.parseInt(attrBlue.getValue());
			
			if (attrAlpha != null)
			{
				int alpha = Integer.parseInt(attrAlpha.getValue());
				return new Color(red, green, blue, alpha);
			}
			else
			{
				return new Color(red, green, blue);
			}
		}
		else
		{
			return null;
		}
	}
}