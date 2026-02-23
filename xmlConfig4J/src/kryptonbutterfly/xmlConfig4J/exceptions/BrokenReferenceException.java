package kryptonbutterfly.xmlConfig4J.exceptions;

import org.w3c.dom.Node;

@SuppressWarnings("serial")
public final class BrokenReferenceException extends BindingException
{
	private static final String msgTmpl = "Unable to reference '%s' at %s, since it has not been defined before.";
	
	public BrokenReferenceException(String refId, Node node)
	{
		super(msgTmpl.formatted(refId, node.getFeature("LS", "3.0")));
	}
}
