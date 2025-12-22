package test;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Value;
import utils.GenericUtils;
import utils.Validator;

public final class TestEnum implements Validator, GenericUtils
{
	@Value
	public DummyEnum dummy1 = DummyEnum.VAL1;
	
	@Value
	public DummyEnum dummy2 = null;
	
	@Value
	public DummyEnum dummy3 = null;
	
	@Value
	public Enum<?> dummy4 = null;
	
	@Test
	public void test()
	{
		this.dummy1	= null;
		this.dummy2	= DummyEnum.VAL1;
		this.dummy3	= DummyEnum.VAL2;
		this.dummy4	= DummyEnum.VAL1;
		
		validate();
	}
	
	@Test
	public void testEnumValues()
	{
		Validator.validate(DummyEnum.VAL1);
		Validator.validate(DummyEnum.VAL2);
	}
	
	public static enum DummyEnum
	{
		VAL1,
		VAL2("Hello, Enum");
		
		public final String text;
		
		DummyEnum(String text)
		{
			this.text = text;
		}
		
		DummyEnum()
		{
			this.text = this.name();
		}
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
