package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;
import kryptonbutterfly.xmlConfig4J.exceptions.BrokenReferenceException;

public final class ObjectArrayAdapter implements TypeAdapter<Object[]>
{
	@Override
	public Class<Object[]> getType()
	{
		return Object[].class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Object[] value) throws IllegalAccessException
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (final var e : value)
			{
				final var item = writer.doc.createElement(writer.getTags().itemTag());
				elem.appendChild(item);
				if (e != null)
					writer.writeType(item, e.getClass());
				writer.write(item, e);
			}
	}
	
	@Override
	public Object[] read(XmlReader reader, Node node, Class<?> classOfT)
		throws ClassNotFoundException,
		AttributeNotFoundException,
		NoSuchFieldException,
		InvocationTargetException,
		InstantiationException,
		IllegalAccessException,
		NoSuchMethodException,
		BrokenReferenceException
	{
		if (reader.isNull(node))
			return null;
		
		final var	nodes	= node.getChildNodes();
		final var	result	= Array.newInstance(classOfT.componentType(), nodes.getLength());
		reader.registerInstance(node, result);
		
		final var list = new ArrayList<>();
		for (final var n : new Nodes(nodes))
			if (n.getNodeName().equals(reader.getTags().itemTag()))
				list.add(reader.read(n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		return list.toArray(d -> (Object[]) result);
	}
}
