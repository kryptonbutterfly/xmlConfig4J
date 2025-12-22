package example;

import kryptonbutterfly.xmlConfig4J.annotations.Value;

public class CustomClass
{
	@Value
	public String foo = "Bar";
	
	@Override
	public String toString()
	{
		return foo;
	}
}