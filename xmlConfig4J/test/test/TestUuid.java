package test;

import static utils.Validator.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public final class TestUuid
{
	@Test
	public void test()
	{
		validate(UUID.randomUUID());
	}
}
