package kryptonbutterfly.xmlConfig4J;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import kryptonbutterfly.xmlConfig4J.Comments.CommentWriter;
import kryptonbutterfly.xmlConfig4J.DataLocation.InitMethod;
import kryptonbutterfly.xmlConfig4J.adapter.EnumAdapter;
import kryptonbutterfly.xmlConfig4J.adapter.RecordAdapter;
import kryptonbutterfly.xmlConfig4J.annotations.InfoProperty;
import kryptonbutterfly.xmlConfig4J.comments.path.GeneralPath;
import kryptonbutterfly.xmlConfig4J.utils.FunctionThrowing;

public final class XmlWriter
{
	public final XmlDataBinding												c4j;
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
	
	public <T> void write(CommentWriter cw, Element elem, T data) throws IllegalAccessException
	{
		if (data == null)
			writeNull(elem);
		else
			write(cw, elem, data, data.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public <T> void write(CommentWriter cw, Element elem, T data, Class<? extends T> valueType)
		throws IllegalAccessException
	{
		Objects.requireNonNull(data);
		
		final var adapter = (TypeAdapter<T>) c4j.getAdapter(valueType);
		if (adapter != null
				&& (adapter.isValueType()
						|| !isSerialized(data, elem)))
			adapter.write(cw, this, elem, data);
		
		else if (valueType.isEnum())
			EnumAdapter.writeEnum(this, elem, (Enum<?>) data);
		else if (!isSerialized(data, elem))
		{
			if (valueType.isRecord())
				RecordAdapter.writeRecord(cw, this, elem, data, valueType);
			else
				writeAnnotated(cw, elem, data);
		}
	}
	
	private <T> void writeAnnotated(CommentWriter cw, Element elem, T data) throws IllegalAccessException
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
			try (var w = cw.push(childElem, GeneralPath.create(getTags(), childElem)))
			{
				elem.appendChild(childElem);
				
				final var childData = field.get(data);
				
				final var location = new DataLocation(
					data.getClass(),
					InitMethod.IMPLICIT,
					field.getType(),
					field.getName());
				
				final var annRes = c4j.handleAnnotations(field.getDeclaredAnnotations(), childData, location);
				if (annRes.info() != null)
					childElem.setAttribute(getTags().infoTag(), annRes.info());
				
				final var fieldType = field.getType();
				if (annRes.value() == null)
					writeNull(childElem);
				else if (fieldType.isPrimitive())
					write(w, childElem, annRes.value(), fieldType);
				else
				{
					if (requiresType(annRes.value(), field))
						writeType(childElem, annRes.value().getClass());
					write(w, childElem, annRes.value());
				}
			}
		}
	}
	
	public String getInfo(Annotation annotation) throws IllegalAccessException, SecurityException
	{
		if (annotation == null)
			return null;
		
		final var infoProperty = Arrays.stream(annotation.annotationType().getDeclaredMethods())
			.filter(m -> m.isAnnotationPresent(InfoProperty.class))
			.filter(m -> CharSequence.class.isAssignableFrom(m.getReturnType()))
			.findFirst()
			.orElse(null);
		
		try
		{
			if (infoProperty.invoke(annotation) instanceof String value && !value.isBlank())
				return value;
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{}
		
		return null;
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
