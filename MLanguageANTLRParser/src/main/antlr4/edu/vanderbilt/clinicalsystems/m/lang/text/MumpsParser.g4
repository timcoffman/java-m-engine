parser grammar MumpsParser;

options { tokenVocab=MumpsLexer; }

@header {
import static java.util.Objects.requireNonNull;
import static edu.vanderbilt.clinicalsystems.m.lang.Scope.TRANSIENT;
import static edu.vanderbilt.clinicalsystems.m.lang.Scope.PERSISTENT;
import static edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle.DIRECT;
import static edu.vanderbilt.clinicalsystems.m.lang.ReferenceStyle.INDIRECT;
import static edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod.BY_VALUE;
import static edu.vanderbilt.clinicalsystems.m.lang.ParameterPassMethod.BY_REFERENCE;
import static edu.vanderbilt.clinicalsystems.m.lang.BuiltinFunction.SELECT;
import edu.vanderbilt.clinicalsystems.m.lang.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.* ;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.* ;
}

@members {
	Converter _converter = new Converter();
	java.util.Stack<Block> _stack = new java.util.Stack<Block>();
	MultilineBlock _expectedBlock = null ;
	void _append( RoutineElement routineElement ) {
		_stack.peek().appendElement(routineElement);
	}
	MultilineBlock expectBlock() {
		if ( null == _expectedBlock )
			_expectedBlock = new MultilineBlock() ;
		return _expectedBlock ;
	}
	int _precedingBlockLevel = 0 ;
	int _blockLevel = 0 ;
	int incrementBlockLevel() { return ++_blockLevel ; }
	int clearBlockLevel() { _precedingBlockLevel = _blockLevel ; return _blockLevel=0 ; }
	void setupBlock() {
		for ( int i = _precedingBlockLevel ; i < _blockLevel; ++i )
			openBlock() ;
		for ( int i = _precedingBlockLevel ; i > _blockLevel; --i )
			closeBlock() ;
		_expectedBlock = null ;
	}
	void openBlock() {
		if ( null != _expectedBlock )
			_stack.push( _expectedBlock );
		else
			_stack.push( new MultilineBlock() );
		_expectedBlock = null ;
	}
	void closeBlock() {
		_stack.pop();
	}
}

/* entry point */
routine
	returns [ Routine result ]
	@init { $result = new Routine(); _stack.push( new MultilineBlock() ); }
	@after { $result.appendElements( (MultilineBlock)_stack.pop() ); }
	: t=tag { _converter.routineName( $t.result.name() ); } ( ( Space | OtherWhitespace )+ command )* ( ( Space | OtherWhitespace )+ finalCommand )?
	  ( endOfLine tag? routineCommands? )*
	  EOF { setupBlock(); }
	;

commandSequence
	returns [ InlineBlock result ]
	@init { _stack.push( new InlineBlock() ); }
	@after { $result =  (InlineBlock)_stack.pop() ; }
	: command ( ( Space | OtherWhitespace )+ command )* ( ( Space | OtherWhitespace )+ finalCommand )?
	| /* nothing */
	;

completeExpression
	returns [ Expression result ] @after { requireNonNull($result); }
	: e=expression EOF { $result = $e.result ; }
	;

inlineBlock
	returns [ InlineBlock result ]
	@init { _stack.push( new InlineBlock() ); }
	@after { $result =  (InlineBlock)_stack.pop() ; }
	: ( ( Space | OtherWhitespace )+ command )* ( ( Space | OtherWhitespace )+ finalCommand )?
	;

endOfLine
	: ( Space | OtherWhitespace )* EndOfLine { clearBlockLevel(); }
	| ( Space | OtherWhitespace )* comment { clearBlockLevel(); }
	;

routineCommands
	: ( Space | OtherWhitespace )+ blockIndent? { setupBlock(); } (
		command ( ( Space | OtherWhitespace )+ command )* ( ( Space | OtherWhitespace )+ finalCommand )?
		| finalCommand
		| /* maybe no commands, just end of line and/or comments */
		)
	;

