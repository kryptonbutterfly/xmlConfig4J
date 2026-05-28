package kryptonbutterfly.xmlConfig4J.comments.path;

import java.util.ArrayList;

import kryptonbutterfly.xmlConfig4J.Tags;

public final class PathView
{
	public static final PathView ROOT = new PathView();
	
	private final ArrayList<PathRef> path;
	
	private int size = 0;
	
	private PathView()
	{
		this.path = new ArrayList<>();
	}
	
	private PathView(PathView parent, PathRef child)
	{
		this.path = parent.path;
		path.add(child);
		this.size = path.size();
	}
	
	PathView(ArrayList<PathRef> path, int size)
	{
		if (path.size() < size)
			throw new IndexOutOfBoundsException(
				"Size was %d, but must be >= 0 and <= %d (path.size())".formatted(size, path.size()));
		
		this.path	= path;
		this.size	= size;
	}
	
	public int size()
	{
		return size;
	}
	
	public PathView child(PathRef child)
	{
		if (size() == path.size())
			return new PathView(this, child);
		if (child.equals(path.get(size())))
			return new PathView(path, size() + 1);
		
		final var path = new ArrayList<PathRef>();
		for (int i = 0; i < size(); i++)
			path.add(this.path.get(i));
		
		path.add(child);
		return new PathView(path, path.size());
	}
	
	public boolean hasParent()
	{
		return size > 0;
	}
	
	public PathView parent()
	{
		return new PathView(this.path, size - 1);
	}
	
	public String toString(Tags tags)
	{
		final var sb = new StringBuilder();
		path.stream()
			.map(r -> "/" + r.toString(tags))
			.forEach(sb::append);
		return sb.toString();
	}
	
	@Override
	public int hashCode()
	{
		int result = size;
		for (int i = 0; i < path.size(); i++)
			result = result * 31 + path.get(i).hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof PathView other))
			return false;
		if (size() != other.size())
			return false;
		
		for (int i = 0; i < size(); i++)
			if (!path.get(i).equals(other.path.get(i)))
				return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		var sb = new StringBuilder();
		for (int i = 0; i < size(); i++)
			sb.append("/").append(path.get(i));
		return sb.toString();
	}
}
