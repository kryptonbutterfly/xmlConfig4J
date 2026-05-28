package kryptonbutterfly.xmlConfig4J.comments.path;

import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Tags;

public interface GeneralPath extends PathRef
{
	boolean match(Tags tags, Node node);
	
	public static GeneralPath create(Tags tags, Node node)
	{
		return create(tags, node.getNodeName());
	}
	
	static GeneralPath create(Tags tags, String name)
	{
		final GeneralPath ref = PathTag.create(tags, name);
		if (ref != null)
			return ref;
		return new PathName(name);
	}
}
