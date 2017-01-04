Services;sample service
	QUIT 
MyService;
	NEW allergen,patients,patientsArrObj,patID,ecfLine
	;
	; Get request
	SET allergen=$$zECFGet^EALIB("Allergen","")
	;
	NEW ctr
	FOR ctr=1 SET patID=$$znxIxID^EALIB("ZPT",400,allergen,patID) QUIT:patID=""  DO 
	. SET patients(ctr)=patID
	SET patients(0)=ctr
	;
	; Send response
	; ==== Set Array Property Patients ====
	SET patientsArrObj=$$zECFNew^EALIB("Patients","","A")
	FOR ecfLine=1:1:patients(0) SET %=$$zECFSetElmt^EALIB(patientsArrObj,patients(ecfLine))
	QUIT ;method returns void

