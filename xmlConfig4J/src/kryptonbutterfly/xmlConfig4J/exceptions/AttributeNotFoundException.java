package kryptonbutterfly.xmlConfig4J.exceptions;

import org.w3c.dom.Node;

@SuppressWarnings("serial")
public class AttributeNotFoundException extends Exception
{
	private static final String msgTmpl = "The Node '%s' doesn't have the expected Attribute '%s'";
	
	public AttributeNotFoundException(String attribute, Node node)
	{
		super(message(attribute, node));
	}
	
	private static String message(String attribute, Node node)
	{
		return msgTmpl.formatted(
			node.getNodeName(),
			attribute);
	}
}
