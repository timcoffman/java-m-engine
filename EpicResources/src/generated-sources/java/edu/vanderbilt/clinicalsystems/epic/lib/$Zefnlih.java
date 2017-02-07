
package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.VariableContext;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.lib.Builtin;
import edu.vanderbilt.clinicalsystems.m.core.lib.Math;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;
import edu.vanderbilt.clinicalsystems.m.core.lib.Reflect;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;

public class $Zefnlih {

    @InjectRoutine
    public Value hFLAG;
    @InjectRoutine
    public Value $ZeOSUNQ;
    @InjectRoutine
    public Value $;
    @InjectRoutine
    public Value $etGlo;
    @InjectRoutine
    public Value $EA;
    @InjectRoutine
    public Value eTMPEPC;
    @InjectRoutine
    public long pCD;
    @InjectRoutine
    public long pCM;
    @InjectRoutine
    public Value debug;
    @InjectRoutine
    public Value eADETDEBUG;
    @InjectRoutine
    public Value $ZeOSDF;

    /**
     * %Zefnlih; ; ;2016-12-08 12:59:43;8.3;0AQaw5bSkEw21SwIKITdB1xiUPg/8Oc9xZWEhpEYGAQzbkKYVwXlbOnzL7yEZhP2
     * Q 
     * 
     */
    public void main() {
        return ;
    }

    /**
     * zPckSzQ 8192
     * 
     */
    public long zPckSz() {
        return  8192;
    }

    /**
     * zPortN port IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public long zPort() {
        Value port = Value.nullValue();
        port.assign($ZeOSUNQ.get("EPICCOMM").get("TCP").get("PORT"));
        port = $ZeOSUNQ.get("EPICCOMM").get("TCP").get("PORT");
        return  4068;
        if (port == Value.nullValue()) {
            return  4068;
        }
        return port;
    }

    /**
     * zHangTmQ ^%ZeOSUNQ("EPICCOMM","TCP","HANGTIME")
     * 
     */
    public Value zHangTm() {
        return $ZeOSUNQ.get("EPICCOMM").get("TCP").get("HANGTIME");
    }

    /**
     * zCommReadTmN timeout IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value zCommReadTm() {
        Value timeout = Value.nullValue();
        timeout.assign($ZeOSUNQ.get("EPICCOMM").get("TCP").get("READTMOUT"));
        timeout = $ZeOSUNQ.get("EPICCOMM").get("TCP").get("READTMOUT");
        return ((timeout == Value.nullValue())? 30 :((timeout<= 0)? 30 :(true?timeout:Value.nullValue())));
    }

    /**
     * zKeepAliveIntervalN value IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value zKeepAliveInterval() {
        Value value = Value.nullValue();
        value.assign($ZeOSUNQ.get("EPICCOMM").get("TCP").get("KeepAliveInterval"));
        value = $ZeOSUNQ.get("EPICCOMM").get("TCP").get("KeepAliveInterval");
        if (value == Value.nullValue()) {
            return  120;
        }
        value.assign((value/ 1));
        value = (value/ 1);
        if (value > 0) {
            value.assign(60);
            value = 60;
        }
        return ((value<= 0)? 0 :(true?value:Value.nullValue()));
    }

    /**
     * zKeepAliveGraceN value IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value zKeepAliveGrace() {
        Value value = Value.nullValue();
        value.assign($ZeOSUNQ.get("EPICCOMM").get("TCP").get("KeepAliveGrace"));
        value = $ZeOSUNQ.get("EPICCOMM").get("TCP").get("KeepAliveGrace");
        if (value == Value.nullValue()) {
            return  30;
        }
        value.assign((value/ 1));
        value = (value/ 1);
        return ((value >= 0)?value:(true? 30 :Value.nullValue()));
    }

    /**
     * zKeepAliveGraceProxyN value IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value zKeepAliveGraceProxy() {
        Value value = Value.nullValue();
        value.assign($ZeOSUNQ.get("EPICCOMM").get("TCP").get("KeepAliveGraceProxy"));
        value = $ZeOSUNQ.get("EPICCOMM").get("TCP").get("KeepAliveGraceProxy");
        if (value == Value.nullValue()) {
            return  1080;
        }
        value.assign((value/ 1));
        value = (value/ 1);
        return ((value >= 0)?value:(true? 1080 :Value.nullValue()));
    }

    /**
     * zPkTLmQ 32768
     * 
     */
    public long zPkTLm() {
        return  32768;
    }

