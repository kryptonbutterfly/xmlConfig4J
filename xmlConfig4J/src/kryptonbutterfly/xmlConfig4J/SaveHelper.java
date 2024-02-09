package kryptonbutterfly.xmlConfig4J;

import static kryptonbutterfly.xmlConfig4J.utils.Utils.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import kryptonbutterfly.reflectionUtils.Accessor;
import kryptonbutterfly.xmlConfig4J.parser.Parser;
import kryptonbutterfly.xmlConfig4J.parser.assignable.ParserAssignable;

public final class SaveHelper
{
	private static final String	INFO	= "info";
	private static final String	VALUE	= "value";
	
	/**
	 * Used to shorten type information in the config tree
	 */
	private final HashMap<String, Integer> reverseTypeMappings;
	
	private final HashMap<String, Parser> parser;
	
	private final Function<Class<?>, ParserAssignable> getParser;
	
	private final Function<Field, ? extends Annotation> includeFieldAnnotation;
	
	SaveHelper(
		HashMap<String, Parser> parser,
		boolean doMap,
		Function<Class<?>, ParserAssignable> getParser,
		Function<Field, ? extends Annotation> includeFieldAnnotation)
	{
		this.parser					= parser;
		this.getParser				= getParser;
		this.includeFieldAnnotation	= includeFieldAnnotation;
		if (doMap)
		{
			this.reverseTypeMappings = new HashMap<>();
		}
		else
		{
			this.reverseTypeMappings = null;
		}
	}
	
	/**
	 * @param type
	 * @return the id associated with type or type if there is no association
	 */
	public final String getMapping(String type)
	{
		if (reverseTypeMappings != null)
		{
			var id = reverseTypeMappings.get(type);
			if (id == null)
			{
				id = reverseTypeMappings.size();
				reverseTypeMappings.put(type, id);
			}
			return id.toString();
		}
		else
		{
			return type;
		}
	}
	
	final Element saveTypeMappings(Document document)
	{
		final var mapping = document.createElement("types");
		reverseTypeMappings.forEach((typeName, id) -> {
			final var type = document.createElement("item");
			type.setAttribute("id", Integer.toString(id));
			type.setAttribute("name", typeName);
			mapping.appendChild(type);
		});
		return mapping;
	}
	
	public Element saveObject(String varName, Object toSave, Document document)
		throws IllegalArgumentException,
		IllegalAccessException
	{
		final var element = document.createElement(varName);
		if (toSave == null)
		{
			element.setAttribute(NULL, TRUE);
			return element;
		}
		
		var			tmpClass	= toSave.getClass();
		final var	parser		= this.parser.get(toSave.getClass().getName());
		if (parser != null)
		{
			parser.save(element, toSave, document, this);
			return element;
		}
		
		final var parserA = getParser.apply(tmpClass);
		if (parserA != null)
		{
			parserA.save(element, toSave, document, this);
			return element;
		}
		
		while (tmpClass != null && tmpClass != Object.class)
		{
			for (final var field : tmpClass.getDeclaredFields())
			{
				final var annotation = includeFieldAnnotation.apply(field);
				if (annotation != null)
				{
					final var	fieldAccess	= new Accessor<>(toSave, field);
					final var	arg			= saveObject(field.getName(), fieldAccess.perform(Field::get), document);
					element.appendChild(arg);
					
					try
					{
						final var info = (String) annotation.annotationType()
							.getDeclaredMethod(VALUE)
							.invoke(annotation);
						if (info != null && !info.isBlank())
							arg.setAttribute(INFO, info);
					}
					catch (NoSuchMethodException e)
					{
						// Ignore -> Do nothing if this method is not present.
					}
					catch (InvocationTargetException e)
					{
						e.printStackTrace();
					}
				}
			}
			tmpClass = tmpClass.getSuperclass();
		}
		return element;
	}
}