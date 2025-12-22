package utils;

import java.util.Arrays;
import java.util.Objects;

import kryptonbutterfly.xmlConfig4J.annotations.Value;

public interface GenericUtils
{
	default String toString(Object self)
	{
		final var	type	= self.getClass();
		final var	sb		= new StringBuilder(type.getName() + "{\n");
		Arrays.stream(type.getDeclaredFields())
			.filter(f -> f.isAnnotationPresent(Value.class))
			.forEach(field ->
			{
				try
				{
					sb.append("\t").append(field.getName()).append("=");
					final var value = field.get(self);
					if (value == null)
						sb.append("null");
					else
					{
						final var valueType = value.getClass();
						if (valueType.isArray())
							sb.append(Arrays.deepToString((Object[]) value).replace("\n", "\n\t"));
						else
							sb.append(value);
					}
					sb.append("\n");
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			});
		sb.append("}");
		return sb.toString();
	}
	
	default boolean equals(Object self, Object obj)
	{
		if (self == obj)
			return true;
		final var type = self.getClass();
		if (type != obj.getClass())
			return false;
		
		return Arrays.stream(self.getClass().getDeclaredFields())
			.filter(f -> f.isAnnotationPresent(Value.class))
			.allMatch(field ->
			{
				try
				{
					return Objects.deepEquals(field.get(self), field.get(obj));
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			});
	}
	
	default int hashCode(Object self)
	{
		final var	sum		= new HashCode(1);
		final var	type	= self.getClass();
		Arrays.stream(type.getDeclaredFields())
			.filter(f -> f.isAnnotationPresent(Value.class))
			.forEach(f ->
			{
				try
				{
					final var value = f.get(self);
					if (value == null)
						sum.append(Objects.hashCode(value));
					else
					{
						final var eType = value.getClass();
						
						if (value instanceof boolean[] a)
							sum.append(Arrays.hashCode(a));
						else if (value instanceof byte[] a)
							sum.append(Arrays.hashCode(a));
						else if (value instanceof char[] a)
							sum.append(Arrays.hashCode(a));
						else if (value instanceof short[] a)
							sum.append(Arrays.hashCode(a));
						else if (value instanceof int[] a)
							sum.append(Arrays.hashCode(a));
						else if (value instanceof float[] a)
							sum.append(Arrays.hashCode(a));
						else if (value instanceof long[] a)
							sum.append(Arrays.hashCode(a));
						else if (value instanceof double[] a)
							sum.append(Arrays.hashCode(a));
						else if (eType.isArray())
							sum.append(Arrays.deepHashCode((Object[]) value));
						else
							sum.append(Objects.hashCode((Object[]) value));
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			});
		return sum.sum;
	}
	
	public static class HashCode
	{
		final int	prime	= 31;
		public int	sum		= 0;
		
		public HashCode(int sum)
		{
			this.sum = sum;
		}
		
		public HashCode append(int hashCode)
		{
			this.sum = sum * prime + hashCode;
			return this;
		}
	}
}
