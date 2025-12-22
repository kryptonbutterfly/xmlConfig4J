package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.BoolAdapter;

public class BoolArrayAdapter implements TypeAdapter<boolean[]>
{
	@Override
	public Class<boolean[]> getType()
	{
		return boolean[].class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, boolean[] value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (final boolean e : value)
			{
				final var item = writer.doc.createElement(XmlDataBinding.ITEM);
				BoolAdapter.write(item, e);
				elem.appendChild(item);
			}
	}
	
	@Override
	public boolean[] read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var list = new ArrayList<Boolean>();
		for (final var n : new Nodes(node.getChildNodes()))
			if (n.getNodeName().equals(XmlDataBinding.ITEM))
				list.add(BoolAdapter.read(n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
		final var result = new boolean[list.size()];
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		return result;
	}
}
