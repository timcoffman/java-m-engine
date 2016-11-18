package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.InlineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.FormatCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputVariable;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Nothing;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.OutputExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.VariableList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BinaryOperation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinSystemVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.ConditionalExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.DirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.IndirectVariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.InvalidExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Operation;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.UnaryOperation;

public class RoutineLinearWriter implements RoutineWriter {

	private final Writer m_writer ;
	private final RoutineFormatter m_routineFormatter ;
	
	public RoutineLinearWriter( Writer writer, RoutineFormatter routineFormatter ) {
		m_writer = writer ;
		m_routineFormatter = routineFormatter ;
	}
	
	@Override
	public void write( Routine routine ) throws RoutineWriterException {
		try {
			m_routineFormatter.openRoutine( m_writer ) ;
			routine.root().write(this);
			m_routineFormatter.closeRoutine( m_writer ) ;
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write( InlineBlock block ) throws RoutineWriterException {
		if ( block.isEmpty() ) return ;
		try {
			m_routineFormatter.openInlineBlock( m_writer );
			for ( RoutineElement element : block.elements() )
				element.write(this);
			m_routineFormatter.closeInlineBlock( m_writer );
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write( MultilineBlock block ) throws RoutineWriterException {
		if ( block.isEmpty() ) return ;
		try {
			m_routineFormatter.openMultilineBlock( m_writer );
			for ( RoutineElement element : block.elements() )
				element.write(this);
			m_routineFormatter.closeMultilineBlock( m_writer );
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write( Command command ) throws RoutineWriterException {
		try {
			m_routineFormatter.writeCommand( command.commandType(), m_writer ) ;
			if ( null != command.condition() ) {
				m_routineFormatter.writeConditionDelimiter( m_writer ) ;
				command.condition().write(this) ;
			}
			m_routineFormatter.writeCommandDelimiter( m_writer ) ;
			command.argument().write(this) ;
			if ( null != command.block() ) {
				command.block().write(this) ;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write( Comment comment ) throws RoutineWriterException {
		try {
			m_routineFormatter.openComment(m_writer);
			m_routineFormatter.writeCommentText(comment.text(), m_writer);
			m_routineFormatter.closeComment(m_writer);
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write(Tag tag) throws RoutineWriterException {
		try {
			m_routineFormatter.writeTagName( tag.name(), m_writer);

			Iterator<ParameterName> i = tag.parameterNames().iterator() ;
			if ( ! i.hasNext() ) return ;
			
			m_routineFormatter.openTagParameters(m_writer);
			m_routineFormatter.writeParameter(i.next().name(), m_writer);
			while ( i.hasNext() ) {
				m_routineFormatter.writeTagParametersDelimiter(m_writer);
				m_routineFormatter.writeParameter(i.next().name(), m_writer);
			}
			m_routineFormatter.closeTagParameters(m_writer);
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write(ParameterName parameterName) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void write( Nothing nothing ) throws RoutineWriterException {
		try {
			m_routineFormatter.writeNoArgument(m_writer);
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write( AssignmentList assignmentList ) throws RoutineWriterException {
		try {
			Iterator<? extends Element> i = assignmentList.elements().iterator() ;
			if ( i.hasNext() )
				i.next().write(this) ;
			while ( i.hasNext() ) {
				m_routineFormatter.writeAssignmentDelimiter(m_writer);
				i.next().write(this) ;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write( Assignment assignment ) throws RoutineWriterException {
		try {
			assignment.destination().write(this) ;
			m_routineFormatter.writeAssignmentOperator(m_writer);
			assignment.source().write(this) ;
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	private boolean isConstant( long checkValue, Expression expr ) {
		if ( expr instanceof Constant )
			if ( ((Constant)expr).representsNumber( checkValue ) )
				return true ;
		return false ;
	}
	
	@SuppressWarnings("unused")
	private boolean isConstant( double checkValue, Expression expr ) {
		if ( expr instanceof Constant )
			if ( ((Constant)expr).representsNumber( checkValue ) )
				return true ;
		return false ;
	}

	@SuppressWarnings("unused")
	private boolean isConstant( String checkValue, Expression expr ) {
		if ( expr instanceof Constant )
			if ( ((Constant)expr).equals( checkValue ) )
				return true ;
		return false ;
	}

	@Override
	public void write( LoopDefinition loopDefinition ) throws RoutineWriterException {
		try {
			DirectVariableReference destination = loopDefinition.destination();
			Expression start = loopDefinition.start() ;
			Expression stop = loopDefinition.stop() ;
			Expression step = null != loopDefinition.step() ? loopDefinition.step() : Constant.from("1") ;
			
			destination.write(this) ;
			m_routineFormatter.writeAssignmentOperator(m_writer);
			start.write(this) ;
			
			if ( null != stop || !isConstant(1,step) ) {
				
				m_routineFormatter.writeLoopDefinitionDelimiter(m_writer) ;
				loopDefinition.step().write(this) ;
				
				if ( stop != null ) {
					m_routineFormatter.writeLoopDefinitionDelimiter(m_writer) ;
					loopDefinition.stop().write(this) ;
				}
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write( TaggedRoutineCall taggedRoutineCall ) throws RoutineWriterException {
		try {
			m_routineFormatter.writeTaggedRoutine( taggedRoutineCall.tagName(), taggedRoutineCall.routineName(), taggedRoutineCall.routineAccess(), m_writer);
			writeFunctionParameterList( taggedRoutineCall.arguments() );
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	private void writeFunctionParameterList(Iterable<Expression> expressions) throws IOException, RoutineWriterException {
		Iterator<Expression> i = expressions.iterator() ;
		
		m_routineFormatter.openFunctionParameterList(m_writer);
		if ( i.hasNext() ) {
			i.next().write(this);
			while ( i.hasNext() ) {
				m_routineFormatter.writeFunctionParameterDelimiter(m_writer);
				i.next().write(this);
			}
		}
		m_routineFormatter.closeFunctionParameterList(m_writer);
	}
	
	@Override
	public void write( ExpressionList expressionList ) throws RoutineWriterException {
		try {
			Iterator<? extends Element> i = expressionList.elements().iterator() ;
			if ( i.hasNext() )
				i.next().write(this) ;
			while ( i.hasNext() ) {
				m_routineFormatter.writeExpressionDelimiter(m_writer);
				i.next().write(this) ;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write( DeclarationList declarationList ) throws RoutineWriterException {
		try {
			Iterator<? extends Element> i = declarationList.elements().iterator() ;
			if ( i.hasNext() )
				i.next().write(this) ;
			while ( i.hasNext() ) {
				m_routineFormatter.writeDeclarationDelimiter(m_writer);
				i.next().write(this) ;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write( VariableList variableList ) throws RoutineWriterException {
		try {
			Iterator<? extends Element> i = variableList.elements().iterator() ;
			if ( i.hasNext() )
				i.next().write(this) ;
			while ( i.hasNext() ) {
				m_routineFormatter.writeDeclarationDelimiter(m_writer);
				i.next().write(this) ;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write( InputOutputList inputOutputList ) throws RoutineWriterException {
		try {
			Iterator<? extends Element> i = inputOutputList.elements().iterator() ;
			if ( i.hasNext() )
				i.next().write(this) ;
			while ( i.hasNext() ) {
				m_routineFormatter.writeDeclarationDelimiter(m_writer);
				i.next().write(this) ;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write( FormatCommand formatCommand ) throws RoutineWriterException {
		try {
			m_writer.append( formatCommand.text() ) ;
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write( InputOutputVariable variable ) throws RoutineWriterException {
		variable.variable().write(this);
	}
	
	@Override
	public void write( OutputExpression expression ) throws RoutineWriterException {
		expression.expression().write(this);
	}
	
	@Override
	public void write(InvalidExpression invalidExpression) throws RoutineWriterException {
		try {
			m_routineFormatter.writeInvalidExpression( invalidExpression.reason(), m_writer);
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	@Override
	public void write(ConditionalExpression conditionalExpression) throws RoutineWriterException {
		try {
			m_routineFormatter.openExpressionPrecondition(m_writer);
			conditionalExpression.condition().write(this);
			m_routineFormatter.closeExpressionPrecondition(m_writer);
			conditionalExpression.expression().write(this);
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}
	
	
	@Override
	public void write(RoutineFunctionCall routineFunctionCall) throws RoutineWriterException {
		try {
			m_routineFormatter.writeFunction( routineFunctionCall.tagReference().tagName(), routineFunctionCall.tagReference().routineName(), routineFunctionCall.tagReference().routineAccess(), m_writer);
			writeFunctionParameterList( routineFunctionCall.arguments() );
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write(BuiltinFunctionCall builtinFunctionCall) throws RoutineWriterException {
		try {
			m_routineFormatter.writeBuiltinFunction( builtinFunctionCall.builtinFunction(), m_writer);
			writeFunctionParameterList( builtinFunctionCall.arguments() );
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write(BinaryOperation operation) throws RoutineWriterException {
		try {
			operation.leftHandSide().write(this) ;
			m_routineFormatter.writeOperator( operation.operator(), m_writer);
			if ( operation.rightHandSide() instanceof Operation ) {
				m_routineFormatter.openExpressionGroup( m_writer );
				operation.rightHandSide().write(this) ;
				m_routineFormatter.closeExpressionGroup( m_writer );
			} else {
				operation.rightHandSide().write(this) ;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write(UnaryOperation operation) throws RoutineWriterException {
		try {
			m_routineFormatter.writeOperator( operation.operator(), m_writer);
			if ( operation.operand() instanceof Operation ) {
				m_routineFormatter.openExpressionGroup( m_writer );
				operation.operand().write(this) ;
				m_routineFormatter.closeExpressionGroup( m_writer );
			} else {
				operation.operand().write(this) ;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

	@Override
	public void write(DirectVariableReference variable) throws RoutineWriterException {
		try {

			switch ( variable.parameterPassMethod() ) {
			case BY_REFERENCE:
				m_routineFormatter.writeVariablePassedByReference(variable.scope(), variable.variableName(), m_writer);
				break;
			case BY_VALUE:
				m_routineFormatter.writeDirectVariable( variable.scope(), variable.variableName(), m_writer);
				break;
			}
			Iterator<Expression> i = variable.keys().iterator() ;
			if ( i.hasNext() ) {
				m_routineFormatter.openVariableKeys(m_writer);
				i.next().write(this);
				while ( i.hasNext() ) {
					m_routineFormatter.writeVariableKeysDelimiter(m_writer);
					i.next().write(this);
				}
				m_routineFormatter.closeVariableKeys(m_writer);
			 }
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
		
	}
	
	@Override
	public void write(IndirectVariableReference variable) throws RoutineWriterException {
		try {
			
			m_routineFormatter.writeIndirectionOperator( m_writer);
			
			variable.variableNameProducer().write(this);
			
			Iterator<Expression> i = variable.keys().iterator() ;
			if ( i.hasNext() ) {
				m_routineFormatter.writeIndirectionOperator( m_writer);
				m_routineFormatter.openVariableKeys(m_writer);
				i.next().write(this);
				while ( i.hasNext() ) {
					m_routineFormatter.writeVariableKeysDelimiter(m_writer);
					i.next().write(this);
				}
				m_routineFormatter.closeVariableKeys(m_writer);
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
		
	}

	@Override
	public void write(BuiltinVariableReference variable) throws RoutineWriterException {
		try {
			m_routineFormatter.writeBuiltinVariable( variable.builtinVariable(), m_writer ) ;
			
			Iterator<Expression> i = variable.keys().iterator() ;
			if ( i.hasNext() ) {
				m_routineFormatter.writeIndirectionOperator( m_writer);
				m_routineFormatter.openVariableKeys(m_writer);
				i.next().write(this);
				while ( i.hasNext() ) {
					m_routineFormatter.writeVariableKeysDelimiter(m_writer);
					i.next().write(this);
				}
				m_routineFormatter.closeVariableKeys(m_writer);
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
		
	}
	
	@Override
	public void write(BuiltinSystemVariableReference variable) throws RoutineWriterException {
		try {
			m_routineFormatter.writeBuiltinSystemVariable( variable.builtinSystemVariable(), m_writer ) ;
			
			Iterator<Expression> i = variable.keys().iterator() ;
			if ( i.hasNext() ) {
				m_routineFormatter.writeIndirectionOperator( m_writer);
				m_routineFormatter.openVariableKeys(m_writer);
				i.next().write(this);
				while ( i.hasNext() ) {
					m_routineFormatter.writeVariableKeysDelimiter(m_writer);
					i.next().write(this);
				}
				m_routineFormatter.closeVariableKeys(m_writer);
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
		
	}
	
	@Override
	public void write(TagReference tag) throws RoutineWriterException {
		try {

			switch ( tag.referenceStyle() ) {
			case DIRECT:
				m_routineFormatter.writeTaggedRoutine( tag.tagName(), tag.routineName(), tag.routineAccess(), m_writer);
				break ;
			case INDIRECT:
				m_routineFormatter.writeTaggedRoutine( tag.tagName(), tag.routineName(), tag.routineAccess(), m_writer);
				break;
			}
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
		
	}

	@Override
	public void write(Constant constant) throws RoutineWriterException {
		try {
			if ( constant.representsNumber() )
				m_routineFormatter.writeNumberConstant( constant.value(), m_writer ) ;
			else
				m_routineFormatter.writeStringConstant( constant.value(), m_writer ) ;
		} catch ( IOException ex ) {
			throw new RoutineWriterException(ex) ;
		}
	}

}
