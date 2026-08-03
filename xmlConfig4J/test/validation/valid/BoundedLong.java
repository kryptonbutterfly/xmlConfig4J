package validation.valid;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Bounded;
import utils.GenericUtils;
import utils.Validator;

public final class BoundedLong implements Validator, GenericUtils
{
	@Bounded(minInclusive = 0x1_0000_0000L)
	public long valid = 0x2_0000_0000L;
	
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
