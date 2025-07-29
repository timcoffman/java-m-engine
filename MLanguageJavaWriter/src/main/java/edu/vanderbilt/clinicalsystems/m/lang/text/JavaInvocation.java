package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;

import edu.vanderbilt.clinicalsystems.m.text.repr.ClassSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodParameter;
import edu.vanderbilt.clinicalsystems.m.text.repr.MethodSymbol;
import edu.vanderbilt.clinicalsystems.m.text.repr.RepresentationNode;
import edu.vanderbilt.clinicalsystems.m.text.repr.SymbolScope;

public class JavaInvocation extends JavaExpression<JInvocation> {

	private final RoutineJavaBuilderContext m_context ;
	private final List<RepresentationNode> m_parameterRepresentationNodes = new ArrayList<>() ;
	private final RepresentationNode m_additionalParametersRepresentationNode ;
	private final List<JavaExpression<?>> m_arguments = new ArrayList<>() ;
	
	public JavaInvocation( JInvocation expr, RepresentationNode returningRepresentationNode, List<RepresentationNode> parameterRepresentationNodes, RepresentationNode additionalParametersRepresentationNode, RoutineJavaBuilderContext context ) {
		super( expr, returningRepresentationNode ) ;
		m_parameterRepresentationNodes.addAll( parameterRepresentationNodes ) ;
		m_additionalParametersRepresentationNode = additionalParametersRepresentationNode ;
		m_context = context ;
	}
	
//	public JavaInvocation( JInvocation expr, Representation returningRepresentation, List<Representation> parameterRepresentations, Representation additionalParametersRepresentation, RoutineJavaBuilderContext context ) {
//		this( expr, returningRepresentation.supplier(), parameterRepresentations, additionalParametersRepresentation, context ) ;
//	}
	
	public JavaInvocationBuilder chain() {
		return builder(m_context).on( this ) ;
	}
	
	public JavaInvocation appendArgument( Function<RepresentationNode,JavaExpression<?>> f ) {
		int position = m_arguments.size() ;
		RepresentationNode rep ;
		if ( position < m_parameterRepresentationNodes.size() ) {
			rep = m_parameterRepresentationNodes.get(position) ;
		} else if ( null != m_additionalParametersRepresentationNode ) {
			rep = m_additionalParametersRepresentationNode ;
		} else {
			throw new IllegalArgumentException( "attempt to add argument to invocation that only accepts " + m_parameterRepresentationNodes.size() + " parameters") ;
		}
		JavaExpression<?> arg = f.apply(rep); // acquire it, expecting rep
		JavaExpression<?> convertedArg = arg.convert(rep.representation(),m_context);
		m_arguments.add( convertedArg ) ; // convert to rep (if not already)
		expr().arg( convertedArg.expr() ) ;
		return this ;
	}
	