    /**
     * zCommPersistTmQ 300
     * 
     */
    public long zCommPersistTm() {
        return  300;
    }

    /**
     * zGtTmpGlo(notCT,key,localeColl
     * 
     */
    public String zGtTmpGlo(final Value notCT, final Value key, final Value localeColl) {
        Value gloName = Value.nullValue();
        Value uniq = Value.nullValue();
        String node = "";
        Value caller = Value.nullValue();
        node = Reflect.name(eTMPEPC.get(Math.increment(eTMPEPC)));
        VariableContext.lookup(node).clear();
        return node;
    }

    /**
     * zzzGtTmpGloAllocPPG(notCT,checkSame,localeColl
     * 
     */
    public Value zzzGtTmpGloAllocPPG(final Value notCT, final boolean checkSame, final Value localeColl) {
        Value type = Value.nullValue();
        Value gSchm = Value.nullValue();
        Value baseG = Value.nullValue();
        Value global = Value.nullValue();
        Value ws = Value.nullValue();
        String error = "";
        if (notCT == 2) {
            notCT.clear();
            notCT = Value.nullValue();
        }
        if (!zCollEnvLocaleEnabled()) {
            localeColl.assign(0);
            localeColl = 0;
        }
        type.assign(zGtTmpGloType(notCT, localeColl));
        type = zGtTmpGloType(notCT, localeColl);
        if (!checkSame) {
            return $EA.get("E").get(type);
        }
        if ($EA.get("G")) {
            ws.assign(zgCurLWS());
            ws = zgCurLWS();
        }
        if (!notCT) {
            gSchm.assign("PIDBASED");
            gSchm = "PIDBASED";
        }
        gSchm.assign($ZeOSUNQ.get("PPG").get("SCHEME"));
        gSchm = $ZeOSUNQ.get("PPG").get("SCHEME");
        if (gSchm == Value.nullValue()) {
            gSchm.assign($ZeOSUNQ.get("PPG").get("SCHEME"));
            gSchm = $ZeOSUNQ.get("PPG").get("SCHEME");
        }
        gSchm.assign($ZeOSDF.get("PPG").get("SCHEME"));
        gSchm = $ZeOSDF.get("PPG").get("SCHEME");
        if (gSchm == Value.nullValue()) {
            gSchm.assign($ZeOSDF.get("PPG").get("SCHEME"));
            gSchm = $ZeOSDF.get("PPG").get("SCHEME");
        }
        if (gSchm == Value.nullValue()) {
            gSchm.assign((notCT?"PSLICED":(localeColl?"PSLICED":(zAllowPPGasDBINTERNAL()?"DBINTERNAL":(true?"PSLICED":Value.nullValue())))));
            gSchm = (notCT?"PSLICED":(localeColl?"PSLICED":(zAllowPPGasDBINTERNAL()?"DBINTERNAL":(true?"PSLICED":Value.nullValue()))));
        } else {
            if (gSchm == "DBINTERNAL") {
                gSchm.assign("PSLICED");
                gSchm = "PSLICED";
            }
        }
        if (gSchm == "DBINTERNAL") {
            global.assign(zGetPPGwhenDBINTERNAL(notCT));
            global = zGetPPGwhenDBINTERNAL(notCT);
        } else {
            do {
                baseG.assign((("^"+((notCT == 1)?"ERTMPRG":(true?"ERTEMP":Value.nullValue())))+(localeColl?"COLL":(true?Value.nullValue():Value.nullValue()))));
                baseG = (("^"+((notCT == 1)?"ERTMPRG":(true?"ERTEMP":Value.nullValue())))+(localeColl?"COLL":(true?Value.nullValue():Value.nullValue())));
                global.assign(zzzGtGloFromBase(gSchm, baseG));
                global = zzzGtGloFromBase(gSchm, baseG);
            } while (false);
        }
        if (checkSame) {
            return global;
        }
        VariableContext.lookup(global.toString()).clear();
        if (localeColl) {
            $zErrLog(("Unable to create locale collated temp global: "+ error), Value.nullValue(), 1);
        }
        $EA.get("E").get(type).assign(global);
        $EA = global;
        VariableContext.lookup(global.toString()).get("OWNER").assign(zzzGtGloOwner());
        if (zGetGloCleanupMode()<= 0) {
            $zRegGlobal(global);
        }
        return global;
    }

