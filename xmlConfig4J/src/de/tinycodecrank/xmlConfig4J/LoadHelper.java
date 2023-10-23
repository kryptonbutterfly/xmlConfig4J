package de.tinycodecrank.xmlConfig4J;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.function.Function;

import org.w3c.dom.Node;

import de.tinycodecrank.reflectionUtils.Accessor;
import de.tinycodecrank.xmlConfig4J.annotations.Value;
import de.tinycodecrank.xmlConfig4J.parser.Parser;
import de.tinycodecrank.xmlConfig4J.parser.assignable.ParserAssignable;
import de.tinycodecrank.xmlConfig4J.utils.Utils;

public class LoadHelper
{
	private static final String TYPE = "type";
	
	/**
	 * Register old class-paths in here and associate them with the current one to
	 * enable loading config-files saved in previous versions of a program!
	 */
	private final HashMap<String, Class<?>> classNameHistory;
	
	/**
	 * Used to shorten type information in the config tree
	 */
	private final String[] typeMappings;
	
	private final HashMap<String, Parser> parser;
	
	private final Function<Class<?>, ParserAssignable> getParser;
	
	LoadHelper(
		HashMap<String, Parser> parser,
		String[] typeMappings,
		HashMap<String, Class<?>> history,
		Function<Class<?>, ParserAssignable> getParser)
	{
		this.parser				= parser;
		this.typeMappings		= typeMappings;
		this.classNameHistory	= history;
		this.getParser			= getParser;
	}
	
	public final Class<?> getClass(String type) throws ClassNotFoundException
	{
		try
		{
			final var clazz = classNameHistory.get(type);
			if (clazz != null)
			{
				return clazz;
			}
			else
			{
				return Class.forName(type);
			}
		}
		catch (ClassNotFoundException e)
		{
			System.err.println(type);
			throw e;
		}
	}
	
	public final String getType(String type)
	{
		if (typeMappings != null && Utils.isValidInt(type))
		{
			final var tmp = typeMappings[Integer.parseInt(type)];
			if (tmp != null)
			{
				return tmp;
			}
		}
		return type;
	}
	
	void loadVar(Field field, Object config, Node node)
	{
		try
		{
			final var fieldAccess = new Accessor<>(config, field);
			if (loadIsNotNull(node, this))
			{
				final var	type		= getAttribute(node, TYPE);
				Class<?>	objectType	= null;
				if (type != null)
				{
					try
					{
						objectType = getClass(getType(type.getValue()));
					}
					catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}
				}
				if (objectType == null)
				{
					objectType = field.getType();
				}
				fieldAccess.applyObj(Field::set, loadFromNode(objectType, node));
			}
			else
			{
				fieldAccess.applyObj(Field::set, null);
			}
		}
		catch (
			SecurityException
			| IllegalArgumentException
			| IllegalAccessException
			| ClassNotFoundException
			| InstantiationException
			| InvocationTargetException
			| NoSuchMethodException e)
		{
			e.printStackTrace();
		}
	}
	
	public Object loadFromNode(Class<?> type, Node node)
		throws SecurityException,
		ClassNotFoundException,
		InstantiationException,
		IllegalAccessException,
		IllegalArgumentException,
		InvocationTargetException,
		NoSuchMethodException
	{
		final var parser = this.parser.get(type.getName());
		if (parser != null)
		{
			return parser.load(node, this);
		}
		else
		{
			final var parserA = getParser.apply(type);
			if (parserA != null)
			{
				return parserA.load(type, node, this);
			}
			else
			{
				return loadAsAnnotated(type, node);
			}
		}
	}
	
	private Object loadAsAnnotated(Class<?> oExtClass, Node parent)
		throws InstantiationException,
		IllegalAccessException,
		IllegalArgumentException,
		InvocationTargetException,
		NoSuchMethodException,
		SecurityException
	{
		final var parentObject = oExtClass.getDeclaredConstructor().newInstance();
		oExtClass = parentObject.getClass();
		while (oExtClass != null && oExtClass != Object.class)
		{
			for (final var varField : oExtClass.getDeclaredFields())
			{
				if (varField.isAnnotationPresent(Value.class))
				{
					final var nodeList = parent.getChildNodes();
					for (int i = 0; i < nodeList.getLength(); i++)
					{
						final var	node	= nodeList.item(i);
						final var	name	= node.getNodeName();
						if (varField.getName().equals(name))
						{
							final var type = getAttribute(node, TYPE);
							try
							{
								final Class<?> objectType;
								if (type != null)
								{
									objectType = getClass(getType(type.getValue()));
								}
								else
								{
									objectType = varField.getType();
								}
								final var parser = this.parser.get(objectType.getName());
								if (parser != null)
								{
									parser.load(varField, parentObject, node, this);
								}
								else
								{
									final var	fieldAccess	= new Accessor<>(parentObject, varField);
									final var	parserA		= getParser.apply(objectType);
									if (parserA != null)
									{
										fieldAccess.applyObj(Field::set, parserA.load(objectType, node, this));
									}
									else
									{
										if (loadIsNotNull(node, this))
										{
											fieldAccess.applyObj(Field::set, loadAsAnnotated(objectType, node));
										}
										else
										{
											fieldAccess.applyObj(Field::set, null);
										}
									}
								}
							}
							catch (
								ClassNotFoundException
								| IllegalArgumentException
								| IllegalAccessException
								| InstantiationException
								| InvocationTargetException
								| NoSuchMethodException
								| SecurityException e)
							{
								String msg = String.format("Failed at: %s", name);
								System.err.println(msg);
								e.printStackTrace();
							}
						}
					}
				}
			}
			oExtClass = oExtClass.getSuperclass();
		}
		return parentObject;
	}
}