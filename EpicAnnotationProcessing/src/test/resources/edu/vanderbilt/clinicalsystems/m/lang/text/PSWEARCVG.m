PSWEARCVG ; ; ;2015-07-03 15:38:51;8.2;vUOl+HKd5WtS96FuoywLMAY5FfJtU2XCzAVciOS2HFvth5lijD1PVfr+z7OqV4gZ
	q
	; Copyright 2012-2015 Epic Systems Corporation
	;*********************************************************************
	; TITLE:   PSWEARCVG
	; PURPOSE: Web service server code for Epic.Registration.GetGuarantorsAndCoverages
	; AUTHOR:  Justin Smith
	; CALLABLE TAGS:
	;   GetGuarsAndCvgs - web service entry point
	; REVISION HISTORY:
	;   *jes 07/12 243876 - Created
	;   *jes 11/12 252001 - Fixes to 243876
	;   *dsf 02/13 259822 - Added Expiration,PayorName,PlanName,Subscriber,BenefitCode,Description, and Status
	;   *syn 05/14 314554 - Get future effective coverages as well
	;   *jvb 05/15 373469 - add tag for active and inactive EAR with effective/future CVG
	;*********************************************************************
	;*jvb++ 05/15 373469 new tag
	; NAME:         GetGuarsAndCvgs2 (PUBLIC)
	; DESCRIPTION:  Web service to load available guarantor accounts and coverages
	;               selectable for an appointment on today's date.
	;
	;               Active guarantors are always returned if there is an associated
	;               effective coverage for today or in the future.
	;
	;               Inactive guarantors (defined by the account or patient level active
	;               flag) are returned if the IncludeInactiveAccounts request parameter
	;               is true and if there there is an associated effective coverage
	;               for today or in the future.
	; CALLED BY:    Epic.Registration.GetGuarantorsAndCoverages2
	; PARAMETERS:   (ECF) see GetGuarsAndCvgs below for details
	; ASSUMES:      ECF Context
	; --------
