package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class DoubleAdapter implements TypeAdapter<Double>
{
	@Override
	public Class<Double> getType()
	{
		return double.class;
	}
	
	@Override
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Double value)
	{
		write(writer.getTags().valueTag(), elem, value);
	}
	
	@Override
	public Double read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(reader.getTags().valueTag(), node);
	}
	
	public static void write(String tag, Element elem, double value)
	{
		elem.setAttribute(tag, Double.toString(value));
	}
	
	public static double read(String tag, Node node)
	{
		return Double.parseDouble(XmlReader.getAttribute(node, tag).getValue());
	}
}
