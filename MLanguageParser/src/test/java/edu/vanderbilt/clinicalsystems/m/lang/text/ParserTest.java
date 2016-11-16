package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle;
import edu.vanderbilt.clinicalsystems.m.lang.Scope;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.DeclarationList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.AssignmentContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.CommandContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.CommentContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.DeclarationContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.FormalArgumentContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.RoutineContext;
import edu.vanderbilt.clinicalsystems.m.lang.text.MumpsParser.TagContext;

public class ParserTest {


	@Test
	public void canParseOneTag() throws IOException {
		StringReader r = new StringReader("MyTag") ;
		MumpsLexer lexer = new MumpsLexer(new ANTLRInputStream(r)) ;
		CommonTokenStream tokens = new CommonTokenStream(lexer);
//		assertThat( tokens.get(0).getText(), equalTo("MyTag") );
	}
	
	@Test
	public void canParse() throws IOException, RoutineWriterException {
		final Routine routine = new Routine() ;
		
//		try ( InputStream is = ParserTest.class.getResourceAsStream("EALIBECF1.m") ) {
		try ( InputStream is = ParserTest.class.getResourceAsStream("Sample.m") ) {
			MumpsLexer lexer = new MumpsLexer(new ANTLRInputStream(is)) ;
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			MumpsParser parser = new MumpsParser(tokens) ;
			MumpsParser.RoutineContext routineContext = parser.routine() ;
			
			MumpsParserListener extractor = new MumpsParserBaseListener() {

				private String m_localRoutineName ;
				
				@Override
				public void enterRoutine(RoutineContext ctx) {
					m_localRoutineName = ctx.tag().getToken( MumpsLexer.Name, 0).getText() ;
				}
				
				@Override
				public void enterTag(TagContext ctx) {
					String tagName = ctx.getToken(MumpsLexer.Name,0).getText();
					
					List<ParameterName> parameterNames = new ArrayList<ParameterName>() ;
					for ( FormalArgumentContext formalArgumentContext : ctx.formalArgument() )
						parameterNames.add( new ParameterName( formalArgumentContext.getToken(MumpsLexer.Name, 0).getText()) ) ;
					
					Tag tag = new Tag( tagName, parameterNames ) ;
					routine.appendElement( tag );
				}
				
				@Override
				public void enterComment(CommentContext ctx) {
					TerminalNode commentText = ctx.getToken(MumpsLexer.CommentText, 0);
					Comment comment = new Comment( null == commentText ? "" : commentText.getText() ) ;
					routine.appendElement( comment );
				}

				@Override
				public void exitCommand(CommandContext ctx) {
					TerminalNode commandTypeToken = ctx.getToken(MumpsLexer.Name, 0) ;
					CommandType commandType = CommandType.valueOfSymbol( commandTypeToken.getText() ) ;
					
					Argument arg = null ;
					switch ( commandType ) {
					case NEW:
						DeclarationList declList = new DeclarationList() ;
						for (DeclarationContext declarationContext : ctx.declarationList().declaration())
							declList.append( new VariableReference(Scope.LOCAL, ReferenceStyle.DIRECT, declarationContext.getToken(MumpsLexer.Name, 0).getText() ) ) ;
						arg = declList ;
						break ;
					case SET:
						AssignmentList assignList = new AssignmentList() ;
						if ( null != ctx.assignmentList() ) {
							for (AssignmentContext assignmentContext : ctx.assignmentList().assignment()) {
								VariableReference destination = new VariableReference(Scope.LOCAL, ReferenceStyle.DIRECT, assignmentContext.destination().getToken(MumpsLexer.Name, 0).getText() ) ;
								Expression source = assignmentContext.expression().result ; 
								assignList.append( new Assignment(destination,source) ) ;
							}
						}
						arg = assignList ;
						break ;
					case IF:
						ExpressionList expressionList = new ExpressionList( ctx.expressionList().result );
						arg = expressionList ;
						break ;
					default:
						arg = Argument.NOTHING ;
					}
					Command command = new Command( commandType, arg ) ;
					routine.appendElement( command );
				}
				
			};
			ParseTreeWalker.DEFAULT.walk( extractor, routineContext ) ;
			
		} finally {
		
			RoutineNativeFormatter routineFormatter = new RoutineNativeFormatter();
			routineFormatter.options().setCommandsPerLineLimit(1) ;
			routineFormatter.options().setCommentsPerLineLimit(1) ;
//			RoutineTreeFormatter routineFormatter = new RoutineTreeFormatter();
			
			System.out.println("---------- Start Routine ----------" ) ;
			routine.write( new RoutineLinearWriter( new PrintWriter(System.out), routineFormatter) );
			System.out.println("----------- End Routine -----------" ) ;
			
		}
	}
	
}
