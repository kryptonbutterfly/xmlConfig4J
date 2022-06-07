package de.tinycodecrank.xmlConfig4J.parser.assignable;

import static de.tinycodecrank.xmlConfig4J.utils.Utils.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.LoadHelper;
import de.tinycodecrank.xmlConfig4J.SaveHelper;

public final class SetParser implements ParserAssignable
{
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
			var setType = container.getClass().getName();
			setType = saveHelper.getMapping(setType);
			element.setAttribute(TYPE, setType);
			
			for (Object o : (Set<?>) container)
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
			final var set = (Set) type.getDeclaredConstructor().newInstance();
			
			final var nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++)
			{
				final var item = nodeList.item(i);
				if (loadIsNotNull(item, loadHelper))
				{
					try
					{
						final var	itemType	= getAttribute(item, TYPE).getValue();
						final var	itemClass	= loadHelper.getClass(loadHelper.getType(itemType));
						final var	oItem		= loadHelper.loadFromNode(itemClass, item);
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
						e.printStackTrace();
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