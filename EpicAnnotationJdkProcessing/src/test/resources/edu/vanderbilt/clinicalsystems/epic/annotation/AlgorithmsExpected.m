Algorithms	QUIT 
	;Generated-By  : edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineGenerator
	;Generated-On  : Thu Oct 27 15:39:41 CDT 2016
	;Generated-From: edu.vanderbilt.clinicalsystems.epic.annotation.Algorithms
basicSummationLoop(start,increment,stop);
	;Generated-From: void basicSummationLoop(java.lang.String,java.lang.String,java.lang.String)
	NEW s
	SET s=0
	NEW x
	SET x=start
	FOR  QUIT:x>stop  DO 
	. SET s=s+x
	. SET x=x+increment
	QUIT s;;#eof#
basicSort(source,delimiter);
	;Generated-From: void basicSort(java.lang.String,java.lang.String)
	NEW n
	SET n=$LENGTH(source,delimiter)
	QUIT:n=1 source
	NEW x,i
	FOR i=1:1:n SET x($PIECE(source,delimiter,i))=1
	NEW result,key
	SET key=$ORDER(x(""))
	FOR  QUIT:key=""  DO 
	. SET:result'="" result=result_delimiter
	. SET result=result_key
	. SET key=$ORDER(x(key))
	QUIT result;;#eof#

