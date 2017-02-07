CSEPRSE5 ; ; ;2016-10-04 09:14:07;8.3;10mQNw+5N7TqMX2vgkCpFqljtmdG6VqOL0l0uwQ6G4w=
	;;#lib#HULIB/CSILIB/CSULIB/WPLIB
	; Copyright (C) 2009-2016 Epic Systems Corporation
	;*********************************************************************
	; TITLE:   CSEPRSE5
	; PURPOSE: Community Model Patient Lookup
	;          Called By: Epic.EMPI.PatientLookup (E4C command)
	; AUTHOR:  mpatel  (business logic taken from CSEPRSE3)
	;
	; REVISION HISTORY:
	;     *MAP            151706 created (Copied from CSEPRSE3 and modified as needed)
	;     *MAP            158005 replace zgetCat by zgetCatC and minor change (remove a bug)
	;     *apz            234435 use separate node for HTMP
	;     *apz            T2856  use new dup check APIs
	;     *lirwin         246990 fix PtSearch when more than one key value is given
	;     *mjp            247610 fix incorrect DOB translation in 2009 version of PatientLookup
	;     *rscott         251232 allow certain I18N names pieces to be used for matching
	;     *wsun           264707 Format SSN before using to search for duplicate patients in PtLkUpMyCCore
	;     *ywu            276951 add address info, phone numbers and email for patient search
	;     *ywu            281328 fix blank line issue and add more comments
	;     *lbannist 02/15 352037 Remove Identity Foreign license check from PatLkUpExternal
	;     *lrobinso/*jdodson 3/15 356862 allow appointment reminder to go to email collected during OS workflow
	;     *dkoch    10/15 387491 AddPatientExternal: return the ID type set in IIT record 0, rather than always returning "EPI"
	;     *eshaw    12/15 393156 Update external web service auditing for consistency
	;     *rscott   02/16 415730 Add event logging for patient lookup
	;     *jboehr   08/16 438858 Eliminate Cache errors for external services
	;********************************************************************
	q
	;
	;---------
	; NAME:         PatLkUpExternal (PRIVATE)
	; DESCRIPTION:  External wrapper to find a patient via ECF auto-generated services
	;               This is designed so third party entities can call the patient search
	; KEYWORDS:     Pat, Lk, Up, External
	; CALLED BY:    Epic.External.EMPI.LookupPatient, Epic.External.EMPI.LookupPatient2
	; RETURNS:
	; ECF ERRORS:
	;   NO-USER-ID-TYPE ---|
	;   NO-USER-ID         |-> via GetUserFromCommand
	;   NO-USER-FOUND  ----|
	;   INVALID-USER
	;   INTERNAL-ERROR  -> via core logic
	; ASSUMES:
	; DECLARES:     empID,event,errCode,errDetails,needsAuditing
	; SIDE EFFECTS:
	;   - Log E1M event WP_PATIENT_LOOKUP
	;   - Log entries into the WPAUDIT global
	; HISTORY:
	;   *lirwin   05/12 239997 Created
	;   *lbannist 02/15 352037 Remove Identity Foreign license check
	;   *eshaw    12/15 393156 Use shared validation logic
	; Note:         This tag is called by two external web services mentioned above.
	;               Epic.External.EMPI.LookupPatient does not pass in the demographics like
	;               phone numbers, address, email. The code still tries to read these fields,
	;               but will get blank value. Ideally, there should be two tags to handle these
	;               two different web services.
	;---------
PatLkUpExternal ;
	n empID,event,errCode,errDetails,needsAuditing,validationSkip,isSuccess
	;
	s event="WP_PATIENT_LOOKUP" ;E1M 32830
	s needsAuditing=1
	;
	s validationSkip("MYCHART_LICENSE")=1
	s validationSkip("SRO")=1
	s isSuccess=$$isValidExternalRequest^WPEXT(event,.errCode,.errDetails,.empID,.validationSkip)
	i 'isSuccess g pluDone
	;
	g PatLkUpMyCCore
	;
	;---------
	; NAME:         PatLkUpMyChart (PRIVATE)
	; DESCRIPTION:  For use by MyChart open scheduling, we need to include the IDC
	;               override, but NOT the authorization checks which are set up for
	;               external callers
	; KEYWORDS:     Patient, Lookup, MyChart
	; CALLED BY:
	; RETURNS:
	; ASSUMES:
	; DECLARES:     needsAuditing
	; SIDE EFFECTS:
	; HISTORY:
	;   *lirwin/jwalz 06/12 239997 Created
	;---------
PatLkUpMyChart ;
	n needsAuditing
	s needsAuditing=0
	;WARNING: This tag returns values to the called and is also called by the
	;  EXTERNAL versions of patient lookup. If you make changes to the _structure_
	;  of the return for internal use, this needs to be refactored to not share
	;  identical logic when it comes to returning information to the caller.
	g PatLkUpMyCCore
	;
	;---------
	; NAME:         PatLkUpMyCCore (PRIVATE)
	; DESCRIPTION:  Shared search logic used by the epic-released open scheduling site as
	;               well as the external services that also allow for patient search. The
	;               external services should set appropriate assumed variables so that the
	;               proper E1M events are logged.
	;               For various reasons*, this is a copy of the existing logic that can be
	;               found in [PatLkUp]. You should be able to compare that tag to this one
	;               to ensure that the logic has been maintained.
	;               * REASONS:
	;                 - The usage of this tag should prevent the FND search
	;                 - This tag may also need to override the search settings via the WP
	;                   mnemonic
	;                 - This functionality is originally written in 2014 and planned to be
	;                   pushed back to 2012, so the more "new" code the easier and less risky
	;                   it is to push back to 2012.
	; KEYWORDS:     Patient, Lookup, ECF
	; CALLED BY:    PatLkUpExternal, PatLkUpMyChart
	; RETURNS:
	; ASSUMES:      empID,event,errCode,errDetails,needsAuditing
	; DECLARES:     netINI
	; SIDE EFFECTS:
	; HISTORY:
	;   *lirwin       239997 Created
	;   *lirwin       243940 No cache errors when non-unique result found
	;   *rscott       251232 allow certain I18N names pieces to be used for matching
	;   *wsun         264707 Format SSN before using to search for duplicate patients
	;   *ywu          276951 add address info, phone numbers and email for patient search
	;   *ywu          281328 fix blank line issue
	;   *lrobinso/*jdodson 3/15 356862 allow appointment reminder to go to email collected during OS workflow
	;   *eshaw  12/15 393156 handle new E1M mnemonics
	;   *rscott 02/16 415730 Add event logging for patient lookup
	;   *jboehr 08/16 438858 Eliminate Cache errors for external services
	;
	; Note:         This tag is called shared among three things:
	;               - Epic.External.EMPI.LookupPatient
	;               - Epic.External.EMPI.LookupPatient2
	;               - Open scheduling
	;               Ideally, there should be one core tag to handle core lookup function
	;               and have three tags to handle ECF gets and sets but calling the same
	;               core function here. Due to the time limit and SU implications, it was
	;               not done this way.
	;---------
PatLkUpMyCCore ;
	n srchIDTy,srchID,val,idTyp,INI,uniq,itm,ovrd,netINI,ID,SSN
	n idc,exclfunc,wt,done,ZER,IDI,cnt,twr,citm,uniqHTMP,realCnt,phoneType,phoneNumber,LineCount,phone,addr
	n returnEptID,nameObj,namestr
	n isSuccess,infoArray
	n email
	;
	s uniq=$$zGetESUn("^HSTEMPI") ;get a unique node in the global ^HSTEMPI
	i uniq="" s errCode="INTERNAL-ERROR",errDetails="uniq empty" g pluDone
	s INI="EPT",itm=""
	d %zidrule("EPT")
	;s altSrch=##zECFGet("SearchType")  ;*lirwin 239997 External version does not allow search type control
	s val=$$zECFGet("RegionRestriction")
	i val]"" s ovrd(5635,1)=val
	s idTyp=$$zECFGet("IDTypeMnemonic")
	;
	s val=""
	; *rscott+5 251232 allow certain I18N names pieces to be used for matching
	s nameObj=$$zECFGet("NameComponents")
	i nameObj'="" d
	. s namestr=$$getNameStringECF(nameObj)  ;Collection of discrete name pieces.
	. s val=$$FormatName^HUNAME("EPT","","",1,"","",namestr,"")
	e  d  ;
	. s val=$$zECFGet("Name")  ;Already-formatted name string.
	i val]"" s itm=.2 d SaveInput(val)
	;
	s srchIDTy=$$zECFGet("PatientIDType")
	s srchID=$$zECFGet("PatientID")
	i srchIDTy']"" s ovrd("ID",1)=srchID
	s SSN=$$zECFGet("SSN")
	i SSN'="" d
	. s %=$$zValidateSSN(SSN,.SSN)
	. s ovrd(160,1)=SSN
	s val=$$zECFGet("DOB")
	i val]"" s itm=110 d SaveInput(val)
	s val=$$zECFGet("Gender")
	i val]"" s itm=130 d SaveInput(val)
	;get address info  *ywu++ 276951
	s addr=$$zECFGet("Demographics")
	s LineCount=$$zECFNumElmts("Street",addr)
	i LineCount>0 d
	. f cnt=1:1:LineCount d
	. . s val=$$zECFGetElmt("Street",addr,cnt)
	. . i val'="" s realCnt=realCnt+1,ovrd(50,realCnt)=val
	. s ovrd(50,0)=realCnt
	s val=$$zECFGet("HouseNumber",addr)
	i val'="" s itm=82 d SaveInput(val)
	s val=$$zECFGet("City",addr)
	i val'="" s itm=60 d SaveInput(val)
	s val=$$zECFGet("District",addr)
	i val'="" s itm=83 d SaveInput(val)
	s val=$$zECFGet("State",addr)
	i val'="" s itm=70 d SaveInput(val)
	s val=$$zECFGet("PostalCode",addr)
	i val'="" s itm=80 d SaveInput(val)
	s val=$$zECFGet("County",addr)
	i val'="" s itm=75 d SaveInput(val)
	s val=$$zECFGet("Country",addr)
	i val'="" s itm=78 d SaveInput(val)
	s val=$$zECFGet("Email",addr)
	i val'="" d
	. s itm=85 d SaveInput(val)
	. s itm=86 d SaveInput(val) s ovrd(86,0)=1
	. s email=val   ;*lrobinso/*jdodson 356862 need to cache email entered by patient
	s LineCount=$$zECFNumElmts("Phones",addr)
	i LineCount>0 d
	. s realCnt=0
	. f cnt=1:1:LineCount d
	. . s phone=$$zECFGetElmt("Phones",addr,cnt)
	. . s phoneType=$$zECFGet("Type",phone)
	. . s phoneNumber=$$zECFGet("Number",phone)
	. . s phoneNumber=$$chkPhone^EAPHONE(phoneNumber)
	. . q:phoneNumber=""
	. . s realCnt=realCnt+1
	. . s ovrd(94,realCnt)=phoneType
	. . s ovrd(98,realCnt)=phoneNumber
	. . i phoneType="8" s itm=5170 d SaveInput(phoneNumber)
	. . i phoneType="7" s itm=90 d SaveInput(phoneNumber)
	. s ovrd(94,0)=realCnt
	. s ovrd(98,0)=realCnt
	;
	;s patientElmt=##zECFNew("LookupPatientResult",,"S")  ;*lirwin External version returns at most one patient, so just put it in the main response object
	i srchIDTy]"" d  i ID]"" s %=$$addPatientExternal(ID) g pluDone  ; *MAP 158005 - use $$ instead of 'd' while calling a function ;if an ID and ID Type are passed in then return info without a search
	. i srchIDTy="CID" s ID=$$zCIDgtID("EPT",srchID)
	. e  d
	. . s srchIDTy=$$znxIxID("IIT",600,srchIDTy,"")  ;get ID Type from desc
	. . i srchIDTy]"" s ID=$$zDnxID("EPT",2060,srchIDTy,2061,srchID)
	;
	;  ## Run the search
	n currID
	;i altSrch d fndSrch q  ;*lirwin 239997 External version does not allow search type control
	s idc=$$getWPIDCOvr()
	s:idc="" idc=$$verIDC^HUDUPUTL("","","",INI)
	;
	;!!!! *lirwin Per emails with EMPI, this is typo. making id->"" (which is what 'id' would be here) to make HXPARSE happy
	s %=$$DatatoAll^IDUPPROC(idc,"","",.uniqHTMP,.ovrd)  ;*mjp+1 243372 use new API
	;s %=##mpiDups(INI,"",idc,.uniqHTMP,.ovrd,.err)    ;we should remove the dup checker?  ;*apz++ 234435 use separate node for HTMP
	i uniqHTMP="" s uniqHTMP=%EA("MPI","EXTMPI","UNIQ")
	i uniqHTMP="" s errCode="INTERNAL-ERROR",errDetails="uniqHTMP empty" g pluDone
	s %EA("H","SA","ALL")=1   ;need to setup so user has access to all SAs
	s exclfunc="" s exclfunc=^ED0("EPT","FindID","EXF")
	;
	s cnt=0
	f  s wt=$o(^HTMP(uniqHTMP,idc,wt)) q:wt=""  d  q:done  ;*mjp+1 243372 second subscript is now IDC ID
	. f  s currID=$o(^HTMP(uniqHTMP,idc,wt,currID)) q:currID=""  d  q:done
	. . i exclfunc'="" s IDI=currID,ZER="" x exclfunc k IDI i ZER'="" s ZER="" q
	. . s cnt=cnt+1
	. . i cnt>1 s done=1 q  ;*lirwin 239997 return at most one result
	. . s ^HSTEMPI(uniq,"A",cnt)=$$zGtCID(INI,currID,"","",.citm,.twr)_"|"_$$znam(INI,currID)
	. . s returnEptID=currID
	;
	d CleanupCompare^IDUPPROC(uniqHTMP)  ;*mjp 243372 new cleanup call. Should have had a k ^HTMP(uniqHTMP) before...
	;
	;*lirwin 239997 return at most one patient
	i cnt=1 d  i 1
	. s %=$$addPatientExternal(returnEptID)
	. ;*lrobinso/*jdodson 356862 need to cache email entered by patient
	. i email'="",errCode="" d  ;yes, error code shout not be set, but just in case someone misses a GOTO above
	. . ;wondering about *? workaround, see [2968120] MYCHART / CACHING / ALLOW FOR EMPTY EPT OR WPR BUT NOT BOTH
	. . ;also read note about * usage in EmailApptReminder^WPOPNSCH2
	. . s %=$$setWCACHE("*",returnEptID,"OSemail",email)
	. s %=$$logPatient("WPAUD_OS_PT_LOOKUP_BY_DEMOG",$$getidout(returnEptID,"EPT"))  ;*rscott 02/16 415730 Add event logging for patient lookup
	e  i cnt>1 s errCode="NO-UNIQUE-RESULT",errDetails="Multiple matching patients detected"
	e  s errCode="NO-UNIQUE-RESULT",errDetails="No matching patients detected"
	;
pluDone ;
	i errCode'="" s isSuccess=0
	e  s isSuccess=1
	i needsAuditing=1 d  i 1
	. s infoArray("PTCOUNT")=cnt
	. d auditEventUsage^WPEXT(1,isSuccess,errCode,empID,event,.infoArray)
	;
	i errCode'="" s %=$$zECFThrow(errCode,errDetails,1) ;
	q
	;
	;---------
	; NAME:         getNameStringECF (PRIVATE)
	; DESCRIPTION:  Pulls out discrete name pieces from the specified ECF object
	;               and assembles them into the identity-approved C5-delimited
	;               string format. (http://wiki.epic.com/main/I18N/How_To/Names#Piece_format)
	; CALLED BY:    PatLkUpMyCCore
	; RETURNS:      A C5-delimited string of name pieces that is usable by Identity APIs.
	; HISTORY:
	;   *rscott 11/12 251232 Created - allow certain I18N names pieces to be used for matching
	;---------
getNameStringECF(nameObj) ;
	n namestr,lastName,spouseLastName,firstName,middleName,prefix,spousePrefix,title,suffix
	n academic,preferredName,preferredNameType,spouseLastNameFirst,givenNameInitials,c5
	s lastName=$$zECFGet("LastName",nameObj)
	s spouseLastName=$$zECFGet("LastNameFromSpouse",nameObj)
	s firstName=$$zECFGet("FirstName",nameObj)
	s middleName=$$zECFGet("MiddleName",nameObj)
	s prefix=$$zECFGet("LastNamePrefix",nameObj)
	s spousePrefix=$$zECFGet("SpouseLastNamePrefix",nameObj)
	s title=""  ;##GetSingleCatValue^WPICUTIL(##zECFGet("Title",nameObj),"EAN",1070)  ;Not currently sent. Uncomment to enable.
	s suffix=$$GetSingleCatValue^WPICUTIL($$zECFGet("Suffix",nameObj),"EAN",1080)
	s academic=""  ;##GetSingleCatValue^WPICUTIL(##zECFGet("Academic",nameObj),"EAN",1090)  ;Not currently sent. Uncomment to enable.
	s preferredName=""  ;##zECFGet("PreferredName",nameObj)  ;Not currently sent. Uncomment to enable.
	s preferredNameType=""  ;##GetSingleCatValue^WPICUTIL(##zECFGet("PreferredNameType",nameObj),"EAN",1110)  ;Not currently sent. Uncomment to enable.
	s spouseLastNameFirst=$$GetSingleCatValue^WPICUTIL($$zECFGet("SpouseLastNameFirst",nameObj),"ECT",101)
	s givenNameInitials=$$zECFGet("GivenNameInitials",nameObj)
	s c5=$c(5)
	s namestr=lastName_c5_spouseLastName_c5_firstName_c5_middleName_c5_prefix
	s namestr=namestr_c5_spousePrefix_c5_title_c5_suffix_c5_academic_c5_preferredName
	s namestr=namestr_c5_preferredNameType_c5_spouseLastNameFirst_c5_givenNameInitials
	q namestr
	;
	;---------
	; NAME:         getWPIDCOvr (PRIVATE)
	; DESCRIPTION:  Looks up the "WP_PT_SRCH_IDC" mnemonic that may be
	;               set in WDF or EpicCare. This is used to change the IDC used
	;               to search for patients when only ONE patient is a valid result
	; KEYWORDS:     get, WPIDCOvr
	; CALLED BY:    PatLkUpExternal
	; RETURNS:      IDC .1 if a valid one found, "" otherwise
	; ASSUMES:
	; DECLARES:
	; SIDE EFFECTS:
	; HISTORY:
	;   *lirwin 06/12 239997 Created
	;---------
getWPIDCOvr() n id
	s id=$$getMnemCode("WP_PT_SRCH_IDC")                        ;look for mnem in WDF or EpicCare
	i id="" q ""                                                ;if nothing defined, quit now
	i $e(id,1,4)="cid." s id=$$zCIDgtID("IDC",$e(id,5,$l(id)))  ;if defined using CID, resolve to local .1
	i '$$zisR("IDC",id) s id=""                                 ;make sure this is a valid ID
	q id
	;
	;patLkUp: Returns a patient's demographic info (does a search if needed)
	; Implements ECF transaction - Epic.EMPI.PatientLookup
PatLkUp n srchIDTy,srchID,val,idTyp,INI,uniq,altSrch,itm,ovrd,netINI,ID,patients
	n idc,exclfunc,wt,done,ZER,IDI,cnt,twr,citm,uniqHTMP  ;*apz 234435 +uniqHTMP
	s uniq=$$zGetESUn("^HSTEMPI") ;get a unique node in the global ^HSTEMPI
	s INI="EPT",itm=""
	d %zidrule("EPT")
	s altSrch=$$zECFGet("SearchType") ;get the search type: foundations search or identity search
	s val=$$zECFGet("RegionRestriction")
	i val]"" s ovrd(5635,1)=val
	s idTyp=$$zECFGet("IDTypeMnemonic")
	s val=""
	s val=$$zECFGet("Name")
	i val]"" s itm=.2 d SaveInput(val)
	s srchIDTy=$$zECFGet("PatientIDType")
	s srchID=$$zECFGet("PatientID")
	i srchIDTy']"" s ovrd("ID",1)=srchID
	s ovrd(160,1)=$$zECFGet("SSN")
	s val=$$zECFGet("DOB")
	;i val]"" s itm=110 d SaveInput(val)  ;*mjp+1 247610 Do not use SaveInput, since zECFGet already gives us the DTE
	s:val'="" ovrd(110,1)=val
	s val=$$zECFGet("Gender")
	i val]"" s itm=130 d SaveInput(val)
	s patients=$$zECFNew("Patients",,"A")
	i srchIDTy]"" d  i ID]"" s %=$$addPatient(ID,patients) q  ;if an ID and ID Type are passed in then return info without a search
	. i srchIDTy="CID" s ID=$$zCIDgtID("EPT",srchID)
	. e  d
	. . s srchIDTy=$$znxIxID("IIT",600,srchIDTy,"")  ;get ID Type from desc
	. . i srchIDTy]"" s ID=$$zDnxID("EPT",2060,srchIDTy,2061,srchID)
	;
RunSrch                   ;Run the search
	n currID
	i altSrch d fndSrch q            ; foundations search
	s idc=$$verIDC^HUDUPUTL("","","",INI)
	s %=$$DatatoAll^IDUPPROC(idc,,"",.uniqHTMP,.ovrd) ;$$ mpiDups(INI,id,idc,.uniqHTMP,.ovrd,.err)   ;*apz++ 234435 use separate node for HTMP  ; *apz T2856 use new dup check API
	i uniqHTMP="" s uniqHTMP=%EA("MPI","EXTMPI","UNIQ")
	s %EA("H","SA","ALL")=1   ;need to setup so user has access to all SAs
	s exclfunc="" s exclfunc=^ED0("EPT","FindID","EXF")
	f  s wt=$o(^HTMP(uniqHTMP,idc,wt)) q:wt=""  d  q:done     ; *apz T2856 use idc subscript
	. f  s currID=$o(^HTMP(uniqHTMP,idc,wt,currID)) q:currID=""  d  q:done
	. . i exclfunc'="" s IDI=currID,ZER="" x exclfunc k IDI i ZER'="" s ZER="" q
	. . s cnt=cnt+1
	. . s ^HSTEMPI(uniq,"A",cnt)=$$zGtCID(INI,currID,"","",.citm,.twr)_"|"_$$znam(INI,currID)
	. . s %=$$addPatient(currID,patients)
	; *apz T2856 clean up HTMP results
	d CleanupCompare^IDUPPROC(uniqHTMP)
	q
	;
	;---------
	; NAME: fndSrch
	; DESCRIPTION: Implements foundations patient search algorithm
	; REVISION HISTORY:
	;   *MAP 02/12/09 151706 created (copied from fndSrch^CSEPRSE3)
	;
fndSrch n name,ws,sex,dob,ssn,sa,INI,currID,i,string,entry,nodeNum,id,return
	n excl,resume,tmpglob,rnum,recmax
	s ws=1
	s name=ovrd(.2,1) k ovrd(.2,1)
	s sex=ovrd(130,1) k ovrd(130,1)
	s dob=ovrd(110,1) k ovrd(110,1)
	s ssn=ovrd(160,1) k ovrd(160,1)
	s id=ovrd("ID",1) k ovrd("ID",1)
	s sa=$$zCIDgtID("EAF",ovrd(5635,1)) k ovrd(5635,1)
	s inBuf("Sender")=ws
	s excl=$$MkExcl^CSEPRSE4(.ovrd)       ; build extra exclusions
	s return=$$FindPats^CSEPRSE1(id,name,sex,dob,ssn,sa,.resume,excl)
	q:return=0
	s nodeNum=$p(return,"^",2)
	s uniq=$p(return,"^",1)
	s tmpglob=$$GetRecSrchGlob^HUWFID(uniq) q:tmpglob=""
	s INI="EPT"
	f i=1:1:nodeNum d
	. s string=string_@tmpglob@(i)
	. s recmax=$l(string,"^")
	. f rnum=1:1:$s(i<nodeNum:recmax-1,1:recmax) d
	. . s entry=$p(string,"^",rnum) q:entry=""
	. . s currID=$p(entry,"|",1) q:currID=""
	. . s %=$$addPatient(currID,patients)
	. i i<nodeNum,recmax>1 s string=$p(string,"^",recmax)
	d %zRelTmpGlo(tmpglob)
	q
	;
	;---------
	; NAME: SaveInput (internal)
	; DESCRIPTION: saves the data in a global which is later used to send patient information via ECF
	; PARAMETERS:
	;   val - value to be stored
	; ASSUMES: itm,netINI,ovrd
	; REVISION HISTORY:
	;   *MAP 02/12/09 151706 created
	;   *lirwin 246990 do not retain twrguc between calls - the items are different! And each item is called once, so why even try to retain it?
	;      Also, allow category abbreviations and values to also work as valid input.
SaveInput(val) n twItm,catVal
	i itm="" q
	s twItm=$$zgetTWR("EPT",itm)
	s:$p(twItm,",",1)="D" val=($$xml2instKpDt^CSEPRSE4(val)\86400)
	i $p(twItm,",",1)="C" d        ;cat?
	. q:$$zisCat(val,"EPT",itm)=1  ;if value is already an active category, let it through. *lirwin++ 246990 Extending the current coding logic. Might be worth looking into using [GetCatTitle^EGENETCMN / GetSingleCatValue^WPICUTIL] here.
	. s catVal=val
	. s val=$$zgetCatN(catVal,1,"EPT",itm)
	. s:val="" val=$$zgetCatN(catVal,2,"EPT",itm)
	s netINI=$p($p(twItm,",",14),";",2)
	i netINI]"" s val=$$zCIDgtID(netINI,val)
	s val=$$getidout(val,netINI)
	s ovrd(itm,1)=val
	s itm=""   ; Prep for the next item value
	q
	;
	;---------
	; NAME: addPatient
	; DESCRIPTION: Returns patient information via ECF.
	; PARAMETERS:
	;   ID (I,REQ)    - Patient ID
	;   patients - reference to ECF dynamic object
	; REVISION HISTORY:
	;   *MAP 02/12/09 151706 created
	;
addPatient(ID,patients) n patElmt,tempRef,tempRefElmt,pcp,pcpArray,lOwner,iit,temp
	n twr11,twr90,twr5170,twr5435,twr5100,twr775,twr750,twr760
	s patElmt=$$zECFNewElmtObj(patients)
	s %=$$zECFSet("Name",$$NameOut^HUNETNM("EPT",ID),patElmt)
	; IDs
	s tempRef=$$zECFNew("IDs",patElmt,"A")
	s tempRefElmt=$$zECFNewElmtObj(tempRef)
	s %=$$zECFSet("Type","CID",tempRefElmt)
	s %=$$zECFSet("ID",$$zGtCID(INI,ID,"","",.citm,.twr11),tempRefElmt)
	s tempRefElmt=$$zECFNewElmtObj(tempRef)
	s %=$$zECFSet("Type","EPI",tempRefElmt)
	s %=$$zECFSet("ID",$$mpiGetID("EPT",ID,-1,0,"","",1),tempRefElmt)
	i idTyp]"" d                     ;requested ID
	. s iit=$$znxIxID("IIT",600,idTyp)
	. s tempRefElmt=$$zECFNewElmtObj(tempRef)
	. s %=$$zECFSet("Type",idTyp,tempRefElmt)
	. s %=$$zECFSet("ID",$$mpiGetID("EPT",ID,-1,iit,"","",1,1),tempRefElmt)
	s nID=$$mpiGetID("EPT",ID,-1,0,"","",1)
	s nID=$$getidin(nID,"EPT")
	; SSN,DOB,Gender,Status and MaritalStatus
	s %=$$zECFSet("SSN",$$GetVal(ID,160),patElmt)
	s %=$$zECFSet("DOB",$$GetVal(ID,110),patElmt)
	s %=$$zECFSet("Gender",$$GetVal(ID,130),patElmt)
	s %=$$zECFSet("Status",$$GetVal(ID,102),patElmt)
	s %=$$zECFSet("MaritalStatus",$$GetVal(ID,140),patElmt)
	; Addresses
	s tempRef=$$zECFNew("Addresses",patElmt,"A")
	s tempRefElmt=$$zECFNewElmtObj(tempRef)
	s %=$$zECFSet("Type","Permanent",tempRefElmt)
	s %=$$zECFSet("Street",$$GetVal(ID,50),tempRefElmt)
	s %=$$zECFSet("City",$$GetVal(ID,60),tempRefElmt)
	s %=$$zECFSet("County",$$GetVal(ID,75),tempRefElmt)
	s %=$$zECFSet("State",$$GetVal(ID,70),tempRefElmt)
	s %=$$zECFSet("Zip",$$GetVal(ID,80),tempRefElmt)
	s %=$$zECFSet("Country",$$GetVal(ID,78),tempRefElmt)
	s %=$$zECFSet("Email",$$GetVal(ID,85),tempRefElmt)
	s tempRef1=$$zECFNew("Phones",tempRefElmt,"A")
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","HOME",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,90,1,99999,.twr90),tempRefElmt1)
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","WORK",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,5170,1,99999,.twr5170),tempRefElmt1)
	;
	s tempRefElmt=$$zECFNewElmtObj(tempRef)
	s %=$$zECFSet("Type","Temporary",tempRefElmt)
	s %=$$zECFSet("Street",$$GetVal(ID,5430),tempRefElmt)
	s %=$$zECFSet("City","",tempRefElmt)
	s %=$$zECFSet("County",$$GetVal(ID,75),tempRefElmt)
	s %=$$zECFSet("State",$$GetVal(ID,5432),tempRefElmt)
	s %=$$zECFSet("Zip",$$GetVal(ID,5434),tempRefElmt)
	s %=$$zECFSet("Country",$$GetVal(ID,5433),tempRefElmt)
	s %=$$zECFSet("Email","",tempRefElmt)
	s tempRef1=$$zECFNew("Phones",tempRefElmt,"A")
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,5435,1,99999,.twr5435),tempRefElmt1)
	; Employment Info.
	s tempRef=$$zECFNew("EmploymentInformation",patElmt,"S")
	s %=$$zECFSet("EmployerName",$$NameOut^HUNETNM("EEP",$$geti("EPT",ID,5100,1,99999,.twr5100)),tempRef)
	s %=$$zECFSet("Occupation",$$GetVal(ID,5180),tempRef)
	s tempRef1=$$zECFNew("Phones",tempRef,"A")
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","",tempRefElmt1)
	s %=$$zECFSet("Number",$$GetVal(ID,5160),tempRefElmt1)
	; Emergency contact
	s tempRef=$$zECFNew("EmergencyContact",patElmt,"S")
	s %=$$zECFSet("LegalGuardian",$$geti("EPT",ID,775,1,99999,.twr775),tempRef)
	s %=$$zECFSet("Name",$$GetVal(ID,700),tempRef)
	s %=$$zECFSet("Relation",$$GetVal(ID,770),tempRef)
	s tempRef1=$$zECFNew("Phones",tempRef,"A")
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","HOME",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,750,1,99999,.twr750),tempRefElmt1)
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","WORK",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,760,1,99999,.twr760),tempRefElmt1)
	; PCP
	s tempRef=$$zECFNew("PCPs",patElmt,"A")
	s %=$$getPCP(ID,"","","","",1,.pcpArray)
	f  s pcp=$o(pcpArray(pcp)) q:pcp']""  d
	. s tempRefElmt=$$zECFNewElmtObj(tempRef)
	. s %=$$zECFSet("Type",pcpArray(pcp),tempRefElmt)
	. s %=$$zECFSet("Name",$$NameOut^HUNETNM("SER",pcp),tempRefElmt)
	. s %=$$zECFSet("ID",$$zGtCID("SER",$$getidin(pcp,"SER")),tempRefElmt)
	; Home Deployment
	s lOwner=$$zGetLOwn(INI,ID)
	s %=$$zECFSet("HomeDeployment",$$zgetnp("ECI",lOwner,100,1,99999),patElmt)
	; NameComponents
	s eanID=$$getn("EPT",$$getidin(ID,"EPT"),1,"300;4")
	;s eanID=$$ getidin(eanID,"EAN") ;*apz 234435 not needed for EAN
	s nameComponents=$$zECFNew("NameComponents",patElmt,"S")
	s %=$$zECFSet("History",$$zgetnp("EAN",eanID,13,1,99999),nameComponents)
	s %=$$zECFSet("FullLastName",$$zgetnp("EAN",eanID,900,1,99999),nameComponents)
	s %=$$zECFSet("LastName",$$getn("EAN",eanID,1,"1000;1"),nameComponents)
	s %=$$zECFSet("LastNameFromSpouce",$$getn("EAN",eanID,1,"1000;2"),nameComponents)
	s %=$$zECFSet("FirstName",$$getn("EAN",eanID,1,"1000;3"),nameComponents)
	s %=$$zECFSet("MiddleName",$$getn("EAN",eanID,1,"1000;4"),nameComponents)
	s %=$$zECFSet("LastNamePrefix",$$getn("EAN",eanID,1,"1000;5"),nameComponents)
	s %=$$zECFSet("SpouceLastNamePrefix",$$getn("EAN",eanID,1,"1000;6"),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;7")
	s %=$$zECFSet("Title",$$zgetCatC(temp,1,"EAN",1070),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;8")
	s %=$$zECFSet("Suffix",$$zgetCatC(temp,1,"EAN",1080),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;9")
	s %=$$zECFSet("Academic",$$zgetCatC(temp,1,"EAN",1090),nameComponents)
	s %=$$zECFSet("PrefferedName",$$getn("EAN",eanID,1,"1000;10"),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;11")
	s %=$$zECFSet("PreferredNameType",$$zgetCatC(temp,1,"EAN",1110),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;12")
	s %=$$zECFSet("SpouceLastNameFirst",$$zgetCatC(temp,1,"EAN",1120),nameComponents)
	s %=$$zECFSet("GivenNameInitials",$$getn("EAN",eanID,1,"1000;13"),nameComponents)
	q 1
	;
	;
	;---------
	; NAME: addPatientExternal
	; DESCRIPTION: Returns patient information via ECF.
	; CALLED BY:   Directly from PatLkUpMyCCore. But more importantly from both the
	;              external and internal services: PatLkUpExternal, PatLkUpMyChart
	; PARAMETERS:
	;   ID (I,REQ)    - Patient ID
	; ASSUMES:      INI,citm,idTyp
	; REVISION HISTORY:
	;   *lirwin 05/12 239997 Created this copy to use for External service. could be easily combined
	;     with the existing AddPatient for INTERNAL use, but the mantra for the initial code release
	;     was to not impact existing code so as to limit the testing scope and danger. So, please do
	;     so at your earliest conveinence in the current dev.
	;
	;     Looks like all that needs to happen is to properly handle the difference between
	;     a list of patients and just one patient (create a new element or not)
	;     My vote is to change the addPatient to accept the patient element to
	;     add to and not always create an element in the addPatient function.
	;     So the previous code would be addPatient(ID,##zECFNewElmtObj(patients))
	;     The new code would would call addPatient(ID,"")
	;   *dkoch 10/15 387491 Return the ID type set in IIT record 0, rather than always returning "EPI"
	;
	;  WARNING: This tag returns values to the called and is also called by both the
	;  EXTERNAL & INTERNAL versions of patient lookup. If you make changes to the
	;  _structure_ (this should only be allowed for internal use) then you will need
	;  to examine the code paths for internal and external callers to be sure that the
	;  rules of web services are respected and the existing contract is not broken
addPatientExternal(ID) n tempRef,tempRefElmt,pcp,pcpArray,lOwner,iit,temp
	n twr11,twr90,twr5170,twr5435,twr5100,twr775,twr750,twr760
	n eanID,nID,nameComponents,tempRef1,tempRefElmt1
	s %=$$zECFSet("Name",$$NameOut^HUNETNM("EPT",ID),"")
	;
	; IDs
	s tempRef=$$zECFNew("IDs","","A")
	s tempRefElmt=$$zECFNewElmtObj(tempRef)
	s %=$$zECFSet("Type","CID",tempRefElmt)
	s %=$$zECFSet("ID",$$zGtCID(INI,ID,"","",.citm,.twr11),tempRefElmt)
	s tempRefElmt=$$zECFNewElmtObj(tempRef)
	s %=$$zECFSet("Type",$$geti("IIT",0,600,1,99999),tempRefElmt)
	s %=$$zECFSet("ID",$$mpiGetID("EPT",ID,-1,0,"","",1),tempRefElmt)
	i idTyp]"" d                     ;requested ID
	. s iit=$$znxIxID("IIT",600,idTyp)
	. s tempRefElmt=$$zECFNewElmtObj(tempRef)
	. s %=$$zECFSet("Type",idTyp,tempRefElmt)
	. s %=$$zECFSet("ID",$$mpiGetID("EPT",ID,-1,iit,"","",1,1),tempRefElmt)
	s nID=$$mpiGetID("EPT",ID,-1,0,"","",1)
	s nID=$$getidin(nID,"EPT")
	;
	; SSN,DOB,Gender,Status and MaritalStatus
	s %=$$zECFSet("SSN",$$GetVal(ID,160),"")
	s %=$$zECFSet("DOB",$$GetVal(ID,110),"")
	s %=$$zECFSet("Gender",$$GetVal(ID,130),"")
	s %=$$zECFSet("Status",$$GetVal(ID,102),"")
	s %=$$zECFSet("MaritalStatus",$$GetVal(ID,140),"")
	;
	;  Addresses
	s tempRef=$$zECFNew("Addresses","","A")
	s tempRefElmt=$$zECFNewElmtObj(tempRef)
	s %=$$zECFSet("Type","Permanent",tempRefElmt)
	s %=$$zECFSet("Street",$$GetVal(ID,50),tempRefElmt)
	s %=$$zECFSet("City",$$GetVal(ID,60),tempRefElmt)
	s %=$$zECFSet("County",$$GetVal(ID,75),tempRefElmt)
	s %=$$zECFSet("State",$$GetVal(ID,70),tempRefElmt)
	s %=$$zECFSet("Zip",$$GetVal(ID,80),tempRefElmt)
	s %=$$zECFSet("Country",$$GetVal(ID,78),tempRefElmt)
	s %=$$zECFSet("Email",$$GetVal(ID,85),tempRefElmt)
	s tempRef1=$$zECFNew("Phones",tempRefElmt,"A")
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","HOME",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,90,1,99999,.twr90),tempRefElmt1)
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","WORK",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,5170,1,99999,.twr5170),tempRefElmt1)
	;
	s tempRefElmt=$$zECFNewElmtObj(tempRef)
	s %=$$zECFSet("Type","Temporary",tempRefElmt)
	s %=$$zECFSet("Street",$$GetVal(ID,5430),tempRefElmt)
	s %=$$zECFSet("City","",tempRefElmt)
	s %=$$zECFSet("County",$$GetVal(ID,75),tempRefElmt)
	s %=$$zECFSet("State",$$GetVal(ID,5432),tempRefElmt)
	s %=$$zECFSet("Zip",$$GetVal(ID,5434),tempRefElmt)
	s %=$$zECFSet("Country",$$GetVal(ID,5433),tempRefElmt)
	s %=$$zECFSet("Email","",tempRefElmt)
	s tempRef1=$$zECFNew("Phones",tempRefElmt,"A")
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,5435,1,99999,.twr5435),tempRefElmt1)
	;
	; Employment Info
	s tempRef=$$zECFNew("EmploymentInformation","","S")
	s %=$$zECFSet("EmployerName",$$NameOut^HUNETNM("EEP",$$geti("EPT",ID,5100,1,99999,.twr5100)),tempRef)
	s %=$$zECFSet("Occupation",$$GetVal(ID,5180),tempRef)
	s tempRef1=$$zECFNew("Phones",tempRef,"A")
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","",tempRefElmt1)
	s %=$$zECFSet("Number",$$GetVal(ID,5160),tempRefElmt1)
	;
	; Emergency contact
	s tempRef=$$zECFNew("EmergencyContact","","S")
	s %=$$zECFSet("LegalGuardian",$$geti("EPT",ID,775,1,99999,.twr775),tempRef)
	s %=$$zECFSet("Name",$$GetVal(ID,700),tempRef)
	s %=$$zECFSet("Relation",$$GetVal(ID,770),tempRef)
	s tempRef1=$$zECFNew("Phones",tempRef,"A")
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","HOME",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,750,1,99999,.twr750),tempRefElmt1)
	s tempRefElmt1=$$zECFNewElmtObj(tempRef1)
	s %=$$zECFSet("Type","WORK",tempRefElmt1)
	s %=$$zECFSet("Number",$$geti("EPT",ID,760,1,99999,.twr760),tempRefElmt1)
	;
	; PCPs
	s tempRef=$$zECFNew("PCPs","","A")
	s %=$$getPCP(ID,"","","","",1,.pcpArray)
	f  s pcp=$o(pcpArray(pcp)) q:pcp']""  d
	. s tempRefElmt=$$zECFNewElmtObj(tempRef)
	. s %=$$zECFSet("Type",pcpArray(pcp),tempRefElmt)
	. s %=$$zECFSet("Name",$$NameOut^HUNETNM("SER",pcp),tempRefElmt)  ;*MAP 158005 - remove extra comma (",")
	. s %=$$zECFSet("ID",$$zGtCID("SER",$$getidin(pcp,"SER")),tempRefElmt)
	;
	; Home Deployment
	s lOwner=$$zGetLOwn(INI,ID)
	s %=$$zECFSet("HomeDeployment",$$zgetnp("ECI",lOwner,100,1,99999),"")
	;
	; NameComponents
	s eanID=$$getn("EPT",$$getidin(ID,"EPT"),1,"300;4")
	s nameComponents=$$zECFNew("NameComponents","","S")
	s %=$$zECFSet("History",$$zgetnp("EAN",eanID,13,1,99999),nameComponents)
	s %=$$zECFSet("FullLastName",$$zgetnp("EAN",eanID,900,1,99999),nameComponents)
	s %=$$zECFSet("LastName",$$getn("EAN",eanID,1,"1000;1"),nameComponents)
	s %=$$zECFSet("LastNameFromSpouce",$$getn("EAN",eanID,1,"1000;2"),nameComponents)
	s %=$$zECFSet("FirstName",$$getn("EAN",eanID,1,"1000;3"),nameComponents)
	s %=$$zECFSet("MiddleName",$$getn("EAN",eanID,1,"1000;4"),nameComponents)
	s %=$$zECFSet("LastNamePrefix",$$getn("EAN",eanID,1,"1000;5"),nameComponents)
	s %=$$zECFSet("SpouceLastNamePrefix",$$getn("EAN",eanID,1,"1000;6"),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;7")
	s %=$$zECFSet("Title",$$zgetCatC(temp,1,"EAN",1070),nameComponents) ; *MAP+10 158005 - use zgetCatC instead of zgetCat
	s temp=$$getn("EAN",eanID,1,"1000;8")
	s %=$$zECFSet("Suffix",$$zgetCatC(temp,1,"EAN",1080),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;9")
	s %=$$zECFSet("Academic",$$zgetCatC(temp,1,"EAN",1090),nameComponents)
	s %=$$zECFSet("PrefferedName",$$getn("EAN",eanID,1,"1000;10"),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;11")
	s %=$$zECFSet("PreferredNameType",$$zgetCatC(temp,1,"EAN",1110),nameComponents)
	s temp=$$getn("EAN",eanID,1,"1000;12")
	s %=$$zECFSet("SpouceLastNameFirst",$$zgetCatC(temp,1,"EAN",1120),nameComponents)
	s %=$$zECFSet("GivenNameInitials",$$getn("EAN",eanID,1,"1000;13"),nameComponents)
	q 1
	;
	;
	;---------
	; NAME: GetVal (internal)
	; DESCRIPTION: Gets value for an item.
	; PARAMETERS:
	;   ID  - record ID
	;   itm - Item number
	;   line - Line number
	;   INI - Master file
	; REVISION HISTORY:
	;   *MAP 02/12/09 151706 created
	;
GetVal(ID,itm,line,INI,twr,addlFlg) q:itm="" ""
	n netINI,val
	s:INI="" INI="EPT"
	s:+line=0 line=1
	s val=$$geti(INI,ID,itm,line,99999,.twr)
	q:val="" val
	i $p(twr,",",1)="D" s val=$$zdoutISO(+val) q val
	i $p(twr,",",1)="C" s val=$$zgetCat(val,1,INI,itm) q val
	s netINI=$p($p(twr,",",14),";",2)         ; networked? Get CID
	i netINI]"" d
	. i 'addlFlg s val=$$zGtCID(netINI,val) q
	. s val=$$znam(netINI,val)  ;for additional data, return the record name.
	q val
	q  ;;#eor#
	;Routine accessed by Timothy A Coffman
