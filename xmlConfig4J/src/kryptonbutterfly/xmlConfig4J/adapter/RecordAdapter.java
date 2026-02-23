package kryptonbutterfly.xmlConfig4J.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.annotations.Value;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;

public final class RecordAdapter
{
	public static <T> void writeRecord(XmlWriter writer, Element elem, T value, Class<? extends T> classOfT)
		throws IllegalAccessException
	{
		if (value == null)
		{
			writer.writeNull(elem);
			return;
		}
		
		for (var c : classOfT.getRecordComponents())
		{
			final var	name		= c.getName();
			final var	childElem	= writer.doc.createElement(name);
			elem.appendChild(childElem);
			
			final var annotation = c.getAnnotation(Value.class);
			if (annotation != null)
				childElem.setAttribute(writer.getTags().infoTag(), annotation.value());
			
			final var childData = getData(c, value);
			
			final var componentType = c.getType();
			if (childData == null)
				writer.writeNull(childElem);
			else if (componentType.isPrimitive())
				writer.write(childElem, childData, componentType);
			else
			{
				if (requiresType(childData, c))
					writer.writeType(childElem, childData.getClass());
				writer.write(childElem, childData);
			}
		}
	}
	
	private static boolean requiresType(Object data, RecordComponent c)
	{
		return !c.getType().equals(data.getClass());
	}
	
	private static <T> Object getData(RecordComponent c, T parent)
		throws IllegalAccessException
	{
		try
		{
			return c.getAccessor().invoke(parent);
		}
		catch (InvocationTargetException e)
		{
			// This should never happen!
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T readRecord(XmlReader reader, Node node, Class<T> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		InstantiationException,
		IllegalAccessException,
		InvocationTargetException,
		NoSuchMethodException,
		NoSuchFieldException,
		BrokenReferenceException
	{
		if (reader.isNull(node))
			return null;
		
		final var constructor = classOfT.getConstructors()[0];
		
		final var params = new HashMap<String, Class<?>>();
		for (var p : constructor.getParameters())
			params.put(p.getName(), p.getType());
		
		final var rawComponents = new ArrayList<Object>();
		for (var child : new Nodes(node.getChildNodes()))
		{
			// TODO handle annotation specific stuff here!
			if (reader.isNull(child))
				rawComponents.add(null);
			else
			{
				final Class<?> type;
				if (reader.hasType(child))
					type = reader.getType(child);
				else
					type = params.get(child.getNodeName());
				
				rawComponents.add(reader.read(child, type));
			}
		}
		
		return (T) constructor.newInstance(rawComponents.toArray(Object[]::new));
	}
}
