package kryptonbutterfly.xmlConfig4J.comments.path;

import kryptonbutterfly.xmlConfig4J.Tags;

public record PathTypeRef(String type) implements PathRef
{
	public boolean match(String type)
	{
		return this.type.equals(type);
	}
	
	@Override
	public String toString(Tags tags)
	{
		return toString();
	}
	
	@Override
	public String toString()
	{
		return type;
	}
}
