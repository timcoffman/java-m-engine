parser grammar MumpsParser;

options { tokenVocab=MumpsLexer; }

@header {
import static java.util.Objects.requireNonNull;
import static edu.vanderbilt.clinicalsystems.m.lang.Scope.LOCAL;
import static edu.vanderbilt.clinicalsystems.m.lang.Scope.GLOBAL;
import static edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle.DIRECT;
import static edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle.INDIRECT;
import edu.vanderbilt.clinicalsystems.m.lang.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.* ;
}

@members {
	Converter _converter ;
}

/* entry point */
routine
	: tag { _converter = new Converter($tag.text); } routineElement* EOF
	;

inlineBlock
	returns [ InlineBlock result ] @after { requireNonNull($result); }
	: inlineBlockElement* EndOfLine
	;

inlineBlockElement
	: ( Space | OtherWhitespace )+ command
	| ( Space | OtherWhitespace )* comment
	;

routineElement
	: tag
	| ( Space | OtherWhitespace )+ command
	| ( Space | OtherWhitespace )* comment
	| EndOfLine
	;

tag
	: tagName=Name ( OpenParenthesis ( formalArgument ( Comma formalArgument )* )? CloseParenthesis )?
	;

formalArgument
	: Name
	;

comment
	: Semicolon CommentText? CommentEnd
	;

command
	returns [ Command result ]
	locals [ CommandType _commandType, Expression _condition, Argument _argument, Block _block ]
	@after { requireNonNull($result); }
	: cmd=Name { $_commandType = CommandType.valueOfSymbol($cmd.text); }
            ( Colon c=expression { $_condition = $c.result; } )?
            ( EndOfLine { $_argument = Argument.NOTHING; }
            | Space a=argument[ $_commandType ] { $_argument = $a.result; }
            | Space b=block[ $_commandType, $_argument ] { $_block = $b.result; }
            )
        {
        	$result = new Command( $_condition, $_commandType, $_argument, $_block ) ;	
        }
	;

argument [ CommandType _commandType ]
	returns [ Argument result ] @after { requireNonNull($result); }
	: { $_commandType == CommandType.SET }?  al=assignmentList    { $result = _converter.argumentFrom( $al.ctx); }
	| { $_commandType == CommandType.NEW }?  dl=declarationList   { $result = _converter.argumentFrom( $dl.ctx); }
	| { $_commandType == CommandType.FOR }?  ld=loopDefinition    { $result = _converter.argumentFrom( $ld.ctx); }
	| { $_commandType == CommandType.DO  }? trc=taggedRoutineCall { $result = _converter.argumentFrom($trc.ctx); }
	| { $_commandType == CommandType.IF  }?  el=expressionList    { $result = _converter.argumentFrom( $el.ctx); }
	|                                         e=expression        { $result = _converter.argumentFrom(  $e.ctx); }
	| /* nothing */
	;

declarationList
	returns [ List<VariableReference> result ] @after { requireNonNull($result); }
	: declaration ( Comma declaration )* { $result = asList($ctx); }
	;

declaration
	returns [ VariableReference result ] @after { requireNonNull($result); } 
	: n=Name { $result = _converter.createVariableReference( LOCAL, DIRECT, $n.text, _converter.asList(null) ); }
	;

expressionList
	returns [ List<Expression> result ] @after { requireNonNull($result); }
	: expression ( Comma expression )* { $result = _converter.asList($ctx); }
	;

loopDefinition
	: Name Equals expression ( Colon expression ( Colon expression )? )?
	;

taggedRoutineCall
	: Name (Caret Name)?
	| Caret Name
	;

assignmentList
	returns [ List<Assignment> result ]
	: assignment ( Comma assignment )* { $result = _converter.asList($ctx); }
	;
	
assignment
	returns [ Assignment result ] @after { requireNonNull($result); }
	: d=destination Equals s=expression { $result = new Assignment( $d.result, $s.result ); }
	;

destination
	returns [ VariableReference result ] @after { requireNonNull($result); }
	: n=Name ( OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createVariableReference( LOCAL, DIRECT, $n, $el.ctx ); }
	| AtSign Name ( AtSign OpenParenthesis expression ( Comma expression )* CloseParenthesis )?
	| Dollar Name OpenParenthesis ( expression (Comma expression)* ) CloseParenthesis
	;

block [ CommandType _commandType, Argument _argument ]
	returns [ Block result ] @after { requireNonNull($result); }
	: { $_commandType == CommandType.FOR  }? b=inlineBlock { $result = $b.result ; }
	| { $_commandType == CommandType.IF   }? b=inlineBlock { $result = $b.result ; }
	| { $_commandType == CommandType.ELSE }? b=inlineBlock { $result = $b.result ; }
	| /* nothing */
	;

expression
	returns [ Expression result ] @after { requireNonNull($result); }
	: OpenParenthesis e=expression CloseParenthesis { $result = $e.result; }
	| ( Plus | Minus )? Digit+ ( Dot Digit+ )? { $result = new Constant($text); }
	| Quote s=quotedSequence EndQuote          { $result = new Constant($s.text); }
	|        Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createVariableReference( GLOBAL,   DIRECT, $n, $el.ctx ); }
	|              n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createVariableReference(  LOCAL,   DIRECT, $n, $el.ctx ); }
	| AtSign Caret n=Name ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createVariableReference( GLOBAL, INDIRECT, $n, $el.ctx ); }
	| AtSign       n=Name ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createVariableReference(  LOCAL, INDIRECT, $n, $el.ctx ); }
	| Dollar Dollar n=Name ( Caret r=Name )? OpenParenthesis el=expressionList? CloseParenthesis { $result = _converter.createRoutineFunctionCall( LOCAL, DIRECT, $n, $r, $el.ctx); }
	|        Dollar n=Name                   OpenParenthesis el=expressionList? CloseParenthesis { $result = _converter.createBuiltinFunctionCall( $n, $el.ctx); }
	| lhs=expression Underscore rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.CONCAT, $rhs.result); }
	| lhs=expression    Plus    rhs=expression
	| lhs=expression   Minus    rhs=expression
	;

quotedSequence
	: ( EscapedCharacter | UnescapedCharacter )*
	;


	