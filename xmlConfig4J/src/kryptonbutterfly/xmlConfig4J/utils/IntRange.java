package kryptonbutterfly.xmlConfig4J.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

public record IntRange(int length) implements Iterable<Integer>
{
	@Override
	public Iterator<Integer> iterator()
	{
		return new Iterator<Integer>()
		{
			int index = 0;
			
			@Override
			public boolean hasNext()
			{
				return index < length;
			}
			
			@Override
			public Integer next()
			{
				if (index == length)
					throw new NoSuchElementException("Range has been exhausted!");
				return index++;
			}
		};
	}
}
