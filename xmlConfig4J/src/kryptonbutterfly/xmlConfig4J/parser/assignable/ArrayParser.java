package kryptonbutterfly.xmlConfig4J.parser.assignable;

import static kryptonbutterfly.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.LoadHelper;
import kryptonbutterfly.xmlConfig4J.SaveHelper;

public final class ArrayParser implements ParserAssignable
{
	private static final Map<Character, IntFunction<?>> PRIMITIVES = primitives();
	
	private static final Map<Character, IntFunction<?>> primitives()
	{
		HashMap<Character, IntFunction<?>> primitiveMap = new HashMap<>();
		primitiveMap.put('Z', l -> new boolean[l]);
		primitiveMap.put('B', l -> new byte[l]);
		primitiveMap.put('C', l -> new char[l]);
		primitiveMap.put('S', l -> new short[l]);
		primitiveMap.put('I', l -> new int[l]);
		primitiveMap.put('F', l -> new float[l]);
		primitiveMap.put('J', l -> new long[l]);
		primitiveMap.put('D', l -> new double[l]);
		return Collections.unmodifiableMap(primitiveMap);
	}
	
	@Override
	public boolean canParse(Class<?> type)
	{
		return type.isArray();
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
			final int arrayLength = Array.getLength(container);
			for (int i = 0; i < arrayLength; i++)
			{
				try
				{
					final var	arrayItem	= Array.get(container, i);
					final var	item		= saveHelper.saveObject(ITEM, arrayItem, document);
					if (arrayItem != null)
					{
						item.setAttribute(TYPE, saveHelper.getMapping(arrayItem.getClass().getName()));
					}
					element.appendChild(item);
					
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public Object load(Class<?> type, Node node, LoadHelper loadHelper) throws ClassNotFoundException
	{
		if (loadIsNotNull(node, loadHelper))
		{
			final var	typeName	= type.getName();
			final var	nodeList	= node.getChildNodes();
			final int	dimCount	= typeName.lastIndexOf('[') + 1;
			final int	length		= nodeList.getLength();
			
			final Object array;
			
			boolean isPrimitive = false;
			if (dimCount > 1)
			{
				if (typeName.endsWith(";"))
				{
					final var	innerType		= loadHelper
						.getClass(typeName.substring(dimCount + 1, typeName.length() - 1));
					final var	componentClass	= Class
						.forName(typeName.substring(1, dimCount + 1) + innerType.getName() + ";");
					array = Array.newInstance(componentClass, length);
				}
				else
				{
					final var componentClass = Class.forName(typeName.substring(1));
					array = Array.newInstance(componentClass, length);
				}
			}
			else
			{
				if (typeName.length() > 2 || !PRIMITIVES.containsKey(typeName.charAt(1)))
				{
					final var	innerType		= loadHelper.getClass(typeName.substring(2, typeName.length() - 1));
					final var	componentClass	= Class.forName(innerType.getName());
					array = Array.newInstance(componentClass, length);
				}
				else
				{
					array		= PRIMITIVES.get(typeName.charAt(1)).apply(length);
					isPrimitive	= true;
				}
			}
			for (int i = 0; i < length; i++)
			{
				final var item = nodeList.item(i);
				if (loadIsNotNull(item, loadHelper))
				{
					try
					{
						final Class<?> itemType;
						if (isPrimitive)
						{
							itemType = Array.get(array, i).getClass();
						}
						else
						{
							final var attrItemType = getAttribute(item, TYPE);
							itemType = loadHelper.getClass(loadHelper.getType(attrItemType.getValue()));
						}
						final var oItem = loadHelper.loadFromNode(itemType, item);
						try
						{
							Array.set(array, i, oItem);
						}
						catch (IllegalArgumentException e)
						{
							e.printStackTrace();
						}
					}
					catch (
						SecurityException
						| IllegalAccessException
						| InstantiationException
						| InvocationTargetException
						| NoSuchMethodException
						| ClassNotFoundException e)
					{
						e.printStackTrace();
					}
				}
			}
			return array;
		}
		else
		{
			return null;
		}
	}
}