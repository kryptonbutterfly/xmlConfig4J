package kryptonbutterfly.xmlConfig4J.parser.assignable;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.LoadHelper;
import kryptonbutterfly.xmlConfig4J.SaveHelper;

public interface ParserAssignable
{
	public static final String	TYPE	= "type";
	public static final String	ITEM	= "item";
	
	boolean canParse(Class<?> type);
	
	void save(Element element, Object container, Document document, SaveHelper saveHelper);
	
	Object load(Class<?> type, Node node, LoadHelper loadHelper)
		throws ClassNotFoundException,
		InstantiationException,
		IllegalAccessException,
		IllegalArgumentException,
		InvocationTargetException,
		NoSuchMethodException,
		SecurityException;
}