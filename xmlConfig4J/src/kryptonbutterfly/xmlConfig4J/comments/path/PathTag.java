package kryptonbutterfly.xmlConfig4J.comments.path;

import org.w3c.dom.Node;

import kryptonbutterfly.xmlConfig4J.Tags;
import kryptonbutterfly.xmlConfig4J.XmlTags;

public enum PathTag implements GeneralPath
{
	ROOT(XmlTags.ROOT),
	TYPES(XmlTags.TYPES),
	DATA(XmlTags.DATA);
	
	private final XmlTags tag;
	
	PathTag(XmlTags tag)
	{
		this.tag = tag;
	}
	
	@Override
	public String toString()
	{
		return tag.name();
	}
	
	@Override
	public String toString(Tags tags)
	{
		return switch (this)
		{
			case ROOT -> tags.rootTag();
			case TYPES -> tags.typesTag();
			case DATA -> tags.dataTag();
		};
	}
	
	@Override
	public boolean match(Tags tags, Node node)
	{
		return node.getNodeName().equals(switch (this)
		{
			case ROOT -> tags.rootTag();
			case TYPES -> tags.typesTag();
			case DATA -> tags.dataTag();
		});
	}
	
	public static PathTag create(Tags tags, String name)
	{
		if (name.equals(tags.rootTag()))
			return ROOT;
		if (name.equals(tags.typesTag()))
			return TYPES;
		if (name.equals(tags.dataTag()))
			return DATA;
		return null;
	}
}
