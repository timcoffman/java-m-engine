package edu.vanderbilt.clinicalsystems.m.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.TYPE } )
@Retention(RetentionPolicy.RUNTIME)
public @interface Library {

	public String value() default DEFAULT_NAME;
	
	public static final String DEFAULT_NAME = "---" ;
	
}
