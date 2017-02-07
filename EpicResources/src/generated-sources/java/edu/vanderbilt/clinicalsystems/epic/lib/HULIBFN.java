
package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.lib.Builtin;
import edu.vanderbilt.clinicalsystems.m.core.lib.Reflect;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;

public class HULIBFN {

    @InjectRoutine
    public Value $;
    @InjectRoutine
    public Value hdata;

    /**
     * HULIBFN; ; ;2016-05-06 15:45:58;8.3;ONaGXHnymoc2apfere10UOfBKeI9LuVVa8I8SOyETd8keJIGmp2BTyEJyaNn0AkR
     * ;;#lib#HULIB/AIlib/KPULIB
     * ;;#lglob#HULIB
     * ; Copyright (C) 1995-2016 Epic Systems Corporation
     * ;*********************************************************************
     * ; TITLE:   HULIBFN
     * ; PURPOSE: Shared library functions related to coverages
     * ; AUTHOR:  many
     * ; CALLABLE TAGS:
     * ;    fngtPln - Retrieved the unformatted IDs for the benefit plan
     * ;              and related info for a coverage
     * ;    fngtPlnD - Same as fngtPln except info is returned formatted
     * ;    fnprCvg - Find primary coverage for a member
     * ;    fncvgLst - Create an array of a member's coverages
     * ;    fneffCvg - Determine if a patient is covered by a coverage
     * ;    fnGpPln - Retrieves the unformatted IDs for the benefit plan
     * ;              and related info for an employer (plan) group
     * ;    fnGpPlnD - Same as fnGpPln except info is returned formatted
     * ;    fnisNet - True if provider and plan have matching networks
     * ;    fncvgDts - Returns eff and term dates for member on coverage
     * ;    fnHARcvgLst - create an array of the coverages on a HAR
     * ;    DoesPlanSuppBillType - Does plan support bill type?
     * ;    PlanBillTypeOnly - Does plan ONLY support bill type?
     * ;    MCgetCvgSubLn - Find the subscriber line on a coverage
     * ;
     * ; REVISION HISTORY:
     * ; *asb 02/14 298991 - Add ppgId and skipRiders parameters to fnGtPln and fnGtPlnD
     * ; *wbw 09/14 326906 - Return lobId with fnGpPln
     * ; *imhh 1/16 402687 - Add MCgetCvgSubLn function
     * ; *sj   2/16 407162 - update isFOTrkOn to support new option
     * ;*********************************************************************
     * ;****
     * ; Parameter info (for all routines):
     * ; cvgId (I) : Coverage
     * ; ppgId (I) : Employer/plan group
     * ; dte (I) : M date
     * ; oa (O) : Pass in a dotted array name; returns:
     * ; oa("ppgId")   : Plan group
     * ; oa("mcrId")   : Carrier
     * ; oa("prodTyp") : Product type
     * ; oa("eppId")   : Benefit plan
     * ; oa("epmId")   : Payor
     * ;****
     * ; The following functions are called recursive and thus makes routines
     * ; calling them grow astronomically large.  Therefore they have been
     * ; moved to HULIC.  All fn functions which call other fn functions
     * ; should be treated in the same manner.
     * ;
     * ;---------
     * ; NAME:         fngtPln (PUBLIC)
     * ; DESCRIPTION:  Gets the payor, plan, plan group, etc for a coverage that are effective as of the specified date.
     * ; KEYWORDS:     Coverage,Benefit,Plan,Group,Payor,Library,Rider
     * ; CALLED BY:    Many, through the fngtPln function in HULIB library
     * ; PARAMETERS:
     * ;  cvgId (I,REQ) - coverage ID
     * ;  dte (I,OPT,DEFAULT:+$h) - date to use for plan group information
     * ;  oa (O,OPT) - Returns information about the coverage:
     * ;                    oa("epmId")   : Payor
     * ;                    oa("eppId")   : Benefit plan
     * ;                    oa("ppgId")   : Plan group
     * ;                    oa("mcrId")   : Carrier
     * ;                    oa("prodTyp") : Product type
     * ;                    oa("lobId")   : Line of business
     * ;                    oa("riderId",ln) : Riders
     * ;                    oa("cvgTyp")  : Coverage type
     * ;  eptId (I,OPT) - padded patient ID, to look up riders for MC cvgs
     * ;  payrOnly (I,OPT) - if true, only look up payor
     * ;  pyplOnly (I,OPT) - if true, only look up payor, plan, and group
     * ;                     also looks up carrier and prod type for free
     * ;  ppgId (I,OPT,DEFAULT:geti(cvgId,115,1,99999)) - The coverage record's employer group / plan group ID
     * ;  skipRiders (I,OPT) - if true, skip the rider lookup
     * ; RETURNS:      Returns the benefit plan ID. Kills and fills oa.
     * ; ASSUMES:
     * ; SIDE EFFECTS:
     * ; Rider algorithm:  loop through PPG if rider is sel mode,
     * ;                   find rider in CVG and compare mode to subScr flag
     * ;---------
     * ; *asb 02/14 298991 add ppgId,skipRiders
     * fngtPln(cvgId,dte,oa,eptId,payrOnly,pyplOnly,ppgId,skipRiders
     * 
     */
    public Value main() {
    }

    /**
     * fngtPln(cvgId,dte,oa,eptId,payrOnly,pyplOnly,ppgId,skipRiders
     * 
     */
    public Value fngtPln(final Value cvgId, final Value dte, final Value oa, final Value eptId, final Value payrOnly, final Value pyplOnly, final Value ppgId, final Value skipRiders) {
        return HULIC.fngtPln(cvgId, dte, oa, eptId, payrOnly, pyplOnly, ppgId, skipRiders);
    }

    /**
     * fngtPlnD(cvgId,dte,oa,eptId,payrOnly,pyplOnly,ppgId,skipRiders
     * 
     */
    public Value fngtPlnD(final Value cvgId, final Value dte, final Value oa, final Value eptId, final Value payrOnly, final Value pyplOnly, final Value ppgId, final Value skipRiders) {
        return HULIC.fngtPlnD(cvgId, dte, oa, eptId, payrOnly, pyplOnly, ppgId, skipRiders);
    }

