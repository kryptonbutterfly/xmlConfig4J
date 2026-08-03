package validation.valid;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.BoundedFp;
import utils.GenericUtils;
import utils.Validator;

public final class BoundedDouble implements Validator, GenericUtils
{
	@BoundedFp(maxInclusive = -1)
	public double valid = -5;
	
	@BoundedFp(allowNaN = true)
	public double nan = Double.NaN;
	
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
