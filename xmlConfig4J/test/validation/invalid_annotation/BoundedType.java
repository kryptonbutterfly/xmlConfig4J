package validation.invalid_annotation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.Bounded;
import utils.Validator;

public final class BoundedType implements Validator
{
	@Bounded
	public String invalidType = "Invalid type for @Bounded annotation!";
	
	@Test
	public void test()
	{
		assertThrowsExactly(IllegalStateException.class, () -> Validator.c4j.toXml(this));
	}
}
