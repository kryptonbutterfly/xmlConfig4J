package validation.valid;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Bounded;
import utils.GenericUtils;
import utils.Validator;

public final class BoundedShort implements Validator, GenericUtils
{
	@Bounded(minInclusive = 1024, maxInclusive = 2048)
	public short valid = 1500;
	
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
