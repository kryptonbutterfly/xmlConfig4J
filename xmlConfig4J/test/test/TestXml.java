package test;

import org.junit.jupiter.api.Test;

import components.Comp;
import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.GenericUtils;
import utils.Validator;

public class TestXml implements Validator, GenericUtils
{
	@Value
	public int a = 0;
	
	@Value
	public double b = 0.0;
	
	@Value
	public boolean c = true;
	
	@Value("Hello, Info!")
	public String[] d = null;
	
	@Value
	public String[] e = new String[] { null };
	
	@Value
	public String[][] f = new String[][] { { null }, null };
	
	@Value
	public Comp comp = Comp.LESS;
	
	@Test
	public void test()
	{
		this.a		= 1337;
		this.b		= Double.NaN;
		this.c		= false;
		this.d		= new String[] { "Hello, ", "World!" };
		this.e[0]	= "Test123";
		this.f[1]	= new String[] {};
		
		validate();
	}
	
	@Override
	public String toString()
	{
		return toString(this);
	}
	
	@Override
	public int hashCode()
	{
		return hashCode(this);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return equals(this, obj);
	}
}
