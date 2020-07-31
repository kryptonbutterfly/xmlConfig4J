package de.tinycodecrank.xmlConfig4J.parser.wrapping;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;
import de.tinycodecrank.xmlConfig4J.parser.Parser;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

public class BooleanObjectParser implements Parser
{
	@Override
	public Class<?> parsedType()
	{
		return Boolean.class;
	}

	@Override
	public void save(Element element, Object container, Document document, SaveHelper saveHelper)
	{
		if(container == null)
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
		if(loadIsNotNull(node, loadHelper))
		{
			String bool = getAttribute(node, VALUE).getValue();
			return Boolean.parseBoolean(bool);
		}
		else
		{
			return null;
		}
	}
}