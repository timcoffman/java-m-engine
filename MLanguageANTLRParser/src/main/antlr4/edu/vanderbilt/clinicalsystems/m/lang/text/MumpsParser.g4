parser grammar MumpsParser;

options { tokenVocab=MumpsLexer; }

@header {
import static java.util.Objects.requireNonNull;
import static edu.vanderbilt.clinicalsystems.m.lang.Scope.LOCAL;
import static edu.vanderbilt.clinicalsystems.m.lang.Scope.GLOBAL;
import static edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle.DIRECT;
import static edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle.INDIRECT;
import static edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod.BY_VALUE;
import static edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod.BY_REFERENCE;
import edu.vanderbilt.clinicalsystems.m.lang.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.* ;
}

@members {
	Converter _converter = new Converter();
	java.util.Stack<Block> _stack = new java.util.Stack<Block>();
	void _append( RoutineElement routineElement ) {
		_stack.peek().appendElement(routineElement);
	}
}

/* entry point */
routine
	returns [ Routine result ]
	@init { $result = new Routine(); _stack.push( new MultilineBlock() ); }
	@after { $result.appendElements( (MultilineBlock)_stack.pop() ); }
	: t=tag { _converter.routineName( $t.result.name() ); } routineElement* EOF
	;

commandSequence
	returns [ InlineBlock result ]
	@init { _stack.push( new InlineBlock() ); }
	@after { $result =  (InlineBlock)_stack.pop() ; }
	: command ( ( Space | OtherWhitespace )+ command )*
	| /* nothing */
	;

inlineBlock
	returns [ InlineBlock result ]
	@init { _stack.push( new InlineBlock() ); }
	@after { $result =  (InlineBlock)_stack.pop() ; }
	: ( ( Space | OtherWhitespace )+ command )*
	;

routineElement
	: tag
	| ( Space | OtherWhitespace )+ blockIndent command
	| ( Space | OtherWhitespace )* comment
	| EndOfLine
	;

blockIndent
	: (Dot ( Space | OtherWhitespace ))*
	;

tag
	returns [ Tag result ] @after { requireNonNull($result); }
	: n=Name ( OpenParenthesis fal=formalArgumentList? CloseParenthesis )?
	  { _append( $result = new Tag( $n.text, _converter.asList($fal.ctx) ) ); }
	;

formalArgumentList
	: formalArgument ( Comma formalArgument )*
	;

formalArgument
	returns [ ParameterName result ] @after { requireNonNull($result); }
	: n=Name { $result = new ParameterName( $n.text ); }
	;

comment
	: Semicolon c=CommentText? (CommentEnd | ) { _append( new Comment( $c == null ? "" : $c.text) ); }
	;

command
	returns [ Command result ]
	locals [ CommandType _commandType, Expression _condition, Argument _argument, Block _block ]
	@after { requireNonNull($result); }
	: cmd=Name { $_commandType = CommandType.valueOfSymbol($cmd.text); }
            ( Colon c=expression { $_condition = $c.result; } )?
            ( EndOfLine { $_argument = Argument.NOTHING; }
            | Space a=argument[ $_commandType ] { $_argument = $a.result; }
            )
            b=block[ $_commandType, $_argument ] { $_block = $b.result; }
        {
        	_append( $result = new Command( $_condition, $_commandType, $_argument, $_block ) ) ;	
        }
	;

argument [ CommandType _commandType ]
	returns [ Argument result ] @after { requireNonNull($result); }
	: { $_commandType == CommandType.SET   }?  al=assignmentList    { $result = _converter.argumentFrom( $al.ctx); }
	| { $_commandType == CommandType.NEW   }?  dl=declarationList   { $result = _converter.argumentFrom( $dl.ctx); }
	| { $_commandType == CommandType.KILL  }?  vl=variableList      { $result = _converter.argumentFrom( $vl.ctx); }
	| { $_commandType == CommandType.FOR   }?  ld=loopDefinition    { $result = _converter.argumentFrom( $ld.ctx); }
	| { $_commandType == CommandType.DO    }? trc=taggedRoutineCall { $result = _converter.argumentFrom($trc.ctx); }
	| { $_commandType == CommandType.GOTO  }? trc=taggedRoutineCall { $result = _converter.argumentFrom($trc.ctx); }
	| { $_commandType == CommandType.IF    }?  el=expressionList    { $result = _converter.argumentFrom( $el.ctx); }
	| { $_commandType == CommandType.WRITE }? iol=inputOutputList   { $result = _converter.argumentFrom($iol.ctx); }
	| { $_commandType == CommandType.READ  }? iol=inputOutputList   { $result = _converter.argumentFrom($iol.ctx); }
	|                                           e=expression        { $result = _converter.argumentFrom(  $e.ctx); }
	| /* nothing */                                                 { $result = Argument.NOTHING; }
	;

