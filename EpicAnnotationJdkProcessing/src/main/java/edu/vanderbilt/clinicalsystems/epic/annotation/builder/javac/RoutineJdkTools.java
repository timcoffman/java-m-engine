package edu.vanderbilt.clinicalsystems.epic.annotation.builder.javac;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.AssertTree;
import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.BreakTree;
import com.sun.source.tree.CaseTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.ContinueTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EmptyStatementTree;
import com.sun.source.tree.EnhancedForLoopTree;
import com.sun.source.tree.ErroneousTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.ImportTree;
import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.IntersectionTypeTree;
import com.sun.source.tree.LabeledStatementTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.MemberReferenceTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ParameterizedTypeTree;
import com.sun.source.tree.ParenthesizedTree;
import com.sun.source.tree.PrimitiveTypeTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Scope;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.SwitchTree;
import com.sun.source.tree.SynchronizedTree;
import com.sun.source.tree.ThrowTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TreeVisitor;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.TypeCastTree;
import com.sun.source.tree.TypeParameterTree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.UnionTypeTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.WildcardTree;
import com.sun.source.util.TreePath;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast.Expression;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast.Statement;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast.VariableDeclarationsStatement;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.ElementInterpreter;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;

public class RoutineJdkTools extends RoutineTools {
	
	private final com.sun.source.util.Trees m_trees ;
	private CompilationUnitTree m_compilationUnit;
	
	public RoutineJdkTools(ProcessingEnvironment processingEnv,Element compilationElement) {
		super(processingEnv) ;
		m_trees = com.sun.source.util.Trees.instance(processingEnv);
		m_compilationUnit = m_trees.getPath(compilationElement).getCompilationUnit();
	}
	
	public com.sun.source.util.Trees trees() { return m_trees ; }

	public void report(ReportType reportType, String message, Tree tree) {
		if ( null == m_compilationUnit ) {
			report( reportType, message + " at " + tree.toString() );
		} else {
			m_trees.printMessage( reportType.kind(), message, tree, m_compilationUnit );
		}
	}

	@Override
	public boolean isPartOfCompilationUnit(Element element) {
		return null != m_trees.getTree(element) ;
	}

	@Override
	public boolean isTypeOfString(Ast.Expression node) {
		return isTypeOfString( determineType( unwrap( node ) ) ) ; 
	}

	private final class ExpressionTypeDeterminer extends TreeInterpreter<TypeMirror, Void> {
		
		public ExpressionTypeDeterminer() { super(RoutineJdkTools.this) ;}
		
//		@Override
//		protected TypeMirror unsupportedTreeTypeResult(Tree tree, Void parameter) {
//			return null;
//		}

		@Override
		public TypeMirror visitIdentifier(IdentifierTree identifierTree, Void parameter) {
			return determineTypeOfIdentifier( identifierTree ) ;
		}

		@Override
		public TypeMirror visitLiteral(LiteralTree literalTree, Void parameter) {
			return determineTypeOfLiteral(literalTree);
		}

		@Override
		public TypeMirror visitBinary(BinaryTree binaryTree, Void parameter) {
			switch( binaryTree.getKind() ) {
			case PLUS:
			case MINUS:
			case MULTIPLY:
			case DIVIDE:
				TypeMirror leftType = binaryTree.getLeftOperand().accept( this, parameter ) ;
				TypeMirror rightType = binaryTree.getRightOperand().accept( this, parameter ) ;
				return leftType ;
			default:				
				return super.visitBinary(binaryTree, parameter);
			}
		}
		
	}
	
	public TypeMirror determineType( ExpressionTree expressionTree ) {
		return expressionTree.accept( new ExpressionTypeDeterminer(), null ) ;
	}

