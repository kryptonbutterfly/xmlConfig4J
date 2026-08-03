package kryptonbutterfly.xmlConfig4J.annotations.handlers;

import java.lang.annotation.Annotation;

import kryptonbutterfly.xmlConfig4J.DataLocation;
import kryptonbutterfly.xmlConfig4J.annotations.NonNull;
import kryptonbutterfly.xmlConfig4J.exceptions.AnnotatedValidationException;

public final class NonNullHandler<NonNullAnnotation extends Annotation> extends Handler<NonNullAnnotation>
{
	/**
	 * It is strongly discouraged to supply a general nonnull annotation here that
	 * is used throughout your project, since this will mark every field annotated
	 * with it for serialization. Doing so will prevent you from marking fields as
	 * nonull without also marking them for serialization.
	 * 
	 * @param annotationType
	 *            Your NonNull annotation or {@link NonNull}
	 */
	public NonNullHandler(Class<NonNullAnnotation> annotationType)
	{
		super(annotationType);
	}
	
	@Override
	public Object handle(DataLocation location, NonNullAnnotation annotation, Object value)
		throws IllegalStateException,
		AnnotatedValidationException
	{
		if (value != null)
			return value;
		throw new AnnotatedValidationException(
			"%s must not be null\n\t%s".formatted(
				location.fieldDesc(),
				location));
	}
}
