package kryptonbutterfly.xmlConfig4J.comments.path;

import kryptonbutterfly.xmlConfig4J.Tags;

public final class PathHashRef implements PathRef
{
	private int		hashKey;
	private boolean	initialized;
	
	public PathHashRef()
	{
		this.initialized	= false;
		this.hashKey		= -1;
	}
	
	public PathHashRef(Object key)
	{
		this.initialized	= true;
		this.hashKey		= key == null ? 0 : key.hashCode();
	}
	
	public <K> K init(K key)
	{
		if (initialized)
			throw new IllegalStateException("Duplicate init!");
		this.hashKey		= key == null ? 0 : key.hashCode();
		this.initialized	= true;
		return key;
	}
	
	public boolean match(int hashCode)
	{
		if (!initialized)
			throw new IllegalStateException("Can't match uninitialized!");
		
		return this.hashKey == hashCode;
	}
	
	@Override
	public String toString()
	{
		if (initialized)
			return "#%d".formatted(hashKey);
		return "#???";
	}
	
	@Override
	public String toString(Tags tags)
	{
		return toString();
	}
	
	@Override
	public int hashCode()
	{
		return hashKey;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof PathHashRef other))
			return false;
		return hashKey == other.hashKey;
	}
	
}
