package edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.dom.*;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicRoutineLibrary;
import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.ElementInterpreter;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;

public class RoutineJdtTools extends RoutineTools {

	private final CompilationUnit m_compilationUnit ;
	
	public RoutineJdtTools(ProcessingEnvironment processingEnvironment, TypeElement annotatedType) {
		super(processingEnvironment) ;
		m_compilationUnit = JdtTools.createCompilationUnit( annotatedType ) ;
	}

	public void report(ReportType reportType, String message, ASTNode node) {
		try {
			int charStart = node.getStartPosition() ;
			int charEnd = charStart + node.getLength() ;
			IMarker marker = m_compilationUnit.getJavaElement().getResource().createMarker("edu.vanderbilt.clinicalsystems.epic.annotation.marker") ;
			switch ( reportType ) {
			case ERROR:   marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_ERROR);   break ;
			case WARNING: marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_WARNING); break ;
			case INFO:    marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_INFO);    break ;
			default:      marker.setAttribute( IMarker.SEVERITY, IMarker.SEVERITY_WARNING);    break ;
			}
			marker.setAttribute( IMarker.MESSAGE, "message: " + message);
			marker.setAttribute( IMarker.CHAR_START, charStart);
			marker.setAttribute( IMarker.CHAR_END, charEnd);
			marker.setAttribute( IJavaModelMarker.ID, 7777);
		} catch ( CoreException ex ) {
			report(reportType, message + " at " + node.toString() );
		}
	}

	@Override
	public boolean isPartOfCompilationUnit(Element element) {
//		m_compilationUnit ;
		return false ; // placeholder; 
	}
	
	@Override
	public boolean isTypeOfString(Ast.Expression node) {
		return isTypeOfString( determineType( unwrap( node ) ) ) ; 
	}

	private final class ExpressionTypeDeterminer extends ASTInterpreter<TypeMirror, Void> {
		
		public ExpressionTypeDeterminer() { super(RoutineJdtTools.this) ;}
		
//		@Override
//		protected TypeMirror unsupportedTreeTypeResult(Tree tree, Void parameter) {
//			return null;
//		}

		@Override
		public boolean visit(SimpleName name) {
			provide( determineTypeOfIdentifier( wrap(name) ) ) ;
			return false ;
		}

		@Override
		public boolean visit(QualifiedName name) {
			provide( determineTypeOfIdentifier( wrap(name) ) ) ;
			return false ;
		}

		@Override
		public boolean visit(BooleanLiteral literal) {
			provide( determineTypeOfLiteral( wrap(literal) ) );
			return false ;
		}

		@Override
		public boolean visit(StringLiteral literal) {
			provide( determineTypeOfLiteral( wrap(literal) ) );
			return false ;
		}

		@Override
		public boolean visit(CharacterLiteral literal) {
			provide( determineTypeOfLiteral( wrap(literal) ) );
			return false ;
		}

		@Override
		public boolean visit(NullLiteral literal) {
			provide( determineTypeOfLiteral( wrap(literal) ) );
			return false ;
		}
	}
	
	public TypeMirror determineType( Expression expression ) {
		return new ExpressionTypeDeterminer().acceptWithParameter(null, expression ) ;
	}

	@Override
	public String tagName(Element element) {
		EpicTag epicTagAnnotation = element.getAnnotation( EpicTag.class ) ;
		if ( null != epicTagAnnotation && !EpicTag.DEFAULT_NAME.equals(epicTagAnnotation.value()) ) {
			return epicTagAnnotation.value() ;
		} else {
			return element.getSimpleName().toString() ;
		}
	}

	@Override
	public String routineName(Element element) {
		if ( element.getKind() != ElementKind.CLASS ) {
			Element enclosingElement = element.getEnclosingElement() ;
			if ( null != enclosingElement )
				return routineName(enclosingElement) ;
			else
				return null ;
		}
		EpicRoutineLibrary epicRoutineLibraryAnnotation = element.getAnnotation( EpicRoutineLibrary.class ) ;
		if ( null != epicRoutineLibraryAnnotation && epicRoutineLibraryAnnotation.implicit() ) {
			return null ; // none needed for implicit libraries
		} else if ( null != epicRoutineLibraryAnnotation && !EpicRoutineLibrary.DEFAULT_NAME.equals(epicRoutineLibraryAnnotation.value()) ) {
			return epicRoutineLibraryAnnotation.value() ;
		} else {
			return element.getSimpleName().toString() ;
		}
	}
	
	@Override
	public IdentifierResolution resolveIdentifier(javax.lang.model.element.Name name, Ast.Node node) {
		// TODO Auto-generated method stub
		return null;
	}	
	@Override
	public IdentifierResolution resolveIdentifier( Ast.Identifier identifierNode ) {
		return resolveIdentifier( this.<Name>unwrap(identifierNode) ) ;
	}
	
	private abstract class BindingResolutionImpl<T extends IBinding> implements IdentifierResolution {
		protected final T m_binding ;
		public BindingResolutionImpl(T binding) { m_binding = binding ; }
		protected TypeMirror typeMirrorFor( ITypeBinding typeBinding ) {
			if ( typeBinding.isPrimitive() ) {
				return types().getPrimitiveType( TypeKind.valueOf(typeBinding.getQualifiedName().toUpperCase())) ;
			} else {
				return elements().getTypeElement( typeBinding.getQualifiedName() ).asType() ;
			}
		}
		
	}
	
	private class TypeBindingResolutionImpl extends BindingResolutionImpl<ITypeBinding> implements TypeResolution {
		public TypeBindingResolutionImpl(ITypeBinding typeBinding) { super(typeBinding) ; }
		@Override public Kind kind() { return IdentifierResolution.Kind.TYPE ; }
		@Override public TypeMirror type() { return selfType() ; }
		@Override public TypeMirror selfType() { return typeMirrorFor( m_binding ) ; }
		@Override public <R, P> R accept(Visitor<R, P> visitor, P parameter) { return visitor.visit(this,parameter) ; }
	}
	
	private class VariableBindingResolutionImpl extends BindingResolutionImpl<IVariableBinding> implements VariableResolution {
		public VariableBindingResolutionImpl(IVariableBinding variableBinding) { super(variableBinding) ; }
		@Override public Kind kind() { return IdentifierResolution.Kind.VARIABLE ; }
		@Override public TypeMirror type() { return declaredType() ; }
		@Override public TypeMirror declaredType() { return typeMirrorFor( m_binding.getType() ) ; }
		@Override public <R, P> R accept(Visitor<R, P> visitor, P parameter) { return visitor.visit(this,parameter) ; }
		@Override public Object constantValue() { return m_binding.getConstantValue() ; }
	}
	
	private class MethodBindingResolutionImpl extends BindingResolutionImpl<IMethodBinding> implements MethodResolution {
		public MethodBindingResolutionImpl(IMethodBinding methodBinding) { super(methodBinding) ; }
		@Override public Kind kind() { return IdentifierResolution.Kind.METHOD ; }
		@Override public TypeMirror type() { return returnType() ; }
		@Override public TypeMirror returnType() { return typeMirrorFor( m_binding.getReturnType() ) ; }
		@Override public ExecutableElement declaration() { return findMethod( elements().getTypeElement( m_binding.getDeclaringClass().getQualifiedName() ), m_binding) ; }
		@Override public <R, P> R accept(Visitor<R, P> visitor, P parameter) { return visitor.visit(this,parameter) ; }
	}
	
	private IdentifierResolution resolveIdentifier( Name name ) {
		ITypeBinding typeBinding = name.resolveTypeBinding() ;
		IBinding binding = name.resolveBinding();
		switch ( binding.getKind() ) {
		case IBinding.METHOD: return new MethodBindingResolutionImpl( (IMethodBinding)binding ) ;
		case IBinding.TYPE: return new TypeBindingResolutionImpl( (ITypeBinding)binding ) ;
		case IBinding.VARIABLE: return new VariableBindingResolutionImpl( (IVariableBinding)binding ) ;
		}
		TypeElement typeElement = elements().getTypeElement( typeBinding.getQualifiedName() );
		return resolutionForTypeElement( typeElement ) ;
	}
	
	@Override
	public IdentifierResolution resolveIdentifier( Ast.Variable variableNode ) {
		return resolveIdentifier( this.<VariableDeclarationFragment>unwrap(variableNode) ) ;
	}
	
	private ExecutableElement findMethod( TypeElement typeElement, IMethodBinding methodBinding ) {
		if ( null == typeElement )
			return null ;
		for (Element element : typeElement.getEnclosedElements()) {
			ExecutableElement methodElement = element.accept( new ElementInterpreter<ExecutableElement, IMethodBinding>(RoutineJdtTools.this) {

				@Override
				public ExecutableElement visitExecutable(ExecutableElement executableElement, IMethodBinding methodBinding) {
					if ( executableElement.getSimpleName().contentEquals( methodBinding.getName() ) )
						return executableElement ;
					else
						return null ;
				}
			}, methodBinding ) ;
			if ( null != methodElement )
				return methodElement ;
		}
		return null ;
	}
	
	private VariableElement findVariableElement( ExecutableElement methodElement, IVariableBinding variableBinding) {
		if ( null == methodElement )
			return null ;
		for ( Element element: methodElement.getEnclosedElements() ) {
			VariableElement variableElement = element.accept( new ElementInterpreter<VariableElement, IVariableBinding>(RoutineJdtTools.this) {
				
				@Override
				public VariableElement visitVariable(VariableElement variableElement, IVariableBinding variableBinding) {
					if ( variableElement.getSimpleName().contentEquals( variableBinding.getName() ) )
						return variableElement ;
					else
						return null ;
				}
			}, variableBinding ) ;
			if ( null != variableElement )
				return variableElement ;
		}
		return null ;
	}
	
	private IdentifierResolution resolveIdentifier( VariableDeclarationFragment variable ) {
		IVariableBinding variableBinding = variable.resolveBinding() ;
		IMethodBinding methodBinding = variableBinding.getDeclaringMethod() ;
		ITypeBinding typeBinding = methodBinding.getDeclaringClass() ;
		TypeElement typeElement = elements().getTypeElement( typeBinding.getQualifiedName() ) ;
		ExecutableElement methodElement = findMethod( typeElement, methodBinding ) ;
		VariableElement variableElement = findVariableElement( methodElement, variableBinding ) ;
		return resolutionForVariableElement(variableElement) ;
	}

	@Override
	public IdentifierResolution resolveIdentifier( javax.lang.model.element.Name name, TypeMirror typeMirror ) {
		Element typeElement = types().asElement(typeMirror);
		for ( Element enclosedElement : typeElement.getEnclosedElements() )
			if ( name.equals( enclosedElement.getSimpleName() ) ) { 
				return resolutionForExecutableElement( (ExecutableElement)enclosedElement ) ;
			}
		return null ;
	}

	public TypeMirror determineTypeOfIdentifier( Ast.Identifier identifier ) {
		return null ;
	}

	private TypeMirror determineTypeOfName( Name name, TypeMirror typeMirror ) {
		Element typeElement = types().asElement(typeMirror);
		for ( Element enclosedElement : typeElement.getEnclosedElements() )
			if ( name.equals( enclosedElement.getSimpleName() ) ) 
				return ((ExecutableElement)enclosedElement).getReturnType() ;
		return null ;
	}

	@Override
	public MethodResolution resolveMethodInvocationTarget( Ast.MethodInvocation methodInvocationNode ) {
		return resolveMethodInvocationTarget( this.<MethodInvocation>unwrap( methodInvocationNode ) ) ;
	}

	public MethodResolution resolveMethodInvocationTarget(MethodInvocation methodInvocation) {
		return new MethodBindingResolutionImpl( methodInvocation.resolveMethodBinding() ) ;
	}

	private <T extends ASTNode> T unwrap( Ast.Node node ) {
		if ( !(node instanceof ASTNodeWrapper) )
			throw new IllegalArgumentException( this.getClass().getSimpleName() + " cannot operate on " + node.getClass().getSimpleName() + " which it did not create" ) ;
		@SuppressWarnings("unchecked")
		ASTNodeWrapper<T> astNodeWrapper = (ASTNodeWrapper<T>)node ;
		return astNodeWrapper.astNode() ;
	}
	
	private class ASTNodeWrapper<T extends ASTNode> implements Ast.Node {
		protected final T m_astNode ;
		public ASTNodeWrapper(T astNode) { m_astNode = astNode ; }
		public T astNode() { return m_astNode ; }
		@Override public void report( ReportType reportType, String message ) { RoutineJdtTools.this.report(reportType, message, m_astNode) ; }
		@Override public String toString() { return m_astNode.toString() ; }
		@Override public <R, P> R accept(Ast.Visitor<R, P> visitor, P parameter) {
			return new ASTAdapter<R,P>() {

				@Override public boolean visit(ASTNode node) { provide( visitor.visitNode( wrap(node), parameter() ) ) ; return false ; }

//				@Override public boolean visit( AnnotationTypeDeclaration node ) { provide( visitor.visitAnnotationTypeDeclaration( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( AnnotationTypeMemberDeclaration node ) { provide( visitor.visitAnnotationTypeMemberDeclaration( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( AnonymousClassDeclaration node ) { provide( visitor.visitAnonymousClassDeclaration( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ArrayAccess node ) { provide( visitor.visitArrayAccess( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( ArrayCreation node ) { provide( visitor.visitArrayCreation( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( ArrayInitializer node ) { provide( visitor.visitArrayInitializer( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ArrayType node ) { provide( visitor.visitArrayType( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( AssertStatement node ) { provide( visitor.visitAssertStatement( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( Assignment node ) { provide( visitor.visitAssignment( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( Block node ) { provide( visitor.visitBlock( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( BlockComment node ) { provide( visitor.visitBlockComment( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( BooleanLiteral node ) { provide( visitor.visitLiteral( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( BreakStatement node ) { provide( visitor.visitBreak( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( CastExpression node ) { provide( visitor.visitTypeCast( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( CatchClause node ) { provide( visitor.visitCatch( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( CharacterLiteral node ) { provide( visitor.visitLiteral( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( ClassInstanceCreation node ) { provide( visitor.visitClassInstanceCreation( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( CompilationUnit node ) { provide( visitor.visitCompilationUnit( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ConditionalExpression node ) { provide( visitor.visitConditionalExpression( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( ConstructorInvocation node ) { provide( visitor.visitConstructorInvocation( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ContinueStatement node ) { provide( visitor.visitContinue( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( CreationReference node ) { provide( visitor.visitCreationReference( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( Dimension node ) { provide( visitor.visitDimension( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( DoStatement node ) { provide( visitor.visitDoStatement( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( EmptyStatement node ) { provide( visitor.visitEmptyStatement( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( EnhancedForStatement node ) { provide( visitor.visitEnhancedForLoop( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( EnumConstantDeclaration node ) { provide( visitor.visitEnumConstantDeclaration( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( EnumDeclaration node ) { provide( visitor.visitEnumDeclaration( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( ExpressionMethodReference node ) { provide( visitor.visitExpressionMethodReference( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ExpressionStatement node ) { provide( visitor.visitExpressionStatement( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( FieldAccess node ) { provide( visitor.visitFieldAccess( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( FieldDeclaration node ) { provide( visitor.visitFieldDeclaration( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ForStatement node ) { provide( visitor.visitForLoop( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( IfStatement node ) { provide( visitor.visitIf( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( ImportDeclaration node ) { provide( visitor.visitImportDeclaration( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( InfixExpression node ) { provide( visitor.visitBinary( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( Initializer node ) { provide( visitor.visitInitializer( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( InstanceofExpression node ) { provide( visitor.visitInstanceOf( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( IntersectionType node ) { provide( visitor.visitIntersectionType( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( Javadoc node ) { provide( visitor.visitJavadoc( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( LabeledStatement node ) { provide( visitor.visitLabeledStatement( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( LambdaExpression node ) { provide( visitor.visitLambdaExpression( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( LineComment node ) { provide( visitor.visitLineComment( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( MarkerAnnotation node ) { provide( visitor.visitAnnotation( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( MemberRef node ) { provide( visitor.visitMemberRef( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( MemberValuePair node ) { provide( visitor.visitMemberValuePair( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( MethodRef node ) { provide( visitor.visitMethodRef( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( MethodRefParameter node ) { provide( visitor.visitMethodRefParameter( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( MethodDeclaration node ) { provide( visitor.visitMethod( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( MethodInvocation node ) { provide( visitor.visitMethodInvocation( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( Modifier node ) { provide( visitor.visitModifier( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( NameQualifiedType node ) { provide( visitor.visitIdentifier( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( NormalAnnotation node ) { provide( visitor.visitAnnotation( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( NullLiteral node ) { provide( visitor.visitLiteral( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( NumberLiteral node ) { provide( visitor.visitLiteral( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( PackageDeclaration node ) { provide( visitor.visitPackageDeclaration( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ParameterizedType node ) { provide( visitor.visitParameterizedType( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ParenthesizedExpression node ) { provide( visitor.visitParenthesized( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( PostfixExpression node ) { provide( visitor.visitUnary( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( PrefixExpression node ) { provide( visitor.visitUnary( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( PrimitiveType node ) { provide( visitor.visitPrimitiveType( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( QualifiedName node ) { provide( visitor.visitIdentifier( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( QualifiedType node ) { provide( visitor.visitQualifiedType( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ReturnStatement node ) { provide( visitor.visitReturn( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( SimpleName node ) { provide( visitor.visitIdentifier( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( SimpleType node ) { provide( visitor.visitSimpleType( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( SingleMemberAnnotation node ) { provide( visitor.visitAnnotation( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( SingleVariableDeclaration node ) { provide( visitor.visitSingleVariableDeclaration( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( StringLiteral node ) { provide( visitor.visitLiteral( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( SuperConstructorInvocation node ) { provide( visitor.visitSuperConstructorInvocation( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( SuperFieldAccess node ) { provide( visitor.visitSuperFieldAccess( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( SuperMethodInvocation node ) { provide( visitor.visitSuperMethodInvocation( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( SuperMethodReference node ) { provide( visitor.visitSuperMethodReference( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( SwitchCase node ) { provide( visitor.visitCase( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( SwitchStatement node ) { provide( visitor.visitSwitch( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( SynchronizedStatement node ) { provide( visitor.visitSynchronized( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( TagElement node ) { provide( visitor.visitTagElement( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( TextElement node ) { provide( visitor.visitTextElement( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( ThisExpression node ) { provide( visitor.visitThisExpression( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( ThrowStatement node ) { provide( visitor.visitThrow( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( TryStatement node ) { provide( visitor.visitTry( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( TypeDeclaration node ) { provide( visitor.visitTypeDeclaration( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( TypeDeclarationStatement node ) { provide( visitor.visitTypeDeclarationStatement( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( TypeLiteral node ) { provide( visitor.visitTypeLiteral( wrap(node), parameter() ) ); return false ; }
//				@Override public boolean visit( TypeMethodReference node ) { provide( visitor.visitTypeMethodReference( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( TypeParameter node ) { provide( visitor.visitTypeParameter( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( UnionType node ) { provide( visitor.visitUnionType( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( VariableDeclarationExpression node ) { provide( visitor.visitVariablesExpression( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( VariableDeclarationStatement node ) { provide( visitor.visitVariablesStatement( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( VariableDeclarationFragment node ) { provide( visitor.visitVariable( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( WhileStatement node ) { provide( visitor.visitWhileLoop( wrap(node), parameter() ) ); return false ; }
				@Override public boolean visit( WildcardType node ) { provide( visitor.visitWildcard( wrap(node), parameter() ) ); return false ; }
			}.acceptWithParameter( parameter, m_astNode ) ;
		}
	}
	
	private abstract class MethodReferenceWrapper<T extends MethodReference> extends ASTNodeWrapper<T> implements Ast.MemberReference /* ??? */ {
		public MethodReferenceWrapper(T node) { super(node) ; }
	}

	private abstract class LiteralWrapper<T extends Expression> extends ASTNodeWrapper<T> implements Ast.Literal {
		public LiteralWrapper(T node) { super(node) ; }
		@Override public Object value() { return m_astNode.resolveConstantExpressionValue() ; }
	}
	
	private abstract class UnaryWrapper<T extends Expression> extends ASTNodeWrapper<T> implements Ast.Unary {
		public UnaryWrapper(T node) { super(node) ; }
	}
	
	private class SyntheticExpressionStatement implements Ast.ExpressionStatement {
		private final Expression m_expression ;
		public SyntheticExpressionStatement(Expression expression) { m_expression = expression; }
		@Override public void report( ReportType reportType, String message ) { RoutineJdtTools.this.report(reportType, message, m_expression) ; } 
		@Override public <R, P> R accept(Ast.Visitor<R, P> visitor,P parameter) { return visitor.visitExpressionStatement(this, parameter) ; }
		@Override public Ast.Expression expression() { return wrap( m_expression ) ; }
	}
	
