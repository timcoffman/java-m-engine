package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JVar;

class RoutineJavaBuilderContextImpl implements RoutineJavaBuilderContext {

	private final JCodeModel m_codeModel;
	private final RoutineJavaBuilderEnvironment m_environment ;
	
	public RoutineJavaBuilderContextImpl( JCodeModel codeModel ) {
		m_codeModel = codeModel ;
		m_environment = new RoutineJavaBuilderEnvironmentImpl() ;
	}
	
	@Override public JCodeModel codeModel() { return m_codeModel; }

	@Override public RoutineJavaBuilderEnvironment env() { return m_environment ; }

	@Override public String mainMethodName() { return "main" ; }

	@Override public String symbolForIdentifier( String variableName ) {
		String symbol = variableName.replaceAll("[^a-zA-Z0-9]", "_") ;
		if ( symbol.length() == 1 )
			symbol = symbol.substring(0,1).toLowerCase() ;
		else if ( symbol.length() > 1 )
			symbol = symbol.substring(0,1).toLowerCase() + symbol.substring(1) ;
		if ( RESERVED_WORDS.contains(symbol) )
			symbol = "_" + symbol ;
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