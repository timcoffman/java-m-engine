package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.lang.ref.Reference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

import edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeValueTypes;

class RoutineJavaBuilderContextImpl implements RoutineJavaBuilderContext {

	private final JCodeModel m_codeModel;
	private final RoutineJavaBuilderEnvironment m_environment ;
	private JExpression m_nullExpr ;
	private final Set<Reference<EventListener>> m_listeners = new HashSet<Reference<EventListener>>();
	
	public RoutineJavaBuilderContextImpl( JCodeModel codeModel ) {
		this( codeModel, new RoutineJavaBuilderEnvironmentImpl() ) ;
	}
	
	public RoutineJavaBuilderContextImpl( JCodeModel codeModel, RoutineJavaBuilderEnvironment environment ) {
		m_codeModel = codeModel ;
		m_environment = environment ;
	}

	@Override public void listen( Reference<EventListener> listener ) { m_listeners.add( listener ) ; }
	@Override public void remove( EventListener listener ) { m_listeners.removeIf( (r)->listener == r.get() || null == r.get() ) ; }
	@Override public void forEachListener( Consumer<EventListener> action ) {
		Iterator<Reference<EventListener>> i = m_listeners.iterator() ;
		while ( i.hasNext() ) {
			EventListener listener = i.next().get() ;
			if ( null == listener )
				i.remove(); 
			else
				action.accept(listener);
		}
	}
	
	@Override public JCodeModel codeModel() { return m_codeModel; }

	@Override public JType typeFor(Representation representation) {
		switch ( representation ) {
		case VOID:
			return m_codeModel.VOID ;
		case NUMERIC:
		case DECIMAL:
			return m_codeModel.DOUBLE ;
		case INTEGER:
			return m_codeModel.INT ;
		case BOOLEAN:
			return m_codeModel.BOOLEAN ;
		case STRING:
		case NATIVE:
		default:
			return m_codeModel.ref( m_environment.typeFor(representation) ) ;
		}
	}

	@Override public JExpression initialValueFor(Representation representation) {
		switch ( representation ) {
		case VOID:
			return null ;
		case NUMERIC:
		case DECIMAL:
			return JExpr.lit(0D) ;
		case INTEGER:
			return JExpr.lit(0) ;
		case BOOLEAN:
			return JExpr.lit(false) ;
		case STRING:
			return JExpr.lit("") ;
		case NATIVE:
		default:
			if ( null == m_nullExpr ) {
				Method initialValueMethod = m_environment.methodFor( NativeValueTypes.INITIAL_VALUE ) ;
				m_nullExpr = m_codeModel.ref(initialValueMethod.getDeclaringClass()).staticInvoke(initialValueMethod.getName());
			}
			return m_nullExpr ;
		}
	}

	@Override public RoutineJavaBuilderEnvironment env() { return m_environment ; }

	@Override public String mainMethodName() { return "main" ; }

	@Override public RoutineJavaBuilderClassContext classContext(String outerClassName) {
		return new RoutineJavaBuilderClassContextImpl( m_codeModel, outerClassName, m_environment ) ;
	}
	
	@Override public String symbolForIdentifier( String variableName ) {
		if ( null == variableName )
			return null ;
		
		String symbol = variableName
				.replaceAll("[%]", "\\$")
				.replaceAll("[^$a-zA-Z0-9]", "_")
				;
		if ( symbol.length() == 1 )
			symbol = symbol.substring(0,1).toLowerCase() ;
		else if ( symbol.length() > 1 )
			symbol = symbol.substring(0,1).toLowerCase() + symbol.substring(1) ;
		if ( RESERVED_WORDS.contains(symbol) )
			symbol = "_" + symbol + "_" ;
		return symbol ;
	}

	private static final Set<String> RESERVED_WORDS = new HashSet<String>( Arrays.asList(
		"abstract", "continue", "for",        "new",       "switch",
		"assert",   "default",  "goto",       "package",   "synchronized",
		"boolean",  "do",       "if",         "private",   "this",
		"break",    "double",   "implements", "protected", "throw",
		"byte",     "else",     "import",     "public",    "throws",
		"case",     "enum",     "instanceof", "return",    "transient",
		"catch",    "extends",  "int",        "short",     "try",
		"char",     "final",    "interface",  "static",    "void",
		"class",    "finally",  "long",       "strictfp",  "volatile",
		"const",    "float",    "native",     "super",     "while"
		)) ;
	
	@Override public boolean isValueType(JavaExpression<?> source) {
		return source.type(env()).equals( codeModel().ref(env().valueClass()) );
	}

	@Override public boolean isValueType(JExpression source) {
		if ( source instanceof JVar) {
			return ((JVar)source).type().equals( codeModel().ref(env().valueClass()) );
//		} else if ( source instanceof JInvocation) {
//				return ((JInvocation)source).().equals( codeModel().ref(env().valueClass()) );
		} else {
			return false ;
		}
	}

}