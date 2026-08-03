package kryptonbutterfly.xmlConfig4J.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD, RECORD_COMPONENT })
public @interface NonNull
{
	@InfoProperty
	String value() default "";
}
