package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import edu.vanderbilt.clinicalsystems.epic.annotation.EpicTag;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.Comment;
import edu.vanderbilt.clinicalsystems.m.lang.model.Directive;
import edu.vanderbilt.clinicalsystems.m.lang.model.MultilineBlock;
import edu.vanderbilt.clinicalsystems.m.lang.model.ParameterName;
import edu.vanderbilt.clinicalsystems.m.lang.model.Tag;

public class TagGenerator extends Generator<MultilineBlock,ExecutableElement> {
		
	public TagGenerator(RoutineTools builderTools) {
		super( builderTools ) ;
	}
	
	private String determineTagName(Element sourceType) {
		EpicTag tagAnnotation = sourceType.getAnnotation( EpicTag.class ) ;
		if ( null == tagAnnotation )
			return sourceType.getSimpleName().toString() ;
		if ( EpicTag.DEFAULT_NAME.equals( tagAnnotation.value() ) )
			return sourceType.getSimpleName().toString() ;
		return tagAnnotation.value();
	}
	
	@Override
	public MultilineBlock generate(ExecutableElement tagSourceMethod, Listener listener ) {
		List<ParameterName> parameterNames = new ArrayList<ParameterName>() ;
		for ( VariableElement parameterElement : tagSourceMethod.getParameters() ) {
			if ( tools().isTypeOfString(parameterElement) ) {
				/* OK */
			} else if ( tools().isTypeOfNumber(parameterElement) ) {
				/* OK */
			} else if ( tools().isLibraryType(parameterElement) ) {
				/* OK */
			} else if ( tools().isNativeType(parameterElement) ) {
				/* OK */
			} else {
				report(RoutineTools.ReportType.WARNING, "tags cannot accept parameters of type " + parameterElement.asType(), parameterElement);
			}
			parameterNames.add( new ParameterName( parameterElement.getSimpleName().toString() ) ) ;
		}
		
		MultilineBlock block = new MultilineBlock() ;
		
		String tagName = determineTagName( tagSourceMethod ) ;
		block.appendElement( new Tag( tagName, parameterNames ) );
		List<String> comments = tools().commentsOnElement( tagSourceMethod );
		if ( comments.isEmpty() ) {
			block.appendElement( new Comment( "" ) );
		} else {
			for ( String comment : comments )
				block.appendElement( new Comment( comment ) );
		}
		block.appendElement( new Comment( "Generated-From: " + tagSourceMethod.getReturnType() + " " + tagSourceMethod.toString() ) );

		block.appendElements( tools().blocks().generate( tools().getMethod( tagSourceMethod ).body(), null ) );
		
		TypeMirror mirrorForReturnType = tagSourceMethod.getReturnType() ;
		if ( mirrorForReturnType.getKind() == TypeKind.VOID ) {
			block.appendElement( new Command( CommandType.QUIT ) );
			block.appendElement( new Comment( "method returns " + mirrorForReturnType ) );
		}

		block.appendElement( Directive.endOfTag() );
		
		return block;
	}

}
