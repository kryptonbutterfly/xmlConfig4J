package validation.valid;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Bounded;
import utils.GenericUtils;
import utils.Validator;

public final class BoundedByte implements Validator, GenericUtils
{
	@Bounded(minInclusive = 0, maxInclusive = 20)
	public byte valid = 10;
	
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
	public String toString()
	{
		return toString(this);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return equals(this, obj);
	}
}
