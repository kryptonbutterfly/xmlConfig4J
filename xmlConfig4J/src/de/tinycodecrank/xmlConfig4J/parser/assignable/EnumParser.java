package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public final class EnumParser implements ParserAssignable
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
			final var enumInstance = (Enum<?>) container;
			element.setAttribute(VALUE, Integer.toString(enumInstance.ordinal()));
			element.setAttribute(INFO, enumInstance.name());
		}
	}
	
	@Override
	public Object load(Class<?> type, Node node, LoadHelper loadHelper)
	{
		if (loadIsNotNull(node, loadHelper))
		{
			final var	attrOrd	= getAttribute(node, VALUE);
			int			ordinal	= Integer.parseInt(attrOrd.getValue());
			if (ordinal >= 0 && ordinal < type.getEnumConstants().length)
			{
				return type.getEnumConstants()[ordinal];
			}
		}
		return null;
	}
}