package it.nextre.corsojava.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Attribute {
	String fieldName() default "";
	String colName() default "";
	String type() default "string";
	Class<?> className() default String.class;
	Class<?> colClass() default String.class;
	boolean auto() default false;

}
