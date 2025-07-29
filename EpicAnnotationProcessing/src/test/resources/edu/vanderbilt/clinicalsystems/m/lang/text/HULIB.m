HULIBFN ; ; ;2016-05-06 15:45:58;8.3;ONaGXHnymoc2apfere10UOfBKeI9LuVVa8I8SOyETd8keJIGmp2BTyEJyaNn0AkR
	;;#lib#HULIB/AIlib/KPULIB
	;;#lglob#HULIB
	; Copyright (C) 1995-2016 Epic Systems Corporation
	;*********************************************************************
	; TITLE:   HULIBFN
	; PURPOSE: Shared library functions related to coverages
	; AUTHOR:  many
	; CALLABLE TAGS:
	;    fngtPln - Retrieved the unformatted IDs for the benefit plan
	;              and related info for a coverage
	;    fngtPlnD - Same as fngtPln except info is returned formatted
	;    fnprCvg - Find primary coverage for a member
	;    fncvgLst - Create an array of a member's coverages
	;    fneffCvg - Determine if a patient is covered by a coverage
	;    fnGpPln - Retrieves the unformatted IDs for the benefit plan
	;              and related info for an employer (plan) group
	;    fnGpPlnD - Same as fnGpPln except info is returned formatted
	;    fnisNet - True if provider and plan have matching networks
	;    fncvgDts - Returns eff and term dates for member on coverage
	;    fnHARcvgLst - create an array of the coverages on a HAR
	;    DoesPlanSuppBillType - Does plan support bill type?
	;    PlanBillTypeOnly - Does plan ONLY support bill type?
	;    MCgetCvgSubLn - Find the subscriber line on a coverage
	;
	; REVISION HISTORY:
	; *asb 02/14 298991 - Add ppgId and skipRiders parameters to fnGtPln and fnGtPlnD
	; *wbw 09/14 326906 - Return lobId with fnGpPln
	; *imhh 1/16 402687 - Add MCgetCvgSubLn function
	; *sj   2/16 407162 - update isFOTrkOn to support new option
	;*********************************************************************
	;****
	; Parameter info (for all routines):
	; cvgId (I) : Coverage
	; ppgId (I) : Employer/plan group
	; dte (I) : M date
	; oa (O) : Pass in a dotted array name; returns:
	; oa("ppgId")   : Plan group
	; oa("mcrId")   : Carrier
	; oa("prodTyp") : Product type
	; oa("eppId")   : Benefit plan
	; oa("epmId")   : Payor
	;****
	; The following functions are called recursive and thus makes routines
	; calling them grow astronomically large.  Therefore they have been
	; moved to HULIC.  All fn functions which call other fn functions
	; should be treated in the same manner.
	;
	;---------
	; NAME:         fngtPln (PUBLIC)
	; DESCRIPTION:  Gets the payor, plan, plan group, etc for a coverage that are effective as of the specified date.
	; KEYWORDS:     Coverage,Benefit,Plan,Group,Payor,Library,Rider
	; CALLED BY:    Many, through the fngtPln function in HULIB library
	; PARAMETERS:
	;  cvgId (I,REQ) - coverage ID
	;  dte (I,OPT,DEFAULT:+$h) - date to use for plan group information
	;  oa (O,OPT) - Returns information about the coverage:
	;                    oa("epmId")   : Payor
	;                    oa("eppId")   : Benefit plan
	;                    oa("ppgId")   : Plan group
	;                    oa("mcrId")   : Carrier
	;                    oa("prodTyp") : Product type
	;                    oa("lobId")   : Line of business
	;                    oa("riderId",ln) : Riders
	;                    oa("cvgTyp")  : Coverage type
	;  eptId (I,OPT) - padded patient ID, to look up riders for MC cvgs
	;  payrOnly (I,OPT) - if true, only look up payor
	;  pyplOnly (I,OPT) - if true, only look up payor, plan, and group
	;                     also looks up carrier and prod type for free
	;  ppgId (I,OPT,DEFAULT:geti(cvgId,115,1,99999)) - The coverage record's employer group / plan group ID
	;  skipRiders (I,OPT) - if true, skip the rider lookup
	; RETURNS:      Returns the benefit plan ID. Kills and fills oa.
	; ASSUMES:
	; SIDE EFFECTS:
	; Rider algorithm:  loop through PPG if rider is sel mode,
	;                   find rider in CVG and compare mode to subScr flag
	;---------
	; *asb 02/14 298991 add ppgId,skipRiders
