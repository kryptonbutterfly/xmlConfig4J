package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.ByteAdapter;

public class ByteArrayAdapter implements TypeAdapter<byte[]>
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
				final var item = writer.doc.createElement(XmlDataBinding.ITEM);
				ByteAdapter.write(item, e);
				elem.appendChild(item);
			}
	}
	
	@Override
	public byte[] read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		
		final var list = new ArrayList<Byte>();
		for (final var n : new Nodes(node.getChildNodes()))
			if (n.getNodeName().equals(XmlDataBinding.ITEM))
				list.add(ByteAdapter.read(n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		final var result = new byte[list.size()];
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		
		return result;
	}
}
