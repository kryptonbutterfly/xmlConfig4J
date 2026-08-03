package validation.setup;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import kryptonbutterfly.xmlConfig4J.annotations.InfoProperty;

@Retention(RUNTIME)
@Target({ FIELD, RECORD_COMPONENT })
public @interface InvalidInfoPropType
{
	@InfoProperty
	int info() default 0;
}
