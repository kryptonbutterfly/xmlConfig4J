package kryptonbutterfly.xmlConfig4J.parser;

import java.lang.reflect.Field;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.reflectionUtils.Accessor;
import kryptonbutterfly.xmlConfig4J.LoadHelper;
import kryptonbutterfly.xmlConfig4J.SaveHelper;

public interface Parser
{
	Class<?> parsedType();
	
	void save(Element element, Object container, Document document, SaveHelper saveHelper);
	
	default void load(Field field, Object parent, Node node, LoadHelper loadHelper)
		throws IllegalArgumentException,
		IllegalAccessException
	{
		new Accessor<>(parent, field).applyObj(Field::set, load(node, loadHelper));
	}
	
	Object load(Node node, LoadHelper loadHelper);
}