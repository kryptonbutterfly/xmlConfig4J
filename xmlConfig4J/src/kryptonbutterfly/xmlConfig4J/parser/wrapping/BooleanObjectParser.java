package kryptonbutterfly.xmlConfig4J.parser.wrapping;

import static kryptonbutterfly.xmlConfig4J.utils.Utils.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.LoadHelper;
import kryptonbutterfly.xmlConfig4J.SaveHelper;
import kryptonbutterfly.xmlConfig4J.parser.Parser;

public final class BooleanObjectParser implements Parser
{
	@Override
	public Class<?> parsedType()
	{
		return Boolean.class;
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
			final var bool = getAttribute(node, VALUE).getValue();
			return Boolean.parseBoolean(bool);
		}
		else
		{
			return null;
		}
	}
}