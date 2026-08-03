package kryptonbutterfly.xmlConfig4J.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * Add this to the property of a custom annotation that stores info text. Add
 * this at most to one property per annotation!
 */
public @interface InfoProperty
{}
