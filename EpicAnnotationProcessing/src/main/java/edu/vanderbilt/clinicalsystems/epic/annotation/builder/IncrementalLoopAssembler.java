package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.List;
import java.util.Optional;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast.Binary;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.Ast.Unary;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class IncrementalLoopAssembler extends FlowAssembler<Ast.ForLoop>{
	
	public IncrementalLoopAssembler( RoutineTools routineTools ) { super(routineTools) ; }
	
	@Override
	public void assemble(Ast.ForLoop forLoopNode, Block block) {
		try ( BlockManager blockManager = new BlockManager(block) ) {
	
			ForLoopConfiguration config = extractForLoopConfiguration( forLoopNode ) ;
			if ( config.complete() ) {
				VariableReference loopVar = new VariableReference(Scope.LOCAL, ReferenceStyle.DIRECT, config.loopVariable().toString() ) ;
				
				if ( config.requiresLoopVariableDeclaration() )
					blockManager.appendElement( new Command( CommandType.NEW, new DeclarationList(loopVar) ) ) ;
				
				Expression loopStart = tools().expressions().generate( config.loopVariableInitializer(), blockManager ) ;
				Constant loopStep = config.loopVariableStep() ;
				Optional<Expression> loopStop = config.loopVariableCondition().map( (b)->tools().expressions().generate( b.rightOperand(), blockManager ) ) ;
	
				Block forLoopBlock = wrapInsideInlineBlock( tools().blocks().generate( forLoopNode.statement(), null ) ) ;
				
				blockManager.appendElement( new Command( CommandType.FOR, new LoopDefinition(loopVar, loopStart, loopStop.orElse(null), loopStep ), forLoopBlock ) ) ;
				
			} else {
				
				/*
				 * CMD: <initializer>
				 * CMD: <initializer>
				 * CMD: FOR | (-) {
				 *   CMD: QUIT:<condition> | (-)
				 *   <statement>
				 *   CMD: <update>
				 *   CMD: <update>
				 * }
				 */
				
				
				for (Ast.Statement initializer : forLoopNode.initializer()) {
					blockManager.appendElements( tools().blocks().generate( initializer, null ) ) ;
				}
				
				Block bodyBlock = tools().blocks().generate( forLoopNode.statement(), null ) ;
				
				Block conditionBlock = new MultilineBlock() ;
				try ( BlockManager conditionBlockManager = new BlockManager(conditionBlock) ) {
					Expression condition = tools().expressions().generate( forLoopNode.condition(), conditionBlockManager ) ;
					conditionBlockManager.appendElement( new Command( condition.inverted(), CommandType.QUIT, Argument.NOTHING ) ) ;
				}
				bodyBlock.prependElements(conditionBlock);
				
				for (Ast.ExpressionStatement updater : forLoopNode.update()) {
					Block updaterBlock = new MultilineBlock() ;
					try ( BlockManager updaterBlockManager = new BlockManager(updaterBlock) ) {
						updaterBlockManager.appendElements( tools().blocks().generate( updater, null ) ) ;
					}
					bodyBlock.appendElements( updaterBlock );
				}
				Block inlineBlock = wrapInsideInlineBlock( bodyBlock ) ;
				blockManager.appendElement( new Command( CommandType.FOR, Argument.NOTHING, inlineBlock ) ) ;
	
			}
		}
	}
	
	private static class ForLoopConfiguration {
		private boolean m_complete = false ;
		private Ast.Name m_loopVariable ;
		private Ast.Expression m_loopVariableInitializer ;
		private boolean m_requiresLoopVariableDeclaration = false ;
		private Ast.Binary m_loopVariableCondition ;
		private boolean m_loopVariableAscending = true ;
		private Constant m_loopVariableStep ;
		public Ast.Name loopVariable() { return m_loopVariable; }
		public Ast.Name loopVariable(Ast.Name loopVariable) { return m_loopVariable = loopVariable; }
		public Ast.Expression loopVariableInitializer() { return m_loopVariableInitializer; }
		public Ast.Expression loopVariableInitializer( Ast.Expression loopVariableInitializer) {return m_loopVariableInitializer = loopVariableInitializer; }
		public boolean requiresLoopVariableDeclaration() { return m_requiresLoopVariableDeclaration ; }
		public boolean requiresLoopVariableDeclaration( boolean requiresLoopVariableDeclaration ) { return m_requiresLoopVariableDeclaration = requiresLoopVariableDeclaration ; }
		public Optional<Ast.Binary> loopVariableCondition() { return Optional.ofNullable(m_loopVariableCondition); } 
		public Optional<Ast.Binary> loopVariableCondition(Ast.Binary loopVariableCondition) {return Optional.ofNullable(m_loopVariableCondition = loopVariableCondition); }
		public boolean loopVariableAscending() { return m_loopVariableAscending ; }
		public boolean loopVariableAscending( boolean loopVariableAscending ) { return m_loopVariableAscending = loopVariableAscending ; }
		public Constant loopVariableStep() { return m_loopVariableStep; }
		public Constant loopVariableStep(Constant loopVariableStep) {	return m_loopVariableStep = loopVariableStep; }
		public boolean complete() { return m_complete ; }
		public boolean complete(boolean complete) { return m_complete = complete ; }
	}
	
	private ForLoopConfiguration extractForLoopConfiguration( Ast.ForLoop forLoopNode ) {
		ForLoopConfiguration config = new ForLoopConfiguration() ;
		config.complete(
			extractLoopVariable( forLoopNode, config )
			&&
			extractConditionOnVariable( forLoopNode, config )
			&&
			extractUpdateToVariable( forLoopNode, config )
		) ;
		return config ;
	}
	
	private boolean extractLoopVariable( Ast.ForLoop forLoopNode, ForLoopConfiguration config ) {
		List<? extends Ast.Statement> initializers = forLoopNode.initializer();
		if ( initializers.size() != 1 ) return false ; // not a single statement
		return initializers.get(0).accept( new Ast.Interpreter<Boolean,ForLoopConfiguration>(tools()) {

			@Override protected Boolean unsupportedNodeTypeResult(Ast.Node node, ForLoopConfiguration config) {
				// anything else is not suitable
				return false ;
			}

			@Override public Boolean visitVariable(Ast.Variable variable, ForLoopConfiguration config) {
				config.loopVariable( variable.name() ) ;
				config.loopVariableInitializer( variable.initializer() ) ;
				config.requiresLoopVariableDeclaration( true ) ;
				return true ;
			}

			@Override public Boolean visitExpressionStatement( Ast.ExpressionStatement expressionStatementNode, ForLoopConfiguration config) {
				// an expression assignment contains an expression, which can be checked for suitability
				return expressionStatementNode.expression().accept(this, config);
			}

			@Override public Boolean visitAssignment(Ast.Assignment assignment, ForLoopConfiguration config) {
				// an assignment may be suitable, if it is an assignment to an identifier
				Ast.Expression variable = assignment.variable();
				if ( variable instanceof Ast.Identifier ) {
					Ast.Identifier variableIdentifier = (Ast.Identifier)variable;
					config.loopVariable( variableIdentifier.name() ) ;
					config.loopVariableInitializer( assignment.expression() ) ;
					config.requiresLoopVariableDeclaration( false ) ;
					return true ;
				} else {
					return false ;
				}
			}

			private Boolean visitVariables( List<? extends Ast.Variable> variables,ForLoopConfiguration config) {
				// a variables declaration statement contains some variable declarations, which will be suitable if there's only one
				if ( variables.size() == 1 )
					return variables.get(0).accept(this,config) ;
				else
					return false ;
			}

			@Override public Boolean visitVariablesStatement( Ast.VariableDeclarationsStatement variablesStatement, ForLoopConfiguration config) {
				return visitVariables( variablesStatement.variables(), config );
			}

			@Override public Boolean visitVariablesExpression( Ast.VariableDeclarationsExpression variablesExpression, ForLoopConfiguration config) {
				return visitVariables( variablesExpression.variables(), config ) ;
			}
			
			
			
		}, config) ;
	}
	
	private boolean extractConditionOnVariable( Ast.ForLoop forLoopNode, ForLoopConfiguration config ) {
		Ast.Name variableName = config.loopVariable() ;
		if ( null == variableName ) return false ; // no variable
		
		Ast.Expression condition = forLoopNode.condition() ;
		if ( null == condition ) {
			config.loopVariableCondition( null ) ;
			config.loopVariableAscending( true ) ;
			return true ; // missing condition is acceptable
		}
		if ( !( condition instanceof Ast.Binary ) ) return false ; // not a binary comparison
		switch ( ((Ast.Binary)condition).operationType() ) {
		case LESS_THAN_EQUAL:
			config.loopVariableAscending( true ) ;
			break ; // acceptable
		case GREATER_THAN_EQUAL:
			config.loopVariableAscending( false ) ;
			break ; // acceptable
		default:
			return false ; // not a LE or GE condition
		}
		Ast.Expression leftOperand = ((Ast.Binary)condition).leftOperand();
		if ( !( leftOperand instanceof Ast.Identifier ) ) return false ; // not a comparison to a variable
		Ast.Name leftOperandName = ((Ast.Identifier)leftOperand).name();
		if ( !variableName.equals( leftOperandName ) ) return false ; // not the same variable
		config.loopVariableCondition( (Ast.Binary)condition ) ;
		return true ;
	}

	private static final Generator.Listener LISTENER_IGNORING_SIDE_EFFECTS = new Generator.Listener() {
		@Override public void generateSideEffect( Generator.Listener.Location location, RoutineElement element ) { /* ignore */ }
	} ;

	private boolean extractUpdateToVariable( Ast.ForLoop forLoopNode, ForLoopConfiguration config ) {
		Ast.Name variableName = config.loopVariable() ;
		if ( null == variableName ) return false ; // no variable
		
		List<? extends Ast.ExpressionStatement> updates = forLoopNode.update() ;
		if ( updates.size() != 1 ) return false ; // not a single statement
		Ast.Expression onlyUpdate = updates.get(0).expression() ;
		
		boolean ascending = config.loopVariableAscending() ;
		return onlyUpdate.accept( new Ast.Interpreter<Boolean,ForLoopConfiguration>(tools()) {

			private boolean extractUpdateToVariable( Ast.Expression target, Ast.Expression update, boolean ascending, ForLoopConfiguration config) {
				Expression expr = tools().expressions().generate( update, LISTENER_IGNORING_SIDE_EFFECTS ) ;
				if ( !(expr instanceof Constant) )
					return false; 
				Constant constantUpdate = (Constant)expr;
				if ( !constantUpdate.representsNumber() )
					return false; 
				if ( !ascending )
					constantUpdate = constantUpdate.negated() ;
				return extractUpdateToVariable( target, constantUpdate, config ) ;
			}
			
			private boolean extractUpdateToVariable( Ast.Expression target, Constant constantUpdate, ForLoopConfiguration config) {
				if ( !( target instanceof Ast.Identifier ) ) return false ; // not an update to a variable
				Ast.Name targetName = ((Ast.Identifier)target).name();
				if ( !variableName.equals( targetName ) ) return false ; // not the same variable
				config.loopVariableStep(constantUpdate) ;
				return true ;
			}
			
			@Override public Boolean visitAssignment(Ast.Assignment assignment, ForLoopConfiguration parameter) {
				switch ( assignment.assignmentType() ) {
				case PLUS_ASSIGNMENT:
					if ( !ascending ) return false ;
					return extractUpdateToVariable( assignment.variable(), assignment.expression(), ascending, config ) ;
				case MINUS_ASSIGNMENT:
					if ( ascending ) return false ;
					return extractUpdateToVariable( assignment.variable(), assignment.expression(), ascending, config ) ;
				default:
					return false ; // not an increment/decrement statement
				}
			}

			@Override public Boolean visitCompoundAssignment( Ast.CompoundAssignment compoundAssignment, ForLoopConfiguration config) {
				switch ( compoundAssignment.operationType() ) {
				case PLUS_ASSIGNMENT:
					if ( !ascending ) return false ;
					return extractUpdateToVariable( compoundAssignment.variable(), compoundAssignment.operand(), ascending, config ) ;
				case MINUS_ASSIGNMENT:
					if ( ascending ) return false ;
					return extractUpdateToVariable( compoundAssignment.variable(), compoundAssignment.operand(), ascending, config ) ;
				default:
					return false ; // not an increment/decrement statement
				}
			}

			@Override public Boolean visitBinary(Binary binary, ForLoopConfiguration config) {
				switch ( binary.operationType() ) {
				case PLUS_ASSIGNMENT:
					if ( !ascending ) return false ;
					return extractUpdateToVariable( binary.leftOperand(), binary.rightOperand(), ascending, config ) ;
				case MINUS_ASSIGNMENT:
					if ( ascending ) return false ;
					return extractUpdateToVariable( binary.leftOperand(), binary.rightOperand(), ascending, config ) ;
				default:
					return false ; // not an increment/decrement statement
				}
			}

			@Override public Boolean visitUnary(Unary unary, ForLoopConfiguration config) {
				switch ( unary.operationType() ) {
				case PREFIX_INCREMENT:
				case POSTFIX_INCREMENT:
					if ( !ascending ) return false ;
					return extractUpdateToVariable( unary.expression(), Constant.from(1), config ) ;
				case PREFIX_DECREMENT:
				case POSTFIX_DECREMENT:
					if ( ascending ) return false ;
					return extractUpdateToVariable( unary.expression(), Constant.from(-1), config ) ;
				default:
					return false ; // not an increment/decrement statement
				}
			}
			
		}, config) ;
	}
	
}
