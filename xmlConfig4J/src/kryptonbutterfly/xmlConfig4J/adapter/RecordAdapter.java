package kryptonbutterfly.xmlConfig4J.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Comments.CommentReader;
import kryptonbutterfly.xmlConfig4J.Comments.CommentWriter;
import kryptonbutterfly.xmlConfig4J.DataLocation;
import kryptonbutterfly.xmlConfig4J.DataLocation.InitMethod;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.comments.path.GeneralPath;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;
import kryptonbutterfly.xmlConfig4J.utils.IntRange;
import kryptonbutterfly.xmlConfig4J.utils.Nodes;

public final class RecordAdapter
{
	public static <T> void writeRecord(
		CommentWriter cw,
		XmlWriter writer,
		Element elem,
		T value,
		Class<? extends T> classOfT)
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
			try (var w = cw.push(childElem, GeneralPath.create(writer.getTags(), childElem)))
			{
				elem.appendChild(childElem);
				
				final var location = new DataLocation(classOfT, InitMethod.CONSTRUCTOR, c.getType(), c.getName());
				
				final var annRes = writer.c4j
					.handleAnnotations(c.getDeclaredAnnotations(), getData(c, value), location);
				childElem.setAttribute(writer.getTags().infoTag(), annRes.info());
				
				final var componentType = c.getType();
				if (annRes.value() == null)
					writer.writeNull(childElem);
				else if (componentType.isPrimitive())
					writer.write(w, childElem, annRes.value(), componentType);
				else
				{
					if (requiresType(annRes.value(), c))
						writer.writeType(childElem, annRes.value().getClass());
					writer.write(w, childElem, annRes.value());
				}
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
	public static <T> T readRecord(CommentReader cr, XmlReader reader, Node node, Class<T> classOfT)
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
		
		final var rawComponents = new Object[constructor.getParameters().length];
		for (var child : new Nodes(node.getChildNodes()))
			if (child instanceof org.w3c.dom.Comment comment)
				cr.addComment(comment);
			else
				try (var r = cr.push(GeneralPath.create(reader.getTags(), child)))
				{
					final int index = componentIndex(classOfT, child.getNodeName());
					if (index == -1)
						System.err.printf("Ignoring unexpected node: '%s'\n", child.getNodeName());
					else
					{
						Object value;
						if (reader.isNull(child))
							value = null;
						else
						{
							final Class<?> type;
							if (reader.hasType(child))
								type = reader.getType(child);
							else
								type = constructor.getParameterTypes()[index];
							
							value = reader.read(r, child, type);
						}
						
						final var component = classOfT.getRecordComponents()[index];
						
						final var location = new DataLocation(
							classOfT,
							InitMethod.CONSTRUCTOR,
							component.getType(),
							component.getName());
						
						final var comp = classOfT.getRecordComponents()[index];
						value = reader.c4j.handleAnnotations(comp.getDeclaredAnnotations(), value, location).value();
						
						rawComponents[index] = value;
					}
				}
			
		final var result = (T) constructor.newInstance(rawComponents);
		reader.registerInstance(node, result);
		return result;
	}
	
	private static <T> int componentIndex(Class<T> classOfT, String name)
	{
		final var components = classOfT.getRecordComponents();
		for (int i : new IntRange(components.length))
			if (components[i].getName().equals(name))
				return i;
		return -1;
	}
}