	public JavaInvocation appendArgument( JavaExpression<?> e ) {
		return appendArgument( (r)->e ) ; // expectation already applied
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
		JavaInvocationBuilder as( Supplier<Optional<Representation>> representation ) ;
		JavaInvocation build() ;
		JavaInvocation build( JBlock block ) ;
		JavaInvocationBuilder buildAnd( JBlock block );
		JavaInvocationBuilder buildAnd();
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
		private final RoutineJavaBuilderContext m_context;
		private String m_methodName ;
		private Class<?> m_declaringClass = null ;
		private Integer m_numberOfParameters = null ;
		private List<Class<?>> m_parameterTypes = null ;
		private JavaExpression<?> m_instance = null ;
		private List<JavaExpression<?>> m_arguments = null ;
		private Supplier<Optional<Representation>> m_asRepresentation = null;

		private JavaInvocationBuilderImpl( RoutineJavaBuilderContext context) {
			m_context = context;
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
				m_declaringClass = instance.type(m_context.env()) ;
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
			numberOfParameters( numberOfParameters ) ;
			return this ;
		}
		
		@Override public JavaInvocationBuilder accepting( Class<?> ... parameterTypes ) {
			if ( null != m_parameterTypes )
				throw new IllegalStateException( "parameter types already specified") ;
			m_parameterTypes = Arrays.asList( parameterTypes ) ;
			numberOfParameters( m_parameterTypes.size() ) ;
			return this ;
		}

		@Override public JavaInvocationBuilder supplying( JavaExpression<?> ... arguments ) {
			return supplying( Arrays.asList( arguments ) );
		}
		
		private void numberOfParameters( int numberOfParameters ) {
			if ( null != m_numberOfParameters && m_numberOfParameters != numberOfParameters )
				throw new IllegalStateException( "number of parameters already specified with a different value") ;
				
			if ( null != m_parameterTypes && m_parameterTypes.size() != numberOfParameters )
				throw new IllegalStateException( "parameter types already specified with a different number of parameters") ;
				
			m_numberOfParameters = numberOfParameters ;
		}
		
		@Override public JavaInvocationBuilder supplying( List<JavaExpression<?>> arguments ) {
			if ( null != m_arguments )
				throw new IllegalStateException( "arguments already specified") ;
			m_arguments = new ArrayList<>( arguments ) ;
			int position = 0 ;
			for ( JavaExpression<?> expr : m_arguments ) {
//				MethodParameter methodParameter = null;
//				methodParameter.isAssigned( expr.representationNode() ) ;
			}
			numberOfParameters( m_arguments.size() ) ;
			return this ;
		}
		
		private Method determineMethod() {
			if ( null == m_declaringClass )
				throw new IllegalArgumentException("missing class") ;
			if ( null == m_methodName )
				throw new IllegalArgumentException("missing method name") ;
			
			if ( null == m_parameterTypes )
				return findOnlyMethod(m_declaringClass, m_methodName, m_numberOfParameters) ;
			else
				return findMethod(m_declaringClass, m_methodName, m_parameterTypes) ;
		}
		
		@Override public JavaInvocationBuilder as( Supplier<Optional<Representation>> representation ) {
			m_asRepresentation  = representation ;
			return this ;
		}

		@Override public JavaInvocationBuilder buildAnd() {
			JavaInvocation invocation = build();
			return builder(m_context).on( determineMethod().getReturnType() ).on( invocation ) ;
		}
		
		private MethodSymbol importMethod( Method method ) {
			SymbolScope rootScope = m_context.env().representationInference().rootScope() ;
			ClassSymbol classSymbol =
				rootScope.classSymbolFor( method.getDeclaringClass().getName() )
				.orElseGet( ()->rootScope.createClass( method.getDeclaringClass().getName() ) )
				;
			MethodSymbol methodSymbol =
				classSymbol.getDeclarationScope().methodSymbolFor( method.getName(), method.getParameterTypes().length )
				.orElseGet( ()->{
					MethodSymbol symbol = classSymbol.getDeclarationScope().createMethod( method.getName() ) ;
					symbol.declaredAs( determineRepresentation( method.getReturnType() ) );
					int position = 0 ;
					for ( Parameter parameter : method.getParameters() ) {
						MethodParameter methodParameter = symbol.createParameter(position++, parameter.getName()) ;
						methodParameter.declaredAs( determineRepresentation( parameter.getType() ) );
					}
					return symbol ;
				} )
				;
			return methodSymbol ;
		}
		
		@Override public JavaInvocation build() {
			if ( null == m_parameterTypes && null != m_arguments ) {
				m_parameterTypes = m_arguments.stream()
					.map( JavaExpression::representationNode )
					.map( RepresentationNode::representation )
					.map( m_context.env()::typeFor )
					.collect( Collectors.toList() )
					;
			}
			Method method = determineMethod() ;
			
			MethodSymbol methodSymbol = importMethod( method ) ; 
			
			JInvocation invocation ;
			if ( null != m_instance ) {
				invocation = JExpr.invoke(m_instance.expr(), method.getName() ) ;
			} else if ( null != m_declaringClass ) {
				invocation = m_context.codeModel().ref(m_declaringClass).staticInvoke( method.getName() ) ;
			} else {
				throw new IllegalArgumentException("missing both instance and declaring class") ;
			}
			
			RepresentationNode additionalParametersRepresentation ;
			List<RepresentationNode> parameterRepresentations = new ArrayList<>();
			if ( method.isVarArgs() ) {
				for ( int position = 0 ; position < method.getParameterTypes().length-1 ; ++position )
					parameterRepresentations.add( methodSymbol.parameter(position) ) ;
				additionalParametersRepresentation = methodSymbol.parameter( method.getParameterTypes().length-1 );
			} else {
				for ( int position = 0 ; position < method.getParameterTypes().length ; ++position )
					parameterRepresentations.add( methodSymbol.parameter(position) ) ;
				additionalParametersRepresentation = null ;
			}
			
			
			
			JavaInvocation result = new JavaInvocation( invocation, methodSymbol, parameterRepresentations, additionalParametersRepresentation, m_context );
			if ( null != m_arguments ) {
				int position = 0 ;
				for (JavaExpression<?> arg : m_arguments) {
					MethodParameter methodParameter = methodSymbol.parameter(position) ;
					methodParameter.isAssigned( arg.representationNode() );
					result.appendArgument( arg ) ;
					
					if ( position < method.getParameterTypes().length-1 )
						++position ;
				}
			}
			return result ;
		}

		@Override public JavaInvocationBuilder buildAnd( JBlock block ) {
			JavaInvocation invocation = build(block);
			return builder(m_context).on( determineMethod().getReturnType() ).on( invocation ) ;
		}
		
		@Override public JavaInvocation build( JBlock block ) {
			if ( null == m_parameterTypes && null != m_arguments ) {
				m_parameterTypes = m_arguments.stream()
					.map( JavaExpression::representationNode )
					.map( RepresentationNode::representation )
					.map( m_context.env()::typeFor )
					.collect( Collectors.toList() )
					;
			}
			Method method = determineMethod();

			MethodSymbol methodSymbol = importMethod( method ) ; 
			
			JInvocation invocation ;
			if ( null != m_instance ) {
				invocation = block.invoke(m_instance.expr(), method.getName() ) ;
			} else if ( null != m_declaringClass ) {
				invocation = block.staticInvoke( m_context.codeModel().ref(m_declaringClass), method.getName() ) ;
			} else {
				throw new IllegalArgumentException("missing both instance and declaring class") ;
			}

			RepresentationNode additionalParametersRepresentation ;
			List<RepresentationNode> parameterRepresentations = new ArrayList<>();
			if ( method.isVarArgs() ) {
				for ( int position = 0 ; position < method.getParameterTypes().length-1 ; ++position )
					parameterRepresentations.add( methodSymbol.parameter(position) ) ;
				additionalParametersRepresentation = methodSymbol.parameter( method.getParameterTypes().length-1 );
			} else {
				for ( int position = 0 ; position < method.getParameterTypes().length ; ++position )
					parameterRepresentations.add( methodSymbol.parameter(position) ) ;
				additionalParametersRepresentation = null ;
			}
			
			JavaInvocation result = new JavaInvocation( invocation, methodSymbol, parameterRepresentations, additionalParametersRepresentation, m_context );
			if ( null != m_arguments ) {
				int position = 0 ;
				for (JavaExpression<?> arg : m_arguments) {
					MethodParameter methodParameter = methodSymbol.parameter(position) ;
					methodParameter.isAssigned( arg.representationNode() ) ;
					result.appendArgument( arg ) ;
					
					if ( position < method.getParameterTypes().length-1 )
						++position ;
				}
			}
			return result ;
		}
	}
	
}
