package edu.vanderbilt.clinicalsystems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.writer.FileCodeWriter;

import edu.vanderbilt.clinicalsystems.epic.annotation.RoutineTranslationInfo;
import edu.vanderbilt.clinicalsystems.epic.annotation.RoutineTranslationInfoFactory;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.RoutineDependency;
import edu.vanderbilt.clinicalsystems.epic.lib.Epic;
import edu.vanderbilt.clinicalsystems.m.Core;
import edu.vanderbilt.clinicalsystems.m.lang.Compatibility;
import edu.vanderbilt.clinicalsystems.m.lang.model.Block;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Element;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineElement;
import edu.vanderbilt.clinicalsystems.m.lang.model.RoutineFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Argument;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Assignment;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.AssignmentList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.Destination;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.LoopDefinition;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.OutputExpression;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.TaggedRoutineCallList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.BuiltinFunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.FunctionCall;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.TagReference;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.VariableReference;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaUnitBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineParser;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineParserFactory;

public class GenerateJavaStubsFromMResources {

	private RoutineJavaUnitBuilder m_routineClassBuilder;
	private RoutineParser m_routineParser;

	public static void main(String[] args) throws IOException {
		String destinationJavaPath = args[0] ;
		String sourceMPath = args[1] ;

		new GenerateJavaStubsFromMResources().processAll( Paths.get(sourceMPath), Paths.get(destinationJavaPath) );
	}
	
	private GenerateJavaStubsFromMResources() {
		RoutineParserFactory factory = ServiceLoader.load( RoutineParserFactory.class ).iterator().next() ;
		m_routineParser = factory.createRoutineParser();
		
		m_routineClassBuilder = new RoutineJavaUnitBuilder();
		m_routineClassBuilder.context().env().additionalCompatibility( Compatibility.EXTENSION ) ;
		Core.useLibrariesIn(m_routineClassBuilder.context().env());
		Epic.useLibrariesIn(m_routineClassBuilder.context().env());
	}
	
	private void processAll( Path sourcePath, Path destinationPath) throws IOException {

		Files.walk( sourcePath )
			.filter( Files::isRegularFile )
			.filter( (p)->p.getFileName().toString().endsWith(".m") )
			.forEach( (p)->processOne(p, sourcePath, destinationPath) )
			;
	}
	
	private void gatherDependencies(TagReference tagReference, Set<RoutineDependency> dependencies) {
		dependencies.add( new RoutineTools.TaggedRoutineDependency() {
			@Override public TypeElement dependsOnType() { throw new UnsupportedOperationException() ; }
			@Override public String dependsOnTypeName() { return null ; }
			@Override public String dependsOnRoutineName() { return tagReference.routineName() ; }
			@Override public String dependsOnMethodName() { return null ; }
			@Override public String dependsOnTagName() { return tagReference.tagName() ; }
			@Override public ExecutableElement dependsOnMethod() { throw new UnsupportedOperationException() ; }
		}) ;
	}
	
	private void gatherDependencies(Expression expression, Set<RoutineDependency> dependencies) {
		expression.visit( new Expression.Visitor<Void>() {

			@Override public Void visitVariableReference(VariableReference variable) { return null ; }
			@Override public Void visitFunctionCall(FunctionCall functionCall) { return null ; }
			@Override public Void visitExpression(Expression expression) { return null ; }
			
			@Override public Void visitRoutineFunctionCall( RoutineFunctionCall functionCall ) {
				gatherDependencies( functionCall.tagReference(), dependencies ) ;
				gatherDependencies( functionCall.arguments(), dependencies ) ;
				return null ;
			}
			
		}) ;
	}
	
	private void gatherDependencies(Iterable<Expression> expressions, Set<RoutineDependency> dependencies) {
		for ( Expression expression : expressions )
			gatherDependencies( expression, dependencies);
	}
	
