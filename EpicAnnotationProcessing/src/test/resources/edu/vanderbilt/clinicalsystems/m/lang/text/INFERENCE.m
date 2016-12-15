INFERENCE ; see edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaWriterTest.canInferTypes
	QUIT

inferMethodReturnsVoid ; expect void
	QUIT

inferMethodReturnsInteger ; expect int
	QUIT 1

inferMethodReturnsDouble ; expect double
	QUIT 3.14159
	
inferMethodReturnsString ; expect String
	QUIT "foobar"
	
inferMethodReturnsValue ; expect Value
	NEW x
	SET x("foo")="bar" 
	QUIT x

inferLocalVariableType ; expect boolean, String, Integer, double
	NEW localBoolean,localString,localInteger,localDouble
	SET localBoolean=3!7
	SET localString=3_7
	SET localInteger=3/7
	SET localDouble=3\7
	QUIT
	
inferGlobal ; expect expect boolean, String, Integer, double
	SET globalBoolean=3!7
	SET globalString=3_7
	SET globalInteger=3/7
	SET globalDouble=3\7
	QUIT

inferSecondOrderReturnTypeFromVariable ; expect String
	QUIT globalString

inferSecondOrderReturnTypeFromMethod ; expect String
	QUIT $$inferMethodReturnsString()

