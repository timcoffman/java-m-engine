lexer grammar MumpsLexer;

/* default mode */

fragment ALPHA : [a-zA-Z] ;
fragment NUMERIC : [0-9] ;
fragment ALPHANUMERIC : (ALPHA | NUMERIC) ;

fragment WHITESPACE : [ \t] ;

fragment SEMICOLON : [;] ;

fragment CR : [\r] ;
fragment LF : [\n] ;
fragment EOL : CR? LF ;

Name
	: ( ALPHA | '%' ) ALPHANUMERIC*
	;

Digit
	: NUMERIC
	;

Dot
	: '.'
	;

Colon
	: ':'
	;

Caret
	: '^'
	;

Underscore
	: '_'
	;

Semicolon
	: ';' -> mode(COMMENT)
	;

OpenParenthesis
	: '('
	;

CloseParenthesis
	: ')'
	;

Comma
	: ','
	;
	
Dollar
	: '$'
	;

Backslash
	: '\\'
	;

EndOfLine
	: EOL
	;

Space
	: ' '
	;

OtherWhitespace
	: '\t'
	;

Equals
	: '='
	;

CloseSquareBracket
	: ']'
	;

OpenSquareBracket
	: '['
	;

GreaterThan
	: '>'
	;

LessThan
	: '<'
	;

Apostrophe
	: '\''
	;

Ampersand
	: '&'
	;

Exclamation
	: '!'
	;

Asterisk
	: '*'
	;

ForwardSlash
	: '/'
	;

QuestionMark
	: '?'
	;

PoundSign
	: '#'
	;

Quote
	: '"' -> pushMode(QUOTED)
	;

Plus
    : '+'
    ;

Minus
    : '-'
    ;

AtSign
    : '@'
    ;

mode QUOTED; /* ---------- QUOTED ---------- */

EscapedCharacter
	: '\\' .
	;

UnescapedCharacter
    : ~["\\]
    ;

EndQuote
    : '"' -> popMode
    ;

mode COMMENT; /* ---------- COMMENT ---------- */

CommentText
	: ~[\r\n]+
	;

CommentEnd
	: EOL -> mode(DEFAULT_MODE)
	;