    /**
     * zzzGtAuditTmpGloAllocPPG(checkSame
     * 
     */
    public Value zzzGtAuditTmpGloAllocPPG(final boolean checkSame) {
        Value gSchm = Value.nullValue();
        Value global = Value.nullValue();
        Value ws = Value.nullValue();
        if (!checkSame) {
            return $EA.get("E").get("%etGlo");
        }
        if ($EA.get("G")) {
            ws.assign(zgCurLWS());
            ws = zgCurLWS();
        }
        if (Text.follows(ws.toString(), Value.nullValue().toString())) {
            gSchm.assign("AUDITDEBUG");
            gSchm = "AUDITDEBUG";
        }
        gSchm.assign($ZeOSUNQ.get("PPG").get("SCHEME"));
        gSchm = $ZeOSUNQ.get("PPG").get("SCHEME");
        if (gSchm == Value.nullValue()) {
            gSchm.assign($ZeOSUNQ.get("PPG").get("SCHEME"));
            gSchm = $ZeOSUNQ.get("PPG").get("SCHEME");
        }
        gSchm.assign($ZeOSDF.get("PPG").get("SCHEME"));
        gSchm = $ZeOSDF.get("PPG").get("SCHEME");
        if (gSchm == Value.nullValue()) {
            gSchm.assign($ZeOSDF.get("PPG").get("SCHEME"));
            gSchm = $ZeOSDF.get("PPG").get("SCHEME");
        }
        gSchm.assign("PSLICED");
        gSchm = "PSLICED";
        if (gSchm == Value.nullValue()) {
            gSchm.assign("PSLICED");
            gSchm = "PSLICED";
        }
        if (gSchm == "DBINTERNAL") {
            gSchm.assign("PSLICED");
            gSchm = "PSLICED";
        }
        if (gSchm == "AUDITDEBUG") {
            global.assign(Reflect.name(eADETDEBUG.get(ju().toString())));
            global = Reflect.name(eADETDEBUG.get(ju().toString()));
        } else {
            do {
                global.assign(zzzGtGloFromBase(gSchm, "^ERTMPRGB"));
                global = zzzGtGloFromBase(gSchm, "^ERTMPRGB");
            } while (false);
        }
        if (checkSame) {
            return global;
        }
        if (Reflect.dataType(VariableContext.lookup(global))> 0) {
            EAETBKG.saveLostBatch(global);
        }
        VariableContext.lookup(global).clear();
        $EA.get("E").get("%etGlo").assign(global);
        $EA = global;
        VariableContext.lookup(global).get("OWNER").assign(zzzGtGloOwner());
        if (zGetGloCleanupMode()<= 0) {
            $zRegGlobal(global);
        }
        return global;
    }

    /**
     * zzzGtGloFromBase(gSchm,baseG
     * 
     */
    public Value zzzGtGloFromBase(final Value gSchm, final Value baseG) {
        String global = "";
        double gSpread = 0.0D;
        if (gSchm == "PIDBASED") {
            global = (baseG + Reflect.job());
        } else {
            do {
                gSchm.assign("PSLICED");
                gSchm = "PSLICED";
                if (gSchm == "RSLICED") {
                    gSchm.assign("PSLICED");
                    gSchm = "PSLICED";
                }
                if (gSchm == "PSLICED") {
                    do {
                        gSpread = $ZeOSUNQ.get("PPG").get("GLOSPREAD");
                        gSpread = $ZeOSDF.get("PPG").get("GLOSPREAD");
                        if (gSpread< 1) {
                            gSpread = $ZeOSDF.get("PPG").get("GLOSPREAD");
                        }
                        gSpread = 100;
                        if (gSpread< 100) {
                            gSpread = 100;
                        }
                        global = (((((baseG +"P")+(Reflect.job()%gSpread))+"(")+(Reflect.job()/gSpread))+")");
                    } while (false);
                }
            } while (false);
        }
        return global;
    }