    /**
     * fnprCvg(eptId,earId,dte,eptDat,noMed,mcrId,mngPyr,noEAR,noMSP,bType,etrId,tarId,tarLn,whichFO,harId
     * 
     */
    public Value fnprCvg(final Value eptId, final Value earId, final Value dte, final Value eptDat, final Value noMed, final Value mcrId, final Value mngPyr, final Value noEAR, final Value noMSP, final Value bType, final Value etrId, final Value tarId, final Value tarLn, final Value whichFO, final Value harId) {
        return HULIC.fnprCvg(eptId, earId, dte, eptDat, noMed, mcrId, mngPyr, noEAR, noMSP, bType, etrId, tarId, tarLn, whichFO, harId);
    }

    /**
     * DoesPlanSuppBillType(eppId,bType,isConv
     * 
     */
    public Value doesPlanSuppBillType(final Value eppId, final Value bType, final Value isConv) {
        Value epp95 = Value.nullValue();
        Value ret = Value.nullValue();
        if ((eppId == Value.nullValue())||(bType == Value.nullValue())) {
            return  0;
        }
        if (isConv == Value.nullValue()) {
            isConv.assign(zIsConv("EPP", eppId, 126139));
            isConv = zIsConv("EPP", eppId, 126139);
        }
        if (isConv == 1) {
            do {
                if (bType< 3) {
                    ret.assign(1);
                    ret = 1;
                    return ;
                }
                ret.assign(zisIxID("EPP", 85, bType, eppId));
                ret = zisIxID("EPP", 85, bType, eppId);
            } while (false);
            return ret;
        }
        if (bType > 2) {
            return  0;
        }
        epp95 .assign(zgetnp("EPP", eppId, 95, 1, 99999));
        epp95 = zgetnp("EPP", eppId, 95, 1, 99999);
        if ((epp95 == 3)||(epp95 == Value.nullValue())) {
            return  1;
        }
        return (epp95 == bType);
    }

    /**
     * PlanBillTypeOnly(eppId,bType,isConv
     * 
     */
    public Value planBillTypeOnly(final Value eppId, final Value bType, final Value isConv) {
        Value ret = Value.nullValue();
        Value epp95 = Value.nullValue();
        if ((eppId == Value.nullValue())||(bType == Value.nullValue())) {
            return  0;
        }
        isConv.assign(zIsConv("EPP", eppId, 126139));
        isConv = zIsConv("EPP", eppId, 126139);
        if (isConv == Value.nullValue()) {
            isConv.assign(zIsConv("EPP", eppId, 126139));
            isConv = zIsConv("EPP", eppId, 126139);
        }
        if (!isConv) {
            do {
                epp95 .assign(zgetnp("EPP", eppId, 95, 1, 99999));
                epp95 = zgetnp("EPP", eppId, 95, 1, 99999);
                if (epp95 == 3) {
                    ret.assign(0);
                    ret = 0;
                    return ;
                }
                epp95 .assign(1);
                epp95 = 1;
                if (epp95 == Value.nullValue()) {
                    epp95 .assign(1);
                    epp95 = 1;
                }
                ret.assign((epp95 == bType));
                ret = (epp95 == bType);
            } while (false);
            return ret;
        }
        if (zgetnp("EPP", eppId, 85, 0) == 0) {
            return  0;
        }
        return  0;
        if (!zisIxID("EPP", 85, bType, eppId)) {
            return  0;
        }
        return  1;
        if (zgetnp("EPP", eppId, 85, 0)< 2) {
            return  1;
        }
        return  0;
    }

    /**
     * fncvgLst(eptExt,earId,cvgList,dte,cvgTyp,mcrId,epmId,eppId,noEAR,chkSA,eptDat,noMed,in,ChkTyp,noEffChk,noMSP,mngPyr,bType,hospFO,disDt,bTypes,skpSec,inclShared,etrId,tarId,tarLn,whichFO,harId
     * 
     */
    public Value fncvgLst(final Value eptExt, final Value earId, final Value cvgList, final Value dte, final Value cvgTyp, final Value mcrId, final Value epmId, final Value eppId, final Value noEAR, final Value chkSA, final Value eptDat, final Value noMed, final Value in, final Value chkTyp, final Value noEffChk, final Value noMSP, final Value mngPyr, final Value bType, final Value hospFO, final Value disDt, final Value bTypes, final Value skpSec, final Value inclShared, final Value etrId, final Value tarId, final Value tarLn, final Value whichFO, final Value harId) {
        return HULIC4 .fncvgLst(eptExt, earId, cvgList, dte, cvgTyp, mcrId, epmId, eppId, noEAR, chkSA, eptDat, noMed, in, chkTyp, noEffChk, noMSP, mngPyr, bType, hospFO, disDt, bTypes, skpSec, inclShared, etrId, tarId, tarLn, whichFO, harId);
    }

    /**
     * FOVISITQ 1
     * 
     */
    public long fOVISIT() {
        return  1;
    }

    /**
     * FOPATIENTQ 0
     * 
     */
    public long fOPATIENT() {
        return  0;
    }

    /**
     * fneffCvg(cvgId,efDt,eptExt,oa,ineffCvg,ChkType,disDTE,src
     * 
     */
    public Value fneffCvg(final Value cvgId, final Value efDt, final Value eptExt, final Value oa, final Value ineffCvg, final Value chkType, final Value disDTE, final Value src) {
        return HULIC1 .fneffCvg(cvgId, efDt, eptExt, oa, ineffCvg, chkType, disDTE, src);
    }

