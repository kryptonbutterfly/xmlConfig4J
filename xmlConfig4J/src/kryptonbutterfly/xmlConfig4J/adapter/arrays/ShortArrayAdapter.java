package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.ShortAdapter;

public final class ShortArrayAdapter implements TypeAdapter<short[]>
{
	@Override
	public Class<short[]> getType()
	{
		return short[].class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, short[] value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (final short e : value)
			{
				final var item = writer.doc.createElement(writer.getTags().itemTag());
				ShortAdapter.write(writer.getTags().valueTag(), item, e);
				elem.appendChild(item);
			}
		
	}
	
	@Override
	public short[] read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		
		final var	nodes	= node.getChildNodes();
		final var	result	= new short[nodes.getLength()];
		reader.registerInstance(node, result);
		
		final var list = new ArrayList<Short>();
		for (final var n : new Nodes(nodes))
			if (n.getNodeName().equals(reader.getTags().itemTag()))
				list.add(ShortAdapter.read(reader.getTags().valueTag(), n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		return result;
	}
}
