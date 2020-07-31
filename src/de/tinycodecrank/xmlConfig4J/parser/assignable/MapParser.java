package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.NULL;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.TRUE;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.loadIsNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public class MapParser implements ParserAssignable
{
	private static final Logger log = LogManager.getLogger(MapParser.class);

	private static final String KEY = "key";
	private static final String VALUE = "value";
	private static final String ITEM = "item";

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
			String mapType = container.getClass().getName();
			mapType = saveHelper.getMapping(mapType);
			element.setAttribute(TYPE, mapType);

			Map<?, ?> map = (Map<?, ?>) container;
			for (Entry<?, ?> entry : map.entrySet())
			{
				try
				{
					Object key = entry.getKey();
					Object value = entry.getValue();

					Element keyE = saveHelper.saveObject(KEY, key, document);
					if (key != null)
					{
						keyE.setAttribute(TYPE, saveHelper.getMapping(key.getClass().getName()));
					}

					Element valueE = saveHelper.saveObject(VALUE, value, document);
					if (value != null)
					{
						valueE.setAttribute(TYPE, saveHelper.getMapping(value.getClass().getName()));
					}

					Element itemE = document.createElement(ITEM);
					itemE.appendChild(keyE);
					itemE.appendChild(valueE);
					element.appendChild(itemE);
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					log.error(e::getMessage, e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object load(Class<?> type, Node node, LoadHelper loadHelper)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException
	{
		if (loadIsNotNull(node, loadHelper) && type != null)
		{
			@SuppressWarnings("rawtypes")
			Map map = (Map) type.getDeclaredConstructor().newInstance();

			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node item = nodeList.item(i);
				NodeList itemList = item.getChildNodes();
				Node keyNode = null, valueNode = null;
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
					Object key = null, value = null;

					if (loadIsNotNull(keyNode, loadHelper))
					{
						Attr attrKeyType = (Attr) keyNode.getAttributes().getNamedItem(TYPE);
						Class<?> keyType = loadHelper.getClass(loadHelper.getType(attrKeyType.getValue()));
						key = loadHelper.loadFromNode(keyType, keyNode);
					}
					if (loadIsNotNull(valueNode, loadHelper))
					{
						Attr attrValueType = (Attr) valueNode.getAttributes().getNamedItem(TYPE);
						Class<?> valueType = loadHelper.getClass(loadHelper.getType(attrValueType.getValue()));
						value = loadHelper.loadFromNode(valueType, valueNode);
					}
					map.put(key, value);
				}
				catch (InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException
						| IllegalAccessException | IllegalArgumentException | ClassNotFoundException e)
				{
					log.error(e::getMessage, e);
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