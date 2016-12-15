
package edu.vanderbilt.clinicalsystems.m.lang.text;

import edu.vanderbilt.clinicalsystems.epic.lib.ZefnLib;
import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.VariableContext;
import edu.vanderbilt.clinicalsystems.m.core.lib.Builtin;
import edu.vanderbilt.clinicalsystems.m.core.lib.HaltCondition;
import edu.vanderbilt.clinicalsystems.m.core.lib.Math;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;
import edu.vanderbilt.clinicalsystems.m.core.lib.Reflect;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;

public class Ealibecf1 {

    public Value $sv9;
    public String zzErrNum;
    public int $ecfFromThrow;
    public Value $sv7;
    public Value $sv8;
    public Value data;
    public Value zzNoLog;
    public Value zzCodeLn;
    public Value parentPropId;
    public Value mode;
    public Value $ecfMode;
    public Value merge;
    public Value appendToPrevious;
    public Value $sv1;
    public Value $sv2;
    public Value $sv5;
    public Value ltlEndian;
    public Value $sv6;
    public Value $sv3;
    public Value $sv4;
    public Value zzDetails;
    public Value $;
    public Value colDefs;
    public Value params;
    public Value str;
    public Value tmpGlo;
    public Value $ecfFtrs;
    public Value shared;
    public Value varName;
    public Value propName;
    public Value propStore;
    public Value $ecfOutBuf;
    public Value $sv11;
    public Value $sv10;
    public Value row;
    public Value enc;
    public Value value;
    public Value key;
    public Value containerPropId;
    public Value more;
    public Value lineOrKey;
    public Value column;
    public Value propNameOrId;
    public Value tblId;
    public Value int;
    public Value cells;
    public Value tableId;
    public Value dataStore;
    public Value short;
    public Value zzErrCode;
    public Value errRsn;
    public Value columnName;

    /**
     * EALIBECF1; ; ;2015-01-21 11:44:22;8.2;Y+qQAnjAnf6LXuHjD4U25nmZ4CFwj/OL8yd5zxgbvvnz+unQEfQoG0ThGsxe7XIp
     * ;;#lglob#EALIB
     * ; Copyright (C) Epic Systems Corporation 2006-2015
     * ;*********************************************************************
     * ; TITLE:   EALIBECF1
     * ; PURPOSE: Epic Communication Foundation Server API
     * ; AUTHOR:  Mike Lee
     * ; CALLABLE TAGS:
     * ;   zECFSetDestGlobal  Set the destination of all set commands to a global, instead of the client
     * ;   zECFSetDestClient  Set the destination of all set commands to the client, instead of a global
     * ;   zECFInitDest   Initialize the destination to one previously set
     * ;   zECFGetDest    Get the identifier of the current destination mode
     * ;   zECFReleaseDestGlobal  Release the global created via zECFSetDestGlobal
     * ;   zECFGet        Get the value of a single property value
     * ;   zECFGetWasSent Get whether the single property value was sent from the client
     * ;   zECFGetElmt    Get the value of an element of an array, list, or dictionary property
     * ;   zECFGetCell    Get the value of a cell of a table property
     * ;   zECFNumElmts   Get the number of elements in an array or list property
     * ;   zECFDctNxKey   Get the next key in a dictionary property
     * ;   zECFTblNxCol   Get the next column name in a table property
     * ;   zECFMrgIn      Get a structure representing an incoming property
     * ;   zECFTblMrgIn   Return a structure representing an incoming parameter
     * ;   zECFNew        Create a new response property
     * ;   zECFNewElmtObj Create a new object as an element in an array, list, or dictionary property
     * ;   zECFReset      Reset a property value back to its default
     * ;   zECFSet        Set the value of a property
     * ;   zECFSetElmt    Set the value of an element in an array, list, or dictionary property
     * ;   zECFNewCol     Create a new column on a table property
     * ;   zECFSetRow     Set a row on a table property
     * ;   zECFStream     Stream an unstructured response back to the client
     * ;   zECFMrgOut     Merge an array into an array, list, or dictionary property
     * ;   zECFTblMrgOut  Merge an array into a table outgoing parameter
     * ;   zECFTmout      Determines whether the server command has exceeded its time limit
     * ;   zECFThrow      Throws an exception during command execution and code execution will return to the client process
     * ;
     * ; REVISION HISTORY:
     * ; *JAM    02/12 M7016113 - zECFThrow: add zzNoLog param
     * ; *DLM    07/12 M7017485 - zECFGet*: support global-mode with positive property numbers
     * ; *MAT    06/13 M7020868 - Restore behavior to only log error once per day in zECFThrow
     * ; *MAT    08/13 M7021456 - Fix logic issues for buffer flush in zECFSet*
     * ; *AGG    02/14 M7023368 - Remove use of %ecfStrongEnc variable
     * ; *MAT    03/14 M7023709 - Change name of variable from %saved in zECFThrow
     * ; *MAT    10/14 M7026422 - Add functions to stream data to client without response object
     * ;*********************************************************************
     * ;---------
     * ; NAME:         zECFSetDestGlobal (PUBLIC)
     * ; DESCRIPTION:  Set the destination of all set commands to a global, instead of the client
     * ; KEYWORDS:     ECF,Set,Destination,Global
     * ; CALLED BY:    ECF Application code.
     * ; PARAMETERS:
     * ;  shared       (I,OPT,DEF:0) - whether to create a global that can be shared across processes
     * ; RETURNS:      A mode identifier that can be passed in to zECFInitDest or zECFGet* functions
     * ; ASSUMES:      %ecfMode,%ecfFtrs
     * 
     * 
     */
    public Value main() {
    }

    /**
     * 
     */
    public Value zECFSetDestGlobal(final boolean shared) {
        Value tmpGlo = Value.nullValue();
        if (shared) {
            tmpGlo.set(ZefnLib.acquireSharedTempGlobal());
            $ecfMode = (((("-1"+ Text.character(1))+ tmpGlo)+ Text.character(1))+"1");
        } else {
            tmpGlo.set(ZefnLib.acquirePrivateTempGlobal());
            $ecfMode = (("-1"+ Text.character(1))+ tmpGlo);
        }
        VariableContext.lookup(tmpGlo).set((- 1));
        return $ecfMode;
    }

