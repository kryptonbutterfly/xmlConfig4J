package kryptonbutterfly.xmlConfig4J;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import kryptonbutterfly.xmlConfig4J.adapter.EnumAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.RecordAdapter;
import kryptonbutterfly.xmlConfig4J.annotations.Value;

public final class XmlWriter
{
	private final XmlDataBinding	c4j;
	final HashMap<String, Integer>	types	= new HashMap<>();
	public final Document			doc;
	
	XmlWriter(XmlDataBinding c4j, Document doc)
	{
		this.c4j	= c4j;
		this.doc	= doc;
	}
	
	public <T> boolean requiresType(T data, Field field)
	{
		return !field.getType().equals(data.getClass());
	}
	
	public void writeNull(Element elem)
	{
		elem.setAttribute(XmlDataBinding.NULL, XmlDataBinding.TRUE);
	}
	
	public <T> void write(Element elem, T data) throws IllegalAccessException
	{
		if (data == null)
			writeNull(elem);
		else
			write(elem, data, data.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public <T> void write(Element elem, T data, Class<? extends T> valueType) throws IllegalAccessException
	{
		Objects.requireNonNull(data);
		if (valueType.isEnum())
			EnumAdapter.writeEnum(this, elem, (Enum<?>) data);
		else
		{
			final var adapter = (TypeAdapter<T>) c4j.getAdapter(valueType);
			if (adapter != null)
				adapter.write(this, elem, data);
			else if (valueType.isRecord())
				RecordAdapter.writeRecord(this, elem, data, valueType);
			else
				writeGeneric(elem, data);
		}
	}
	
	private <T> void writeGeneric(Element elem, T data) throws IllegalAccessException
	{
		final var type = data.getClass();
		for (final var field : type.getDeclaredFields())
		{
			final var annotation = c4j.includeFieldAnnotation(field);
			if (annotation == null)
				continue;
			
			final var childElem = doc.createElement(field.getName());
			elem.appendChild(childElem);
			
			if (annotation instanceof Value valAnnotation
					&& !valAnnotation.value().isBlank())
				childElem.setAttribute(XmlDataBinding.INFO, valAnnotation.value());
			
			final var childData = field.get(data);
			
			final var fieldType = field.getType();
			if (childData == null)
				writeNull(childElem);
			else if (fieldType.isPrimitive())
				write(childElem, childData, fieldType);
			else
			{
				if (requiresType(childData, field))
					writeType(childElem, childData.getClass());
				write(childElem, childData);
			}
		}
	}
	
	public void writeType(Element elem, Class<?> type)
	{
		String typeName = type.getName();
		if (c4j.mapTypes)
			typeName = types.computeIfAbsent(typeName, k -> types.size()).toString();
		
		elem.setAttribute(XmlDataBinding.TYPE, typeName);
	}
}
