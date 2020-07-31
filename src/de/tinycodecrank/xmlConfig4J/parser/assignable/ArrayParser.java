package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public class ArrayParser implements ParserAssignable
{
	private static final Logger log = LogManager.getLogger(ArrayParser.class);

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
		return type.isArray();// && !type.getName().startsWith("[[");
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
			int arrayLength = Array.getLength(container);
			for (int i = 0; i < arrayLength; i++)
			{
				try
				{
					Object arrayItem = Array.get(container, i);
					Element item = saveHelper.saveObject(ITEM, arrayItem, document);
					if(arrayItem != null)
					{
						item.setAttribute(TYPE, saveHelper.getMapping(arrayItem.getClass().getName()));
					}
					element.appendChild(item);

				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					log.error(e::getMessage, e);
				}
			}
		}
	}

	@Override
	public Object load(Class<?> type, Node node, LoadHelper loadHelper) throws ClassNotFoundException
	{
		if (loadIsNotNull(node, loadHelper))
		{
			String typeName = type.getName();
			NodeList nodeList = node.getChildNodes();
			int dimCount = typeName.lastIndexOf('[') + 1;
			int length = nodeList.getLength();
			
			Object array;
			
			boolean isPrimitive = false;
			if (dimCount > 1)
			{
				if (typeName.endsWith(";"))
				{
					Class<?> innerType = loadHelper.getClass(typeName.substring(dimCount + 1, typeName.length() - 1));
					Class<?> componentClass = Class.forName(typeName.substring(1, dimCount + 1) + innerType.getName() + ";");
					array = Array.newInstance(componentClass, length);
				}
				else
				{
					Class<?> componentClass = Class.forName(typeName.substring(1));
					array = Array.newInstance(componentClass, length);
				}
			}
			else
			{
				if (typeName.length() > 2 || !PRIMITIVES.containsKey(typeName.charAt(1)))
				{
					Class<?> innerType = loadHelper.getClass(typeName.substring(2, typeName.length() - 1));
					Class<?> componentClass = Class.forName(innerType.getName());
					array = Array.newInstance(componentClass, length);
				}
				else
				{
					array = PRIMITIVES.get(typeName.charAt(1)).apply(length);
					isPrimitive = true;
				}
			}
			for (int i = 0; i < length; i++)
			{
				Node item = nodeList.item(i);
				if (loadIsNotNull(item, loadHelper))
				{
					try
					{
						Class<?> itemType;
						if(isPrimitive)
						{
							itemType = Array.get(array, i).getClass();
						}
						else
						{
							Attr attrItemType = getAttribute(item, TYPE);
							itemType = loadHelper.getClass(loadHelper.getType(attrItemType.getValue()));
						}
						Object oItem = loadHelper.loadFromNode(itemType, item);
						try
						{
							Array.set(array, i, oItem);
						}
						catch (IllegalArgumentException e)
						{
							log.error(typeName + " - " + oItem.getClass().getName(), e);

						}
					}
					catch (SecurityException | IllegalAccessException | InstantiationException
							| InvocationTargetException | NoSuchMethodException | ClassNotFoundException e)
					{
						log.error(e);
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