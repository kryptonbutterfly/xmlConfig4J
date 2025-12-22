package test;

import static utils.Validator.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public final class TestMap
{
	@Test
	public void test()
	{
		validate(new HashMap<>(Map.of(1, "Hello,", 2, "World!")));
	}
}
