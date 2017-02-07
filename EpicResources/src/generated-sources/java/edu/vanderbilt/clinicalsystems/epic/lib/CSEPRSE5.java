
package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.VariableContext;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.lib.Builtin;
import edu.vanderbilt.clinicalsystems.m.core.lib.HaltCondition;
import edu.vanderbilt.clinicalsystems.m.core.lib.Reflect;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;

public class CSEPRSE5 {

    @InjectRoutine
    public Value nID;
    @InjectRoutine
    public Value empID;
    @InjectRoutine
    public Value $;
    @InjectRoutine
    public Value twr;
    @InjectRoutine
    public Value eanID;
    @InjectRoutine
    public boolean altSrch;
    @InjectRoutine
    public Value ovrd;
    @InjectRoutine
    public Value tempRefElmt1;
    @InjectRoutine
    public Value netINI;
    @InjectRoutine
    public Value idTyp;
    @InjectRoutine
    public Value itm;
    @InjectRoutine
    public Value zER;
    @InjectRoutine
    public Value idc;
    @InjectRoutine
    public Value iDI;
    @InjectRoutine
    public Value errDetails;
    @InjectRoutine
    public String wt;
    @InjectRoutine
    public Value tempRef1;
    @InjectRoutine
    public Value uniqHTMP;
    @InjectRoutine
    public Value inBuf;
    @InjectRoutine
    public Value needsAuditing;
    @InjectRoutine
    public Value hSTEMPI;
    @InjectRoutine
    public Value errCode;
    @InjectRoutine
    public Value event;
    @InjectRoutine
    public long isSuccess;
    @InjectRoutine
    public double cnt;
    @InjectRoutine
    public Value citm;
    @InjectRoutine
    public Value infoArray;
    @InjectRoutine
    public Value eD0;
    @InjectRoutine
    public Value patients;
    @InjectRoutine
    public Value uniq;
    @InjectRoutine
    public Value hTMP;
    @InjectRoutine
    public Value $EA;
    @InjectRoutine
    public Value iNI;
    @InjectRoutine
    public Value exclfunc;
    @InjectRoutine
    public Value nameComponents;

    /**
     * CSEPRSE5; ; ;2016-10-04 09:14:07;8.3;10mQNw+5N7TqMX2vgkCpFqljtmdG6VqOL0l0uwQ6G4w=
     * ;;#lib#HULIB/CSILIB/CSULIB/WPLIB
     * ; Copyright (C) 2009-2016 Epic Systems Corporation
     * ;*********************************************************************
     * ; TITLE:   CSEPRSE5
     * ; PURPOSE: Community Model Patient Lookup
     * ;          Called By: Epic.EMPI.PatientLookup (E4C command)
     * ; AUTHOR:  mpatel  (business logic taken from CSEPRSE3)
     * ;
     * ; REVISION HISTORY:
     * ;     *MAP            151706 created (Copied from CSEPRSE3 and modified as needed)
     * ;     *MAP            158005 replace zgetCat by zgetCatC and minor change (remove a bug)
     * ;     *apz            234435 use separate node for HTMP
     * ;     *apz            T2856  use new dup check APIs
     * ;     *lirwin         246990 fix PtSearch when more than one key value is given
     * ;     *mjp            247610 fix incorrect DOB translation in 2009 version of PatientLookup
     * ;     *rscott         251232 allow certain I18N names pieces to be used for matching
     * ;     *wsun           264707 Format SSN before using to search for duplicate patients in PtLkUpMyCCore
     * ;     *ywu            276951 add address info, phone numbers and email for patient search
     * ;     *ywu            281328 fix blank line issue and add more comments
     * ;     *lbannist 02/15 352037 Remove Identity Foreign license check from PatLkUpExternal
     * ;     *lrobinso/*jdodson 3/15 356862 allow appointment reminder to go to email collected during OS workflow
     * ;     *dkoch    10/15 387491 AddPatientExternal: return the ID type set in IIT record 0, rather than always returning "EPI"
     * ;     *eshaw    12/15 393156 Update external web service auditing for consistency
     * ;     *rscott   02/16 415730 Add event logging for patient lookup
     * ;     *jboehr   08/16 438858 Eliminate Cache errors for external services
     * ;********************************************************************
     * Q 
     * 
     */
    public void main() {
        return ;
    }

