package kryptonbutterfly.xmlConfig4J.adapter.primitive;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class FloatAdapter implements TypeAdapter<Float>
{
	@Override
	public Class<Float> getType()
	{
		return float.class;
	}
	
	@Override
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Float value)
	{
		write(writer.getTags().valueTag(), elem, value);
	}
	
	@Override
	public Float read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return read(reader.getTags().valueTag(), node);
	}
	
	public static void write(String tag, Element elem, float value)
	{
		elem.setAttribute(tag, Float.toString(value));
	}
	
	public static float read(String tag, Node node)
	{
		return Float.parseFloat(XmlReader.getAttribute(node, tag).getValue());
	}
}