fngtPln(cvgId,dte,oa,eptId,payrOnly,pyplOnly,ppgId,skipRiders) ;
	q $$fngtPln^HULIC(cvgId,dte,.oa,eptId,payrOnly,pyplOnly,ppgId,skipRiders)
	;;#eof#  ;;#inline#
	;
	;---------
	; NAME:         fngtPlnD (PRIVATE)
	; DESCRIPTION:  Same as fngtPln except this the data is returned formatted with
	;               netOut and CatOut for VB displays
	; PARAMETERS:
	;  cvgId (I,REQ) - coverage ID
	;  dte (I,OPT,DEFAULT:+$h) - date to use for plan group information
	;  oa (O,OPT) - Returns information about the coverage:
	;                    oa("epmId")   : Payor
	;                    oa("eppId")   : Benefit plan
	;                    oa("ppgId")   : Plan group
	;                    oa("mcrId")   : Carrier
	;                    oa("prodTyp") : Product type
	;                    oa("lobId")   : Line of business
	;                    oa("riderId",ln) : Riders
	;                    oa("cvgTyp")  : Coverage type
	;  eptId (I,OPT) - padded patient ID, to look up riders for MC cvgs
	;  payrOnly (I,OPT) - if true, only look up payor
	;  pyplOnly (I,OPT) - if true, only look up payor, plan, and group
	;                     also looks up carrier and prod type for free
	;  ppgId (I,OPT,DEFAULT:geti(cvgId,115,1,99999)) - The coverage record's employer group / plan group ID
	;  skipRiders (I,OPT) - if true, skip the rider lookup
	; RETURNS:      Returns the benefit plan ID. Kills and fills oa.
	; ASSUMES:      Nothing
	; SIDE EFFECTS: None
	;---------
	; *asb 02/14 298991 add payrOnly,pyplOnly,ppgId,skipRiders
fngtPlnD(cvgId,dte,oa,eptId,payrOnly,pyplOnly,ppgId,skipRiders) ;
	q $$fngtPlnD^HULIC(cvgId,dte,.oa,eptId,payrOnly,pyplOnly,ppgId,skipRiders)
	;;#eof# ;;#inline#
	;---------
	;
	; NAME: fnprCvg
	; DESCRIPTION: Finds primary coverage for a patient
	; RETURNS: Returns CvgId of the primary coverage, "" if none.
fnprCvg(eptId,earId,dte,eptDat,noMed,mcrId,mngPyr,noEAR,noMSP,bType,etrId,tarId,tarLn,whichFO,harId) q $$fnprCvg^HULIC(eptId,earId,dte,eptDat,noMed,mcrId,mngPyr,noEAR,noMSP,bType,etrId,tarId,tarLn,whichFO,.harId) ;;#eof#
	;
	;---------
	; NAME:         DoesPlanSuppBillType (PUBLIC)
	; DESCRIPTION:  Tag for determining whether a benefit plan supports the passed in
	;               billing type (EPP-85 if conversion 126139 has run; EPP-95, otherwise).
	; PARAMETERS:
	;   eppId (I/REQ) - Internal Id of benefit plan (EPP) to check
	;   bType (I/REQ) - Billing type to check for on the plan (corresponding to category value in EPP-85)
	;   isConv (IO/OPT) - Whether the conversion 126139 has been run for this EPP
	;                     Pass in return value for multiple calls.
	; RETURNS:      1 if supports the billing type; 0, otherwise
	;---------
DoesPlanSuppBillType(eppId,bType,isConv) n epp95,ret
	i (eppId="")!(bType="") q 0
	;;#strip# ;Get conversion value if not given
	i isConv="" s isConv=$$zIsConv("EPP",eppId,126139)
	;;#strip# ;Return whether EPP-85 index has a count for this value
	i isConv=1 d  q ret
	. i (bType<3),(+$$zgetnp("EPP",eppId,85,0)=0) s ret=1 q
	. s ret=+$$zisIxID("EPP",85,bType,eppId)
	;;#strip# ;Only billing types 1 & 2 existed before conversion
	i bType>2 q 0
	s epp95=$$zgetnp("EPP",eppId,95,1,99999)
	;;#strip# ;Satisfies if looking for 1 or 2 and epp95 is both or ""
	i ((epp95=3)!(epp95="")),((bType=1)!(bType=2)) q 1
	;;#strip# ;Quit with match result
	q epp95=bType ;;#eof#
	;
	;
	;---------
	; NAME:         PlanBillTypeOnly (PUBLIC)
	; DESCRIPTION:  Tag for determining whether a benefit plan only
	;               supports the passed in billing type (EPP-85 if
	;               conversion 126139 has run, EPP-95, otherwise).
	; PARAMETERS:
	;   eppId (I/REQ)   - Internal Id of benefit plan (EPP) to check
	;   bType (I/REQ)   - Billing type to check for on the plan
	;                     (corresponding to category value in EPP-85)
	;   isConv (IO/OPT) - Whether the conversion 126139 has been run for
	;                     this eppId
	; RETURNS: 1 if the plan only supports bType, 0 otherwise
	;---------