GetGuarsAndCvgs2  ;
	d GetGuarsAndCvgs(1)
	q
	;
	; NAME:         GetGuarsAndCvgs (PUBLIC)
	; DESCRIPTION:  Web service to load available guarantor accounts and coverages
	;               selectable for an appointment on today's date.
	;
	;               Active guarantors are always returned if there is an associated
	;               effective coverage for today or in the future.
	; CALLED BY:    Epic.Registration.GetGuarantorsAndCoverages
	;               GetGuarsAndCvgs2
	; PARAMETERS:
	;   ShowInactive (I,OPT) - If true, will check the IncludeInactiveAccounts
	;                          request property and will set the Inactive
	;                          response property. Used by the GetGuarantorsAndCoverages2
	;                          web service.
	; PARAMETERS: (ECF)
	;  Request:
	;   <PatientID> (Required) – The ID of the patient being used.
	;   <PatientIDType> (Optional) - The ID type of PatientID.
	;   <UserID> (Required) – The ID of the user sending the request.
	;   <UserIDType> (Optional) - The ID type of UserID.
	;   <DepartmentID> (Required) – The ID of the department of the appointment.
	;   <DepartmentIDType> (Optional) - The ID type of DepartmentID.
	;   <IncludeInactiveAccounts> (Optional) - Set to True to include inactive accounts with active coverages
	;
	;  Response:
	;   <Error> - if could not retrieve the guarantor accounts and coverages,
	;             will return a message as to why.
	;   <Guarantors> - An array of guarantor data objects (Epic.Registration.Guarantor).
	;       <Guarantor>
	;           <ID> - An array of ID values (Epic.Common.IDType).
	;              <ID> - The ID of the guarantor.
	;              <Type> - The type of ID. This service supports "Internal" and "CID".
	;           </ID>
	;           <DisplayName> - Guarantor's name as it appears in Hyperspace (EAR-.2)
	;           <Type> - Guarantor account type (EAR-210).
	;           <RelationshipToPatient> - Title value of the guarantor's relationship to
	;                                     the current patient (EPT-2225 for the corresponding
	;                                     guarantor).
	;           <Comment> - Guarantor comment (EPT-2240 for the corresponding guarantor).
	;           <Expiration> - Expiration date of the guarantor account (EAR-217)
	;           <Inactive> - Only set if called from GetGuarsAndCvgs2. Returns True if the
	;                        account is inactive at the patient or account level. Returns
	;                        False if the accost is active at both levels.
	;           <Coverages> - An array of coverage data objects (Epic.Registration.Coverage2).
	;               <Coverage2>
	;                   <ID> - An array of ID values (Epic.Common.IDType).
	;                       <ID> - The ID of the coverage.
	;                       <Type> - The type of ID. This service supports "Internal" and "CID".
	;                   </ID>
	;                   <PayorID> - An array of ID values (Epic.Common.IDType).
	;                       <ID> - The ID of the payor.
	;                       <Type> - The type of ID. This service supports "Internal" and "CID".
	;                   </ID>
	;                   <PlanID> - An array of ID values (Epic.Common.IDType).
	;                       <ID> - The ID of the plan.
	;                       <Type> - The type of ID. This service supports "Internal" and "CID".
	;                   </ID>
	;                   <EffectiveFromDate> - DTE of the coverage's effective-from date for
	;                                         the patient.  First looks for a member line of the
	;                                         current patient with dates that include today's
	;                                         date, and if none found, fall back to the coverage-
	;                                         level effective-from date.
	;                   <EffectiveToDate> - DTE of the coverage's effective-to date for
	;                                         the patient.  First looks for a member line of the
	;                                         current patient with dates that include today's
	;                                         date, and if none found, fall back to the coverage-
	;                                         level effective-to date.
	;                   <GroupNumber> - The coverage's group number (CVG-210).
	;                   <GroupName> - The coverage's group name (CVG-215).
	;                   <MedicareCoverageType> - The coverage's Medicare type (CVG-2015).
	;                   <PayorName> - The coverage's payor name (EPM-.2)
	;                   <PlanName> - The coverage's plan name (EPP-.2)
	;                   <Subscriber> - The coverage's subscriber's name (CVG-230)
	;                   <BenefitCode> - The coverage's benefit code (CVG-212)
	;                   <Description> - The coverage's description (CVG-30); array of lines
	;                   <Status> - The coverage's status (CVG-470); category title
	;               </Coverage2>
	;               <Coverage2>
	;                   (Additional coverages...)
	;               </Coverage2>
	;           </Coverages>
	;       </Guarantor>
	;       <Guarantor>
	;           (Additional guarantors...)
	;       </Guarantor>
	;   </Guarantors>
	;
	;  Fault Codes:
	;    "NOT-LICENSED": "Prelude is not licensed." - Returned if Prelude is
	;               not a licensed application.
	;    "NO-PATIENT-ID": "Patient ID not provided." - Returned if the
	;               PatientID parameter is blank.
	;    "NO-PATIENT-FOUND": "Patient could not be determined from the ID
	;               and ID-type provided." – Returned if the patient record
	;               could not be determined from the given parameters.
	;    "NO-USER-ID": "User ID not provided." - Returned if the
	;               UserID parameter is blank.
	;    "NO-USER-FOUND": "User could not be determined from the ID
	;               and ID-type provided." – Returned if the user record
	;               could not be determined from the given parameters.
	;    "NO-DEPARTMENT-ID": "Department ID not provided." - Returned if the
	;               DepartmentID parameter is blank.
	;    "NO-DEPARTMENT-FOUND": "Department could not be determined from
	;               the ID and ID-type provided." – Returned if the department
	;               record could not be determined from the given parameters.
	;    "NO-REGISTRATION-SECURITY": "User does not have Registration
	;               security." - Returned if the user does not have a Registration
	;               Security Class.
	;
	; ASSUMES:      ECF Context
	; --------
