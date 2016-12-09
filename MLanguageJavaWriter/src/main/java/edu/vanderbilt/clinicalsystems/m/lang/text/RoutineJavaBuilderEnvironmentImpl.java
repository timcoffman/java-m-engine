package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import edu.vanderbilt.clinicalsystems.m.core.annotation.Command;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Function;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Library;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeCommand;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeFunction;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeValue;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Operator;
import edu.vanderbilt.clinicalsystems.m.core.annotation.ReadWriteCode;
import edu.vanderbilt.clinicalsystems.m.core.annotation.Variable;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeCommandType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeFunctionType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.NativeValueTypes;
import edu.vanderbilt.clinicalsystems.m.core.annotation.support.ReadWriteCodeType;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction;
import edu.vanderbilt.clinicalsystems.m.lang.BuiltinVariable;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.Compatibility;
import edu.vanderbilt.clinicalsystems.m.lang.OperatorType;

class RoutineJavaBuilderEnvironmentImpl implements RoutineJavaBuilderEnvironment {
	
	private final EnumSet<Compatibility> m_compatibility = EnumSet.of( Compatibility.ANSI_1995_X11_1 ) ;
	private final List<Resolver> m_resolvers = new ArrayList<Resolver>() ;
	
	private Class<?> m_valueClass ;
	private Map<String,Class<?>> m_libraries = new HashMap<String,Class<?>>();
	private Map<String,Method> m_methods = new HashMap<String,Method>();

	
	@Override public Class<?> valueClass() { return m_valueClass; }

	private <T extends Annotation> Optional<Method> methodFor(Class<?> libraryClass, Class<T> annotationClass, BiPredicate<T,Method> filter ) {
		for ( Method method : libraryClass.getDeclaredMethods() ) {
			T annotation = method.getAnnotation( annotationClass );
			if ( null != annotation && filter.test(annotation,method) )
				return Optional.of(method);
		}
		return Optional.empty() ;
	}
	
	@Override public Method methodFor(NativeValueTypes nativeValueType) {
		return methodFor(
				m_valueClass,
				NativeValue.class,
				(a,m)->a.value().equals(nativeValueType)
			).orElseThrow(
				()->new IllegalArgumentException( "\"" + nativeValueType + "\" not present on " + m_valueClass.getName() )
			) ;
	}
	
	@Override public Method methodFor(NativeFunctionType nativeFunctionType) {
		return methodFor(
				m_valueClass,
				NativeFunction.class,
				(a,m)->a.value().equals(nativeFunctionType)
			).orElseThrow(
				()->new IllegalArgumentException( "\"" + nativeFunctionType + "\" not present on " + m_valueClass.getName() )
			) ;
	}
	
	@Override public Method methodFor(NativeCommandType nativeCommandType) {
		return methodFor(
				m_valueClass,
				NativeCommand.class,
				(a,m)->a.value().equals(nativeCommandType)
			).orElseThrow(
				()->new IllegalArgumentException( "\"" + nativeCommandType + "\" not present on " + m_valueClass.getName() )
			) ;
	}

	private <T extends Annotation> Optional<Method> methodFor(Class<T> annotationClass, BiPredicate<T,Method> filter ) {
		return m_libraries.values().stream()
			.map( (lc)->methodFor(lc,annotationClass,filter))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst()
			;
	}
	
	@Override
	public Method methodFor(ReadWriteCodeType readWriteCodeType) {
		return methodFor(
				ReadWriteCode.class,
				(a,m)->a.value().equals(readWriteCodeType)
			).orElseThrow(
				()->new IllegalArgumentException( "method for read/write code \"" + readWriteCodeType + "\" not present in a library" )
			) ;
	}

	private static boolean numberOfParametersMatches( Integer numberOfParameters, Method method ) {
		if ( null == numberOfParameters )
			return true ;
		int actualNumberOfParameters = method.getParameterCount();
		if ( numberOfParameters == actualNumberOfParameters )
			return true ;
		if ( method.isVarArgs() && numberOfParameters >= actualNumberOfParameters-1 )
			return true ;
		return false ;
	}
	
	@Override public Method methodFor(BuiltinFunction builtinFunction,Integer numberOfParameters, boolean forAssignment) {
		return methodFor(
				Function.class,
				(a,m)->a.value().equals(builtinFunction)
					&& a.assignment() == forAssignment
					&& numberOfParametersMatches(numberOfParameters,m)
			).orElseThrow(
				()->new IllegalArgumentException( "method for builtin function \"" + builtinFunction + "\" not present in a library" )
			) ;
	}
	
	@Override public Method methodFor(BuiltinVariable builtinVariable) {
		return methodFor(
				Variable.class,
				(a,m)->a.value().equals(builtinVariable)
			).orElseThrow(
				()->new IllegalArgumentException( "method for variable \"" + builtinVariable + "\" not present in a library" )
			) ;
	}
	