PlanBillTypeOnly(eppId,bType,isConv) n ret,epp95
	i (eppId="")!(bType="") q 0
	s:isConv="" isConv=$$zIsConv("EPP",eppId,126139)
	i 'isConv d  q ret
	. s epp95=$$zgetnp("EPP",eppId,95,1,99999)
	. i epp95=3 s ret=0 q
	. s:epp95="" epp95=1
	. s ret=(epp95=bType)
	i +$$zgetnp("EPP",eppId,85,0)=0 q 0    ;no values means PB *and* HB
	q:'(+$$zisIxID("EPP",85,bType,eppId)) 0
	q:+$$zgetnp("EPP",eppId,85,0)<2 1
	q 0 ;;#eof#
	;
	;----------
	; NAME:         fncvgLst
	; DESCRIPTION:  create an array of patient;s coverages with checks.
	; PARAMETERS:
	;   eppId (I/REQ)   - Internal Id of benefit plan (EPP) to check
	;   bType (I/REQ)   - Billing type to check for on the plan
	;                     (corresponding to category value in EPP-85)
	;   isConv (IO/OPT) - Whether the conversion 126139 has been run for
	;                     this eppId
	; RETURNS: 1 if the plan only supports bType, 0 otherwise
	;----------
fncvgLst(eptExt,earId,cvgList,dte,cvgTyp,mcrId,epmId,eppId,noEAR,chkSA,eptDat,noMed,in,ChkTyp,noEffChk,noMSP,mngPyr,bType,hospFO,disDt,bTypes,skpSec,inclShared,etrId,tarId,tarLn,whichFO,harId) ;
	q $$fncvgLst^HULIC4(eptExt,earId,.cvgList,dte,cvgTyp,mcrId,epmId,eppId,noEAR,chkSA,eptDat,noMed,.in,ChkTyp,noEffChk,noMSP,mngPyr,bType,hospFO,disDt,bTypes,skpSec,inclShared,etrId,tarId,tarLn,whichFO,.harId) ;;#eof#
	;---------
	; NAME:         FOVISIT
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Constant representing visit-level filing order
	; RETURNS:      1
	;---------
FOVISIT() q 1 ;;#eof# ;;#inline#
	;---------
	; NAME:         FOPATIENT
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Constant representing patient-level filing order
	; RETURNS:      0
	;---------