    /**
     * fnGpPln(ppgId,dte,oa,pastOk
     * 
     */
    public Value fnGpPln(final Value ppgId, final Value dte, final Value oa, final boolean pastOk) {
        return Value.nullValue();
        if ((ppgId == Value.nullValue())||(dte == Value.nullValue())) {
            return Value.nullValue();
        }
        Value numBens = Value.nullValue();
        Value i = Value.nullValue();
        Value benDat = Value.nullValue();
        Value bestDat = Value.nullValue();
        Value ln = Value.nullValue();
        Value pak = Value.nullValue();
        Value cnt = Value.nullValue();
        $.assign(din(dte));
        $ = din(dte);
        dte.assign($);
        dte = $;
        if (Text.follows($.toString(), Value.nullValue().toString())) {
            dte.assign($);
            dte = $;
        }
        if (!pastOk) {
            dte.assign(Builtin.horolog());
            dte = Builtin.horolog();
            if (dte<Builtin.horolog()) {
                dte.assign(Builtin.horolog());
                dte = Builtin.horolog();
            }
        }
        numBens.assign(getn("PPG", ppgId, 0, "18200;1;1"));
        numBens = getn("PPG", ppgId, 0, "18200;1;1");
        bestDat.assign((- 1));
        bestDat = (- 1);
        ln.assign((- 1));
        ln = (- 1);
        if (numBens == 0) {
            do {
                benDat.assign(((121531 -dte)- 1));
                benDat = ((121531 -dte)- 1);
                benDat.assign(zoDT("PPG", ppgId, 100, benDat));
                benDat = zoDT("PPG", ppgId, 100, benDat);
                return ;
                if (benDat == Value.nullValue()) {
                    return ;
                }
                oa.get("eppId").assign(zgetnp("PPG", ppgId, 100, 1, benDat));
                oa = zgetnp("PPG", ppgId, 100, 1, benDat);
                cnt.assign(zgetnp("PPG", ppgId, 120, 0, benDat));
                cnt = zgetnp("PPG", ppgId, 120, 0, benDat);
                for (Value ln = 1; (ln >= cnt); ln += 1) {
                    oa.get("riderId").get(ln).assign(zgetnp("PPG", ppgId, 120, ln, benDat));
                    oa = zgetnp("PPG", ppgId, 120, ln, benDat);
                }
                oa.get("riderId").get("0").assign(cnt);
                oa = cnt;
            } while (false);
            return  1;
        }
        for (Value i = 1; (i >= numBens); i += 1) {
            do {
                benDat.assign(getn("PPG", ppgId, i, "18200;1;1"));
                benDat = getn("PPG", ppgId, i, "18200;1;1");
                if (Text.follows(benDat.toString(), Value.nullValue().toString())) {
                    bestDat.assign(benDat);
                    bestDat = benDat;
                    ln.assign(i);
                    ln = i;
                }
            } while (false);
        }
        if (ln >= 0) {
            pak.assign(zgetblu("PPG", ppgId, 18200, ln));
            pak = zgetblu("PPG", ppgId, 18200, ln);
        }
        oa.get("benDat").assign(((ln< 0)?Value.nullValue():((numBens == 0)?benDat:(true?zgetpcu(pak, 6):Value.nullValue()))));
        oa = ((ln< 0)?Value.nullValue():((numBens == 0)?benDat:(true?zgetpcu(pak, 6):Value.nullValue())));
        return Value.nullValue();
        if (ln< 0) {
            return Value.nullValue();
        }
        oa.get("mcrId").assign(zgetpcu(pak, 2));
        oa = zgetpcu(pak, 2);
        oa.get("prodTyp").assign(zgetpcu(pak, 3));
        oa = zgetpcu(pak, 3);
        oa.get("epmId").assign(zgetpcu(pak, 4));
        oa = zgetpcu(pak, 4);
        oa.get("eppId").assign(zgetpcu(pak, 5));
        oa = zgetpcu(pak, 5);
        oa.get("PlnEffDte").assign(zgetpcu(pak, 1));
        oa = zgetpcu(pak, 1);
        oa.get("lobId").assign(zgetpcu(pak, 8));
        oa = zgetpcu(pak, 8);
        return  1;
    }

    /**
     * fnGpPlnD(ppgId,dte,oa,pastOk
     * 
     */
    public Value fnGpPlnD(final Value ppgId, final Value dte, final Value oa, final Value pastOk) {
        return Value.nullValue();
        if (fnGpPln(ppgId, dte, oa, pastOk) == Value.nullValue()) {
            return Value.nullValue();
        }
        oa.get("mcrId").assign(netOut("MCR", ".2", ".1", "NI", oa.get("mcrId")));
        oa = netOut("MCR", ".2", ".1", "NI", oa.get("mcrId"));
        oa.get("prodTyp").assign(catOut("PPG", 18005, "T", oa.get("prodTyp")));
        oa = catOut("PPG", 18005, "T", oa.get("prodTyp"));
        oa.get("epmId").assign(netOut("EPM", ".2", ".1", "NI", oa.get("epmId")));
        oa = netOut("EPM", ".2", ".1", "NI", oa.get("epmId"));
        oa.get("eppId").assign(netOut("EPP", ".2", ".1", "NI", oa.get("eppId")));
        oa = netOut("EPP", ".2", ".1", "NI", oa.get("eppId"));
        return  1;
    }

    /**
     * fnisNet(serId,eptId,cvgId,dte
     * 
     */
    public Value fnisNet(final Value serId, final Value eptId, final Value cvgId, final Value dte) {
        return (serNetIn(eptId, cvgId, dte, serId)!= Value.nullValue());
    }

