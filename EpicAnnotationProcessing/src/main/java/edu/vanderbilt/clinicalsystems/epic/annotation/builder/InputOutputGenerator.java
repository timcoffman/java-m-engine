package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.MethodResolution;
import edu.vanderbilt.clinicalsystems.m.core.annotation.ReadWriteCode;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.FormatCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;

public class InputOutputGenerator extends Generator<InputOutput,Ast.Expression> {
		
	public InputOutputGenerator(RoutineTools builderTools) {
		super( builderTools ) ;
	}
	
	private final class InputOutputInterpreter extends Ast.Interpreter<InputOutput, Listener> {
		
		public InputOutputInterpreter() { super(tools()) ; }

		
		
		@Override
		public InputOutput visitExpression(Ast.Expression expressionNode, Listener listener) {
			return buildInputOutput( expressionNode, listener ) ;
		}

		@Override
		public InputOutput visitMethodInvocation(Ast.MethodInvocation methodInvocationNode, Listener listener) {
			return buildInputOutput( methodInvocationNode, listener ) ;
		}

	}
	
	@Override
	public InputOutput generate( Ast.Expression expression, Listener listener ) {
		return expression.accept( new InputOutputInterpreter(), listener );
	}

	private InputOutput buildInputOutput(Ast.MethodInvocation methodInvocationNode, Listener listener) {
		MethodResolution methodInvocationTarget = tools().resolveMethodInvocationTarget( methodInvocationNode );
		
		ReadWriteCode readWriteCodeAnnotation = methodInvocationTarget.declaration().getAnnotation( ReadWriteCode.class ) ;
		if ( null != readWriteCodeAnnotation ) {
			switch ( readWriteCodeAnnotation.value() ) {
			case NEWLINE:
				return FormatCommand.carriageReturn() ;
			case PAGEFEED:
				return FormatCommand.pageFeed() ;
			case COLUMN_ALIGN:
				Expression arg1 = tools().expressions().generate( methodInvocationNode.arguments().get(0), listener );
				if ( arg1 instanceof Constant ) {
					long col = ((Constant)arg1).toLong() ;
					return FormatCommand.column(col) ;
				}
			default:
				report( RoutineTools.ReportType.ERROR, "unrecognized read/write code \"" + readWriteCodeAnnotation.value() + "\"", methodInvocationNode ) ;
			}
		}
		
		return buildInputOutput( (Ast.Expression)methodInvocationNode, listener ) ;
	}

	private InputOutput buildInputOutput(Ast.Expression expressionNode, Listener listener) {
		
		Expression inputOutputSource = tools().expressions().generate( expressionNode, listener);
		if ( inputOutputSource instanceof VariableReference )
			return InputOutput.wrap( (VariableReference)inputOutputSource ) ;
			
		return InputOutput.wrap( inputOutputSource ) ;
	}

}
