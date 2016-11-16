package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutineLibrary;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeType;
import edu.vanderbilt.clinicalsystems.m.core.annotation.NativeWrapperType;
import edu.vanderbilt.clinicalsystems.m.lang.RoutineAccess;

public abstract class RoutineTools {
	
	private final ProcessingEnvironment m_processingEnv;
	protected final NullType m_mirrorForNull ;
	protected final TypeMirror m_mirrorForBoolean ;
	protected final TypeMirror m_mirrorForCharacter ;
	protected final TypeMirror m_mirrorForString ;
	protected final TypeMirror m_mirrorForNumber ;
	protected final TypeMirror m_mirrorForIntNumber ;
	protected final TypeMirror m_mirrorForLongNumber ;
//	protected final TypeMirror m_mirrorForBigDecimalNumber ;
	protected final TypeMirror m_mirrorForFloatNumber ;
	protected final TypeMirror m_mirrorForDoubleNumber ;
	
	private TagGenerator m_tagBuilder = null;
	private ExpressionGenerator m_expressionBuilder = null;
	private BlockGenerator m_blockBuilder = null;

	
	protected RoutineTools(ProcessingEnvironment processingEnv) {
		m_processingEnv = processingEnv ;
		m_mirrorForNull = m_processingEnv.getTypeUtils().getNullType() ;
		// m_processingEnv.getTypeUtils().getPrimitiveType( TypeKind.INT ) ;
		m_mirrorForBoolean = m_processingEnv.getElementUtils().getTypeElement("java.lang.Boolean").asType() ;
		m_mirrorForCharacter = m_processingEnv.getElementUtils().getTypeElement("java.lang.Character").asType() ;
		m_mirrorForString = m_processingEnv.getElementUtils().getTypeElement("java.lang.String").asType() ;
		m_mirrorForNumber = m_processingEnv.getElementUtils().getTypeElement("java.lang.Number").asType() ;
		m_mirrorForIntNumber = m_processingEnv.getElementUtils().getTypeElement("java.lang.Integer").asType() ;
		m_mirrorForLongNumber = m_processingEnv.getElementUtils().getTypeElement("java.lang.Long").asType() ;
//		m_mirrorForBigDecimalNumber = m_processingEnv.getElementUtils().getTypeElement("java.math.BigDecimal").asType() ;
		m_mirrorForFloatNumber = m_processingEnv.getElementUtils().getTypeElement("java.lang.Float").asType() ;
		m_mirrorForDoubleNumber = m_processingEnv.getElementUtils().getTypeElement("java.lang.Double").asType() ;
	}
	
	public TagGenerator tags() {
		if ( null == m_tagBuilder ) {
			synchronized(this) { m_tagBuilder = new TagGenerator(this) ; }
		}
		return m_tagBuilder ;
	}
	
	public ExpressionGenerator expressions() {
		if ( null == m_expressionBuilder ) {
			synchronized(this) { m_expressionBuilder = new ExpressionGenerator(this) ; }
		}
		return m_expressionBuilder ;
	}
	
	public BlockGenerator blocks() {
		if ( null == m_blockBuilder ) {
			synchronized(this) { m_blockBuilder = new BlockGenerator(this) ; }
		}
		return m_blockBuilder ;
	}
	
	public Elements elements() { return m_processingEnv.getElementUtils() ; }
	
	public Types types() { return m_processingEnv.getTypeUtils() ; }
	
	protected Messager messager() { return m_processingEnv.getMessager() ; }
	
	public enum ReportType {
		ERROR(Kind.ERROR), WARNING(Kind.WARNING), INFO(Kind.NOTE);
		private Kind m_kind ;
		ReportType(Kind kind) { m_kind = kind ; }
		public Kind kind() { return m_kind ; }
	}
	
	public void report(ReportType reportType, String message) {
		report( reportType, message, (Element)null ) ;
	}
	
	public void report(ReportType reportType, String message, Element element) {
		m_processingEnv.getMessager().printMessage(reportType.kind(), message, element);
	}
	
	public void report(ReportType reportType, String message, Ast.Node node ) { node.report( reportType, message ); }
	
	public void report(ReportType reportType, String message, TypeMirror typeMirror) {
		Element element = m_processingEnv.getTypeUtils().asElement( typeMirror ) ;
		report( reportType, message, element );
	}
	
	protected List<String> commentsOnElement(Element element) {
		String comment = m_processingEnv.getElementUtils().getDocComment( element ) ;
		if ( null == comment )
			return Collections.emptyList() ;
		return Arrays.stream(comment.split("\n"))
				.map((s)->s.replaceAll("^\\s*|\\s*$",""))
				.filter((s)->!s.isEmpty())
				.collect(Collectors.toList())
				;
	}

