package edu.vanderbilt.clinicalsystems.epic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.TYPE } )
@Retention(RetentionPolicy.CLASS)
public @interface EpicRoutine {
	
	public String value() default DEFAULT_NAME ;
	
	public static final String DEFAULT_NAME = "---" ;
}
