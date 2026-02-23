package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.IntAdapter;

public final class IntArrayAdapter implements TypeAdapter<int[]>
{
	@Override
	public Class<int[]> getType()
	{
		return int[].class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, int[] value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (final int e : value)
			{
				final var item = writer.doc.createElement(writer.getTags().itemTag());
				IntAdapter.write(writer.getTags().valueTag(), item, e);
				elem.appendChild(item);
			}
	}
	
	@Override
	public int[] read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		
		final var	nodes	= node.getChildNodes();
		final var	result	= new int[nodes.getLength()];
		reader.registerInstance(node, result);
		
		final var list = new ArrayList<Integer>();
		for (final var n : new Nodes(nodes))
			if (n.getNodeName().equals(reader.getTags().itemTag()))
				list.add(IntAdapter.read(reader.getTags().valueTag(), n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		return result;
	}
}