	public boolean isLibraryType(Element element) {
		EpicRoutineLibrary libraryAnnotation = element.asType().getAnnotation(EpicRoutineLibrary.class) ;
		return null != libraryAnnotation ;
	}

	public abstract boolean isPartOfCompilationUnit(Element element) ;

	public boolean isNativeType(Element element) {
		return null != element && null != element.getAnnotation( NativeType.class );
	}

	public boolean isNativeType(TypeMirror typeMirror) {
		return isNativeType( m_processingEnv.getTypeUtils().asElement( typeMirror ));
	}

	public boolean isNativeWrapperType(Element element) {
		return null != element && null != element.getAnnotation( NativeWrapperType.class );
	}
	
	public boolean isNativeWrapperType(TypeMirror typeMirror) {
		return isNativeWrapperType( m_processingEnv.getTypeUtils().asElement( typeMirror ));
	}
	
	public boolean isTypeOfAnything(TypeMirror typeMirror) {
		return !isVoidType(typeMirror) ;
	}

	private boolean isVoidType(TypeMirror typeMirror) {
		return typeMirror.accept( new TypeInterpreter<Boolean,Void>(this) {
			@Override protected Boolean unsupportedTypeMirrorTypeResult( TypeMirror typeMirror, Void parameter) { return false; }
			@Override public Boolean visitNull(NullType nullType, Void parameter) { return true; }
			@Override public Boolean visitNoType(NoType noType, Void parameter) { return true; }
		}, null) ;
	}

	public TypeMirror determineTypeOfLiteral(Ast.Literal literal) {
		switch (literal.literalType()) {
		case STRING_LITERAL:
			return m_mirrorForString;
		case INT_LITERAL:
			return m_mirrorForIntNumber;
		case FLOAT_LITERAL:
			return m_mirrorForFloatNumber;
		case DOUBLE_LITERAL:
			return m_mirrorForDoubleNumber;
		case BOOLEAN_LITERAL:
			return m_mirrorForBoolean;
		case CHAR_LITERAL:
			return m_mirrorForCharacter;
		case LONG_LITERAL:
			return m_mirrorForLongNumber;
		case NULL_LITERAL:
			return m_mirrorForNull;
		default:
			return null;
		}
	}
	
	public boolean isTypeOfNumber(Element element) {
		return isTypeOfNumber( element.asType() );
	}

	public boolean isTypeOfNumber(TypeMirror typeMirror) {
		return m_processingEnv.getTypeUtils().isAssignable( typeMirror, m_mirrorForNumber );
	}

//	private boolean isTypeOfDecimalNumber(Element element) {
//		return isTypeOfDecimalNumber( element.asType() );
//	}
//
//	private boolean isTypeOfDecimalNumber(TypeMirror typeMirror) {
//		return 
//			m_processingEnv.getTypeUtils().isAssignable( typeMirror, m_mirrorForBigDecimalNumber )
//			||
//			m_processingEnv.getTypeUtils().isAssignable( typeMirror, m_mirrorForFloatNumber )
//			||
//			m_processingEnv.getTypeUtils().isAssignable( typeMirror, m_mirrorForDoubleNumber )
//			;
//	}

	public abstract boolean isTypeOfString(Ast.Expression node) ;

	public boolean isTypeOfString(Element element) {
		return isTypeOfString( element.asType() );
	}

	public boolean isTypeOfString(TypeMirror typeMirror) {
		return types().isAssignable( typeMirror, m_mirrorForString );
	}

	public String tagName(Element element) {
		EpicTag epicTagAnnotation = element.getAnnotation( EpicTag.class ) ;
		if ( null != epicTagAnnotation && !EpicTag.DEFAULT_NAME.equals(epicTagAnnotation.value()) ) {
			return epicTagAnnotation.value() ;
		} else {
			return element.getSimpleName().toString() ;
		}
	}

	public String routineName(Element element) {
		if ( element.getKind() != ElementKind.CLASS ) {
			Element enclosingElement = element.getEnclosingElement() ;
			if ( null != enclosingElement )
				return routineName(enclosingElement) ;
			else
				return null ;
		}
		EpicRoutineLibrary epicRoutineLibraryAnnotation = element.getAnnotation( EpicRoutineLibrary.class ) ;
		if ( null != epicRoutineLibraryAnnotation && !EpicRoutineLibrary.DEFAULT_NAME.equals(epicRoutineLibraryAnnotation.value()) ) {
			return epicRoutineLibraryAnnotation.value() ;
		} else {
			return element.getSimpleName().toString() ;
		}
	}

