Expressions;expressions
	;Generated-By  : edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineGenerator
	;Generated-On  : Thu Oct 27 15:39:41 CDT 2016
	;Generated-From: edu.vanderbilt.clinicalsystems.epic.annotation.Expressions
	QUIT 
main(x,y);main entry point, expecting "Q"
	;Generated-From: void main(java.lang.String,java.lang.Integer)
	QUIT ;method returns void
	;;#eof#
helper(s);helper function, expecting "Q s_s"
	;Generated-From: java.lang.String helper(java.lang.String)
	QUIT s_s;;#eof#
helper2(a,b,c);helper2 function, expecting "Q b*c+a"
	;Generated-From: double helper2(double,double,double)
	QUIT b*c+a;;#eof#
helper3(a,b,c);helper3 function, expecting "N z S z=$$helper2(1.0,2.0,3.0) Q b*z+a"
	;Generated-From: java.lang.Float helper3(java.lang.Float,java.lang.Float,java.lang.Float)
	NEW z
	SET z=$$helper2^Expressions("1.0","2.0","3.0")
	NEW k
	SET k=99
	NEW t
	SET t=$$currentTimeMillis^System()
	QUIT b*z+a;;#eof#
	;;#eor#