FOPATIENT() q 0 ;;#eof# ;;#inline#
	;
	;
	;----------
	; NAME:         fneffCvg
	; DESCRIPTION:  determine if a patient is covered
	; RETURNS: 1 if coverage is effective, 0 otherwise.
	;      oa("from"),oa("until"),oa("status"),oa("memId) may be set
fneffCvg(cvgId,efDt,eptExt,oa,ineffCvg,ChkType,disDTE,src) q $$fneffCvg^HULIC1(cvgId,efDt,eptExt,.oa,ineffCvg,ChkType,disDTE,src)  ;;#eof#
	;---------
	; NAME:         fnGpPln
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Retrieves the information for the oa array given the group id
	;               and the DTE.  Outgoing information is unformatted IDs.
	; PARAMETERS:
	;  ppgId (I,REQ) - Employer group ID
	;  dte (I,REQ) - Date to get plan information for
	;  oa (O,REQ) - Output array for the benefit plan info. Contains the following nodes:
	;       "eppId"         Benefit plan ID
	;       "riderId",0     Number of riders on plan
	;       "riderId",n     nth rider on the plan
	;       "benDat"        Dat for benefit plan line in PPG
	;       "mcrId"         Carrier for benefit plan
	;       "prodTyp"       Product type for benefit plan
	;       "epmId"         Payor for benefit plan
	;       "PlnEffDte"     Effective date for benefit plan
	;       "lobId"         Line of Business for benefit plan
	;  pastOk (I,OPT) - Pass 1 to allow lookup of past benefit plan information
	; RETURNS:      1 on success, null on failure
	;---------
fnGpPln(ppgId,dte,oa,pastOk) q:(ppgId="")!(dte="") ""
	n numBens,i,benDat,bestDat,ln,pak,cnt
	s %=$$din(dte) s:%]"" dte=%
	i 'pastOk s:dte<+$H dte=+$H
	;Find the proper effective date to apply.
	s numBens=$$getn("PPG",ppgId,0,"18200;1;1"),bestDat=-1,ln=-1
	i +numBens=0 d  q 1  ;5.5 ppg structure
	. s benDat=121531-dte-1
	. s benDat=$$zoDT("PPG",ppgId,100,benDat) q:benDat=""
	. s oa("eppId")=$$zgetnp("PPG",ppgId,100,1,benDat)
	. s cnt=$$zgetnp("PPG",ppgId,120,0,benDat)
	. f ln=1:1:cnt s oa("riderId",ln)=$$zgetnp("PPG",ppgId,120,ln,benDat)
	. s oa("riderId",0)=cnt
	f i=1:1:numBens d
	. s benDat=$$getn("PPG",ppgId,i,"18200;1;1")
	. i benDat]"",(benDat'>dte),(benDat>bestDat) s bestDat=benDat,ln=i
	i ln'<0 s pak=$$zgetblu("PPG",ppgId,18200,ln)
	s oa("benDat")=$s(ln<0:"",+numBens=0:benDat,1:$$zgetpcu(pak,6))
	q:ln<0 ""
	s oa("mcrId")=$$zgetpcu(pak,2)
	s oa("prodTyp")=$$zgetpcu(pak,3)
	s oa("epmId")=$$zgetpcu(pak,4)
	s oa("eppId")=$$zgetpcu(pak,5)
	s oa("PlnEffDte")=$$zgetpcu(pak,1)
	s oa("lobId")=$$zgetpcu(pak,8)  ;*WBW 09/14 326906 Return lobId
	q 1 ;;#eof#
	;---------
	; NAME:         fnGpPlnD
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Same as fnGpPln execpt this the data is returned formatted with
	;               netOut and CatOut for VB displays
	; PARAMETERS:
	;  ppgId (I,REQ) - Employer group ID
	;  dte (I,REQ) - Date to get plan information for
	;  oa (O,REQ) - Output array for the benefit plan info. Contains the following nodes:
	;       "eppId"         Name of benefit plan
	;       "riderId",0     Number of riders on plan
	;       "riderId",n     nth rider on the plan
	;       "benDat"        Dat for benefit plan line in PPG
	;       "mcrId"         Name of carrier for benefit plan
	;       "prodTyp"       Name of product type for benefit plan
	;       "epmId"         Name of payor for benefit plan
	;       "PlnEffDte"     Effective date for benefit plan
	;       "lobId"         Line of Business for benefit plan
	;  pastOk (I/O/IO,REQ/OPT,DEFAULT:<value>) - <description>
	; RETURNS:      1 on success, null on failure
	;---------
fnGpPlnD(ppgId,dte,oa,pastOk) ;
	q:$$fnGpPln(ppgId,dte,.oa,pastOk)="" ""  ;*imhh 1/16 402687
	s oa("mcrId")=$$netOut("MCR",".2",".1","NI",oa("mcrId"))
	s oa("prodTyp")=$$catOut("PPG",18005,"T",oa("prodTyp"))  ;*imhh 1/16 402687 replace deprecated function
	s oa("epmId")=$$netOut("EPM",".2",".1","NI",oa("epmId"))
	s oa("eppId")=$$netOut("EPP",".2",".1","NI",oa("eppId"))
	q 1 ;;#eof#
	;---------
	; NAME:         fnisNet
	; STATUS:       DEPRECATED
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Check whether a provider is in a network
	; PARAMETERS:
	;  serId (I,REQ) - internal provider ID
	;  eptId (I,REQ) - patient ID
	;  cvgId (I,REQ) - coverage ID
	;  dte (I,REQ) - check on this date, M date format
	;---------
	;*imhh+1 1/16 402687
fnisNet(serId,eptId,cvgId,dte) q ($$serNetIn(eptId,cvgId,dte,serId)'="")   ;;#eof# ;;#inline# ;;#deprecated# Use ($$)serNetIn instead
	;---------
	; NAME:         fncvgDts
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Find the effective and term dates for a member on a coverage.
	; PARAMETERS:
	;  eptExt (I,REQ) - external ID of member record
	;  cvgId (I,REQ) - ID of coverage record
	;  oa (O,REQ) - Output array.
	;                 oa("effDt") = effective date
	;                 oa("tmDt") = term date
	;  apptDt (I,OPT) - date of service
	; RETURNS:      0 if the returned date is coverage-level, 1 if member-level
	;---------
fncvgDts(eptExt,cvgId,oa,apptDt) n ln,found,cnt
	s found=0,(oa("effDt"),oa("tmDt"))=""
	s ln=$$fnocvgpt(cvgId,eptExt,"","",apptDt)
	s:ln]"" found=1
	d:found
	. s oa("effDt")=$$zgetnp("CVG",cvgId,320,ln)
	. s oa("tmDt")=$$zgetnp("CVG",cvgId,330,ln)
	. s oa("line")=ln
	d:oa("effDt")=""&(oa("tmDt")="")!'found
	. i apptDt="" d  i 1
	. . s oa("effDt")=$$zgetnp("CVG",cvgId,400,1)
	. . s oa("tmDt")=$$zgetnp("CVG",cvgId,410,1)
	. . s:oa("effDt")]""!(oa("tmDt")]"") found=2
	. e  d
	. . s found=0
	. . f cnt=1:1:$$zgetnp("CVG",cvgId,400,0) q:found=2  d
	. . . s oa("effDt")=$$zgetnp("CVG",cvgId,400,cnt)
	. . . s oa("tmDt")=$$zgetnp("CVG",cvgId,410,cnt)
	. . . i apptDt'>oa("tmDt"),apptDt'<oa("effDt") s found=2
	q found ;;#eof#
	;
	; NAME:fnCvgFC
	; DESCRIPTION:
	; return fin class for a coverage, if it can't find one it
	; will return the fin class for the account's default FC based on SA
	; setup or alternatively the account's financial class
	; PARAMS:
	; forPrice (I) if true will return the fin class setup in
	; the fin class for pricing items in EPP & EPM
	; REVISION:
	; if no cvgId="" and for pricing, use profile default FC
	; from the account's service area
	; *jdb 192831 fixed checking for epmId
