package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.NULL;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.TRUE;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.getAttribute;
import static de.tinycodecrank.xmlConfig4J.utils.Utils.loadIsNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public class ListParser implements ParserAssignable
{
	private static final Logger log = LogManager.getLogger(ListParser.class);
	
	private static final String ITEM = "item";
	
	@Override
	public boolean canParse(Class<?> type)
	{
		return List.class.isAssignableFrom(type);
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
			String listType = container.getClass().getName();
			listType = saveHelper.getMapping(listType);
			element.setAttribute(TYPE, listType);
			
			List<?> list = (List<?>) container;
			for (Object o : list)
			{
				try
				{
					Element item = saveHelper.saveObject(ITEM, o, document);
					if(o != null)
					{
						item.setAttribute(TYPE, saveHelper.getMapping(o.getClass().getName()));
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
			List list = (List) type.getDeclaredConstructor().newInstance();
			
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node item = nodeList.item(i);
				if (loadIsNotNull(item, loadHelper))
				{
					String itemType = getAttribute(item, TYPE).getValue();
					try
					{
						Class<?>	itemClass	= loadHelper.getClass(loadHelper.getType(itemType));
						Object		oItem		= loadHelper.loadFromNode(itemClass, item);
						list.add(oItem);
					}
					catch (
						IllegalArgumentException
						| IllegalAccessException
						| InstantiationException
						| InvocationTargetException
						| NoSuchMethodException
						| SecurityException
						| ClassNotFoundException e)
					{
						log.error(e::getMessage, e);
					}
				}
				else
				{
					list.add(null);
				}
			}
			return list;
		}
		else
		{
			return null;
		}
	}
}