declarationList
	returns [ List<DirectVariableReference> result ] @after { requireNonNull($result); }
	: declaration ( Comma declaration )* { $result = _converter.asList($ctx); }
	;

declaration
	returns [ DirectVariableReference result ] @after { requireNonNull($result); } 
	: n=Name { $result = (DirectVariableReference)_converter.createDirectVariableReference( LOCAL, $n, null ); }
	;

variableList
	returns [ List<VariableReference> result ] @after { requireNonNull($result); }
	: variable ( Comma variable )* { $result = _converter.asList($ctx); }
	;

variable
	returns [ VariableReference result ] @after { requireNonNull($result); }
	:              n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference(  LOCAL, $n, $el.ctx ); }
	|        Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( GLOBAL, $n, $el.ctx ); }
	| AtSign e=expression ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createIndirectVariableReference( $e.result, $el.ctx ); }
	;

inputOutputList
	returns [ List<InputOutput> result ] @after { requireNonNull($result); }
	: inputOutput ( Comma inputOutput )* { $result = _converter.asList($ctx); }
	;

inputOutput
	returns [ InputOutput result ] @after { requireNonNull($result); }
	:              n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = InputOutput.wrap( _converter.createDirectVariableReference(  LOCAL, $n, $el.ctx ) ); }
	|        Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = InputOutput.wrap( _converter.createDirectVariableReference( GLOBAL, $n, $el.ctx ) ); }
	| AtSign e=expression ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = InputOutput.wrap( _converter.createIndirectVariableReference( $e.result, $el.ctx ) ); }
	| e=expression { $result = InputOutput.wrap( $e.result ); }
	| Exclamation           { $result = FormatCommand.carriageReturn() ; }
	| PoundSign             { $result = FormatCommand.pageFeed() ; }
	| QuestionMark c=Digit+ { $result = FormatCommand.column( Integer.parseInt($c.text) ) ; }
	| ForwardSlash Name ( OpenParenthesis parameterList CloseParenthesis )?
	;

expressionList
	returns [ List<Expression> result ] @after { requireNonNull($result); }
	: expression ( Comma expression )* { $result = _converter.asList($ctx); }
	;

parameterList
	returns [ List<Expression> result ] @after { requireNonNull($result); }
	: expression ( Comma expression? )* { $result = _converter.asList($ctx); }
	;

loopDefinition
	returns [ LoopDefinition result ] @after { requireNonNull($result); }
	: n=Name Equals i=expression ( Colon b=expression ( Colon l=expression )? )? { $result = _converter.createLoopDefinition( $n, $i.result, $b.ctx, $l.ctx ); }
	;

taggedRoutineCall
	returns [ TaggedRoutineCall result ] @after { requireNonNull($result); }
	: n=Name Caret r=Name ( OpenParenthesis pl=parameterList? CloseParenthesis )? { $result = new TaggedRoutineCall( $n.text, $r.text, RoutineAccess.EXPLICIT, _converter.asList($pl.ctx) ); }
	|        Caret r=Name ( OpenParenthesis pl=parameterList? CloseParenthesis )? { $result = new TaggedRoutineCall( null,    $r.text, RoutineAccess.EXPLICIT, _converter.asList($pl.ctx) ); }
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
	returns [ Destination result ] @after { requireNonNull($result); }
	:              n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = Destination.wrap( _converter.createDirectVariableReference(  LOCAL, $n, $el.ctx ) ); }
	|        Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = Destination.wrap( _converter.createDirectVariableReference( GLOBAL, $n, $el.ctx ) ); }
	| AtSign e=expression ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = Destination.wrap( _converter.createIndirectVariableReference( $e.result, $el.ctx ) ); }
	|       Dollar n=Name OpenParenthesis pl=parameterList? CloseParenthesis { $result = Destination.wrap( _converter.createBuiltinFunctionCall( $n, $pl.ctx) ); }
	|       Dollar n=Name                                                    { $result = Destination.wrap( _converter.createBuiltinVariableReference( LOCAL,  $n ) ); }
	| Caret Dollar n=Name                                                    { $result = Destination.wrap( _converter.createBuiltinVariableReference( GLOBAL, $n ) ); }
	;

