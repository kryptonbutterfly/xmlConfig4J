package test;

import org.junit.jupiter.api.Test;

import utils.Validator;

public final class TestRecord
{
	@Test
	public void test()
	{
		Validator.validate(new Record("Hello, Record!", -1337));
	}
	
	@Test
	public void testRepeating()
	{
		final var rec = new Record("Repeating!", 1234);
		Validator.validate(new Record[] { rec, rec });
	}
	
	public static record Record(CharSequence name, int bla)
	{}
}
