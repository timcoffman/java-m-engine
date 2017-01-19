ChroniclesMethods	QUIT 
	;Generated-By  : edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineGenerator
	;Generated-On  : Thu Oct 27 15:39:41 CDT 2016
	;Generated-From: edu.vanderbilt.clinicalsystems.epic.annotation.ChroniclesMethods
chroniclesArrayFromIndex(allergen,patients);
	;Generated-From: void chroniclesArrayFromIndex(java.lang.String,edu.vanderbilt.clinicalsystems.m.core.Value)
	NEW patId,ctr
	FOR ctr=1 SET patId=$$znxIxID^EA3LIB5("ZPT",400,allergen,patId) QUIT:patId=""  DO 
	. SET patients(ctr)=patId
	SET patients(0)=ctr
	QUIT ;method returns void
	;;#eof#