    /**
     * zzzGtGloOwner;
     * Q $$ju()_$C(9)_$$zgUserID()_$C(9)_$C(9)_$I_$C(9)_$H_$C(9)_$$zPIDLong()
     * 
     */
    public Value zzzGtGloOwner() {
        return (((((((((ju()+ Text.character(9))+ zgUserID())+ Text.character(9))+ Text.character(9))+ ReadWrite.channel())+ Text.character(9))+ Builtin.horolog())+ Text.character(9))+ zPIDLong());
    }

    /**
     * zGtTmpGloType(notCT,localeColl
     * 
     */
    public Value zGtTmpGloType(final Value notCT, final Value localeColl) {
        return ((notCT == 2)?"%etGlo":(true?(((notCT == 1)?"pGloRg":(true?"pGlo":Value.nullValue()))+(localeColl?"C":(true?Value.nullValue():Value.nullValue()))):Value.nullValue()));
    }

    /**
     * %zRelTmpGlo(gloName
     * 
     */
    public void $zRelTmpGlo(final String gloName) {
        return ;
        if (gloName == Value.nullValue()) {
            return ;
        }
        VariableContext.lookup(gloName).clear();
        return ;
    }

    /**
     * %zRelAllTmpGlo;
     * ;call zTagExists since core libraries (%Zefnlih) load before chronicles routines (EAETBT4). This check can be removed in 2015+1
     * I $$zTagExists("checkBatchesServer^EAETBT4") S %=$$checkBatchesServer^EAETBT4();cleans up any active batches
     * I %EA("E","pGlo")'="" D %zRelTmpGlo(%EA("E","pGlo"))
     * I %EA("E","pGloRg")'="" D %zRelTmpGlo(%EA("E","pGloRg"))
     * I %EA("E","pGloC")'="" D %zRelTmpGlo(%EA("E","pGloC"))
     * I %EA("E","pGloRgC")'="" D %zRelTmpGlo(%EA("E","pGloRgC"))
     * I %EA("E","%etGlo")'="" D %zRelTmpGlo(%EA("E","%etGlo"))
     * S %=$$GlobalCleanUp^%ZeCLEANUP($J,$$zgUserID(),$$zClient^elibEALIBG(1));*DJK 05/15 T7024 - Clean up globals  ;Uses extended reference to avoid direct dependency on ^EALIB
     * K rqst("%ET");Caches the current batch ID
     * Q 
     * 
     */
    public void $zRelAllTmpGlo() {
        if (zTagExists("checkBatchesServer^EAETBT4")) {
            $.assign(EAETBT4 .checkBatchesServer());
            $ = EAETBT4 .checkBatchesServer();
        }
        if ($EA.get("E").get("pGlo")!= Value.nullValue()) {
            ZefnLib.releasePrivateTempGlobal($EA.get("E").get("pGlo").toString());
        }
        if ($EA.get("E").get("pGloRg")!= Value.nullValue()) {
            ZefnLib.releasePrivateTempGlobal($EA.get("E").get("pGloRg").toString());
        }
        if ($EA.get("E").get("pGloC")!= Value.nullValue()) {
            ZefnLib.releasePrivateTempGlobal($EA.get("E").get("pGloC").toString());
        }
        if ($EA.get("E").get("pGloRgC")!= Value.nullValue()) {
            ZefnLib.releasePrivateTempGlobal($EA.get("E").get("pGloRgC").toString());
        }
        if ($EA.get("E").get("%etGlo")!= Value.nullValue()) {
            ZefnLib.releasePrivateTempGlobal($EA.get("E").get("%etGlo").toString());
        }
        $.assign(%ZeCLEANUP.globalCleanUp(Reflect.job(), zgUserID(), elibEALIBG.zClient(1)));
        $ = %ZeCLEANUP.globalCleanUp(Reflect.job(), zgUserID(), elibEALIBG.zClient(1));
        rqst.get("%ET").clear();
        return ;
    }

