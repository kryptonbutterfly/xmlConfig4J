package kryptonbutterfly.xmlConfig4J.parser.primitiv;

import static kryptonbutterfly.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.reflectionUtils.Accessor;
import kryptonbutterfly.xmlConfig4J.LoadHelper;
import kryptonbutterfly.xmlConfig4J.SaveHelper;
import kryptonbutterfly.xmlConfig4J.parser.Parser;

public final class IntParser implements Parser
{
	
	@Override
	public Class<?> parsedType()
	{
		return int.class;
	}
	
	@Override
	public void save(Element element, Object container, Document document, SaveHelper saveHelper)
	{
		element.setAttribute(VALUE, container.toString());
	}
	
	@Override
	public void load(Field field, Object parent, Node node, LoadHelper loadHelper)
		throws IllegalArgumentException,
		IllegalAccessException
	{
		final var	val		= getAttribute(node, VALUE);
		final int	value	= Integer.parseInt(val.getValue());
		new Accessor<>(parent, field).applyInt(Field::setInt, value);
	}
	
	@Override
	public Object load(Node node, LoadHelper loadHelper)
	{
		final var val = getAttribute(node, VALUE);
		return Integer.parseInt(val.getValue());
	}
}