	private void gatherDependencies(Block block, Set<RoutineDependency> dependencies) {
		if ( null == block )
			return ;
		
		for ( RoutineElement element : block.elements() ) {
			if ( element instanceof Command ) {
				Command command = (Command)element ;
				command.argument().visit( new Argument.Visitor<Void>() {

					@Override public Void visitArgument(Argument argument) { /* nothing */ return null ; }

					@Override
					public Void visitLoopDefinition(LoopDefinition loopDefinition) {
						gatherDependencies( loopDefinition.start(), dependencies ) ;
						gatherDependencies( loopDefinition.step(), dependencies ) ;
						gatherDependencies( loopDefinition.stop(), dependencies ) ;
						return null ;
					}

					@Override
					public Void visitAssignmentList( AssignmentList assignmentList) {
						for ( Assignment assignment : assignmentList.elements() ) {
							for ( Destination<?> destination : assignment.destinations() ) {
								destination.visit( new Destination.Visitor<Void>() {

									@Override public Void visitElement(Element element) { /* nothing */ return null ; }

									@Override
									public Void visitBuiltinFunctionCall( BuiltinFunctionCall builtinFunctionCall ) {
										gatherDependencies( builtinFunctionCall.arguments(), dependencies ) ;
										return null;
									}
									
									
								}) ;
							}
							gatherDependencies( assignment.source(), dependencies ) ;
						}
						return null ;
					}

					@Override
					public Void visitTaggedRoutineCallList( TaggedRoutineCallList taggedRoutineCallList) {
						for (TaggedRoutineCall taggedRoutineCall : taggedRoutineCallList.elements()) {
							gatherDependencies( taggedRoutineCall.tagReference() , dependencies);
							gatherDependencies( taggedRoutineCall.arguments(), dependencies ) ;
						}
						return null ;
					}

					@Override
					public Void visitExpressionList( ExpressionList expressionList) {
						gatherDependencies( expressionList.elements(), dependencies ) ;
						return null;
					}

					@Override
					public Void visitInputOutputList( InputOutputList inputOutputList) {
						for ( InputOutput inputOutput : inputOutputList.elements() ) {
							inputOutput.visit( new InputOutput.Visitor<Void>() {
								
								@Override public Void visitInputOutput( InputOutput inputOutput) { /* nothing */ return null ; }

								@Override public Void visitOutputExpression( OutputExpression outputExpression ) {
									gatherDependencies( outputExpression.expression(), dependencies);
									return null ;
								}
								
								
							}) ;
						}
						return null;
					}
					
					
					
				}) ;

				gatherDependencies( command.block(), dependencies ) ;
			}

		}
	}
	
	private void processOne( Path src, Path sourcePath, Path destinationPath ) {
		try {
		Path srcRelativePath = sourcePath.relativize( src ) ;
		Path dstRelativePath = srcRelativePath.resolveSibling( src.getFileName().toString().replaceAll("\\.[^.]$", ".java")) ;
		try ( InputStream in = new FileInputStream( src.toFile() ) ) {
				
			Routine routine = m_routineParser.parse(in) ;
			RoutineTranslationInfoFactory routineTranslationInfoFactory = new RoutineTranslationInfoFactory() ;
			Set<RoutineDependency> dependencies = new HashSet<RoutineDependency>();
			gatherDependencies( routine.root(), dependencies ) ;
			RoutineTranslationInfo translation = routineTranslationInfoFactory.create(routine, dependencies) ;
			
			Path metaRelativePath = destinationPath.resolve( dstRelativePath.resolveSibling( src.getFileName().toString().replaceAll("\\.[^.]$", ".xml")) );
			metaRelativePath.getParent().toFile().mkdirs() ;
			try ( Writer resourceWriter = new FileWriter(metaRelativePath.toFile()) ) {
				routineTranslationInfoFactory.write(translation, resourceWriter);
			}

			String packageName = StreamSupport.stream( dstRelativePath.getParent().spliterator(), false )
					.map( Path::toString )
					.collect( Collectors.joining(".") ) ;
			writeRoutineClass( routine, packageName, destinationPath.toFile() ) ;

		}
		
		} catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}

	private void writeRoutineClass( Routine routine, String packageName, File destinationFile ) throws Exception {
		m_routineClassBuilder.build( packageName, routine);
		CodeWriter cw = new FileCodeWriter( destinationFile );
		try {
			m_routineClassBuilder.context().codeModel().build(cw);
		} finally {
			cw.close();
		}
	}

}
