package kryptonbutterfly.xmlConfig4J.adapter.misc;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class StringAdapter implements TypeAdapter<String>
{
	@Override
	public Class<String> getType()
	{
		return String.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, String value)
	{
		elem.setAttribute(XmlDataBinding.VALUE, value);
	}
	
	@Override
	public String read(XmlReader reader, Node node, Class<?> classOfT)
	{
		return XmlReader.getAttribute(node, XmlDataBinding.VALUE).getValue();
	}
}
