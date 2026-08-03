package validation.valid;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.NonNull;
import utils.GenericUtils;
import utils.Validator;

public final class NonNull_Test implements Validator, GenericUtils
{
	@NonNull
	public String nonNull = "Hello, World!";
	
	@Test
	public void test()
	{
		this.validate();
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
	
	@Override
	public String toString()
	{
		return toString(this);
	}
}
