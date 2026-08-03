package validation.invalid_annotation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.annotations.BoundedFp;
import utils.Validator;

public final class BoundedFpType implements Validator
{
	@BoundedFp
	public String invalidType = "Invalid type for @BoundedFp annotation!";
	
	@Test
	public void test()
	{
		assertThrowsExactly(IllegalStateException.class, () -> Validator.c4j.toXml(this));
	}
}
