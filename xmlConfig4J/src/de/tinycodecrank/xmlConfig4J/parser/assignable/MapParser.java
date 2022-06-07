package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public final class MapParser implements ParserAssignable
{
	private static final String	KEY		= "key";
	private static final String	VALUE	= "value";
	private static final String	ITEM	= "item";
	
	@Override
	public boolean canParse(Class<?> type)
	{
		return Map.class.isAssignableFrom(type);
	}
	
	@Override
	public void save(Element element, Object container, Document document, SaveHelper saveHelper)
	{
		if (container == null)
		{
			element.setAttribute(NULL, TRUE);
		}
		else
		{
			var mapType = container.getClass().getName();
			mapType = saveHelper.getMapping(mapType);
			element.setAttribute(TYPE, mapType);
			
			for (final var entry : ((Map<?, ?>) container).entrySet())
			{
				try
				{
					final var	key		= entry.getKey();
					final var	value	= entry.getValue();
					
					final var keyE = saveHelper.saveObject(KEY, key, document);
					if (key != null)
					{
						keyE.setAttribute(TYPE, saveHelper.getMapping(key.getClass().getName()));
					}
					
					final var valueE = saveHelper.saveObject(VALUE, value, document);
					if (value != null)
					{
						valueE.setAttribute(TYPE, saveHelper.getMapping(value.getClass().getName()));
					}
					
					final var itemE = document.createElement(ITEM);
					itemE.appendChild(keyE);
					itemE.appendChild(valueE);
					element.appendChild(itemE);
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object load(Class<?> type, Node node, LoadHelper loadHelper)
		throws InstantiationException,
		IllegalAccessException,
		IllegalArgumentException,
		InvocationTargetException,
		NoSuchMethodException,
		SecurityException
	{
		if (loadIsNotNull(node, loadHelper) && type != null)
		{
			@SuppressWarnings("rawtypes")
			final Map map = (Map) type.getDeclaredConstructor().newInstance();
			
			final var nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				final var	item		= nodeList.item(i);
				final var	itemList	= item.getChildNodes();
				Node		keyNode		= null;
				Node		valueNode	= null;
				for (int j = 0; j < 2; j++)
				{
					if (itemList.item(j).getNodeName().equals(KEY))
					{
						keyNode = itemList.item(j);
					}
					else
					{
						valueNode = itemList.item(j);
					}
				}
				
				try
				{
					Object	key		= null;
					Object	value	= null;
					
					if (loadIsNotNull(keyNode, loadHelper))
					{
						final var	attrKeyType	= (Attr) keyNode.getAttributes().getNamedItem(TYPE);
						final var	keyType		= loadHelper.getClass(loadHelper.getType(attrKeyType.getValue()));
						key = loadHelper.loadFromNode(keyType, keyNode);
					}
					if (loadIsNotNull(valueNode, loadHelper))
					{
						final var	attrValueType	= (Attr) valueNode.getAttributes().getNamedItem(TYPE);
						final var	valueType		= loadHelper.getClass(loadHelper.getType(attrValueType.getValue()));
						value = loadHelper.loadFromNode(valueType, valueNode);
					}
					map.put(key, value);
				}
				catch (
					InstantiationException
					| InvocationTargetException
					| NoSuchMethodException
					| SecurityException
					| IllegalAccessException
					| IllegalArgumentException
					| ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			return map;
		}
		else
		{
			return null;
		}
	}
}