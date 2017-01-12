package edu.vanderbilt.clinicalsystems.m.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.METHOD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface RoutineTag {
	
	public String value() default DEFAULT_NAME ;
	
	public static final String DEFAULT_NAME = "---" ;
}
