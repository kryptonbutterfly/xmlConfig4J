package kryptonbutterfly.xmlConfig4J.adapter.boxed;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlDataBinding;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public class FloatObjAdapter implements TypeAdapter<Float>
{
	@Override
	public Class<Float> getType()
	{
		return Float.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, Float value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
			elem.setAttribute(XmlDataBinding.VALUE, value.toString());
	}
	
	@Override
	public Float read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var valueAttr = XmlReader.getAttribute(node, XmlDataBinding.VALUE);
		if (valueAttr == null)
			return null;
		return Float.parseFloat(valueAttr.getValue());
	}
}