GetGuarsAndCvgs(ShowInactive)  ;*jvb 05/15 373469 Add ShowInactive
	n id,inclInactEAR,type,ret,ln,ln2,newLn,pGlo
	n eptID,empID,depID,eclID,locID,saID,dte
	n preCvgList,cvgList,allowInactive,cvgID,cvgLn,earCount,earID,earLn,earNum
	n cvgElement,cvgElmObject,earElement,earElmObject,idElement,idElmObject
	n desLn,descriptionElmt,endDte
	;
	; Prelude license check
	i '$$isLicPrelude() s %=$$zECFThrow("NOT-LICENSED","Prelude is not licensed") q
	;
	; Get request
	; Patient ID
	s id=$$zECFGet("PatientID","")
	i id="" s %=$$zECFThrow("NO-PATIENT-ID","Patient ID not provided") q
	s type=$$zECFGet("PatientIDType","")
	s eptID=$$GetRecordId^EGENETCMN("EPT",id,type)   ;*jes 11/12 252001 - Blank type already defaults to internal, no need to handle that here
	i eptID="" s %=$$zECFThrow("NO-PATIENT-FOUND","Patient could not be determined from the ID and ID-type provided") q
	;
	; User ID
	s id=$$zECFGet("UserID","")
	i id="" s %=$$zECFThrow("NO-USER-ID","User ID not provided") q
	s type=$$zECFGet("UserIDType","")
	s empID=$$GetRecordId^EGENETCMN("EMP",id,type)   ;*jes 11/12 252001 - Blank type already defaults to internal, no need to handle that here
	i empID="" s %=$$zECFThrow("NO-USER-FOUND","User could not be determined from the ID and ID-type provided") q
	;
	; Department ID
	s id=$$zECFGet("DepartmentID","")
	i id="" s %=$$zECFThrow("NO-DEPARTMENT-ID","Department ID not provided") q
	s type=$$zECFGet("DepartmentIDType","")
	s depID=$$GetRecordId^EGENETCMN("DEP",id,type)   ;*jes 11/12 252001 - Blank type already defaults to internal, no need to handle that here
	i depID="" s %=$$zECFThrow("NO-DEPARTMENT-FOUND","Department could not be determined from the ID and ID-type provided") q
	;
	;Add parameter for DTE here ;*syn 05/14 314554 for future when we add dte parameter
	;
	s saID=$$DEP2SA(depID,.locID)  ;Get context from department
	s eclID=$$eprECL(depID,empID)  ;Get user's registration security class
	i dte="" s dte=+$H,endDte=99999    ;Use "today" as default for day of appointment   ;*syn 05/14 314554 if dte parameter not passed, get current & future effective CVG
	s pGlo=$$zGtTmpGlo()  ;Used to cache coverage data in getCvgData
	;
	;Load accounts and coverages for the DTE given
	s allowInactive=$$getOPR(64245,1,"","","",saID)  ;EAF-64245 - whether to allow accounts with inactive payors/plans
	;
	;*jvb+2 05/15 373469
	;If called from GetGuarsAndCvgs2, obtain IncludeInactiveAccounts request property
	i ShowInactive=1 s inclInactEAR=$$zECFGet("IncludeInactiveAccounts","")
	;
	;Get each guarantor account
	s earCount=$$zgetnp("EPT",eptID,2200,0,99999)
	f earLn=1:1:earCount d
	. s earID=$$zgetnp("EPT",eptID,2200,earLn,99999)
	. ;Guarantor record checks
	. q:'$$AllowedEAR(eptID,dte,earID,earLn,saID,eclID,allowInactive,.preCvgList,endDte,inclInactEAR)  ;*syn 05/14 314554 +endDte ;*jvb 05/15 373469 +inclInactEAR
	. k cvgList,newLn
	. ;
	. ;Account is good, so get data and add it to the list
	. s earNum=earNum+1  ;Increment which guarantor we are on for finalized list
	. d getEarData(eptID,earID,earNum,.ret)
	. ; Build new list which skips restricted coverages
	. f ln=1:1:preCvgList(0) d  ;Look at all coverages
	. . i '$$restCvg(preCvgList(ln),90,"",eclID,saID,locID,empID) s newLn=newLn+1,cvgList(newLn)=preCvgList(ln)  ;If not restricted, add to new list
	. s cvgList(0)=+newLn  ;Give new list a line count
	. ;
	. ;Get data for each coverage
	. f cvgLn=1:1:cvgList(0) d
	. . s cvgID=cvgList(cvgLn)
	. . d getCvgData(eptID,dte,earNum,cvgID,cvgLn,pGlo,.ret)
	. ;Set coverage count for this guarantor
	. s ret("EAR",earNum,"CVG",0)=+cvgList(0)
	;Set guarantor count
	s ret("EAR",0)=+earNum
	d %zRelTmpGlo(pGlo)  ;Clean up temp global from above
	;
	; Data is now stored in nodes of ret - send response to client
	s earElement=$$zECFNew("Guarantors","","A")
	f ln=1:1:ret("EAR",0) d   ;For each guarantor
	. s earElmObject=$$zECFNewElmtObj(earElement)
	. ; Basic guarantor information
	. s %=$$zECFSet("DisplayName",ret("EAR",ln,"DisplayName"),earElmObject)
	. s %=$$zECFSet("Type",ret("EAR",ln,"Type"),earElmObject)
	. s %=$$zECFSet("RelationshipToPatient",ret("EAR",ln,"RelationshipToPatient"),earElmObject)
	. s %=$$zECFSet("Comment",ret("EAR",ln,"Comment"),earElmObject)
	. s %=$$zECFSet("Expiration",ret("EAR",ln,"Expiration"),earElmObject)
	. i ShowInactive=1 s %=$$zECFSet("Inactive",ret("EAR",ln,"Inactive"),earElmObject) ;*jvb 05/15 373469 include inactive flag
	. ;
	. ; Guarantor ID
	. s idElement=$$zECFNew("ID",earElmObject,"A")
	. s idElmObject=$$zECFNewElmtObj(idElement)
	. s %=$$zECFSet("ID",ret("EAR",ln,"ID"),idElmObject),%=$$zECFSet("Type","Internal",idElmObject)
	. ; Add guarantor's community ID if present
	. i ret("EAR",ln,"CID")'="" d
	. . s idElmObject=$$zECFNewElmtObj(idElement)
	. . s %=$$zECFSet("ID",ret("EAR",ln,"CID"),idElmObject),%=$$zECFSet("Type","CID",idElmObject)
	. . ;
	. ; Coverage information for this guarantor
	. s cvgElement=$$zECFNew("Coverages",earElmObject,"A")
	. f ln2=1:1:ret("EAR",ln,"CVG",0) d
	. . s cvgElmObject=$$zECFNewElmtObj(cvgElement)
	. . ; Basic coverage information
	. . s %=$$zECFSet("GroupNumber",ret("EAR",ln,"CVG",ln2,"GroupNumber"),cvgElmObject)
	. . s %=$$zECFSet("GroupName",ret("EAR",ln,"CVG",ln2,"GroupName"),cvgElmObject)
	. . s %=$$zECFSet("MedicareCoverageType",ret("EAR",ln,"CVG",ln2,"MedicareCoverageType"),cvgElmObject)
	. . s %=$$zECFSet("EffectiveFromDate",ret("EAR",ln,"CVG",ln2,"EffectiveFromDate"),cvgElmObject)
	. . s %=$$zECFSet("EffectiveToDate",ret("EAR",ln,"CVG",ln2,"EffectiveToDate"),cvgElmObject)
	. . s %=$$zECFSet("PayorName",ret("EAR",ln,"CVG",ln2,"PayorName"),cvgElmObject)
	. . s %=$$zECFSet("PlanName",ret("EAR",ln,"CVG",ln2,"PlanName"),cvgElmObject)
	. . s %=$$zECFSet("Subscriber",ret("EAR",ln,"CVG",ln2,"Subscriber"),cvgElmObject)
	. . s %=$$zECFSet("BenefitCode",ret("EAR",ln,"CVG",ln2,"BenefitCode"),cvgElmObject)
	. . s %=$$zECFSet("Status",ret("EAR",ln,"CVG",ln2,"Status"),cvgElmObject)
	. . ;
	. . s descriptionElmt=$$zECFNew("Description",cvgElmObject,"A")
	. . f desLn=1:1:ret("EAR",ln,"CVG",ln2,"Description","desCount") d
	. . . s %=$$zECFSetElmt(descriptionElmt,ret("EAR",ln,"CVG",ln2,"Description",desLn))
	. . ;
	. . ; Coverage ID
	. . s idElement=$$zECFNew("ID",cvgElmObject,"A")
	. . s idElmObject=$$zECFNewElmtObj(idElement)
	. . s %=$$zECFSet("ID",ret("EAR",ln,"CVG",ln2,"ID"),idElmObject),%=$$zECFSet("Type","Internal",idElmObject)
	. . ; Add coverage's community ID if present
	. . i ret("EAR",ln,"CVG",ln2,"CID")'="" d
	. . . s idElmObject=$$zECFNewElmtObj(idElement)
	. . . s %=$$zECFSet("ID",ret("EAR",ln,"CVG",ln2,"CID"),idElmObject),%=$$zECFSet("Type","CID",idElmObject)
	. . ;
	. . ; Payor ID
	. . s idElement=$$zECFNew("PayorID",cvgElmObject,"A")
	. . s idElmObject=$$zECFNewElmtObj(idElement)
	. . s %=$$zECFSet("ID",ret("EAR",ln,"CVG",ln2,"PayorID"),idElmObject),%=$$zECFSet("Type","Internal",idElmObject)
	. . ; Add payor's community ID if present
	. . i ret("EAR",ln,"CVG",ln2,"PayorCID")'="" d
	. . . s idElmObject=$$zECFNewElmtObj(idElement)
	. . . s %=$$zECFSet("ID",ret("EAR",ln,"CVG",ln2,"PayorCID"),idElmObject),%=$$zECFSet("Type","CID",idElmObject)
	. . ;
	. . ; Plan ID
	. . s idElement=$$zECFNew("PlanID",cvgElmObject,"A")
	. . s idElmObject=$$zECFNewElmtObj(idElement)
	. . s %=$$zECFSet("ID",ret("EAR",ln,"CVG",ln2,"PlanID"),idElmObject),%=$$zECFSet("Type","Internal",idElmObject)
	. . ; Add plan's community ID if present
	. . i ret("EAR",ln,"CVG",ln2,"PlanCID")'="" d
	. . . s idElmObject=$$zECFNewElmtObj(idElement)
	. . . s %=$$zECFSet("ID",ret("EAR",ln,"CVG",ln2,"PlanCID"),idElmObject),%=$$zECFSet("Type","CID",idElmObject)
	;
	q
	;
	; NAME:         AllowedEAR (PRIVATE)
	; DESCRIPTION:  Checks if a guarantor should be included in the list to choose
	; CALLED BY:    GetGuarsAndCvgs^PSWEARCVG
	; PARAMETERS:
	;  eptID (I,REQ) - Patient ID
	;  dte (I,REQ) - date of appointment
	;  earID (I,REQ) - guarantor ID
	;  earLn (I,REQ) - Line of current guarantor in current patient
	;  saID (I,REQ) - Service area to check against
	;  eclID (I,REQ) - Registration Security Class to use for account restriction checks
	;  allowInactive (I,REQ) - EAF-64245
	;  cvgList (O,REQ) - Resulting coverage list for guarantor if it is good
	;  endDte (I,REQ) - End date for effective checks (discharge date)
	;  inclInactEAR(I,REQ) - flag to include inactive EAR if they have an effective CVG
	;
	; RETURNS:      1 if guarantor is allowed, 0 if not
	;---------
	;*jes 11/12 252001 - Accept eclID parameter
