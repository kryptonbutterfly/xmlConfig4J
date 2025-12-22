package test;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.GenericUtils;
import utils.Validator;

public final class TestPrimitive implements Validator, GenericUtils
{
	@Value
	public boolean a = false;
	
	@Value
	public byte b = 0;
	
	@Value
	public char c = '0';
	
	@Value
	public short d = 0;
	
	@Value
	public int e = 0;
	
	@Value
	public float f = 0F;
	
	@Value
	public long g = 0L;
	
	@Value
	public double h = 0D;
	
	@Test
	public void test()
	{
		a	= true;
		b	= 123;
		c	= 'A';
		d	= 1234;
		e	= 12345678;
		f	= 1.234F;
		g	= 1234567890L;
		h	= 1234.5678;
		
		validate();
	}
	
	@Override
	public String toString()
	{
		return toString(this);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return equals(this, obj);
	}
	
	@Override
	public int hashCode()
	{
		return hashCode(this);
	}
}
