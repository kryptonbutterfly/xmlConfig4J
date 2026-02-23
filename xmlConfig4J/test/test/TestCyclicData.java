package test;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.Validator;

public final class TestCyclicData
{
	@Test
	public void test()
	{
		var	data1	= new Wrapper();
		var	data2	= new Wrapper();
		data2.w	= data1;
		data1.w	= data2;
		
		Validator.validate2(data1);
	}
	
	public static class Wrapper
	{
		@Value
		public Object w = null;
	}
}