AllowedEAR(eptID,dte,earID,earLn,saID,eclID,allowInactive,cvgList,endDte,inclInactEAR) n inactFlag,allCvgList,cvgLn,count,bType,inactEAR  ;*syn 05/14 314554 +endDte param ;*jvb 05/15 373469 +inactEAR,inclInactEAR
	k cvgList
	;Checks directly on the guarantor
	q:$$zgetnp("EAR",earID,65,1,99999)'=saID 0  ;Account is in different service area
	;*jvb+4 05/15 373469 If inactive, only quit if we do not want inactive accounts
	;q:$_$zgetnp("EAR",earID,55,1,99999)=2 0  ;Account is inactive
	;q:$_$zgetnp("EPT",eptID,2210,earLn,99999)=2 0 ;Account is inactive on patient
	i ($$zgetnp("EAR",earID,55,1,99999)=2)!($$zgetnp("EPT",eptID,2210,earLn,99999)=2) s inactEAR=1
	Q:'inclInactEAR&inactEAR 0
	q:$$RestrictAcct(earID,"",saID,4,eclID) 0  ;Account is restricted    ;*jes 11/12 252001 - Pass in eclID
	;
	;Checks on coverages attached to the guarantor which may filter out that guarantor
	s:$$getOPRpc(61250,1,saID) bType=2   ;EAF-61250 - Exclude hospital only coverages
	s allCvgList(0)=$$fncvgLst($$getidout(eptID,"EPT"),earID,.allCvgList,dte,"","","","",1,"","","","","","","","",bType,"",endDte)  ;Get coverage list   *syn 05/14 314554 - Get future effective coverages as well, if dte not passed
	;
	;Check for any coverages with inactive payors/plans (chkType is blank in the above fncvgLst call, this is to enable the inactive check manually)
	f cvgLn=1:1:allCvgList(0) d
	. i $$inactCvg^PRUTIL1(allCvgList(cvgLn),"",dte) s inactFlag=1 q  ;Do not add inactive payor/plans
	. s count=count+1,cvgList(count)=allCvgList(cvgLn)  ;Build list with only active coverages to return
	s cvgList(0)=+count
	;
	;Respect EAF-64245 with regard to inactive coverages
	i allowInactive=3,inactFlag q 0  ;Setting=3, may not have any inactive coverages
	i allowInactive=4,+cvgList(0)=0 q 0  ;Setting=4, must have at least one active coverage
	Q:inactEAR&(cvgList(0)<1) 0  ;*jvb 05/15 373469 Inactive accounts need to have at least 1 coverage
	q 1
	;
	; NAME:         getEarData (PRIVATE)
	; DESCRIPTION:  Retrieves visit data for a given guarantor
	; CALLED BY:    GetGuarsAndCvgs^PSWEARCVG
	; PARAMETERS:
	;  eptID (I,REQ) - Patient ID
	;  earID (I,REQ) - guarantor ID
	;  earNum (I,REQ) - guarantor number in the reply to client (may not be the same as the line in EPT-2200 group)
	;  ret (O,REQ) - Array to add looked up data to
	;---------
