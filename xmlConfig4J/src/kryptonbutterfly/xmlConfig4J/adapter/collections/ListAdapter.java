package kryptonbutterfly.xmlConfig4J.adapter.collections;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;

@SuppressWarnings("rawtypes")
public class ListAdapter implements TypeAdapter<List>
{
	@Override
	public Class<List> getType()
	{
		return List.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, List value) throws IllegalAccessException
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (var child : value)
			{
				final var childElem = writer.doc.createElement(XmlDataBinding.ITEM);
				elem.appendChild(childElem);
				if (child == null)
					writer.writeNull(childElem);
				else
				{
					writer.write(childElem, child, child.getClass());
					writer.writeType(childElem, child.getClass());
				}
			}
	}
	
	@Override
	public List<?> read(XmlReader reader, Node node, Class<?> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		NoSuchFieldException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException
	{
		if (reader.isNull(node))
			return null;
		final var	type	= reader.getType(node);
		final var	list	= (List<?>) type.getConstructor().newInstance();
		for (final var n : new Nodes(node.getChildNodes()))
			if (n.getNodeName().equals(XmlDataBinding.ITEM))
				list.add(reader.read(n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
		return list;
	}
}