    /**
     * zGtTmpGloShrd(notCT,localeColl
     * 
     */
    public String zGtTmpGloShrd(final boolean notCT, final boolean localeColl) {
        Value baseGloName = Value.nullValue();
        String gloName = "";
        double uniq = 0.0D;
        Value error = Value.nullValue();
        baseGloName.assign((notCT?"^ESNTEMPUNIQ":(true?"^ESTEMPUNIQ":Value.nullValue())));
        baseGloName = (notCT?"^ESNTEMPUNIQ":(true?"^ESTEMPUNIQ":Value.nullValue()));
        if (localeColl) {
            baseGloName.assign((baseGloName +"COLL"));
            baseGloName = (baseGloName +"COLL");
        } else {
            baseGloName.assign(((baseGloName + zGtNodeID())+"A"));
            baseGloName = ((baseGloName + zGtNodeID())+"A");
        }
        baseGloName.assign(((baseGloName + zGtNodeID())+"A"));
        baseGloName = ((baseGloName + zGtNodeID())+"A");
        while (true) {
            do {
                uniq = zIncSafe(baseGloName, 1);
                gloName = Reflect.name(VariableContext.lookup((baseGloName +(uniq% 100))).get((uniq/ 100)));
            } while (false);
            return ;
            if (!Reflect.dataType(VariableContext.lookup(gloName))) {
                return ;
            }
        }
        return gloName;
    }

    /**
     * %zRelTmpGloShrd(gloName
     * 
     */
    public void $zRelTmpGloShrd(final String gloName) {
        VariableContext.lookup(Reflect.name(VariableContext.lookup(gloName), 1)).clear();
        if (gloName!= Value.nullValue()) {
            VariableContext.lookup(Reflect.name(VariableContext.lookup(gloName), 1)).clear();
        }
        return ;
    }

    /**
     * zNumMaxIncrementQ 99999999999999999
     * 
     */
    public long zNumMaxIncrement() {
        return  99999999999999999L;
    }

    /**
     * zIncSafe(glo,resetCnt
     * 
     */
    public long zIncSafe(final Value glo, final Value resetCnt) {
        long ret = 0;
        ret = Math.increment(VariableContext.lookup(glo.toString()));
        return ret;
        if (ret<zNumMaxIncrement()) {
            return ret;
        }
        $zzIncSafeReset(glo, resetCnt);
        return Math.increment(VariableContext.lookup(glo.toString()));
    }

    /**
     * %zzIncSafeReset(glo,resetCnt
     * 
     */
    public void $zzIncSafeReset(final String glo, final Value resetCnt) {
        String lockGlo = "";
        lockGlo = Reflect.name(hFLAG.get("E").get("INC-RESET").get(glo));
        Builtin.lock(VariableContext.lookup(lockGlo));
        VariableContext.lookup(glo).assign(resetCnt);
        if (VariableContext.lookup(glo)>= zNumMaxIncrement()) {
            VariableContext.lookup(glo).assign(resetCnt);
        }
        Builtin.lock((-VariableContext.lookup(lockGlo)));
        return ;
    }

    /**
     * zGetGloCleanupMode;
     * Q $$zGetGloCleanupMode^%Zelibh()
     * 
     */
    public Value zGetGloCleanupMode() {
        return %Zelibh.zGetGloCleanupMode();
    }

    /**
     * zGetGloCleanupModeCustNoCache;
     * N wsid,gloClnupSetting
     * S wsid=$$zgCurLWSIDEnt()
     * I wsid="" S %EA("E","CLEANUP","Global")=0 Q 0
     * S gloClnupSetting=+^debug("GlobalCleanup",wsid)
     * S %EA("E","CLEANUP","Global")=gloClnupSetting
     * Q gloClnupSetting
     * 
     */
    public long zGetGloCleanupModeCustNoCache() {
        Value wsid = Value.nullValue();
        Value gloClnupSetting = Value.nullValue();
        wsid.assign(zgCurLWSIDEnt());
        wsid = zgCurLWSIDEnt();
        if (wsid == Value.nullValue()) {
            $EA.get("E").get("CLEANUP").get("Global").assign(0);
            $EA = 0;
            return  0;
        }
        gloClnupSetting.assign(debug.get("GlobalCleanup").get(wsid.toString()));
        gloClnupSetting = debug.get("GlobalCleanup").get(wsid.toString());
        $EA.get("E").get("CLEANUP").get("Global").assign(gloClnupSetting);
        $EA = gloClnupSetting;
        return gloClnupSetting;
    }

    /**
     * zOIDType;
     * N val
     * S val=^%ZeOSDF("OID","ETYPE")
     * Q $S(val=1:1,val=2:2,1:3)
     * 
     */
    public Value zOIDType() {
        Value val = Value.nullValue();
        val.assign($ZeOSDF.get("OID").get("ETYPE"));
        val = $ZeOSDF.get("OID").get("ETYPE");
        return ((val == 1)? 1 :((val == 2)? 2 :(true? 3 :Value.nullValue())));
    }