getEarData(eptID,earID,earNum,ret) n ln,found
	s ret("EAR",earNum,"ID")=earID  ;Guarantor ID
	s ret("EAR",earNum,"CID")=$$zGtCID("EAR",earID)   ;Community ID
	s ret("EAR",earNum,"DisplayName")=$$zConfNam("EAR",earID)  ;Guarantor display name
	s ret("EAR",earNum,"Type")=$$zgetCatC($$zgetnp("EAR",earID,210,1,99999),1,"EAR",210)  ;Account type (title)
	s ret("EAR",earNum,"Expiration")=$$zFmtDate($$zgetnp("EAR",earID,217,1,99999)) ;Guarantor contract expiration date
	; Relationship to patient (EPT-2225 for corresponding guarantor)
	f ln=1:1:$$zgetnp("EPT",eptID,2200,0,99999) d  q:found
	. i $$zgetnp("EPT",eptID,2200,ln,99999)=earID d
	. . s found=1   ;Flag to escape loop after setting these values
	. . s ret("EAR",earNum,"RelationshipToPatient")=$$zgetCatC($$zgetnp("EPT",eptID,2225,ln,99999),1,"EAR",255)  ;EPT-2225, relationship to patient
	. . s ret("EAR",earNum,"Comment")=$$zgetnp("EPT",eptID,2240,ln,99999)  ;EPT-2240, comment
	. . i ($$zgetnp("EAR",earID,55,1,99999)=2)!($$zgetnp("EPT",eptID,2210,ln,99999)=2) s ret("EAR",earNum,"Inactive")=1  ;*jvb+1 05/15 373469
	. . e  s ret("EAR",earNum,"Inactive")=0
	q
	;
	; NAME:         getCvgData (PRIVATE)
	; DESCRIPTION:  Retrieves visit data for a given coverage
	; CALLED BY:    GetGuarsAndCvgs^PSWEARCVG
	; PARAMETERS:
	;  eptID (I,REQ) - Patient ID
	;  dte (I,REQ) - date of appointment
	;  earNum (I,REQ) - guarantor number in the reply to client (may not be the same as the line in EPT-2200 group)
	;  cvgID (I,REQ) - coverage ID to check
	;  cvgNum (I,REQ) - which identifier to give this guarantor in the RPC reply
	;  pGlo (I,OPT) - Global name to store coverage data in for repeat lookups later on other guarantors
	;  ret (I/O,REQ) - Array to add looked up data to, and to get data from as needed
	;---------