    /**
     * zECFSetDestClient;
     * ;;#strip# only set the first piece, to leave the global piece intact for future calls to zECFGet* with negative numbers
     * S $P(%ecfMode,$C(1),1)=0
     * 
     * 
     */
    public Value zECFSetDestClient() {
        Text.pieceAssign($ecfMode, Text.character(1), 1, 0);
        return $ecfMode;
    }

    /**
     * 
     */
    public Value zECFInitDest(final Value mode) {
        $ecfMode.set(mode);
        return  1;
    }

    /**
     * zECFGetDest;
     * S:%ecfMode="" %ecfMode=0
     * 
     * 
     */
    public Value zECFGetDest() {
        if ($ecfMode == Value.nullValue()) {
            $ecfMode = 0;
        }
        return $ecfMode;
    }

    /**
     * 
     */
    public Value zECFReleaseDestGlobal(final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set($ecfMode);
        }
        if (Text.piece(mode, Text.character(1), 3)) {
            ZefnLib.releaseSharedTempGlobal(Text.piece(mode, Text.character(1), 2));
        } else {
            ZefnLib.releasePrivateTempGlobal(Text.piece(mode, Text.character(1), 2));
        }
        return  1;
    }

    /**
     * 
     */
    public Value zECFGet(final Value propName, final Value parentPropId, final Value more, final Value mode) {
        $sv1 .set(Value.nullValue());
        if (mode == Value.nullValue()) {
            mode.set($ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        $sv5 = Text.piece(mode, Text.character(1), 2);
        $sv5 .set((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        if (more == 0) {
            more = 1;
        }
        while (true) {
            $sv1 = ($sv1 + VariableContext.lookup($sv5).get(parentPropId).get(propName).get(1).get(more));
            more = Builtin.followingKey(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(1).get(more));
            if (more == Value.nullValue()) {
                return ;
            }
            if ((Text.length($sv1)+ Text.length(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(1).get(more)))>$EA) {
                return ;
            }
        }
        return $sv1;
    }

    /**
     * 
     */
    public Value zECFGetWasSent(final Value propName, final Value parentPropId, final Value lineOrKey, final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set($ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        $sv5 = Text.piece(mode, Text.character(1), 2);
        $sv5 .set((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        if (Text.follows(lineOrKey.toString(), Value.nullValue().toString())) {
            return Text.follows(Builtin.followingKey(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(lineOrKey).get(Value.nullValue())), Value.nullValue().toString());
        }
        if (!$ecfVersion) {
            do {
                Value errRsn = Value.nullValue();
                errRsn.set("The communication assembly Epic.Core.Ecf.dll does not support ");
                errRsn = (errRsn +"ignoring properties and renders a call to $$zECFGetWasSent invalid.");
                $.set(zECFThrow("CLIENT-CANNOT-IGNORE-PROPERTIES", errRsn));
            } while (false);
        }
        return (Builtin.followingKey(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(Value.nullValue()))!= Value.nullValue());
    }

    /**
     * 
     */
    public Value zECFGetElmt(final Value propName, final Value parentPropId, final Value lineOrKey, final Value more, final Value mode) {
        $sv1 .set(Value.nullValue());
        if (mode == Value.nullValue()) {
            mode.set($ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        $sv5 = Text.piece(mode, Text.character(1), 2);
        $sv5 .set((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        if (more == Value.nullValue()) {
            if (zbitAnd(VariableContext.lookup($sv5).get(parentPropId).get(propName), 32) == 32) {
                if (VariableContext.lookup($sv5).get(parentPropId).get(propName).get(lineOrKey).get(1) == 0) {
                    return Value.nullValue();
                }
            }
        }
        if (more == Value.nullValue()) {
            more = ("1,"+ VariableContext.lookup($sv5).get(parentPropId).get(propName).get(lineOrKey).get(0));
        }
        while (true) {
            $sv1 = ($sv1 + VariableContext.lookup($sv5).get(parentPropId).get(propName).get(lineOrKey).get(more));
            if ((1 + more)>Text.piece(more, ",", 2)) {
                more.set(Value.nullValue());
            }
            if (more == Value.nullValue()) {
                return ;
            }
            Text.pieceAssign(more, ",", (1 + more).toString());
            if ((Text.length($sv1)+ Text.length(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(lineOrKey).get(more)))>$EA) {
                return ;
            }
        }
        return $sv1;
    }

    /**
     * 
     */
    public Value zECFGetCell(final Value propName, final Value parentPropId, final Value row, final Value column, final Value more, final Value mode) {
        $sv1 .set(Value.nullValue());
        if (mode == Value.nullValue()) {
            mode.set($ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        $sv5 = Text.piece(mode, Text.character(1), 2);
        $sv5 .set((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        if (more == Value.nullValue()) {
            more = 1;
        }
        while (true) {
            $sv1 = ($sv1 + VariableContext.lookup($sv5).get(parentPropId).get(propName).get(row).get(column).get(more));
            more = Builtin.followingKey(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(row).get(column).get(more));
            if (more == Value.nullValue()) {
                return ;
            }
            if ((Text.length($sv1)+ Text.length(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(row).get(column).get(more)))>$EA) {
                return ;
            }
        }
        return $sv1;
    }

    /**
     * 
     */
    public Value zECFNumElmts(final Value propName, final Value parentPropId, final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set($ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        $sv5 = Text.piece(mode, Text.character(1), 2);
        return VariableContext.lookup((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())).toString()).get(parentPropId).get(propName).get(0);
    }

    /**
     * 
     */
    public Value zECFDctNxKey(final Value propName, final Value parentPropId, final Value key, final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set($ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        $sv5 .set(Value.nullValue());
        $sv5 = Text.piece(mode, Text.character(1), 2);
        $sv5 .set((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        while (true) {
            key = Builtin.followingKey(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(key));
            if (key == Value.nullValue()) {
                return ;
            }
            if (Reflect.dataType(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(key))> 1) {
                return ;
            }
        }
        return key;
    }

    /**
     * 
     */
    public Value zECFTblNxCol(final Value propName, final Value parentPropId, final Value row, final Value column, final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set($ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        $sv5 = Text.piece(mode, Text.character(1), 2);
        if (row == Value.nullValue()) {
            row = 1;
        }
        return Builtin.followingKey(VariableContext.lookup((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())).toString()).get(parentPropId).get(propName).get(row).get(column));
    }

    /**
     * 
     */
    public Value zECFMrgIn(final String propName, final Value parentPropId, final Value varName) {
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        VariableContext.lookup(varName).set(VariableContext.lookup($ecfInBufLoc).get(parentPropId).get(propName));
        VariableContext.lookup($ecfInBufLoc).get(parentPropId).get(propName).set(Value.nullValue());
        return  1;
    }

    /**
     * 
     */
    public Value zECFTblMrgIn(final String propName, final Value parentPropId, final Value varName) {
        $sv1 .set(Value.nullValue());
        $sv2 .set(Value.nullValue());
        $sv3 .set(Value.nullValue());
        $sv4 .set(Value.nullValue());
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        VariableContext.lookup(varName).set(VariableContext.lookup($ecfInBufLoc).get(parentPropId).get(propName));
        VariableContext.lookup($ecfInBufLoc).get(parentPropId).get(propName).set(Value.nullValue());
        for (int $sv1 = 1; ($sv1 >= VariableContext.lookup(varName).get(0)); $sv1 ++) {
            do {
                while (true) {
                    $sv2 = Builtin.followingKey(VariableContext.lookup(varName).get($sv1).get($sv2));
                    if ($sv2 == Value.nullValue()) {
                        return ;
                    }
                    do {
                        for (int $sv3 = 1; ($sv3 >= VariableContext.lookup(varName).get($sv1).get($sv2).get(0)); $sv3 ++) {
                            do {
                                VariableContext.lookup(varName).get($sv1).get($sv2).set((VariableContext.lookup(varName).get($sv1).get($sv2)+ VariableContext.lookup(varName).get($sv1).get($sv2).get($sv3)));
                                VariableContext.lookup(varName).get($sv1).get($sv2).get($sv3).set(Value.nullValue());
                            } while (false);
                        }
                        VariableContext.lookup(varName).get($sv1).get($sv2).get(0).set(Value.nullValue());
                    } while (false);
                }
            } while (false);
        }
        while (true) {
            $sv4 = Builtin.followingKey(VariableContext.lookup(varName).get($sv4));
            if ($sv4 == Value.nullValue()) {
                return ;
            }
            while (true) {
                $sv2 = Builtin.followingKey(VariableContext.lookup(varName).get($sv4).get($sv2));
                if ($sv2 == Value.nullValue()) {
                    return ;
                }
                if (!Reflect.dataType(VariableContext.lookup(varName).get(0).get($sv2))) {
                    VariableContext.lookup(varName).get(0).get($sv2).set(Math.increment(VariableContext.lookup(varName).get(0)));
                }
            }
        }
        return  1;
    }

    /**
     * 
     */
    public Value zECFNew(final Value propName, final Value parentPropId, final Value propStore, final Value merge, final Value enc) {
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFNewGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set(($ecfEncrypt||enc));
        $ecfFtrs.set(($ecfFtrs + 1));
        $sv1 .set($ecfFtrs);
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        if (enc) {
            $.set(zcrypt(propName, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), propName));
        }
        if (((Text.length($ecfOutBuf)+ Text.length(propName))+ 12)>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +((((((Text.character(2)+ Text.character(((propStore == "S")? 0 :((propStore == "A")? 16 :((propStore == "L")? 16 :((propStore == "D")? 32 :(true? 0 :Value.nullValue())))))))+ Text.character(((merge* 64)+(enc* 128))))+ zchrL($sv1))+ zchrL(parentPropId))+ Text.character(Text.length(propName)))+ propName));
        return $sv1;
    }

    /**
     * 
     */
    public Value zECFNewElmtObj(final Value containerPropId, final Value key, final Value enc) {
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFNewElmtObjGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set(($ecfEncrypt||enc));
        $ecfFtrs.set(($ecfFtrs + 1));
        $sv1 .set($ecfFtrs);
        if (enc) {
            if (Text.follows(key.toString(), Value.nullValue().toString())) {
                $.set(zcrypt(key, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), key));
            }
        }
        if ((Text.length($ecfOutBuf)+(Text.follows(key.toString(), Value.nullValue().toString())?(Text.length(key)+ 12):(true? 11 :Value.nullValue())))>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        if (Text.follows(key.toString(), Value.nullValue().toString())) {
            $ecfOutBuf = ($ecfOutBuf +((((((Text.character(2)+ Text.character(2))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key));
        } else {
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(2)+ Text.character(1))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(containerPropId)));
        }
        return $sv1;
    }

    /**
     * 
     */
    public Value zECFReset(final boolean propName, final Value parentPropId, final Value enc) {
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFResetGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set(($ecfEncrypt||enc));
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        if (enc) {
            if (Text.follows(propName.toString(), Value.nullValue().toString())) {
                $.set(zcrypt(propName, $ecfToken, (ju()+ $ecfToken), propName));
            }
        }
        if (((Text.length($ecfOutBuf)+ Text.length(propName))+ 8)>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(3)+ Text.character(0))+ Text.character(0))+ zchrL(parentPropId))+ Text.character(Text.length(propName)))+ propName));
        return  1;
    }

    /**
     * 
     */
    public Value zECFNewCol(final boolean tableId, final Value columnName, final Value enc) {
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFNewColGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set(($ecfEncrypt||enc));
        $ecfFtrs.get(tableId).set(($ecfFtrs + 1));
        $sv1 .set($ecfFtrs);
        if (enc) {
            $.set(zcrypt(columnName, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), columnName));
        }
        if (((Text.length($ecfOutBuf)+ Text.length(columnName))+ 10)>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +((((((Text.character(1)+ Text.character(0))+ Text.character(0))+ zchrL(tableId))+ zchrW($sv1))+ Text.character(Text.length(columnName)))+ columnName));
        return $sv1;
    }

    /**
     * 
     */
    public Value zECFSet(final Value propNameOrId, final Value value, final Value parentPropId, final Value enc) {
        $sv1 .set(propNameOrId);
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFSetGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set(($ecfEncrypt||enc));
        if ($sv1 == propNameOrId) {
            try {
                $zzECFSetValSwitch();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfFtrs.set(($ecfFtrs + 1));
        $sv1 .set($ecfFtrs);
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        if (enc) {
            $.set(zcrypt(propNameOrId, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), propNameOrId));
        }
        if (((Text.length($ecfOutBuf)+ Text.length(propNameOrId))+ 12)>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +((((((Text.character(2)+ Text.character(0))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(parentPropId))+ Text.character(Text.length(propNameOrId)))+ propNameOrId));
        $zzECFSetValSwitch:
        if (enc) {
            try {
                $zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetVal:
        $sv2 .set(Value.nullValue());
        $sv3 = Text.length(value);
        if ($sv3 == 0) {
            if ((Text.length($ecfOutBuf)+ 9)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0)));
            return $sv1;
        }
        if (((Text.length($ecfOutBuf)+ $sv3)+ 9)>= $ecfPckSz) {
            $sv4 .set((($ecfPckSz-Text.length($ecfOutBuf))- 8));
            $sv2 = Text.extract(value, ($sv4 + 1), 999999999);
            value = Text.extract(value, 1, $sv4);
            $sv3 = Text.length(value);
        }
        if (($sv3 > 0)||(Text.length($sv2) == 0)) {
            $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value));
        }
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            try {
                $zzECFSetVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return $sv1;
    }

    /**
     * %zzECFSetValSwitchG:enc %zzECFSetValEnc()
     * %zzECFSetValS %sv2="",%sv3=$L(value)
     * I %sv3=0 S:$L(%ecfOutBuf)+9>%ecfPckSz %=$$zECFSndRsltPckt() S %ecfOutBuf=%ecfOutBuf_($C(0)_$C(0)_$C(0)_$$zchrL(%sv1)_$$zchrW(0)) Q %sv1
     * I $L(%ecfOutBuf)+%sv3+9'<%ecfPckSz S %sv4=%ecfPckSz-$L(%ecfOutBuf)-8,%sv2=$E(value,%sv4+1,999999999),value=$E(value,1,%sv4),%sv3=$L(value)
     * I %sv3>0!($L(%sv2)=0) S %ecfOutBuf=%ecfOutBuf_($C(0)_$C(0)_$C(0)_$$zchrL(%sv1)_$$zchrW(%sv3)_value)
     * I %sv2]"" S %=$$zECFSndRsltPckt(),value=%sv2 G %zzECFSetVal()
     * 
     * 
     */
    public Value $zzECFSetValSwitch() {
        if (enc) {
            try {
                $zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetVal:
        $sv2 .set(Value.nullValue());
        $sv3 = Text.length(value);
        if ($sv3 == 0) {
            if ((Text.length($ecfOutBuf)+ 9)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0)));
            return $sv1;
        }
        if (((Text.length($ecfOutBuf)+ $sv3)+ 9)>= $ecfPckSz) {
            $sv4 .set((($ecfPckSz-Text.length($ecfOutBuf))- 8));
            $sv2 = Text.extract(value, ($sv4 + 1), 999999999);
            value = Text.extract(value, 1, $sv4);
            $sv3 = Text.length(value);
        }
        if (($sv3 > 0)||(Text.length($sv2) == 0)) {
            $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value));
        }
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            try {
                $zzECFSetVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return $sv1;
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetVal() {
        $sv2 .set(Value.nullValue());
        $sv3 = Text.length(value);
        if ($sv3 == 0) {
            if ((Text.length($ecfOutBuf)+ 9)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0)));
            return $sv1;
        }
        if (((Text.length($ecfOutBuf)+ $sv3)+ 9)>= $ecfPckSz) {
            $sv4 .set((($ecfPckSz-Text.length($ecfOutBuf))- 8));
            $sv2 = Text.extract(value, ($sv4 + 1), 999999999);
            value = Text.extract(value, 1, $sv4);
            $sv3 = Text.length(value);
        }
        if (($sv3 > 0)||(Text.length($sv2) == 0)) {
            $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value));
        }
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            try {
                $zzECFSetVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return $sv1;
    }

    /**
     * %zzECFSetValEncI $L(value)=0
     *   S:$L(%ecfOutBuf)+9>%ecfPckSz %=$$zECFSndRsltPckt()
     *   S %ecfOutBuf=%ecfOutBuf_($C(0)_$C(0)_$C(128)_$$zchrL(%sv1)_$$zchrW(0))
     *   Q %sv1;*MAT 8/13 M7021456 - don't append null value without buffer check
     * S %sv2="",%sv5=%ecfPckSz-$L(%ecfOutBuf)-9,%sv5=%sv5-(%sv5#4)
     * S %sv3=%sv5-(%sv5/32),%sv3=%sv3/(4*3)
     * I $L(value)>%sv3 S %sv2=$E(value,%sv3+1,999999999),value=$E(value,1,%sv3)
     * I %sv3>0!($L(%sv2)=0) S %=$$zcrypt(value,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.value),value=$E(value,1,$L(value)-1),%ecfOutBuf=%ecfOutBuf_($C(0)_$C(0)_$C(128)_$$zchrL(%sv1)_$$zchrW($L(value))_value)
     * I %sv2]"" S %=$$zECFSndRsltPckt(),value=%sv2 G %zzECFSetValEnc()
     * 
     * 
     */
    public Value $zzECFSetValEnc() {
        if (Text.length(value) == 0) {
            if ((Text.length($ecfOutBuf)+ 9)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(128))+ zchrL($sv1))+ zchrW(0)));
            return $sv1;
        }
        $sv2 .set(Value.nullValue());
        $sv5 .set((($ecfPckSz-Text.length($ecfOutBuf))- 9));
        $sv5 .set(($sv5 -($sv5 % 4)));
        $sv3 .set(($sv5 -($sv5 / 32.0D)));
        $sv3 .set(($sv3 /(4 * 3)));
        if (Text.length(value)>$sv3) {
            $sv2 = Text.extract(value, ($sv3 + 1), 999999999);
            value = Text.extract(value, 1, $sv3);
        }
        if (($sv3 > 0)||(Text.length($sv2) == 0)) {
            $.set(zcrypt(value, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), value));
            value = Text.extract(value, 1, (Text.length(value)- 1));
            $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(128))+ zchrL($sv1))+ zchrW(Text.length(value)))+ value));
        }
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            try {
                $zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return $sv1;
    }

    /**
     * 
     */
    public Value zECFSetElmt(final boolean containerPropId, final Value value, final Value key, final Value appendToPrevious, final Value enc) {
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFSetElmtGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set(($ecfEncrypt||enc));
        if ((!enc)&&(key == Value.nullValue())) {
            try {
                $zzECFSetElmtArrVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        if (!enc) {
            try {
                $zzECFSetElmtDctVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        if (key == Value.nullValue()) {
            try {
                $zzECFSetElmtArrValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $.set(zcrypt(key, $ecfToken, (ju()+ $ecfToken), key));
        $zzECFSetElmtDctValEnc:
        if (!Text.length(value)) {
            if (((Text.length($ecfOutBuf)+ Text.length(key))+ 10)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0)));
            return containerPropId;
        }
        $sv2 .set(Value.nullValue());
        $sv5 .set(((($ecfPckSz-Text.length($ecfOutBuf))-Text.length(key))- 9));
        $sv5 .set(($sv5 -($sv5 % 4)));
        $sv3 .set(($sv5 -($sv5 / 32.0D)));
        $sv3 .set(($sv3 /(4 * 3)));
        if (Text.length(value)>$sv3) {
            $sv2 = Text.extract(value, ($sv3 + 1), 999999999);
            value = Text.extract(value, 1, $sv3);
        }
        $.set(zcrypt(value, $ecfToken, (ju()+ $ecfToken), value));
        value = Text.extract(value, 1, (Text.length(value)- 1));
        $ecfOutBuf = ($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(Text.length(value)))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            try {
                $zzECFSetElmtDctValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return containerPropId;
    }

    /**
     * %zzECFSetElmtDctValEncI '$L(value)
     *   S:$L(%ecfOutBuf)+$L(key)+10>%ecfPckSz %=$$zECFSndRsltPckt()
     *   S %ecfOutBuf=%ecfOutBuf_($C(0)_$C(32)_$C(128)_$$zchrL(containerPropId)_$C($L(key))_key_$$zchrW(0))
     *   Q containerPropId;*MAT 8/13 M7021456 - don't append null value without buffer check
     * S %sv2="",%sv5=%ecfPckSz-$L(%ecfOutBuf)-$L(key)-9,%sv5=%sv5-(%sv5#4)
     * S %sv3=%sv5-(%sv5/32),%sv3=%sv3/(4*3)
     * I $L(value)>%sv3 S %sv2=$E(value,%sv3+1,999999999),value=$E(value,1,%sv3)
     * S %=$$zcrypt(value,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),.value),value=$E(value,1,$L(value)-1)
     * S %ecfOutBuf=%ecfOutBuf_($C(0)_$C(32)_$C(128)_$$zchrL(containerPropId)_$C($L(key))_key_$$zchrW($L(value))_value)
     * I %sv2]"" S %=$$zECFSndRsltPckt(),value=%sv2 G %zzECFSetElmtDctValEnc()
     * 
     * 
     */
    public Value $zzECFSetElmtDctValEnc() {
        if (!Text.length(value)) {
            if (((Text.length($ecfOutBuf)+ Text.length(key))+ 10)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0)));
            return containerPropId;
        }
        $sv2 .set(Value.nullValue());
        $sv5 .set(((($ecfPckSz-Text.length($ecfOutBuf))-Text.length(key))- 9));
        $sv5 .set(($sv5 -($sv5 % 4)));
        $sv3 .set(($sv5 -($sv5 / 32.0D)));
        $sv3 .set(($sv3 /(4 * 3)));
        if (Text.length(value)>$sv3) {
            $sv2 = Text.extract(value, ($sv3 + 1), 999999999);
            value = Text.extract(value, 1, $sv3);
        }
        $.set(zcrypt(value, $ecfToken, (ju()+ $ecfToken), value));
        value = Text.extract(value, 1, (Text.length(value)- 1));
        $ecfOutBuf = ($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(Text.length(value)))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            try {
                $zzECFSetElmtDctValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return containerPropId;
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetElmtArrVal() {
        $sv2 .set(Value.nullValue());
        $sv3 = Text.length(value);
        if ($sv3 == 0) {
            if ((Text.length($ecfOutBuf)+ 9)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(16))+ Text.character((appendToPrevious* 64)))+ zchrL(containerPropId))+ zchrW(0)));
            return containerPropId;
        }
        if (((Text.length($ecfOutBuf)+ $sv3)+ 9)>= $ecfPckSz) {
            $sv4 .set((($ecfPckSz-Text.length($ecfOutBuf))- 8));
            $sv2 = Text.extract(value, ($sv4 + 1), 999999999);
            value = Text.extract(value, 1, $sv4);
            $sv3 = Text.length(value);
        }
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(16))+ Text.character((appendToPrevious* 64)))+ zchrL(containerPropId))+ zchrW($sv3))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            appendToPrevious = 1;
            try {
                $zzECFSetElmtArrVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return containerPropId;
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetElmtDctVal() {
        $sv2 .set(Value.nullValue());
        $sv3 = Text.length(value);
        if ($sv3 == 0) {
            if (((Text.length($ecfOutBuf)+ Text.length(key))+ 10)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(0))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0)));
            return containerPropId;
        }
        if ((((Text.length($ecfOutBuf)+ $sv3)+ Text.length(key))+ 10)>= $ecfPckSz) {
            $sv4 .set(((($ecfPckSz-Text.length($ecfOutBuf))-Text.length(key))- 9));
            $sv2 = Text.extract(value, ($sv4 + 1), 999999999);
            value = Text.extract(value, 1, $sv4);
            $sv3 = Text.length(value);
        }
        $ecfOutBuf = ($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(0))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW($sv3))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            try {
                $zzECFSetElmtDctVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return containerPropId;
    }

    /**
     * %zzECFSetElmtArrValEncI '$L(value)
     *   S:$L(%ecfOutBuf)+9>%ecfPckSz %=$$zECFSndRsltPckt()
     *   S %ecfOutBuf=%ecfOutBuf_($C(0)_$C(16)_$C(128+(+appendToPrevious*64))_$$zchrL(containerPropId)_$$zchrW(0))
     *   Q containerPropId;*MAT 8/13 M7021456 - don't append null value without buffer check
     * S %sv2="",%sv5=%ecfPckSz-$L(%ecfOutBuf)-8,%sv5=%sv5-(%sv5#4)
     * S %sv3=%sv5-(%sv5/32),%sv3=%sv3/(4*3)
     * I $L(value)>%sv3 S %sv2=$E(value,%sv3+1,999999999),value=$E(value,1,%sv3)
     * S %=$$zcrypt(value,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),.value),value=$E(value,1,$L(value)-1)
     * S %ecfOutBuf=%ecfOutBuf_($C(0)_$C(16)_$C(128+(+appendToPrevious*64))_$$zchrL(containerPropId)_$$zchrW($L(value))_value)
     * I %sv2]"" S %=$$zECFSndRsltPckt(),value=%sv2,appendToPrevious=1 G %zzECFSetElmtArrValEnc()
     * 
     * 
     */
    public Value $zzECFSetElmtArrValEnc() {
        if (!Text.length(value)) {
            if ((Text.length($ecfOutBuf)+ 9)>$ecfPckSz) {
                $.set(zECFSndRsltPckt());
            }
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(16))+ Text.character((128 +(appendToPrevious* 64))))+ zchrL(containerPropId))+ zchrW(0)));
            return containerPropId;
        }
        $sv2 .set(Value.nullValue());
        $sv5 .set((($ecfPckSz-Text.length($ecfOutBuf))- 8));
        $sv5 .set(($sv5 -($sv5 % 4)));
        $sv3 .set(($sv5 -($sv5 / 32.0D)));
        $sv3 .set(($sv3 /(4 * 3)));
        if (Text.length(value)>$sv3) {
            $sv2 = Text.extract(value, ($sv3 + 1), 999999999);
            value = Text.extract(value, 1, $sv3);
        }
        $.set(zcrypt(value, $ecfToken, (ju()+ $ecfToken), value));
        value = Text.extract(value, 1, (Text.length(value)- 1));
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(16))+ Text.character((128 +(appendToPrevious* 64))))+ zchrL(containerPropId))+ zchrW(Text.length(value)))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.set(zECFSndRsltPckt());
            value.set($sv2);
            appendToPrevious = 1;
            try {
                $zzECFSetElmtArrValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return containerPropId;
    }

    /**
     * 
     */
    public Value zECFSetRow(final Value tableId, final Value cells, final Value enc) {
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFSetRowGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set(($ecfEncrypt||enc));
        $sv1 = 0;
        $sv2 .set($ecfFtrs);
        if ((Text.length($ecfOutBuf)+ 9)>$ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +(((Text.character(0)+ Text.character(48))+ Text.character((enc* 128)))+ zchrL(tableId)));
        if (enc) {
            try {
                $zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetRowCell:
        $sv1 .set(($sv1 + 1));
        value.set(cells);
        $sv3 = Text.length(value);
        if (((Text.length($ecfOutBuf)+ $sv3)+ 6)>$ecfPckSz) {
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
            $.set(zECFSndRsltPckt());
        } else {
            $ecfOutBuf = ($ecfOutBuf +((zchrW($sv1)+ zchrW($sv3))+ value));
            try {
                $zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value);
        if (($sv3 + 13)<= $ecfPckSz) {
            try {
                $zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        $.set(zECFSndRsltPckt());
        if ($sv1 == $sv2) {
            try {
                $zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId));
        $zzECFSetRowCellFnsh:
        if ($sv1 <$sv2) {
            try {
                $zzECFSetRowCell();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            $zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetRowCell() {
        $sv1 .set(($sv1 + 1));
        value.set(cells);
        $sv3 = Text.length(value);
        if (((Text.length($ecfOutBuf)+ $sv3)+ 6)>$ecfPckSz) {
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
            $.set(zECFSndRsltPckt());
        } else {
            $ecfOutBuf = ($ecfOutBuf +((zchrW($sv1)+ zchrW($sv3))+ value));
            try {
                $zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value);
        if (($sv3 + 13)<= $ecfPckSz) {
            try {
                $zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        $.set(zECFSndRsltPckt());
        if ($sv1 == $sv2) {
            try {
                $zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId));
        $zzECFSetRowCellFnsh:
        if ($sv1 <$sv2) {
            try {
                $zzECFSetRowCell();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            $zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetRowCellFnsh() {
        if ($sv1 <$sv2) {
            try {
                $zzECFSetRowCell();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            $zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetRowCellEnc() {
        $sv1 .set(($sv1 + 1));
        value.set(cells);
        $.set(zcrypt(value, $ecfToken, (ju()+ $ecfToken), value));
        value = Text.extract(value, 1, (Text.length(value)- 1));
        $sv3 = Text.length(value);
        if (((Text.length($ecfOutBuf)+ $sv3)+ 6)>$ecfPckSz) {
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
            $.set(zECFSndRsltPckt());
        } else {
            $ecfOutBuf = ($ecfOutBuf +((zchrW($sv1)+ zchrW(Text.length(value)))+ value));
            try {
                $zzECFSetRowCellEncFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(192))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value);
        if (($sv3 + 13)<= $ecfPckSz) {
            try {
                $zzECFSetRowCellEncFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        $.set(zECFSndRsltPckt());
        if ($sv1 == $sv2) {
            try {
                $zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(192))+ zchrL(tableId));
        $zzECFSetRowCellEncFnsh:
        if ($sv1 <$sv2) {
            try {
                $zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetRowFnsh:
        if (Text.length($ecfOutBuf)> 0) {
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * %zzECFSetRowCellEncFnshG:%sv1<%sv2 %zzECFSetRowCellEnc()
     * %zzECFSetRowFnshS:$L(%ecfOutBuf)>0 %ecfOutBuf=%ecfOutBuf_$$zchrW(0)
     * 
     * 
     */
    public Value $zzECFSetRowCellEncFnsh() {
        if ($sv1 <$sv2) {
            try {
                $zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetRowFnsh:
        if (Text.length($ecfOutBuf)> 0) {
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetRowFnsh() {
        if (Text.length($ecfOutBuf)> 0) {
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * 
     */
    public Value zECFStream(final Value data) {
        if (!$ecfStreamCmd) {
            $.set(zECFThrow("ECF-INVALID-FUNCTION-CALL", "The zECFStream function can only be used with StreamCommand type ECF commands."));
            return  0;
        }
        $zzECFStreamLenChk:
        $sv1 = Text.length(data);
        $sv2 = Text.length($ecfOutBuf);
        if ((($sv1 + $sv2)+ 5)>$ecfPckSz) {
            $ecfOutBuf = ($ecfOutBuf + Text.extract(data, 1, (($ecfPckSz-$sv2)- 5)));
            data = Text.extract(data, (($ecfPckSz-$sv2)- 4), $sv1);
            $.set(zECFSndRsltPckt());
            try {
                $zzECFStreamLenChk();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = ($ecfOutBuf + data);
        return  1;
    }

    /**
     * %zzECFStreamLenChkS %sv1=$L(data),%sv2=$L(%ecfOutBuf);only send up to max packet length at a time, subtract 5 for control characters tacked on in zECFSndRsltPckt
     * I %sv1+%sv2+5>%ecfPckSz S %ecfOutBuf=%ecfOutBuf_$E(data,1,%ecfPckSz-%sv2-5),data=$E(data,%ecfPckSz-%sv2-4,%sv1) S %=$$zECFSndRsltPckt() G %zzECFStreamLenChk()
     * S %ecfOutBuf=%ecfOutBuf_data
     * 
     * 
     */
    public Value $zzECFStreamLenChk() {
        $sv1 = Text.length(data);
        $sv2 = Text.length($ecfOutBuf);
        if ((($sv1 + $sv2)+ 5)>$ecfPckSz) {
            $ecfOutBuf = ($ecfOutBuf + Text.extract(data, 1, (($ecfPckSz-$sv2)- 5)));
            data = Text.extract(data, (($ecfPckSz-$sv2)- 4), $sv1);
            $.set(zECFSndRsltPckt());
            try {
                $zzECFStreamLenChk();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf = ($ecfOutBuf + data);
        return  1;
    }

    /**
     * 
     */
    public Value zECFMrgOut(final Value propName, final Value parentPropId, final Value varName, final Value dataStore, final Value enc) {
        $sv6 .set(Value.nullValue());
        $sv7 .set(Value.nullValue());
        $sv8 .set(Value.nullValue());
        $sv9 .set(Value.nullValue());
        $sv10 .set(Value.nullValue());
        $sv11 .set(Value.nullValue());
        enc.set(($ecfEncrypt||enc));
        $sv6 .set(zECFNew(propName, parentPropId, dataStore, enc));
        $sv7 = 1;
        if (dataStore == "D") {
            try {
                $zzECFMrgOutDct();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $sv8 = 0;
        $sv9 = 0;
        if (!Reflect.dataType(VariableContext.lookup(varName).get(1).get(1))) {
            while (true) {
                $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8));
                if ($sv8 == Value.nullValue()) {
                    return ;
                }
                $sv9 .set(($sv9 + 1));
                $sv7 .set(zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8), enc));
                if (!$sv7) {
                    return ;
                }
            }
        } else {
            while (true) {
                $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8));
                if ($sv8 == Value.nullValue()) {
                    return ;
                }
                $sv9 .set(($sv9 + 1));
                $sv10 = 0;
                $sv11 = 0;
                while (true) {
                    $sv10 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8).get($sv10));
                    if ($sv10 == Value.nullValue()) {
                        return ;
                    }
                    $sv7 .set(zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8).get($sv10), $sv11, enc));
                    $sv11 = 1;
                    if (!$sv7) {
                        return ;
                    }
                }
            }
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        return $sv7;
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFMrgOutDct() {
        $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get(Value.nullValue()), (- 1));
        if ($sv8 == Value.nullValue()) {
            return $sv6;
        }
        if (!Reflect.dataType(VariableContext.lookup(varName).get($sv8).get(1))) {
            $sv8 .set(Value.nullValue());
            while (true) {
                $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8));
                if ($sv8 == Value.nullValue()) {
                    return ;
                }
                $sv7 .set(zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8), $sv8, enc));
                if (!$sv7) {
                    return ;
                }
            }
        } else {
            $sv8 .set(Value.nullValue());
            while (true) {
                $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8));
                if ($sv8 == Value.nullValue()) {
                    return ;
                }
                $sv10 = 0;
                while (true) {
                    $sv10 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8).get($sv10));
                    if ($sv10 == Value.nullValue()) {
                        return ;
                    }
                    $sv7 .set(zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8).get($sv10), $sv8, enc));
                    if (!$sv7) {
                        return ;
                    }
                }
            }
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        return $sv7;
    }

    /**
     * 
     */
    public Value zECFTblMrgOut(final Value propName, final Value parentPropId, final Value varName, final Value enc) {
        $sv6 .set(Value.nullValue());
        $sv7 .set(Value.nullValue());
        $sv8 .set(Value.nullValue());
        $sv9 .set(Value.nullValue());
        enc.set(($ecfEncrypt||enc));
        $sv6 .set(zECFNew(propName, parentPropId, "T", enc));
        while (true) {
            $sv7 = Builtin.followingKey(VariableContext.lookup(varName).get(0).get($sv7));
            if ($sv7 == Value.nullValue()) {
                return ;
            }
            VariableContext.lookup(varName).get(0).get($sv7).set(zECFNewCol($sv6, $sv7, enc));
        }
        $sv9 = 1;
        $sv8 = 0;
        while (true) {
            $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8));
            if ($sv8 == Value.nullValue()) {
                return ;
            }
            do {
                while (true) {
                    $sv7 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8).get($sv7));
                    if ($sv7 == Value.nullValue()) {
                        return ;
                    }
                    if (Reflect.dataType(VariableContext.lookup(varName).get($sv8).get($sv7))) {
                        cells.get(VariableContext.lookup(varName).get(0).get($sv7)).set(VariableContext.lookup(varName).get($sv8).get($sv7));
                    }
                }
                $sv9 .set(zECFSetRow($sv6, cells, enc));
                if (!$sv9) {
                    return ;
                }
                cells.set(Value.nullValue());
                VariableContext.lookup(varName).get($sv8).set(Value.nullValue());
            } while (false);
            if (!$sv9) {
                return ;
            }
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        return $sv9;
    }

    /**
     * 
     */
    public Value zECFNewTbl(final Value propName, final Value parentPropId, final Value colDefs, final Value enc) {
        $ecfFtrs.set(($ecfFtrs + 1));
        $sv1 .set($ecfFtrs);
        if ($ecfMode == (- 1)) {
            return $sv1;
        }
        enc.set(($ecfEncrypt||enc));
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        if (enc) {
            $.set(zcrypt(propName, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), propName));
        }
        if (((Text.length($ecfOutBuf)+ Text.length(propName))+ 12)>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +((((((Text.character(4)+ Text.character(64))+ Text.character((enc* 128)))+ zchrL($sv1))+ Text.character((Text.length(propName)+ 4)))+ zchrL(parentPropId))+ propName));
        $sv2 = 0;
        $zzECFNewTblCol:
        $sv2 .set(($sv2 + 1));
        if ($sv2 >colDefs) {
            try {
                $zzECFNewTblDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $sv3 .set(Value.nullValue());
        $sv4 .set(Value.nullValue());
        $ecfFtrs.get($sv1).set($sv2);
        while (true) {
            $sv3 = Builtin.followingKey(colDefs);
            if ($sv3 == Value.nullValue()) {
                return ;
            }
            $sv5 .set(colDefs);
            $sv4 = (((($sv4 + Text.character(Text.length($sv3)))+ $sv3)+ Text.character(Text.length($sv5)))+ $sv5);
        }
        if (enc) {
            $.set(zcrypt($sv4, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), $sv4));
        }
        if (((Text.length($ecfOutBuf)+ Text.length($sv4))+ 9)>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(20)+ Text.character(80))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrW(Text.length($sv4)))+ $sv4));
        try {
            $zzECFNewTblCol();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFNewTblCol() {
        $sv2 .set(($sv2 + 1));
        if ($sv2 >colDefs) {
            try {
                $zzECFNewTblDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $sv3 .set(Value.nullValue());
        $sv4 .set(Value.nullValue());
        $ecfFtrs.get($sv1).set($sv2);
        while (true) {
            $sv3 = Builtin.followingKey(colDefs);
            if ($sv3 == Value.nullValue()) {
                return ;
            }
            $sv5 .set(colDefs);
            $sv4 = (((($sv4 + Text.character(Text.length($sv3)))+ $sv3)+ Text.character(Text.length($sv5)))+ $sv5);
        }
        if (enc) {
            $.set(zcrypt($sv4, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), $sv4));
        }
        if (((Text.length($ecfOutBuf)+ Text.length($sv4))+ 9)>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(20)+ Text.character(80))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrW(Text.length($sv4)))+ $sv4));
        try {
            $zzECFNewTblCol();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFNewTblDone() {
        $sv4 .set(Value.nullValue());
        $sv5 .set(Value.nullValue());
        return $sv1;
    }

    /**
     * 
     */
    public Value zECFSetTblPrms(final Value tblId, final Value params, final Value enc) {
        if ($ecfMode == (- 1)) {
            return  0;
        }
        enc.set(($ecfEncrypt||enc));
        $sv3 .set(Value.nullValue());
        $sv4 .set(Value.nullValue());
        while (true) {
            $sv3 = Builtin.followingKey(params);
            if ($sv3 == Value.nullValue()) {
                return ;
            }
            $sv5 .set(params);
            $sv4 = (((($sv4 + Text.character(Text.length($sv3)))+ $sv3)+ Text.character(Text.length($sv5)))+ $sv5);
        }
        if (enc) {
            $.set(zcrypt($sv4, $ecfToken, (ju()+ $ecfToken), Value.nullValue(), Value.nullValue(), $sv4));
        }
        if (((Text.length($ecfOutBuf)+ Text.length($sv4))+ 9)>= $ecfPckSz) {
            $.set(zECFSndRsltPckt());
        }
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(20)+ Text.character(96))+ Text.character((enc* 128)))+ zchrL(tblId))+ zchrW(Text.length($sv4)))+ $sv4));
        return  1;
    }

    /**
     * zECFSndRsltPckt;
     * Q:%ecfAsync 0
     * S %ecfOutBuf="L"_%ecfOutBuf
     * U $$zECFTCPDev()
     * W $$zchrW(0),$$zchrW($L(%ecfOutBuf)),%ecfOutBuf
     * U $$zUniqNullDev()
     * D:%ecfDbg LgRespLn^EGECFS2(%elogID,"Body",%ecfOutBuf)
     * D:%EA("E","EI","Level") logPacket^EGEI("RESP",$L(%ecfOutBuf)+4)
     * S %ecfOutBuf=""
     * 
     * 
     */
    public Value zECFSndRsltPckt() {
        if ($ecfAsync) {
            return  0;
        }
        $ecfOutBuf = ("L"+ $ecfOutBuf);
        ReadWrite.use(zECFTCPDev());
        ReadWrite.write(zchrW(0), zchrW(Text.length($ecfOutBuf)), $ecfOutBuf);
        ReadWrite.use(zUniqNullDev());
        if ($ecfDbg) {
            EGECFS2 .lgRespLn($elogID, "Body", $ecfOutBuf);
        }
        if ($EA) {
            EGEI.logPacket("RESP", (Text.length($ecfOutBuf)+ 4));
        }
        $ecfOutBuf.set(Value.nullValue());
        return  1;
    }

    /**
     * 
     */
    public Value zECFTCPDev() {
        return $ecfCurrTCPdev;
    }

    /**
     * 
     */
    public Value zShrToStr(final Value short) {
        return zascW(_short_);
    }

    /**
     * 
     */
    public Value zIntToStr(final Value int) {
        return zascL(_int_);
    }

    /**
     * 
     */
    public Value zStrToShr(final boolean str, final Value ltlEndian) {
        return ((!ltlEndian)?zchrW(str):(true?Text.character((str% 256), (str/ 256)):Value.nullValue()));
    }

    /**
     * 
     */
    public Value zStrToInt(final Value str) {
        return zchrL(str);
    }

    /**
     * 
     */
    public Value zECFTmout() {
        return ((zPerfTimer()<$ecfSrvTmout)? 0 :(true? 1 :Value.nullValue()));
    }

    /**
     * 
     */
    public Value zECFThrow(final boolean zzErrCode, final Value zzDetails, final Value zzNoLog) {
        if (zzErrCode == Value.nullValue()) {
            Builtin.ecode().set(",U-EMPTY-THROW-REASON,");
        }
        Value zzCodeLn = Value.nullValue();
        Value zzErrNum = Value.nullValue();
        zzCodeLn.set((Reflect.stackInfo((- 1))- 1));
        zzCodeLn = Reflect.stackInfo(zzCodeLn, "PLACE");
        if (!zzNoLog) {
            $zErrLog(("EGECFS2: <zECFThrow>"+ zzCodeLn), Value.nullValue(), 1, zzErrNum);
            zzErrNum = Text.piece(zzErrNum, ",", 2);
        }
        EGECFS2 .sndError(zzErrCode, zzDetails, zzErrNum, zzCodeLn);
        zzErrCode = ((((Text.extract(zzErrCode, 1, 3) == ",U-")?Value.nullValue():(true?",U-":Value.nullValue()))+ zzErrCode)+((Text.extract(zzErrCode, Text.length(zzErrCode)) == ",")?Value.nullValue():(true?",":Value.nullValue())));
        $ecfFromThrow = 1;
        Builtin.ecode().set(zzErrCode);
        return  1;
    }

}
