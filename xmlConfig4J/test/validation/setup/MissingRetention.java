package validation.setup;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Target;

@Target({ FIELD, RECORD_COMPONENT })
public @interface MissingRetention
{}
