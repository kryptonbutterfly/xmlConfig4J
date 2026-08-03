package validation.valid;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.BoundedFp;
import utils.GenericUtils;
import utils.Validator;

public final class BoundedFloat implements Validator, GenericUtils
{
	@BoundedFp(minInclusive = 0, maxInclusive = 1)
	public float valid = 0.5F;
	
	@BoundedFp(allowNaN = true)
	public float nan = Float.NaN;
	
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
