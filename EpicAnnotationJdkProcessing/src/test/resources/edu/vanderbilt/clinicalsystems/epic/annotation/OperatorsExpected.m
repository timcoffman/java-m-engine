Operators;Operators Sample
	;Generated-By  : edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineGenerator
	;Generated-On  : Thu Oct 27 15:39:41 CDT 2016
	;Generated-From: edu.vanderbilt.clinicalsystems.epic.annotation.Operators
	QUIT 
concatenation(x);Expect Q x_"abc"
	;Generated-From: String concatentation(String)
	QUIT x_"abc";;#eof#
assignmentConcatenation(x);Expect S x=x_"abc" Q x
	;Generated-From: String assignmentConcatenation(String)
	SET x=x_"abc"
	QUIT x;;#eof#
prefixPlus(x);Expect Q +x
	;Generated-From: int prefixPlus(int)
	QUIT +x;;#eof#
prefixMinus(x);Expect Q -x
	;Generated-From: int prefixMinus(int)
	QUIT -x;;#eof#
postfixIncrement(x);Expect Q x  S x=x+1
	;Generated-From: int postfixIncrement(int)
	QUIT x
	SET x=x+1;;#eof#
prefixIncrement(x);Expect S x=x+1 Q x
	;Generated-From: int prefixIncrement(int)
	SET x=x+1
	QUIT x;;#eof#
postfixDecrement(x);Expect Q x  S x=x-1
	;Generated-From: int postfixDecrement(int)
	QUIT x
	SET x=x-1;;#eof#
prefixDecrement(x);Expect S x=x-1 Q x
	;Generated-From: int prefixDecrement(int)
	SET x=x-1
	QUIT x;;#eof#
assignmentPlus(x);Expect S x=x+7 Q x
	;Generated-From: int assignmentPlus(int)
	SET x=x+7
	QUIT x;;#eof#
assignmentMinus(x);Expect S x=x-7 Q x
	;Generated-From: int assignmentPlus(int)
	SET x=x-7
	QUIT x;;#eof#
infixFollows(x);Expect Q x]"abc"
	;Generated-From: boolean infixFollows(String)
	QUIT x]"abc";;#eof#
infixNotFollows(x);Expect Q x']"abc"
	;Generated-From: boolean infixNotFollows(String)
	QUIT x']"abc";;#eof#
	;;#eor#

