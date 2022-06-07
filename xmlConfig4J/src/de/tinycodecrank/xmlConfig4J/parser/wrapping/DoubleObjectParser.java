package de.tinycodecrank.xmlConfig4J.parser.wrapping;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;
import de.tinycodecrank.xmlConfig4J.parser.Parser;

public final class DoubleObjectParser implements Parser
{
	@Override
	public Class<?> parsedType()
	{
		return Double.class;
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
			element.setAttribute(VALUE, container.toString());
		}
	}
	
	@Override
	public Object load(Node node, LoadHelper loadHelper)
	{
		if (loadIsNotNull(node, loadHelper))
		{
			final var val = getAttribute(node, VALUE);
			return Double.parseDouble(val.getValue());
		}
		else
		{
			return null;
		}
	}
}