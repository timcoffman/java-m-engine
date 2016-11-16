SampleOne;sample service
	;Generated-By  : edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineGenerator
	;Generated-On  : Thu Oct 27 15:39:41 CDT 2016
	;Generated-From: edu.vanderbilt.clinicalsystems.epic.annotation.SampleOne
	QUIT 
sample;
	;Generated-From: void sample()
	NEW k,x
	SET x=0
	FOR  QUIT:'(x<10)  DO 
	. SET k=0
	. SET x=x+1
	QUIT ;method returns void
	;;#eof#
	;;#eor#