//	private class AnnotationTypeDeclarationImpl extends ASTNodeWrapper<AnnotationTypeDeclaration> implements Ast.AnnotationTypeDeclaration {
//		public AnnotationTypeDeclarationImpl(AnnotationTypeDeclaration node) { super(node) ; }
//	}
//	private class AnnotationTypeMemberDeclarationImpl extends ASTNodeWrapper<AnnotationTypeMemberDeclaration> implements Ast.AnnotationTypeMemberDeclaration {
//		public AnnotationTypeMemberDeclarationImpl(AnnotationTypeMemberDeclaration node) { super(node) ; }
//	}
//	private class AnonymousClassDeclarationImpl extends ASTNodeWrapper<AnonymousClassDeclaration> implements Ast.AnonymousClassDeclaration {
//		public AnonymousClassDeclarationImpl(AnonymousClassDeclaration node) { super(node) ; }
//	}
	private class ArrayAccessImpl extends ASTNodeWrapper<ArrayAccess> implements Ast.ArrayAccess {
		public ArrayAccessImpl(ArrayAccess node) { super(node) ; }
	}
//	private class ArrayCreationImpl extends ASTNodeWrapper<ArrayCreation> implements Ast.ArrayCreation {
//		public ArrayCreationImpl(ArrayCreation node) { super(node) ; }
//	}
//	private class ArrayInitializerImpl extends ASTNodeWrapper<ArrayInitializer> implements Ast.ArrayInitializer {
//		public ArrayInitializerImpl(ArrayInitializer node) { super(node) ; }
//	}
	private class ArrayTypeImpl extends ASTNodeWrapper<ArrayType> implements Ast.ArrayType {
		public ArrayTypeImpl(ArrayType node) { super(node) ; }
	}
	private class AssertStatementImpl extends ASTNodeWrapper<AssertStatement> implements Ast.Assert {
		public AssertStatementImpl(AssertStatement node) { super(node) ; }
	}
	private class AssignmentImpl extends ASTNodeWrapper<Assignment> implements Ast.Assignment {
		public AssignmentImpl(Assignment node) { super(node) ; }
		@Override public Ast.Expression expression() { return wrap( m_astNode.getRightHandSide() ) ; }
		@Override public Ast.Expression variable() { return wrap( m_astNode.getLeftHandSide() ) ; }
		@Override public Ast.Assignment.AssignmentType assignmentType() {
			Assignment.Operator operator = m_astNode.getOperator() ;
			if ( operator == Assignment.Operator.ASSIGN )
				return Ast.Assignment.AssignmentType.ASSIGNMENT ;
			else if ( operator == Assignment.Operator.BIT_AND_ASSIGN )
				return Ast.Assignment.AssignmentType.BIT_AND_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.BIT_OR_ASSIGN )
				return Ast.Assignment.AssignmentType.BIT_OR_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.BIT_XOR_ASSIGN )
				return Ast.Assignment.AssignmentType.BIT_XOR_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.DIVIDE_ASSIGN )
				return Ast.Assignment.AssignmentType.DIVIDE_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.LEFT_SHIFT_ASSIGN )
				return Ast.Assignment.AssignmentType.LEFT_SHIFT_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.MINUS_ASSIGN )
				return Ast.Assignment.AssignmentType.MINUS_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.PLUS_ASSIGN )
				return Ast.Assignment.AssignmentType.PLUS_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.REMAINDER_ASSIGN )
				return Ast.Assignment.AssignmentType.REMAINDER_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.RIGHT_SHIFT_SIGNED_ASSIGN )
				return Ast.Assignment.AssignmentType.RIGHT_SHIFT_SIGNED_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.RIGHT_SHIFT_UNSIGNED_ASSIGN )
				return Ast.Assignment.AssignmentType.RIGHT_SHIFT_UNSIGNED_ASSIGNMENT ;
			else if ( operator == Assignment.Operator.TIMES_ASSIGN )
				return Ast.Assignment.AssignmentType.MULTIPLY_ASSIGNMENT ;
			else
				return Ast.Assignment.AssignmentType.UNKNOWN ;
		}
	}
	private class BlockImpl extends ASTNodeWrapper<Block> implements Ast.Block {
		public BlockImpl(Block node) { super(node) ; }
		@Override public List<? extends Ast.Statement> statements() { return wrapStatements( m_astNode.statements() ) ; }
	}
