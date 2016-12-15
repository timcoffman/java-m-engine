package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

public class JavaInvocation extends JavaExpression<JInvocation> {

	private final RoutineJavaBuilderContext m_context ;
	private final List<Representation> m_parameterRepresentations = new ArrayList<Representation>() ;
	private final Representation m_additionalParametersRepresentation ;
	private final List<JavaExpression<?>> m_arguments = new ArrayList<JavaExpression<?>>() ;
	
	public JavaInvocation( JInvocation expr, Representation returningRepresentation, List<Representation> parameterRepresentations, Representation additionalParametersRepresentation, RoutineJavaBuilderContext context ) {
		super( expr, returningRepresentation.supplier() ) ;
		m_parameterRepresentations.addAll( parameterRepresentations ) ;
		m_additionalParametersRepresentation = additionalParametersRepresentation ;
		m_context = context ;
	}
	
	public JavaInvocation appendArgument( Function<Representation,JavaExpression<?>> f ) {
		int position = m_arguments.size() ;
		Representation rep ;
		if ( position < m_parameterRepresentations.size() ) {
			rep = m_parameterRepresentations.get(position) ;
		} else if ( null != m_additionalParametersRepresentation ) {
			rep = m_additionalParametersRepresentation ;
		} else {
			throw new IllegalArgumentException( "attempt to add argument to invocation that only accepts " + m_parameterRepresentations.size() + " parameters") ;
		}
		JavaExpression<?> arg = f.apply(rep);
		m_arguments.add( arg ) ;
		expr().arg( arg.expr() ) ;
		return this ;
	}
	
	public JavaInvocation appendArgument( JavaExpression<?> e ) {
		return appendArgument( (r)->e.convert(r,m_context) ) ;
	}

	public interface JavaInvocationBuilder {
		JavaInvocationBuilder on( Class<?> declaringClass ) ;
		JavaInvocationBuilder on( JavaExpression<?> instance ) ;
		JavaInvocationBuilder invoke( String methodName ) ;
		JavaInvocationBuilder invoke( Method method ) ;
		JavaInvocationBuilder accepting( int argumentCount ) ;
		JavaInvocationBuilder accepting( Class<?> ... parameterTypes ) ;
		JavaInvocationBuilder acceptingNothing() ;
		JavaInvocationBuilder supplying( JavaExpression<?> ... arguments ) ;
		JavaInvocationBuilder supplying( List<JavaExpression<?>> arguments ) ;
		JavaInvocation build() ;
		JavaInvocation build( JBlock block ) ;
	}

	public static JavaInvocationBuilder builder(final RoutineJavaBuilderContext context) {
		return new JavaInvocationBuilderImpl(context) ;
	}
	
	private static List<Method> findMethods( Class<?> declaringClass, String methodName ) {
		List<Method> methods = Arrays.stream(declaringClass.getMethods())
			.filter( (m)->methodName.equals(m.getName()) )
			.collect( Collectors.toList() );
		if ( methods.isEmpty() )
			throw new IllegalArgumentException("no method named \"" + methodName + "\" found in " + declaringClass ) ;
		return methods ;
	}
	
	private static Method findMethod( Class<?> declaringClass, String methodName, List<Class<?>> parameterTypes ) {
		List<Method> methods = findMethods(declaringClass, methodName) ;
		if ( methods.size() == 1 )
			return methods.get(0) ;
		
		int numberOfParameters = parameterTypes.size() ;
		methods = methods.stream()
				.filter( (m)->numberOfParameters==m.getParameterCount())
				.collect( Collectors.toList() )
				;
		
		if ( methods.isEmpty() )
			throw new IllegalArgumentException("no method named \"" + methodName + "\" found in " + declaringClass + " accepting " + numberOfParameters + " parameters") ;
		if ( methods.size() == 1 )
			return methods.get(0) ;

		methods = methods.stream()
				.filter( (m)->parameterTypes.equals( Arrays.asList(m.getParameterTypes() ) ))
				.collect( Collectors.toList() )
				;
		
		if ( methods.isEmpty() )
			throw new IllegalArgumentException("no method named \"" + methodName + "\" found in " + declaringClass + " accepting " + parameterTypes ) ;
		if ( methods.size() == 1 )
			return methods.get(0) ;
		
		throw new IllegalArgumentException("more than one method named \"" + methodName + "\" found in " + declaringClass + " accepting " + parameterTypes ) ;
	}
	
