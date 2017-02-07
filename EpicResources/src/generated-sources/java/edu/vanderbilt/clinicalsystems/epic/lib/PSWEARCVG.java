
package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.VariableContext;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.lib.Builtin;
import edu.vanderbilt.clinicalsystems.m.core.lib.Reflect;

public class PSWEARCVG {

    @InjectRoutine
    public Value $;

    /**
     * PSWEARCVG; ; ;2015-07-03 15:38:51;8.2;vUOl+HKd5WtS96FuoywLMAY5FfJtU2XCzAVciOS2HFvth5lijD1PVfr+z7OqV4gZ
     * Q 
     * 
     */
    public void main() {
        return ;
    }

    /**
     * GetGuarsAndCvgs2;
     * D GetGuarsAndCvgs(1)
     * Q 
     * 
     */
    public void getGuarsAndCvgs2() {
        getGuarsAndCvgs(1);
        return ;
    }

    /**
     * GetGuarsAndCvgs(ShowInactive
     * 
     */
    public void getGuarsAndCvgs(final Value showInactive) {
        Value id = Value.nullValue();
        Value inclInactEAR = Value.nullValue();
        Value type = Value.nullValue();
        Value ret = Value.nullValue();
        String ln = "";
        String ln2 = "";
        double newLn = 0.0D;
        Value pGlo = Value.nullValue();
        Value eptID = Value.nullValue();
        Value empID = Value.nullValue();
        Value depID = Value.nullValue();
        Value eclID = Value.nullValue();
        Value locID = Value.nullValue();
        Value saID = Value.nullValue();
        Value dte = Value.nullValue();
        Value preCvgList = Value.nullValue();
        Value cvgList = Value.nullValue();
        Value allowInactive = Value.nullValue();
        Value cvgID = Value.nullValue();
        String cvgLn = "";
        Value earCount = Value.nullValue();
        Value earID = Value.nullValue();
        Value earLn = Value.nullValue();
        double earNum = 0.0D;
        Value cvgElement = Value.nullValue();
        Value cvgElmObject = Value.nullValue();
        Value earElement = Value.nullValue();
        Value earElmObject = Value.nullValue();
        Value idElement = Value.nullValue();
        Value idElmObject = Value.nullValue();
        String desLn = "";
        Value descriptionElmt = Value.nullValue();
        long endDte = 0;
        if (!isLicPrelude()) {
            $.assign(zECFThrow("NOT-LICENSED", "Prelude is not licensed"));
            $ = zECFThrow("NOT-LICENSED", "Prelude is not licensed");
            return ;
        }
        id.assign(zECFGet("PatientID", Value.nullValue()));
        id = zECFGet("PatientID", Value.nullValue());
        if (id == Value.nullValue()) {
            $.assign(zECFThrow("NO-PATIENT-ID", "Patient ID not provided"));
            $ = zECFThrow("NO-PATIENT-ID", "Patient ID not provided");
            return ;
        }
        type.assign(zECFGet("PatientIDType", Value.nullValue()));
        type = zECFGet("PatientIDType", Value.nullValue());
        eptID.assign(EGENETCMN.getRecordId("EPT", id, type));
        eptID = EGENETCMN.getRecordId("EPT", id, type);
        if (eptID == Value.nullValue()) {
            $.assign(zECFThrow("NO-PATIENT-FOUND", "Patient could not be determined from the ID and ID-type provided"));
            $ = zECFThrow("NO-PATIENT-FOUND", "Patient could not be determined from the ID and ID-type provided");
            return ;
        }
        id.assign(zECFGet("UserID", Value.nullValue()));
        id = zECFGet("UserID", Value.nullValue());
        if (id == Value.nullValue()) {
            $.assign(zECFThrow("NO-USER-ID", "User ID not provided"));
            $ = zECFThrow("NO-USER-ID", "User ID not provided");
            return ;
        }
        type.assign(zECFGet("UserIDType", Value.nullValue()));
        type = zECFGet("UserIDType", Value.nullValue());
        empID.assign(EGENETCMN.getRecordId("EMP", id, type));
        empID = EGENETCMN.getRecordId("EMP", id, type);
        if (empID == Value.nullValue()) {
            $.assign(zECFThrow("NO-USER-FOUND", "User could not be determined from the ID and ID-type provided"));
            $ = zECFThrow("NO-USER-FOUND", "User could not be determined from the ID and ID-type provided");
            return ;
        }
        id.assign(zECFGet("DepartmentID", Value.nullValue()));
        id = zECFGet("DepartmentID", Value.nullValue());
        if (id == Value.nullValue()) {
            $.assign(zECFThrow("NO-DEPARTMENT-ID", "Department ID not provided"));
            $ = zECFThrow("NO-DEPARTMENT-ID", "Department ID not provided");
            return ;
        }
        type.assign(zECFGet("DepartmentIDType", Value.nullValue()));
        type = zECFGet("DepartmentIDType", Value.nullValue());
        depID.assign(EGENETCMN.getRecordId("DEP", id, type));
        depID = EGENETCMN.getRecordId("DEP", id, type);
        if (depID == Value.nullValue()) {
            $.assign(zECFThrow("NO-DEPARTMENT-FOUND", "Department could not be determined from the ID and ID-type provided"));
            $ = zECFThrow("NO-DEPARTMENT-FOUND", "Department could not be determined from the ID and ID-type provided");
            return ;
        }
        saID.assign(dEP2SA(depID, locID));
        saID = dEP2SA(depID, locID);
        eclID.assign(eprECL(depID, empID));
        eclID = eprECL(depID, empID);
        if (dte == Value.nullValue()) {
            dte.assign(Builtin.horolog());
            dte = Builtin.horolog();
            endDte = 99999;
        }
        pGlo.assign(ZefnLib.acquirePrivateTempGlobal());
        pGlo = ZefnLib.acquirePrivateTempGlobal();
        allowInactive.assign(getOPR(64245, 1, Value.nullValue(), Value.nullValue(), Value.nullValue(), saID));
        allowInactive = getOPR(64245, 1, Value.nullValue(), Value.nullValue(), Value.nullValue(), saID);
        if (showInactive == 1) {
            inclInactEAR.assign(zECFGet("IncludeInactiveAccounts", Value.nullValue()));
            inclInactEAR = zECFGet("IncludeInactiveAccounts", Value.nullValue());
        }
        earCount.assign(zgetnp("EPT", eptID, 2200, 0, 99999));
        earCount = zgetnp("EPT", eptID, 2200, 0, 99999);
        for (Value earLn = 1; (earLn >= earCount); earLn += 1) {
            do {
                earID.assign(zgetnp("EPT", eptID, 2200, earLn, 99999));
                earID = zgetnp("EPT", eptID, 2200, earLn, 99999);
                return ;
                if (!allowedEAR(eptID, dte, earID, earLn, saID, eclID, allowInactive, preCvgList, endDte, inclInactEAR)) {
                    return ;
                }
                cvgList.clear();
                cvgList = Value.nullValue();
                newLn = 0.0D;
                earNum = (earNum + 1);
                getEarData(eptID, earID, earNum, ret);
                for (String ln = 1; (ln >= preCvgList.get("0")); ln += 1) {
                    do {
                        if (!restCvg(preCvgList.get(ln), 90, Value.nullValue(), eclID, saID, locID, empID)) {
                            newLn = (newLn + 1);
                            cvgList.get(newLn).assign(preCvgList.get(ln));
                            cvgList = preCvgList.get(ln);
                        }
                    } while (false);
                }
                cvgList.get("0").assign(newLn);
                cvgList = newLn;
                for (String cvgLn = 1; (cvgLn >= cvgList.get("0")); cvgLn += 1) {
                    do {
                        cvgID.assign(cvgList.get(cvgLn));
                        cvgID = cvgList.get(cvgLn);
                        getCvgData(eptID, dte, earNum, cvgID, cvgLn, pGlo, ret);
                    } while (false);
                }
                ret.get("EAR").get(earNum).get("CVG").get("0").assign(cvgList.get("0"));
                ret = cvgList.get("0");
            } while (false);
        }
        ret.get("EAR").get("0").assign(earNum);
        ret = earNum;
        ZefnLib.releasePrivateTempGlobal(pGlo.toString());
        earElement.assign(zECFNew("Guarantors", Value.nullValue(), "A"));
        earElement = zECFNew("Guarantors", Value.nullValue(), "A");
        for (String ln = 1; (ln >= ret.get("EAR").get("0")); ln += 1) {
            do {
                earElmObject.assign(zECFNewElmtObj(earElement));
                earElmObject = zECFNewElmtObj(earElement);
                $.assign(zECFSet("DisplayName", ret.get("EAR").get(ln).get("DisplayName"), earElmObject));
                $ = zECFSet("DisplayName", ret.get("EAR").get(ln).get("DisplayName"), earElmObject);
                $.assign(zECFSet("Type", ret.get("EAR").get(ln).get("Type"), earElmObject));
                $ = zECFSet("Type", ret.get("EAR").get(ln).get("Type"), earElmObject);
                $.assign(zECFSet("RelationshipToPatient", ret.get("EAR").get(ln).get("RelationshipToPatient"), earElmObject));
                $ = zECFSet("RelationshipToPatient", ret.get("EAR").get(ln).get("RelationshipToPatient"), earElmObject);
                $.assign(zECFSet("Comment", ret.get("EAR").get(ln).get("Comment"), earElmObject));
                $ = zECFSet("Comment", ret.get("EAR").get(ln).get("Comment"), earElmObject);
                $.assign(zECFSet("Expiration", ret.get("EAR").get(ln).get("Expiration"), earElmObject));
                $ = zECFSet("Expiration", ret.get("EAR").get(ln).get("Expiration"), earElmObject);
                if (showInactive == 1) {
                    $.assign(zECFSet("Inactive", ret.get("EAR").get(ln).get("Inactive"), earElmObject));
                    $ = zECFSet("Inactive", ret.get("EAR").get(ln).get("Inactive"), earElmObject);
                }
                idElement.assign(zECFNew("ID", earElmObject, "A"));
                idElement = zECFNew("ID", earElmObject, "A");
                idElmObject.assign(zECFNewElmtObj(idElement));
                idElmObject = zECFNewElmtObj(idElement);
                $.assign(zECFSet("ID", ret.get("EAR").get(ln).get("ID"), idElmObject));
                $ = zECFSet("ID", ret.get("EAR").get(ln).get("ID"), idElmObject);
                $.assign(zECFSet("Type", "Internal", idElmObject));
                $ = zECFSet("Type", "Internal", idElmObject);
                if (ret.get("EAR").get(ln).get("CID")!= Value.nullValue()) {
                    do {
                        idElmObject.assign(zECFNewElmtObj(idElement));
                        idElmObject = zECFNewElmtObj(idElement);
                        $.assign(zECFSet("ID", ret.get("EAR").get(ln).get("CID"), idElmObject));
                        $ = zECFSet("ID", ret.get("EAR").get(ln).get("CID"), idElmObject);
                        $.assign(zECFSet("Type", "CID", idElmObject));
                        $ = zECFSet("Type", "CID", idElmObject);
                    } while (false);
                }
                cvgElement.assign(zECFNew("Coverages", earElmObject, "A"));
                cvgElement = zECFNew("Coverages", earElmObject, "A");
                for (String ln2 = 1; (ln2 >= ret.get("EAR").get(ln).get("CVG").get("0")); ln2 += 1) {
                    do {
                        cvgElmObject.assign(zECFNewElmtObj(cvgElement));
                        cvgElmObject = zECFNewElmtObj(cvgElement);
                        $.assign(zECFSet("GroupNumber", ret.get("EAR").get(ln).get("CVG").get(ln2).get("GroupNumber"), cvgElmObject));
                        $ = zECFSet("GroupNumber", ret.get("EAR").get(ln).get("CVG").get(ln2).get("GroupNumber"), cvgElmObject);
                        $.assign(zECFSet("GroupName", ret.get("EAR").get(ln).get("CVG").get(ln2).get("GroupName"), cvgElmObject));
                        $ = zECFSet("GroupName", ret.get("EAR").get(ln).get("CVG").get(ln2).get("GroupName"), cvgElmObject);
                        $.assign(zECFSet("MedicareCoverageType", ret.get("EAR").get(ln).get("CVG").get(ln2).get("MedicareCoverageType"), cvgElmObject));
                        $ = zECFSet("MedicareCoverageType", ret.get("EAR").get(ln).get("CVG").get(ln2).get("MedicareCoverageType"), cvgElmObject);
                        $.assign(zECFSet("EffectiveFromDate", ret.get("EAR").get(ln).get("CVG").get(ln2).get("EffectiveFromDate"), cvgElmObject));
                        $ = zECFSet("EffectiveFromDate", ret.get("EAR").get(ln).get("CVG").get(ln2).get("EffectiveFromDate"), cvgElmObject);
                        $.assign(zECFSet("EffectiveToDate", ret.get("EAR").get(ln).get("CVG").get(ln2).get("EffectiveToDate"), cvgElmObject));
                        $ = zECFSet("EffectiveToDate", ret.get("EAR").get(ln).get("CVG").get(ln2).get("EffectiveToDate"), cvgElmObject);
                        $.assign(zECFSet("PayorName", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PayorName"), cvgElmObject));
                        $ = zECFSet("PayorName", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PayorName"), cvgElmObject);
                        $.assign(zECFSet("PlanName", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PlanName"), cvgElmObject));
                        $ = zECFSet("PlanName", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PlanName"), cvgElmObject);
                        $.assign(zECFSet("Subscriber", ret.get("EAR").get(ln).get("CVG").get(ln2).get("Subscriber"), cvgElmObject));
                        $ = zECFSet("Subscriber", ret.get("EAR").get(ln).get("CVG").get(ln2).get("Subscriber"), cvgElmObject);
                        $.assign(zECFSet("BenefitCode", ret.get("EAR").get(ln).get("CVG").get(ln2).get("BenefitCode"), cvgElmObject));
                        $ = zECFSet("BenefitCode", ret.get("EAR").get(ln).get("CVG").get(ln2).get("BenefitCode"), cvgElmObject);
                        $.assign(zECFSet("Status", ret.get("EAR").get(ln).get("CVG").get(ln2).get("Status"), cvgElmObject));
                        $ = zECFSet("Status", ret.get("EAR").get(ln).get("CVG").get(ln2).get("Status"), cvgElmObject);
                        descriptionElmt.assign(zECFNew("Description", cvgElmObject, "A"));
                        descriptionElmt = zECFNew("Description", cvgElmObject, "A");
                        for (String desLn = 1; (desLn >= ret.get("EAR").get(ln).get("CVG").get(ln2).get("Description").get("desCount")); desLn += 1) {
                            do {
                                $.assign(zECFSetElmt(descriptionElmt, ret.get("EAR").get(ln).get("CVG").get(ln2).get("Description").get(desLn)));
                                $ = zECFSetElmt(descriptionElmt, ret.get("EAR").get(ln).get("CVG").get(ln2).get("Description").get(desLn));
                            } while (false);
                        }
                        idElement.assign(zECFNew("ID", cvgElmObject, "A"));
                        idElement = zECFNew("ID", cvgElmObject, "A");
                        idElmObject.assign(zECFNewElmtObj(idElement));
                        idElmObject = zECFNewElmtObj(idElement);
                        $.assign(zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("ID"), idElmObject));
                        $ = zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("ID"), idElmObject);
                        $.assign(zECFSet("Type", "Internal", idElmObject));
                        $ = zECFSet("Type", "Internal", idElmObject);
                        if (ret.get("EAR").get(ln).get("CVG").get(ln2).get("CID")!= Value.nullValue()) {
                            do {
                                idElmObject.assign(zECFNewElmtObj(idElement));
                                idElmObject = zECFNewElmtObj(idElement);
                                $.assign(zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("CID"), idElmObject));
                                $ = zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("CID"), idElmObject);
                                $.assign(zECFSet("Type", "CID", idElmObject));
                                $ = zECFSet("Type", "CID", idElmObject);
                            } while (false);
                        }
                        idElement.assign(zECFNew("PayorID", cvgElmObject, "A"));
                        idElement = zECFNew("PayorID", cvgElmObject, "A");
                        idElmObject.assign(zECFNewElmtObj(idElement));
                        idElmObject = zECFNewElmtObj(idElement);
                        $.assign(zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PayorID"), idElmObject));
                        $ = zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PayorID"), idElmObject);
                        $.assign(zECFSet("Type", "Internal", idElmObject));
                        $ = zECFSet("Type", "Internal", idElmObject);
                        if (ret.get("EAR").get(ln).get("CVG").get(ln2).get("PayorCID")!= Value.nullValue()) {
                            do {
                                idElmObject.assign(zECFNewElmtObj(idElement));
                                idElmObject = zECFNewElmtObj(idElement);
                                $.assign(zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PayorCID"), idElmObject));
                                $ = zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PayorCID"), idElmObject);
                                $.assign(zECFSet("Type", "CID", idElmObject));
                                $ = zECFSet("Type", "CID", idElmObject);
                            } while (false);
                        }
                        idElement.assign(zECFNew("PlanID", cvgElmObject, "A"));
                        idElement = zECFNew("PlanID", cvgElmObject, "A");
                        idElmObject.assign(zECFNewElmtObj(idElement));
                        idElmObject = zECFNewElmtObj(idElement);
                        $.assign(zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PlanID"), idElmObject));
                        $ = zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PlanID"), idElmObject);
                        $.assign(zECFSet("Type", "Internal", idElmObject));
                        $ = zECFSet("Type", "Internal", idElmObject);
                        if (ret.get("EAR").get(ln).get("CVG").get(ln2).get("PlanCID")!= Value.nullValue()) {
                            do {
                                idElmObject.assign(zECFNewElmtObj(idElement));
                                idElmObject = zECFNewElmtObj(idElement);
                                $.assign(zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PlanCID"), idElmObject));
                                $ = zECFSet("ID", ret.get("EAR").get(ln).get("CVG").get(ln2).get("PlanCID"), idElmObject);
                                $.assign(zECFSet("Type", "CID", idElmObject));
                                $ = zECFSet("Type", "CID", idElmObject);
                            } while (false);
                        }
                    } while (false);
                }
            } while (false);
        }
        return ;
    }

    /**
     * AllowedEAR(eptID,dte,earID,earLn,saID,eclID,allowInactive,cvgList,endDte,inclInactEAR
     * 
     */
    public long allowedEAR(final Value eptID, final Value dte, final Value earID, final Value earLn, final Value saID, final Value eclID, final Value allowInactive, final Value cvgList, final Value endDte, final Value inclInactEAR) {
        long inactFlag = 0;
        Value allCvgList = Value.nullValue();
        String cvgLn = "";
        double count = 0.0D;
        Value bType = Value.nullValue();
        long inactEAR = 0;
        cvgList.clear();
        cvgList = Value.nullValue();
        return  0;
        if (zgetnp("EAR", earID, 65, 1, 99999)!= saID) {
            return  0;
        }
        if ((zgetnp("EAR", earID, 55, 1, 99999) == 2)||(zgetnp("EPT", eptID, 2210, earLn, 99999) == 2)) {
            inactEAR = 1;
        }
        return  0;
        if ((!inclInactEAR)&&inactEAR) {
            return  0;
        }
        return  0;
        if (restrictAcct(earID, Value.nullValue(), saID, 4, eclID)) {
            return  0;
        }
        bType.assign(2);
        bType = 2;
        if (getOPRpc(61250, 1, saID)) {
            bType.assign(2);
            bType = 2;
        }
        allCvgList.get("0").assign(fncvgLst(getidout(eptID, "EPT"), earID, allCvgList, dte, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), 1, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), bType, Value.nullValue(), endDte));
        allCvgList = fncvgLst(getidout(eptID, "EPT"), earID, allCvgList, dte, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), 1, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), bType, Value.nullValue(), endDte);
        for (String cvgLn = 1; (cvgLn >= allCvgList.get("0")); cvgLn += 1) {
            do {
                if (PRUTIL1 .inactCvg(allCvgList.get(cvgLn), Value.nullValue(), dte)) {
                    inactFlag = 1;
                    return ;
                }
                count = (count + 1);
                cvgList.get(count).assign(allCvgList.get(cvgLn));
                cvgList = allCvgList.get(cvgLn);
            } while (false);
        }
        cvgList.get("0").assign(count);
        cvgList = count;
        if (allowInactive == 3) {
            return  0;
        }
        if (allowInactive == 4) {
            return  0;
        }
        return  0;
        if (inactEAR&&(cvgList.get("0")< 1)) {
            return  0;
        }
        return  1;
    }

    /**
     * getEarData(eptID,earID,earNum,ret
     * 
     */
    public void getEarData(final Value eptID, final Value earID, final Value earNum, final Value ret) {
        Value ln = Value.nullValue();
        long found = 0;
        ret.get("EAR").get(earNum).get("ID").assign(earID);
        ret = earID;
        ret.get("EAR").get(earNum).get("CID").assign(zGtCID("EAR", earID));
        ret = zGtCID("EAR", earID);
        ret.get("EAR").get(earNum).get("DisplayName").assign(zConfNam("EAR", earID));
        ret = zConfNam("EAR", earID);
        ret.get("EAR").get(earNum).get("Type").assign(zgetCatC(zgetnp("EAR", earID, 210, 1, 99999), 1, "EAR", 210));
        ret = zgetCatC(zgetnp("EAR", earID, 210, 1, 99999), 1, "EAR", 210);
        ret.get("EAR").get(earNum).get("Expiration").assign(zFmtDate(zgetnp("EAR", earID, 217, 1, 99999)));
        ret = zFmtDate(zgetnp("EAR", earID, 217, 1, 99999));
        for (Value ln = 1; (ln >= zgetnp("EPT", eptID, 2200, 0, 99999)); ln += 1) {
            do {
                if (zgetnp("EPT", eptID, 2200, ln, 99999) == earID) {
                    do {
                        found = 1;
                        ret.get("EAR").get(earNum).get("RelationshipToPatient").assign(zgetCatC(zgetnp("EPT", eptID, 2225, ln, 99999), 1, "EAR", 255));
                        ret = zgetCatC(zgetnp("EPT", eptID, 2225, ln, 99999), 1, "EAR", 255);
                        ret.get("EAR").get(earNum).get("Comment").assign(zgetnp("EPT", eptID, 2240, ln, 99999));
                        ret = zgetnp("EPT", eptID, 2240, ln, 99999);
                        if ((zgetnp("EAR", earID, 55, 1, 99999) == 2)||(zgetnp("EPT", eptID, 2210, ln, 99999) == 2)) {
                            ret.get("EAR").get(earNum).get("Inactive").assign(1);
                            ret = 1;
                        } else {
                            ret.get("EAR").get(earNum).get("Inactive").assign(0);
                            ret = 0;
                        }
                    } while (false);
                }
            } while (false);
            return ;
            if (found) {
                return ;
            }
        }
        return ;
    }

    /**
     * getCvgData(eptID,dte,earNum,cvgID,cvgNum,pGlo,ret
     * 
     */
    public void getCvgData(final Value eptID, final Value dte, final String earNum, final Value cvgID, final String cvgNum, final Value pGlo, final Value ret) {
        Value cvgData = Value.nullValue();
        Value oa = Value.nullValue();
        Value fromDate = Value.nullValue();
        Value toDate = Value.nullValue();
        Value eptExtID = Value.nullValue();
        Value desLn = Value.nullValue();
        Value isMember = Value.nullValue();
        Value dateArr = Value.nullValue();
        if (pGlo == Value.nullValue()) {
            return ;
        }
        eptExtID.assign(getidout(eptID, "EPT"));
        eptExtID = getidout(eptID, "EPT");
        if (Reflect.dataType(VariableContext.lookup(pGlo.toString()).get(cvgID)) == 0) {
            do {
                cvgData.get("ID").assign(cvgID);
                cvgData = cvgID;
                cvgData.get("CID").assign(zGtCID("CVG", cvgID));
                cvgData = zGtCID("CVG", cvgID);
                cvgData.get("PlanID").assign(fngtPln(cvgID, dte, oa, eptID));
                cvgData = fngtPln(cvgID, dte, oa, eptID);
                cvgData.get("PlanCID").assign(zGtCID("EPP", cvgData.get("PlanID")));
                cvgData = zGtCID("EPP", cvgData.get("PlanID"));
                cvgData.get("PayorID").assign(oa.get("epmId"));
                cvgData = oa.get("epmId");
                cvgData.get("PayorCID").assign(zGtCID("EPM", cvgData.get("PayorID")));
                cvgData = zGtCID("EPM", cvgData.get("PayorID"));
                cvgData.get("GroupNumber").assign(zgetnp("CVG", cvgID, 210, 1, 99999));
                cvgData = zgetnp("CVG", cvgID, 210, 1, 99999);
                cvgData.get("GroupName").assign(zgetnp("CVG", cvgID, 215, 1, 99999));
                cvgData = zgetnp("CVG", cvgID, 215, 1, 99999);
                cvgData.get("MedicareCoverageType").assign(zgetCatC(zgetnp("CVG", cvgID, 2015, 1, 99999), 1, "CVG", 2015));
                cvgData = zgetCatC(zgetnp("CVG", cvgID, 2015, 1, 99999), 1, "CVG", 2015);
                cvgData.get("PayorName").assign(znam("EPM", oa.get("epmId")));
                cvgData = znam("EPM", oa.get("epmId"));
                cvgData.get("PlanName").assign(znam("EPP", oa.get("eppId")));
                cvgData = znam("EPP", oa.get("eppId"));
                cvgData.get("Subscriber").assign(zConfNam("CVG", cvgID));
                cvgData = zConfNam("CVG", cvgID);
                cvgData.get("BenefitCode").assign(zgetnp("CVG", cvgID, 212, 1, 99999));
                cvgData = zgetnp("CVG", cvgID, 212, 1, 99999);
                cvgData.get("Status").assign(zgetCatC(gtVerfSt(cvgID, dte, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), eptID, (121531 -dte)), 1, "EPT", 401));
                cvgData = zgetCatC(gtVerfSt(cvgID, dte, Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), Value.nullValue(), eptID, (121531 -dte)), 1, "EPT", 401);
                if (oa.get("ppgId")!= Value.nullValue()) {
                    do {
                        cvgData.get("Description").get("desCount").assign(1);
                        cvgData = 1;
                        cvgData.get("Description").get("1").assign(znam("PPG", oa.get("ppgId")));
                        cvgData = znam("PPG", oa.get("ppgId"));
                    } while (false);
                } else {
                    do {
                        cvgData.get("Description").get("desCount").assign(zgetnp("CVG", cvgID, 30, 0, 99999));
                        cvgData = zgetnp("CVG", cvgID, 30, 0, 99999);
                        for (Value desLn = 1; (desLn >= cvgData.get("Description").get("desCount")); desLn += 1) {
                            do {
                                cvgData.get("Description").get(desLn).assign(zgetnp("CVG", cvgID, 30, desLn, 99999));
                                cvgData = zgetnp("CVG", cvgID, 30, desLn, 99999);
                            } while (false);
                        }
                    } while (false);
                }
                isMember.assign(memDates(cvgID, eptExtID, dte, dateArr));
                isMember = memDates(cvgID, eptExtID, dte, dateArr);
                if (isMember) {
                    do {
                        fromDate.assign(dateArr.get("From"));
                        fromDate = dateArr.get("From");
                        toDate.assign(dateArr.get("To"));
                        toDate = dateArr.get("To");
                    } while (false);
                }
                cvgData.get("EffectiveFromDate").assign(zFmtDate(fromDate));
                cvgData = zFmtDate(fromDate);
                cvgData.get("EffectiveToDate").assign(zFmtDate(toDate));
                cvgData = zFmtDate(toDate);
                VariableContext.lookup(pGlo.toString()).get(cvgID).assign(cvgData);
            } while (false);
        }
        ret.get("EAR").get(earNum).get("CVG").get(cvgNum).merge(VariableContext.lookup(pGlo.toString()).get(cvgID.toString()));
        return ;
    }

}
