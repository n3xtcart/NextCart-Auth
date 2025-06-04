package it.nextre.corsojava.entity.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ManyToMany {
	Class<?> mapObject();
	String joinColumn();
	String joinTable();
	String supportTable() default "";
	String supportJoinColumn() default "";

}
