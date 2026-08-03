package validation.valid;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Bounded;
import utils.GenericUtils;
import utils.Validator;

public final class BoundedInt implements Validator, GenericUtils
{
	@Bounded(minInclusive = 0x12345, maxInclusive = 0x23456)
	public int valid = 0x1FFFF;
	
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
