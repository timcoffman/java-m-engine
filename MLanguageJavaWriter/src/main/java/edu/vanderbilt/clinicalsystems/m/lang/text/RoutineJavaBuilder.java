package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import com.sun.codemodel.JCodeModel;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class RoutineJavaBuilder<T extends RoutineJavaBuilderContext> {

	public enum JavaMethodContents {
		STUB,
		EXECUTABLE,
		IMPLEMENTATION
	}
	
	private final T m_builderContext ;

	public RoutineJavaBuilder( T builderContext ) {
		m_builderContext = builderContext ;
	}
	
	public interface Builder<T> {
		void build( T location ) ;
	}

	protected static <T> Builder<T> buildInSequence( Builder<T> a, Builder<T> x ) {
		return (b)->{
			a.build(b) ;
			x.build(b) ; 
		} ;
	}

	public T context() { return m_builderContext; }
	protected RoutineJavaBuilderEnvironment env() { return context().env(); }

	protected JCodeModel codeModel() { return context().codeModel(); }
	
	protected Class<?> valueClass() { return env().valueClass(); }

	protected boolean endOfMethod(RoutineElement element) {
		if ( element instanceof Command ) {
			return endOfMethod( (Command)element ) ;
		} else if ( element instanceof Tag ) {
			return endOfMethod( (Tag)element ) ;
		} else {
			return false ;
		}
	}

	private boolean endOfMethod(Command command) {
		if ( CommandType.QUIT == command.commandType() && null == command.condition() )
			return true ;
		else if ( CommandType.GOTO == command.commandType() && null == command.condition() )
			return true ;
		else
			return false ;
	}
	
	private boolean endOfMethod(Tag tag) {
		if ( tag.parameterNames().iterator().hasNext() )
			return true ;
		else
			return false ;
	}
		
	protected void variableUsedAs( SymbolUsage symbolUsage, DirectVariableReference variable, Supplier<Optional<Representation>> expectedRepresentation ) {
		symbolUsage.usedAs( symbolForVariable(variable), expectedRepresentation);
		for ( Optional<VariableReference> parent = variable.parent() ; parent.isPresent() ; parent = parent.get().parent() )
			symbolUsage.usedAs( symbolForVariable((DirectVariableReference)parent.get()), Representation.NATIVE );
	}
	
	protected String symbolForVariable( DirectVariableReference variable ) {
		StringBuilder sb = new StringBuilder() ;
		sb.append( context().symbolForIdentifier(variable.variableName()) ) ;
		
		StreamSupport.stream(variable.keys().spliterator(),false)
			.forEach( (e)->{
				sb.append("[");
				sb.append(symbolForIndex(e));
				sb.append("]");
			} );
		
		return sb.toString() ;
	}
	
	private String symbolForIndex( Expression e ) {
		if ( e instanceof Constant )
			return ((Constant)e).value() ;
		else
			return "?" ;
	}
	
}