Services;sample service
	QUIT 
MyService;
	NEW allergen,patients,patientsArrObj,patID,ecfLine
	;
	; Get request
	SET allergen=$$zECFGet("Allergen","")
	;
	NEW ctr
	FOR ctr=1 SET patID=$$znxIxID("ZPT",400,allergen,patID) QUIT:patID=""  DO 
	. SET patients(ctr)=patID
	SET patients(0)=ctr
	;
	; Send response
	; ==== Set Array Property Patients ====
	SET patientsArrObj=$$zECFNew("Patients","","A")
	FOR ecfLine=1:1:patients(0) SET %=$$zECFSetElmt(patientsArrObj,patients(ecfLine))
	QUIT ;method returns void


