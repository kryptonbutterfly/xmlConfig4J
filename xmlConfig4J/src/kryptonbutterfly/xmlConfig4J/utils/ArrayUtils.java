package kryptonbutterfly.xmlConfig4J.utils;

import java.util.Objects;

public final class ArrayUtils
{
	@SafeVarargs
	public static <A> boolean containsAll(A[] target, A... required)
	{
		for (var r : required)
			if (!contains(target, r))
				return false;
		return true;
	}
	
	private static <A> boolean contains(A[] target, A required)
	{
		for (var a : target)
			if (Objects.equals(a, required))
				return true;
		return false;
	}
}
