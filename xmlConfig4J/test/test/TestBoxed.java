package test;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.GenericUtils;
import utils.Validator;

public final class TestBoxed implements Validator, GenericUtils
{
	@Value
	public Boolean a = null;
	
	@Value
	public Byte b = null;
	
	@Value
	public Character c = null;
	
	@Value
	public Short d = null;
	
	@Value
	public Integer e = null;
	
	@Value
	public Float f = null;
	
	@Value
	public Long g = null;
	
	@Value
	public Double h = null;
	
	@Test
	public void test()
	{
		a	= true;
		b	= 0x14;
		c	= 'C';
		d	= 0x0123;
		e	= 0xFFFF_FFFF;
		f	= 1.2345F;
		g	= 0xFFFF_FFFF_FFFF_FFFFL;
		h	= Math.PI;
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
