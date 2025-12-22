package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.FloatAdapter;

public final class FloatArrayAdapter implements TypeAdapter<float[]>
{
	@Override
	public Class<float[]> getType()
	{
		return float[].class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, float[] value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (final float e : value)
			{
				final var item = writer.doc.createElement(XmlDataBinding.ITEM);
				FloatAdapter.write(item, e);
				elem.appendChild(item);
			}
	}
	
	@Override
	public float[] read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		
		final var list = new ArrayList<Float>();
		for (final var n : new Nodes(node.getChildNodes()))
			if (n.getNodeName().equals(XmlDataBinding.ITEM))
				list.add(FloatAdapter.read(n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		final var result = new float[list.size()];
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		return result;
	}
}