blockIndent
	: (Dot ( Space | OtherWhitespace ) { incrementBlockLevel(); } )+
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
      Space a=argument[ $_commandType ] { $_argument = $a.result; }
      b=block[ $_commandType, $_argument ] { $_block = $b.result; }
    {
    	_append( $result = new Command( $_condition, $_commandType, $_argument, $_block ) ) ;	
    }
	;

finalCommand
	returns [ Command result ]
	locals [ CommandType _commandType, Expression _condition, Block _block ]
	@after { requireNonNull($result); }
	: cmd=Name { $_commandType = CommandType.valueOfSymbol($cmd.text); }
      ( Colon c=expression { $_condition = $c.result; } )?
      b=finalBlock[ $_commandType, Argument.NOTHING ] { $_block = $b.result; }
    {
    	_append( $result = new Command( $_condition, $_commandType, Argument.NOTHING, $_block ) ) ;	
    }
	;


argument [ CommandType _commandType ]
	returns [ Argument result ] @after { requireNonNull($result); }
	: { $_commandType == CommandType.SET   }?  al=assignmentList        { $result = _converter.argumentFrom( $al.ctx); }
	| { $_commandType == CommandType.MERGE }?  al=assignmentList        { $result = _converter.argumentFrom( $al.ctx); }
	| { $_commandType == CommandType.NEW   }?  dl=declarationList       { $result = _converter.argumentFrom( $dl.ctx); }
	| { $_commandType == CommandType.KILL  }?  vl=variableList          { $result = _converter.argumentFrom( $vl.ctx); }
	| { $_commandType == CommandType.FOR   }?  ld=loopDefinition        { $result = _converter.argumentFrom( $ld.ctx); }
	| { $_commandType == CommandType.DO    }? trc=taggedRoutineCallList { $result = _converter.argumentFrom($trc.ctx); }
	| { $_commandType == CommandType.GOTO  }? trc=taggedRoutineCallList { $result = _converter.argumentFrom($trc.ctx); }
	| { $_commandType == CommandType.IF    }?  el=expressionList        { $result = _converter.argumentFrom( $el.ctx); }
	| { $_commandType == CommandType.WRITE }? iol=inputOutputList       { $result = _converter.argumentFrom($iol.ctx); }
	| { $_commandType == CommandType.READ  }? iol=inputOutputList       { $result = _converter.argumentFrom($iol.ctx); }
	|                                           e=expression            { $result = _converter.argumentFrom(  $e.ctx); }
	| /* nothing */                                                     { $result = Argument.NOTHING; }
	;

declarationList
	returns [ List<DirectVariableReference> result ] @after { requireNonNull($result); }
	: declaration ( Comma declaration )* { $result = _converter.asList($ctx); }
	;

declaration
	returns [ DirectVariableReference result ] @after { requireNonNull($result); } 
	: n=Name { $result = (DirectVariableReference)_converter.createDirectVariableReference( TRANSIENT, $n, null ); }
	;

variableList
	returns [ List<VariableReference> result ] @after { requireNonNull($result); }
	: variable ( Comma variable )* { $result = _converter.asList($ctx); }
	;

variable
	returns [ VariableReference result ] @after { requireNonNull($result); }
	:              n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference(  TRANSIENT, $n, $el.ctx ); }
	|        Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( PERSISTENT, $n, $el.ctx ); }
	| AtSign e=expression ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createIndirectVariableReference( $e.result, $el.ctx ); }
	;

inputOutputList
	returns [ List<InputOutput> result ] @after { requireNonNull($result); }
	: inputOutput ( Comma inputOutput )* { $result = _converter.asList($ctx); }
	;

inputOutput
	returns [ InputOutput result ] @after { requireNonNull($result); }
	:              n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = InputOutput.wrap( _converter.createDirectVariableReference(  TRANSIENT, $n, $el.ctx ) ); }
	|        Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = InputOutput.wrap( _converter.createDirectVariableReference( PERSISTENT, $n, $el.ctx ) ); }
	| AtSign e=expression ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = InputOutput.wrap( _converter.createIndirectVariableReference( $e.result, $el.ctx ) ); }
	| e=expression { $result = InputOutput.wrap( $e.result ); }
	| Exclamation           { $result = FormatCommand.carriageReturn() ; }
	| PoundSign             { $result = FormatCommand.pageFeed() ; }
	| QuestionMark c=Digit+ { $result = FormatCommand.column( Integer.parseInt($c.text) ) ; }
	| ForwardSlash n=Name ( OpenParenthesis parameterList[$n] CloseParenthesis )?
	;

