package kryptonbutterfly.xmlConfig4J;

public record DataLocation(Class<?> container, InitMethod init, int line, Class<?> valueType, String name)
{
	
	public DataLocation(Class<?> container, InitMethod init, Class<?> valueType, String name)
	{
		this(container, init, -1, valueType, name);
	}
	
	public String fieldDesc()
	{
		return "%s %s".formatted(valueType.getSimpleName(), name);
	}
	
	public String toString()
	{
		return "at %s.%s(%s.java:%d)".formatted(container.getName(), init.initName, container.getSimpleName(), line);
	}
	
	public static enum InitMethod
	{
		STATIC("<cinit>"),
		IMPLICIT("<cinit>"),
		CONSTRUCTOR("<init>");
		
		private final String initName;
		
		InitMethod(String initName)
		{
			this.initName = initName;
		}
	}
}
