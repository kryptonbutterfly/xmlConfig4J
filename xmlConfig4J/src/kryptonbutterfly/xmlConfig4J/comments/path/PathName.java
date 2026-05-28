package kryptonbutterfly.xmlConfig4J.comments.path;

import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Tags;

public final record PathName(String name) implements GeneralPath
{
	@Override
	public String toString()
	{
		return name;
	}
	
	@Override
	public boolean match(Tags tags, Node node)
	{
		return name.equals(node.getNodeName());
	}
	
	@Override
	public String toString(Tags tags)
	{
		return toString();
	}
	
}