    /**
     * fncvgDts(eptExt,cvgId,oa,apptDt
     * 
     */
    public long fncvgDts(final Value eptExt, final Value cvgId, final Value oa, final Value apptDt) {
        Value ln = Value.nullValue();
        long found = 0;
        Value cnt = Value.nullValue();
        found = 0;
        Value __multipleAssignmentTemp = Value.nullValue();
        oa.get("effDt").assign(Value.nullValue());
        oa = Value.nullValue();
        oa.get("tmDt").assign(Value.nullValue());
        oa = Value.nullValue();
        ln.assign(fnocvgpt(cvgId, eptExt, Value.nullValue(), Value.nullValue(), apptDt));
        ln = fnocvgpt(cvgId, eptExt, Value.nullValue(), Value.nullValue(), apptDt);
        found = 1;
        if (Text.follows(ln.toString(), Value.nullValue().toString())) {
            found = 1;
        }
        do {
            oa.get("effDt").assign(zgetnp("CVG", cvgId, 320, ln));
            oa = zgetnp("CVG", cvgId, 320, ln);
            oa.get("tmDt").assign(zgetnp("CVG", cvgId, 330, ln));
            oa = zgetnp("CVG", cvgId, 330, ln);
            oa.get("line").assign(ln);
            oa = ln;
        } while (false);
        if (found) {
            do {
                oa.get("effDt").assign(zgetnp("CVG", cvgId, 320, ln));
                oa = zgetnp("CVG", cvgId, 320, ln);
                oa.get("tmDt").assign(zgetnp("CVG", cvgId, 330, ln));
                oa = zgetnp("CVG", cvgId, 330, ln);
                oa.get("line").assign(ln);
                oa = ln;
            } while (false);
        }
        do {
            if (apptDt == Value.nullValue()) {
                do {
                    oa.get("effDt").assign(zgetnp("CVG", cvgId, 400, 1));
                    oa = zgetnp("CVG", cvgId, 400, 1);
                    oa.get("tmDt").assign(zgetnp("CVG", cvgId, 410, 1));
                    oa = zgetnp("CVG", cvgId, 410, 1);
                    found = 2;
                    if (Text.follows(oa.get("effDt").toString(), (Value.nullValue()||Text.follows(oa.get("tmDt").toString(), Value.nullValue().toString())))) {
                        found = 2;
                    }
                } while (false);
            } else {
                do {
                    found = 0;
                    for (Value cnt = 1; (cnt >= zgetnp("CVG", cvgId, 400, 0)); cnt += 1) {
                        return ;
                        if (found == 2) {
                            return ;
                        }
                        do {
                            oa.get("effDt").assign(zgetnp("CVG", cvgId, 400, cnt));
                            oa = zgetnp("CVG", cvgId, 400, cnt);
                            oa.get("tmDt").assign(zgetnp("CVG", cvgId, 410, cnt));
                            oa = zgetnp("CVG", cvgId, 410, cnt);
                            if (apptDt<= oa.get("tmDt")) {
                                found.assign(2);
                                found = 2;
                            }
                        } while (false);
                    }
                } while (false);
            }
        } while (false);
        if (((oa.get("effDt") == Value.nullValue())&&(oa.get("tmDt") == Value.nullValue()))||(!found)) {
            do {
                if (apptDt == Value.nullValue()) {
                    do {
                        oa.get("effDt").assign(zgetnp("CVG", cvgId, 400, 1));
                        oa = zgetnp("CVG", cvgId, 400, 1);
                        oa.get("tmDt").assign(zgetnp("CVG", cvgId, 410, 1));
                        oa = zgetnp("CVG", cvgId, 410, 1);
                        found.assign(2);
                        found = 2;
                        if (Text.follows(oa.get("effDt").toString(), (Value.nullValue()||Text.follows(oa.get("tmDt").toString(), Value.nullValue().toString())))) {
                            found.assign(2);
                            found = 2;
                        }
                    } while (false);
                } else {
                    do {
                        found.assign(0);
                        found = 0;
                        for (Value cnt = 1; (cnt >= zgetnp("CVG", cvgId, 400, 0)); cnt += 1) {
                            return ;
                            if (found == 2) {
                                return ;
                            }
                            do {
                                oa.get("effDt").assign(zgetnp("CVG", cvgId, 400, cnt));
                                oa = zgetnp("CVG", cvgId, 400, cnt);
                                oa.get("tmDt").assign(zgetnp("CVG", cvgId, 410, cnt));
                                oa = zgetnp("CVG", cvgId, 410, cnt);
                                if (apptDt<= oa.get("tmDt")) {
                                    found.assign(2);
                                    found = 2;
                                }
                            } while (false);
                        }
                    } while (false);
                }
            } while (false);
        }
        return found;
    }

    /**
     * fnCvgFC(earId,cvgId,dte,epmId,forPrice
     * 
     */
    public Value fnCvgFC(final Value earId, final Value cvgId, final Value dte, final Value epmId, final boolean forPrice) {
        Value fc = Value.nullValue();
        Value eppId = Value.nullValue();
        Value oa = Value.nullValue();
        if (cvgId!= Value.nullValue()) {
            fc.assign(zgetnp("CVG", cvgId, 650, 1));
            fc = zgetnp("CVG", cvgId, 650, 1);
        }
        if (fc!= Value.nullValue()) {
            do {
                if (epmId == 0) {
                    $.assign(HULIC.fngtPln(cvgId, dte, oa, Value.nullValue(), 1));
                    $ = HULIC.fngtPln(cvgId, dte, oa, Value.nullValue(), 1);
                    if (oa.get("epmId") == Value.nullValue()) {
                        $.assign(HULIC.fngtPln(cvgId, dte, oa, Value.nullValue(), 1));
                        $ = HULIC.fngtPln(cvgId, dte, oa, Value.nullValue(), 1);
                    }
                    epmId.assign(oa.get("epmId"));
                    epmId = oa.get("epmId");
                }
                if (fc == geti("EPM", epmId, 700, 1)) {
                    fc.assign(Value.nullValue());
                    fc = Value.nullValue();
                }
            } while (false);
        }
        if (fc!= Value.nullValue()) {
            return fc;
        }
        if (Text.follows(cvgId.toString(), Value.nullValue().toString())) {
            eppId.assign(HULIC.fngtPln(cvgId, dte, oa));
            eppId = HULIC.fngtPln(cvgId, dte, oa);
            fc.assign(zgetnp("EPP", eppId, 2115, 1));
            fc = zgetnp("EPP", eppId, 2115, 1);
            return fc;
            if (Text.follows(fc.toString(), Value.nullValue().toString())) {
                return fc;
            }
        }
        if (epmId == 0) {
            $.assign(HULIC.fngtPln(cvgId, dte, oa, Value.nullValue(), 1));
            $ = HULIC.fngtPln(cvgId, dte, oa, Value.nullValue(), 1);
            if (oa.get("epmId") == Value.nullValue()) {
                $.assign(HULIC.fngtPln(cvgId, dte, oa, Value.nullValue(), 1));
                $ = HULIC.fngtPln(cvgId, dte, oa, Value.nullValue(), 1);
            }
            epmId.assign(oa.get("epmId"));
            epmId = oa.get("epmId");
        }
        if (epmId!= 0) {
            do {
                if (forPrice) {
                    fc.assign(zgetnp("EPM", epmId, 701, 1));
                    fc = zgetnp("EPM", epmId, 701, 1);
                    return ;
                    if (Text.follows(fc.toString(), Value.nullValue().toString())) {
                        return ;
                    }
                }
                fc.assign(zgetnp("EPM", epmId, 700, 1));
                fc = zgetnp("EPM", epmId, 700, 1);
            } while (false);
            return fc;
            if (Text.follows(fc.toString(), Value.nullValue().toString())) {
                return fc;
            }
        }
        if (Text.follows(earId.toString(), Value.nullValue().toString())) {
            do {
                fc.assign(gtActFC(earId));
                fc = gtActFC(earId);
                return ;
                if (Text.follows(fc.toString(), Value.nullValue().toString())) {
                    return ;
                }
                fc.assign(zgetnp("EAR", earId, 220, 1));
                fc = zgetnp("EAR", earId, 220, 1);
            } while (false);
            return fc;
            if (Text.follows(fc.toString(), Value.nullValue().toString())) {
                return fc;
            }
        }
        return fc;
    }

