package kryptonbutterfly.xmlConfig4J;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.adapter.EnumAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.RecordAdapter;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;

public final class XmlReader
{
	private final XmlDataBinding			c4j;
	private final HashMap<Integer, String>	typeMapping;
	private final boolean					declaredOnly;
	
	private final HashMap<String, Object> references = new HashMap<>();
	
	XmlReader(XmlDataBinding c4j, HashMap<Integer, String> typeMapping, boolean declaredOnly)
	{
		this.c4j			= c4j;
		this.typeMapping	= typeMapping;
		this.declaredOnly	= declaredOnly;
	}
	
	public Tags getTags()
	{
		return c4j.tags;
	}
	
	private Field getField(Class<?> cls, String fieldName) throws SecurityException, NoSuchFieldException
	{
		return declaredOnly ? cls.getDeclaredField(fieldName) : cls.getField(fieldName);
	}
	
	private Object getFromReference(Node node) throws BrokenReferenceException
	{
		final var refID = (Attr) node.getAttributes().getNamedItem(getTags().refIdTag());
		if (refID == null)
			return null;
		
		var			id		= refID.getValue();
		final var	result	= references.get(id);
		if (result == null)
			throw new BrokenReferenceException(id, node);
		
		return result;
	}
	
	public <T> T registerInstance(Node node, T instance)
	{
		final var instID = (Attr) node.getAttributes().getNamedItem(getTags().instIdTag());
		if (instID != null)
		{
			references.put(instID.getValue(), instance);
		}
		return instance;
	}
	
	public boolean hasType(Node node)
	{
		return null != node.getAttributes().getNamedItem(getTags().typeTag());
	}
	
	public static Attr getAttribute(Node node, String attr)
	{
		return (Attr) node.getAttributes().getNamedItem(attr);
	}
	
	public Class<?> getType(Node node) throws ClassNotFoundException, AttributeNotFoundException
	{
		final var attr = (Attr) node.getAttributes().getNamedItem(getTags().typeTag());
		if (attr == null)
			throw new AttributeNotFoundException(getTags().typeTag(), node);
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
		final var attr = (Attr) node.getAttributes().getNamedItem(getTags().nullTag());
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
		NoSuchFieldException,
		BrokenReferenceException
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
		NoSuchFieldException,
		BrokenReferenceException
	{
		final var instID = getFromReference(node);
		if (instID != null)
			return (T) instID;
		
		final var adapter = c4j.getAdapter(classOfT);
		if (adapter != null)
			return (T) adapter.read(this, node, classOfT);
		
		if (classOfT.isEnum())
			return (T) EnumAdapter.readEnum(this, node, classOfT);
		
		if (classOfT.isRecord())
			return (T) RecordAdapter.readRecord(this, node, classOfT);
		
		return readAnnotated(node, classOfT);
	}
	
	private <T> T readAnnotated(Node node, Class<T> classOfT)
		throws InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		InvocationTargetException,
		NoSuchFieldException,
		ClassNotFoundException,
		AttributeNotFoundException,
		BrokenReferenceException
	{
		final T data = classOfT.getConstructor().newInstance();
		registerInstance(node, data);
		
		final var children = node.getChildNodes();
		for (var child : new Nodes(children))
		{
			final var	field		= getField(classOfT, child.getNodeName());
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