//	private class BlockCommentImpl extends ASTNodeWrapper<BlockComment> implements Ast.BlockComment {
//		public BlockCommentImpl(BlockComment node) { super(node) ; }
//	}
	private class BooleanLiteralImpl extends LiteralWrapper<BooleanLiteral> {
		public BooleanLiteralImpl(BooleanLiteral node) { super(node) ; }
		@Override public Ast.Literal.LiteralType literalType() { return Ast.Literal.LiteralType.BOOLEAN_LITERAL ; }
		@Override public Object value() { return m_astNode.booleanValue() ; }
	}
	private class BreakStatementImpl extends ASTNodeWrapper<BreakStatement> implements Ast.Break {
		public BreakStatementImpl(BreakStatement node) { super(node) ; }
	}
	private class CastExpressionImpl extends ASTNodeWrapper<CastExpression> implements Ast.TypeCast {
		public CastExpressionImpl(CastExpression node) { super(node) ; }
		@Override public Ast.Expression expression() { return wrap( m_astNode.getExpression() ) ; }
	}
	private class CatchClauseImpl extends ASTNodeWrapper<CatchClause> implements Ast.Catch {
		public CatchClauseImpl(CatchClause node) { super(node) ; }
	}
	private class CharacterLiteralImpl extends LiteralWrapper<CharacterLiteral> {
		public CharacterLiteralImpl(CharacterLiteral node) { super(node) ; }
		@Override public Ast.Literal.LiteralType literalType() { return Ast.Literal.LiteralType.CHAR_LITERAL ; }
		@Override public Object value() { return m_astNode.charValue() ; }
	}
	
