package kryptonbutterfly.xmlConfig4J;

import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Comments.CommentReader;
import kryptonbutterfly.xmlConfig4J.Comments.CommentWriter;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;

public interface TypeAdapter<T>
{
	Class<T> getType();
	
	default boolean isValueType()
	{
		return false;
	}
	
	void write(CommentWriter cw, XmlWriter writer, Element elem, T value) throws IllegalAccessException;
	
	T read(CommentReader cr, XmlReader reader, Node node, Class<?> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		NoSuchFieldException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		BrokenReferenceException;
	
	public default boolean isItemNode(XmlReader reader, Node node)
	{
		return node.getNodeName().equals(reader.getTags().itemTag());
	}
}
