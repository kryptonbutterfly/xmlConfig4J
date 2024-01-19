package kryptonbutterfly.xmlConfig4J.parser;

import static kryptonbutterfly.xmlConfig4J.utils.Utils.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.LoadHelper;
import kryptonbutterfly.xmlConfig4J.SaveHelper;

public class StringParser implements Parser
{
	@Override
	public Class<?> parsedType()
	{
		return String.class;
	}
	
	@Override
	public void save(Element element, Object container, Document document, SaveHelper saveHelper)
	{
		element.setAttribute(VALUE, container.toString());
	}
	
	@Override
	public Object load(Node node, LoadHelper loadHelper)
	{
		if (loadIsNotNull(node, loadHelper))
		{
			final var val = getAttribute(node, VALUE);
			return val.getValue();
		}
		else
		{
			return null;
		}
	}
}