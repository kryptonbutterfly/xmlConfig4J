package test;

import static utils.Validator.*;

import java.awt.Color;

import org.junit.jupiter.api.Test;

public final class TestColor
{
	@Test
	public void test()
	{
		validate(new Color(0x80, 0xFF, 0x00));
	}
}
