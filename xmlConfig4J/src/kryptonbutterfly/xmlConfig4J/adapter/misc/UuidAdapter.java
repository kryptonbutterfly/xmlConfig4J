package kryptonbutterfly.xmlConfig4J.adapter.misc;

import java.util.UUID;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.TypeAdapter;
import kryptonbutterfly.xmlConfig4J.XmlReader;
import kryptonbutterfly.xmlConfig4J.XmlWriter;

public final class UuidAdapter implements TypeAdapter<UUID>
{
	public static final String	MOST_SIG	= "mostSigBits";
	public static final String	LEAST_SIG	= "leastSigBits";
	
	@Override
	public Class<UUID> getType()
	{
		return UUID.class;
	}
	
	@Override
	public void write(XmlWriter writer, Element elem, UUID value)
	{
		if (value == null)
			writer.writeNull(elem);
		else
		{
			elem.setAttribute(MOST_SIG, Long.toString(value.getMostSignificantBits()));
			elem.setAttribute(LEAST_SIG, Long.toString(value.getLeastSignificantBits()));
		}
	}
	
	@Override
	public UUID read(XmlReader reader, Node node, Class<?> classOfT)
	{
		if (reader.isNull(node))
			return null;
		final var	mostSigBit	= XmlReader.getAttribute(node, MOST_SIG).getValue();
		final var	leastSigBit	= XmlReader.getAttribute(node, LEAST_SIG).getValue();
		return reader.registerInstance(node, new UUID(Long.parseLong(mostSigBit), Long.parseLong(leastSigBit)));
	}
}
