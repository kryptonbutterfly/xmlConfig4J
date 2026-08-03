package kryptonbutterfly.xmlConfig4J.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD, RECORD_COMPONENT })
public @interface Bounded
{
	long minInclusive() default Long.MIN_VALUE;
	
	long maxInclusive() default Long.MAX_VALUE;
	
	@InfoProperty
	String info() default "";
	
	/**
	 * If true values will be clamped, else an error is thrown.
	 */
	boolean clamp() default false;
}