//	private class ClassInstanceCreationImpl extends ASTNodeWrapper<ClassInstanceCreation> implements Ast.ClassInstanceCreation {
//		public ClassInstanceCreationImpl(ClassInstanceCreation node) { super(node) ; }
//	}
	private class CompilationUnitImpl extends ASTNodeWrapper<CompilationUnit> implements Ast.CompilationUnit {
		public CompilationUnitImpl(CompilationUnit node) { super(node) ; }
	}
	private class ConditionalExpressionImpl extends ASTNodeWrapper<ConditionalExpression> implements Ast.ConditionalExpression {
		public ConditionalExpressionImpl(ConditionalExpression node) { super(node) ; }
	}
//	private class ConstructorInvocationImpl extends ASTNodeWrapper<ConstructorInvocation> implements Ast.ConstructorInvocation {
//		public ConstructorInvocationImpl(ConstructorInvocation node) { super(node) ; }
//	}
	private class ContinueStatementImpl extends ASTNodeWrapper<ContinueStatement> implements Ast.Continue {
		public ContinueStatementImpl(ContinueStatement node) { super(node) ; }
	}
	private class CreationReferenceImpl extends MethodReferenceWrapper<CreationReference> {
		public CreationReferenceImpl(CreationReference node) { super(node) ; }
	}
//	private class DimensionImpl extends ASTNodeWrapper<Dimension> implements Ast.Dimension {
//		public DimensionImpl(Dimension node) { super(node) ; }
//	}
	private class DoStatementImpl extends ASTNodeWrapper<DoStatement> implements Ast.DoWhileLoop {
		public DoStatementImpl(DoStatement node) { super(node) ; }
		@Override public Ast.Statement statement() { return wrap( m_astNode.getBody() ) ; }
		@Override public Ast.Expression condition() { return wrap( m_astNode.getExpression() ); }
	}
	private class EmptyStatementImpl extends ASTNodeWrapper<EmptyStatement> implements Ast.EmptyStatement {
		public EmptyStatementImpl(EmptyStatement node) { super(node) ; }
	}
	private class EnhancedForStatementImpl extends ASTNodeWrapper<EnhancedForStatement> implements Ast.EnhancedForLoop {
		public EnhancedForStatementImpl(EnhancedForStatement node) { super(node) ; }
	}
//	private class EnumConstantDeclarationImpl extends ASTNodeWrapper<EnumConstantDeclaration> implements Ast.EnumConstantDeclaration {
//		public EnumConstantDeclarationImpl(EnumConstantDeclaration node) { super(node) ; }
//	}
//	private class EnumDeclarationImpl extends ASTNodeWrapper<EnumDeclaration> implements Ast.EnumDeclaration {
//		public EnumDeclarationImpl(EnumDeclaration node) { super(node) ; }
//	}
	private class ExpressionMethodReferenceImpl extends MethodReferenceWrapper<ExpressionMethodReference> {
		public ExpressionMethodReferenceImpl(ExpressionMethodReference node) { super(node) ; }
	}
	private class ExpressionStatementImpl extends ASTNodeWrapper<ExpressionStatement> implements Ast.ExpressionStatement {
		public ExpressionStatementImpl(ExpressionStatement node) { super(node) ; }
		@Override public Ast.Expression expression() { return wrap( m_astNode.getExpression() ) ; }
	}
//	private class FieldAccessImpl extends ASTNodeWrapper<FieldAccess> implements Ast.FieldAccess {
//		public FieldAccessImpl(FieldAccess node) { super(node) ; }
//	}
//	private class FieldDeclarationImpl extends ASTNodeWrapper<FieldDeclaration> implements Ast.FieldDeclaration {
//		public FieldDeclarationImpl(FieldDeclaration node) { super(node) ; }
//	}
	private class ForStatementImpl extends ASTNodeWrapper<ForStatement> implements Ast.ForLoop {
		public ForStatementImpl(ForStatement node) { super(node) ; }
		@Override public List<? extends Ast.Statement> initializer() { return wrapExpressionsAsStatements( m_astNode.initializers() ) ; }
		@Override public Ast.Expression condition() { return wrap( m_astNode.getExpression() ) ; }
		@Override public List<? extends Ast.ExpressionStatement> update() { return wrapExpressionsAsStatements( m_astNode.updaters() ) ; }
		@Override public Ast.Statement statement() { return wrap( m_astNode.getBody() ) ; }
	}
	private class IfStatementImpl extends ASTNodeWrapper<IfStatement> implements Ast.If {
		public IfStatementImpl(IfStatement node) { super(node) ; }
		@Override public Ast.Expression condition() { return wrap( m_astNode.getExpression() ) ; }
		@Override public Ast.Statement thenStatement() { return wrap( m_astNode.getThenStatement() ) ; }
		@Override public Ast.Statement elseStatement() { return wrap( m_astNode.getElseStatement() ) ; }
	}
	private class ImportDeclarationImpl extends ASTNodeWrapper<ImportDeclaration> implements Ast.Import {
		public ImportDeclarationImpl(ImportDeclaration node) { super(node) ; }
	}
	private class InfixExpressionImpl extends ASTNodeWrapper<InfixExpression> implements Ast.Binary {
		public InfixExpressionImpl(InfixExpression node) { super(node) ; }
		@Override public Ast.Expression leftOperand() { return wrap( m_astNode.getLeftOperand() ) ; }
		@Override public Ast.Expression rightOperand() { return wrap( m_astNode.getRightOperand() ) ; }
		@Override public Ast.Binary.OperationType operationType() {
			InfixExpression.Operator operator = m_astNode.getOperator() ;
			if ( operator == InfixExpression.Operator.PLUS )
				return Ast.Binary.OperationType.PLUS ;
			else if ( operator == InfixExpression.Operator.MINUS )
				return Ast.Binary.OperationType.MINUS ;
			else if ( operator == InfixExpression.Operator.TIMES )
				return Ast.Binary.OperationType.MULTIPLY ;
			else if ( operator == InfixExpression.Operator.DIVIDE )
				return Ast.Binary.OperationType.DIVIDE ;
			else if ( operator == InfixExpression.Operator.LESS )
				return Ast.Binary.OperationType.LESS_THAN ;
			else if ( operator == InfixExpression.Operator.GREATER)
				return Ast.Binary.OperationType.GREATER_THAN ;
			else if ( operator == InfixExpression.Operator.LESS_EQUALS )
				return Ast.Binary.OperationType.LESS_THAN_EQUAL ;
			else if ( operator == InfixExpression.Operator.GREATER_EQUALS )
				return Ast.Binary.OperationType.GREATER_THAN_EQUAL ;
			else if ( operator == InfixExpression.Operator.EQUALS )
				return Ast.Binary.OperationType.EQUAL_TO ;
			else if ( operator == InfixExpression.Operator.NOT_EQUALS )
				return Ast.Binary.OperationType.NOT_EQUAL_TO ;
			else if ( operator == InfixExpression.Operator.REMAINDER )
				return Ast.Binary.OperationType.MODULO ;
			else
				return Ast.Binary.OperationType.UNKNOWN ;
		}
	}
//	private class InitializerImpl extends ASTNodeWrapper<Initializer> implements Ast.Initializer {
//		public InitializerImpl(Initializer node) { super(node) ; }
//	}
	private class InstanceofExpressionImpl extends ASTNodeWrapper<InstanceofExpression> implements Ast.InstanceOf {
		public InstanceofExpressionImpl(InstanceofExpression node) { super(node) ; }
	}
	private class IntersectionTypeImpl extends ASTNodeWrapper<IntersectionType> implements Ast.IntersectionType {
		public IntersectionTypeImpl(IntersectionType node) { super(node) ; }
	}
//	private class JavadocImpl extends ASTNodeWrapper<Javadoc> implements Ast.Javadoc {
//		public JavadocImpl(Javadoc node) { super(node) ; }
//	}
	private class LabeledStatementImpl extends ASTNodeWrapper<LabeledStatement> implements Ast.LabeledStatement {
		public LabeledStatementImpl(LabeledStatement node) { super(node) ; }
	}
	private class LambdaExpressionImpl extends ASTNodeWrapper<LambdaExpression> implements Ast.LambdaExpression {
		public LambdaExpressionImpl(LambdaExpression node) { super(node) ; }
	}
