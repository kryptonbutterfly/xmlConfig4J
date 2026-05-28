package kryptonbutterfly.xmlConfig4J.comments.path;

import kryptonbutterfly.xmlConfig4J.Tags;

public record PathIndexRef(int index) implements PathRef
{
	public boolean match(int index)
	{
		return this.index == index;
	}
	
	public String toString()
	{
		return Integer.toString(index);
	}
	
	@Override
	public String toString(Tags tags)
	{
		return toString();
	}
}
