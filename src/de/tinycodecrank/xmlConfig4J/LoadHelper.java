package de.tinycodecrank.xmlConfig4J;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.getAttribute;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.loadIsNotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.function.Function;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tinycodecrank.xmlConfig4J.annotations.Value;
import de.tinycodecrank.xmlConfig4J.parser.Parser;
import de.tinycodecrank.xmlConfig4J.parser.assignable.ParserAssignable;

public class LoadHelper
{
	private static final Logger log = LogManager.getLogger(LoadHelper.class.getName());
	
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
			Class<?> clazz = classNameHistory.get(type);
			if (clazz != null)
			{
				return clazz;
			}
			else
			{
				return Class.forName(type);
			}
		}
		catch(ClassNotFoundException e)
		{
			System.err.println(type);
			throw e;
		}
	}
	
	public final String getType(String type)
	{
		if (typeMappings != null && NumberUtils.isParsable(type))
		{
			String tmp = typeMappings[Integer.parseInt(type)];
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
			if(loadIsNotNull(node, this))
			{
				Attr		type		= getAttribute(node, TYPE);
				Class<?>	objectType	= null;
				if (type != null)
				{
					try
					{
						objectType = getClass(getType(type.getValue()));
					}
					catch (ClassNotFoundException e)
					{
						log.error(e::getMessage, e);
					}
				}
				if (objectType == null)
				{
					objectType = field.getType();
				}
				PrivAction.doPrivileged(() -> field.setAccessible(true));
				field.set(config, loadFromNode(objectType, node));
			}
			else
			{
				field.set(config, null);
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
			log.error(e::getMessage, e);
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
		Parser parser = this.parser.get(type.getName());
		if (parser != null)
		{
			return parser.load(node, this);
		}
		else
		{
			ParserAssignable parserA = getParser.apply(type);
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
		Object parentObject = oExtClass.getDeclaredConstructor().newInstance();
		oExtClass = parentObject.getClass();
		do
		{
			for (Field varField : oExtClass.getDeclaredFields())
			{
				if (varField.isAnnotationPresent(Value.class))
				{
					NodeList nodeList = parent.getChildNodes();
					for (int i = 0; i < nodeList.getLength(); i++)
					{
						Node	node	= nodeList.item(i);
						String	name	= node.getNodeName();
						if (varField.getName().equals(name))
						{
							try
							{
								PrivAction.doPrivileged(() -> varField.setAccessible(true));
								Attr		type	= getAttribute(node, TYPE);
								Class<?>	objectType;
								if (type != null)
								{
									objectType = getClass(getType(type.getValue()));
								}
								else
								{
									objectType = varField.getType();
								}
								Parser parser = this.parser.get(objectType.getName());
								if (parser != null)
								{
									parser.load(varField, parentObject, node, this);
								}
								else
								{
									ParserAssignable parserA = getParser.apply(objectType);
									if (parserA != null)
									{
										varField.set(parentObject, parserA.load(objectType, node, this));
									}
									else
									{
										if (loadIsNotNull(node, this))
										{
											varField.set(parentObject, loadAsAnnotated(objectType, node));
										}
										else
										{
											varField.set(parentObject, null);
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
								log.error(msg);
								log.error(e::getMessage, e);
							}
						}
					}
				}
			}
		}
		while ((oExtClass = oExtClass.getSuperclass()) != null && oExtClass != Object.class);
		return parentObject;
	}
}