	@Override public Method methodFor(CommandType commandType) {
		return methodFor(
				Command.class,
				(a,m)->a.value().equals(commandType)
			).orElseThrow(
				()->new IllegalArgumentException( "method for command \"" + commandType + "\" not present in a library" )
			) ;
	}
	
	@Override public Method methodFor(OperatorType operatorType) {
		return methodFor(
				Operator.class,
				(a,m)->a.value().equals(operatorType)
			).orElseThrow(
				()->new IllegalArgumentException( "method for operator \"" + operatorType + "\" not present in a library" )
			) ;
	}
	
	@Override public Class<?> classForRoutine( String routineName ) {
		return m_libraries.get( routineName ) ;
	}
	
	@Override public Method methodFor( String routineName, String tagName ) {
		return m_methods.get( tagName ) ;
	}

	@Override public RoutineJavaBuilderEnvironment additionalCompatibility( Compatibility additionalCompatibility) {
		m_compatibility.add(additionalCompatibility) ;
		return this ;
	}
	
	@Override public RoutineJavaBuilderEnvironmentImpl use( Resolver resolver ) {
		m_resolvers.add( resolver ) ;
		return this ;
	}
	
	@Override public RoutineJavaBuilderEnvironmentImpl use( Class<?> environmentClass ) {
		return this
			.useAnnotated( environmentClass, NativeType.class, this::useNativeType )
			.useAnnotated( environmentClass, Library.class, this::useLibrary )
			.useResolvers( environmentClass )
			;
	}

	private <T extends Annotation> RoutineJavaBuilderEnvironmentImpl useAnnotated(Class<?> environmentClass, Class<T> annotationType, BiConsumer<Class<?>,T> consumer ) {
		T annotation = environmentClass.getAnnotation( annotationType ) ;
		
		if ( null != annotation )
			consumer.accept( environmentClass, annotation) ;
		
		return this ;
	}

	private RoutineJavaBuilderEnvironmentImpl useResolvers( Class<?> environmentClass ) {
		for (Resolver resolver : m_resolvers) {
			String name = resolver.namedLibrary(environmentClass) ;
			if ( null != name ) {
				useLibrary(environmentClass, name);
			}
		}
		return this ;
	}
	
	private RoutineJavaBuilderEnvironmentImpl useResolvers( Method method ) {
		for (Resolver resolver : m_resolvers) {
			String name = resolver.namedMethod(method) ;
			if ( null != name ) {
				useMethod(method, name);
			}
		}
		return this ;
	}
	

	private void useNativeType(Class<?> environmentClass, NativeType valueTypeAnnotation ) {
		m_valueClass = environmentClass ;
	}
	
	private void useMethod(Method method, String methodName ) {
		m_methods.put( methodName, method ) ;
	}
	
	private void useLibrary(Class<?> environmentClass, Library libraryAnnotation ) {
		String name = Library.DEFAULT_NAME.equals(libraryAnnotation.value()) ? environmentClass.getSimpleName() : libraryAnnotation.value() ;
		useLibrary(environmentClass, name);
	}
	
	private void useLibrary(Class<?> environmentClass, String libraryName ) {
		m_libraries.put( libraryName, environmentClass ) ;
		
		for (Method method : environmentClass.getMethods())
			this
				.useAnnotated( method,        Command.class, this::useCommand )
				.useAnnotated( method,       Function.class, this::useFunction )
				.useAnnotated( method,       Operator.class, this::useOperator )
				.useAnnotated( method,       Variable.class, this::useVariable )
				.useAnnotated( method,  NativeCommand.class, this::useNativeCommand )
				.useAnnotated( method, NativeFunction.class, this::useNativeFunction )
				.useAnnotated( method,    NativeValue.class, this::useNativeValue )
				.useResolvers( method )
				;
	}

	private <T extends Annotation> RoutineJavaBuilderEnvironmentImpl useAnnotated(Method method, Class<T> annotationType, BiConsumer<Method,T> consumer ) {
		T annotation = method.getAnnotation( annotationType ) ;
		
		if ( null != annotation )
			consumer.accept( method, annotation) ;
		
		return this ;
	}
	
	private void useCommand( Method method, Command commandAnnotation ) {
		
	}
	
	private void useFunction( Method method, Function functionAnnotation ) {
		
	}
	
	private void useOperator( Method method, Operator functionAnnotation ) {
		
	}
	
	private void useVariable( Method method, Variable variableAnnotation ) {
		
	}
	
	private void useNativeCommand( Method method, NativeCommand nativeCommandAnnotation ) {
		
	}
	
	private void useNativeFunction( Method method, NativeFunction functionAnnotation ) {
		
	}
	
	private void useNativeValue( Method method, NativeValue nativeValueAnnotation ) {
		
	}
	
}