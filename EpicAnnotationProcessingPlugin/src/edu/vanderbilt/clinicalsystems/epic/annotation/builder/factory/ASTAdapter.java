package edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory;

import org.eclipse.jdt.core.dom.*;

public class ASTAdapter<R,P> extends ASTVisitor {
	private R m_result = null ;
	public ASTAdapter() { super(true) ; }
	
	public R result() { return m_result ; }
	protected void provide( R result ) {
		if ( null != m_result )
			throw new IllegalStateException( "ASTAdapter cannot return more than one result" ) ;
		m_result = result ;
	}

	private final ThreadLocal<P> tl_parameter = new ThreadLocal<P>() ;
	protected P parameter() { return tl_parameter.get() ; }
	
	public R acceptWithParameter( P parameter, ASTNode acceptNode ) {
		try {
			tl_parameter.set(parameter) ;			
			acceptNode.accept(this);
		} finally {
			tl_parameter.set(null) ;			
		}
		return result() ;
	}
	
	protected RuntimeException unsupportedAstNodeTypeException(ASTNode node) {
		return new UnsupportedOperationException(node.getClass().getSimpleName() + " not supported here") ;
	}
	
	protected R unsupportedAstNodeTypeResult(ASTNode node) {
		throw unsupportedAstNodeTypeException(node);
	}
	
	public boolean visit(ASTNode node) { provide( unsupportedAstNodeTypeResult(node) ) ; return false ; }
	public boolean visit(Statement node) { return visit((ASTNode)node) ; }
	public boolean visit(Expression node) { return visit((ASTNode)node) ; }

	public boolean visit(Annotation node) { return visit((Expression)node) ; }
	public boolean visit(MethodReference node) { return visit((Expression)node) ; }
	public boolean visit(Name node) { return visit((Expression)node) ; }
	
	@Override public boolean visit(AnnotationTypeDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(AnnotationTypeMemberDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(AnonymousClassDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(ArrayAccess node) { return visit((Expression)node) ; }
	@Override public boolean visit(ArrayCreation node) { return visit((Expression)node) ; }
	@Override public boolean visit(ArrayInitializer node) { return visit((Expression)node) ; }
	@Override public boolean visit(ArrayType node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(AssertStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(Assignment node) { return visit((Expression)node) ; }
	@Override public boolean visit(Block node) { return visit((Statement)node) ; }
	@Override public boolean visit(BlockComment node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(BooleanLiteral node) { return visit((Expression)node) ; }
	@Override public boolean visit(BreakStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(CastExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(CatchClause node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(CharacterLiteral node) { return visit((Expression)node) ; }
	@Override public boolean visit(ClassInstanceCreation node) { return visit((Expression)node) ; }
	@Override public boolean visit(CompilationUnit node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(ConditionalExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(ConstructorInvocation node) { return visit((Statement)node) ; }
	@Override public boolean visit(ContinueStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(CreationReference node) { return visit((MethodReference)node) ; }
	@Override public boolean visit(Dimension node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(DoStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(EmptyStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(EnhancedForStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(EnumConstantDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(EnumDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(ExpressionMethodReference node) { return visit((MethodReference)node) ; }
	@Override public boolean visit(ExpressionStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(FieldAccess node) { return visit((Expression)node) ; }
	@Override public boolean visit(FieldDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(ForStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(IfStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(ImportDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(InfixExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(Initializer node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(InstanceofExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(IntersectionType node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(Javadoc node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(LabeledStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(LambdaExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(LineComment node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(MarkerAnnotation node) { return visit((Annotation)node) ; }
	@Override public boolean visit(MemberRef node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(MemberValuePair node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(MethodRef node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(MethodRefParameter node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(MethodDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(MethodInvocation node) { return visit((Expression)node) ; }
	@Override public boolean visit(Modifier node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(NameQualifiedType node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(NormalAnnotation node) { return visit((Annotation)node) ; }
	@Override public boolean visit(NullLiteral node) { return visit((Expression)node) ; }
	@Override public boolean visit(NumberLiteral node) { return visit((Expression)node) ; }
	@Override public boolean visit(PackageDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(ParameterizedType node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(ParenthesizedExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(PostfixExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(PrefixExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(PrimitiveType node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(QualifiedName node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(QualifiedType node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(ReturnStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(SimpleName node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(SimpleType node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(SingleMemberAnnotation node) { return visit((Annotation)node) ; }
	@Override public boolean visit(SingleVariableDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(StringLiteral node) { return visit((Expression)node) ; }
	@Override public boolean visit(SuperConstructorInvocation node) { return visit((Statement)node) ; }
	@Override public boolean visit(SuperFieldAccess node) { return visit((Expression)node) ; }
	@Override public boolean visit(SuperMethodInvocation node) { return visit((Expression)node) ; }
	@Override public boolean visit(SuperMethodReference node) { return visit((MethodReference)node) ; }
	@Override public boolean visit(SwitchCase node) { return visit((Statement)node) ; }
	@Override public boolean visit(SwitchStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(SynchronizedStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(TagElement node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(TextElement node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(ThisExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(ThrowStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(TryStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(TypeDeclaration node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(TypeDeclarationStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(TypeLiteral node) { return visit((Expression)node) ; }
	@Override public boolean visit(TypeMethodReference node) { return visit((MethodReference)node) ; }
	@Override public boolean visit(TypeParameter node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(UnionType node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(VariableDeclarationExpression node) { return visit((Expression)node) ; }
	@Override public boolean visit(VariableDeclarationStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(VariableDeclarationFragment node) { return visit((ASTNode)node) ; }
	@Override public boolean visit(WhileStatement node) { return visit((Statement)node) ; }
	@Override public boolean visit(WildcardType node) { return visit((ASTNode)node) ; }
	
}