	private static Method findOnlyMethod( Class<?> declaringClass, String methodName, Integer numberOfParameters ) {
		List<Method> methods = findMethods(declaringClass, methodName) ;
		if ( methods.size() == 1 )
			return methods.get(0) ;

		if ( null == numberOfParameters )
			throw new IllegalArgumentException("more than one method named \"" + methodName + "\" found in " + declaringClass ) ;
		
		methods = methods.stream()
				.filter( (m)->numberOfParameters==m.getParameterCount())
				.collect( Collectors.toList() )
				;
		
		if ( methods.isEmpty() )
			throw new IllegalArgumentException("no method named \"" + methodName + "\" found in " + declaringClass + " accepting " + numberOfParameters + " parameters") ;
		if ( methods.size() == 1 )
			return methods.get(0) ;
		
		throw new IllegalArgumentException("more than one method named \"" + methodName + "\" found in " + declaringClass + " accepting " + numberOfParameters + " parameters" ) ;
	}
	
	private static final class JavaInvocationBuilderImpl implements JavaInvocationBuilder {
		private final RoutineJavaBuilderContext context;
		private String m_methodName ;
		private Class<?> m_declaringClass = null ;
		private Integer m_numberOfParameters = null ;
		private List<Class<?>> m_parameterTypes = null ;
		private JavaExpression<?> m_instance = null ;
		private List<JavaExpression<?>> m_arguments = null ;

		private JavaInvocationBuilderImpl(
				RoutineJavaBuilderContext context) {
			this.context = context;
		}
		
		@Override public JavaInvocationBuilder invoke( Method method ) {
			if ( null != m_methodName )
				throw new IllegalStateException( "method name already specified") ;
			m_methodName = method.getName() ;
			if ( null == m_declaringClass )
				m_declaringClass = method.getDeclaringClass() ;
			return this ;
			}

		@Override public JavaInvocationBuilder invoke( String methodName ) {
			if ( null != m_methodName )
				throw new IllegalStateException( "method name already specified") ;
			m_methodName = methodName ;
			return this ;
		}

		@Override public JavaInvocationBuilder on( Class<?> declaringClass ) {
			if ( null != m_declaringClass )
				throw new IllegalStateException( "declaring class already specified") ;
			m_declaringClass = declaringClass ;
			return this ;
		}

		@Override public JavaInvocationBuilder on( JavaExpression<?> instance ) {
			if ( null != m_instance )
				throw new IllegalStateException( "target instance already specified") ;
			m_instance = instance ;
			if ( null == m_declaringClass )
				m_declaringClass = instance.type(context.env()) ;
			return this ;
		}

		@Override public JavaInvocationBuilder acceptingNothing() {
			if ( null != m_parameterTypes )
				throw new IllegalStateException( "parameter types already specified") ;
			m_parameterTypes = Collections.emptyList() ;
			m_numberOfParameters = 0;
			return this ;
		}

		@Override public JavaInvocationBuilder accepting( int numberOfParameters ) {
			if ( null != m_numberOfParameters )
				throw new IllegalStateException( "number of parameters already specified") ;
			m_numberOfParameters = numberOfParameters ;
			return this ;
		}
		
		@Override public JavaInvocationBuilder accepting( Class<?> ... parameterTypes ) {
			if ( null != m_parameterTypes )
				throw new IllegalStateException( "parameter types already specified") ;
			if ( null != m_numberOfParameters && m_numberOfParameters != parameterTypes.length )
				throw new IllegalStateException( "number of parameters already specified with a different value") ;
			m_parameterTypes = Arrays.asList(parameterTypes) ;
			m_numberOfParameters = m_parameterTypes.size();
			return this ;
		}

		@Override public JavaInvocationBuilder supplying( JavaExpression<?> ... arguments ) {
			return supplying( Arrays.asList( arguments ) );
		}
		
