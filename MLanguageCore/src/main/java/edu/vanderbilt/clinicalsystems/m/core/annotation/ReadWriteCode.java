package edu.vanderbilt.clinicalsystems.m.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType;

@Target( { ElementType.METHOD } )
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadWriteCode {

	ReadWriteCodeType value() ;
	
}
