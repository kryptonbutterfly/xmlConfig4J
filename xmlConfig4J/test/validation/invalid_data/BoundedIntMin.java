package validation.invalid_data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Bounded;
import kryptonbutterfly.xmlConfig4J.exceptions.AnnotatedValidationException;
import utils.GenericUtils;
import utils.Validator;

public final class BoundedIntMin implements GenericUtils, Validator
{
	@Bounded(minInclusive = -10)
	public int invalid = -20;
	
	@Test
	public void test()
	{
		assertThrowsExactly(AnnotatedValidationException.class, () -> Validator.c4j.toXml(this));
	}
}