    /**
     * gtActFC(earId
     * 
     */
    public Value gtActFC(final Value earId) {
        Value actType = Value.nullValue();
        Value sa = Value.nullValue();
        Value ovride = Value.nullValue();
        Value finCls = Value.nullValue();
        Value altSa = Value.nullValue();
        actType.assign(zgetnp("EAR", earId, 210, 1, 99999));
        actType = zgetnp("EAR", earId, 210, 1, 99999);
        sa.assign(zgetnp("EAR", earId, 65, 1, 99999));
        sa = zgetnp("EAR", earId, 65, 1, 99999);
        ovride.assign(hugeti("EAF", sa, 5830, 99999, actType));
        ovride = hugeti("EAF", sa, 5830, 99999, actType);
        if (Text.follows(ovride.toString(), Value.nullValue().toString())) {
            finCls.assign(ovride);
            finCls = ovride;
        } else {
            finCls.assign(zgetnp("EAF", sa, 5800, 1, 99999));
            finCls = zgetnp("EAF", sa, 5800, 1, 99999);
        }
        return finCls;
        if (Text.follows(finCls.toString(), Value.nullValue().toString())) {
            return finCls;
        }
        altSa.assign(zgetnp("EAF", sa, 236, 1, 99999));
        altSa = zgetnp("EAF", sa, 236, 1, 99999);
        ovride.assign(hugeti("EAF", altSa, 5830, 99999, actType));
        ovride = hugeti("EAF", altSa, 5830, 99999, actType);
        if (Text.follows(ovride.toString(), Value.nullValue().toString())) {
            finCls.assign(ovride);
            finCls = ovride;
        } else {
            finCls.assign(zgetnp("EAF", altSa, 5800, 1, 99999));
            finCls = zgetnp("EAF", altSa, 5800, 1, 99999);
        }
        return finCls;
        if (Text.follows(finCls.toString(), Value.nullValue().toString())) {
            return finCls;
        }
        return finCls;
        if (zgetnp("EAF", sa, 222, 1)!= 1) {
            return finCls;
        }
        finCls.assign(hugeti("EAF", 1, 5830, 99999, actType));
        finCls = hugeti("EAF", 1, 5830, 99999, actType);
        return finCls;
        if (Text.follows(finCls.toString(), Value.nullValue().toString())) {
            return finCls;
        }
        finCls.assign(zgetnp("EAF", 1, 5800, 1, 99999));
        finCls = zgetnp("EAF", 1, 5800, 1, 99999);
        return finCls;
    }

    /**
     * fngtPyr(cvgId,date
     * 
     */
    public Value fngtPyr(final Value cvgId, final Value date) {
        Value array = Value.nullValue();
        Value epmId = Value.nullValue();
        if (Text.notFollows(cvgId.toString(), Value.nullValue().toString())) {
            return Value.nullValue();
        }
        date.assign(Builtin.horolog());
        date = Builtin.horolog();
        if (date == Value.nullValue()) {
            date.assign(Builtin.horolog());
            date = Builtin.horolog();
        }
        $.assign(HULIC.fngtPln(cvgId, date, array, Value.nullValue(), 1));
        $ = HULIC.fngtPln(cvgId, date, array, Value.nullValue(), 1);
        epmId.assign(array.get("epmId"));
        epmId = array.get("epmId");
        epmId.assign(zgetnp("CVG", cvgId, 100, 1));
        epmId = zgetnp("CVG", cvgId, 100, 1);
        if (epmId == 0) {
            epmId.assign(zgetnp("CVG", cvgId, 100, 1));
            epmId = zgetnp("CVG", cvgId, 100, 1);
        }
        return epmId;
    }

    /**
     * fnHARcvgLst(harID,eptID,eptDAT,cvgLst
     * 
     */
    public Value fnHARcvgLst(final Value harID, final Value eptID, final Value eptDAT, final Value cvgLst) {
        Value ln = Value.nullValue();
        if (harID == Value.nullValue()) {
            do {
                if ((eptID == Value.nullValue())||(eptDAT == Value.nullValue())) {
                    return ;
                }
                harID.assign(zgetnp("EPT", eptID, 2500, 1, eptDAT));
                harID = zgetnp("EPT", eptID, 2500, 1, eptDAT);
            } while (false);
            return  0;
            if (harID == Value.nullValue()) {
                return  0;
            }
        }
        cvgLst.get("0").assign(zgetnp("HAR", harID, 300, 0, 99999));
        cvgLst = zgetnp("HAR", harID, 300, 0, 99999);
        for (Value ln = 1; (ln >= cvgLst.get("0")); ln += 1) {
            cvgLst.get(ln).assign(zgetnp("HAR", harID, 300, ln, 99999));
            cvgLst = zgetnp("HAR", harID, 300, ln, 99999);
        }
        return cvgLst.get("0");
    }