//	private class LineCommentImpl extends ASTNodeWrapper<LineComment> implements Ast.LineComment {
//		public LineCommentImpl(LineComment node) { super(node) ; }
//	}
//	private class MarkerAnnotationImpl extends ASTNodeWrapper<MarkerAnnotation> implements Ast.MarkerAnnotation {
//		public MarkerAnnotationImpl(MarkerAnnotation node) { super(node) ; }
//	}
//	private class MemberRefImpl extends ASTNodeWrapper<MemberRef> implements Ast.MemberRef {
//		public MemberRefImpl(MemberRef node) { super(node) ; }
//	}
//	private class MemberValuePairImpl extends ASTNodeWrapper<MemberValuePair> implements Ast.MemberValuePair {
//		public MemberValuePairImpl(MemberValuePair node) { super(node) ; }
//	}
//	private class MethodRefImpl extends ASTNodeWrapper<MethodRef> implements Ast.MethodRef {
//		public MethodRefImpl(MethodRef node) { super(node) ; }
//	}
//	private class MethodRefParameterImpl extends ASTNodeWrapper<MethodRefParameter> implements Ast.MethodRefParameter {
//		public MethodRefParameterImpl(MethodRefParameter node) { super(node) ; }
//	}
	private class MethodDeclarationImpl extends ASTNodeWrapper<MethodDeclaration> implements Ast.Method {
		public MethodDeclarationImpl(MethodDeclaration node) { super(node) ; }
		@Override public Ast.Block body() { return wrap( m_astNode.getBody() ) ; }
		@Override public TypeMirror returnType() { return elements().getTypeElement( m_astNode.getReturnType2().resolveBinding().getQualifiedName() ).asType() ; }
		@Override public List<? extends Ast.Variable> parameters() { return wrapVariables( m_astNode.parameters() ) ; }
	}
	private class MethodInvocationImpl extends ASTNodeWrapper<MethodInvocation> implements Ast.MethodInvocation {
		public MethodInvocationImpl(MethodInvocation node) { super(node) ; }
		@Override public List<? extends Ast.Expression> arguments() { return wrapExpressions( m_astNode.arguments() ) ; }
		@Override public Ast.Expression methodSelect() { return wrap( m_astNode.getExpression() ) ; }
	}
//	private class ModifierImpl extends ASTNodeWrapper<Modifier> implements Ast.Modifier {
//		public ModifierImpl(Modifier node) { super(node) ; }
//	}
//	private class NameQualifiedTypeImpl extends ASTNodeWrapper<NameQualifiedType> implements Ast.NameQualifiedType {
//		public NameQualifiedTypeImpl(NameQualifiedType node) { super(node) ; }
//	}
//	private class NormalAnnotationImpl extends ASTNodeWrapper<NormalAnnotation> implements Ast.NormalAnnotation {
//		public NormalAnnotationImpl(NormalAnnotation node) { super(node) ; }
//	}
	private class NullLiteralImpl extends LiteralWrapper<NullLiteral> {
		public NullLiteralImpl(NullLiteral node) { super(node) ; }
		@Override public Ast.Literal.LiteralType literalType() { return Ast.Literal.LiteralType.NULL_LITERAL ; }
		@Override public Object value() { return null ; }
	}
	private class NumberLiteralImpl extends LiteralWrapper<NumberLiteral> implements Ast.Literal {
		public NumberLiteralImpl(NumberLiteral node) { super(node) ; }
		@Override public Ast.Literal.LiteralType literalType() {
			Object value = m_astNode.resolveConstantExpressionValue() ;
			// possibly check m_astNode.resolveTypeBinding()
			if ( value instanceof Integer )
				return Ast.Literal.LiteralType.INT_LITERAL ;
			else if ( value instanceof Long )
				return Ast.Literal.LiteralType.LONG_LITERAL ;
			else if ( value instanceof Float )
				return Ast.Literal.LiteralType.FLOAT_LITERAL ;
			else if ( value instanceof Double )
				return Ast.Literal.LiteralType.DOUBLE_LITERAL ;
			else
				return Ast.Literal.LiteralType.UNKNOWN;
		}
		
	}
//	private class PackageDeclarationImpl extends ASTNodeWrapper<PackageDeclaration> implements Ast.PackageDeclaration {
//		public PackageDeclarationImpl(PackageDeclaration node) { super(node) ; }
//	}
	private class ParameterizedTypeImpl extends ASTNodeWrapper<ParameterizedType> implements Ast.ParameterizedType {
		public ParameterizedTypeImpl(ParameterizedType node) { super(node) ; }
	}
	private class ParenthesizedExpressionImpl extends ASTNodeWrapper<ParenthesizedExpression> implements Ast.Parenthesized {
		public ParenthesizedExpressionImpl(ParenthesizedExpression node) { super(node) ; }
		@Override public Ast.Expression expression() { return wrap( m_astNode.getExpression() ) ; }
	}
	private class PostfixExpressionImpl extends UnaryWrapper<PostfixExpression> {
		public PostfixExpressionImpl(PostfixExpression node) { super(node) ; }
		@Override public Ast.Expression expression() { return wrap( m_astNode.getOperand() ) ; }
		@Override public OperationType operationType() {
			PostfixExpression.Operator operator = m_astNode.getOperator();
			if ( operator == PostfixExpression.Operator.DECREMENT )
				return Ast.Unary.OperationType.POSTFIX_DECREMENT ; 
			else if ( operator == PostfixExpression.Operator.INCREMENT )
				return Ast.Unary.OperationType.POSTFIX_INCREMENT ; 
			else
				return Ast.Unary.OperationType.UNKNOWN ; 
		}
	}
	private class PrefixExpressionImpl extends UnaryWrapper<PrefixExpression> {
		public PrefixExpressionImpl(PrefixExpression node) { super(node) ; }
		@Override public Ast.Expression expression() { return wrap( m_astNode.getOperand() ) ; }
		@Override public OperationType operationType() {
			PrefixExpression.Operator operator = m_astNode.getOperator();
			if ( operator == PrefixExpression.Operator.DECREMENT )
				return Ast.Unary.OperationType.PREFIX_DECREMENT ; 
			else if ( operator == PrefixExpression.Operator.INCREMENT )
				return Ast.Unary.OperationType.PREFIX_INCREMENT ; 
			else if ( operator == PrefixExpression.Operator.COMPLEMENT )
				return Ast.Unary.OperationType.LOGICAL_COMPLEMENT ; 
			else if ( operator == PrefixExpression.Operator.NOT )
				return Ast.Unary.OperationType.LOGICAL_COMPLEMENT ; 
			else if ( operator == PrefixExpression.Operator.MINUS )
				return Ast.Unary.OperationType.UNARY_MINUS ; 
			else if ( operator == PrefixExpression.Operator.PLUS )
				return Ast.Unary.OperationType.UNARY_PLUS ; 
			else
				return Ast.Unary.OperationType.UNKNOWN ; 
		}
	}
	private class PrimitiveTypeImpl extends ASTNodeWrapper<PrimitiveType> implements Ast.PrimitiveType {
		public PrimitiveTypeImpl(PrimitiveType node) { super(node) ; }
	}
	private class QualifiedNameImpl extends ASTNodeWrapper<QualifiedName> implements Ast.Identifier {
		public QualifiedNameImpl(QualifiedName node) { super(node) ; }
		@Override public Ast.Name name() { return new Ast.Name( m_astNode.getName().getIdentifier() ) ; }
	}
//	private class QualifiedTypeImpl extends ASTNodeWrapper<QualifiedType> implements Ast.QualifiedType {
//		public QualifiedTypeImpl(QualifiedType node) { super(node) ; }
//	}
	private class ReturnStatementImpl extends ASTNodeWrapper<ReturnStatement> implements Ast.Return {
		public ReturnStatementImpl(ReturnStatement node) { super(node) ; }
		@Override public Ast.Expression expression() { return wrap( m_astNode.getExpression() ) ; }
	}
	private class SimpleNameImpl extends ASTNodeWrapper<SimpleName> implements Ast.Identifier {
		public SimpleNameImpl(SimpleName node) { super(node) ; }
		@Override public Ast.Name name() { return new Ast.Name( m_astNode.getIdentifier() ) ; }
	}