		@Override public JavaInvocationBuilder supplying( List<JavaExpression<?>> arguments ) {
			if ( null != m_arguments )
				throw new IllegalStateException( "arguments already specified") ;
			m_arguments = new ArrayList<JavaExpression<?>>( arguments ) ;
			if ( null == m_parameterTypes ) {
				if ( null != m_numberOfParameters && m_numberOfParameters != arguments.size() )
					throw new IllegalStateException( "number of parameters already specified with a different value") ;
				m_parameterTypes = arguments.stream()
						.map( (e)->e.type(context.env()) )
						.collect( Collectors.toList() )
						;
				m_numberOfParameters = m_parameterTypes.size() ;
			}
			return this ;
		}

		@Override public JavaInvocation build() {
			if ( null == m_declaringClass )
				throw new IllegalArgumentException("missing class") ;
			if ( null == m_methodName )
				throw new IllegalArgumentException("missing method name") ;
			
			Method method ;
			if ( null == m_parameterTypes )
				method = findOnlyMethod(m_declaringClass, m_methodName, m_numberOfParameters) ;
			else
				method = findMethod(m_declaringClass, m_methodName, m_parameterTypes) ;
			
			JInvocation invocation ;
			if ( null != m_instance ) {
				invocation = JExpr.invoke(m_instance.expr(), method.getName() ) ;
			} else if ( null != m_declaringClass ) {
				invocation = context.codeModel().ref(m_declaringClass).staticInvoke( method.getName() ) ;
			} else {
				throw new IllegalArgumentException("missing both instance and declaring class") ;
			}

			Representation returningRepresentation = determineRepresentation( method.getReturnType() );
			Representation additionalParametersRepresentation ;
			List<Representation> parameterRepresentations ;
			if (  method.isVarArgs() ) {
				List<Class<?>> parameterTypes = new ArrayList<Class<?>>( Arrays.asList(method.getParameterTypes()) );
				Class<?> additionalParametersType = parameterTypes.remove( parameterTypes.size()-1 ).getComponentType() ; 

				parameterRepresentations = determineRepresentations( parameterTypes ) ;
				additionalParametersRepresentation = determineRepresentation( additionalParametersType ) ;
			} else {
				parameterRepresentations = determineRepresentations( method.getParameterTypes() ) ;
				additionalParametersRepresentation = null ;
			}
			
			JavaInvocation result = new JavaInvocation( invocation, returningRepresentation, parameterRepresentations, additionalParametersRepresentation, context );
			if ( null != m_arguments )
				m_arguments.forEach( result::appendArgument ) ;
			return result ;
		}

		@Override public JavaInvocation build( JBlock block ) {
			if ( null == m_declaringClass )
				throw new IllegalArgumentException("missing class") ;
			if ( null == m_methodName )
				throw new IllegalArgumentException("missing method name") ;
			
			Method method ;
			if ( null == m_parameterTypes )
				method = findOnlyMethod(m_declaringClass, m_methodName, m_numberOfParameters ) ;
			else
				method = findMethod(m_declaringClass, m_methodName, m_parameterTypes) ;
			
			JInvocation invocation ;
			if ( null != m_instance ) {
				invocation = block.invoke(m_instance.expr(), method.getName() ) ;
			} else if ( null != m_declaringClass ) {
				invocation = block.staticInvoke( context.codeModel().ref(m_declaringClass), method.getName() ) ;
			} else {
				throw new IllegalArgumentException("missing both instance and declaring class") ;
			}

			Representation returningRepresentation = determineRepresentation( method.getReturnType() );
			Representation additionalParametersRepresentation ;
			List<Representation> parameterRepresentations ;
			if (  method.isVarArgs() ) {
				List<Class<?>> parameterTypes = new ArrayList<Class<?>>( Arrays.asList(method.getParameterTypes()) );
				Class<?> additionalParametersType = parameterTypes.remove( parameterTypes.size()-1 ).getComponentType() ; 

				parameterRepresentations = determineRepresentations( parameterTypes ) ;
				additionalParametersRepresentation = determineRepresentation( additionalParametersType ) ;
			} else {
				parameterRepresentations = determineRepresentations( method.getParameterTypes() ) ;
				additionalParametersRepresentation = null ;
			}
			
			JavaInvocation result = new JavaInvocation( invocation, returningRepresentation, parameterRepresentations, additionalParametersRepresentation, context );
			if ( null != m_arguments )
				m_arguments.forEach( result::appendArgument ) ;
			return result ;
		}
	}
	
}
