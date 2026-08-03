package kryptonbutterfly.xmlConfig4J.annotations.handlers;

import java.lang.annotation.Annotation;

import kryptonbutterfly.xmlConfig4J.DataLocation;
import kryptonbutterfly.xmlConfig4J.exceptions.AnnotatedValidationException;

public abstract class Handler<A extends Annotation>
{
	private final Class<A> annotationType;
	
	/**
	 * @param annotationType
	 *            The type of the specific annotation this handler is for.
	 */
	protected Handler(Class<A> annotationType)
	{
		this.annotationType = annotationType;
	}
	
	/**
	 * @return The type of the specific annotation this Handler is for.
	 */
	public final Class<A> getType()
	{
		return annotationType;
	}
	
	/**
	 * @deprecated Do not call this function, call
	 *             {@link Handler#handle(Annotation, Object)} instead!
	 * @param location
	 * @param annotation
	 * @param value
	 * @throws AnnotatedValidationException
	 */
	@SuppressWarnings("unchecked")
	@Deprecated(forRemoval = false)
	public final Object handleRaw(DataLocation location, Annotation annotation, Object value)
		throws AnnotatedValidationException,
		IllegalStateException
	{
		return handle(location, (A) annotation, value);
	}
	
	/**
	 * <p>
	 * <b>Implementation contract:</b>
	 * <ul>
	 * <li>Verify that {@code value} is of an expected type, otherwise throw an
	 * {@link IllegalStateException}.</li>
	 * <li>In all other exception cases throw an
	 * {@link AnnotatedValidationException} instead!</li>
	 * </ul>
	 * 
	 * @param location
	 * @param annotation
	 * @param value
	 * @throws IllegalStateException
	 *             if {@code value} has an unexpected type
	 * @throws AnnotatedValidationException
	 */
	public abstract Object handle(DataLocation location, A annotation, Object value)
		throws IllegalStateException,
		AnnotatedValidationException;
}
