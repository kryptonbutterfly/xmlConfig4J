package kryptonbutterfly.xmlConfig4J;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;

public interface TypeAdapter<T>
{
	Class<T> getType();
	
	void write(XmlWriter writer, Element elem, T value) throws IllegalAccessException;
	
	T read(XmlReader reader, Node node, Class<?> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		NoSuchFieldException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException;
}
