package de.tinycodecrank.xmlConfig4J;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tinycodecrank.xmlConfig4J.annotations.Value;
import de.tinycodecrank.xmlConfig4J.parser.Parser;
import de.tinycodecrank.xmlConfig4J.parser.assignable.ParserAssignable;

public final class SaveHelper
{
	/**
	 * Used to shorten type information in the config tree
	 */
	private final HashMap<String, Integer> reverseTypeMappings;
	
	private final HashMap<String, Parser> parser;
	
	private final Function<Class<?>, ParserAssignable> getParser;
	
	SaveHelper(HashMap<String, Parser> parser, boolean doMap, Function<Class<?>, ParserAssignable> getParser)
	{
		this.parser		= parser;
		this.getParser	= getParser;
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
		reverseTypeMappings.forEach((typeName, id) ->
		{
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
		}
		else
		{
			var			tmpClass	= toSave.getClass();
			final var	parser		= this.parser.get(toSave.getClass().getName());
			if (parser != null)
			{
				parser.save(element, toSave, document, this);
			}
			else
			{
				final var parserA = getParser.apply(tmpClass);
				if (parserA != null)
				{
					parserA.save(element, toSave, document, this);
				}
				else
				{
					do
					{
						for (final var tmp : tmpClass.getDeclaredFields())
						{
							if (tmp.isAnnotationPresent(Value.class))
							{
								final var	value		= tmp.getAnnotation(Value.class);
								final var	fieldAccess	= new Accessor<>(toSave, tmp);
								final var	field		= saveObject(
									tmp.getName(),
									fieldAccess.perform(Field::get),
									document);
								if (value != null && value.value() != null && !value.value().isEmpty())
								{
									field.setAttribute("info", value.value());
								}
								element.appendChild(field);
							}
						}
					}
					while ((tmpClass = tmpClass.getSuperclass()) != null && tmpClass != Object.class);
				}
			}
		}
		return element;
	}
}