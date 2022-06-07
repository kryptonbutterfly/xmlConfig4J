package de.tinycodecrank.xmlConfig4J.parser.primitiv;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.Accessor;
import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;
import de.tinycodecrank.xmlConfig4J.parser.Parser;

public final class ByteParser implements Parser
{
	
	@Override
	public Class<?> parsedType()
	{
		return byte.class;
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
		final var val = getAttribute(node, VALUE);
		new Accessor<>(parent, field).perform(Field::setByte, Byte.parseByte(val.getValue()));
	}
	
	@Override
	public Object load(Node node, LoadHelper loadHelper)
	{
		final var val = getAttribute(node, VALUE);
		return Byte.parseByte(val.getValue());
	}
}