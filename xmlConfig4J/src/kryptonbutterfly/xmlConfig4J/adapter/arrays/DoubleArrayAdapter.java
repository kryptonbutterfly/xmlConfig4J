package kryptonbutterfly.xmlConfig4J.adapter.arrays;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Nodes;
import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;
import kryptonbutterfly.xmlConfig4J.adapter.primitive.DoubleAdapter;
import kryptonbutterfly.xmlConfig4J.exceptions.AttributeNotFoundException;

public final class DoubleArrayAdapter implements TypeAdapter<double[]>
{
	@Override
	public Class<double[]> getType()
	{
		return double[].class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, double[] value) throws IllegalAccessException
	{
		if (value == null)
			writer.writeNull(elem);
		else
			for (final double d : value)
			{
				final var item = writer.doc.createElement(XmlDataBinding.ITEM);
				DoubleAdapter.write(item, d);
				elem.appendChild(item);
			}
	}
	
	@Override
	public double[] read(XmlReader reader, Node node, Class<?> classOfT)
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
		
		final var list = new ArrayList<Double>();
		for (final var n : new Nodes(node.getChildNodes()))
			if (n.getNodeName().equals(XmlDataBinding.ITEM))
				list.add(DoubleAdapter.read(n));
			else
				System.err.printf("Unexpected element '%s'\n", n.getNodeName());
			
		final var result = new double[list.size()];
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		return result;
	}
}
