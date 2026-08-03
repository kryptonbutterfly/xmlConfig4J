package validation.setup;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import kryptonbutterfly.xmlConfig4J.BindingBuilder;

public final class Setup
{
	private static final Class<IllegalStateException> ise = IllegalStateException.class;
	
	@Test
	public void test()
	{
		final var builder = new BindingBuilder();
		
		assertThrowsExactly(ise, () -> builder.addIncludeAnnotation(MultipleInfoProps.class));
		assertThrowsExactly(ise, () -> builder.addIncludeAnnotation(InvalidInfoPropType.class));
		
		assertThrowsExactly(ise, () -> builder.addIncludeAnnotation(MissingRecordCompTarget.class));
		assertThrowsExactly(ise, () -> builder.addIncludeAnnotation(MissingFieldTarget.class));
		assertThrowsExactly(ise, () -> builder.addIncludeAnnotation(MissingTarget.class));
		
		assertThrowsExactly(ise, () -> builder.addIncludeAnnotation(InvalidRetentionClass.class));
		assertThrowsExactly(ise, () -> builder.addIncludeAnnotation(InvalidRetentionSource.class));
		assertThrowsExactly(ise, () -> builder.addIncludeAnnotation(MissingRetention.class));
		
	}
}
