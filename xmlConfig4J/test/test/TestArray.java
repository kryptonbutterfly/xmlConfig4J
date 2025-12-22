package test;

import org.junit.jupiter.api.Test;

import utils.Validator;

public final class TestArray
{
	@Test
	public void testBoolean()
	{
		Validator.validate(new boolean[] { true, false, true });
	}
	
	@Test
	public void testByte()
	{
		Validator.validate(
			new byte[] {
				0x00,
				0x11,
				0x22,
				0x33,
				0x44,
				0x55,
				0x66,
				0x77,
				-0x78,
				-0x67,
				-0x56,
				-0x45,
				-0x34,
				-0x23,
				-0x12,
				-0x01 });
	}
	
	@Test
	public void testChar()
	{
		Validator.validate("Hello, ░".toCharArray());
	}
	
	@Test
	public void testShort()
	{
		Validator.validate(new short[] { 0x0000, -0x0001, 12345 });
	}
	
	@Test
	public void testInt()
	{
		Validator.validate(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, -1 });
	}
	
	@Test
	public void testFloat()
	{
		Validator.validate(new float[] { 0, 1, 2, 3, 4, 5, 6, 7, 0.1F, 0.2F, 0.3F, 0.4F });
	}
	
	@Test
	public void testLong()
	{
		
		Validator.validate(new long[] { 0, 2, 4, 6, 8, 10, -1 });
	}
	
	@Test
	public void testDouble()
	{
		Validator.validate(new double[] { 0.0, 100.0, 1.25, Math.PI, Math.E });
	}
	
	@Test
	public void testNested()
	{
		
		Validator.validate(new char[][] { "Hello, ▓".toCharArray(), null });
	}
	
	@Test
	public void testNested2()
	{
		Validator.validate(
			new Object[] {
				new String[] { "Hello,", "Nested2!" },
				new Object[] { "Hello,".toCharArray(), "Nested2!".toCharArray() },
				"Not nested."
			});
	}
}