    /**
     * PatLkUpExternal;
     * N empID,event,errCode,errDetails,needsAuditing,validationSkip,isSuccess;
     * S event="WP_PATIENT_LOOKUP";E1M 32830
     * S needsAuditing=1;
     * S validationSkip("MYCHART_LICENSE")=1
     * S validationSkip("SRO")=1
     * S isSuccess=$$isValidExternalRequest^WPEXT(event,.errCode,.errDetails,.empID,.validationSkip)
     * I 'isSuccess G pluDone();
     * G PatLkUpMyCCore()
     * 
     */
    public Value patLkUpExternal() {
        Value empID = Value.nullValue();
        Value event = Value.nullValue();
        Value errCode = Value.nullValue();
        Value errDetails = Value.nullValue();
        long needsAuditing = 0;
        Value validationSkip = Value.nullValue();
        Value isSuccess = Value.nullValue();
        event.assign("WP_PATIENT_LOOKUP");
        event = "WP_PATIENT_LOOKUP";
        needsAuditing = 1;
        validationSkip.get("MYCHART_LICENSE").assign(1);
        validationSkip = 1;
        validationSkip.get("SRO").assign(1);
        validationSkip = 1;
        isSuccess.assign(WPEXT.isValidExternalRequest(event, errCode, errDetails, empID, validationSkip));
        isSuccess = WPEXT.isValidExternalRequest(event, errCode, errDetails, empID, validationSkip);
        if (!isSuccess) {
            try {
                pluDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            patLkUpMyCCore();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * PatLkUpMyChart;
     * N needsAuditing
     * S needsAuditing=0;WARNING: This tag returns values to the called and is also called by the
     * ;  EXTERNAL versions of patient lookup. If you make changes to the _structure_
     * ;  of the return for internal use, this needs to be refactored to not share
     * ;  identical logic when it comes to returning information to the caller.
     * G PatLkUpMyCCore()
     * 
     */
    public Value patLkUpMyChart() {
        long needsAuditing = 0;
        needsAuditing = 0;
        try {
            patLkUpMyCCore();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * PatLkUpMyCCore;
     * N srchIDTy,srchID,val,idTyp,INI,uniq,itm,ovrd,netINI,ID,SSN,idc,exclfunc,wt,done,ZER,IDI,cnt,twr,citm,uniqHTMP,realCnt,phoneType,phoneNumber,LineCount,phone,addr,returnEptID,nameObj,namestr,isSuccess,infoArray,email;
     * S uniq=$$zGetESUn("^HSTEMPI");get a unique node in the global ^HSTEMPI
     * I uniq="" S errCode="INTERNAL-ERROR",errDetails="uniq empty" G pluDone()
     * S INI="EPT",itm=""
     * D %zidrule("EPT");s altSrch=##zECFGet("SearchType")  ;*lirwin 239997 External version does not allow search type control
     * S val=$$zECFGet("RegionRestriction")
     * I val]"" S ovrd(5635,1)=val
     * S idTyp=$$zECFGet("IDTypeMnemonic");
     * S val=""; *rscott+5 251232 allow certain I18N names pieces to be used for matching
     * S nameObj=$$zECFGet("NameComponents")
     * I nameObj'="" D 
     *   S namestr=$$getNameStringECF(nameObj);Collection of discrete name pieces.
     *   S val=$$FormatName^HUNAME("EPT","","",1,"","",namestr,"")
     * E  D 
     *   S val=$$zECFGet("Name");Already-formatted name string.
     * ;
     * I val]"" S itm=".2" D SaveInput(val);
     * S srchIDTy=$$zECFGet("PatientIDType")
     * S srchID=$$zECFGet("PatientID")
     * I srchIDTy']"" S ovrd("ID",1)=srchID
     * S SSN=$$zECFGet("SSN")
     * I SSN'="" D 
     *   S %=$$zValidateSSN(SSN,.SSN)
     *   S ovrd(160,1)=SSN
     * S val=$$zECFGet("DOB")
     * I val]"" S itm=110 D SaveInput(val)
     * S val=$$zECFGet("Gender")
     * I val]"" S itm=130 D SaveInput(val);get address info  *ywu++ 276951
     * S addr=$$zECFGet("Demographics")
     * S LineCount=$$zECFNumElmts("Street",addr)
     * I LineCount>0 D 
     *   F cnt=1:1:LineCount D 
     *   S ovrd(50,0)=realCnt
     *   S val=$$zECFGetElmt("Street",addr,cnt) I val'="" S realCnt=realCnt+1,ovrd(50,realCnt)=val
     * S val=$$zECFGet("HouseNumber",addr)
     * I val'="" S itm=82 D SaveInput(val)
     * S val=$$zECFGet("City",addr)
     * I val'="" S itm=60 D SaveInput(val)
     * S val=$$zECFGet("District",addr)
     * I val'="" S itm=83 D SaveInput(val)
     * S val=$$zECFGet("State",addr)
     * I val'="" S itm=70 D SaveInput(val)
     * S val=$$zECFGet("PostalCode",addr)
     * I val'="" S itm=80 D SaveInput(val)
     * S val=$$zECFGet("County",addr)
     * I val'="" S itm=75 D SaveInput(val)
     * S val=$$zECFGet("Country",addr)
     * I val'="" S itm=78 D SaveInput(val)
     * S val=$$zECFGet("Email",addr)
     * I val'="" D 
     *   S itm=85
     *   D SaveInput(val)
     *   S itm=86
     *   D SaveInput(val)
     *   S ovrd(86,0)=1
     *   S email=val;*lrobinso/*jdodson 356862 need to cache email entered by patient
     * S LineCount=$$zECFNumElmts("Phones",addr)
     * I LineCount>0 D 
     *   S realCnt=0
     *   F cnt=1:1:LineCount D 
     *   S ovrd(94,0)=realCnt
     *   S ovrd(98,0)=realCnt
     *   S phone=$$zECFGetElmt("Phones",addr,cnt) S phoneType=$$zECFGet("Type",phone) S phoneNumber=$$zECFGet("Number",phone) S phoneNumber=$$chkPhone^EAPHONE(phoneNumber) Q:phoneNumber=""  S realCnt=realCnt+1 S ovrd(94,realCnt)=phoneType S ovrd(98,realCnt)=phoneNumber I phoneType=8 S itm=5170 D SaveInput(phoneNumber) I phoneType=7 S itm=90 D SaveInput(phoneNumber)
     * ;
     * ;s patientElmt=##zECFNew("LookupPatientResult",,"S")  ;*lirwin External version returns at most one patient, so just put it in the main response object
     * I srchIDTy]"" D  I ID]"" S %=$$addPatientExternal(ID) G pluDone()
     *   I srchIDTy="CID" S ID=$$zCIDgtID("EPT",srchID)
     *   E  D 
     *   S srchIDTy=$$znxIxID("IIT",600,srchIDTy,"");get ID Type from desc
     *   I srchIDTy]"" S ID=$$zDnxID("EPT",2060,srchIDTy,2061,srchID)
     * ; *MAP 158005 - use $$ instead of 'd' while calling a function ;if an ID and ID Type are passed in then return info without a search
     * ;
     * ;  ## Run the search
     * N currID;i altSrch d fndSrch q  ;*lirwin 239997 External version does not allow search type control
     * S idc=$$getWPIDCOvr()
     * S:idc="" idc=$$verIDC^HUDUPUTL("","","",INI);
     * ;!!!! *lirwin Per emails with EMPI, this is typo. making id->"" (which is what 'id' would be here) to make HXPARSE happy
     * S %=$$DatatoAll^IDUPPROC(idc,"","",.uniqHTMP,.ovrd);*mjp+1 243372 use new API
     * ;s %=##mpiDups(INI,"",idc,.uniqHTMP,.ovrd,.err)    ;we should remove the dup checker?  ;*apz++ 234435 use separate node for HTMP
     * I uniqHTMP="" S uniqHTMP=%EA("MPI","EXTMPI","UNIQ")
     * I uniqHTMP="" S errCode="INTERNAL-ERROR",errDetails="uniqHTMP empty" G pluDone()
     * S %EA("H","SA","ALL")=1;need to setup so user has access to all SAs
     * S exclfunc=""
     * S exclfunc=^ED0("EPT","FindID","EXF");
     * S cnt=0
     * F  S wt=$O(^HTMP(uniqHTMP,idc,wt)) Q:wt=""  D  Q:done 
     *   F  S currID=$O(^HTMP(uniqHTMP,idc,wt,currID)) Q:currID=""  D  Q:done 
     *   I exclfunc'="" S IDI=currID,ZER="" X exclfunc K IDI I ZER'="" S ZER="" Q  S cnt=cnt+1 I cnt>1 S done=1 Q ;*lirwin 239997 return at most one result
     *   S ^HSTEMPI(uniq,"A",cnt)=$$zGtCID(INI,currID,"","",.citm,.twr)_"|"_$$znam(INI,currID)
     *   S returnEptID=currID
     * ;*mjp+1 243372 second subscript is now IDC ID
     * ;
     * D CleanupCompare^IDUPPROC(uniqHTMP);*mjp 243372 new cleanup call. Should have had a k ^HTMP(uniqHTMP) before...
     * ;
     * ;*lirwin 239997 return at most one patient
     * I cnt=1 D  I 1
     *   S %=$$addPatientExternal(returnEptID);*lrobinso/*jdodson 356862 need to cache email entered by patient
     *   I email'="",errCode="" D ;yes, error code shout not be set, but just in case someone misses a GOTO above
     *   S %=$$logPatient("WPAUD_OS_PT_LOOKUP_BY_DEMOG",$$getidout(returnEptID,"EPT"));*rscott 02/16 415730 Add event logging for patient lookup
     * ;wondering about *? workaround, see [2968120] MYCHART / CACHING / ALLOW FOR EMPTY EPT OR WPR BUT NOT BOTH
     *   ;also read note about * usage in EmailApptReminder^WPOPNSCH2
     *   S %=$$setWCACHE("*",returnEptID,"OSemail",email)
     * E  I cnt>1 S errCode="NO-UNIQUE-RESULT",errDetails="Multiple matching patients detected"
     * E  S errCode="NO-UNIQUE-RESULT",errDetails="No matching patients detected";
     * pluDone;
     * I errCode'="" S isSuccess=0
     * E  S isSuccess=1
     * I needsAuditing=1 D  I 1
     *   S infoArray("PTCOUNT")=cnt
     *   D auditEventUsage^WPEXT(1,isSuccess,errCode,empID,event,.infoArray)
     * ;
     * I errCode'="" S %=$$zECFThrow(errCode,errDetails,1);
     * Q 
     * 
     */
    public void patLkUpMyCCore() {
        Value srchIDTy = Value.nullValue();
        Value srchID = Value.nullValue();
        Value val = Value.nullValue();
        Value idTyp = Value.nullValue();
        Value iNI = Value.nullValue();
        Value uniq = Value.nullValue();
        Value itm = Value.nullValue();
        Value ovrd = Value.nullValue();
        Value netINI = Value.nullValue();
        Value iD = Value.nullValue();
        Value sSN = Value.nullValue();
        Value idc = Value.nullValue();
        Value exclfunc = Value.nullValue();
        String wt = "";
        long done = 0;
        Value zER = Value.nullValue();
        Value iDI = Value.nullValue();
        Value cnt = Value.nullValue();
        Value twr = Value.nullValue();
        Value citm = Value.nullValue();
        Value uniqHTMP = Value.nullValue();
        double realCnt = 0.0D;
        Value phoneType = Value.nullValue();
        Value phoneNumber = Value.nullValue();
        Value lineCount = Value.nullValue();
        Value phone = Value.nullValue();
        Value addr = Value.nullValue();
        Value returnEptID = Value.nullValue();
        Value nameObj = Value.nullValue();
        Value namestr = Value.nullValue();
        long isSuccess = 0;
        Value infoArray = Value.nullValue();
        Value email = Value.nullValue();
        uniq.assign(zGetESUn("^HSTEMPI"));
        uniq = zGetESUn("^HSTEMPI");
        if (uniq == Value.nullValue()) {
            errCode.assign("INTERNAL-ERROR");
            errCode = "INTERNAL-ERROR";
            errDetails.assign("uniq empty");
            errDetails = "uniq empty";
            try {
                pluDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        iNI.assign("EPT");
        iNI = "EPT";
        itm.assign(Value.nullValue());
        itm = Value.nullValue();
        $zidrule("EPT");
        val.assign(zECFGet("RegionRestriction"));
        val = zECFGet("RegionRestriction");
        if (Text.follows(val.toString(), Value.nullValue().toString())) {
            ovrd.get("5635").get("1").assign(val);
            ovrd = val;
        }
        idTyp.assign(zECFGet("IDTypeMnemonic"));
        idTyp = zECFGet("IDTypeMnemonic");
        val.assign(Value.nullValue());
        val = Value.nullValue();
        nameObj.assign(zECFGet("NameComponents"));
        nameObj = zECFGet("NameComponents");
        if (nameObj!= Value.nullValue()) {
            do {
                namestr.assign(getNameStringECF(nameObj));
                namestr = getNameStringECF(nameObj);
                val.assign(HUNAME.formatName("EPT", Value.nullValue(), Value.nullValue(), 1, Value.nullValue(), Value.nullValue(), namestr, Value.nullValue()));
                val = HUNAME.formatName("EPT", Value.nullValue(), Value.nullValue(), 1, Value.nullValue(), Value.nullValue(), namestr, Value.nullValue());
            } while (false);
        } else {
            do {
                val.assign(zECFGet("Name"));
                val = zECFGet("Name");
            } while (false);
        }
        if (Text.follows(val.toString(), Value.nullValue().toString())) {
            itm.assign(".2");
            itm = ".2";
            saveInput(val);
        }
        srchIDTy.assign(zECFGet("PatientIDType"));
        srchIDTy = zECFGet("PatientIDType");
        srchID.assign(zECFGet("PatientID"));
        srchID = zECFGet("PatientID");
        if (Text.notFollows(srchIDTy.toString(), Value.nullValue().toString())) {
            ovrd.get("ID").get("1").assign(srchID);
            ovrd = srchID;
        }
        sSN.assign(zECFGet("SSN"));
        sSN = zECFGet("SSN");
        if (sSN!= Value.nullValue()) {
            do {
                $.assign(zValidateSSN(sSN, sSN));
                $ = zValidateSSN(sSN, sSN);
                ovrd.get("160").get("1").assign(sSN);
                ovrd = sSN;
            } while (false);
        }
        val.assign(zECFGet("DOB"));
        val = zECFGet("DOB");
        if (Text.follows(val.toString(), Value.nullValue().toString())) {
            itm.assign(110);
            itm = 110;
            saveInput(val);
        }
        val.assign(zECFGet("Gender"));
        val = zECFGet("Gender");
        if (Text.follows(val.toString(), Value.nullValue().toString())) {
            itm.assign(130);
            itm = 130;
            saveInput(val);
        }
        addr.assign(zECFGet("Demographics"));
        addr = zECFGet("Demographics");
        lineCount.assign(zECFNumElmts("Street", addr));
        lineCount = zECFNumElmts("Street", addr);
        if (lineCount > 0) {
            do {
                for (Value cnt = 1; (cnt >= lineCount); cnt += 1) {
                    do {
                        val.assign(zECFGetElmt("Street", addr, cnt));
                        val = zECFGetElmt("Street", addr, cnt);
                        if (val!= Value.nullValue()) {
                            realCnt = (realCnt + 1);
                            ovrd.get("50").get(realCnt).assign(val);
                            ovrd = val;
                        }
                    } while (false);
                }
                ovrd.get("50").get("0").assign(realCnt);
                ovrd = realCnt;
            } while (false);
        }
        val.assign(zECFGet("HouseNumber", addr));
        val = zECFGet("HouseNumber", addr);
        if (val!= Value.nullValue()) {
            itm.assign(82);
            itm = 82;
            saveInput(val);
        }
        val.assign(zECFGet("City", addr));
        val = zECFGet("City", addr);
        if (val!= Value.nullValue()) {
            itm.assign(60);
            itm = 60;
            saveInput(val);
        }
        val.assign(zECFGet("District", addr));
        val = zECFGet("District", addr);
        if (val!= Value.nullValue()) {
            itm.assign(83);
            itm = 83;
            saveInput(val);
        }
        val.assign(zECFGet("State", addr));
        val = zECFGet("State", addr);
        if (val!= Value.nullValue()) {
            itm.assign(70);
            itm = 70;
            saveInput(val);
        }
        val.assign(zECFGet("PostalCode", addr));
        val = zECFGet("PostalCode", addr);
        if (val!= Value.nullValue()) {
            itm.assign(80);
            itm = 80;
            saveInput(val);
        }
        val.assign(zECFGet("County", addr));
        val = zECFGet("County", addr);
        if (val!= Value.nullValue()) {
            itm.assign(75);
            itm = 75;
            saveInput(val);
        }
        val.assign(zECFGet("Country", addr));
        val = zECFGet("Country", addr);
        if (val!= Value.nullValue()) {
            itm.assign(78);
            itm = 78;
            saveInput(val);
        }
        val.assign(zECFGet("Email", addr));
        val = zECFGet("Email", addr);
        if (val!= Value.nullValue()) {
            do {
                itm.assign(85);
                itm = 85;
                saveInput(val);
                itm.assign(86);
                itm = 86;
                saveInput(val);
                ovrd.get("86").get("0").assign(1);
                ovrd = 1;
                email.assign(val);
                email = val;
            } while (false);
        }
        lineCount.assign(zECFNumElmts("Phones", addr));
        lineCount = zECFNumElmts("Phones", addr);
        if (lineCount > 0) {
            do {
                realCnt.assign(0);
                realCnt = 0;
                for (Value cnt = 1; (cnt >= lineCount); cnt += 1) {
                    do {
                        phone.assign(zECFGetElmt("Phones", addr, cnt));
                        phone = zECFGetElmt("Phones", addr, cnt);
                        phoneType.assign(zECFGet("Type", phone));
                        phoneType = zECFGet("Type", phone);
                        phoneNumber.assign(zECFGet("Number", phone));
                        phoneNumber = zECFGet("Number", phone);
                        phoneNumber.assign(EAPHONE.chkPhone(phoneNumber));
                        phoneNumber = EAPHONE.chkPhone(phoneNumber);
                        return ;
                        if (phoneNumber == Value.nullValue()) {
                            return ;
                        }
                        realCnt.assign((realCnt + 1));
                        realCnt = (realCnt + 1);
                        ovrd.get("94").get(realCnt).assign(phoneType);
                        ovrd = phoneType;
                        ovrd.get("98").get(realCnt).assign(phoneNumber);
                        ovrd = phoneNumber;
                        if (phoneType == 8) {
                            itm.assign(5170);
                            itm = 5170;
                            saveInput(phoneNumber);
                        }
                        if (phoneType == 7) {
                            itm.assign(90);
                            itm = 90;
                            saveInput(phoneNumber);
                        }
                    } while (false);
                }
                ovrd.get("94").get("0").assign(realCnt);
                ovrd = realCnt;
                ovrd.get("98").get("0").assign(realCnt);
                ovrd = realCnt;
            } while (false);
        }
        if (Text.follows(srchIDTy.toString(), Value.nullValue().toString())) {
            do {
                if (srchIDTy == "CID") {
                    iD.assign(zCIDgtID("EPT", srchID));
                    iD = zCIDgtID("EPT", srchID);
                } else {
                    do {
                        srchIDTy.assign(znxIxID("IIT", 600, srchIDTy, Value.nullValue()));
                        srchIDTy = znxIxID("IIT", 600, srchIDTy, Value.nullValue());
                        if (Text.follows(srchIDTy.toString(), Value.nullValue().toString())) {
                            iD.assign(zDnxID("EPT", 2060, srchIDTy, 2061, srchID));
                            iD = zDnxID("EPT", 2060, srchIDTy, 2061, srchID);
                        }
                    } while (false);
                }
            } while (false);
            if (Text.follows(iD.toString(), Value.nullValue().toString())) {
                $.assign(addPatientExternal(iD));
                $ = addPatientExternal(iD);
                try {
                    pluDone();
                } finally {
                    throw new HaltCondition("return from GOTO");
                }
            }
        }
        Value currID = Value.nullValue();
        idc.assign(getWPIDCOvr());
        idc = getWPIDCOvr();
        idc.assign(HUDUPUTL.verIDC(Value.nullValue(), Value.nullValue(), Value.nullValue(), iNI));
        idc = HUDUPUTL.verIDC(Value.nullValue(), Value.nullValue(), Value.nullValue(), iNI);
        if (idc == Value.nullValue()) {
            idc.assign(HUDUPUTL.verIDC(Value.nullValue(), Value.nullValue(), Value.nullValue(), iNI));
            idc = HUDUPUTL.verIDC(Value.nullValue(), Value.nullValue(), Value.nullValue(), iNI);
        }
        $.assign(IDUPPROC.datatoAll(idc, Value.nullValue(), Value.nullValue(), uniqHTMP, ovrd));
        $ = IDUPPROC.datatoAll(idc, Value.nullValue(), Value.nullValue(), uniqHTMP, ovrd);
        if (uniqHTMP == Value.nullValue()) {
            uniqHTMP.assign($EA.get("MPI").get("EXTMPI").get("UNIQ"));
            uniqHTMP = $EA.get("MPI").get("EXTMPI").get("UNIQ");
        }
        if (uniqHTMP == Value.nullValue()) {
            errCode.assign("INTERNAL-ERROR");
            errCode = "INTERNAL-ERROR";
            errDetails.assign("uniqHTMP empty");
            errDetails = "uniqHTMP empty";
            try {
                pluDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $EA.get("H").get("SA").get("ALL").assign(1);
        $EA = 1;
        exclfunc.assign(Value.nullValue());
        exclfunc = Value.nullValue();
        exclfunc.assign(eD0 .get("EPT").get("FindID").get("EXF"));
        exclfunc = eD0 .get("EPT").get("FindID").get("EXF");
        cnt.assign(0);
        cnt = 0;
        while (true) {
            wt = Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt));
            return ;
            if (wt == Value.nullValue()) {
                return ;
            }
            do {
                while (true) {
                    currID.assign(Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt.toString()).get(currID.toString())));
                    currID = Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt.toString()).get(currID.toString()));
                    return ;
                    if (currID == Value.nullValue()) {
                        return ;
                    }
                    do {
                        if (exclfunc!= Value.nullValue()) {
                            iDI.assign(currID);
                            iDI = currID;
                            zER.assign(Value.nullValue());
                            zER = Value.nullValue();
                            Reflect.inline(exclfunc.toString());
                            iDI.clear();
                            iDI = Value.nullValue();
                            if (zER!= Value.nullValue()) {
                                zER.assign(Value.nullValue());
                                zER = Value.nullValue();
                                return ;
                            }
                        }
                        cnt.assign((cnt + 1));
                        cnt = (cnt + 1);
                        if (cnt > 1) {
                            done = 1;
                            return ;
                        }
                        hSTEMPI.get(uniq).get("A").get(cnt).assign(((zGtCID(iNI, currID, Value.nullValue(), Value.nullValue(), citm, twr)+"|")+ znam(iNI, currID)));
                        hSTEMPI = ((zGtCID(iNI, currID, Value.nullValue(), Value.nullValue(), citm, twr)+"|")+ znam(iNI, currID));
                        returnEptID.assign(currID);
                        returnEptID = currID;
                    } while (false);
                    return ;
                    if (done) {
                        return ;
                    }
                }
            } while (false);
            return ;
            if (done) {
                return ;
            }
        }
        IDUPPROC.cleanupCompare(uniqHTMP);
        if (cnt == 1) {
            do {
                $.assign(addPatientExternal(returnEptID));
                $ = addPatientExternal(returnEptID);
                if (email!= Value.nullValue()) {
                    do {
                        $.assign(setWCACHE("*", returnEptID, "OSemail", email));
                        $ = setWCACHE("*", returnEptID, "OSemail", email);
                    } while (false);
                }
                $.assign(logPatient("WPAUD_OS_PT_LOOKUP_BY_DEMOG", getidout(returnEptID, "EPT")));
                $ = logPatient("WPAUD_OS_PT_LOOKUP_BY_DEMOG", getidout(returnEptID, "EPT"));
            } while (false);
        } else {
            if (cnt > 1) {
                errCode.assign("NO-UNIQUE-RESULT");
                errCode = "NO-UNIQUE-RESULT";
                errDetails.assign("Multiple matching patients detected");
                errDetails = "Multiple matching patients detected";
            }
            errCode.assign("NO-UNIQUE-RESULT");
            errCode = "NO-UNIQUE-RESULT";
            errDetails.assign("No matching patients detected");
            errDetails = "No matching patients detected";
        }
        pluDone:
        if (errCode!= Value.nullValue()) {
            isSuccess = 0;
        } else {
            isSuccess = 1;
        }
        if (needsAuditing == 1) {
            do {
                infoArray.get("PTCOUNT").assign(cnt);
                infoArray = cnt;
                WPEXT.auditEventUsage(1, isSuccess, errCode, empID, event, infoArray);
            } while (false);
        }
        if (errCode!= Value.nullValue()) {
            $.assign(zECFThrow(errCode, errDetails, 1));
            $ = zECFThrow(errCode, errDetails, 1);
        }
        return ;
    }

    /**
     * pluDone;
     * I errCode'="" S isSuccess=0
     * E  S isSuccess=1
     * I needsAuditing=1 D  I 1
     *   S infoArray("PTCOUNT")=cnt
     *   D auditEventUsage^WPEXT(1,isSuccess,errCode,empID,event,.infoArray)
     * ;
     * I errCode'="" S %=$$zECFThrow(errCode,errDetails,1);
     * Q 
     * 
     */
    public void pluDone() {
        if (errCode!= Value.nullValue()) {
            isSuccess = 0;
        } else {
            isSuccess = 1;
        }
        if (needsAuditing == 1) {
            do {
                infoArray.get("PTCOUNT").assign(cnt);
                infoArray = cnt;
                WPEXT.auditEventUsage(1, isSuccess, errCode, empID, event, infoArray);
            } while (false);
        }
        if (errCode!= Value.nullValue()) {
            $.assign(zECFThrow(errCode, errDetails, 1));
            $ = zECFThrow(errCode, errDetails, 1);
        }
        return ;
    }

    /**
     * getNameStringECF(nameObj
     * 
     */
    public Value getNameStringECF(final Value nameObj) {
        String namestr = "";
        Value lastName = Value.nullValue();
        Value spouseLastName = Value.nullValue();
        Value firstName = Value.nullValue();
        Value middleName = Value.nullValue();
        Value prefix = Value.nullValue();
        Value spousePrefix = Value.nullValue();
        Value title = Value.nullValue();
        Value suffix = Value.nullValue();
        Value academic = Value.nullValue();
        Value preferredName = Value.nullValue();
        Value preferredNameType = Value.nullValue();
        Value spouseLastNameFirst = Value.nullValue();
        Value givenNameInitials = Value.nullValue();
        String c5 = "";
        lastName.assign(zECFGet("LastName", nameObj));
        lastName = zECFGet("LastName", nameObj);
        spouseLastName.assign(zECFGet("LastNameFromSpouse", nameObj));
        spouseLastName = zECFGet("LastNameFromSpouse", nameObj);
        firstName.assign(zECFGet("FirstName", nameObj));
        firstName = zECFGet("FirstName", nameObj);
        middleName.assign(zECFGet("MiddleName", nameObj));
        middleName = zECFGet("MiddleName", nameObj);
        prefix.assign(zECFGet("LastNamePrefix", nameObj));
        prefix = zECFGet("LastNamePrefix", nameObj);
        spousePrefix.assign(zECFGet("SpouseLastNamePrefix", nameObj));
        spousePrefix = zECFGet("SpouseLastNamePrefix", nameObj);
        title.assign(Value.nullValue());
        title = Value.nullValue();
        suffix.assign(WPICUTIL.getSingleCatValue(zECFGet("Suffix", nameObj), "EAN", 1080));
        suffix = WPICUTIL.getSingleCatValue(zECFGet("Suffix", nameObj), "EAN", 1080);
        academic.assign(Value.nullValue());
        academic = Value.nullValue();
        preferredName.assign(Value.nullValue());
        preferredName = Value.nullValue();
        preferredNameType.assign(Value.nullValue());
        preferredNameType = Value.nullValue();
        spouseLastNameFirst.assign(WPICUTIL.getSingleCatValue(zECFGet("SpouseLastNameFirst", nameObj), "ECT", 101));
        spouseLastNameFirst = WPICUTIL.getSingleCatValue(zECFGet("SpouseLastNameFirst", nameObj), "ECT", 101);
        givenNameInitials.assign(zECFGet("GivenNameInitials", nameObj));
        givenNameInitials = zECFGet("GivenNameInitials", nameObj);
        c5 = Text.character(5);
        namestr = ((((((((lastName + c5)+ spouseLastName)+ c5)+ firstName)+ c5)+ middleName)+ c5)+ prefix);
        namestr = ((((((((((namestr + c5)+ spousePrefix)+ c5)+ title)+ c5)+ suffix)+ c5)+ academic)+ c5)+ preferredName);
        namestr = ((((((namestr + c5)+ preferredNameType)+ c5)+ spouseLastNameFirst)+ c5)+ givenNameInitials);
        return namestr;
    }

    /**
     * getWPIDCOvrN id IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value getWPIDCOvr() {
        Value id = Value.nullValue();
        id.assign(getMnemCode("WP_PT_SRCH_IDC"));
        id = getMnemCode("WP_PT_SRCH_IDC");
        if (id == Value.nullValue()) {
            return Value.nullValue();
        }
        if (Text.extract(id.toString(), 1, 4) == "cid.") {
            id.assign(zCIDgtID("IDC", Text.extract(id.toString(), 5, Text.length(id.toString()))));
            id = zCIDgtID("IDC", Text.extract(id.toString(), 5, Text.length(id.toString())));
        }
        if (!zisR("IDC", id)) {
            id.assign(Value.nullValue());
            id = Value.nullValue();
        }
        return id;
    }

    /**
     * PatLkUpN srchIDTy,srchID,val,idTyp,INI,uniq,altSrch,itm,ovrd,netINI,ID,patients,idc,exclfunc,wt,done,ZER,IDI,cnt,twr,citm,uniqHTMP;*apz 234435 +uniqHTMP
     * S uniq=$$zGetESUn("^HSTEMPI");get a unique node in the global ^HSTEMPI
     * S INI="EPT",itm=""
     * D %zidrule("EPT")
     * S altSrch=$$zECFGet("SearchType");get the search type: foundations search or identity search
     * S val=$$zECFGet("RegionRestriction")
     * I val]"" S ovrd(5635,1)=val
     * S idTyp=$$zECFGet("IDTypeMnemonic")
     * S val=""
     * S val=$$zECFGet("Name")
     * I val]"" S itm=".2" D SaveInput(val)
     * S srchIDTy=$$zECFGet("PatientIDType")
     * S srchID=$$zECFGet("PatientID")
     * I srchIDTy']"" S ovrd("ID",1)=srchID
     * S ovrd(160,1)=$$zECFGet("SSN")
     * S val=$$zECFGet("DOB");i val]"" s itm=110 d SaveInput(val)  ;*mjp+1 247610 Do not use SaveInput, since zECFGet already gives us the DTE
     * S:val'="" ovrd(110,1)=val
     * S val=$$zECFGet("Gender")
     * I val]"" S itm=130 D SaveInput(val)
     * S patients=$$zECFNew("Patients","A")
     * I srchIDTy]"" D  I ID]"" S %=$$addPatient(ID,patients) Q 
     *   I srchIDTy="CID" S ID=$$zCIDgtID("EPT",srchID)
     *   E  D 
     *   S srchIDTy=$$znxIxID("IIT",600,srchIDTy,"");get ID Type from desc
     *   I srchIDTy]"" S ID=$$zDnxID("EPT",2060,srchIDTy,2061,srchID)
     * ;if an ID and ID Type are passed in then return info without a search
     * ;
     * RunSrch;Run the search
     * N currID
     * I altSrch D fndSrch() Q ; foundations search
     * S idc=$$verIDC^HUDUPUTL("","","",INI)
     * S %=$$DatatoAll^IDUPPROC(idc,"",.uniqHTMP,.ovrd);$$ mpiDups(INI,id,idc,.uniqHTMP,.ovrd,.err)   ;*apz++ 234435 use separate node for HTMP  ; *apz T2856 use new dup check API
     * I uniqHTMP="" S uniqHTMP=%EA("MPI","EXTMPI","UNIQ")
     * S %EA("H","SA","ALL")=1;need to setup so user has access to all SAs
     * S exclfunc=""
     * S exclfunc=^ED0("EPT","FindID","EXF")
     * F  S wt=$O(^HTMP(uniqHTMP,idc,wt)) Q:wt=""  D  Q:done 
     *   F  S currID=$O(^HTMP(uniqHTMP,idc,wt,currID)) Q:currID=""  D  Q:done 
     *   I exclfunc'="" S IDI=currID,ZER="" X exclfunc K IDI I ZER'="" S ZER="" Q  S cnt=cnt+1 S ^HSTEMPI(uniq,"A",cnt)=$$zGtCID(INI,currID,"","",.citm,.twr)_"|"_$$znam(INI,currID) S %=$$addPatient(currID,patients)
     * ; *apz T2856 use idc subscript
     * ; *apz T2856 clean up HTMP results
     * D CleanupCompare^IDUPPROC(uniqHTMP)
     * Q 
     * 
     */
    public void patLkUp() {
        Value srchIDTy = Value.nullValue();
        Value srchID = Value.nullValue();
        Value val = Value.nullValue();
        Value idTyp = Value.nullValue();
        Value iNI = Value.nullValue();
        Value uniq = Value.nullValue();
        Value altSrch = Value.nullValue();
        Value itm = Value.nullValue();
        Value ovrd = Value.nullValue();
        Value netINI = Value.nullValue();
        Value iD = Value.nullValue();
        Value patients = Value.nullValue();
        Value idc = Value.nullValue();
        Value exclfunc = Value.nullValue();
        String wt = "";
        Value done = Value.nullValue();
        Value zER = Value.nullValue();
        Value iDI = Value.nullValue();
        double cnt = 0.0D;
        Value twr = Value.nullValue();
        Value citm = Value.nullValue();
        Value uniqHTMP = Value.nullValue();
        uniq.assign(zGetESUn("^HSTEMPI"));
        uniq = zGetESUn("^HSTEMPI");
        iNI.assign("EPT");
        iNI = "EPT";
        itm.assign(Value.nullValue());
        itm = Value.nullValue();
        $zidrule("EPT");
        altSrch.assign(zECFGet("SearchType"));
        altSrch = zECFGet("SearchType");
        val.assign(zECFGet("RegionRestriction"));
        val = zECFGet("RegionRestriction");
        if (Text.follows(val.toString(), Value.nullValue().toString())) {
            ovrd.get("5635").get("1").assign(val);
            ovrd = val;
        }
        idTyp.assign(zECFGet("IDTypeMnemonic"));
        idTyp = zECFGet("IDTypeMnemonic");
        val.assign(Value.nullValue());
        val = Value.nullValue();
        val.assign(zECFGet("Name"));
        val = zECFGet("Name");
        if (Text.follows(val.toString(), Value.nullValue().toString())) {
            itm.assign(".2");
            itm = ".2";
            saveInput(val);
        }
        srchIDTy.assign(zECFGet("PatientIDType"));
        srchIDTy = zECFGet("PatientIDType");
        srchID.assign(zECFGet("PatientID"));
        srchID = zECFGet("PatientID");
        if (Text.notFollows(srchIDTy.toString(), Value.nullValue().toString())) {
            ovrd.get("ID").get("1").assign(srchID);
            ovrd = srchID;
        }
        ovrd.get("160").get("1").assign(zECFGet("SSN"));
        ovrd = zECFGet("SSN");
        val.assign(zECFGet("DOB"));
        val = zECFGet("DOB");
        ovrd.get("110").get("1").assign(val);
        ovrd = val;
        if (val!= Value.nullValue()) {
            ovrd.get("110").get("1").assign(val);
            ovrd = val;
        }
        val.assign(zECFGet("Gender"));
        val = zECFGet("Gender");
        if (Text.follows(val.toString(), Value.nullValue().toString())) {
            itm.assign(130);
            itm = 130;
            saveInput(val);
        }
        patients.assign(zECFNew("Patients", "A"));
        patients = zECFNew("Patients", "A");
        if (Text.follows(srchIDTy.toString(), Value.nullValue().toString())) {
            do {
                if (srchIDTy == "CID") {
                    iD.assign(zCIDgtID("EPT", srchID));
                    iD = zCIDgtID("EPT", srchID);
                } else {
                    do {
                        srchIDTy.assign(znxIxID("IIT", 600, srchIDTy, Value.nullValue()));
                        srchIDTy = znxIxID("IIT", 600, srchIDTy, Value.nullValue());
                        if (Text.follows(srchIDTy.toString(), Value.nullValue().toString())) {
                            iD.assign(zDnxID("EPT", 2060, srchIDTy, 2061, srchID));
                            iD = zDnxID("EPT", 2060, srchIDTy, 2061, srchID);
                        }
                    } while (false);
                }
            } while (false);
            if (Text.follows(iD.toString(), Value.nullValue().toString())) {
                $.assign(addPatient(iD, patients));
                $ = addPatient(iD, patients);
                return ;
            }
        }
        runSrch:
        Value currID = Value.nullValue();
        if (altSrch) {
            fndSrch();
            return ;
        }
        idc.assign(HUDUPUTL.verIDC(Value.nullValue(), Value.nullValue(), Value.nullValue(), iNI));
        idc = HUDUPUTL.verIDC(Value.nullValue(), Value.nullValue(), Value.nullValue(), iNI);
        $.assign(IDUPPROC.datatoAll(idc, Value.nullValue(), uniqHTMP, ovrd));
        $ = IDUPPROC.datatoAll(idc, Value.nullValue(), uniqHTMP, ovrd);
        if (uniqHTMP == Value.nullValue()) {
            uniqHTMP.assign($EA.get("MPI").get("EXTMPI").get("UNIQ"));
            uniqHTMP = $EA.get("MPI").get("EXTMPI").get("UNIQ");
        }
        $EA.get("H").get("SA").get("ALL").assign(1);
        $EA = 1;
        exclfunc.assign(Value.nullValue());
        exclfunc = Value.nullValue();
        exclfunc.assign(eD0 .get("EPT").get("FindID").get("EXF"));
        exclfunc = eD0 .get("EPT").get("FindID").get("EXF");
        while (true) {
            wt = Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt));
            return ;
            if (wt == Value.nullValue()) {
                return ;
            }
            do {
                while (true) {
                    currID.assign(Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt.toString()).get(currID.toString())));
                    currID = Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt.toString()).get(currID.toString()));
                    return ;
                    if (currID == Value.nullValue()) {
                        return ;
                    }
                    do {
                        if (exclfunc!= Value.nullValue()) {
                            iDI.assign(currID);
                            iDI = currID;
                            zER.assign(Value.nullValue());
                            zER = Value.nullValue();
                            Reflect.inline(exclfunc.toString());
                            iDI.clear();
                            iDI = Value.nullValue();
                            if (zER!= Value.nullValue()) {
                                zER.assign(Value.nullValue());
                                zER = Value.nullValue();
                                return ;
                            }
                        }
                        cnt = (cnt + 1);
                        hSTEMPI.get(uniq).get("A").get(cnt).assign(((zGtCID(iNI, currID, Value.nullValue(), Value.nullValue(), citm, twr)+"|")+ znam(iNI, currID)));
                        hSTEMPI = ((zGtCID(iNI, currID, Value.nullValue(), Value.nullValue(), citm, twr)+"|")+ znam(iNI, currID));
                        $.assign(addPatient(currID, patients));
                        $ = addPatient(currID, patients);
                    } while (false);
                    return ;
                    if (done) {
                        return ;
                    }
                }
            } while (false);
            return ;
            if (done) {
                return ;
            }
        }
        IDUPPROC.cleanupCompare(uniqHTMP);
        return ;
    }

    /**
     * RunSrch;Run the search
     * N currID
     * I altSrch D fndSrch() Q ; foundations search
     * S idc=$$verIDC^HUDUPUTL("","","",INI)
     * S %=$$DatatoAll^IDUPPROC(idc,"",.uniqHTMP,.ovrd);$$ mpiDups(INI,id,idc,.uniqHTMP,.ovrd,.err)   ;*apz++ 234435 use separate node for HTMP  ; *apz T2856 use new dup check API
     * I uniqHTMP="" S uniqHTMP=%EA("MPI","EXTMPI","UNIQ")
     * S %EA("H","SA","ALL")=1;need to setup so user has access to all SAs
     * S exclfunc=""
     * S exclfunc=^ED0("EPT","FindID","EXF")
     * F  S wt=$O(^HTMP(uniqHTMP,idc,wt)) Q:wt=""  D  Q:done 
     *   F  S currID=$O(^HTMP(uniqHTMP,idc,wt,currID)) Q:currID=""  D  Q:done 
     *   I exclfunc'="" S IDI=currID,ZER="" X exclfunc K IDI I ZER'="" S ZER="" Q  S cnt=cnt+1 S ^HSTEMPI(uniq,"A",cnt)=$$zGtCID(INI,currID,"","",.citm,.twr)_"|"_$$znam(INI,currID) S %=$$addPatient(currID,patients)
     * ; *apz T2856 use idc subscript
     * ; *apz T2856 clean up HTMP results
     * D CleanupCompare^IDUPPROC(uniqHTMP)
     * Q 
     * 
     */
    public void runSrch() {
        Value currID = Value.nullValue();
        if (altSrch) {
            fndSrch();
            return ;
        }
        idc.assign(HUDUPUTL.verIDC(Value.nullValue(), Value.nullValue(), Value.nullValue(), iNI));
        idc = HUDUPUTL.verIDC(Value.nullValue(), Value.nullValue(), Value.nullValue(), iNI);
        $.assign(IDUPPROC.datatoAll(idc, Value.nullValue(), uniqHTMP, ovrd));
        $ = IDUPPROC.datatoAll(idc, Value.nullValue(), uniqHTMP, ovrd);
        if (uniqHTMP == Value.nullValue()) {
            uniqHTMP.assign($EA.get("MPI").get("EXTMPI").get("UNIQ"));
            uniqHTMP = $EA.get("MPI").get("EXTMPI").get("UNIQ");
        }
        $EA.get("H").get("SA").get("ALL").assign(1);
        $EA = 1;
        exclfunc.assign(Value.nullValue());
        exclfunc = Value.nullValue();
        exclfunc.assign(eD0 .get("EPT").get("FindID").get("EXF"));
        exclfunc = eD0 .get("EPT").get("FindID").get("EXF");
        while (true) {
            wt = Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt));
            return ;
            if (wt == Value.nullValue()) {
                return ;
            }
            do {
                while (true) {
                    currID.assign(Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt.toString()).get(currID.toString())));
                    currID = Builtin.followingKey(hTMP.get(uniqHTMP.toString()).get(idc.toString()).get(wt.toString()).get(currID.toString()));
                    return ;
                    if (currID == Value.nullValue()) {
                        return ;
                    }
                    do {
                        if (exclfunc!= Value.nullValue()) {
                            iDI.assign(currID);
                            iDI = currID;
                            zER.assign(Value.nullValue());
                            zER = Value.nullValue();
                            Reflect.inline(exclfunc.toString());
                            iDI.clear();
                            iDI = Value.nullValue();
                            if (zER!= Value.nullValue()) {
                                zER.assign(Value.nullValue());
                                zER = Value.nullValue();
                                return ;
                            }
                        }
                        cnt.assign((cnt + 1));
                        cnt = (cnt + 1);
                        hSTEMPI.get(uniq).get("A").get(cnt).assign(((zGtCID(iNI, currID, Value.nullValue(), Value.nullValue(), citm, twr)+"|")+ znam(iNI, currID)));
                        hSTEMPI = ((zGtCID(iNI, currID, Value.nullValue(), Value.nullValue(), citm, twr)+"|")+ znam(iNI, currID));
                        $.assign(addPatient(currID, patients));
                        $ = addPatient(currID, patients);
                    } while (false);
                    return ;
                    if (done) {
                        return ;
                    }
                }
            } while (false);
            return ;
            if (done) {
                return ;
            }
        }
        IDUPPROC.cleanupCompare(uniqHTMP);
        return ;
    }

    /**
     * fndSrchN name,ws,sex,dob,ssn,sa,INI,currID,i,string,entry,nodeNum,id,return,excl,resume,tmpglob,rnum,recmax IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public void fndSrch() {
        Value name = Value.nullValue();
        long ws = 0;
        Value sex = Value.nullValue();
        Value dob = Value.nullValue();
        Value ssn = Value.nullValue();
        Value sa = Value.nullValue();
        String iNI = "";
        Value currID = Value.nullValue();
        String i = "";
        String string = "";
        String entry = "";
        String nodeNum = "";
        Value id = Value.nullValue();
        Value _return_ = Value.nullValue();
        Value excl = Value.nullValue();
        Value resume = Value.nullValue();
        Value tmpglob = Value.nullValue();
        double rnum = 0.0D;
        double recmax = 0.0D;
        ws = 1;
        name.assign(ovrd.get(".2").get("1"));
        name = ovrd.get(".2").get("1");
        ovrd.get(".2").get("1").clear();
        sex.assign(ovrd.get("130").get("1"));
        sex = ovrd.get("130").get("1");
        ovrd.get("130").get("1").clear();
        dob.assign(ovrd.get("110").get("1"));
        dob = ovrd.get("110").get("1");
        ovrd.get("110").get("1").clear();
        ssn.assign(ovrd.get("160").get("1"));
        ssn = ovrd.get("160").get("1");
        ovrd.get("160").get("1").clear();
        id.assign(ovrd.get("ID").get("1"));
        id = ovrd.get("ID").get("1");
        ovrd.get("ID").get("1").clear();
        sa.assign(zCIDgtID("EAF", ovrd.get("5635").get("1")));
        sa = zCIDgtID("EAF", ovrd.get("5635").get("1"));
        ovrd.get("5635").get("1").clear();
        inBuf.get("Sender").assign(ws);
        inBuf = ws;
        excl.assign(CSEPRSE4 .mkExcl(ovrd));
        excl = CSEPRSE4 .mkExcl(ovrd);
        _return_.assign(CSEPRSE1 .findPats(id, name, sex, dob, ssn, sa, resume, excl));
        _return_ = CSEPRSE1 .findPats(id, name, sex, dob, ssn, sa, resume, excl);
        return ;
        if (_return_ == 0) {
            return ;
        }
        nodeNum = Text.piece(_return_.toString(), "^", 2);
        uniq.assign(Text.piece(_return_.toString(), "^", 1));
        uniq = Text.piece(_return_.toString(), "^", 1);
        tmpglob.assign(HUWFID.getRecSrchGlob(uniq));
        tmpglob = HUWFID.getRecSrchGlob(uniq);
        return ;
        if (tmpglob == Value.nullValue()) {
            return ;
        }
        iNI = "EPT";
        for (String i = 1; (i >= nodeNum); i += 1) {
            do {
                string = (string + VariableContext.lookup(tmpglob.toString()).get(i));
                recmax = Text.occurrencesPlusOne(string, "^");
                for (double rnum = 1; (rnum >= ((i<nodeNum)?(recmax- 1):(true?recmax:Value.nullValue()))); rnum += 1) {
                    do {
                        entry = Text.piece(string, "^", rnum);
                        return ;
                        if (entry == Value.nullValue()) {
                            return ;
                        }
                        currID.assign(Text.piece(entry.toString(), "|", 1));
                        currID = Text.piece(entry.toString(), "|", 1);
                        return ;
                        if (currID == Value.nullValue()) {
                            return ;
                        }
                        $.assign(addPatient(currID, patients));
                        $ = addPatient(currID, patients);
                    } while (false);
                }
                if (i<nodeNum) {
                    string = Text.piece(string, "^", recmax);
                }
            } while (false);
        }
        ZefnLib.releasePrivateTempGlobal(tmpglob.toString());
        return ;
    }

    /**
     * SaveInput(val
     * 
     */
    public void saveInput(final Value val) {
        Value twItm = Value.nullValue();
        Value catVal = Value.nullValue();
        if (itm == Value.nullValue()) {
            return ;
        }
        twItm.assign(zgetTWR("EPT", itm));
        twItm = zgetTWR("EPT", itm);
        val.assign((CSEPRSE4 .xml2instKpDt(val)/ 86400));
        val = (CSEPRSE4 .xml2instKpDt(val)/ 86400);
        if (Text.piece(twItm.toString(), ",", 1) == "D") {
            val.assign((CSEPRSE4 .xml2instKpDt(val)/ 86400));
            val = (CSEPRSE4 .xml2instKpDt(val)/ 86400);
        }
        if (Text.piece(twItm.toString(), ",", 1) == "C") {
            do {
                return ;
                if (zisCat(val, "EPT", itm) == 1) {
                    return ;
                }
                catVal.assign(val);
                catVal = val;
                val.assign(zgetCatN(catVal, 1, "EPT", itm));
                val = zgetCatN(catVal, 1, "EPT", itm);
                val.assign(zgetCatN(catVal, 2, "EPT", itm));
                val = zgetCatN(catVal, 2, "EPT", itm);
                if (val == Value.nullValue()) {
                    val.assign(zgetCatN(catVal, 2, "EPT", itm));
                    val = zgetCatN(catVal, 2, "EPT", itm);
                }
            } while (false);
        }
        netINI.assign(Text.piece(Text.piece(twItm.toString(), ",", 14), ";", 2));
        netINI = Text.piece(Text.piece(twItm.toString(), ",", 14), ";", 2);
        if (Text.follows(netINI, Value.nullValue().toString())) {
            val.assign(zCIDgtID(netINI, val));
            val = zCIDgtID(netINI, val);
        }
        val.assign(getidout(val, netINI));
        val = getidout(val, netINI);
        ovrd.get(itm).get("1").assign(val);
        ovrd = val;
        itm.assign(Value.nullValue());
        itm = Value.nullValue();
        return ;
    }

    /**
     * addPatient(ID,patients
     * 
     */
    public Value addPatient(final Value iD, final Value patients) {
        Value patElmt = Value.nullValue();
        Value tempRef = Value.nullValue();
        Value tempRefElmt = Value.nullValue();
        Value pcp = Value.nullValue();
        Value pcpArray = Value.nullValue();
        Value lOwner = Value.nullValue();
        Value iit = Value.nullValue();
        Value temp = Value.nullValue();
        Value twr11 = Value.nullValue();
        Value twr90 = Value.nullValue();
        Value twr5170 = Value.nullValue();
        Value twr5435 = Value.nullValue();
        Value twr5100 = Value.nullValue();
        Value twr775 = Value.nullValue();
        Value twr750 = Value.nullValue();
        Value twr760 = Value.nullValue();
        patElmt.assign(zECFNewElmtObj(patients));
        patElmt = zECFNewElmtObj(patients);
        $.assign(zECFSet("Name", HUNETNM.nameOut("EPT", iD), patElmt));
        $ = zECFSet("Name", HUNETNM.nameOut("EPT", iD), patElmt);
        tempRef.assign(zECFNew("IDs", patElmt, "A"));
        tempRef = zECFNew("IDs", patElmt, "A");
        tempRefElmt.assign(zECFNewElmtObj(tempRef));
        tempRefElmt = zECFNewElmtObj(tempRef);
        $.assign(zECFSet("Type", "CID", tempRefElmt));
        $ = zECFSet("Type", "CID", tempRefElmt);
        $.assign(zECFSet("ID", zGtCID(iNI, iD, Value.nullValue(), Value.nullValue(), citm, twr11), tempRefElmt));
        $ = zECFSet("ID", zGtCID(iNI, iD, Value.nullValue(), Value.nullValue(), citm, twr11), tempRefElmt);
        tempRefElmt.assign(zECFNewElmtObj(tempRef));
        tempRefElmt = zECFNewElmtObj(tempRef);
        $.assign(zECFSet("Type", "EPI", tempRefElmt));
        $ = zECFSet("Type", "EPI", tempRefElmt);
        $.assign(zECFSet("ID", mpiGetID("EPT", iD, (- 1), 0, Value.nullValue(), Value.nullValue(), 1), tempRefElmt));
        $ = zECFSet("ID", mpiGetID("EPT", iD, (- 1), 0, Value.nullValue(), Value.nullValue(), 1), tempRefElmt);
        if (Text.follows(idTyp.toString(), Value.nullValue().toString())) {
            do {
                iit.assign(znxIxID("IIT", 600, idTyp));
                iit = znxIxID("IIT", 600, idTyp);
                tempRefElmt.assign(zECFNewElmtObj(tempRef));
                tempRefElmt = zECFNewElmtObj(tempRef);
                $.assign(zECFSet("Type", idTyp, tempRefElmt));
                $ = zECFSet("Type", idTyp, tempRefElmt);
                $.assign(zECFSet("ID", mpiGetID("EPT", iD, (- 1), iit, Value.nullValue(), Value.nullValue(), 1, 1), tempRefElmt));
                $ = zECFSet("ID", mpiGetID("EPT", iD, (- 1), iit, Value.nullValue(), Value.nullValue(), 1, 1), tempRefElmt);
            } while (false);
        }
        nID.assign(mpiGetID("EPT", iD, (- 1), 0, Value.nullValue(), Value.nullValue(), 1));
        nID = mpiGetID("EPT", iD, (- 1), 0, Value.nullValue(), Value.nullValue(), 1);
        nID.assign(getidin(nID, "EPT"));
        nID = getidin(nID, "EPT");
        $.assign(zECFSet("SSN", getVal(iD, 160), patElmt));
        $ = zECFSet("SSN", getVal(iD, 160), patElmt);
        $.assign(zECFSet("DOB", getVal(iD, 110), patElmt));
        $ = zECFSet("DOB", getVal(iD, 110), patElmt);
        $.assign(zECFSet("Gender", getVal(iD, 130), patElmt));
        $ = zECFSet("Gender", getVal(iD, 130), patElmt);
        $.assign(zECFSet("Status", getVal(iD, 102), patElmt));
        $ = zECFSet("Status", getVal(iD, 102), patElmt);
        $.assign(zECFSet("MaritalStatus", getVal(iD, 140), patElmt));
        $ = zECFSet("MaritalStatus", getVal(iD, 140), patElmt);
        tempRef.assign(zECFNew("Addresses", patElmt, "A"));
        tempRef = zECFNew("Addresses", patElmt, "A");
        tempRefElmt.assign(zECFNewElmtObj(tempRef));
        tempRefElmt = zECFNewElmtObj(tempRef);
        $.assign(zECFSet("Type", "Permanent", tempRefElmt));
        $ = zECFSet("Type", "Permanent", tempRefElmt);
        $.assign(zECFSet("Street", getVal(iD, 50), tempRefElmt));
        $ = zECFSet("Street", getVal(iD, 50), tempRefElmt);
        $.assign(zECFSet("City", getVal(iD, 60), tempRefElmt));
        $ = zECFSet("City", getVal(iD, 60), tempRefElmt);
        $.assign(zECFSet("County", getVal(iD, 75), tempRefElmt));
        $ = zECFSet("County", getVal(iD, 75), tempRefElmt);
        $.assign(zECFSet("State", getVal(iD, 70), tempRefElmt));
        $ = zECFSet("State", getVal(iD, 70), tempRefElmt);
        $.assign(zECFSet("Zip", getVal(iD, 80), tempRefElmt));
        $ = zECFSet("Zip", getVal(iD, 80), tempRefElmt);
        $.assign(zECFSet("Country", getVal(iD, 78), tempRefElmt));
        $ = zECFSet("Country", getVal(iD, 78), tempRefElmt);
        $.assign(zECFSet("Email", getVal(iD, 85), tempRefElmt));
        $ = zECFSet("Email", getVal(iD, 85), tempRefElmt);
        tempRef1 .assign(zECFNew("Phones", tempRefElmt, "A"));
        tempRef1 = zECFNew("Phones", tempRefElmt, "A");
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", "HOME", tempRefElmt1));
        $ = zECFSet("Type", "HOME", tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 90, 1, 99999, twr90), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 90, 1, 99999, twr90), tempRefElmt1);
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", "WORK", tempRefElmt1));
        $ = zECFSet("Type", "WORK", tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 5170, 1, 99999, twr5170), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 5170, 1, 99999, twr5170), tempRefElmt1);
        tempRefElmt.assign(zECFNewElmtObj(tempRef));
        tempRefElmt = zECFNewElmtObj(tempRef);
        $.assign(zECFSet("Type", "Temporary", tempRefElmt));
        $ = zECFSet("Type", "Temporary", tempRefElmt);
        $.assign(zECFSet("Street", getVal(iD, 5430), tempRefElmt));
        $ = zECFSet("Street", getVal(iD, 5430), tempRefElmt);
        $.assign(zECFSet("City", Value.nullValue(), tempRefElmt));
        $ = zECFSet("City", Value.nullValue(), tempRefElmt);
        $.assign(zECFSet("County", getVal(iD, 75), tempRefElmt));
        $ = zECFSet("County", getVal(iD, 75), tempRefElmt);
        $.assign(zECFSet("State", getVal(iD, 5432), tempRefElmt));
        $ = zECFSet("State", getVal(iD, 5432), tempRefElmt);
        $.assign(zECFSet("Zip", getVal(iD, 5434), tempRefElmt));
        $ = zECFSet("Zip", getVal(iD, 5434), tempRefElmt);
        $.assign(zECFSet("Country", getVal(iD, 5433), tempRefElmt));
        $ = zECFSet("Country", getVal(iD, 5433), tempRefElmt);
        $.assign(zECFSet("Email", Value.nullValue(), tempRefElmt));
        $ = zECFSet("Email", Value.nullValue(), tempRefElmt);
        tempRef1 .assign(zECFNew("Phones", tempRefElmt, "A"));
        tempRef1 = zECFNew("Phones", tempRefElmt, "A");
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", Value.nullValue(), tempRefElmt1));
        $ = zECFSet("Type", Value.nullValue(), tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 5435, 1, 99999, twr5435), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 5435, 1, 99999, twr5435), tempRefElmt1);
        tempRef.assign(zECFNew("EmploymentInformation", patElmt, "S"));
        tempRef = zECFNew("EmploymentInformation", patElmt, "S");
        $.assign(zECFSet("EmployerName", HUNETNM.nameOut("EEP", geti("EPT", iD, 5100, 1, 99999, twr5100)), tempRef));
        $ = zECFSet("EmployerName", HUNETNM.nameOut("EEP", geti("EPT", iD, 5100, 1, 99999, twr5100)), tempRef);
        $.assign(zECFSet("Occupation", getVal(iD, 5180), tempRef));
        $ = zECFSet("Occupation", getVal(iD, 5180), tempRef);
        tempRef1 .assign(zECFNew("Phones", tempRef, "A"));
        tempRef1 = zECFNew("Phones", tempRef, "A");
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", Value.nullValue(), tempRefElmt1));
        $ = zECFSet("Type", Value.nullValue(), tempRefElmt1);
        $.assign(zECFSet("Number", getVal(iD, 5160), tempRefElmt1));
        $ = zECFSet("Number", getVal(iD, 5160), tempRefElmt1);
        tempRef.assign(zECFNew("EmergencyContact", patElmt, "S"));
        tempRef = zECFNew("EmergencyContact", patElmt, "S");
        $.assign(zECFSet("LegalGuardian", geti("EPT", iD, 775, 1, 99999, twr775), tempRef));
        $ = zECFSet("LegalGuardian", geti("EPT", iD, 775, 1, 99999, twr775), tempRef);
        $.assign(zECFSet("Name", getVal(iD, 700), tempRef));
        $ = zECFSet("Name", getVal(iD, 700), tempRef);
        $.assign(zECFSet("Relation", getVal(iD, 770), tempRef));
        $ = zECFSet("Relation", getVal(iD, 770), tempRef);
        tempRef1 .assign(zECFNew("Phones", tempRef, "A"));
        tempRef1 = zECFNew("Phones", tempRef, "A");
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", "HOME", tempRefElmt1));
        $ = zECFSet("Type", "HOME", tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 750, 1, 99999, twr750), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 750, 1, 99999, twr750), tempRefElmt1);
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", "WORK", tempRefElmt1));
        $ = zECFSet("Type", "WORK", tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 760, 1, 99999, twr760), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 760, 1, 99999, twr760), tempRefElmt1);
        tempRef.assign(zECFNew("PCPs", patElmt, "A"));
        tempRef = zECFNew("PCPs", patElmt, "A");
        $.assign(getPCP(iD, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), 1, pcpArray));
        $ = getPCP(iD, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), 1, pcpArray);
        while (true) {
            pcp.assign(Builtin.followingKey(pcpArray.get(pcp.toString())));
            pcp = Builtin.followingKey(pcpArray.get(pcp.toString()));
            return ;
            if (Text.notFollows(pcp.toString(), Value.nullValue().toString())) {
                return ;
            }
            do {
                tempRefElmt.assign(zECFNewElmtObj(tempRef));
                tempRefElmt = zECFNewElmtObj(tempRef);
                $.assign(zECFSet("Type", pcpArray.get(pcp.toString()), tempRefElmt));
                $ = zECFSet("Type", pcpArray.get(pcp.toString()), tempRefElmt);
                $.assign(zECFSet("Name", HUNETNM.nameOut("SER", pcp), tempRefElmt));
                $ = zECFSet("Name", HUNETNM.nameOut("SER", pcp), tempRefElmt);
                $.assign(zECFSet("ID", zGtCID("SER", getidin(pcp, "SER")), tempRefElmt));
                $ = zECFSet("ID", zGtCID("SER", getidin(pcp, "SER")), tempRefElmt);
            } while (false);
        }
        lOwner.assign(zGetLOwn(iNI, iD));
        lOwner = zGetLOwn(iNI, iD);
        $.assign(zECFSet("HomeDeployment", zgetnp("ECI", lOwner, 100, 1, 99999), patElmt));
        $ = zECFSet("HomeDeployment", zgetnp("ECI", lOwner, 100, 1, 99999), patElmt);
        eanID.assign(getn("EPT", getidin(iD, "EPT"), 1, "300;4"));
        eanID = getn("EPT", getidin(iD, "EPT"), 1, "300;4");
        nameComponents.assign(zECFNew("NameComponents", patElmt, "S"));
        nameComponents = zECFNew("NameComponents", patElmt, "S");
        $.assign(zECFSet("History", zgetnp("EAN", eanID, 13, 1, 99999), nameComponents));
        $ = zECFSet("History", zgetnp("EAN", eanID, 13, 1, 99999), nameComponents);
        $.assign(zECFSet("FullLastName", zgetnp("EAN", eanID, 900, 1, 99999), nameComponents));
        $ = zECFSet("FullLastName", zgetnp("EAN", eanID, 900, 1, 99999), nameComponents);
        $.assign(zECFSet("LastName", getn("EAN", eanID, 1, "1000;1"), nameComponents));
        $ = zECFSet("LastName", getn("EAN", eanID, 1, "1000;1"), nameComponents);
        $.assign(zECFSet("LastNameFromSpouce", getn("EAN", eanID, 1, "1000;2"), nameComponents));
        $ = zECFSet("LastNameFromSpouce", getn("EAN", eanID, 1, "1000;2"), nameComponents);
        $.assign(zECFSet("FirstName", getn("EAN", eanID, 1, "1000;3"), nameComponents));
        $ = zECFSet("FirstName", getn("EAN", eanID, 1, "1000;3"), nameComponents);
        $.assign(zECFSet("MiddleName", getn("EAN", eanID, 1, "1000;4"), nameComponents));
        $ = zECFSet("MiddleName", getn("EAN", eanID, 1, "1000;4"), nameComponents);
        $.assign(zECFSet("LastNamePrefix", getn("EAN", eanID, 1, "1000;5"), nameComponents));
        $ = zECFSet("LastNamePrefix", getn("EAN", eanID, 1, "1000;5"), nameComponents);
        $.assign(zECFSet("SpouceLastNamePrefix", getn("EAN", eanID, 1, "1000;6"), nameComponents));
        $ = zECFSet("SpouceLastNamePrefix", getn("EAN", eanID, 1, "1000;6"), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;7"));
        temp = getn("EAN", eanID, 1, "1000;7");
        $.assign(zECFSet("Title", zgetCatC(temp, 1, "EAN", 1070), nameComponents));
        $ = zECFSet("Title", zgetCatC(temp, 1, "EAN", 1070), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;8"));
        temp = getn("EAN", eanID, 1, "1000;8");
        $.assign(zECFSet("Suffix", zgetCatC(temp, 1, "EAN", 1080), nameComponents));
        $ = zECFSet("Suffix", zgetCatC(temp, 1, "EAN", 1080), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;9"));
        temp = getn("EAN", eanID, 1, "1000;9");
        $.assign(zECFSet("Academic", zgetCatC(temp, 1, "EAN", 1090), nameComponents));
        $ = zECFSet("Academic", zgetCatC(temp, 1, "EAN", 1090), nameComponents);
        $.assign(zECFSet("PrefferedName", getn("EAN", eanID, 1, "1000;10"), nameComponents));
        $ = zECFSet("PrefferedName", getn("EAN", eanID, 1, "1000;10"), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;11"));
        temp = getn("EAN", eanID, 1, "1000;11");
        $.assign(zECFSet("PreferredNameType", zgetCatC(temp, 1, "EAN", 1110), nameComponents));
        $ = zECFSet("PreferredNameType", zgetCatC(temp, 1, "EAN", 1110), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;12"));
        temp = getn("EAN", eanID, 1, "1000;12");
        $.assign(zECFSet("SpouceLastNameFirst", zgetCatC(temp, 1, "EAN", 1120), nameComponents));
        $ = zECFSet("SpouceLastNameFirst", zgetCatC(temp, 1, "EAN", 1120), nameComponents);
        $.assign(zECFSet("GivenNameInitials", getn("EAN", eanID, 1, "1000;13"), nameComponents));
        $ = zECFSet("GivenNameInitials", getn("EAN", eanID, 1, "1000;13"), nameComponents);
        return  1;
    }

    /**
     * addPatientExternal(ID
     * 
     */
    public Value addPatientExternal(final Value iD) {
        Value tempRef = Value.nullValue();
        Value tempRefElmt = Value.nullValue();
        Value pcp = Value.nullValue();
        Value pcpArray = Value.nullValue();
        Value lOwner = Value.nullValue();
        Value iit = Value.nullValue();
        Value temp = Value.nullValue();
        Value twr11 = Value.nullValue();
        Value twr90 = Value.nullValue();
        Value twr5170 = Value.nullValue();
        Value twr5435 = Value.nullValue();
        Value twr5100 = Value.nullValue();
        Value twr775 = Value.nullValue();
        Value twr750 = Value.nullValue();
        Value twr760 = Value.nullValue();
        Value eanID = Value.nullValue();
        Value nID = Value.nullValue();
        Value nameComponents = Value.nullValue();
        Value tempRef1 = Value.nullValue();
        Value tempRefElmt1 = Value.nullValue();
        $.assign(zECFSet("Name", HUNETNM.nameOut("EPT", iD), Value.nullValue()));
        $ = zECFSet("Name", HUNETNM.nameOut("EPT", iD), Value.nullValue());
        tempRef.assign(zECFNew("IDs", Value.nullValue(), "A"));
        tempRef = zECFNew("IDs", Value.nullValue(), "A");
        tempRefElmt.assign(zECFNewElmtObj(tempRef));
        tempRefElmt = zECFNewElmtObj(tempRef);
        $.assign(zECFSet("Type", "CID", tempRefElmt));
        $ = zECFSet("Type", "CID", tempRefElmt);
        $.assign(zECFSet("ID", zGtCID(iNI, iD, Value.nullValue(), Value.nullValue(), citm, twr11), tempRefElmt));
        $ = zECFSet("ID", zGtCID(iNI, iD, Value.nullValue(), Value.nullValue(), citm, twr11), tempRefElmt);
        tempRefElmt.assign(zECFNewElmtObj(tempRef));
        tempRefElmt = zECFNewElmtObj(tempRef);
        $.assign(zECFSet("Type", geti("IIT", 0, 600, 1, 99999), tempRefElmt));
        $ = zECFSet("Type", geti("IIT", 0, 600, 1, 99999), tempRefElmt);
        $.assign(zECFSet("ID", mpiGetID("EPT", iD, (- 1), 0, Value.nullValue(), Value.nullValue(), 1), tempRefElmt));
        $ = zECFSet("ID", mpiGetID("EPT", iD, (- 1), 0, Value.nullValue(), Value.nullValue(), 1), tempRefElmt);
        if (Text.follows(idTyp.toString(), Value.nullValue().toString())) {
            do {
                iit.assign(znxIxID("IIT", 600, idTyp));
                iit = znxIxID("IIT", 600, idTyp);
                tempRefElmt.assign(zECFNewElmtObj(tempRef));
                tempRefElmt = zECFNewElmtObj(tempRef);
                $.assign(zECFSet("Type", idTyp, tempRefElmt));
                $ = zECFSet("Type", idTyp, tempRefElmt);
                $.assign(zECFSet("ID", mpiGetID("EPT", iD, (- 1), iit, Value.nullValue(), Value.nullValue(), 1, 1), tempRefElmt));
                $ = zECFSet("ID", mpiGetID("EPT", iD, (- 1), iit, Value.nullValue(), Value.nullValue(), 1, 1), tempRefElmt);
            } while (false);
        }
        nID.assign(mpiGetID("EPT", iD, (- 1), 0, Value.nullValue(), Value.nullValue(), 1));
        nID = mpiGetID("EPT", iD, (- 1), 0, Value.nullValue(), Value.nullValue(), 1);
        nID.assign(getidin(nID, "EPT"));
        nID = getidin(nID, "EPT");
        $.assign(zECFSet("SSN", getVal(iD, 160), Value.nullValue()));
        $ = zECFSet("SSN", getVal(iD, 160), Value.nullValue());
        $.assign(zECFSet("DOB", getVal(iD, 110), Value.nullValue()));
        $ = zECFSet("DOB", getVal(iD, 110), Value.nullValue());
        $.assign(zECFSet("Gender", getVal(iD, 130), Value.nullValue()));
        $ = zECFSet("Gender", getVal(iD, 130), Value.nullValue());
        $.assign(zECFSet("Status", getVal(iD, 102), Value.nullValue()));
        $ = zECFSet("Status", getVal(iD, 102), Value.nullValue());
        $.assign(zECFSet("MaritalStatus", getVal(iD, 140), Value.nullValue()));
        $ = zECFSet("MaritalStatus", getVal(iD, 140), Value.nullValue());
        tempRef.assign(zECFNew("Addresses", Value.nullValue(), "A"));
        tempRef = zECFNew("Addresses", Value.nullValue(), "A");
        tempRefElmt.assign(zECFNewElmtObj(tempRef));
        tempRefElmt = zECFNewElmtObj(tempRef);
        $.assign(zECFSet("Type", "Permanent", tempRefElmt));
        $ = zECFSet("Type", "Permanent", tempRefElmt);
        $.assign(zECFSet("Street", getVal(iD, 50), tempRefElmt));
        $ = zECFSet("Street", getVal(iD, 50), tempRefElmt);
        $.assign(zECFSet("City", getVal(iD, 60), tempRefElmt));
        $ = zECFSet("City", getVal(iD, 60), tempRefElmt);
        $.assign(zECFSet("County", getVal(iD, 75), tempRefElmt));
        $ = zECFSet("County", getVal(iD, 75), tempRefElmt);
        $.assign(zECFSet("State", getVal(iD, 70), tempRefElmt));
        $ = zECFSet("State", getVal(iD, 70), tempRefElmt);
        $.assign(zECFSet("Zip", getVal(iD, 80), tempRefElmt));
        $ = zECFSet("Zip", getVal(iD, 80), tempRefElmt);
        $.assign(zECFSet("Country", getVal(iD, 78), tempRefElmt));
        $ = zECFSet("Country", getVal(iD, 78), tempRefElmt);
        $.assign(zECFSet("Email", getVal(iD, 85), tempRefElmt));
        $ = zECFSet("Email", getVal(iD, 85), tempRefElmt);
        tempRef1 .assign(zECFNew("Phones", tempRefElmt, "A"));
        tempRef1 = zECFNew("Phones", tempRefElmt, "A");
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", "HOME", tempRefElmt1));
        $ = zECFSet("Type", "HOME", tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 90, 1, 99999, twr90), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 90, 1, 99999, twr90), tempRefElmt1);
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", "WORK", tempRefElmt1));
        $ = zECFSet("Type", "WORK", tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 5170, 1, 99999, twr5170), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 5170, 1, 99999, twr5170), tempRefElmt1);
        tempRefElmt.assign(zECFNewElmtObj(tempRef));
        tempRefElmt = zECFNewElmtObj(tempRef);
        $.assign(zECFSet("Type", "Temporary", tempRefElmt));
        $ = zECFSet("Type", "Temporary", tempRefElmt);
        $.assign(zECFSet("Street", getVal(iD, 5430), tempRefElmt));
        $ = zECFSet("Street", getVal(iD, 5430), tempRefElmt);
        $.assign(zECFSet("City", Value.nullValue(), tempRefElmt));
        $ = zECFSet("City", Value.nullValue(), tempRefElmt);
        $.assign(zECFSet("County", getVal(iD, 75), tempRefElmt));
        $ = zECFSet("County", getVal(iD, 75), tempRefElmt);
        $.assign(zECFSet("State", getVal(iD, 5432), tempRefElmt));
        $ = zECFSet("State", getVal(iD, 5432), tempRefElmt);
        $.assign(zECFSet("Zip", getVal(iD, 5434), tempRefElmt));
        $ = zECFSet("Zip", getVal(iD, 5434), tempRefElmt);
        $.assign(zECFSet("Country", getVal(iD, 5433), tempRefElmt));
        $ = zECFSet("Country", getVal(iD, 5433), tempRefElmt);
        $.assign(zECFSet("Email", Value.nullValue(), tempRefElmt));
        $ = zECFSet("Email", Value.nullValue(), tempRefElmt);
        tempRef1 .assign(zECFNew("Phones", tempRefElmt, "A"));
        tempRef1 = zECFNew("Phones", tempRefElmt, "A");
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", Value.nullValue(), tempRefElmt1));
        $ = zECFSet("Type", Value.nullValue(), tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 5435, 1, 99999, twr5435), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 5435, 1, 99999, twr5435), tempRefElmt1);
        tempRef.assign(zECFNew("EmploymentInformation", Value.nullValue(), "S"));
        tempRef = zECFNew("EmploymentInformation", Value.nullValue(), "S");
        $.assign(zECFSet("EmployerName", HUNETNM.nameOut("EEP", geti("EPT", iD, 5100, 1, 99999, twr5100)), tempRef));
        $ = zECFSet("EmployerName", HUNETNM.nameOut("EEP", geti("EPT", iD, 5100, 1, 99999, twr5100)), tempRef);
        $.assign(zECFSet("Occupation", getVal(iD, 5180), tempRef));
        $ = zECFSet("Occupation", getVal(iD, 5180), tempRef);
        tempRef1 .assign(zECFNew("Phones", tempRef, "A"));
        tempRef1 = zECFNew("Phones", tempRef, "A");
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", Value.nullValue(), tempRefElmt1));
        $ = zECFSet("Type", Value.nullValue(), tempRefElmt1);
        $.assign(zECFSet("Number", getVal(iD, 5160), tempRefElmt1));
        $ = zECFSet("Number", getVal(iD, 5160), tempRefElmt1);
        tempRef.assign(zECFNew("EmergencyContact", Value.nullValue(), "S"));
        tempRef = zECFNew("EmergencyContact", Value.nullValue(), "S");
        $.assign(zECFSet("LegalGuardian", geti("EPT", iD, 775, 1, 99999, twr775), tempRef));
        $ = zECFSet("LegalGuardian", geti("EPT", iD, 775, 1, 99999, twr775), tempRef);
        $.assign(zECFSet("Name", getVal(iD, 700), tempRef));
        $ = zECFSet("Name", getVal(iD, 700), tempRef);
        $.assign(zECFSet("Relation", getVal(iD, 770), tempRef));
        $ = zECFSet("Relation", getVal(iD, 770), tempRef);
        tempRef1 .assign(zECFNew("Phones", tempRef, "A"));
        tempRef1 = zECFNew("Phones", tempRef, "A");
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", "HOME", tempRefElmt1));
        $ = zECFSet("Type", "HOME", tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 750, 1, 99999, twr750), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 750, 1, 99999, twr750), tempRefElmt1);
        tempRefElmt1 .assign(zECFNewElmtObj(tempRef1));
        tempRefElmt1 = zECFNewElmtObj(tempRef1);
        $.assign(zECFSet("Type", "WORK", tempRefElmt1));
        $ = zECFSet("Type", "WORK", tempRefElmt1);
        $.assign(zECFSet("Number", geti("EPT", iD, 760, 1, 99999, twr760), tempRefElmt1));
        $ = zECFSet("Number", geti("EPT", iD, 760, 1, 99999, twr760), tempRefElmt1);
        tempRef.assign(zECFNew("PCPs", Value.nullValue(), "A"));
        tempRef = zECFNew("PCPs", Value.nullValue(), "A");
        $.assign(getPCP(iD, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), 1, pcpArray));
        $ = getPCP(iD, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), 1, pcpArray);
        while (true) {
            pcp.assign(Builtin.followingKey(pcpArray.get(pcp.toString())));
            pcp = Builtin.followingKey(pcpArray.get(pcp.toString()));
            return ;
            if (Text.notFollows(pcp.toString(), Value.nullValue().toString())) {
                return ;
            }
            do {
                tempRefElmt.assign(zECFNewElmtObj(tempRef));
                tempRefElmt = zECFNewElmtObj(tempRef);
                $.assign(zECFSet("Type", pcpArray.get(pcp.toString()), tempRefElmt));
                $ = zECFSet("Type", pcpArray.get(pcp.toString()), tempRefElmt);
                $.assign(zECFSet("Name", HUNETNM.nameOut("SER", pcp), tempRefElmt));
                $ = zECFSet("Name", HUNETNM.nameOut("SER", pcp), tempRefElmt);
                $.assign(zECFSet("ID", zGtCID("SER", getidin(pcp, "SER")), tempRefElmt));
                $ = zECFSet("ID", zGtCID("SER", getidin(pcp, "SER")), tempRefElmt);
            } while (false);
        }
        lOwner.assign(zGetLOwn(iNI, iD));
        lOwner = zGetLOwn(iNI, iD);
        $.assign(zECFSet("HomeDeployment", zgetnp("ECI", lOwner, 100, 1, 99999), Value.nullValue()));
        $ = zECFSet("HomeDeployment", zgetnp("ECI", lOwner, 100, 1, 99999), Value.nullValue());
        eanID.assign(getn("EPT", getidin(iD, "EPT"), 1, "300;4"));
        eanID = getn("EPT", getidin(iD, "EPT"), 1, "300;4");
        nameComponents.assign(zECFNew("NameComponents", Value.nullValue(), "S"));
        nameComponents = zECFNew("NameComponents", Value.nullValue(), "S");
        $.assign(zECFSet("History", zgetnp("EAN", eanID, 13, 1, 99999), nameComponents));
        $ = zECFSet("History", zgetnp("EAN", eanID, 13, 1, 99999), nameComponents);
        $.assign(zECFSet("FullLastName", zgetnp("EAN", eanID, 900, 1, 99999), nameComponents));
        $ = zECFSet("FullLastName", zgetnp("EAN", eanID, 900, 1, 99999), nameComponents);
        $.assign(zECFSet("LastName", getn("EAN", eanID, 1, "1000;1"), nameComponents));
        $ = zECFSet("LastName", getn("EAN", eanID, 1, "1000;1"), nameComponents);
        $.assign(zECFSet("LastNameFromSpouce", getn("EAN", eanID, 1, "1000;2"), nameComponents));
        $ = zECFSet("LastNameFromSpouce", getn("EAN", eanID, 1, "1000;2"), nameComponents);
        $.assign(zECFSet("FirstName", getn("EAN", eanID, 1, "1000;3"), nameComponents));
        $ = zECFSet("FirstName", getn("EAN", eanID, 1, "1000;3"), nameComponents);
        $.assign(zECFSet("MiddleName", getn("EAN", eanID, 1, "1000;4"), nameComponents));
        $ = zECFSet("MiddleName", getn("EAN", eanID, 1, "1000;4"), nameComponents);
        $.assign(zECFSet("LastNamePrefix", getn("EAN", eanID, 1, "1000;5"), nameComponents));
        $ = zECFSet("LastNamePrefix", getn("EAN", eanID, 1, "1000;5"), nameComponents);
        $.assign(zECFSet("SpouceLastNamePrefix", getn("EAN", eanID, 1, "1000;6"), nameComponents));
        $ = zECFSet("SpouceLastNamePrefix", getn("EAN", eanID, 1, "1000;6"), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;7"));
        temp = getn("EAN", eanID, 1, "1000;7");
        $.assign(zECFSet("Title", zgetCatC(temp, 1, "EAN", 1070), nameComponents));
        $ = zECFSet("Title", zgetCatC(temp, 1, "EAN", 1070), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;8"));
        temp = getn("EAN", eanID, 1, "1000;8");
        $.assign(zECFSet("Suffix", zgetCatC(temp, 1, "EAN", 1080), nameComponents));
        $ = zECFSet("Suffix", zgetCatC(temp, 1, "EAN", 1080), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;9"));
        temp = getn("EAN", eanID, 1, "1000;9");
        $.assign(zECFSet("Academic", zgetCatC(temp, 1, "EAN", 1090), nameComponents));
        $ = zECFSet("Academic", zgetCatC(temp, 1, "EAN", 1090), nameComponents);
        $.assign(zECFSet("PrefferedName", getn("EAN", eanID, 1, "1000;10"), nameComponents));
        $ = zECFSet("PrefferedName", getn("EAN", eanID, 1, "1000;10"), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;11"));
        temp = getn("EAN", eanID, 1, "1000;11");
        $.assign(zECFSet("PreferredNameType", zgetCatC(temp, 1, "EAN", 1110), nameComponents));
        $ = zECFSet("PreferredNameType", zgetCatC(temp, 1, "EAN", 1110), nameComponents);
        temp.assign(getn("EAN", eanID, 1, "1000;12"));
        temp = getn("EAN", eanID, 1, "1000;12");
        $.assign(zECFSet("SpouceLastNameFirst", zgetCatC(temp, 1, "EAN", 1120), nameComponents));
        $ = zECFSet("SpouceLastNameFirst", zgetCatC(temp, 1, "EAN", 1120), nameComponents);
        $.assign(zECFSet("GivenNameInitials", getn("EAN", eanID, 1, "1000;13"), nameComponents));
        $ = zECFSet("GivenNameInitials", getn("EAN", eanID, 1, "1000;13"), nameComponents);
        return  1;
    }

    /**
     * GetVal(ID,itm,line,INI,twr,addlFlg
     * 
     */
    public Value getVal(final Value iD, final Value itm, final Value line, final Value iNI, final Value twr, final Value addlFlg) {
        return Value.nullValue();
        if (itm == Value.nullValue()) {
            return Value.nullValue();
        }
        Value netINI = Value.nullValue();
        Value val = Value.nullValue();
        iNI.assign("EPT");
        iNI = "EPT";
        if (iNI == Value.nullValue()) {
            iNI.assign("EPT");
            iNI = "EPT";
        }
        line.assign(1);
        line = 1;
        if (line == 0) {
            line.assign(1);
            line = 1;
        }
        val.assign(geti(iNI, iD, itm, line, 99999, twr));
        val = geti(iNI, iD, itm, line, 99999, twr);
        return val;
        if (val == Value.nullValue()) {
            return val;
        }
        if (Text.piece(twr.toString(), ",", 1) == "D") {
            val.assign(zdoutISO(val));
            val = zdoutISO(val);
            return val;
        }
        if (Text.piece(twr.toString(), ",", 1) == "C") {
            val.assign(zgetCat(val, 1, iNI, itm));
            val = zgetCat(val, 1, iNI, itm);
            return val;
        }
        netINI.assign(Text.piece(Text.piece(twr.toString(), ",", 14), ";", 2));
        netINI = Text.piece(Text.piece(twr.toString(), ",", 14), ";", 2);
        if (Text.follows(netINI, Value.nullValue().toString())) {
            do {
                if (!addlFlg) {
                    val.assign(zGtCID(netINI, val));
                    val = zGtCID(netINI, val);
                    return ;
                }
                val.assign(znam(netINI, val));
                val = znam(netINI, val);
            } while (false);
        }
        return val;
    }

}