fnCvgFC(earId,cvgId,dte,epmId,forPrice) n fc,eppId,oa
	i cvgId'="",$$zgetnp("CVG",cvgId,120,1)=4 s fc=$$zgetnp("CVG",cvgId,650,1)
	i fc'="",forPrice d
	. i (+epmId)=0,cvgId'="" s:oa("epmId")="" %=$$fngtPln^HULIC(cvgId,dte,.oa,"",1) s epmId=oa("epmId")
	. i fc=$$geti("EPM",epmId,700,1) s fc=""
	i fc'="" q fc
	;
	i cvgId]"",forPrice s eppId=$$fngtPln^HULIC(cvgId,dte,.oa) s fc=$$zgetnp("EPP",eppId,2115,1) q:fc]"" fc
	i (+epmId)=0,cvgId]"" s:oa("epmId")="" %=$$fngtPln^HULIC(cvgId,dte,.oa,"",1) s epmId=oa("epmId")
	i +epmId'=0 d  q:fc]"" fc
	. i forPrice s fc=$$zgetnp("EPM",epmId,701,1) q:fc]""
	. s fc=$$zgetnp("EPM",epmId,700,1)
	i earId]"" d  q:fc]"" fc
	. s fc=$$gtActFC(earId) q:fc]""
	. s fc=$$zgetnp("EAR",earId,220,1)
	q fc  ;;#eof#
	;
	;*ech+tag
	; NAME: gtActFC
	; DESCRIPTION: returns the default financial class for an account by looking up the account type and service area defaults. This function does not return the account financial class.
	; PARAMS: earId=account ID
	; RETURNS: default financial class of account if found.
gtActFC(earId) n actType,sa,ovride,finCls,altSa
	s actType=$$zgetnp("EAR",earId,210,1,99999)
	s sa=$$zgetnp("EAR",earId,65,1,99999)
	s ovride=$$hugeti("EAF",sa,5830,99999,actType)
	i ovride]"" s finCls=ovride i 1
	e  s finCls=$$zgetnp("EAF",sa,5800,1,99999)
	q:finCls]"" finCls
	s altSa=$$zgetnp("EAF",sa,236,1,99999)
	s ovride=$$hugeti("EAF",altSa,5830,99999,actType)
	i ovride]"" s finCls=ovride i 1
	e  s finCls=$$zgetnp("EAF",altSa,5800,1,99999)
	q:finCls]"" finCls
	q:$$zgetnp("EAF",sa,222,1)'=1 finCls
	s finCls=$$hugeti("EAF",1,5830,99999,actType)
	q:finCls]"" finCls
	s finCls=$$zgetnp("EAF",1,5800,1,99999)
	q finCls  ;;#eof#
	;
	;---------
	; NAME:   fngtPyr
	; DESCRIPTION:  given coverage Id and date, returns payor ID
	; KEYWORDS:     payor, epm
	; PARAMETERS: cvgId - coverage ID
	;             date - DTE
	; RETURNS:   epmId (Payor id)
fngtPyr(cvgId,date) n array,epmId
	i cvgId']"" q ""
	s:date="" date=+$h
	s %=$$fngtPln^HULIC(cvgId,date,.array,"",1)
	s epmId=array("epmId")
	s:(+epmId=0) epmId=$$zgetnp("CVG",cvgId,100,1)
	q epmId ;;#eof#
	;---------
	; NAME:         fnHARcvgLst
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Gets the list of coverages on the hospital account.
	;               If harID is not passed in, eptID and eptDAT will be used
	;               to get the HAR ID from I EPT 2500.
	;               Returns number of coverages, and cvgLst.
	; PARAMETERS:
	;  harID (IO,OPT) - hospital account ID
	;  eptID (I,OPT) - patient record ID
	;  eptDAT (I,OPT) - patient record DAT
	;  cvgLst (O,REQ) - list of coverages
	; RETURNS:      The number of coverages
	;---------
	;*imhh+tag 1/16 402687 change parameter names
