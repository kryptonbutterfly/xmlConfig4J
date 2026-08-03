package kryptonbutterfly.xmlConfig4J.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

public final class ToStringUtils
{
	public static String annToSimpleString(Annotation ann)
	{
		if (ann == null)
			return "null";
		
		final var annType = ann.annotationType();
		
		final var sb = new StringBuilder("@");
		sb.append(annType.getSimpleName());
		sb.append("{");
		
		boolean first = true;
		for (var m : annType.getDeclaredMethods())
		{
			if (first)
				first = false;
			else
				sb.append(", ");
			
			sb.append(m.getName())
				.append("=");
			
			final String valueTempl = String.class.equals(m.getReturnType()) ? "\"%s\"" : "%s";
			try
			{
				sb.append(valueTempl.formatted(m.invoke(ann)));
			}
			catch (IllegalAccessException | InvocationTargetException e)
			{
				sb.append("??ERR??");
			}
		}
		
		return sb.append("}").toString();
	}
}
