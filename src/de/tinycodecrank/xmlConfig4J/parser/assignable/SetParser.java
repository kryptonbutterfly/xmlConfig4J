package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public class SetParser implements ParserAssignable
{
	private static final Logger log = LogManager.getLogger(SetParser.class);
	
	private static final String ITEM = "item";
	
	@Override
	public boolean canParse(Class<?> type)
	{
		return Set.class.isAssignableFrom(type);
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
			String setType = container.getClass().getName();
			setType = saveHelper.getMapping(setType);
			element.setAttribute(TYPE, setType);
			
			Set<?> set = (Set<?>) container;
			for (Object o : set)
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
			Set set = (Set) type.getDeclaredConstructor().newInstance();
			
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				Node item = nodeList.item(i);
				if (loadIsNotNull(item, loadHelper))
				{
					try
					{
						String		itemType	= getAttribute(item, TYPE).getValue();
						Class<?>	itemClass	= loadHelper.getClass(loadHelper.getType(itemType));
						Object		oItem		= loadHelper.loadFromNode(itemClass, item);
						set.add(oItem);
					}
					catch (
						ClassNotFoundException
						| InstantiationException
						| IllegalAccessException
						| IllegalArgumentException
						| InvocationTargetException
						| NoSuchMethodException
						| SecurityException e)
					{
						log.error(e::getMessage, e);
					}
				}
				else
				{
					set.add(null);
				}
			}
			return set;
		}
		else
		{
			return null;
		}
	}
}