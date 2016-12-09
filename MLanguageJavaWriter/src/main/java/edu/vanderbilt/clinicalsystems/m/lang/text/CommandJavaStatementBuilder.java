package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.util.List;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
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

public abstract class CommandJavaStatementBuilder extends RoutineJavaBuilder {
	private final JBlock m_block ;
	private final RoutineJavaExpressionBuilder m_expressionBuilder ;
	
	public CommandJavaStatementBuilder( RoutineJavaBuilderContext builderContext, JBlock block, RoutineJavaExpressionBuilder expressionBuilder ) {
		super(builderContext) ;
		m_block = block ;
		m_expressionBuilder = expressionBuilder ;
	}

	protected JClass outerClass() { return m_expressionBuilder.outerClass() ; }
	protected JBlock block() { return m_block; }
	protected RoutineJavaExpressionBuilder expression() { return m_expressionBuilder; }
	
	protected <T extends JStatement> T getContext( Class<T> statementType ) {
		List<Object> contents = m_block.getContents();
		if ( contents.isEmpty() )
			throw new IllegalStateException( "required a " + statementType.getSimpleName() + ", but block is empty") ;
		Object object = contents.get( contents.size() -1 ) ;
		try {
			return statementType.cast(object) ;
		} catch ( Throwable ex ) {
			throw new IllegalStateException( "required a " + statementType.getSimpleName() + ", but last statement is a " + object.getClass().getSimpleName(), ex) ;
		}
	}
	
	public void build( CommandType commandType, Argument argument, Block block ) {
		argument.visit( new Argument.Visitor<Void>() {

			@Override public Void visitArgument(Argument argument) {
				throw new UnsupportedOperationException( "argument type \"" + argument.getClass() + "\" not supported" ) ;	
			}

			@Override public Void visitNothing              (Nothing                             nothing) { build( commandType,               nothing, block ) ; return null ; }
			@Override public Void visitExpressionList       (ExpressionList               expressionList) { build( commandType,        expressionList, block ) ; return null ; }
			@Override public Void visitLoopDefinition       (LoopDefinition               loopDefinition) { build( commandType,        loopDefinition, block ) ; return null ; }
			@Override public Void visitTaggedRoutineCallList(TaggedRoutineCallList taggedRoutineCallList) { build( commandType, taggedRoutineCallList, block ) ; return null ; }
			@Override public Void visitAssignmentList       (AssignmentList               assignmentList) { build( commandType,        assignmentList, block ) ; return null ; }
			@Override public Void visitDeclarationList      (DeclarationList             declarationList) { build( commandType,       declarationList, block ) ; return null ; }
			@Override public Void visitVariableList         (VariableList                   variableList) { build( commandType,          variableList, block ) ; return null ; }
			@Override public Void visitInputOutputList      (InputOutputList             inputOutputList) { build( commandType,       inputOutputList, block ) ; return null ; }
			
		});
	}
	
	private void argumentNotSupported( CommandType commandType, Argument argument ) {
		throw new UnsupportedOperationException( "argument type \"" + argument.getClass() + "\" not supported with command type \"" + commandType + "\"" ) ;	
	}
	
	protected void expect( CommandType requiredCommandType, CommandType commandType, Argument argument ) {
		if ( requiredCommandType != commandType )
			unexpected(commandType, argument);
	}
	
	protected void unexpected( CommandType commandType, Argument argument ) {
		throw new UnsupportedOperationException( "command type \"" + commandType + "\" + not supported with argument type \"" + argument.getClass() + "\"" ) ;	
	}

	protected void build( CommandType commandType, Nothing nothing, Block block ) {
		argumentNotSupported(commandType,nothing) ;
	}
	
	protected void build( CommandType commandType, ExpressionList expressionList, Block block ) {
		argumentNotSupported(commandType,expressionList) ;
	}
	
	protected void build( CommandType commandType, LoopDefinition loopDefinition, Block block ) {
		argumentNotSupported(commandType,loopDefinition) ;
	}
	
	protected void build( CommandType commandType, TaggedRoutineCallList taggedRoutineCallList, Block block ) {
		argumentNotSupported(commandType,taggedRoutineCallList) ;
	}
	
	protected void build( CommandType commandType, AssignmentList assignmentList, Block block ) {
		argumentNotSupported(commandType,assignmentList) ;
	}
	
	protected void build( CommandType commandType, DeclarationList declarationList, Block block ) {
		argumentNotSupported(commandType,declarationList) ;
	}
	
	protected void build( CommandType commandType, VariableList variableList, Block block ) {
		argumentNotSupported(commandType,variableList) ;
	}
	
	protected void build( CommandType commandType, InputOutputList inputOutputList, Block block ) {
		argumentNotSupported(commandType,inputOutputList) ;
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