    /**
     * zIsEpicOID;
     * ;;#if# ^%ZeOSDF("OID","ETYPE")=1
     * Q 1
     * 
     */
    public long zIsEpicOID() {
        return  1;
    }

    /**
     * %zetGloS %etGlo=$S(%etGlo'="":%etGlo,1:$$zzzGtAuditTmpGloAllocPPG()) IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public void $zetGlo() {
        $etGlo.assign((($etGlo!= Value.nullValue())?$etGlo:(true?zzzGtAuditTmpGloAllocPPG():Value.nullValue())));
        $etGlo = (($etGlo!= Value.nullValue())?$etGlo:(true?zzzGtAuditTmpGloAllocPPG():Value.nullValue()));
        return ;
    }

    /**
     * %zsetPCMPCD;
     * S PCM=^%ZeOSDF("PCM"),PCD=^%ZeOSDF("PCD")
     * S:PCM="" PCM=1,PCD=2
     * Q 
     * 
     */
    public void $zsetPCMPCD() {
        pCM = $ZeOSDF.get("PCM");
        pCD = $ZeOSDF.get("PCD");
        pCM = 1;
        pCD = 2;
        if (pCM == Value.nullValue()) {
            pCM.assign(1);
            pCM = 1;
            pCD = 2;
        }
        return ;
    }

    /**
     * zNeedsExtDirRef(var
     * 
     */
    public Value zNeedsExtDirRef(final String var) {
        return ((Text.extract(var)!= "^")? 0 :(Text.matches(Text.extract(var, 2), "1dA")? 1 :((Text.extract(var, 2) == "%")? 1 :(true? 0 :Value.nullValue()))));
    }

    /**
     * %zWrapPrint(str,wd,prfx,fwd,lead,txt
     * 
     */
    public void $zWrapPrint(final Value str, final long wd, final Value prfx, final Value fwd, final Value lead, final Value txt) {
        double ln = 0.0D;
        txt.clear();
        txt = Value.nullValue();
        wd = 80;
        if (wd == 0) {
            wd = 80;
        }
        $zwrap(str, txt, wd, prfx, fwd, lead);
        for (double ln = 1; (ln >= txt.get("0")); ln += 1) {
            ReadWrite.write(ReadWrite.newline());
            if (ln!= 1) {
                ReadWrite.write(ReadWrite.newline());
            }
            ReadWrite.write(txt.get(ln.toString()));
        }
        return ;
    }

    /**
     * %zEMPLstN pc,list IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public void $zEMPLst() {
        double pc = 0.0D;
        Value list = Value.nullValue();
        list.assign(zEMPini());
        list = zEMPini();
        for (double pc = 1; (pc >= Text.occurrencesPlusOne(list.toString(), ",")); pc += 1) {
            $EA.get("E").get("EMP").get(Text.piece(list.toString(), ",", pc)).assign(1);
            $EA = 1;
        }
        $EA.get("E").get("EMP").assign(1);
        $EA = 1;
        return ;
    }

    /**
     * EMPLstD %zEMPLst() IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public long eMPLst() {
        $zEMPLst();
        return  1;
    }

    /**
     * zGetESUn(global,fromGtTmpGlo
     * 
     */
    public Value zGetESUn(final String global, final Value fromGtTmpGlo) {
        return Value.nullValue();
        if (global == Value.nullValue()) {
            return Value.nullValue();
        }
        String uniq = "";
        uniq = Math.increment(VariableContext.lookup(global.toString()), 1);
        Builtin.ecode().assign(Value.nullValue());
        VariableContext.lookup(global).get("CALLER").get(uniq).assign(Reflect.stackInfo((Reflect.stackDepth()- 1), "PLACE"));
        if (Reflect.dataType(VariableContext.lookup(global).get(uniq))) {
            VariableContext.lookup(global).get(uniq).clear();
        }
        return uniq;
    }

    /**
     * zGtPrGlo(notCT
     * 
     */
    public String zGtPrGlo(final Value notCT) {
        return zGtTmpGlo(notCT);
    }

}