//	private class SimpleTypeImpl extends ASTNodeWrapper<SimpleType> implements Ast.SimpleType {
//		public SimpleTypeImpl(SimpleType node) { super(node) ; }
//	}
//	private class SingleMemberAnnotationImpl extends ASTNodeWrapper<SingleMemberAnnotation> implements Ast.SingleMemberAnnotation {
//		public SingleMemberAnnotationImpl(SingleMemberAnnotation node) { super(node) ; }
//	}
	private class SingleVariableDeclarationImpl extends ASTNodeWrapper<SingleVariableDeclaration> implements Ast.Variable {
		public SingleVariableDeclarationImpl(SingleVariableDeclaration node) { super(node) ; }
		@Override public Ast.Expression initializer() { return wrap( m_astNode.getInitializer() ) ; }
		@Override public Ast.Name name() { return wrap( m_astNode.getName().getFullyQualifiedName() ) ; }
		@Override public TypeMirror type() { return elements().getTypeElement( m_astNode.getType().resolveBinding().getQualifiedName() ).asType() ; }
	}
	private class StringLiteralImpl extends LiteralWrapper<StringLiteral> {
		public StringLiteralImpl(StringLiteral node) { super(node) ; }
		@Override public Ast.Literal.LiteralType literalType() { return Ast.Literal.LiteralType.STRING_LITERAL ; }
	}
//	private class SuperConstructorInvocationImpl extends ASTNodeWrapper<SuperConstructorInvocation> implements Ast.SuperConstructorInvocation {
//		public SuperConstructorInvocationImpl(SuperConstructorInvocation node) { super(node) ; }
//	}
//	private class SuperFieldAccessImpl extends ASTNodeWrapper<SuperFieldAccess> implements Ast.SuperFieldAccess {
//		public SuperFieldAccessImpl(SuperFieldAccess node) { super(node) ; }
//	}
//	private class SuperMethodInvocationImpl extends ASTNodeWrapper<SuperMethodInvocation> implements Ast.SuperMethodInvocation {
//		public SuperMethodInvocationImpl(SuperMethodInvocation node) { super(node) ; }
//	}
	private class SuperMethodReferenceImpl extends MethodReferenceWrapper<SuperMethodReference> {
		public SuperMethodReferenceImpl(SuperMethodReference node) { super(node) ; }
	}
	private class SwitchCaseImpl extends ASTNodeWrapper<SwitchCase> implements Ast.Case {
		public SwitchCaseImpl(SwitchCase node) { super(node) ; }
	}
	private class SwitchStatementImpl extends ASTNodeWrapper<SwitchStatement> implements Ast.Switch {
		public SwitchStatementImpl(SwitchStatement node) { super(node) ; }
	}
	private class SynchronizedStatementImpl extends ASTNodeWrapper<SynchronizedStatement> implements Ast.Synchronized {
		public SynchronizedStatementImpl(SynchronizedStatement node) { super(node) ; }
	}
//	private class TagElementImpl extends ASTNodeWrapper<TagElement> implements Ast.TagElement {
//		public TagElementImpl(TagElement node) { super(node) ; }
//	}
//	private class TextElementImpl extends ASTNodeWrapper<TextElement> implements Ast.TextElement {
//		public TextElementImpl(TextElement node) { super(node) ; }
//	}
//	private class ThisExpressionImpl extends ASTNodeWrapper<ThisExpression> implements Ast.ThisExpression {
//		public ThisExpressionImpl(ThisExpression node) { super(node) ; }
//	}
	private class ThrowStatementImpl extends ASTNodeWrapper<ThrowStatement> implements Ast.Throw {
		public ThrowStatementImpl(ThrowStatement node) { super(node) ; }
	}
	private class TryStatementImpl extends ASTNodeWrapper<TryStatement> implements Ast.Try {
		public TryStatementImpl(TryStatement node) { super(node) ; }
	}
//	private class TypeDeclarationImpl extends ASTNodeWrapper<TypeDeclaration> implements Ast.TypeDeclaration {
//		public TypeDeclarationImpl(TypeDeclaration node) { super(node) ; }
//	}
//	private class TypeDeclarationStatementImpl extends ASTNodeWrapper<TypeDeclarationStatement> implements Ast.TypeDeclarationStatement {
//		public TypeDeclarationStatementImpl(TypeDeclarationStatement node) { super(node) ; }
//	}
//	private class TypeLiteralImpl extends ASTNodeWrapper<TypeLiteral> implements Ast.TypeLiteral {
//		public TypeLiteralImpl(TypeLiteral node) { super(node) ; }
//	}
	private class TypeMethodReferenceImpl extends MethodReferenceWrapper<TypeMethodReference> {
		public TypeMethodReferenceImpl(TypeMethodReference node) { super(node) ; }
	}
	private class TypeParameterImpl extends ASTNodeWrapper<TypeParameter> implements Ast.TypeParameter {
		public TypeParameterImpl(TypeParameter node) { super(node) ; }
	}
	private class UnionTypeImpl extends ASTNodeWrapper<UnionType> implements Ast.UnionType {
		public UnionTypeImpl(UnionType node) { super(node) ; }
	}
	private class VariableDeclarationExpressionImpl extends ASTNodeWrapper<VariableDeclarationExpression> implements Ast.VariableDeclarationsExpression {
		public VariableDeclarationExpressionImpl(VariableDeclarationExpression node) { super(node) ; }
		@Override public List<? extends Ast.Variable> variables() { return wrapVariables( m_astNode.fragments() ) ; } 
	}
	private class VariableDeclarationStatementImpl extends ASTNodeWrapper<VariableDeclarationStatement> implements Ast.VariableDeclarationsStatement {
		public VariableDeclarationStatementImpl(VariableDeclarationStatement node) { super(node) ; }
		@Override public List<? extends Ast.Variable> variables() { return wrapVariables( m_astNode.fragments() ) ; } 
	}
	private class VariableDeclarationFragmentImpl extends ASTNodeWrapper<VariableDeclarationFragment> implements Ast.Variable {
		public VariableDeclarationFragmentImpl(VariableDeclarationFragment node) { super(node) ; }
		@Override public Ast.Name name() { return wrap( m_astNode.getName().getFullyQualifiedName() ); }
		@Override public Ast.Expression initializer() { return wrap( m_astNode.getInitializer() ) ; }
		@Override public TypeMirror type() { return resolveIdentifier( m_astNode.getName() ).type() ; }
	}
	private class WhileStatementImpl extends ASTNodeWrapper<WhileStatement> implements Ast.WhileLoop {
		public WhileStatementImpl(WhileStatement node) { super(node) ; }
		@Override public Ast.Expression condition() { return wrap( m_astNode.getExpression() ) ; }
		@Override public Ast.Statement statement() { return wrap( m_astNode.getBody() ) ; }
	}
	private class WildcardTypeImpl extends ASTNodeWrapper<WildcardType> implements Ast.Wildcard {
		public WildcardTypeImpl(WildcardType node) { super(node) ; }
	}
	
	private class MethodFinder extends ASTVisitor {
		private final javax.lang.model.element.Name m_methodName;
		private MethodDeclaration m_methodDeclaration = null ;
		public MethodFinder( javax.lang.model.element.Name methodName ) { m_methodName = methodName ; }
		public MethodDeclaration foundMethodDeclaration() { return m_methodDeclaration ; }
		
		@Override public boolean visit(MethodDeclaration node) {
			if ( m_methodName.contentEquals( node.getName().getIdentifier() ) )
				m_methodDeclaration = node ;
			return false ;
		}
	}

	@Override
	public Ast.Method getMethod(ExecutableElement methodElement) {
		// maybe use bindings here?
		MethodFinder methodFinder = new MethodFinder( methodElement.getSimpleName() ) ;
		m_compilationUnit.accept( methodFinder );
		return wrap( methodFinder.foundMethodDeclaration() ) ;
	}

	private Ast.Name wrap( String name ) { return name == null ? null : new Ast.Name(name) ; }

	private List<? extends Ast.Variable> wrapVariables( List<? extends VariableDeclarationFragment> variables ) {
		return variables.stream().map( (e)->wrap(e) ).collect(Collectors.toList()) ;
	}
	
	private List<? extends Ast.Statement> wrapStatements( List<? extends Statement> statements ) {
		return statements.stream().map( (e)->wrap(e) ).collect(Collectors.toList()) ;
	}

	private List<? extends Ast.ExpressionStatement> wrapExpressionStatements( List<? extends ExpressionStatement> expressionStatements ) {
		return expressionStatements.stream().map( (e)->wrap(e) ).collect(Collectors.toList()) ;
	}
	
	private List<? extends Ast.Expression> wrapExpressions( List<? extends Expression> expressions ) {
		return expressions.stream().map( (e)->wrap(e) ).collect(Collectors.toList()) ;
	}

	private Ast.ExpressionStatement wrapExpressionAsStatement( Expression expression ) {
		return new SyntheticExpressionStatement(expression) ;
	}

	private List<? extends Ast.ExpressionStatement> wrapExpressionsAsStatements( List<? extends Expression> expressions ) {
		return expressions.stream().map( (e)->wrapExpressionAsStatement(e) ).collect(Collectors.toList()) ;
	}
	
