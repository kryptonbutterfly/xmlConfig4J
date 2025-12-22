package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class DoubleAdapter implements TypeAdapter<Double>
{
	@Override
	public Class<Double> getType()
	{
		return double.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Double value)
	{
		write(elem, value);
	}
	
	@Override
	public Double read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(node);
	}
	
	public static void write(Element elem, double value)
	{
		elem.setAttribute(XmlDataBinding.VALUE, Double.toString(value));
	}
	
	public static double read(Node node)
	{
		return Double.parseDouble(XmlReader.getAttribute(node, XmlDataBinding.VALUE).getValue());
	}
}
