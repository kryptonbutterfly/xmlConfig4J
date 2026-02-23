package kryptonbutterfly.xmlConfig4J.adapter.misc;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class StringAdapter implements TypeAdapter<String>
{
	@Override
	public Class<String> getType()
	{
		return String.class;
	}
	
	@Override
	public boolean isValueType()
	{
		return true;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, String value)
	{
		elem.setAttribute(writer.getTags().valueTag(), value);
	}
	
	@Override
	public String read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return XmlReader.getAttribute(node, reader.getTags().valueTag()).getValue();
	}
}
