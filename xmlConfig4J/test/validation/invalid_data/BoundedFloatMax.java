package validation.invalid_data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.BoundedFp;
import kryptonbutterfly.xmlConfig4J.exceptions.AnnotatedValidationException;
import utils.Validator;

public final class BoundedFloatMax
{
	@BoundedFp(minInclusive = -1, maxInclusive = 1)
	public float invalid = 2;
	
	@Test
	public void test()
	{
		assertThrowsExactly(AnnotatedValidationException.class, () -> Validator.c4j.toXml(this));
	}
}
