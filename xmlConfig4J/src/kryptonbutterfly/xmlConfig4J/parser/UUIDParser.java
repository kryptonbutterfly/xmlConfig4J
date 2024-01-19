package kryptonbutterfly.xmlConfig4J.parser;

import static kryptonbutterfly.xmlConfig4J.utils.Utils.*;

import java.util.UUID;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.LoadHelper;
import kryptonbutterfly.xmlConfig4J.SaveHelper;

public class UUIDParser implements Parser
{
	private static final String	MOST_SIG	= "mostSigBits";
	private static final String	LEAST_SIG	= "leastSigBit";
	
	@Override
	public Class<?> parsedType()
	{
		return UUID.class;
	}
	
	@Override
	public void save(Element element, Object container, Document document, SaveHelper saveHelper)
	{
		final var uuid = (UUID) container;
		if (uuid == null)
		{
			element.setAttribute(NULL, TRUE);
		}
		else
		{
			element.setAttribute(MOST_SIG, Long.toString(uuid.getMostSignificantBits()));
			element.setAttribute(LEAST_SIG, Long.toString(uuid.getLeastSignificantBits()));
		}
	}
	
	@Override
	public Object load(Node node, LoadHelper loadHelper)
	{
		if (loadIsNotNull(node, loadHelper))
		{
			final var	mostSigBit	= getAttribute(node, MOST_SIG).getValue();
			final var	leastSigBit	= getAttribute(node, LEAST_SIG).getValue();
			return new UUID(Long.parseLong(mostSigBit), Long.parseLong(leastSigBit));
		}
		else
		{
			return null;
		}
	}
}