//	private Ast.AnnotationTypeDeclaration wrap( AnnotationTypeDeclaration node ) { return node == null ? null : new AnnotationTypeDeclarationImpl( node ) ; }
//	private Ast.AnnotationTypeMemberDeclaration wrap( AnnotationTypeMemberDeclaration node ) { return node == null ? null : new AnnotationTypeMemberDeclarationImpl( node ) ; }
//	private Ast.AnonymousClassDeclaration wrap( AnonymousClassDeclaration node ) { return node == null ? null : new AnonymousClassDeclarationImpl( node ) ; }
	private Ast.ArrayAccess wrap( ArrayAccess node ) { return node == null ? null : new ArrayAccessImpl( node ) ; }
//	private Ast.ArrayCreation wrap( ArrayCreation node ) { return node == null ? null : new ArrayCreationImpl( node ) ; }
//	private Ast.ArrayInitializer wrap( ArrayInitializer node ) { return node == null ? null : new ArrayInitializerImpl( node ) ; }
	private Ast.ArrayType wrap( ArrayType node ) { return node == null ? null : new ArrayTypeImpl( node ) ; }
	private Ast.Assert wrap( AssertStatement node ) { return node == null ? null : new AssertStatementImpl( node ) ; }
	private Ast.Assignment wrap( Assignment node ) { return node == null ? null : new AssignmentImpl( node ) ; }
	private Ast.Block wrap( Block node ) { return node == null ? null : new BlockImpl( node ) ; }
//	private Ast.BlockComment wrap( BlockComment node ) { return node == null ? null : new BlockCommentImpl( node ) ; }
	private Ast.Literal wrap( BooleanLiteral node ) { return node == null ? null : new BooleanLiteralImpl( node ) ; }
	private Ast.Break wrap( BreakStatement node ) { return node == null ? null : new BreakStatementImpl( node ) ; }
	private Ast.TypeCast wrap( CastExpression node ) { return node == null ? null : new CastExpressionImpl( node ) ; }
	private Ast.Catch wrap( CatchClause node ) { return node == null ? null : new CatchClauseImpl( node ) ; }
	private Ast.Literal wrap( CharacterLiteral node ) { return node == null ? null : new CharacterLiteralImpl( node ) ; }
//	private Ast.ClassInstanceCreation wrap( ClassInstanceCreation node ) { return node == null ? null : new ClassInstanceCreationImpl( node ) ; }
	private Ast.CompilationUnit wrap( CompilationUnit node ) { return node == null ? null : new CompilationUnitImpl( node ) ; }
	private Ast.ConditionalExpression wrap( ConditionalExpression node ) { return node == null ? null : new ConditionalExpressionImpl( node ) ; }
//	private Ast.ConstructorInvocation wrap( ConstructorInvocation node ) { return node == null ? null : new ConstructorInvocationImpl( node ) ; }
	private Ast.Continue wrap( ContinueStatement node ) { return node == null ? null : new ContinueStatementImpl( node ) ; }
	private Ast.MemberReference wrap( CreationReference node ) { return node == null ? null : new CreationReferenceImpl( node ) ; }
//	private Ast.Dimension wrap( Dimension node ) { return node == null ? null : new DimensionImpl( node ) ; }
	private Ast.DoWhileLoop wrap( DoStatement node ) { return node == null ? null : new DoStatementImpl( node ) ; }
	private Ast.EmptyStatement wrap( EmptyStatement node ) { return node == null ? null : new EmptyStatementImpl( node ) ; }
	private Ast.EnhancedForLoop wrap( EnhancedForStatement node ) { return node == null ? null : new EnhancedForStatementImpl( node ) ; }
//	private Ast.EnumConstantDeclaration wrap( EnumConstantDeclaration node ) { return node == null ? null : new EnumConstantDeclarationImpl( node ) ; }
//	private Ast.EnumDeclaration wrap( EnumDeclaration node ) { return node == null ? null : new EnumDeclarationImpl( node ) ; }
	private Ast.MemberReference wrap( ExpressionMethodReference node ) { return node == null ? null : new ExpressionMethodReferenceImpl( node ) ; }
	private Ast.ExpressionStatement wrap( ExpressionStatement node ) { return node == null ? null : new ExpressionStatementImpl( node ) ; }
//	private Ast.FieldAccess wrap( FieldAccess node ) { return node == null ? null : new FieldAccessImpl( node ) ; }
//	private Ast.FieldDeclaration wrap( FieldDeclaration node ) { return node == null ? null : new FieldDeclarationImpl( node ) ; }
	private Ast.ForLoop wrap( ForStatement node ) { return node == null ? null : new ForStatementImpl( node ) ; }
	private Ast.If wrap( IfStatement node ) { return node == null ? null : new IfStatementImpl( node ) ; }
	private Ast.Import wrap( ImportDeclaration node ) { return node == null ? null : new ImportDeclarationImpl( node ) ; }
	private Ast.Binary wrap( InfixExpression node ) { return node == null ? null : new InfixExpressionImpl( node ) ; }
//	private Ast.Initializer wrap( Initializer node ) { return node == null ? null : new InitializerImpl( node ) ; }
	private Ast.InstanceOf wrap( InstanceofExpression node ) { return node == null ? null : new InstanceofExpressionImpl( node ) ; }
	private Ast.IntersectionType wrap( IntersectionType node ) { return node == null ? null : new IntersectionTypeImpl( node ) ; }
//	private Ast.Javadoc wrap( Javadoc node ) { return node == null ? null : new JavadocImpl( node ) ; }
	private Ast.LabeledStatement wrap( LabeledStatement node ) { return node == null ? null : new LabeledStatementImpl( node ) ; }
	private Ast.LambdaExpression wrap( LambdaExpression node ) { return node == null ? null : new LambdaExpressionImpl( node ) ; }
//	private Ast.LineComment wrap( LineComment node ) { return node == null ? null : new LineCommentImpl( node ) ; }
//	private Ast.MarkerAnnotation wrap( MarkerAnnotation node ) { return node == null ? null : new MarkerAnnotationImpl( node ) ; }
//	private Ast.MemberRef wrap( MemberRef node ) { return node == null ? null : new MemberRefImpl( node ) ; }
//	private Ast.MemberValuePair wrap( MemberValuePair node ) { return node == null ? null : new MemberValuePairImpl( node ) ; }
//	private Ast.MethodRef wrap( MethodRef node ) { return node == null ? null : new MethodRefImpl( node ) ; }
//	private Ast.MethodRefParameter wrap( MethodRefParameter node ) { return node == null ? null : new MethodRefParameterImpl( node ) ; }
	private Ast.Method wrap( MethodDeclaration node ) { return node == null ? null : new MethodDeclarationImpl( node ) ; }
	private Ast.MethodInvocation wrap( MethodInvocation node ) { return node == null ? null : new MethodInvocationImpl( node ) ; }
//	private Ast.Modifier wrap( Modifier node ) { return node == null ? null : new ModifierImpl( node ) ; }
//	private Ast.NameQualifiedType wrap( NameQualifiedType node ) { return node == null ? null : new NameQualifiedTypeImpl( node ) ; }
//	private Ast.NormalAnnotation wrap( NormalAnnotation node ) { return node == null ? null : new NormalAnnotationImpl( node ) ; }
	private Ast.Literal wrap( NullLiteral node ) { return node == null ? null : new NullLiteralImpl( node ) ; }
	private Ast.Literal wrap( NumberLiteral node ) { return node == null ? null : new NumberLiteralImpl( node ) ; }
//	private Ast.PackageDeclaration wrap( PackageDeclaration node ) { return node == null ? null : new PackageDeclarationImpl( node ) ; }
	private Ast.ParameterizedType wrap( ParameterizedType node ) { return node == null ? null : new ParameterizedTypeImpl( node ) ; }
	private Ast.Parenthesized wrap( ParenthesizedExpression node ) { return node == null ? null : new ParenthesizedExpressionImpl( node ) ; }
	private Ast.Unary wrap( PostfixExpression node ) { return node == null ? null : new PostfixExpressionImpl( node ) ; }
	private Ast.Unary wrap( PrefixExpression node ) { return node == null ? null : new PrefixExpressionImpl( node ) ; }
	private Ast.PrimitiveType wrap( PrimitiveType node ) { return node == null ? null : new PrimitiveTypeImpl( node ) ; }
	private Ast.Identifier wrap( QualifiedName node ) { return node == null ? null : new QualifiedNameImpl( node ) ; }
//	private Ast.QualifiedType wrap( QualifiedType node ) { return node == null ? null : new QualifiedTypeImpl( node ) ; }
	private Ast.Return wrap( ReturnStatement node ) { return node == null ? null : new ReturnStatementImpl( node ) ; }
	private Ast.Identifier wrap( SimpleName node ) { return node == null ? null : new SimpleNameImpl( node ) ; }
