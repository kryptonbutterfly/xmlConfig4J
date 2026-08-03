package validation.setup;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target({ FIELD, RECORD_COMPONENT })
public @interface InvalidRetentionClass
{}