	public RoutineAccess routineAccess(Element element ) {
		if ( isPartOfCompilationUnit(element) )
			return RoutineAccess.LOCAL ;
		
		if ( element.getKind() != ElementKind.CLASS ) {
			Element enclosingElement = element.getEnclosingElement() ;
			if ( null != enclosingElement )
				return routineAccess(enclosingElement) ;
			else
				return null ;
		}
		
		EpicRoutineLibrary epicRoutineLibraryAnnotation = element.getAnnotation( EpicRoutineLibrary.class ) ;
		if ( null != epicRoutineLibraryAnnotation && epicRoutineLibraryAnnotation.implicit() ) {
			return RoutineAccess.IMPLICIT ;
		} else {
			return RoutineAccess.EXPLICIT ;
		}
	}
	
	public interface IdentifierResolution {
		enum Kind { TYPE, VARIABLE, METHOD, UNKNOWN } ;
		Kind kind() ;
		TypeMirror type() ;
		interface Visitor<R,P> {
			R visit( TypeResolution         typeResolution, P parameter ) ;
			R visit( VariableResolution variableResolution, P parameter ) ;
			R visit( MethodResolution     methodResolution, P parameter ) ;
		}
		<R,P> R accept( Visitor<R,P> visitor, P parameter ) ;
	}
	
	public interface TypeResolution extends IdentifierResolution {
		TypeMirror selfType() ;
	}
	
	public interface VariableResolution extends IdentifierResolution {
		TypeMirror declaredType() ;
		Object constantValue();
	}
	
	public interface MethodResolution extends IdentifierResolution {
		TypeMirror returnType() ;
		ExecutableElement declaration() ;
	}
	
	public abstract IdentifierResolution resolveIdentifier( Ast.Identifier node ) ;

	public abstract IdentifierResolution resolveIdentifier( Ast.Variable node ) ;

	public abstract IdentifierResolution resolveIdentifier( Name name, Ast.Node node ) ;
	
	public IdentifierResolution resolveIdentifier( Name name, TypeMirror typeMirror ) {
		Element typeElement = m_processingEnv.getTypeUtils().asElement(typeMirror);
		for ( Element enclosedElement : typeElement.getEnclosedElements() )
			if ( name.equals( enclosedElement.getSimpleName() ) ) { 
				return resolutionForExecutableElement( (ExecutableElement)enclosedElement ) ;
			}
		return null ;
	}
	
	protected TypeResolution resolutionForTypeElement( TypeElement typeElement ) {
		return new TypeResolutionImpl( typeElement ) ;
	}
	
	private class TypeResolutionImpl implements TypeResolution {
		private final TypeElement m_typeElement ;
		public TypeResolutionImpl( TypeElement typeElement ) { m_typeElement = typeElement ; }
		@Override public IdentifierResolution.Kind kind() { return IdentifierResolution.Kind.TYPE ; }
		@Override public TypeMirror type() { return selfType() ; }
		@Override public TypeMirror selfType() { return m_typeElement.asType() ; }
		@Override public <R,P> R accept( Visitor<R,P> visitor, P parameter ) { return visitor.visit(this, parameter) ; }
	}

	protected VariableResolution resolutionForVariableElement( VariableElement variableElement) {
		return new VariableResolutionImpl( variableElement );
	}
	
	private class VariableResolutionImpl implements VariableResolution {
		private final VariableElement m_variableElement ;
		public VariableResolutionImpl( VariableElement variableElement ) { m_variableElement = variableElement ; }
		@Override public IdentifierResolution.Kind kind() { return IdentifierResolution.Kind.VARIABLE ; }
		@Override public TypeMirror type() { return declaredType() ; }
		@Override public TypeMirror declaredType() { return m_variableElement.asType() ; }
		@Override public Object constantValue() { return m_variableElement.getConstantValue() ; }
		@Override public <R,P> R accept( Visitor<R,P> visitor, P parameter ) { return visitor.visit(this, parameter) ; }
	}

	protected MethodResolution resolutionForExecutableElement( ExecutableElement executableElement ) {
		return new MethodResolutionImpl( executableElement ) ; 
	}
	
	private class MethodResolutionImpl implements MethodResolution {
		private final ExecutableElement m_executableElement ;
		public MethodResolutionImpl( ExecutableElement executableElement ) { m_executableElement = executableElement ; }
		@Override public IdentifierResolution.Kind kind() { return IdentifierResolution.Kind.METHOD ; }
		@Override public ExecutableElement declaration() { return m_executableElement ; }
		@Override public TypeMirror type() { return returnType() ; }
		@Override public TypeMirror returnType() { return m_executableElement.getReturnType() ; }
		@Override public <R,P> R accept( Visitor<R,P> visitor, P parameter ) { return visitor.visit(this, parameter) ; }
	}
	
	public abstract MethodResolution resolveMethodInvocationTarget( Ast.MethodInvocation methodInvocationNode ) ;
	
	public abstract Ast.Method getMethod(ExecutableElement methodElement) ;
}
