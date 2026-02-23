package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.ByteAdapter;

public final class ByteArrayAdapter implements TypeAdapter<byte[]>
{
	@Override
	public Class<byte[]> getType()
	{
		return byte[].class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, byte[] value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (final byte e : value)
			{
				final var item = writer.doc.createElement(writer.getTags().itemTag());
				ByteAdapter.write(writer.getTags().valueTag(), item, e);
				elem.appendChild(item);
			}
	}
	
	@Override
	public byte[] read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		
		final var	nodes	= node.getChildNodes();
		final var	result	= new byte[nodes.getLength()];
		reader.registerInstance(node, result);
		
		final var list = new ArrayList<Byte>();
		for (final var n : new Nodes(nodes))
			if (n.getNodeName().equals(reader.getTags().itemTag()))
				list.add(ByteAdapter.read(reader.getTags().valueTag(), n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		
		return result;
	}
}
