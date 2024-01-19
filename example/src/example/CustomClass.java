package example;

import kryptonbutterfly.xmlConfig4J.annotations.Value;

public class CustomClass
{
	public CustomClass()
	{
		//An empty constructor is required!
	}
	
	@Value
	public String foo = "Bar";
	
	@Override
	public String toString()
	{
		return foo;
	}
}