expressionList
	returns [ List<Expression> result ] @after { requireNonNull($result); }
	: expression ( Comma expression )* { $result = _converter.asList($ctx); }
	;

parameterList [ Token n ]
	returns [ List<Expression> result ]
	locals [ boolean _isConditional ]
	@init { $_isConditional = SELECT.matchesSymbol($n.getText()) ; }
	@after { requireNonNull($result); }
	: parameter[$_isConditional] ( Comma parameter[$_isConditional]? )* { $result = _converter.asList($ctx); }
	;

parameter [ boolean _isConditional ]
	returns [ Expression result ] @after { requireNonNull($result); }
	: { $_isConditional }? c=expression Colon e=expression { $result = new ConditionalExpression( $c.result, $e.result ); }
	|                                         e=expression { $result = $e.result ; }
	;

loopDefinition
	returns [ LoopDefinition result ] @after { requireNonNull($result); }
	: n=Name Equals i=expression ( Colon b=expression ( Colon l=expression )? )? { $result = _converter.createLoopDefinition( $n, $i.result, $b.ctx, $l.ctx ); }
	;

taggedRoutineCallList
	returns [ List<TaggedRoutineCall> result ] @after { requireNonNull($result); }
	: taggedRoutineCall ( Comma taggedRoutineCall )* { $result = _converter.asList($ctx); }
	;

taggedRoutineCall
	returns [ TaggedRoutineCall result ] @after { requireNonNull($result); }
	: n=Name              ( OpenParenthesis pl=parameterList[ $n ]? CloseParenthesis )? { $result = new TaggedRoutineCall( _converter.createTagReference( DIRECT, $n,   null ), _converter.asList($pl.ctx) ); }
	| n=Name Caret r=Name ( OpenParenthesis pl=parameterList[ $n ]? CloseParenthesis )? { $result = new TaggedRoutineCall( _converter.createTagReference( DIRECT, $n,   $r   ), _converter.asList($pl.ctx) ); }
	|        Caret r=Name ( OpenParenthesis pl=parameterList[null]? CloseParenthesis )? { $result = new TaggedRoutineCall( _converter.createTagReference( DIRECT, null, $r   ), _converter.asList($pl.ctx) ); }
	;

assignmentList
	returns [ List<Assignment> result ] @after { requireNonNull($result); }
	: assignment ( Comma assignment )* { $result = _converter.asList($ctx); }
	;
	
assignment
	returns [ Assignment result ] @after { requireNonNull($result); }
	: d=destination Equals s=expression { $result = new Assignment( $d.result, $s.result ); }
	| OpenParenthesis dl=destinationList CloseParenthesis Equals s=expression { $result = new Assignment( $dl.result, $s.result ); }
	;

destinationList
	returns [ List<Destination<?>> result ] @after { requireNonNull($result); }
	: destination ( Comma destination )* { $result = _converter.asList($ctx); }
	;
	
destination
	returns [ Destination result ] @after { requireNonNull($result); }
	:              n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = Destination.wrap( _converter.createDirectVariableReference(  TRANSIENT, $n, $el.ctx ) ); }
	|        Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = Destination.wrap( _converter.createDirectVariableReference( PERSISTENT, $n, $el.ctx ) ); }
	| AtSign e=expression ( AtSign OpenParenthesis el=expressionList CloseParenthesis )? { $result = Destination.wrap( _converter.createIndirectVariableReference( $e.result, $el.ctx ) ); }
	|       Dollar n=Name OpenParenthesis pl=parameterList[$n]? CloseParenthesis { $result = Destination.wrap( _converter.createBuiltinFunctionCall( $n, $pl.ctx) ); }
	|       Dollar n=Name                                                             { $result = Destination.wrap( _converter.createBuiltinVariableReference( TRANSIENT,  $n ) ); }
	| Caret Dollar n=Name                                                             { $result = Destination.wrap( _converter.createBuiltinVariableReference( PERSISTENT, $n ) ); }
	;

