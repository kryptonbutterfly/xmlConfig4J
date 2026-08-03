package kryptonbutterfly.xmlConfig4J.annotations.handlers;

import kryptonbutterfly.xmlConfig4J.DataLocation;
import kryptonbutterfly.xmlConfig4J.annotations.Bounded;
import kryptonbutterfly.xmlConfig4J.exceptions.AnnotatedValidationException;
import kryptonbutterfly.xmlConfig4J.utils.ToStringUtils;

public class BoundedHandler extends Handler<Bounded>
{
	public BoundedHandler()
	{
		super(Bounded.class);
	}
	
	@Override
	public Object handle(DataLocation location, Bounded annotation, Object value)
	{
		validateBounded(location, annotation);
		return switch (value)
		{
			case Byte b -> (byte) handleMax(location, annotation, handleMin(location, annotation, b));
			case Short s -> (short) handleMax(location, annotation, handleMin(location, annotation, s));
			case Integer i -> (int) handleMax(location, annotation, handleMin(location, annotation, i));
			case Long l -> (long) handleMax(location, annotation, handleMin(location, annotation, l));
			default -> throw new IllegalStateException(
				"Only fields of type byte, short, int or long may be annotated with %s!\n\t%s %s".formatted(
					getType().getSimpleName(),
					location,
					location.fieldDesc()));
		};
	}
	
	private long handleMin(DataLocation location, Bounded annotation, long value)
	{
		if (value >= annotation.minInclusive())
			return value;
		if (annotation.clamp())
			return annotation.minInclusive();
		
		throw new AnnotatedValidationException(
			"%s=%s must be >= %s\n\t%s",
			location.fieldDesc(),
			value,
			annotation.minInclusive(),
			location);
	}
	
	private long handleMax(DataLocation location, Bounded annotation, long value)
	{
		if (value <= annotation.maxInclusive())
			return value;
		if (annotation.clamp())
			return annotation.maxInclusive();
		
		throw new AnnotatedValidationException(
			"%s=%s must be <= %s\n\t%s",
			location.fieldDesc(),
			value,
			annotation.maxInclusive(),
			location);
	}
	
	private void validateBounded(DataLocation location, Bounded annotation)
	{
		if (annotation.minInclusive() > annotation.maxInclusive())
			throw new AnnotatedValidationException(
				"'minInclusive' must be <= 'maxInclusive', but was %s\n\t%s %s",
				ToStringUtils.annToSimpleString(annotation),
				location,
				location.fieldDesc());
	}
}
