Services;sample service
	QUIT 
MyService;
	NEW patients,patId,allergen
	;
	; Get request
	SET allergen=$$zECFGet^EALIBECF1("Allergen","")
	;
	NEW ctr
	FOR ctr=1 SET patId=$$znxIxID^EA3LIB5("ZPT",400,allergen,patId) QUIT:patId=""  DO 
	. SET patients(ctr)=patId
	SET patients(0)=ctr
	;
	; Send response
	; ==== Set Array Property Patients ====
	NEW patientsNodeId
	SET patientsNodeId=$$zECFNew^EALIBECF1("Patients","","A")
	NEW ecfLine
	FOR ecfLine=1:1:patients(0) SET %=$$zECFSetElmt^EALIBECF1(patientsNodeId,patients(ecfLine))
	QUIT ;method returns void