block [ CommandType _commandType, Argument _argument ]
	returns [ Block result ]
	: { $_commandType == CommandType.FOR  }? b=inlineBlock { $result = $b.result ; }
	| { $_commandType == CommandType.IF   }? b=inlineBlock { $result = $b.result ; }
	| { $_commandType == CommandType.ELSE }? b=inlineBlock { $result = $b.result ; }
	| { $_commandType == CommandType.DO   && $_argument == Argument.NOTHING }? { $result = expectBlock() ; }
	| /* nothing */ { $result = null ; }
	;

finalBlock [ CommandType _commandType, Argument _argument ]
	returns [ Block result ]
	: { $_commandType == CommandType.DO && $_argument == Argument.NOTHING }? { $result = expectBlock() ; }
	| /* nothing */ { $result = null ; }
	;

group
	returns [ Expression result ] @after { requireNonNull($result); }
	: OpenParenthesis e=expression CloseParenthesis { $result = $e.result; }
	;

literal
	returns [ Expression result ] @after { requireNonNull($result); }
	: ( Plus | Minus )? Digit+ ( Dot Digit* )? { $result = Constant.from(  $text); }
	| ( Plus | Minus )?          Dot Digit+    { $result = Constant.from(  $text); }
	| Quote s=quotedSequence EndQuote          { $result = Constant.from($s.text); }
	;

directVariableRef
	returns [ Expression result ] @after { requireNonNull($result); }
	:     Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( BY_VALUE,     PERSISTENT, $n, $el.ctx ); }
	|           n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( BY_VALUE,     TRANSIENT, $n, $el.ctx ); }
	| Dot Caret n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( BY_REFERENCE, PERSISTENT, $n, $el.ctx ); }
	| Dot       n=Name (        OpenParenthesis el=expressionList CloseParenthesis )? { $result = _converter.createDirectVariableReference( BY_REFERENCE, TRANSIENT, $n, $el.ctx ); }
	;

variableReference
	returns [ Expression result ] @after { requireNonNull($result); }
	: v=directVariableRef { $result = $v.result; }
	| AtSign e=expression AtSign OpenParenthesis el=expressionList CloseParenthesis { $result = _converter.createIndirectVariableReference( $e.result, $el.ctx ); }
	;

functionCall
	returns [ Expression result ] @after { requireNonNull($result); }
	: Dollar Dollar n=Name ( Caret r=Name )? OpenParenthesis pl=parameterList[$n]? CloseParenthesis { $result = _converter.createRoutineFunctionCall( TRANSIENT, DIRECT, $n, $r, $pl.ctx); }
	|        Dollar n=Name                   OpenParenthesis pl=parameterList[$n]? CloseParenthesis { $result = _converter.createBuiltinFunctionCall( $n,  $pl.ctx); }
	|        Dollar n=Name                                                                               { $result = _converter.createBuiltinVariableReference( TRANSIENT,  $n ); }
	|  Caret Dollar n=Name                                                                               { $result = _converter.createBuiltinVariableReference( PERSISTENT, $n ); }
	;

