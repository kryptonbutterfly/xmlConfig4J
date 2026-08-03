package test;

import static utils.Validator.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

public final class TestSet
{
	@Test
	public void test()
	{
		validate(new HashSet<>(Set.of("1", "s", "k", "SAD", "fdsgdfgdfg")));
	}
}