block [ CommandType _commandType, Argument _argument ]
	returns [ Block result ]
	: { $_commandType == CommandType.FOR  }? b=inlineBlock { $result = $b.result ; }
	| { $_commandType == CommandType.IF   }? b=inlineBlock { $result = $b.result ; }
	| { $_commandType == CommandType.ELSE }? b=inlineBlock { $result = $b.result ; }
	| /* nothing */
	;

expression
	returns [ Expression result ] @after { requireNonNull($result); }
	/* grouping */
	: OpenParenthesis e=expression CloseParenthesis { $result = $e.result; }
	/* literals */
	| ( Plus | Minus )? Digit+ ( Dot Digit+ )? { $result = Constant.from($text); }
	| Quote s=quotedSequence EndQuote          { $result = Constant.from($s.text); }
	/* variable references */
	|     Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( BY_VALUE,     GLOBAL, $n, $el.ctx ); }
	|           n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( BY_VALUE,     LOCAL, $n, $el.ctx ); }
	| Dot Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( BY_REFERENCE, GLOBAL, $n, $el.ctx ); }
	| Dot       n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( BY_REFERENCE, LOCAL, $n, $el.ctx ); }
	| AtSign e=expression ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createIndirectVariableReference( $e.result, $el.ctx ); }
	/* function calls */
	| Dollar Dollar n=Name ( Caret r=Name )? OpenParenthesis pl=parameterList? CloseParenthesis { $result = _converter.createRoutineFunctionCall( LOCAL, DIRECT, $n, $r, $pl.ctx); }
	|        Dollar n=Name                   OpenParenthesis pl=parameterList? CloseParenthesis { $result = _converter.createBuiltinFunctionCall( $n, $pl.ctx); }
	|        Dollar n=Name                                                                      { $result = _converter.createBuiltinVariableReference( LOCAL,  $n ); }
	|  Caret Dollar n=Name                                                                      { $result = _converter.createBuiltinVariableReference( GLOBAL, $n ); }
	/* unary operations */
	| Minus      e=expression { $result = new UnaryOperation( OperatorType.SUBTRACT, $e.result ); }
	| Plus       e=expression { $result = new UnaryOperation( OperatorType.ADD,      $e.result ); }
	| Apostrophe e=expression { $result = new UnaryOperation( OperatorType.NOT,      $e.result ); }
	/* binary operations */
	| lhs=expression        Underscore      rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.CONCAT          , $rhs.result); }
	| lhs=expression           Plus         rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.ADD             , $rhs.result); }
	| lhs=expression          Minus         rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.SUBTRACT        , $rhs.result); }
	| lhs=expression    Asterisk Asterisk   rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.POWER           , $rhs.result); }
	| lhs=expression         Asterisk       rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.MULTIPLY        , $rhs.result); }
	| lhs=expression        PoundSign       rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.MODULO          , $rhs.result); }
	| lhs=expression         Backslash      rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.DIVIDE_INT      , $rhs.result); }
	| lhs=expression       ForwardSlash     rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.DIVIDE          , $rhs.result); }
	| lhs=expression          Equals        rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.EQUALS          , $rhs.result); }
	| lhs=expression       GreaterThan      rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.GREATER_THAN    , $rhs.result); }
	| lhs=expression         LessThan       rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.LESS_THAN       , $rhs.result); }
	| lhs=expression    Apostrophe Equals   rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.NOT_EQUALS      , $rhs.result); }
	| lhs=expression Apostrophe GreaterThan rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.NOT_GREATER_THAN, $rhs.result); }
	| lhs=expression   Apostrophe LessThan  rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.NOT_LESS_THAN   , $rhs.result); }
	| lhs=expression        Ampersand       rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.AND             , $rhs.result); }
	| lhs=expression       Exclamation      rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.OR              , $rhs.result); }
	| lhs=expression   CloseSquareBracket   rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.FOLLOWS         , $rhs.result); }
	/* $SELECT(...) only */
	| lhs=expression Colon rhs=expression { $result = new ConditionalExpression( $lhs.result, $rhs.result ); }
	;

quotedSequence
	: ( EscapedCharacter | UnescapedCharacter )*
	;


	