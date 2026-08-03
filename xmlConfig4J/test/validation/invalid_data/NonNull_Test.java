package validation.invalid_data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.NonNull;
import kryptonbutterfly.xmlConfig4J.exceptions.AnnotatedValidationException;
import utils.Validator;

public final class NonNull_Test
{
	@NonNull
	public String nonNull = null;
	
	@Test
	public void test()
	{
		assertThrowsExactly(AnnotatedValidationException.class, () -> Validator.c4j.toXml(this));
	}
}
