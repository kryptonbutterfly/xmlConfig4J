package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class FloatAdapter implements TypeAdapter<Float>
{
	@Override
	public Class<Float> getType()
	{
		return float.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Float value)
	{
		write(elem, value);
	}
	
	@Override
	public Float read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(node);
	}
	
	public static void write(Element elem, float value)
	{
		elem.setAttribute(XmlDataBinding.VALUE, Float.toString(value));
	}
	
	public static float read(Node node)
	{
		return Float.parseFloat(XmlReader.getAttribute(node, XmlDataBinding.VALUE).getValue());
	}
}
