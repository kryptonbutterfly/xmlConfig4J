package validation.invalid_data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.BoundedFp;
import kryptonbutterfly.xmlConfig4J.exceptions.AnnotatedValidationException;
import utils.Validator;

public final class BoundedDoubleNan
{
	@BoundedFp
	public double invalid = Double.NaN;
	
	@Test
	public void test()
	{
		assertThrowsExactly(AnnotatedValidationException.class, () -> Validator.c4j.toXml(this));
	}
}
