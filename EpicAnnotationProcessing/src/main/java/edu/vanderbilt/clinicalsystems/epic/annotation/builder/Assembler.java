package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Generator.Listener;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;


public abstract class Assembler<T> extends Builder {

	protected Assembler( RoutineTools builderTools ) { super(builderTools) ; }
	
	public abstract void assemble( T source, Block block, Listener delegate ) ;
}