	private TypeMirror determineTypeOfLiteral(LiteralTree literalTree) {
		switch (literalTree.getKind()) {
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
		
	@Override
	public IdentifierResolution resolveIdentifier( Ast.Identifier identifierNode ) {
		return resolveIdentifier( this.<IdentifierTree>unwrap(identifierNode) ) ;
	}
	
	@Override
	public IdentifierResolution resolveIdentifier( Ast.Variable variableNode ) {
		return resolveIdentifier( this.<VariableTree>unwrap(variableNode) ) ;
	}

	@Override
	public IdentifierResolution resolveIdentifier( Name name, Ast.Node node) {
		return resolveIdentifier( name, m_trees.getPath(m_compilationUnit, unwrap(node) ) ) ;
	}

	private IdentifierResolution resolveIdentifier( Tree tree ) {
		return tree.accept( new TreeInterpreter<IdentifierResolution, Void>(this) {

			@Override
			public IdentifierResolution visitIdentifier( IdentifierTree identifierTree, Void parameter ) {
				return resolveIdentifier(identifierTree);
			}
			
			@Override
			public IdentifierResolution visitVariable( VariableTree variableTree, Void parameter ) {
				return resolveIdentifier(variableTree);
			}
			
			@Override
			public IdentifierResolution visitPrimitiveType( PrimitiveTypeTree primitiveTypeTree, Void parameter ) {
				return new PrimitiveTypeResolutionImpl(primitiveTypeTree);
			}

			@Override
			public IdentifierResolution visitMemberSelect(MemberSelectTree memberSelectTree, Void parameter) {
				/* this is a package access */
				TypeElement typeElement = elements().getTypeElement( memberSelectTree.toString() ) ;
				return resolutionForTypeElement( typeElement ) ;
			}
			
			
		}, null) ;
	}

	private IdentifierResolution resolveIdentifier( IdentifierTree identifierTree ) {
		return resolveIdentifier( identifierTree.getName(), m_trees.getPath(m_compilationUnit,identifierTree) ) ;
	}
	
	private IdentifierResolution resolveIdentifier( VariableTree variableTree ) {
		return new VariableResolutionImpl( variableTree ) ;
	}
	
	private class PrimitiveTypeResolutionImpl implements TypeResolution {
		private final PrimitiveTypeTree m_primitiveTypeTree ;
		public PrimitiveTypeResolutionImpl( PrimitiveTypeTree primitiveTypeTree ) { m_primitiveTypeTree = primitiveTypeTree ; }
		@Override public IdentifierResolution.Kind kind() { return IdentifierResolution.Kind.TYPE ; }
		@Override public TypeMirror type() { return selfType() ; }
		@Override public TypeMirror selfType() { return types().getPrimitiveType( m_primitiveTypeTree.getPrimitiveTypeKind() ) ; }
		@Override public <R,P> R accept( Visitor<R,P> visitor, P parameter ) { return visitor.visit(this, parameter) ; }
	}
	
	private class VariableResolutionImpl implements VariableResolution {
		private final VariableTree m_variableTree ;
		public VariableResolutionImpl( VariableTree variableTree ) { m_variableTree = variableTree ; }
		@Override public IdentifierResolution.Kind kind() { return IdentifierResolution.Kind.VARIABLE ; }
		@Override public TypeMirror type() { return declaredType() ; }
		@Override public TypeMirror declaredType() {
			TypeResolution typeResolution = (TypeResolution)resolveIdentifier( m_variableTree.getType() );
			return typeResolution.type() ;
		}
		@Override public Object constantValue() {
			Set<Modifier> flags = m_variableTree.getModifiers().getFlags() ;
			if ( !flags.contains( Modifier.FINAL ) )
				return null ;
			return m_variableTree.getInitializer().accept( new TreeInterpreter<Object,Void>(RoutineJdkTools.this) {
				@Override protected Object unsupportedTreeTypeResult(Tree tree, Void parameter) {
					return null ;
				}
				@Override public Object visitLiteral(LiteralTree literalTree, Void parameter) {
					return literalTree.getValue() ;
				}
				
			},null) ;
		}
		@Override public <R,P> R accept( Visitor<R,P> visitor, P parameter ) { return visitor.visit(this, parameter) ; }
	}
	
	private class MethodResolutionImpl implements MethodResolution {
		private final MethodTree m_methodTree ;
		public MethodResolutionImpl( MethodTree methodTree ) { m_methodTree = methodTree ; }
		@Override public IdentifierResolution.Kind kind() { return IdentifierResolution.Kind.METHOD ; }
		@Override public ExecutableElement declaration() { return (ExecutableElement) m_trees.getElement( m_trees.getPath(m_compilationUnit,m_methodTree) ) ; }
		@Override public TypeMirror type() { return returnType() ; }
		@Override public TypeMirror returnType() { return m_trees.getTypeMirror( m_trees.getPath(m_compilationUnit,m_methodTree.getReturnType()) ) ; }
		@Override public <R,P> R accept( Visitor<R,P> visitor, P parameter ) { return visitor.visit(this, parameter) ; }
	}
	
	@Override
	protected VariableResolution resolutionForVariableElement( VariableElement variableElement ) {
		Tree tree = m_trees.getTree( variableElement ) ;
		if ( null != tree )
			return new VariableResolutionImpl( (VariableTree) tree ) ;
		else
			return super.resolutionForVariableElement(variableElement) ;
		
//		Tree enclosingTree = m_trees.getTree( variableElement.getEnclosingElement() ) ; 
//		if ( null == enclosingTree ) // import
//			return super.resolutionForVariableElement(variableElement) ;
//		
//		return enclosingTree.accept(new TreeInterpreter<VariableResolution,Name>(RoutineJdkTools.this) {
//
//			@Override public VariableResolution visitMethod( MethodTree methodTree, Name name) {
//				
//				for ( VariableTree parameter : methodTree.getParameters() )
//					if ( name.contentEquals( parameter.getName() ) )
//						return new VariableResolutionImpl( parameter ) ;
//				
//				for (StatementTree statementTree : methodTree.getBody().getStatements()) {
//					
//					VariableResolution variableResolution = statementTree.accept( new TreeInterpreter<VariableResolution,Name>(RoutineJdkTools.this) {
//						
//						@Override protected VariableResolution unsupportedTreeTypeResult( Tree tree, Name parameter) {
//							return null ;
//						}
//
//						@Override public VariableResolution visitVariable( VariableTree variableTree, Name name) {
//							if ( name.contentEquals( variableTree.getName() ) )
//								return new VariableResolutionImpl(variableTree) ;
//							else
//								return null ; 
//						}
//						
//					}, name) ;
//					if ( null != variableResolution )
//						return variableResolution ;
//				}
//				return null ;
//			}
//		}, variableElement.getSimpleName() ) ;
	}

	private IdentifierResolution resolveIdentifier( Name name, TreePath path ) {
		Scope scope = m_trees.getScope( path ) ;
		for ( Element localElement : scope.getLocalElements() )
			if ( name.equals( localElement.getSimpleName() ) ) {
				return localElement.accept( new ElementInterpreter<IdentifierResolution,Void>(this) {

					@Override public IdentifierResolution visitExecutable(ExecutableElement methodElement, Void parameter) {
						return resolutionForExecutableElement( methodElement ) ;
					}
					
					@Override public IdentifierResolution visitVariable(VariableElement variableElement, Void parameter) {
						return resolutionForVariableElement( variableElement ) ;
					}

					@Override
					public IdentifierResolution visitType(TypeElement typeElement, Void p) {
						return resolutionForTypeElement( typeElement ) ;
					}

					
					
				}, null) ;
			}
		
		for ( Element localElement : scope.getLocalElements() )
			if ( "this".equals( localElement.getSimpleName().toString() ) ) {
				IdentifierResolution resolution = resolveIdentifier( name, localElement.asType() ) ;
				if ( null != resolution )
					return resolution ;
			}
		
		TreePath parentPath = path.getParentPath() ;
		if ( null != parentPath ) {
			
			return resolveIdentifier( name, parentPath ) ;
			
		} else {
		
			for ( String packageName : new String[] { "java.lang", path.getCompilationUnit().getPackageName().toString() } ) {
				TypeElement implicitlyImportedElement = elements().getTypeElement( packageName + "." + name) ;
				if ( null != implicitlyImportedElement )
					return resolutionForTypeElement( implicitlyImportedElement ) ;
			}
			throw new IllegalArgumentException( "\"" + name + "\" not implicitly imported" ) ; 
		}
	}
	
	private TypeMirror determineTypeOfIdentifier( IdentifierTree identifierTree ) {
		return determineTypeOfName( identifierTree.getName(), m_trees.getPath(m_compilationUnit, identifierTree ) ) ;
	}

	private TypeMirror determineTypeOfName( Name name, TreePath path ) {
		Scope scope = m_trees.getScope( path ) ;
		for ( Element localElement : scope.getLocalElements() ) {
			if ( "this".equals( localElement.getSimpleName().toString() ) ) {
				// check for member access
				TypeMirror typeMethod = determineTypeOfName( name, localElement.asType() ) ;
				if ( null != typeMethod )
					return typeMethod ;
			} else if ( name.equals( localElement.getSimpleName() ) ) {
				return localElement.asType() ;
			}
		}
		TreePath parentPath = path.getParentPath() ;
		if ( null != parentPath )
			return determineTypeOfName( name, parentPath ) ;
		
		TypeElement implicitlyImportedElement ;
		implicitlyImportedElement = elements().getTypeElement("java.lang." + name) ;
		if ( null != implicitlyImportedElement )
			return implicitlyImportedElement.asType() ;
		
		return null ;
	}

	@Override
	public MethodResolution resolveMethodInvocationTarget( Ast.MethodInvocation methodInvocationNode ) {
		return resolveMethodInvocationTarget( this.<MethodInvocationTree>unwrap( methodInvocationNode ) ) ;
	}

	private MethodResolution resolveMethodInvocationTarget(MethodInvocationTree methodInvocationTree) {
		return methodInvocationTree.getMethodSelect().accept( new TreeInterpreter<MethodResolution, Void>(this) {

			@Override
			public MethodResolution visitIdentifier(IdentifierTree identifierTree, Void parameter) {
				/* identifier of a local method, without a target */
				IdentifierResolution resolvedIdentifier = resolveIdentifier(identifierTree) ;
				return (MethodResolution)resolvedIdentifier ;
			}

			@Override
			public MethodResolution visitMemberSelect(MemberSelectTree memberSelectTree, Void parameter) {
				TypeMirror targetType = memberSelectTree.getExpression().accept( new TreeInterpreter<TypeMirror,Void>(RoutineJdkTools.this) {

					@Override
					public TypeMirror visitIdentifier( IdentifierTree identifierTree, Void parameter) {
						IdentifierResolution targetElement = resolveIdentifier( identifierTree ) ;
						return targetElement.type() ;
					}

					@Override
					public TypeMirror visitMethodInvocation( MethodInvocationTree methodInvocationTree, Void parameter) {
						return resolveMethodInvocationTarget( methodInvocationTree ).type() ;
					}
					
				},null) ;
				
				IdentifierResolution resolvedIdentifier = resolveIdentifier( memberSelectTree.getIdentifier(), targetType ) ;
				return (MethodResolution)resolvedIdentifier;
			}
			
			
			
		}, null) ;
	}

	private <T extends Tree> T unwrap( Ast.Node node ) {
		if ( !(node instanceof TreeWrapper) )
			throw new IllegalArgumentException( this.getClass().getSimpleName() + " cannot operate on " + node.getClass().getSimpleName() + " which it did not create" ) ;
		@SuppressWarnings("unchecked")
		TreeWrapper<T> treeWrapper = (TreeWrapper<T>)node ;
		return treeWrapper.tree() ;
	}

	private class TreeWrapper<T extends Tree> implements Ast.Node {
		protected final T m_tree ;
		public TreeWrapper(T tree) { m_tree = tree ; }
		public T tree() { return m_tree ; }
		@Override public void report( ReportType reportType, String message ) { RoutineJdkTools.this.report(reportType, message, m_tree) ; }
		@Override public String toString() { return m_tree.toString() ; }
		@Override public <R, P> R accept(Ast.Visitor<R, P> visitor, P parameter) {
			return m_tree.accept(new TreeVisitor<R,P>() {
				@Override public R visitAnnotatedType(AnnotatedTypeTree tree, P parameter) { return visitor.visitAnnotatedType( wrap(tree), parameter ); }
				@Override public R visitAnnotation(AnnotationTree tree, P parameter) { return visitor.visitAnnotation( wrap(tree), parameter ); }
				@Override public R visitArrayAccess(ArrayAccessTree tree, P parameter) { return visitor.visitArrayAccess( wrap(tree), parameter ); }
				@Override public R visitArrayType(ArrayTypeTree tree, P parameter) { return visitor.visitArrayType( wrap(tree), parameter ); }
				@Override public R visitAssert(AssertTree tree, P parameter) { return visitor.visitAssert( wrap(tree), parameter ); }
				@Override public R visitAssignment(AssignmentTree tree, P parameter) { return visitor.visitAssignment( wrap(tree), parameter ); }
				@Override public R visitBinary(BinaryTree tree, P parameter) { return visitor.visitBinary( wrap(tree), parameter ); }
				@Override public R visitBlock(BlockTree tree, P parameter) { return visitor.visitBlock( wrap(tree), parameter ); }
				@Override public R visitBreak(BreakTree tree, P parameter) { return visitor.visitBreak( wrap(tree), parameter ); }
				@Override public R visitCase(CaseTree tree, P parameter) { return visitor.visitCase( wrap(tree), parameter ); }
				@Override public R visitCatch(CatchTree tree, P parameter) { return visitor.visitCatch( wrap(tree), parameter ); }
				@Override public R visitClass(ClassTree tree, P parameter) { return visitor.visitClass( wrap(tree), parameter ); }
				@Override public R visitCompilationUnit(CompilationUnitTree tree, P parameter) { return visitor.visitCompilationUnit( wrap(tree), parameter ); }
				@Override public R visitCompoundAssignment(CompoundAssignmentTree tree, P parameter) { return visitor.visitCompoundAssignment( wrap(tree), parameter ); }
				@Override public R visitConditionalExpression(ConditionalExpressionTree tree, P parameter) { return visitor.visitConditionalExpression( wrap(tree), parameter ); }
				@Override public R visitContinue(ContinueTree tree, P parameter) { return visitor.visitContinue( wrap(tree), parameter ); }
				@Override public R visitDoWhileLoop(DoWhileLoopTree tree, P parameter) { return visitor.visitDoWhileLoop( wrap(tree), parameter ); }
				@Override public R visitEmptyStatement(EmptyStatementTree tree, P parameter) { return visitor.visitEmptyStatement( wrap(tree), parameter ); }
				@Override public R visitEnhancedForLoop(EnhancedForLoopTree tree, P parameter) { return visitor.visitEnhancedForLoop( wrap(tree), parameter ); }
				@Override public R visitErroneous(ErroneousTree tree, P parameter) { return visitor.visitErroneous( wrap(tree), parameter ); }
				@Override public R visitExpressionStatement(ExpressionStatementTree tree, P parameter) { return visitor.visitExpressionStatement( wrap(tree), parameter ); }
				@Override public R visitForLoop(ForLoopTree tree, P parameter) { return visitor.visitForLoop( wrap(tree), parameter ); }
				@Override public R visitIdentifier(IdentifierTree tree, P parameter) { return visitor.visitIdentifier( wrap(tree), parameter ); }
				@Override public R visitIf(IfTree tree, P parameter) { return visitor.visitIf( wrap(tree), parameter ); }
				@Override public R visitImport(ImportTree tree, P parameter) { return visitor.visitImport( wrap(tree), parameter ); }
				@Override public R visitInstanceOf(InstanceOfTree tree, P parameter) { return visitor.visitInstanceOf( wrap(tree), parameter ); }
				@Override public R visitIntersectionType(IntersectionTypeTree tree, P parameter) { return visitor.visitIntersectionType( wrap(tree), parameter ); }
				@Override public R visitLabeledStatement(LabeledStatementTree tree, P parameter) { return visitor.visitLabeledStatement( wrap(tree), parameter ); }
				@Override public R visitLambdaExpression(LambdaExpressionTree tree, P parameter) { return visitor.visitLambdaExpression( wrap(tree), parameter ); }
				@Override public R visitLiteral(LiteralTree tree, P parameter) { return visitor.visitLiteral( wrap(tree), parameter ); }
				@Override public R visitMemberReference(MemberReferenceTree tree, P parameter) { return visitor.visitMemberReference( wrap(tree), parameter ); }
				@Override public R visitMemberSelect(MemberSelectTree tree, P parameter) { return visitor.visitMemberSelect( wrap(tree), parameter ); }
				@Override public R visitMethod(MethodTree tree, P parameter) { return visitor.visitMethod( wrap(tree), parameter ); }
				@Override public R visitMethodInvocation(MethodInvocationTree tree, P parameter) { return visitor.visitMethodInvocation( wrap(tree), parameter ); }
				@Override public R visitModifiers(ModifiersTree tree, P parameter) { return visitor.visitModifiers( wrap(tree), parameter ); }
				@Override public R visitNewArray(NewArrayTree tree, P parameter) { return visitor.visitNewArray( wrap(tree), parameter ); }
				@Override public R visitNewClass(NewClassTree tree, P parameter) { return visitor.visitNewClass( wrap(tree), parameter ); }
				@Override public R visitOther(Tree tree, P parameter) { return visitor.visitNode( wrap(tree), parameter ) ; }
				@Override public R visitParameterizedType(ParameterizedTypeTree tree, P parameter) { return visitor.visitParameterizedType( wrap(tree), parameter ); }
				@Override public R visitParenthesized(ParenthesizedTree tree, P parameter) { return visitor.visitParenthesized( wrap(tree), parameter ); }
				@Override public R visitPrimitiveType(PrimitiveTypeTree tree, P parameter) { return visitor.visitPrimitiveType( wrap(tree), parameter ); }
				@Override public R visitReturn(ReturnTree tree, P parameter) { return visitor.visitReturn( wrap(tree), parameter ); }
				@Override public R visitSwitch(SwitchTree tree, P parameter) { return visitor.visitSwitch( wrap(tree), parameter ); }
				@Override public R visitSynchronized(SynchronizedTree tree, P parameter) { return visitor.visitSynchronized( wrap(tree), parameter ); }
				@Override public R visitThrow(ThrowTree tree, P parameter) { return visitor.visitThrow( wrap(tree), parameter ); }
				@Override public R visitTry(TryTree tree, P parameter) { return visitor.visitTry( wrap(tree), parameter ); }
				@Override public R visitTypeCast(TypeCastTree tree, P parameter) { return visitor.visitTypeCast( wrap(tree), parameter ); }
				@Override public R visitTypeParameter(TypeParameterTree tree, P parameter) { return visitor.visitTypeParameter( wrap(tree), parameter ); }
				@Override public R visitUnary(UnaryTree tree, P parameter) { return visitor.visitUnary( wrap(tree), parameter ); }
				@Override public R visitUnionType(UnionTypeTree tree, P parameter) { return visitor.visitUnionType( wrap(tree), parameter ); }
				@Override public R visitVariable(VariableTree tree, P parameter) { return visitor.visitVariablesStatement( wrap(tree), parameter ); }
				@Override public R visitWhileLoop(WhileLoopTree tree, P parameter) { return visitor.visitWhileLoop( wrap(tree), parameter ); }
				@Override public R visitWildcard(WildcardTree tree, P parameter) { return visitor.visitWildcard( wrap(tree), parameter ); }
			}, parameter);
		}
	}
		
	private class AnnotatedTypeImpl extends TreeWrapper<AnnotatedTypeTree> implements Ast.AnnotatedType {
		public AnnotatedTypeImpl(AnnotatedTypeTree tree) { super(tree) ; }
	}
	private class AnnotationImpl extends TreeWrapper<AnnotationTree> implements Ast.Annotation {
		public AnnotationImpl(AnnotationTree tree) { super(tree) ; }
	}
	private class ArrayAccessImpl extends TreeWrapper<ArrayAccessTree> implements Ast.ArrayAccess {
		public ArrayAccessImpl(ArrayAccessTree tree) { super(tree) ; }
	}
	private class ArrayTypeImpl extends TreeWrapper<ArrayTypeTree> implements Ast.ArrayType {
		public ArrayTypeImpl(ArrayTypeTree tree) { super(tree) ; }
	}
	private class AssertImpl extends TreeWrapper<AssertTree> implements Ast.Assert {
		public AssertImpl(AssertTree tree) { super(tree) ; }
	}
	private class AssignmentImpl extends TreeWrapper<AssignmentTree> implements Ast.Assignment {
		public AssignmentImpl(AssignmentTree tree) { super(tree) ; }
		@Override public Ast.Expression expression() { return wrap( m_tree.getExpression() ) ; }
		@Override public Ast.Expression variable() { return wrap( m_tree.getVariable() ) ; }
		@Override public Ast.Assignment.AssignmentType assignmentType() { return Ast.Assignment.AssignmentType.ASSIGNMENT ; }
	}
	private class BinaryImpl extends TreeWrapper<BinaryTree> implements Ast.Binary {
		public BinaryImpl(BinaryTree tree) { super(tree) ; }
		@Override public Ast.Expression leftOperand() { return wrap( m_tree.getLeftOperand() ) ; }
		@Override public Ast.Expression rightOperand() { return wrap( m_tree.getRightOperand() ) ; }
		@Override
		public OperationType operationType() {
			switch (m_tree.getKind()) {
			case PLUS_ASSIGNMENT: return Ast.Binary.OperationType.PLUS_ASSIGNMENT ;
			case MINUS_ASSIGNMENT: return Ast.Binary.OperationType.MINUS_ASSIGNMENT ;
			case MULTIPLY_ASSIGNMENT: return Ast.Binary.OperationType.MULTIPLY_ASSIGNMENT ;
			case DIVIDE_ASSIGNMENT: return Ast.Binary.OperationType.DIVIDE_ASSIGNMENT ;
			case PLUS: return Ast.Binary.OperationType.PLUS ;
			case MINUS: return Ast.Binary.OperationType.MINUS ;
			case MULTIPLY: return Ast.Binary.OperationType.MULTIPLY ;
			case DIVIDE: return Ast.Binary.OperationType.DIVIDE ;
			case EQUAL_TO: return Ast.Binary.OperationType.EQUAL_TO ;
			case NOT_EQUAL_TO: return Ast.Binary.OperationType.NOT_EQUAL_TO ;
			case LESS_THAN: return Ast.Binary.OperationType.LESS_THAN ;
			case GREATER_THAN: return Ast.Binary.OperationType.GREATER_THAN ;
			case GREATER_THAN_EQUAL: return Ast.Binary.OperationType.GREATER_THAN_EQUAL ;
			case LESS_THAN_EQUAL: return Ast.Binary.OperationType.LESS_THAN_EQUAL ;
			default: return OperationType.UNKNOWN ;
			}
		}
	}
	private class BlockImpl extends TreeWrapper<BlockTree> implements Ast.Block {
		public BlockImpl(BlockTree tree) { super(tree) ; }
		@Override public List<? extends Ast.Statement> statements() { return wrapStatements( m_tree.getStatements() ) ; }
	}
	private class BreakImpl extends TreeWrapper<BreakTree> implements Ast.Break {
		public BreakImpl(BreakTree tree) { super(tree) ; }
	}
	private class CaseImpl extends TreeWrapper<CaseTree> implements Ast.Case {
		public CaseImpl(CaseTree tree) { super(tree) ; }
	}
	private class CatchImpl extends TreeWrapper<CatchTree> implements Ast.Catch {
		public CatchImpl(CatchTree tree) { super(tree) ; }
	}
	private class ClassImpl extends TreeWrapper<ClassTree> implements Ast.Class {
		public ClassImpl(ClassTree tree) { super(tree) ; }
	}
	private class CompilationUnitImpl extends TreeWrapper<CompilationUnitTree> implements Ast.CompilationUnit {
		public CompilationUnitImpl(CompilationUnitTree tree) { super(tree) ; }
	}
	private class CompoundAssignmentImpl extends TreeWrapper<CompoundAssignmentTree> implements Ast.CompoundAssignment {
		public CompoundAssignmentImpl(CompoundAssignmentTree tree) { super(tree) ; }
		@Override public Ast.Expression variable() { return wrap( m_tree.getVariable() ) ; }
		@Override public Ast.Expression operand() { return wrap( m_tree.getExpression() ) ; }
		@Override public OperationType operationType() {
			switch ( m_tree.getKind() ) {
			case PLUS_ASSIGNMENT: return OperationType.PLUS_ASSIGNMENT ;
			case MINUS_ASSIGNMENT: return OperationType.MINUS_ASSIGNMENT ;
			case MULTIPLY_ASSIGNMENT: return OperationType.MULTIPLY_ASSIGNMENT ;
			case DIVIDE_ASSIGNMENT: return OperationType.DIVIDE_ASSIGNMENT ;
			default: return OperationType.UNKNOWN ;
			}
		}
	}
	private class ConditionalExpressionImpl extends TreeWrapper<ConditionalExpressionTree> implements Ast.ConditionalExpression {
		public ConditionalExpressionImpl(ConditionalExpressionTree tree) { super(tree) ; }
	}
	private class ContinueImpl extends TreeWrapper<ContinueTree> implements Ast.Continue {
		public ContinueImpl(ContinueTree tree) { super(tree) ; }
	}
	private class DoWhileLoopImpl extends TreeWrapper<DoWhileLoopTree> implements Ast.DoWhileLoop {
		public DoWhileLoopImpl(DoWhileLoopTree tree) { super(tree) ; }
		@Override public Ast.Statement statement() { return wrap( m_tree.getStatement() ) ; }
		@Override public Ast.Expression condition() { return wrap( m_tree.getCondition() ) ; }
	}
	private class EmptyStatementImpl extends TreeWrapper<EmptyStatementTree> implements Ast.EmptyStatement {
		public EmptyStatementImpl(EmptyStatementTree tree) { super(tree) ; }
	}
	private class EnhancedForLoopImpl extends TreeWrapper<EnhancedForLoopTree> implements Ast.EnhancedForLoop {
		public EnhancedForLoopImpl(EnhancedForLoopTree tree) { super(tree) ; }
		@Override
		public VariableDeclarationsStatement variable() { return wrap( m_tree.getVariable() ); }
		@Override
		public Expression expression() { return wrap( m_tree.getExpression() ); }
		@Override
		public Statement statement() { return wrap( m_tree.getStatement() ); }
	}
	private class ErroneousImpl extends TreeWrapper<ErroneousTree> implements Ast.Erroneous {
		public ErroneousImpl(ErroneousTree tree) { super(tree) ; }
	}
	private class ExpressionStatementImpl extends TreeWrapper<ExpressionStatementTree> implements Ast.ExpressionStatement {
		public ExpressionStatementImpl(ExpressionStatementTree tree) { super(tree) ; }
		@Override public Ast.Expression expression() { return wrap( m_tree.getExpression() ) ; }
	}
	private class ForLoopImpl extends TreeWrapper<ForLoopTree> implements Ast.ForLoop {
		public ForLoopImpl(ForLoopTree tree) { super(tree) ; }
		@Override public List<? extends Ast.Statement> initializer() { return wrapStatements( m_tree.getInitializer() ) ; }
		@Override public Ast.Expression condition() { return wrap( m_tree.getCondition() ) ; }
		@Override public List<? extends Ast.ExpressionStatement> update() { return wrapExpressionStatements( m_tree.getUpdate() ) ; }
		@Override public Ast.Statement statement() { return wrap( m_tree.getStatement() ) ; }
	}
	private class IdentifierImpl extends TreeWrapper<IdentifierTree> implements Ast.Identifier {
		public IdentifierImpl(IdentifierTree tree) { super(tree) ; }
		@Override public Ast.Name name() { return wrap( m_tree.getName() ) ; }
	}
	private class IfImpl extends TreeWrapper<IfTree> implements Ast.If {
		public IfImpl(IfTree tree) { super(tree) ; }
		@Override public Ast.Expression condition() { return wrap( m_tree.getCondition() ) ; }
		@Override public Ast.Statement thenStatement() { return wrap( m_tree.getThenStatement() ) ; }
		@Override public Ast.Statement elseStatement() { return wrap( m_tree.getElseStatement() ) ; }
	}
	private class ImportImpl extends TreeWrapper<ImportTree> implements Ast.Import {
		public ImportImpl(ImportTree tree) { super(tree) ; }
	}
	private class InstanceOfImpl extends TreeWrapper<InstanceOfTree> implements Ast.InstanceOf {
		public InstanceOfImpl(InstanceOfTree tree) { super(tree) ; }
	}
	private class IntersectionTypeImpl extends TreeWrapper<IntersectionTypeTree> implements Ast.IntersectionType {
		public IntersectionTypeImpl(IntersectionTypeTree tree) { super(tree) ; }
	}
	private class LabeledStatementImpl extends TreeWrapper<LabeledStatementTree> implements Ast.LabeledStatement {
		public LabeledStatementImpl(LabeledStatementTree tree) { super(tree) ; }
	}
	private class LambdaExpressionImpl extends TreeWrapper<LambdaExpressionTree> implements Ast.LambdaExpression {
		public LambdaExpressionImpl(LambdaExpressionTree tree) { super(tree) ; }
	}
	private class LiteralImpl extends TreeWrapper<LiteralTree> implements Ast.Literal {
		public LiteralImpl(LiteralTree tree) { super(tree) ; }
		@Override public Object value() { return m_tree.getValue() ; }
		@Override public LiteralType literalType() {
			switch ( m_tree.getKind() ) {
			case STRING_LITERAL: return Ast.Literal.LiteralType.STRING_LITERAL ;
			case CHAR_LITERAL: return Ast.Literal.LiteralType.CHAR_LITERAL ;
			case INT_LITERAL: return Ast.Literal.LiteralType.INT_LITERAL ;
			case FLOAT_LITERAL: return Ast.Literal.LiteralType.FLOAT_LITERAL ;
			case DOUBLE_LITERAL: return Ast.Literal.LiteralType.DOUBLE_LITERAL ;
			case BOOLEAN_LITERAL: return Ast.Literal.LiteralType.BOOLEAN_LITERAL ;
			case LONG_LITERAL: return Ast.Literal.LiteralType.LONG_LITERAL ;
			case NULL_LITERAL: return Ast.Literal.LiteralType.NULL_LITERAL ;
			default: return Ast.Literal.LiteralType.UNKNOWN ;
			}
		}
	}
	private class MemberReferenceImpl extends TreeWrapper<MemberReferenceTree> implements Ast.MemberReference {
		public MemberReferenceImpl(MemberReferenceTree tree) { super(tree) ; }
	}
	private class MemberSelectImpl extends TreeWrapper<MemberSelectTree> implements Ast.MemberSelect {
		public MemberSelectImpl(MemberSelectTree tree) { super(tree) ; }
		@Override public Ast.Expression expression() { return wrap( m_tree.getExpression() ) ; }
		@Override public Ast.Name identifier() { return wrap( m_tree.getIdentifier() ) ; } 
	}
	private class MethodImpl extends TreeWrapper<MethodTree> implements Ast.Method {
		public MethodImpl(MethodTree tree) { super(tree) ; }
		@Override public Ast.Block body() { return wrap( m_tree.getBody() ) ; }
		@Override public TypeMirror returnType() { return resolveIdentifier( m_tree.getReturnType() ).type() ; }
		@Override public List<? extends Ast.Variable> parameters() { return wrapVariables( m_tree.getParameters() ) ; }
	}
	private class MethodInvocationImpl extends TreeWrapper<MethodInvocationTree> implements Ast.MethodInvocation {
		public MethodInvocationImpl(MethodInvocationTree tree) { super(tree) ; }
		@Override public List<? extends Ast.Expression> arguments() { return wrapExpressions( m_tree.getArguments() ) ;}
		@Override public Ast.Expression methodSelect() { return wrap( m_tree.getMethodSelect() ) ; } 
	}
	private class ModifiersImpl extends TreeWrapper<ModifiersTree> implements Ast.Modifiers {
		public ModifiersImpl(ModifiersTree tree) { super(tree) ; }
	}
	private class NewArrayImpl extends TreeWrapper<NewArrayTree> implements Ast.NewArray {
		public NewArrayImpl(NewArrayTree tree) { super(tree) ; }
	}
	private class NewClassImpl extends TreeWrapper<NewClassTree> implements Ast.NewClass {
		public NewClassImpl(NewClassTree tree) { super(tree) ; }
	}
	private class ParameterizedTypeImpl extends TreeWrapper<ParameterizedTypeTree> implements Ast.ParameterizedType {
		public ParameterizedTypeImpl(ParameterizedTypeTree tree) { super(tree) ; }
	}
	private class ParenthesizedImpl extends TreeWrapper<ParenthesizedTree> implements Ast.Parenthesized {
		public ParenthesizedImpl(ParenthesizedTree tree) { super(tree) ; }
		@Override public Ast.Expression expression() { return wrap( m_tree.getExpression() ) ; }
	}
	private class PrimitiveTypeImpl extends TreeWrapper<PrimitiveTypeTree> implements Ast.PrimitiveType {
		public PrimitiveTypeImpl(PrimitiveTypeTree tree) { super(tree) ; }
	}
	private class ReturnImpl extends TreeWrapper<ReturnTree> implements Ast.Return {
		public ReturnImpl(ReturnTree tree) { super(tree) ; }
		@Override public Ast.Expression expression() { return wrap( m_tree.getExpression() ) ; }
	}
	private class SwitchImpl extends TreeWrapper<SwitchTree> implements Ast.Switch {
		public SwitchImpl(SwitchTree tree) { super(tree) ; }
	}
	private class SynchronizedImpl extends TreeWrapper<SynchronizedTree> implements Ast.Synchronized {
		public SynchronizedImpl(SynchronizedTree tree) { super(tree) ; }
	}
	private class ThrowImpl extends TreeWrapper<ThrowTree> implements Ast.Throw {
		public ThrowImpl(ThrowTree tree) { super(tree) ; }
	}
	private class TryImpl extends TreeWrapper<TryTree> implements Ast.Try {
		public TryImpl(TryTree tree) { super(tree) ; }
	}
	private class TypeCastImpl extends TreeWrapper<TypeCastTree> implements Ast.TypeCast {
		public TypeCastImpl(TypeCastTree tree) { super(tree) ; }
		@Override public Ast.Expression expression() { return wrap( m_tree.getExpression() ) ; }
	}
	private class TypeParameterImpl extends TreeWrapper<TypeParameterTree> implements Ast.TypeParameter {
		public TypeParameterImpl(TypeParameterTree tree) { super(tree) ; }
	}
	private class UnaryImpl extends TreeWrapper<UnaryTree> implements Ast.Unary {
		public UnaryImpl(UnaryTree tree) { super(tree) ; }
		@Override public Ast.Expression expression() { return wrap( m_tree.getExpression() ) ; }
		@Override
		public OperationType operationType() {
			switch (m_tree.getKind()) {
			case PREFIX_INCREMENT: return Ast.Unary.OperationType.PREFIX_INCREMENT ;
			case POSTFIX_INCREMENT: return Ast.Unary.OperationType.POSTFIX_INCREMENT ;
			case PREFIX_DECREMENT: return Ast.Unary.OperationType.PREFIX_DECREMENT ;
			case POSTFIX_DECREMENT: return Ast.Unary.OperationType.POSTFIX_DECREMENT ;
			case UNARY_PLUS: return Ast.Unary.OperationType.UNARY_PLUS ;
			case UNARY_MINUS: return Ast.Unary.OperationType.UNARY_MINUS ;
			case LOGICAL_COMPLEMENT: return Ast.Unary.OperationType.LOGICAL_COMPLEMENT ;
			default: return OperationType.UNKNOWN ;
			}
		}
	}
	private class UnionTypeImpl extends TreeWrapper<UnionTypeTree> implements Ast.UnionType {
		public UnionTypeImpl(UnionTypeTree tree) { super(tree) ; }
	}
	private class VariableImpl extends TreeWrapper<VariableTree> implements Ast.Variable {
		public VariableImpl(VariableTree tree) { super(tree) ; }
		@Override public Ast.Name name() { return wrap( m_tree.getName() ) ; }
		@Override public Ast.Expression initializer() { return wrap( m_tree.getInitializer() ) ; }
		@Override public TypeMirror type() { return resolveIdentifier( m_tree.getType() ).type() ; }
		@Override public <R, P> R accept(Ast.Visitor<R, P> visitor, P parameter) {
			return visitor.visitVariable( this, parameter ) ; // because the inherited accept(...) never calls visitor.visitVariable(...)
		}
	}
	private class VariableStatementImpl extends TreeWrapper<VariableTree> implements Ast.VariableDeclarationsStatement {
		private List<? extends Ast.Variable> m_variables ;
		public VariableStatementImpl(VariableTree tree) { super(tree) ; m_variables = Collections.singletonList( new VariableImpl(tree) ) ; }
		@Override public List<? extends Ast.Variable> variables() { return m_variables ; }
	}
	private class WhileLoopImpl extends TreeWrapper<WhileLoopTree> implements Ast.WhileLoop {
		public WhileLoopImpl(WhileLoopTree tree) { super(tree) ; }
		@Override public Ast.Expression condition() { return wrap( m_tree.getCondition() ) ; }
		@Override public Ast.Statement statement() { return wrap( m_tree.getStatement() ) ; }
	}
	private class WildcardImpl extends TreeWrapper<WildcardTree> implements Ast.Wildcard {
		public WildcardImpl(WildcardTree tree) { super(tree) ; }
	}	
	
	@Override
	public Ast.Method getMethod(ExecutableElement methodElement) {
		return wrap( trees().getTree( methodElement ) ) ;
	}

	private Ast.Name wrap( Name name ) { return name == null ? null : new Ast.Name(name) ; }
	
	private List<? extends Ast.Variable> wrapVariables( List<? extends VariableTree> variables ) {
		return Collections.emptyList() ;
	}

	private List<? extends Ast.Statement> wrapStatements( List<? extends StatementTree> statements ) {
		return statements.stream().map( (e)->wrap(e) ).collect(Collectors.toList()) ;
	}

	private List<? extends Ast.ExpressionStatement> wrapExpressionStatements( List<? extends ExpressionStatementTree> expressionStatements ) {
		return expressionStatements.stream().map( (e)->wrap(e) ).collect(Collectors.toList()) ;
	}
	
	private List<? extends Ast.Expression> wrapExpressions( List<? extends ExpressionTree> expressions ) {
		return expressions.stream().map( (e)->wrap(e) ).collect(Collectors.toList()) ;
	}

	private Ast.AnnotatedType         wrap( AnnotatedTypeTree annotatedTypeTree ) { return annotatedTypeTree == null ? null : new AnnotatedTypeImpl( annotatedTypeTree ) ; }
	private Ast.Annotation            wrap( AnnotationTree annotationTree ) { return annotationTree == null ? null : new AnnotationImpl( annotationTree ) ; }
	private Ast.ArrayAccess           wrap( ArrayAccessTree arrayAccessTree ) { return arrayAccessTree == null ? null : new ArrayAccessImpl( arrayAccessTree ) ; }
	private Ast.ArrayType             wrap( ArrayTypeTree arrayTypeTree ) { return arrayTypeTree == null ? null : new ArrayTypeImpl( arrayTypeTree ) ; }
	private Ast.Assert                wrap( AssertTree assertTree ) { return assertTree == null ? null : new AssertImpl( assertTree ) ; }
	private Ast.Assignment            wrap( AssignmentTree assignmentTree ) { return assignmentTree == null ? null : new AssignmentImpl( assignmentTree ) ; }
	private Ast.Binary                wrap( BinaryTree binaryTree ) { return binaryTree == null ? null : new BinaryImpl( binaryTree ) ; }
	private Ast.Block                 wrap( BlockTree blockTree ) { return blockTree == null ? null : new BlockImpl(  blockTree) ; }
	private Ast.Break                 wrap( BreakTree breakTree ) { return breakTree == null ? null : new BreakImpl( breakTree ) ; }
	private Ast.Case                  wrap( CaseTree caseTree ) { return caseTree == null ? null : new CaseImpl( caseTree ) ; }
	private Ast.Catch                 wrap( CatchTree catchTree ) { return catchTree == null ? null : new CatchImpl( catchTree ) ; }
	private Ast.Class                 wrap( ClassTree classTree ) { return classTree == null ? null : new ClassImpl( classTree ) ; }
	private Ast.CompilationUnit       wrap( CompilationUnitTree compilationUnitTree ) { return compilationUnitTree == null ? null : new CompilationUnitImpl( compilationUnitTree ) ; }
	private Ast.CompoundAssignment    wrap( CompoundAssignmentTree compoundAssignmentTree ) { return compoundAssignmentTree == null ? null : new CompoundAssignmentImpl( compoundAssignmentTree ) ; }
	private Ast.ConditionalExpression wrap( ConditionalExpressionTree conditionalExpressionTree ) { return conditionalExpressionTree == null ? null : new ConditionalExpressionImpl( conditionalExpressionTree ) ; }
	private Ast.Continue              wrap( ContinueTree continueTree ) { return continueTree == null ? null : new ContinueImpl( continueTree ) ; }
	private Ast.DoWhileLoop           wrap( DoWhileLoopTree doWhileLoopTree ) { return doWhileLoopTree == null ? null : new DoWhileLoopImpl( doWhileLoopTree ) ; }
	private Ast.EmptyStatement        wrap( EmptyStatementTree emptyStatementTree ) { return emptyStatementTree == null ? null : new EmptyStatementImpl( emptyStatementTree ) ; }
	private Ast.EnhancedForLoop       wrap( EnhancedForLoopTree enhancedForLoopTree ) { return enhancedForLoopTree == null ? null : new EnhancedForLoopImpl( enhancedForLoopTree ) ; }
	private Ast.Erroneous             wrap( ErroneousTree erroneousTree ) { return erroneousTree == null ? null : new ErroneousImpl( erroneousTree ) ; }
	private Ast.ExpressionStatement   wrap( ExpressionStatementTree expressionStatementTree ) { return expressionStatementTree == null ? null : new ExpressionStatementImpl( expressionStatementTree ) ; }
	private Ast.ForLoop               wrap( ForLoopTree forLoopTree ) { return forLoopTree == null ? null : new ForLoopImpl( forLoopTree ) ; }
	private Ast.Identifier            wrap( IdentifierTree identifierTree ) { return identifierTree == null ? null : new IdentifierImpl( identifierTree ) ; }
	private Ast.If                    wrap( IfTree ifTree ) { return ifTree == null ? null : new IfImpl( ifTree ) ; }
	private Ast.Import                wrap( ImportTree importTree ) { return importTree == null ? null : new ImportImpl( importTree ) ; }
	private Ast.InstanceOf            wrap( InstanceOfTree instanceOfTree ) { return instanceOfTree == null ? null : new InstanceOfImpl( instanceOfTree ) ; }
	private Ast.IntersectionType      wrap( IntersectionTypeTree intersectionTypeTree ) { return intersectionTypeTree == null ? null : new IntersectionTypeImpl( intersectionTypeTree ) ; }
	private Ast.LabeledStatement      wrap( LabeledStatementTree labeledStatementTree ) { return labeledStatementTree == null ? null : new LabeledStatementImpl( labeledStatementTree ) ; }
	private Ast.LambdaExpression      wrap( LambdaExpressionTree lambdaExpressionTree ) { return lambdaExpressionTree == null ? null : new LambdaExpressionImpl( lambdaExpressionTree ) ; }
	private Ast.Literal               wrap( LiteralTree literalTree ) { return literalTree == null ? null : new LiteralImpl( literalTree ) ; }
	private Ast.MemberReference       wrap( MemberReferenceTree memberReferenceTree ) { return memberReferenceTree == null ? null : new MemberReferenceImpl( memberReferenceTree ) ; }
	private Ast.MemberSelect          wrap( MemberSelectTree memberSelectTree ) { return memberSelectTree == null ? null : new MemberSelectImpl( memberSelectTree ) ; }
	private Ast.Method                wrap( MethodTree methodTree ) { return methodTree == null ? null : new MethodImpl( methodTree ) ; }
	private Ast.MethodInvocation      wrap( MethodInvocationTree methodInvocationTree ) { return methodInvocationTree == null ? null : new MethodInvocationImpl( methodInvocationTree ) ; }
	private Ast.Modifiers             wrap( ModifiersTree modifiersTree ) { return modifiersTree == null ? null : new ModifiersImpl( modifiersTree ) ; }
	private Ast.NewArray              wrap( NewArrayTree newArrayTree ) { return newArrayTree == null ? null : new NewArrayImpl( newArrayTree ) ; }
	private Ast.NewClass              wrap( NewClassTree newClassTree ) { return newClassTree == null ? null : new NewClassImpl( newClassTree ) ; }
	private Ast.ParameterizedType     wrap( ParameterizedTypeTree parameterizedTypeTree ) { return parameterizedTypeTree == null ? null : new ParameterizedTypeImpl( parameterizedTypeTree ) ; }
	private Ast.Parenthesized         wrap( ParenthesizedTree parenthesizedTree ) { return parenthesizedTree == null ? null : new ParenthesizedImpl( parenthesizedTree ) ; }
	private Ast.PrimitiveType         wrap( PrimitiveTypeTree primitiveTypeTree ) { return primitiveTypeTree == null ? null : new PrimitiveTypeImpl( primitiveTypeTree ) ; }
	private Ast.Return                wrap( ReturnTree returnTree ) { return returnTree == null ? null : new ReturnImpl( returnTree ) ; }
	private Ast.Switch                wrap( SwitchTree switchTree ) { return switchTree == null ? null : new SwitchImpl( switchTree ) ; }
	private Ast.Synchronized          wrap( SynchronizedTree synchronizedTree ) { return synchronizedTree == null ? null : new SynchronizedImpl( synchronizedTree ) ; }
	private Ast.Throw                 wrap( ThrowTree throwTree ) { return throwTree == null ? null : new ThrowImpl( throwTree ) ; }
	private Ast.Try                   wrap( TryTree tryTree ) { return tryTree == null ? null : new TryImpl( tryTree ) ; }
	private Ast.TypeCast              wrap( TypeCastTree typeCastTree ) { return typeCastTree == null ? null : new TypeCastImpl( typeCastTree ) ; }
	private Ast.TypeParameter         wrap( TypeParameterTree typeParameterTree ) { return typeParameterTree == null ? null : new TypeParameterImpl( typeParameterTree ) ; }
	private Ast.Unary                 wrap( UnaryTree unaryTree ) { return unaryTree == null ? null : new UnaryImpl( unaryTree ) ; }
	private Ast.UnionType             wrap( UnionTypeTree unionTypeTree ) { return unionTypeTree == null ? null : new UnionTypeImpl( unionTypeTree ) ; }
	private Ast.VariableDeclarationsStatement     wrap( VariableTree variableTree ) { return variableTree == null ? null : new VariableStatementImpl( variableTree ) ; }
	private Ast.WhileLoop             wrap( WhileLoopTree whileLoopTree ) { return whileLoopTree == null ? null : new WhileLoopImpl( whileLoopTree ) ; }
	private Ast.Wildcard              wrap( WildcardTree wildcardTree ) { return wildcardTree == null ? null : new WildcardImpl( wildcardTree ) ; }	

	private Ast.Node wrap( Tree tree ) {
		return tree == null ? null : tree.accept( new TreeInterpreter<Ast.Node,Void>(RoutineJdkTools.this) {
			@Override public Ast.Node visitAnnotatedType(AnnotatedTypeTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitAnnotation(AnnotationTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitArrayAccess(ArrayAccessTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitArrayType(ArrayTypeTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitAssert(AssertTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitAssignment(AssignmentTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitBinary(BinaryTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitBlock( BlockTree blockTree, Void parameter ) { return wrap(blockTree) ; }
			@Override public Ast.Node visitBreak(BreakTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitCase(CaseTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitCatch(CatchTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitClass(ClassTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitCompilationUnit(CompilationUnitTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitCompoundAssignment(CompoundAssignmentTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitConditionalExpression(ConditionalExpressionTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitContinue(ContinueTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitDoWhileLoop(DoWhileLoopTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitEmptyStatement(EmptyStatementTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitEnhancedForLoop(EnhancedForLoopTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitErroneous(ErroneousTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitExpressionStatement(ExpressionStatementTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitForLoop(ForLoopTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitIdentifier(IdentifierTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitIf(IfTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitImport(ImportTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitInstanceOf(InstanceOfTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitIntersectionType(IntersectionTypeTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitLabeledStatement(LabeledStatementTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitLambdaExpression(LambdaExpressionTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitLiteral(LiteralTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitMemberReference(MemberReferenceTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitMemberSelect(MemberSelectTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitMethod(MethodTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitMethodInvocation(MethodInvocationTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitModifiers(ModifiersTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitNewArray(NewArrayTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitNewClass(NewClassTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitParameterizedType(ParameterizedTypeTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitParenthesized(ParenthesizedTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitPrimitiveType(PrimitiveTypeTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitReturn(ReturnTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitSwitch(SwitchTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitSynchronized(SynchronizedTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitThrow(ThrowTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitTry(TryTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitTypeCast(TypeCastTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitTypeParameter(TypeParameterTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitUnary(UnaryTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitUnionType(UnionTypeTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitVariable(VariableTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitWhileLoop(WhileLoopTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Node visitWildcard(WildcardTree tree, Void parameter) { return wrap(tree); }	
		}, null) ;
	}
	
	private Ast.Statement wrap( StatementTree tree ) {
		return tree == null ? null : tree.accept( new TreeInterpreter<Ast.Statement,Void>(RoutineJdkTools.this) {
			@Override public Ast.Statement visitAssert(AssertTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitBlock(BlockTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitBreak(BreakTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitCase(CaseTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitClass(ClassTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitContinue(ContinueTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitDoWhileLoop(DoWhileLoopTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitEmptyStatement(EmptyStatementTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitEnhancedForLoop(EnhancedForLoopTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitExpressionStatement(ExpressionStatementTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitForLoop(ForLoopTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitIf(IfTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitLabeledStatement(LabeledStatementTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitReturn(ReturnTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitSwitch(SwitchTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitSynchronized(SynchronizedTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitThrow(ThrowTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitTry(TryTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitVariable(VariableTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Statement visitWhileLoop(WhileLoopTree tree, Void parameter) { return wrap(tree); }
		}, null) ;
	}

	private Ast.Expression wrap( ExpressionTree tree ) {
		return tree == null ? null : tree.accept( new TreeInterpreter<Ast.Expression,Void>(RoutineJdkTools.this) {
			@Override public Ast.Expression visitAnnotatedType(AnnotatedTypeTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitAnnotation(AnnotationTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitArrayAccess(ArrayAccessTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitAssignment(AssignmentTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitBinary(BinaryTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitCompoundAssignment(CompoundAssignmentTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitConditionalExpression(ConditionalExpressionTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitErroneous(ErroneousTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitIdentifier(IdentifierTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitInstanceOf(InstanceOfTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitLambdaExpression(LambdaExpressionTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitLiteral(LiteralTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitMemberReference(MemberReferenceTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitMemberSelect(MemberSelectTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitMethodInvocation(MethodInvocationTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitNewArray(NewArrayTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitNewClass(NewClassTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitParenthesized(ParenthesizedTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitTypeCast(TypeCastTree tree, Void parameter) { return wrap(tree); }
			@Override public Ast.Expression visitUnary(UnaryTree tree, Void parameter) { return wrap(tree); }
		}, null) ;
	}
}
