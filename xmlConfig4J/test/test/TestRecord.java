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
	
	public static record Record(CharSequence name, int bla)
	{}
}
