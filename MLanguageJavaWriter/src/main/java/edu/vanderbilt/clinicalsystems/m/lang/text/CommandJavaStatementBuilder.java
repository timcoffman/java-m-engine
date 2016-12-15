package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JStatement;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.VariableList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public abstract class CommandJavaStatementBuilder extends RoutineJavaBuilder<RoutineJavaBuilderClassContext> {
	private final RoutineJavaExpressionBuilder m_expressionBuilder ;
	
	public CommandJavaStatementBuilder( RoutineJavaBuilderClassContext builderContext, RoutineJavaExpressionBuilder expressionBuilder ) {
		super(builderContext) ;
		m_expressionBuilder = expressionBuilder ;
	}

	protected String outerClassName() { return context().outerClassName() ; }
	protected RoutineJavaExpressionBuilder expression() { return m_expressionBuilder; }
	
	protected <T extends JStatement> T getContext( Class<T> statementType, JBlock block ) {
		List<Object> contents = block.getContents();
		if ( contents.isEmpty() )
			throw new IllegalStateException( "required a " + statementType.getSimpleName() + ", but block is empty") ;
		Object object = contents.get( contents.size() -1 ) ;
		try {
			return statementType.cast(object) ;
		} catch ( Throwable ex ) {
			throw new IllegalStateException( "required a " + statementType.getSimpleName() + ", but last statement is a " + object.getClass().getSimpleName(), ex) ;
		}
	}
	
//	private void build( CommandType commandType, Argument argument, Block innerBlock, JBlock block) {
//		argument.visit( new Argument.Visitor<Void>() {
//
//			@Override public Void visitArgument(Argument argument) {
//				throw new UnsupportedOperationException( "argument type \"" + argument.getClass() + "\" not supported" ) ;	
//			}
//
//			@Override public Void visitNothing              (Nothing                             nothing) { build( commandType,               nothing, innerBlock, block ) ; return null ; }
//			@Override public Void visitExpressionList       (ExpressionList               expressionList) { build( commandType,        expressionList, innerBlock, block ) ; return null ; }
//			@Override public Void visitLoopDefinition       (LoopDefinition               loopDefinition) { build( commandType,        loopDefinition, innerBlock, block ) ; return null ; }
//			@Override public Void visitTaggedRoutineCallList(TaggedRoutineCallList taggedRoutineCallList) { build( commandType, taggedRoutineCallList, innerBlock, block ) ; return null ; }
//			@Override public Void visitAssignmentList       (AssignmentList               assignmentList) { build( commandType,        assignmentList, innerBlock, block ) ; return null ; }
//			@Override public Void visitDeclarationList      (DeclarationList             declarationList) { build( commandType,       declarationList, innerBlock, block ) ; return null ; }
//			@Override public Void visitVariableList         (VariableList                   variableList) { build( commandType,          variableList, innerBlock, block ) ; return null ; }
//			@Override public Void visitInputOutputList      (InputOutputList             inputOutputList) { build( commandType,       inputOutputList, innerBlock, block ) ; return null ; }
//			
//		});
//	}
	
	private Builder<JBlock> argumentNotSupported( CommandType commandType, Argument argument ) {
		throw new UnsupportedOperationException( "argument type \"" + argument.getClass() + "\" not supported with command type \"" + commandType + "\"" ) ;	
	}
	
	protected void expect( CommandType requiredCommandType, CommandType commandType, Argument argument ) {
		if ( requiredCommandType != commandType )
			unexpected(commandType, argument);
	}
	
	protected Builder<JBlock> unexpected( CommandType commandType, Argument argument ) {
		throw new UnsupportedOperationException( "command type \"" + commandType + "\" + not supported with argument type \"" + argument.getClass() + "\"" ) ;	
	}

//	protected void build( CommandType commandType, Nothing nothing, Block innerBlock, JBlock block ) {
//		argumentNotSupported(commandType,nothing) ;
//	}
//	
//	protected void build( CommandType commandType, ExpressionList expressionList, Block innerBlock, JBlock block ) {
//		argumentNotSupported(commandType,expressionList) ;
//	}
//	
//	protected void build( CommandType commandType, LoopDefinition loopDefinition, Block innerBlock, JBlock block ) {
//		argumentNotSupported(commandType,loopDefinition) ;
//	}
//	
//	protected void build( CommandType commandType, TaggedRoutineCallList taggedRoutineCallList, Block innerBlock, JBlock block ) {
//		argumentNotSupported(commandType,taggedRoutineCallList) ;
//	}
//	
//	protected void build( CommandType commandType, AssignmentList assignmentList, Block innerBlock, JBlock block ) {
//		argumentNotSupported(commandType,assignmentList) ;
//	}
//	
//	protected void build( CommandType commandType, DeclarationList declarationList, Block innerBlock, JBlock block ) {
//		argumentNotSupported(commandType,declarationList) ;
//	}
//	
//	protected void build( CommandType commandType, VariableList variableList, Block innerBlock, JBlock block ) {
//		argumentNotSupported(commandType,variableList) ;
//	}
//	
//	protected void build( CommandType commandType, InputOutputList inputOutputList, Block innerBlock, JBlock block ) {
//		argumentNotSupported(commandType,inputOutputList) ;
//	}

	public Builder<JBlock> analyze( CommandType commandType, Argument argument, Block innerBlock) {
		return argument.visit( new Argument.Visitor<Builder<JBlock>>() {

			@Override public Builder<JBlock> visitArgument(Argument argument) {
				throw new UnsupportedOperationException( "argument type \"" + argument.getClass() + "\" not supported" ) ;	
			}

			@Override public Builder<JBlock> visitNothing              (Nothing                             nothing) { return analyze( commandType,               nothing, innerBlock ) ; }
			@Override public Builder<JBlock> visitExpressionList       (ExpressionList               expressionList) { return analyze( commandType,        expressionList, innerBlock ) ; }
			@Override public Builder<JBlock> visitLoopDefinition       (LoopDefinition               loopDefinition) { return analyze( commandType,        loopDefinition, innerBlock ) ; }
			@Override public Builder<JBlock> visitTaggedRoutineCallList(TaggedRoutineCallList taggedRoutineCallList) { return analyze( commandType, taggedRoutineCallList, innerBlock ) ; }
			@Override public Builder<JBlock> visitAssignmentList       (AssignmentList               assignmentList) { return analyze( commandType,        assignmentList, innerBlock ) ; }
			@Override public Builder<JBlock> visitDeclarationList      (DeclarationList             declarationList) { return analyze( commandType,       declarationList, innerBlock ) ; }
			@Override public Builder<JBlock> visitVariableList         (VariableList                   variableList) { return analyze( commandType,          variableList, innerBlock ) ; }
			@Override public Builder<JBlock> visitInputOutputList      (InputOutputList             inputOutputList) { return analyze( commandType,       inputOutputList, innerBlock ) ; }
			
		});
	}
	
	protected Builder<JBlock> analyze( CommandType commandType, Nothing nothing, Block innerBlock ) {
		return argumentNotSupported(commandType,nothing) ;
	}
	
	protected Builder<JBlock> analyze( CommandType commandType, ExpressionList expressionList, Block innerBlock ) {
		return argumentNotSupported(commandType,expressionList) ;
	}
	
	protected Builder<JBlock> analyze( CommandType commandType, LoopDefinition loopDefinition, Block innerBlock ) {
		return argumentNotSupported(commandType,loopDefinition) ;
	}
	
	protected Builder<JBlock> analyze( CommandType commandType, TaggedRoutineCallList taggedRoutineCallList, Block innerBlock ) {
		return argumentNotSupported(commandType,taggedRoutineCallList) ;
	}
	
	protected Builder<JBlock> analyze( CommandType commandType, AssignmentList assignmentList, Block innerBlock ) {
		return argumentNotSupported(commandType,assignmentList) ;
	}
	
	protected Builder<JBlock> analyze( CommandType commandType, DeclarationList declarationList, Block innerBlock ) {
		return argumentNotSupported(commandType,declarationList) ;
	}
	
	protected Builder<JBlock> analyze( CommandType commandType, VariableList variableList, Block innerBlock ) {
		return argumentNotSupported(commandType,variableList) ;
	}
	
	protected Builder<JBlock> analyze( CommandType commandType, InputOutputList inputOutputList, Block innerBlock ) {
		return argumentNotSupported(commandType,inputOutputList) ;
	}
	
	protected JavaExpression<?> nullExpr() {
		return m_expressionBuilder.build( null );
	}
	
	protected JavaExpression<?> expr( Expression expression ) {
		return m_expressionBuilder.build( expression  ) ;
	}
	
	protected JavaExpression<?> expr( Expression expression, Representation representation ) {
		return m_expressionBuilder.build(expression, representation);
	}
}