//	private Ast.SimpleType wrap( SimpleType node ) { return node == null ? null : new SimpleTypeImpl( node ) ; }
//	private Ast.SingleMemberAnnotation wrap( SingleMemberAnnotation node ) { return node == null ? null : new SingleMemberAnnotationImpl( node ) ; }
	private Ast.Variable wrap( SingleVariableDeclaration node ) { return node == null ? null : new SingleVariableDeclarationImpl( node ) ; }
	private Ast.Literal wrap( StringLiteral node ) { return node == null ? null : new StringLiteralImpl( node ) ; }
//	private Ast.SuperConstructorInvocation wrap( SuperConstructorInvocation node ) { return node == null ? null : new SuperConstructorInvocationImpl( node ) ; }
//	private Ast.SuperFieldAccess wrap( SuperFieldAccess node ) { return node == null ? null : new SuperFieldAccessImpl( node ) ; }
//	private Ast.SuperMethodInvocation wrap( SuperMethodInvocation node ) { return node == null ? null : new SuperMethodInvocationImpl( node ) ; }
	private Ast.MemberReference wrap( SuperMethodReference node ) { return node == null ? null : new SuperMethodReferenceImpl( node ) ; }
	private Ast.Case wrap( SwitchCase node ) { return node == null ? null : new SwitchCaseImpl( node ) ; }
	private Ast.Switch wrap( SwitchStatement node ) { return node == null ? null : new SwitchStatementImpl( node ) ; }
	private Ast.Synchronized wrap( SynchronizedStatement node ) { return node == null ? null : new SynchronizedStatementImpl( node ) ; }
//	private Ast.TagElement wrap( TagElement node ) { return node == null ? null : new TagElementImpl( node ) ; }
//	private Ast.TextElement wrap( TextElement node ) { return node == null ? null : new TextElementImpl( node ) ; }
//	private Ast.ThisExpression wrap( ThisExpression node ) { return node == null ? null : new ThisExpressionImpl( node ) ; }
	private Ast.Throw wrap( ThrowStatement node ) { return node == null ? null : new ThrowStatementImpl( node ) ; }
	private Ast.Try wrap( TryStatement node ) { return node == null ? null : new TryStatementImpl( node ) ; }
//	private Ast.TypeDeclaration wrap( TypeDeclaration node ) { return node == null ? null : new TypeDeclarationImpl( node ) ; }
//	private Ast.TypeDeclarationStatement wrap( TypeDeclarationStatement node ) { return node == null ? null : new TypeDeclarationStatementImpl( node ) ; }
//	private Ast.TypeLiteral wrap( TypeLiteral node ) { return node == null ? null : new TypeLiteralImpl( node ) ; }
	private Ast.MemberReference wrap( TypeMethodReference node ) { return node == null ? null : new TypeMethodReferenceImpl( node ) ; }
	private Ast.TypeParameter wrap( TypeParameter node ) { return node == null ? null : new TypeParameterImpl( node ) ; }
	private Ast.UnionType wrap( UnionType node ) { return node == null ? null : new UnionTypeImpl( node ) ; }
	private Ast.VariableDeclarationsExpression wrap( VariableDeclarationExpression node ) { return node == null ? null : new VariableDeclarationExpressionImpl( node ) ; }
	private Ast.VariableDeclarationsStatement wrap( VariableDeclarationStatement node ) { return node == null ? null : new VariableDeclarationStatementImpl( node ) ; }
	private Ast.Variable wrap( VariableDeclarationFragment node ) { return node == null ? null : new VariableDeclarationFragmentImpl( node ) ; }
	private Ast.WhileLoop wrap( WhileStatement node ) { return node == null ? null : new WhileStatementImpl( node ) ; }
	private Ast.Wildcard wrap( WildcardType node ) { return node == null ? null : new WildcardTypeImpl( node ) ; }
	
	private Ast.Node wrap( ASTNode node ) {
		return node == null ? null : new ASTInterpreter<Ast.Node,Void>(RoutineJdtTools.this) {
			@Override public boolean visit(Statement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(Expression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(Annotation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(MethodReference node) { provide( wrap(node) ); return false ; }
			
			@Override public boolean visit(AnnotationTypeDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(AnnotationTypeMemberDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(AnonymousClassDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ArrayType node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(BlockComment node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(CatchClause node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(CompilationUnit node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(Dimension node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(EnumConstantDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(EnumDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(FieldDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ImportDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(Initializer node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(IntersectionType node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(Javadoc node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(LineComment node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(MemberRef node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(MemberValuePair node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(MethodRef node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(MethodRefParameter node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(MethodDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(Modifier node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(NameQualifiedType node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(PackageDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ParameterizedType node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(PrimitiveType node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(QualifiedName node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(QualifiedType node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SimpleName node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SimpleType node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SingleVariableDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(TagElement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(TextElement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(TypeDeclaration node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(TypeParameter node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(UnionType node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(VariableDeclarationFragment node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(WildcardType node) { provide( wrap(node) ); return false ; }
			
		}.acceptWithParameter(null, node) ;
	}
	
	private Ast.Annotation wrap( Annotation node ) {
		return node == null ? null : new ASTInterpreter<Ast.Annotation,Void>(RoutineJdtTools.this) {
			@Override public boolean visit(MarkerAnnotation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(NormalAnnotation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SingleMemberAnnotation node) { provide( wrap(node) ); return false ; }
		}.acceptWithParameter(null, node) ;
	}
	
	private Ast.Node wrap( MethodReference node ) {
		return node == null ? null : new ASTInterpreter<Ast.Node,Void>(RoutineJdtTools.this) {
			@Override public boolean visit(CreationReference node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ExpressionMethodReference node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SuperMethodReference node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(TypeMethodReference node) { provide( wrap(node) ); return false ; }
		}.acceptWithParameter(null, node) ;
	}
	
	private Ast.Statement wrap( Statement node ) {
		return node == null ? null : new ASTInterpreter<Ast.Statement,Void>(RoutineJdtTools.this) {
			@Override public boolean visit(AssertStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(Block node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(BreakStatement node) { provide( wrap(node) ); return false ; }
//			@Override public boolean visit(ConstructorInvocation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ContinueStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(DoStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(EmptyStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(EnhancedForStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ExpressionStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ForStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(IfStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(LabeledStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ReturnStatement node) { provide( wrap(node) ); return false ; }
//			@Override public boolean visit(SuperConstructorInvocation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SwitchCase node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SwitchStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SynchronizedStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ThrowStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(TryStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(VariableDeclarationStatement node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(WhileStatement node) { provide( wrap(node) ); return false ; }
		}.acceptWithParameter(null, node) ;
	}

	private Ast.Expression wrap( Expression node ) {
		return node == null ? null : new ASTInterpreter<Ast.Expression,Void>(RoutineJdtTools.this) {
			@Override public boolean visit(MarkerAnnotation node) { provide( wrap(node) ); return false ; } /* Annotation */
			@Override public boolean visit(NormalAnnotation node) { provide( wrap(node) ); return false ; } /* Annotation */
			@Override public boolean visit(SingleMemberAnnotation node) { provide( wrap(node) ); return false ; } /* Annotation */
			@Override public boolean visit(ArrayAccess node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ArrayCreation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ArrayInitializer node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(Assignment node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(BooleanLiteral node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(CastExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(CharacterLiteral node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ClassInstanceCreation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ConditionalExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(FieldAccess node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(InfixExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(InstanceofExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(LambdaExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(MethodInvocation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(CreationReference node) { provide( wrap(node) ); return false ; } /* MethodReference */
			@Override public boolean visit(ExpressionMethodReference node) { provide( wrap(node) ); return false ; } /* MethodReference */
			@Override public boolean visit(SuperMethodReference node) { provide( wrap(node) ); return false ; } /* MethodReference */
			@Override public boolean visit(TypeMethodReference node) { provide( wrap(node) ); return false ; } /* MethodReference */
			@Override public boolean visit(SimpleName node) { provide( wrap(node) ); return false ; } /* Name */
			@Override public boolean visit(QualifiedName node) { provide( wrap(node) ); return false ; } /* Name */
			@Override public boolean visit(NullLiteral node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(NumberLiteral node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ParenthesizedExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(PostfixExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(PrefixExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(StringLiteral node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SuperFieldAccess node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(SuperMethodInvocation node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(ThisExpression node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(TypeLiteral node) { provide( wrap(node) ); return false ; }
			@Override public boolean visit(VariableDeclarationExpression node) { provide( wrap(node) ); return false ; }
		}.acceptWithParameter(null, node) ;
	}

}