expression
	returns [ Expression result ] @after { requireNonNull($result); }
	/* grouping */
	: grp=group             { $result = $grp.result; }
	| lit=literal           { $result = $lit.result; }
	| var=variableReference { $result = $var.result; }
	| fun=functionCall      { $result = $fun.result; }
	/* unary operations */
	| Minus      e=expression { $result = new UnaryOperation( OperatorType.SUBTRACT,    $e.result ); }
	| Plus       e=expression { $result = new UnaryOperation( OperatorType.ADD,         $e.result ); }
	| Apostrophe e=expression { $result = new UnaryOperation( OperatorType.NOT,         $e.result ); }
	| AtSign     e=expression { $result = new UnaryOperation( OperatorType.INDIRECTION, $e.result ); }
	/* binary operations */
	| lhs=expression           Underscore          rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.CONCAT          , $rhs.result); }
	| lhs=expression              Plus             rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.ADD             , $rhs.result); }
	| lhs=expression             Minus             rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.SUBTRACT        , $rhs.result); }
	| lhs=expression       Asterisk Asterisk       rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.POWER           , $rhs.result); }
	| lhs=expression            Asterisk           rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.MULTIPLY        , $rhs.result); }
	| lhs=expression           PoundSign           rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.MODULO          , $rhs.result); }
	| lhs=expression            Backslash          rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.DIVIDE_INT      , $rhs.result); }
	| lhs=expression          ForwardSlash         rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.DIVIDE          , $rhs.result); }
	| lhs=expression             Equals            rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.EQUALS          , $rhs.result); }
	| lhs=expression          GreaterThan          rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.GREATER_THAN    , $rhs.result); }
	| lhs=expression            LessThan           rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.LESS_THAN       , $rhs.result); }
	| lhs=expression       Apostrophe Equals       rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.NOT_EQUALS      , $rhs.result); }
	| lhs=expression    Apostrophe GreaterThan     rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.NOT_GREATER_THAN, $rhs.result); }
	| lhs=expression      Apostrophe LessThan      rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.NOT_LESS_THAN   , $rhs.result); }
	| lhs=expression           Ampersand           rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.AND             , $rhs.result); }
	| lhs=expression          Exclamation          rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.OR              , $rhs.result); }
	| lhs=expression      CloseSquareBracket       rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.FOLLOWS         , $rhs.result); }
	| lhs=expression Apostrophe CloseSquareBracket rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.NOT_FOLLOWS     , $rhs.result); }
	| lhs=expression         QuestionMark          mp=matchPattern{ $result = new BinaryOperation( $lhs.result, OperatorType.MATCH           ,  $mp.result); }
	| lhs=expression         QuestionMark          rhs=expression { $result = new BinaryOperation( $lhs.result, OperatorType.MATCH           , $rhs.result); }
	;

matchPattern
	returns [ MatchPattern result ] @after { requireNonNull($result); }
	: mas=matchAtomSequence { $result = new MatchPattern( new MatchPattern.MatchSequence( _converter.asList($mas.ctx) ) ); }
	;

completeMatchPattern
	returns [ MatchPattern result ] @after { requireNonNull($result); }
	: mp=matchPattern EOF { $result = $mp.result ; } 
	;

matchAtomSequence
	: matchAtom+
	;
	
matchAtom
	returns [ MatchPattern.Atom result ] @after { requireNonNull($result); }
	: min=Digit+ Dot max=Digit+ mt=matchType { $result = new MatchPattern.Atom( Integer.parseInt($min.text), Integer.parseInt($max.text), $mt.result ); }
	| min=Digit+ Dot            mt=matchType { $result = new MatchPattern.Atom( Integer.parseInt($min.text), null,                        $mt.result ); }
	|            Dot max=Digit+ mt=matchType { $result = new MatchPattern.Atom( null,                        Integer.parseInt($max.text), $mt.result ); }
	|            Dot            mt=matchType { $result = new MatchPattern.Atom( null,                        null,                        $mt.result ); }
	| mnx=Digit+                mt=matchType { $result = new MatchPattern.Atom( Integer.parseInt($mnx.text), Integer.parseInt($mnx.text), $mt.result ); }
	;
	
matchType
	returns [ MatchPattern.MatchType result ] @after { requireNonNull($result); }
	: csmt=codeSetMatchType { $result = $csmt.result ; }
	|  lmt=literalMatchType { $result =  $lmt.result ; }
	|  umt=unionMatchType   { $result =  $umt.result ; }
	;

literalMatchType
	returns [ MatchPattern.LiteralMatchType result ] @after { requireNonNull($result); }
	: Quote s=quotedSequence EndQuote { $result = new MatchPattern.LiteralMatchType( $s.text ) ; }
	;

codeSetMatchType
	returns [ MatchPattern.CodeSetMatchType result ] @after { requireNonNull($result); }
	: cs=Name /* how to differentiate in the lexer between Name and Alpha+ ??? */ { $result = new MatchPattern.CodeSetMatchType( _converter.codeSetFrom($cs.text) ) ; }
	;

matchAtomList
	: matchAtom ( Comma matchAtom )*
	;
	
unionMatchType
	returns [ MatchPattern.UnionMatchType result ] @after { requireNonNull($result); }
	: OpenParenthesis mal=matchAtomList? CloseParenthesis { $result = new MatchPattern.UnionMatchType( _converter.asList($mal.ctx) ) ; }
	;

quotedSequence
	: ( EscapedCharacter | UnescapedCharacter )*
	;


	