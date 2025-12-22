package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import static kryptonbutterfly.xmlConfig4J.XmlDataBinding.*;

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

public class ObjectArrayAdapter implements TypeAdapter<Object[]>
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
				final var item = writer.doc.createElement(ITEM);
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
		NoSuchMethodException
	{
		if (reader.isNull(node))
			return null;
		
		final var list = new ArrayList<>();
		for (final var n : new Nodes(node.getChildNodes()))
			if (n.getNodeName().equals(ITEM))
				list.add(reader.read(n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		return list.toArray(d -> (Object[]) Array.newInstance(classOfT.componentType(), d));
	}
}
