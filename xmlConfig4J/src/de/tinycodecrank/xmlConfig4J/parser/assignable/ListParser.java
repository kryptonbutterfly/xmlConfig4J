package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public final class ListParser implements ParserAssignable
{
	
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
			var listType = container.getClass().getName();
			listType = saveHelper.getMapping(listType);
			element.setAttribute(TYPE, listType);
			
			for (var o : (List<?>) container)
			{
				try
				{
					final var item = saveHelper.saveObject(ITEM, o, document);
					if (o != null)
					{
						item.setAttribute(TYPE, saveHelper.getMapping(o.getClass().getName()));
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
			final var list = (List) type.getDeclaredConstructor().newInstance();
			
			final var nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				final var item = nodeList.item(i);
				if (loadIsNotNull(item, loadHelper))
				{
					final var itemType = getAttribute(item, TYPE).getValue();
					try
					{
						final var	itemClass	= loadHelper.getClass(loadHelper.getType(itemType));
						final var	oItem		= loadHelper.loadFromNode(itemClass, item);
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
						e.printStackTrace();
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