fnHARcvgLst(harID,eptID,eptDAT,cvgLst) n ln
	i harID="" d  q:harID="" 0
	. i eptID=""!(eptDAT="") q
	. s harID=$$zgetnp("EPT",eptID,2500,1,eptDAT)
	s cvgLst(0)=$$zgetnp("HAR",harID,300,0,99999)
	f ln=1:1:cvgLst(0) s cvgLst(ln)=$$zgetnp("HAR",harID,300,ln,99999)
	q cvgLst(0) ;;#eof#
	;
	;---------
	; NAME:         gtcvgcxt (PUBLIC)
	; DESCRIPTION:  Gets the context/info about a coverage
	; KEYWORDS:     context,info,coverage
	; CALLED BY:   gtDfltGL^NPBACAD4, TstProc^NPBCALC1 (Basically premium billing only, as of now)
	; PARAMETERS:
	; Input parameters
	;  cvgId - Coverage ID
	;  eptId - Patient INTERNAL Id (unpadded ID will not work)
	;  effDt - effective date checked, default is today
	;  oa    - array to pass back information
	;  pcpTyp - PCP type, defaul is 1
	;  spons - sponsorship of network: 1 carrier sponsored; 2-SA sponsored
	;  locFlag - location flag, see function $fgtPCSA for detail
	;  hdata   - hdata array with value in hdata("HDF",3005).  This parameter is not passed in one.
	;            But will be used if available.
	; Output parameters (all set in oa array)
	;  ppgId - Employer group ID
	;  eppId - Benefit plan ID
	;  mcrId - Carrier
	;  epmId - Payor ID
	;  lobId - LOB ID
	;  eafId - Primary location
	;  SA    - Service area
	;  serExt, serId - member PCP
	;  rkpId - risk panel Id
	;  netExt - network unpadded ID
	;  netId - netowork ID
	;  pbs   - Owning SA or PBS ID
	; ASSUMES: hdata
	; RETURNS: Various coverage info set in oa array
