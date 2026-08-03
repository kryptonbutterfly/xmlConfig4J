package kryptonbutterfly.xmlConfig4J.annotations.handlers;

import kryptonbutterfly.xmlConfig4J.DataLocation;
import kryptonbutterfly.xmlConfig4J.annotations.BoundedFp;
import kryptonbutterfly.xmlConfig4J.exceptions.AnnotatedValidationException;
import kryptonbutterfly.xmlConfig4J.utils.ToStringUtils;

public final class BoundedFpHandler extends Handler<BoundedFp>
{
	public BoundedFpHandler()
	{
		super(BoundedFp.class);
	}
	
	public Object handle(DataLocation location, BoundedFp annotation, Object value)
		throws AnnotatedValidationException
	{
		validateBounded(location, annotation);
		switch (value)
		{
			case Float f -> {
				if (Float.isNaN(f))
				{
					handleNan(location, annotation);
					return f;
				}
				return (float) handleMax(location, annotation, handleMin(location, annotation, f));
			}
			case Double d -> {
				if (Double.isNaN(d))
				{
					handleNan(location, annotation);
					return d;
				}
				return handleMax(location, annotation, handleMin(location, annotation, d));
			}
			default -> throw new IllegalStateException(
				"Only fields of type float or double may be annotated with @%s!\n\t%s %s".formatted(
					getType().getSimpleName(),
					location,
					location.fieldDesc()));
		}
	}
	
	private void handleNan(DataLocation location, BoundedFp annotation)
	{
		if (!annotation.allowNaN())
			throw new AnnotatedValidationException(
				"%s is not allowed to be NaN!\n\t%s",
				location.fieldDesc(),
				location);
	}
	
	private double handleMin(DataLocation location, BoundedFp annotation, double value)
	{
		if (value >= annotation.minInclusive())
			return value;
		if (annotation.clamp())
			return annotation.minInclusive();
		
		throw new AnnotatedValidationException(
			"%s must be >= %s, but was %s\n\t%s",
			location.fieldDesc(),
			annotation.minInclusive(),
			value,
			location);
	}
	
	private double handleMax(DataLocation location, BoundedFp annotation, double value)
	{
		if (value <= annotation.maxInclusive())
			return value;
		if (annotation.clamp())
			return annotation.maxInclusive();
		
		throw new AnnotatedValidationException(
			"%s must be <= %s, but was %s\n\t%s",
			location.fieldDesc(),
			annotation.maxInclusive(),
			value,
			location);
	}
	
	private void validateBounded(DataLocation location, BoundedFp annotation)
	{
		if (annotation.minInclusive() > annotation.maxInclusive())
			throw new AnnotatedValidationException(
				"'minInclusive' must be <= 'maxInclusive', but was %s\n\t%s %s",
				ToStringUtils.annToSimpleString(annotation),
				location,
				location.fieldDesc());
	}
}
