package kryptonbutterfly.xmlConfig4J.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD, RECORD_COMPONENT })
public @interface BoundedFp
{
	double minInclusive() default Double.NEGATIVE_INFINITY;
	
	double maxInclusive() default Double.POSITIVE_INFINITY;
	
	boolean allowNaN() default false;
	
	@InfoProperty
	String info() default "";
	
	/**
	 * If true values will be clamped, else an error is thrown.
	 */
	boolean clamp() default false;
}