getCvgData(eptID,dte,earNum,cvgID,cvgNum,pGlo,ret) n cvgData,oa,fromDate,toDate,eptExtID,desLn,isMember,dateArr
	i pGlo="" q  ;
	s eptExtID=$$getidout(eptID,"EPT")  ;Used during member ID comparisons  *jes 11/12 252001
	;
	;See if coverage has already been loaded by a previous guarantor, if so, reuse
	i $d(@pGlo@(cvgID))=0 d   ;If not cached, load and cache
	. s cvgData("ID")=cvgID
	. s cvgData("CID")=$$zGtCID("CVG",cvgID)   ;Community ID
	. s cvgData("PlanID")=$$fngtPln(cvgID,dte,.oa,eptID)
	. s cvgData("PlanCID")=$$zGtCID("EPP",cvgData("PlanID"))   ;Community ID
	. s cvgData("PayorID")=oa("epmId")
	. s cvgData("PayorCID")=$$zGtCID("EPM",cvgData("PayorID"))   ;Community ID
	. s cvgData("GroupNumber")=$$zgetnp("CVG",cvgID,210,1,99999)
	. s cvgData("GroupName")=$$zgetnp("CVG",cvgID,215,1,99999)
	. s cvgData("MedicareCoverageType")=$$zgetCatC($$zgetnp("CVG",cvgID,2015,1,99999),1,"CVG",2015)
	. s cvgData("PayorName")=$$znam("EPM",oa("epmId"))
	. s cvgData("PlanName")=$$znam("EPP",oa("eppId"))
	. s cvgData("Subscriber")=$$zConfNam("CVG",cvgID)
	. s cvgData("BenefitCode")=$$zgetnp("CVG",cvgID,212,1,99999)
	. s cvgData("Status")=$$zgetCatC($$gtVerfSt(cvgID,dte,"","","","","",eptID,121531-dte),1,"EPT",401)
	. ;
	. i oa("ppgId")'="" d  i 1
	. . s cvgData("Description","desCount")=1
	. . s cvgData("Description",1)=$$znam("PPG",oa("ppgId"))
	. e  d
	. . s cvgData("Description","desCount")=+$$zgetnp("CVG",cvgID,30,0,99999)
	. . f desLn=1:1:cvgData("Description","desCount") d
	. . . s cvgData("Description",desLn)=$$zgetnp("CVG",cvgID,30,desLn,99999)
	. ;
	. ;Effective dates (member, then coverage)    ;*syn+15 05/14 314554  use MemDates function to return 1) effective member line on a given date, 2) The nearest future line, 3) The most recent past line
	. ;s cvgData("member","memCount")=+$$ zgetnp("CVG",cvgID,300,0,99999)
	. ;f memLn=1:1:cvgData("member","memCount") d  q:found
	. ;. s memID=$$ zgetnp("CVG",cvgID,300,memLn,99999),cvgData("member","memID"_memLn)=memID   ;CVG-300 stores the external patient ID of the member
	. ;. i memID=eptExtID d  ;Only look at lines where the current patient is the member     ;*jes 11/12 252001 - Use external EPT ID for comparison
	. ;. . s tempFrom=$$ zgetnp("CVG",cvgID,320,memLn,99999)  ;CVG-320, member effective-from date
	. ;. . s tempTo=$$ zgetnp("CVG",cvgID,330,memLn,99999)  ;CVG-330, member effective-to date
	. ;. . i ((tempFrom'>dte)!(tempFrom="")),((tempTo'<dte)!(tempTo="")) d  ;If the current date is within this member line's date range
	. ;. . . s fromDate=tempFrom,toDate=tempTo,found=1  ;Use these dates - no need to keep searching, as there shouldn't be more than one line which qualifies
	. ;
	. ;Fall back to coverage-level dates if member-level dates are blank
	. ;i fromDate="" s fromDate=$$ zgetnp("CVG",cvgID,400,1,99999)  ;CVG-400, effective from date
	. ;i toDate="" s toDate=$$ zgetnp("CVG",cvgID,410,1,99999)  ;CVG-410, effective to date
	. s isMember=$$MemDates(cvgID,eptExtID,dte,.dateArr)  ;should always be a member
	. i isMember d
	. . s fromDate=dateArr("From")
	. . s toDate=dateArr("To")
	. ;Convert to external display format
	. s cvgData("EffectiveFromDate")=$$zFmtDate(fromDate),cvgData("EffectiveToDate")=$$zFmtDate(toDate)  ;*jes 11/12 252001
	. ;Cache data
	. m @pGlo@(cvgID)=cvgData
	;
	m ret("EAR",earNum,"CVG",cvgNum)=@pGlo@(cvgID)  ;Return data
	q
	;
	q  ;;#eor#
	;Routine accessed by Timothy A Coffman