gtcvgcxt(cvgId,eptId,effDt,oa,pcpTyp,spons,locFlag) n effDat,dat,i,ln,str,tmp,h3005,rpTermDt
	s:+effDt=0 effDt=+$H
	s:pcpTyp="" pcpTyp=1
	s effDat=121531-effDt-1
	;;#strip# Employer group
	s oa("ppgId")=$$zgetnp("CVG",cvgId,115,1,99999)
	i oa("ppgId") d
	. s dat=$$zoDT("PPG",oa("ppgId"),100,effDat) q:dat=""
	. ;;#strip# Benefit plan
	. s oa("eppId")=$$zgetnp("PPG",oa("ppgId"),100,1,dat)
	. ;;#strip# Payor
	. s oa("epmId")=$$zgetnp("PPG",oa("ppgId"),95,1,dat)
	. ;;#strip# For backward compatible
	. s:oa("epmId")="" oa("epmId")=$$zgetnp("PPG",oa("ppgId"),90,1)
	. ;;#strip# LOB
	. s oa("lobId")=$$zgetnp("PPG",oa("ppgId"),80,1,dat)
	. ;;#strip# Carrier
	. s oa("mcrId")=$$zgetnp("PPG",oa("ppgId"),18001,1,dat)
	s dat=$$zoDT("EPT",eptId,5615,effDat)
	;;#strip# Primary location
	s:dat oa("eafId")=$$zgetnp("EPT",eptId,5615,1,dat)
	s:oa("eafId")]"" oa("SA")=$$zgetnp("EAF",oa("eafId"),5250,1)
	i oa("SA")="",locFlag<2 s oa("SA")=$$zgetnp("EPT",eptId,5635,1)
	i oa("SA")="",locFlag<1 s oa("SA")=1
	;;#strip#  ; Get PCP, may use the indexed item if available
	s ln=$$zmaxunl($$zgetblu("EPT",eptId,80100,0))
	f i=1:1:ln d  q:$d(oa("serId"))
	. s str=$$zgetblu("EPT",eptId,80100,i)
	. q:($$zgetpcu(str,12)=1)
	. q:$$zgetpcu(str,4)'=pcpTyp
	. q:$$zgetpcu(str,5)>effDt
	. s tmp=$$zgetpcu(str,6)
	. i tmp'="",tmp<effDt q
	. s oa("serExt")=$$zgetpcu(str,3)
	. s oa("serId")=$$getidin(oa("serExt"),"SER")
	s ln=$$zgetnp("SER",oa("serId"),3190,0)
	f i=1:1:ln d  q:$d(oa("rkpId"))
	. i $$zgetnp("SER",oa("serId"),3195,i)>effDt q
	. s rpTermDt=$$zgetnp("SER",oa("serId"),3196,i)
	. i rpTermDt'="",effDt>rpTermDt q
	. s oa("rkpId")=$$zgetnp("SER",oa("serId"),3190,i)
	s oa("pbs")=$$MCgetOwnPBS("CVG",cvgId)  ;*imhh 1/16 402687 use business segmentation library function
	;;#strip#  ;Get network
	;;#strip#  ;Restrict network search by svc area? 1- No, 2 -Yes
	i $d(hdata("HDF",3005)) s h3005=hdata("HDF",3005) i 1
	e  s h3005=($$zgetnp("HDF",1,3005,1,99999)=2)
	;;#strip#  ;Below code is copied from %zEptNet^HULICNET for backward compatibility
	i oa("epmId")="",%EA("CurrApp")=4 d
	. n epmId,eppId
	. d %zLuEpm^HULICNET(.epmId,.eppId)
	. s oa("epmId")=epmId,oa("eppId")=eppId
	n netId,netIds
	;;#strip#  ;Those items should be packed for faster search
	i oa("ppgId")]"" d  q:oa("netId")]"" 1  ;Check employer group
	. f  s netId=$$znxIxID("NET",180,oa("ppgId"),netId) q:netId=""  d  q:oa("netId")]""
	. . q:$d(netIds(netId))   ;Already checked
	. . s netIds(netId)=""
	. . q:'$$chknet(netId,.spons,.oa,h3005)
	. . s ln=$$zgetnp("NET",netId,180,0,99999)
	. . f i=1:1:ln d  q:oa("netId")]""
	. . . q:$$zgetnp("NET",netId,180,i,99999)'=oa("ppgId")
	. . . s %=$$zgetnp("NET",netId,182,i,99999)
	. . . i %>0,%>effDt q
	. . . s %=$$zgetnp("NET",netId,184,i,99999)
	. . . i %>0,%<effDt q
	. . . s oa("netId")=netId,oa("netExt")=$$sp(netId)
	;;#strip#  ;Those items should be packed for faster search
	i oa("eppId")]"" d  q:oa("netId")]"" 1  ;Check benefit plan
	. f  s netId=$$znxIxID("NET",195,oa("eppId"),netId) q:netId=""  d  q:oa("netId")]""
	. . q:$d(netIds(netId))  ;Already checked
	. . s netIds(netId)=""
	. . q:'$$chknet(netId,.spons,.oa,h3005)
	. . s ln=$$zgetnp("NET",netId,195,0,99999)
	. . f i=1:1:ln d  q:oa("netId")]""
	. . . q:$$zgetnp("NET",netId,195,i,99999)'=oa("eppId")
	. . . s %=$$zgetnp("NET",netId,197,i,99999)
	. . . i %>0,%>effDt q
	. . . s %=$$zgetnp("NET",netId,198,i,99999)
	. . . i %>0,%<effDt q
	. . . s oa("netId")=netId,oa("netExt")=$$sp(netId)
	;;#strip#  ;Those items should be packed for faster search
	i oa("epmId")]"" d  q:oa("netId")]"" 1 ;Check payor
	. f  s netId=$$znxIxID("NET",170,oa("epmId"),netId) q:netId=""  d  q:oa("netId")]""
	. . q:$d(netIds(netId))  ;Already checked
	. . s netIds(netId)=""
	. . q:'$$chknet(netId,.spons,.oa,h3005)
	. . s ln=$$zgetnp("NET",netId,170,0,99999)
	. . f i=1:1:ln d  q:oa("netId")]""
	. . . q:$$zgetnp("NET",netId,170,i,99999)'=oa("epmId")
	. . . s %=$$zgetnp("NET",netId,175,i,99999)
	. . . i %>0,%>effDt q
	. . . s %=$$zgetnp("NET",netId,177,i,99999)
	. . . i %>0,%<effDt q
	. . . s oa("netId")=netId,oa("netExt")=$$sp(netId)
	i oa("netId")="" d  q:oa("netId")]"" 1
	. f  s netId=$$znxIxID("NET",150,1,netId) q:netId=""  d  q:oa("netId")]""
	. . q:$d(netIds(netId))  ;Already checked
	. . q:'$$chknet(netId,spons,.oa,h3005)
	. . s oa("netId")=netId,oa("netExt")=$$sp(netId)
	s oa("netId")="",oa("netExt")=""
	q 1    ;;#eof#
	;---------
	; NAME:         gtgrpmcr
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Get carrier from employer group
	; PARAMETERS:
	;  ppgId (I,REQ) - employer group ID
	;  effDt (I,OPT,DEFAULT:+$h) - effective date
	; RETURNS:      The ID of the carrier
	;---------
