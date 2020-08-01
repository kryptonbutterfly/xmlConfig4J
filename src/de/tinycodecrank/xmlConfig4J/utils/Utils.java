package de.tinycodecrank.xmlConfig4J.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

import de.tinycodecrank.xmlConfig4J.LoadHelper;

public final class Utils
{
	private Utils(){}
	
	public static final String NULL = "null";
	public static final String TRUE = "true";
	public static final String VALUE = "value";
	public static final String TYPE = "type";
	
	public static boolean loadIsNotNull(Node node, LoadHelper loadHelper)
	{
		Attr isNull = (Attr) node.getAttributes().getNamedItem(NULL);
		Attr type = (Attr) node.getAttributes().getNamedItem(TYPE);		//legacy compatibility
		if(isNull != null)
		{
			return !isNull.getValue().equalsIgnoreCase(TRUE);
		}
		else if(type != null)
		{
			return !loadHelper.getType(type.getValue()).equalsIgnoreCase(NULL);	
		}
		else
		{
			return true;
		}
	}
	
	public static Attr getAttribute(Node node, String attr)
	{
		return (Attr) node.getAttributes().getNamedItem(attr);
	}
}