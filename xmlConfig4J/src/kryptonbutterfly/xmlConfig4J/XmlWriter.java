package kryptonbutterfly.xmlConfig4J;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import kryptonbutterfly.xmlConfig4J.adapter.EnumAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.RecordAdapter;
import kryptonbutterfly.xmlConfig4J.annotations.Value;
import kryptonbutterfly.xmlConfig4J.utils.FunctionThrowing;

public final class XmlWriter
{
	private final XmlDataBinding											c4j;
	final HashMap<String, Integer>											types	= new HashMap<>();
	private final FunctionThrowing<Field[], Class<?>, SecurityException>	getFields;
	public final Document													doc;
	
	private int										nextId		= 0;
	private final HashMap<ObjWrapper, Reference>	references	= new HashMap<>();
	
	XmlWriter(XmlDataBinding c4j, Document doc, boolean declaredOnly)
	{
		this.c4j		= c4j;
		this.doc		= doc;
		this.getFields	= declaredOnly ? Class::getDeclaredFields : Class::getFields;
	}
	
	public Tags getTags()
	{
		return c4j.tags;
	}
	
	private String nextId()
	{
		return "" + nextId++;
	}
	
	public <T> boolean isSerialized(T data, Element elem)
	{
		final var	wrapper	= new ObjWrapper(data);
		final var	ref		= references.get(wrapper);
		
		if (ref != null)
		{
			ref.reference(elem);
			return true;
		}
		
		references.put(wrapper, new Reference(elem));
		return false;
	}
	
	public <T> boolean requiresType(T data, Field field)
	{
		return !field.getType().equals(data.getClass());
	}
	
	public void writeNull(Element elem)
	{
		elem.setAttribute(getTags().nullTag(), XmlDataBinding.TRUE);
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
		{
			EnumAdapter.writeEnum(this, elem, (Enum<?>) data);
			return;
		}
		
		final var adapter = (TypeAdapter<T>) c4j.getAdapter(valueType);
		if (adapter != null
				&& (adapter.isValueType()
						|| !isSerialized(data, elem)))
			adapter.write(this, elem, data);
		else if (!isSerialized(data, elem))
		{
			if (valueType.isRecord())
				RecordAdapter.writeRecord(this, elem, data, valueType);
			else
				writeAnnotated(elem, data);
		}
		
	}
	
	private <T> void writeAnnotated(Element elem, T data) throws IllegalAccessException
	{
		final var type = data.getClass();
		for (final var field : getFields.apply(type))
		{
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			
			final var annotation = c4j.includeFieldAnnotation(field);
			if (annotation == null)
				continue;
			
			final var childElem = doc.createElement(field.getName());
			elem.appendChild(childElem);
			
			if (annotation instanceof Value valAnnotation
					&& !valAnnotation.value().isBlank())
				childElem.setAttribute(getTags().infoTag(), valAnnotation.value());
			
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
		
		elem.setAttribute(getTags().typeTag(), typeName);
	}
	
	private static final record ObjWrapper(Object o)
	{
		@Override
		public int hashCode()
		{
			return 0;
		}
		
		@Override
		public boolean equals(Object o)
		{
			if (!(o instanceof ObjWrapper obj))
				return false;
			return this.o == obj.o;
		}
	}
	
	private final class Reference
	{
		private String id = null;
		
		public final Element elem;
		
		public Reference(Element elem)
		{
			this.elem = elem;
		}
		
		public void reference(Element elem)
		{
			if (id == null)
			{
				id = nextId();
				this.elem.setAttribute(getTags().instIdTag(), id);
			}
			elem.setAttribute(getTags().refIdTag(), id);
		}
	}
}