gtgrpmcr(ppgId,effDt) q:ppgId="" ""
	n dat,numPlans,line,carrier,index,date
	s:+effDt=0 effDt=+$H
	s numPlans=$$getn("PPG",ppgId,0,"18200;1;1"),line=-1
	i +numPlans=0 d  q carrier ;If the 18200 superitem doesn't exist, get carrier from 18001
	. s dat=$$zoDT("PPG",ppgId,100,121531-effDt-1)
	. i dat="" s carrier="" q
	. s carrier=$$zgetnp("PPG",ppgId,18001,1,dat)
	f index=1:1:numPlans d  q:line>0  ;Find the date closest to effDt without going past it
	. s date=$$getn("PPG",ppgId,index,"18200;1;1")
	. i date]"",(date'>effDt) s line=index
	q:line<0 ""
	q $$getn("PPG",ppgId,line,"18200;2;1") ;Return the carrier found in piece two of SI 18200
	;;#eof#
	;
	; Check network status, sponsorship and SA
	; Called by the above function gtcvgcxt
	;---------
	; NAME:         chknet (PRIVATE)
	; DESCRIPTION:  Checks validity of a network. Check network status, sponsorship, business segment and Service area.
	; KEYWORDS:     validity,network,status,sponsorship,business segment,service area
	; CALLED BY:    gtcvgcxt
	; PARAMETERS:
	;  ID - network ID
	;  spons - Sponsorship type requested. Can have value 1(Carrier) or 2(Service area). We don't consider this request for business segment sponsored networks.
	;  oa    - Coverage context info
	;  h3005 - Value of service area setting (HDF 3005)
	; RETURNS:      1 is the network is valid 0 otherwise
chknet(ID,spons,oa,h3005) n ret,sponBy,netSA,netPBS
	;;#strip# net inactive
	i $$zgetnp("NET",ID,5,1,99999)=1 q ""
	s sponBy=$$zgetnp("NET",ID,230,1,99999)    ;NET-230
	s netSA=$$zgetnp("NET",ID,160,1,99999)     ;NET-160
	s netPBS=$$getn("NET",ID,1,"165;1")        ;NET-165
	;;#strip# if bus seg spon then match bus seg on the network to the cvg's bus seg. Also service area is then not a must. But if present and the HDF3005 is set then it must match
	i sponBy=3 d  q ret
	. i netPBS'=oa("pbs") q
	. i h3005,(oa("SA")]""),(netSA]""),netSA'=oa("SA") q
	. s ret=1
	;;#strip# handle carrier and SA sponsored networks.
	i spons]"" d
	. ;;#strip# ;SA sponsored, must have a SA
	. i netSA="",spons=2 q
	. ;;#strip# Should have the requested sponsorship type
	. i sponBy'=spons q
	. ;;#strip# Carrier sponsored, must have a carrier
	. i spons=1,$$zgetnp("NET",ID,110,1,99999)="" q
	. s ret=1
	;;#strip# For carrier and SA sponsored networks if HDF3005 is on then SA must match
	i h3005,oa("SA")]"",netSA'=oa("SA") s ret=""
	q ret    ;;#eof#
	;---------
	; NAME:         isFOTrkOn
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Determine whether filing order history tracking is turned on
	;               by FO history tracking is on, we mean the value is > 0, currently either 1 (full) or 2 (lightweight)
	; RETURNS:      1 if filing order history tracking is turned on, 0 otherwise
	;---------
	;*sj+1 2/16 407162
isFOTrkOn() q $$getHDF(1135)>0  ;;#eof#  ;;#inline#
	;---------
	; NAME:         MCgetCvgSubLn
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Return the member line on a coverage associated with the coverage's subscriber
	; PARAMETERS:
	;  cvgId (I,REQ) - the coverage ID
	; RETURNS:      The subscriber line number of CVG related group 300, or "" if the coverage has no subscriber
	;---------
	;*imhh 1/16 402687 Create function
MCgetCvgSubLn(cvgId) n ln,subLn
	f ln=1:1:$$geti("CVG",cvgId,305,0) d  q:subLn'=""
	. s:$$geti("CVG",cvgId,305,ln)="01" subLn=ln
	q subLn ;;#eof#
	q  ;;#eor#
	;Routine accessed by Timothy A Coffman