package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.ShortAdapter;

public class ShortArrayAdapter implements TypeAdapter<short[]>
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
				final var item = writer.doc.createElement(XmlDataBinding.ITEM);
				ShortAdapter.write(item, e);
				elem.appendChild(item);
			}
		
	}
	
	@Override
	public short[] read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var list = new ArrayList<Short>();
		for (final var n : new Nodes(node.getChildNodes()))
			if (n.getNodeName().equals(XmlDataBinding.ITEM))
				list.add(ShortAdapter.read(n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		final var result = new short[list.size()];
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		return result;
	}
}