    /**
     * gtcvgcxt(cvgId,eptId,effDt,oa,pcpTyp,spons,locFlag
     * 
     */
    public long gtcvgcxt(final Value cvgId, final Value eptId, final Value effDt, final Value oa, final long pcpTyp, final Value spons, final Value locFlag) {
        Value effDat = Value.nullValue();
        Value dat = Value.nullValue();
        Value i = Value.nullValue();
        Value ln = Value.nullValue();
        Value str = Value.nullValue();
        Value tmp = Value.nullValue();
        Value h3005 = Value.nullValue();
        Value rpTermDt = Value.nullValue();
        effDt.assign(Builtin.horolog());
        effDt = Builtin.horolog();
        if (effDt == 0) {
            effDt.assign(Builtin.horolog());
            effDt = Builtin.horolog();
        }
        pcpTyp = 1;
        if (pcpTyp == Value.nullValue()) {
            pcpTyp.assign(1);
            pcpTyp = 1;
        }
        effDat.assign(((121531 -effDt)- 1));
        effDat = ((121531 -effDt)- 1);
        oa.get("ppgId").assign(zgetnp("CVG", cvgId, 115, 1, 99999));
        oa = zgetnp("CVG", cvgId, 115, 1, 99999);
        if (oa.get("ppgId")) {
            do {
                dat.assign(zoDT("PPG", oa.get("ppgId"), 100, effDat));
                dat = zoDT("PPG", oa.get("ppgId"), 100, effDat);
                return ;
                if (dat == Value.nullValue()) {
                    return ;
                }
                oa.get("eppId").assign(zgetnp("PPG", oa.get("ppgId"), 100, 1, dat));
                oa = zgetnp("PPG", oa.get("ppgId"), 100, 1, dat);
                oa.get("epmId").assign(zgetnp("PPG", oa.get("ppgId"), 95, 1, dat));
                oa = zgetnp("PPG", oa.get("ppgId"), 95, 1, dat);
                oa.get("epmId").assign(zgetnp("PPG", oa.get("ppgId"), 90, 1));
                oa = zgetnp("PPG", oa.get("ppgId"), 90, 1);
                if (oa.get("epmId") == Value.nullValue()) {
                    oa.get("epmId").assign(zgetnp("PPG", oa.get("ppgId"), 90, 1));
                    oa = zgetnp("PPG", oa.get("ppgId"), 90, 1);
                }
                oa.get("lobId").assign(zgetnp("PPG", oa.get("ppgId"), 80, 1, dat));
                oa = zgetnp("PPG", oa.get("ppgId"), 80, 1, dat);
                oa.get("mcrId").assign(zgetnp("PPG", oa.get("ppgId"), 18001, 1, dat));
                oa = zgetnp("PPG", oa.get("ppgId"), 18001, 1, dat);
            } while (false);
        }
        dat.assign(zoDT("EPT", eptId, 5615, effDat));
        dat = zoDT("EPT", eptId, 5615, effDat);
        oa.get("eafId").assign(zgetnp("EPT", eptId, 5615, 1, dat));
        oa = zgetnp("EPT", eptId, 5615, 1, dat);
        if (dat) {
            oa.get("eafId").assign(zgetnp("EPT", eptId, 5615, 1, dat));
            oa = zgetnp("EPT", eptId, 5615, 1, dat);
        }
        oa.get("SA").assign(zgetnp("EAF", oa.get("eafId"), 5250, 1));
        oa = zgetnp("EAF", oa.get("eafId"), 5250, 1);
        if (Text.follows(oa.get("eafId").toString(), Value.nullValue().toString())) {
            oa.get("SA").assign(zgetnp("EAF", oa.get("eafId"), 5250, 1));
            oa = zgetnp("EAF", oa.get("eafId"), 5250, 1);
        }
        if (oa.get("SA") == Value.nullValue()) {
            oa.get("SA").assign(zgetnp("EPT", eptId, 5635, 1));
            oa = zgetnp("EPT", eptId, 5635, 1);
        }
        if (oa.get("SA") == Value.nullValue()) {
            oa.get("SA").assign(1);
            oa = 1;
        }
        ln.assign(zmaxunl(zgetblu("EPT", eptId, 80100, 0)));
        ln = zmaxunl(zgetblu("EPT", eptId, 80100, 0));
        for (Value i = 1; (i >= ln); i += 1) {
            do {
                str.assign(zgetblu("EPT", eptId, 80100, i));
                str = zgetblu("EPT", eptId, 80100, i);
                return ;
                if (zgetpcu(str, 12) == 1) {
                    return ;
                }
                return ;
                if (zgetpcu(str, 4)!= pcpTyp) {
                    return ;
                }
                return ;
                if (zgetpcu(str, 5)>effDt) {
                    return ;
                }
                tmp.assign(zgetpcu(str, 6));
                tmp = zgetpcu(str, 6);
                if (tmp!= Value.nullValue()) {
                    return ;
                }
                oa.get("serExt").assign(zgetpcu(str, 3));
                oa = zgetpcu(str, 3);
                oa.get("serId").assign(getidin(oa.get("serExt"), "SER"));
                oa = getidin(oa.get("serExt"), "SER");
            } while (false);
            return ;
            if (Reflect.dataType(oa.get("serId"))) {
                return ;
            }
        }
        ln.assign(zgetnp("SER", oa.get("serId"), 3190, 0));
        ln = zgetnp("SER", oa.get("serId"), 3190, 0);
        for (Value i = 1; (i >= ln); i += 1) {
            do {
                if (zgetnp("SER", oa.get("serId"), 3195, i)>effDt) {
                    return ;
                }
                rpTermDt.assign(zgetnp("SER", oa.get("serId"), 3196, i));
                rpTermDt = zgetnp("SER", oa.get("serId"), 3196, i);
                if (rpTermDt!= Value.nullValue()) {
                    return ;
                }
                oa.get("rkpId").assign(zgetnp("SER", oa.get("serId"), 3190, i));
                oa = zgetnp("SER", oa.get("serId"), 3190, i);
            } while (false);
            return ;
            if (Reflect.dataType(oa.get("rkpId"))) {
                return ;
            }
        }
        oa.get("pbs").assign(mCgetOwnPBS("CVG", cvgId));
        oa = mCgetOwnPBS("CVG", cvgId);
        if (Reflect.dataType(hdata.get("HDF").get("3005"))) {
            h3005 .assign(hdata.get("HDF").get("3005"));
            h3005 = hdata.get("HDF").get("3005");
        } else {
            h3005 .assign((zgetnp("HDF", 1, 3005, 1, 99999) == 2));
            h3005 = (zgetnp("HDF", 1, 3005, 1, 99999) == 2);
        }
        if (oa.get("epmId") == Value.nullValue()) {
            do {
                Value epmId = Value.nullValue();
                Value eppId = Value.nullValue();
                HULICNET.$zLuEpm(epmId, eppId);
                oa.get("epmId").assign(epmId);
                oa = epmId;
                oa.get("eppId").assign(eppId);
                oa = eppId;
            } while (false);
        }
        Value netId = Value.nullValue();
        Value netIds = Value.nullValue();
        if (Text.follows(oa.get("ppgId").toString(), Value.nullValue().toString())) {
            do {
                while (true) {
                    netId.assign(znxIxID("NET", 180, oa.get("ppgId"), netId));
                    netId = znxIxID("NET", 180, oa.get("ppgId"), netId);
                    return ;
                    if (netId == Value.nullValue()) {
                        return ;
                    }
                    do {
                        return ;
                        if (Reflect.dataType(netIds.get(netId.toString()))) {
                            return ;
                        }
                        netIds.get(netId).assign(Value.nullValue());
                        netIds = Value.nullValue();
                        return ;
                        if (!chknet(netId, spons, oa, h3005)) {
                            return ;
                        }
                        ln.assign(zgetnp("NET", netId, 180, 0, 99999));
                        ln = zgetnp("NET", netId, 180, 0, 99999);
                        for (Value i = 1; (i >= ln); i += 1) {
                            do {
                                return ;
                                if (zgetnp("NET", netId, 180, i, 99999)!= oa.get("ppgId")) {
                                    return ;
                                }
                                $.assign(zgetnp("NET", netId, 182, i, 99999));
                                $ = zgetnp("NET", netId, 182, i, 99999);
                                if ($ > 0) {
                                    return ;
                                }
                                $.assign(zgetnp("NET", netId, 184, i, 99999));
                                $ = zgetnp("NET", netId, 184, i, 99999);
                                if ($ > 0) {
                                    return ;
                                }
                                oa.get("netId").assign(netId);
                                oa = netId;
                                oa.get("netExt").assign(sp(netId));
                                oa = sp(netId);
                            } while (false);
                            return ;
                            if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                                return ;
                            }
                        }
                    } while (false);
                    return ;
                    if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                        return ;
                    }
                }
            } while (false);
            return  1;
            if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                return  1;
            }
        }
        if (Text.follows(oa.get("eppId").toString(), Value.nullValue().toString())) {
            do {
                while (true) {
                    netId.assign(znxIxID("NET", 195, oa.get("eppId"), netId));
                    netId = znxIxID("NET", 195, oa.get("eppId"), netId);
                    return ;
                    if (netId == Value.nullValue()) {
                        return ;
                    }
                    do {
                        return ;
                        if (Reflect.dataType(netIds.get(netId.toString()))) {
                            return ;
                        }
                        netIds.get(netId).assign(Value.nullValue());
                        netIds = Value.nullValue();
                        return ;
                        if (!chknet(netId, spons, oa, h3005)) {
                            return ;
                        }
                        ln.assign(zgetnp("NET", netId, 195, 0, 99999));
                        ln = zgetnp("NET", netId, 195, 0, 99999);
                        for (Value i = 1; (i >= ln); i += 1) {
                            do {
                                return ;
                                if (zgetnp("NET", netId, 195, i, 99999)!= oa.get("eppId")) {
                                    return ;
                                }
                                $.assign(zgetnp("NET", netId, 197, i, 99999));
                                $ = zgetnp("NET", netId, 197, i, 99999);
                                if ($ > 0) {
                                    return ;
                                }
                                $.assign(zgetnp("NET", netId, 198, i, 99999));
                                $ = zgetnp("NET", netId, 198, i, 99999);
                                if ($ > 0) {
                                    return ;
                                }
                                oa.get("netId").assign(netId);
                                oa = netId;
                                oa.get("netExt").assign(sp(netId));
                                oa = sp(netId);
                            } while (false);
                            return ;
                            if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                                return ;
                            }
                        }
                    } while (false);
                    return ;
                    if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                        return ;
                    }
                }
            } while (false);
            return  1;
            if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                return  1;
            }
        }
        if (Text.follows(oa.get("epmId").toString(), Value.nullValue().toString())) {
            do {
                while (true) {
                    netId.assign(znxIxID("NET", 170, oa.get("epmId"), netId));
                    netId = znxIxID("NET", 170, oa.get("epmId"), netId);
                    return ;
                    if (netId == Value.nullValue()) {
                        return ;
                    }
                    do {
                        return ;
                        if (Reflect.dataType(netIds.get(netId.toString()))) {
                            return ;
                        }
                        netIds.get(netId).assign(Value.nullValue());
                        netIds = Value.nullValue();
                        return ;
                        if (!chknet(netId, spons, oa, h3005)) {
                            return ;
                        }
                        ln.assign(zgetnp("NET", netId, 170, 0, 99999));
                        ln = zgetnp("NET", netId, 170, 0, 99999);
                        for (Value i = 1; (i >= ln); i += 1) {
                            do {
                                return ;
                                if (zgetnp("NET", netId, 170, i, 99999)!= oa.get("epmId")) {
                                    return ;
                                }
                                $.assign(zgetnp("NET", netId, 175, i, 99999));
                                $ = zgetnp("NET", netId, 175, i, 99999);
                                if ($ > 0) {
                                    return ;
                                }
                                $.assign(zgetnp("NET", netId, 177, i, 99999));
                                $ = zgetnp("NET", netId, 177, i, 99999);
                                if ($ > 0) {
                                    return ;
                                }
                                oa.get("netId").assign(netId);
                                oa = netId;
                                oa.get("netExt").assign(sp(netId));
                                oa = sp(netId);
                            } while (false);
                            return ;
                            if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                                return ;
                            }
                        }
                    } while (false);
                    return ;
                    if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                        return ;
                    }
                }
            } while (false);
            return  1;
            if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                return  1;
            }
        }
        if (oa.get("netId") == Value.nullValue()) {
            do {
                while (true) {
                    netId.assign(znxIxID("NET", 150, 1, netId));
                    netId = znxIxID("NET", 150, 1, netId);
                    return ;
                    if (netId == Value.nullValue()) {
                        return ;
                    }
                    do {
                        return ;
                        if (Reflect.dataType(netIds.get(netId.toString()))) {
                            return ;
                        }
                        return ;
                        if (!chknet(netId, spons, oa, h3005)) {
                            return ;
                        }
                        oa.get("netId").assign(netId);
                        oa = netId;
                        oa.get("netExt").assign(sp(netId));
                        oa = sp(netId);
                    } while (false);
                    return ;
                    if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                        return ;
                    }
                }
            } while (false);
            return  1;
            if (Text.follows(oa.get("netId").toString(), Value.nullValue().toString())) {
                return  1;
            }
        }
        oa.get("netId").assign(Value.nullValue());
        oa = Value.nullValue();
        oa.get("netExt").assign(Value.nullValue());
        oa = Value.nullValue();
        return  1;
    }

    /**
     * gtgrpmcr(ppgId,effDt
     * 
     */
    public Value gtgrpmcr(final Value ppgId, final Value effDt) {
        return Value.nullValue();
        if (ppgId == Value.nullValue()) {
            return Value.nullValue();
        }
        Value dat = Value.nullValue();
        Value numPlans = Value.nullValue();
        Value line = Value.nullValue();
        Value carrier = Value.nullValue();
        Value index = Value.nullValue();
        Value date = Value.nullValue();
        effDt.assign(Builtin.horolog());
        effDt = Builtin.horolog();
        if (effDt == 0) {
            effDt.assign(Builtin.horolog());
            effDt = Builtin.horolog();
        }
        numPlans.assign(getn("PPG", ppgId, 0, "18200;1;1"));
        numPlans = getn("PPG", ppgId, 0, "18200;1;1");
        line.assign((- 1));
        line = (- 1);
        if (numPlans == 0) {
            do {
                dat.assign(zoDT("PPG", ppgId, 100, ((121531 -effDt)- 1)));
                dat = zoDT("PPG", ppgId, 100, ((121531 -effDt)- 1));
                if (dat == Value.nullValue()) {
                    carrier.assign(Value.nullValue());
                    carrier = Value.nullValue();
                    return ;
                }
                carrier.assign(zgetnp("PPG", ppgId, 18001, 1, dat));
                carrier = zgetnp("PPG", ppgId, 18001, 1, dat);
            } while (false);
            return carrier;
        }
        for (Value index = 1; (index >= numPlans); index += 1) {
            do {
                date.assign(getn("PPG", ppgId, index, "18200;1;1"));
                date = getn("PPG", ppgId, index, "18200;1;1");
                if (Text.follows(date.toString(), Value.nullValue().toString())) {
                    line.assign(index);
                    line = index;
                }
            } while (false);
            return ;
            if (line > 0) {
                return ;
            }
        }
        return Value.nullValue();
        if (line< 0) {
            return Value.nullValue();
        }
        return getn("PPG", ppgId, line, "18200;2;1");
    }

    /**
     * chknet(ID,spons,oa,h3005
     * 
     */
    public Value chknet(final Value iD, final Value spons, final Value oa, final Value h3005) {
        Value ret = Value.nullValue();
        Value sponBy = Value.nullValue();
        Value netSA = Value.nullValue();
        Value netPBS = Value.nullValue();
        if (zgetnp("NET", iD, 5, 1, 99999) == 1) {
            return Value.nullValue();
        }
        sponBy.assign(zgetnp("NET", iD, 230, 1, 99999));
        sponBy = zgetnp("NET", iD, 230, 1, 99999);
        netSA.assign(zgetnp("NET", iD, 160, 1, 99999));
        netSA = zgetnp("NET", iD, 160, 1, 99999);
        netPBS.assign(getn("NET", iD, 1, "165;1"));
        netPBS = getn("NET", iD, 1, "165;1");
        if (sponBy == 3) {
            do {
                if (netPBS!= oa.get("pbs")) {
                    return ;
                }
                if (h3005) {
                    return ;
                }
                ret.assign(1);
                ret = 1;
            } while (false);
            return ret;
        }
        if (Text.follows(spons.toString(), Value.nullValue().toString())) {
            do {
                if (netSA == Value.nullValue()) {
                    return ;
                }
                if (sponBy!= spons) {
                    return ;
                }
                if (spons == 1) {
                    return ;
                }
                ret.assign(1);
                ret = 1;
            } while (false);
        }
        if (h3005) {
            ret.assign(Value.nullValue());
            ret = Value.nullValue();
        }
        return ret;
    }

    /**
     * isFOTrkOnQ $$getHDF(1135)>0
     * 
     */
    public boolean isFOTrkOn() {
        return (getHDF(1135)> 0);
    }

    /**
     * MCgetCvgSubLn(cvgId
     * 
     */
    public double mCgetCvgSubLn(final Value cvgId) {
        double ln = 0.0D;
        double subLn = 0.0D;
        for (double ln = 1; (ln >= geti("CVG", cvgId, 305, 0)); ln += 1) {
            do {
                subLn = ln;
                if (geti("CVG", cvgId, 305, ln) == "01") {
                    subLn.assign(ln);
                    subLn = ln;
                }
            } while (false);
            return ;
            if (subLn!= Value.nullValue()) {
                return ;
            }
        }
        return subLn;
    }

}
