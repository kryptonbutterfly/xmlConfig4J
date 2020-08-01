package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.NULL;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.TRUE;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.VALUE;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.getAttribute;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.loadIsNotNull;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public class EnumParser implements ParserAssignable
{
	private static final String INFO = "info";
	
	@Override
	public boolean canParse(Class<?> type)
	{
		return Enum.class.isAssignableFrom(type);
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
			Enum<?>	_enum	= (Enum<?>) container;
			int		ordinal	= _enum.ordinal();
			element.setAttribute(VALUE, Integer.toString(ordinal));
			element.setAttribute(INFO, _enum.name());
		}
	}

	@Override
	public Object load(Class<?> type, Node node, LoadHelper loadHelper)
	{
		if(loadIsNotNull(node, loadHelper))
		{
			Attr attrOrd = getAttribute(node, VALUE);
			int ordinal = Integer.parseInt(attrOrd.getValue());
			if(ordinal >= 0 && ordinal < type.getEnumConstants().length)
			{
				return type.getEnumConstants()[ordinal];
			}
		}
		return null;
	}
}