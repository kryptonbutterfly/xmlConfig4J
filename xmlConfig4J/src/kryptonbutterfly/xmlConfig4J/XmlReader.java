package kryptonbutterfly.xmlConfig4J;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.adapter.EnumAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.RecordAdapter;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;

public final class XmlReader
{
	private final XmlDataBinding			c4j;
	private final HashMap<Integer, String>	typeMapping;
	
	XmlReader(XmlDataBinding c4j, HashMap<Integer, String> typeMapping)
	{
		this.c4j			= c4j;
		this.typeMapping	= typeMapping;
	}
	
	public boolean hasType(Node node)
	{
		return null != node.getAttributes().getNamedItem(XmlDataBinding.TYPE);
	}
	
	public static Attr getAttribute(Node node, String attr)
	{
		return (Attr) node.getAttributes().getNamedItem(attr);
	}
	
	public Class<?> getType(Node node) throws ClassNotFoundException, AttributeNotFoundException
	{
		final var attr = (Attr) node.getAttributes().getNamedItem(XmlDataBinding.TYPE);
		if (attr == null)
			throw new AttributeNotFoundException(XmlDataBinding.TYPE, node);
		final var	typeValue	= attr.getValue();
		String		typeName	= typeValue;
		try
		{
			typeName = typeMapping.get(Integer.valueOf(typeValue));
		}
		catch (NumberFormatException ignored)
		{}
		
		if (c4j.classNameHistory.containsKey(typeName))
			return c4j.classNameHistory.get(typeName);
		return Class.forName(typeName);
	}
	
	public boolean isNull(Node node)
	{
		final var attr = (Attr) node.getAttributes().getNamedItem(XmlDataBinding.NULL);
		if (attr == null)
			return false;
		
		final var value = attr.getValue();
		if (value == null)
			return true;
		
		return value.equalsIgnoreCase(XmlDataBinding.TRUE);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T read(Node node)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		InstantiationException,
		IllegalAccessException,
		InvocationTargetException,
		NoSuchMethodException,
		NoSuchFieldException
	{
		if (isNull(node))
			return null;
		return (T) read(node, getType(node));
	}
	
	@SuppressWarnings("unchecked")
	public <T> T read(Node node, Class<T> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		InstantiationException,
		IllegalAccessException,
		InvocationTargetException,
		NoSuchMethodException,
		NoSuchFieldException
	{
		if (classOfT.isEnum())
			return (T) EnumAdapter.readEnum(this, node, classOfT);
		
		final var adapter = c4j.getAdapter(classOfT);
		if (adapter != null)
			return (T) adapter.read(this, node, classOfT);
		
		if (classOfT.isRecord())
			return (T) RecordAdapter.readRecord(this, node, classOfT);
		
		return readGeneric(node, classOfT);
	}
	
	private <T> T readGeneric(Node node, Class<T> classOfT)
		throws InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		InvocationTargetException,
		NoSuchFieldException,
		ClassNotFoundException,
		AttributeNotFoundException
	{
		final T		data		= classOfT.getConstructor().newInstance();
		final var	children	= node.getChildNodes();
		for (var child : new Nodes(children))
		{
			final var	field		= classOfT.getDeclaredField(child.getNodeName());
			final var	annotation	= c4j.includeFieldAnnotation(field);
			if (annotation != null)
			{
				// TODO handle annotation specific stuff here!
				
				if (isNull(child))
				{
					field.set(data, null);
				}
				else
				{
					final var type = hasType(child) ? getType(child) : field.getType();
					
					final var childData = read(child, type);
					field.set(data, childData);
				}
			}
		}
		return data;
	}
}
