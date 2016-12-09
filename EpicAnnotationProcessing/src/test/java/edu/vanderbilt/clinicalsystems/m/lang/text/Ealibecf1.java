
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

    public Value zzErrNum;
    public Value data;
    public boolean zzNoLog;
    public Value zzCodeLn;
    public Value parentPropId;
    public Value _ecfInBufLoc;
    public Value mode;
    public Value _ecfToken;
    public Value merge;
    public Value appendToPrevious;
    public Value _ecfMode;
    public Value _int;
    public boolean ltlEndian;
    public Value zzDetails;
    public Value colDefs;
    public Value params;
    public boolean _ecfEncrypt;
    public boolean _ecfAsync;
    public Value str;
    public Value tmpGlo;
    public boolean _EA;
    public boolean shared;
    public Value varName;
    public Value _sv1;
    public Value _ecfCurrTCPdev;
    public boolean _ecfDbg;
    public Value propName;
    public Value propStore;
    public Value _sv10;
    public Value _sv11;
    public Value _sv6;
    public Value _sv7;
    public boolean _ecfStreamCmd;
    public Value _sv8;
    public Value row;
    public Value enc;
    public Value _sv9;
    public Value _sv2;
    public Value _short;
    public Value _sv3;
    public Value value;
    public Value key;
    public Value _sv4;
    public Value _sv5;
    public Value containerPropId;
    public Value more;
    public Value lineOrKey;
    public Value column;
    public Value _ecfOutBuf;
    public Value propNameOrId;
    public Value tblId;
    public Value _ecfFtrs;
    public Value _ecfSrvTmout;
    public Value cells;
    public Value tableId;
    public Value dataStore;
    public boolean _ecfVersion;
    public Value zzErrCode;
    public Value errRsn;
    public Value _ecfPckSz;
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
            _ecfMode = (((("-1"+ Text.character(1))+ tmpGlo)+ Text.character(1))+"1");
        } else {
            tmpGlo.set(ZefnLib.acquirePrivateTempGlobal());
            _ecfMode = (("-1"+ Text.character(1))+ tmpGlo);
        }
        VariableContext.lookup(tmpGlo).set((- 1));
        return _ecfMode;
    }

    /**
     * zECFSetDestClient;
     * ;;#strip# only set the first piece, to leave the global piece intact for future calls to zECFGet* with negative numbers
     * S $P(%ecfMode,$C(1),1)=0
     * 
     * 
     */
    public Value zECFSetDestClient() {
        Text.pieceAssign(_ecfMode, Text.character(1), 1, 0);
        return _ecfMode;
    }

    /**
     * 
     */
    public Value zECFInitDest(final Value mode) {
        _ecfMode.set(mode);
        return  1;
    }

    /**
     * zECFGetDest;
     * S:%ecfMode="" %ecfMode=0
     * 
     * 
     */
    public Value zECFGetDest() {
        if (_ecfMode == Value.nullValue()) {
            _ecfMode = 0;
        }
        return _ecfMode;
    }

    /**
     * 
     */
    public Value zECFReleaseDestGlobal(final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set(_ecfMode);
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
        _sv1 .set(Value.nullValue());
        if (mode == Value.nullValue()) {
            mode.set(_ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        _sv5 = Text.piece(mode, Text.character(1), 2);
        _sv5 .set((Text.follows(Object.toString(), Object.toString())?_sv5 :(true?_ecfInBufLoc:Value.nullValue())));
        if (more == 0) {
            more = 1;
        }
        while (true) {
            _sv1 = (_sv1 + VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(1).get(more));
            more = Builtin.followingKey(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(1).get(more));
            if (more == Value.nullValue()) {
                return ;
            }
            if ((Text.length(_sv1)+ Text.length(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(1).get(more)))>_EA) {
                return ;
            }
        }
        return _sv1;
    }

    /**
     * 
     */
    public Value zECFGetWasSent(final Value propName, final Value parentPropId, final Value lineOrKey, final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set(_ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        _sv5 = Text.piece(mode, Text.character(1), 2);
        _sv5 .set((Text.follows(Object.toString(), Object.toString())?_sv5 :(true?_ecfInBufLoc:Value.nullValue())));
        if (Text.follows(Object.toString(), Object.toString())) {
            return Text.follows(Builtin.followingKey(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(lineOrKey).get(Value.nullValue())), Object.toString());
        }
        if (!_ecfVersion) {
            do {
                Value errRsn = Value.nullValue();
                errRsn.set("The communication assembly Epic.Core.Ecf.dll does not support ");
                errRsn = (errRsn +"ignoring properties and renders a call to $$zECFGetWasSent invalid.");
                _.set(zECFThrow("CLIENT-CANNOT-IGNORE-PROPERTIES", errRsn));
            } while (false);
        }
        return (Builtin.followingKey(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(Value.nullValue()))!= Value.nullValue());
    }

    /**
     * 
     */
    public Value zECFGetElmt(final Value propName, final Value parentPropId, final Value lineOrKey, final Value more, final Value mode) {
        _sv1 .set(Value.nullValue());
        if (mode == Value.nullValue()) {
            mode.set(_ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        _sv5 = Text.piece(mode, Text.character(1), 2);
        _sv5 .set((Text.follows(Object.toString(), Object.toString())?_sv5 :(true?_ecfInBufLoc:Value.nullValue())));
        if (more == Value.nullValue()) {
            if (zbitAnd(VariableContext.lookup(_sv5).get(parentPropId).get(propName), 32) == 32) {
                if (VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(lineOrKey).get(1) == 0) {
                    return Value.nullValue();
                }
            }
        }
        if (more == Value.nullValue()) {
            more = ("1,"+ VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(lineOrKey).get(0));
        }
        while (true) {
            _sv1 = (_sv1 + VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(lineOrKey).get(more));
            if ((1 + more)>Text.piece(more, ",", 2)) {
                more.set(Value.nullValue());
            }
            if (more == Value.nullValue()) {
                return ;
            }
            Text.pieceAssign(more, ",", Object.toString());
            if ((Text.length(_sv1)+ Text.length(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(lineOrKey).get(more)))>_EA) {
                return ;
            }
        }
        return _sv1;
    }

    /**
     * 
     */
    public Value zECFGetCell(final Value propName, final Value parentPropId, final Value row, final Value column, final Value more, final Value mode) {
        _sv1 .set(Value.nullValue());
        if (mode == Value.nullValue()) {
            mode.set(_ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        _sv5 = Text.piece(mode, Text.character(1), 2);
        _sv5 .set((Text.follows(Object.toString(), Object.toString())?_sv5 :(true?_ecfInBufLoc:Value.nullValue())));
        if (more == Value.nullValue()) {
            more = 1;
        }
        while (true) {
            _sv1 = (_sv1 + VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(row).get(column).get(more));
            more = Builtin.followingKey(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(row).get(column).get(more));
            if (more == Value.nullValue()) {
                return ;
            }
            if ((Text.length(_sv1)+ Text.length(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(row).get(column).get(more)))>_EA) {
                return ;
            }
        }
        return _sv1;
    }

    /**
     * 
     */
    public Value zECFNumElmts(final Value propName, final Value parentPropId, final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set(_ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        _sv5 = Text.piece(mode, Text.character(1), 2);
        return VariableContext.lookup(Object.toString()).get(parentPropId).get(propName).get(0);
    }

    /**
     * 
     */
    public Value zECFDctNxKey(final Value propName, final Value parentPropId, final Value key, final Value mode) {
        if (mode == Value.nullValue()) {
            mode.set(_ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        _sv5 .set(Value.nullValue());
        _sv5 = Text.piece(mode, Text.character(1), 2);
        _sv5 .set((Text.follows(Object.toString(), Object.toString())?_sv5 :(true?_ecfInBufLoc:Value.nullValue())));
        while (true) {
            key = Builtin.followingKey(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(key));
            if (key == Value.nullValue()) {
                return ;
            }
            if (Reflect.dataType(VariableContext.lookup(_sv5).get(parentPropId).get(propName).get(key))> 1) {
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
            mode.set(_ecfMode);
        }
        if (parentPropId == Value.nullValue()) {
            parentPropId.set(mode);
        }
        _sv5 = Text.piece(mode, Text.character(1), 2);
        if (row == Value.nullValue()) {
            row = 1;
        }
        return Builtin.followingKey(VariableContext.lookup(Object.toString()).get(parentPropId).get(propName).get(row).get(column));
    }

    /**
     * 
     */
    public Value zECFMrgIn(final Value propName, final Value parentPropId, final Value varName) {
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        VariableContext.lookup(varName).set(VariableContext.lookup(_ecfInBufLoc).get(parentPropId).get(propName));
        VariableContext.lookup(_ecfInBufLoc).get(parentPropId).get(propName).set(Value.nullValue());
        return  1;
    }

    /**
     * 
     */
    public Value zECFTblMrgIn(final Value propName, final Value parentPropId, final Value varName) {
        _sv1 .set(Value.nullValue());
        _sv2 .set(Value.nullValue());
        _sv3 .set(Value.nullValue());
        _sv4 .set(Value.nullValue());
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        VariableContext.lookup(varName).set(VariableContext.lookup(_ecfInBufLoc).get(parentPropId).get(propName));
        VariableContext.lookup(_ecfInBufLoc).get(parentPropId).get(propName).set(Value.nullValue());
        for (int%sv1 = 1; (%sv1 >= VariableContext.lookup(varName).get(0)); %sv1 ++) {
            do {
                while (true) {
                    _sv2 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv1).get(_sv2));
                    if (_sv2 == Value.nullValue()) {
                        return ;
                    }
                    do {
                        for (int%sv3 = 1; (%sv3 >= VariableContext.lookup(varName).get(_sv1).get(_sv2).get(0)); %sv3 ++) {
                            do {
                                VariableContext.lookup(varName).get(_sv1).get(_sv2).set((VariableContext.lookup(varName).get(_sv1).get(_sv2)+ VariableContext.lookup(varName).get(_sv1).get(_sv2).get(_sv3)));
                                VariableContext.lookup(varName).get(_sv1).get(_sv2).get(_sv3).set(Value.nullValue());
                            } while (false);
                        }
                        VariableContext.lookup(varName).get(_sv1).get(_sv2).get(0).set(Value.nullValue());
                    } while (false);
                }
            } while (false);
        }
        while (true) {
            _sv4 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv4));
            if (_sv4 == Value.nullValue()) {
                return ;
            }
            while (true) {
                _sv2 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv4).get(_sv2));
                if (_sv2 == Value.nullValue()) {
                    return ;
                }
                if (!Reflect.dataType(VariableContext.lookup(varName).get(0).get(_sv2))) {
                    VariableContext.lookup(varName).get(0).get(_sv2).set(Math.increment(VariableContext.lookup(varName).get(0)));
                }
            }
        }
        return  1;
    }

    /**
     * 
     */
    public Value zECFNew(final Value propName, final Value parentPropId, final Value propStore, final Value merge, final Value enc) {
        if (_ecfMode == (- 1)) {
            try {
                EGECFUTL._zzECFNewGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set((_ecfEncrypt||enc));
        _ecfFtrs.set((_ecfFtrs + 1));
        _sv1 .set(_ecfFtrs);
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        if (enc) {
            _.set(zcrypt(propName, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), propName));
        }
        if (((Text.length(_ecfOutBuf)+ Text.length(propName))+ 12)>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +((((((Text.character(2)+ Text.character(((propStore == "S")? 0 :((propStore == "A")? 16 :((propStore == "L")? 16 :((propStore == "D")? 32 :(true? 0 :Value.nullValue())))))))+ Text.character(((merge* 64)+(enc* 128))))+ zchrL(_sv1))+ zchrL(parentPropId))+ Text.character(Text.length(propName)))+ propName));
        return _sv1;
    }

    /**
     * 
     */
    public Value zECFNewElmtObj(final boolean containerPropId, final Value key, final Value enc) {
        if (_ecfMode == (- 1)) {
            try {
                EGECFUTL._zzECFNewElmtObjGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set((_ecfEncrypt||enc));
        _ecfFtrs.set((_ecfFtrs + 1));
        _sv1 .set(_ecfFtrs);
        if (enc) {
            if (Text.follows(Object.toString(), Object.toString())) {
                _.set(zcrypt(key, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), key));
            }
        }
        if ((Text.length(_ecfOutBuf)+(Text.follows(Object.toString(), Object.toString())?(Text.length(key)+ 12):(true? 11 :Value.nullValue())))>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        if (Text.follows(Object.toString(), Object.toString())) {
            _ecfOutBuf = (_ecfOutBuf +((((((Text.character(2)+ Text.character(2))+ Text.character((enc* 128)))+ zchrL(_sv1))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key));
        } else {
            _ecfOutBuf = (_ecfOutBuf +((((Text.character(2)+ Text.character(1))+ Text.character((enc* 128)))+ zchrL(_sv1))+ zchrL(containerPropId)));
        }
        return _sv1;
    }

    /**
     * 
     */
    public Value zECFReset(final boolean propName, final Value parentPropId, final Value enc) {
        if (_ecfMode == (- 1)) {
            try {
                EGECFUTL._zzECFResetGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set((_ecfEncrypt||enc));
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        if (enc) {
            if (Text.follows(Object.toString(), Object.toString())) {
                _.set(zcrypt(propName, _ecfToken, (ju()+ _ecfToken), propName));
            }
        }
        if (((Text.length(_ecfOutBuf)+ Text.length(propName))+ 8)>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +(((((Text.character(3)+ Text.character(0))+ Text.character(0))+ zchrL(parentPropId))+ Text.character(Text.length(propName)))+ propName));
        return  1;
    }

    /**
     * 
     */
    public Value zECFNewCol(final boolean tableId, final Value columnName, final Value enc) {
        if (_ecfMode == (- 1)) {
            try {
                EGECFUTL._zzECFNewColGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set((_ecfEncrypt||enc));
        _ecfFtrs.get(tableId).set((_ecfFtrs + 1));
        _sv1 .set(_ecfFtrs);
        if (enc) {
            _.set(zcrypt(columnName, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), columnName));
        }
        if (((Text.length(_ecfOutBuf)+ Text.length(columnName))+ 10)>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +((((((Text.character(1)+ Text.character(0))+ Text.character(0))+ zchrL(tableId))+ zchrW(_sv1))+ Text.character(Text.length(columnName)))+ columnName));
        return _sv1;
    }

    /**
     * 
     */
    public Value zECFSet(final Value propNameOrId, final Value value, final Value parentPropId, final Value enc) {
        _sv1 .set(propNameOrId);
        if (_ecfMode == (- 1)) {
            try {
                EGECFUTL._zzECFSetGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set((_ecfEncrypt||enc));
        if (_sv1 == propNameOrId) {
            try {
                _zzECFSetValSwitch();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfFtrs.set((_ecfFtrs + 1));
        _sv1 .set(_ecfFtrs);
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        if (enc) {
            _.set(zcrypt(propNameOrId, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), propNameOrId));
        }
        if (((Text.length(_ecfOutBuf)+ Text.length(propNameOrId))+ 12)>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +((((((Text.character(2)+ Text.character(0))+ Text.character((enc* 128)))+ zchrL(_sv1))+ zchrL(parentPropId))+ Text.character(Text.length(propNameOrId)))+ propNameOrId));
        _zzECFSetValSwitch:
        if (enc) {
            try {
                _zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _zzECFSetVal:
        _sv2 .set(Value.nullValue());
        _sv3 = Text.length(value);
        if (_sv3 == 0) {
            if ((Text.length(_ecfOutBuf)+ 9)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL(_sv1))+ zchrW(0)));
            return _sv1;
        }
        if (((Text.length(_ecfOutBuf)+ _sv3)+ 9)>= _ecfPckSz) {
            _sv4 .set(((_ecfPckSz-Text.length(_ecfOutBuf))- 8));
            _sv2 .set(Text.extractAssign(value, (_sv4 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv4));
            _sv3 = Text.length(value);
        }
        if ((_sv3 > 0)||(Text.length(_sv2) == 0)) {
            _ecfOutBuf = (_ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL(_sv1))+ zchrW(_sv3))+ value));
        }
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            try {
                _zzECFSetVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return _sv1;
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
    public Value _zzECFSetValSwitch() {
        if (enc) {
            try {
                _zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _zzECFSetVal:
        _sv2 .set(Value.nullValue());
        _sv3 = Text.length(value);
        if (_sv3 == 0) {
            if ((Text.length(_ecfOutBuf)+ 9)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL(_sv1))+ zchrW(0)));
            return _sv1;
        }
        if (((Text.length(_ecfOutBuf)+ _sv3)+ 9)>= _ecfPckSz) {
            _sv4 .set(((_ecfPckSz-Text.length(_ecfOutBuf))- 8));
            _sv2 .set(Text.extractAssign(value, (_sv4 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv4));
            _sv3 = Text.length(value);
        }
        if ((_sv3 > 0)||(Text.length(_sv2) == 0)) {
            _ecfOutBuf = (_ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL(_sv1))+ zchrW(_sv3))+ value));
        }
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            try {
                _zzECFSetVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return _sv1;
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value _zzECFSetVal() {
        _sv2 .set(Value.nullValue());
        _sv3 = Text.length(value);
        if (_sv3 == 0) {
            if ((Text.length(_ecfOutBuf)+ 9)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL(_sv1))+ zchrW(0)));
            return _sv1;
        }
        if (((Text.length(_ecfOutBuf)+ _sv3)+ 9)>= _ecfPckSz) {
            _sv4 .set(((_ecfPckSz-Text.length(_ecfOutBuf))- 8));
            _sv2 .set(Text.extractAssign(value, (_sv4 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv4));
            _sv3 = Text.length(value);
        }
        if ((_sv3 > 0)||(Text.length(_sv2) == 0)) {
            _ecfOutBuf = (_ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL(_sv1))+ zchrW(_sv3))+ value));
        }
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            try {
                _zzECFSetVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return _sv1;
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
    public Value _zzECFSetValEnc() {
        if (Text.length(value) == 0) {
            if ((Text.length(_ecfOutBuf)+ 9)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(128))+ zchrL(_sv1))+ zchrW(0)));
            return _sv1;
        }
        _sv2 .set(Value.nullValue());
        _sv5 .set(((_ecfPckSz-Text.length(_ecfOutBuf))- 9));
        _sv5 .set((_sv5 -(_sv5 % 4)));
        _sv3 .set((_sv5 -(_sv5 / 32.0D)));
        _sv3 .set((_sv3 /(4 * 3)));
        if (Text.length(value)>_sv3) {
            _sv2 .set(Text.extractAssign(value, (_sv3 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv3));
        }
        if ((_sv3 > 0)||(Text.length(_sv2) == 0)) {
            _.set(zcrypt(value, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), value));
            value.set(Text.extractAssign(value, 1, (Text.length(value)- 1)));
            _ecfOutBuf = (_ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(128))+ zchrL(_sv1))+ zchrW(Text.length(value)))+ value));
        }
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            try {
                _zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return _sv1;
    }

    /**
     * 
     */
    public Value zECFSetElmt(final boolean containerPropId, final Value value, final Value key, final Value appendToPrevious, final Value enc) {
        if (_ecfMode == (- 1)) {
            try {
                EGECFUTL._zzECFSetElmtGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set((_ecfEncrypt||enc));
        if ((!enc)&&(key == Value.nullValue())) {
            try {
                _zzECFSetElmtArrVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        if (!enc) {
            try {
                _zzECFSetElmtDctVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        if (key == Value.nullValue()) {
            try {
                _zzECFSetElmtArrValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _.set(zcrypt(key, _ecfToken, (ju()+ _ecfToken), key));
        _zzECFSetElmtDctValEnc:
        if (!Text.length(value)) {
            if (((Text.length(_ecfOutBuf)+ Text.length(key))+ 10)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0)));
            return containerPropId;
        }
        _sv2 .set(Value.nullValue());
        _sv5 .set((((_ecfPckSz-Text.length(_ecfOutBuf))-Text.length(key))- 9));
        _sv5 .set((_sv5 -(_sv5 % 4)));
        _sv3 .set((_sv5 -(_sv5 / 32.0D)));
        _sv3 .set((_sv3 /(4 * 3)));
        if (Text.length(value)>_sv3) {
            _sv2 .set(Text.extractAssign(value, (_sv3 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv3));
        }
        _.set(zcrypt(value, _ecfToken, (ju()+ _ecfToken), value));
        value.set(Text.extractAssign(value, 1, (Text.length(value)- 1)));
        _ecfOutBuf = (_ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(Text.length(value)))+ value));
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            try {
                _zzECFSetElmtDctValEnc();
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
    public Value _zzECFSetElmtDctValEnc() {
        if (!Text.length(value)) {
            if (((Text.length(_ecfOutBuf)+ Text.length(key))+ 10)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0)));
            return containerPropId;
        }
        _sv2 .set(Value.nullValue());
        _sv5 .set((((_ecfPckSz-Text.length(_ecfOutBuf))-Text.length(key))- 9));
        _sv5 .set((_sv5 -(_sv5 % 4)));
        _sv3 .set((_sv5 -(_sv5 / 32.0D)));
        _sv3 .set((_sv3 /(4 * 3)));
        if (Text.length(value)>_sv3) {
            _sv2 .set(Text.extractAssign(value, (_sv3 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv3));
        }
        _.set(zcrypt(value, _ecfToken, (ju()+ _ecfToken), value));
        value.set(Text.extractAssign(value, 1, (Text.length(value)- 1)));
        _ecfOutBuf = (_ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(Text.length(value)))+ value));
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            try {
                _zzECFSetElmtDctValEnc();
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
    public Value _zzECFSetElmtArrVal() {
        _sv2 .set(Value.nullValue());
        _sv3 = Text.length(value);
        if (_sv3 == 0) {
            if ((Text.length(_ecfOutBuf)+ 9)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((Text.character(0)+ Text.character(16))+ Text.character((appendToPrevious* 64)))+ zchrL(containerPropId))+ zchrW(0)));
            return containerPropId;
        }
        if (((Text.length(_ecfOutBuf)+ _sv3)+ 9)>= _ecfPckSz) {
            _sv4 .set(((_ecfPckSz-Text.length(_ecfOutBuf))- 8));
            _sv2 .set(Text.extractAssign(value, (_sv4 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv4));
            _sv3 = Text.length(value);
        }
        _ecfOutBuf = (_ecfOutBuf +(((((Text.character(0)+ Text.character(16))+ Text.character((appendToPrevious* 64)))+ zchrL(containerPropId))+ zchrW(_sv3))+ value));
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            appendToPrevious = 1;
            try {
                _zzECFSetElmtArrVal();
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
    public Value _zzECFSetElmtDctVal() {
        _sv2 .set(Value.nullValue());
        _sv3 = Text.length(value);
        if (_sv3 == 0) {
            if (((Text.length(_ecfOutBuf)+ Text.length(key))+ 10)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(0))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0)));
            return containerPropId;
        }
        if ((((Text.length(_ecfOutBuf)+ _sv3)+ Text.length(key))+ 10)>= _ecfPckSz) {
            _sv4 .set((((_ecfPckSz-Text.length(_ecfOutBuf))-Text.length(key))- 9));
            _sv2 .set(Text.extractAssign(value, (_sv4 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv4));
            _sv3 = Text.length(value);
        }
        _ecfOutBuf = (_ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(0))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(_sv3))+ value));
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            try {
                _zzECFSetElmtDctVal();
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
    public Value _zzECFSetElmtArrValEnc() {
        if (!Text.length(value)) {
            if ((Text.length(_ecfOutBuf)+ 9)>_ecfPckSz) {
                _.set(zECFSndRsltPckt());
            }
            _ecfOutBuf = (_ecfOutBuf +((((Text.character(0)+ Text.character(16))+ Text.character((128 +(appendToPrevious* 64))))+ zchrL(containerPropId))+ zchrW(0)));
            return containerPropId;
        }
        _sv2 .set(Value.nullValue());
        _sv5 .set(((_ecfPckSz-Text.length(_ecfOutBuf))- 8));
        _sv5 .set((_sv5 -(_sv5 % 4)));
        _sv3 .set((_sv5 -(_sv5 / 32.0D)));
        _sv3 .set((_sv3 /(4 * 3)));
        if (Text.length(value)>_sv3) {
            _sv2 .set(Text.extractAssign(value, (_sv3 + 1), "999999999"));
            value.set(Text.extractAssign(value, 1, _sv3));
        }
        _.set(zcrypt(value, _ecfToken, (ju()+ _ecfToken), value));
        value.set(Text.extractAssign(value, 1, (Text.length(value)- 1)));
        _ecfOutBuf = (_ecfOutBuf +(((((Text.character(0)+ Text.character(16))+ Text.character((128 +(appendToPrevious* 64))))+ zchrL(containerPropId))+ zchrW(Text.length(value)))+ value));
        if (Text.follows(Object.toString(), Object.toString())) {
            _.set(zECFSndRsltPckt());
            value.set(_sv2);
            appendToPrevious = 1;
            try {
                _zzECFSetElmtArrValEnc();
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
        if (_ecfMode == (- 1)) {
            try {
                EGECFUTL._zzECFSetRowGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.set((_ecfEncrypt||enc));
        _sv1 = 0;
        _sv2 .set(_ecfFtrs);
        if ((Text.length(_ecfOutBuf)+ 9)>_ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +(((Text.character(0)+ Text.character(48))+ Text.character((enc* 128)))+ zchrL(tableId)));
        if (enc) {
            try {
                _zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _zzECFSetRowCell:
        _sv1 .set((_sv1 + 1));
        value.set(cells);
        _sv3 = Text.length(value);
        if (((Text.length(_ecfOutBuf)+ _sv3)+ 6)>_ecfPckSz) {
            _ecfOutBuf = (_ecfOutBuf + zchrW(0));
            _.set(zECFSndRsltPckt());
        } else {
            _ecfOutBuf = (_ecfOutBuf +((zchrW(_sv1)+ zchrW(_sv3))+ value));
            try {
                _zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId))+ zchrW(_sv1))+ zchrW(_sv3))+ value);
        if ((_sv3 + 13)<= _ecfPckSz) {
            try {
                _zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = (_ecfOutBuf + zchrW(0));
        _.set(zECFSndRsltPckt());
        if (_sv1 == _sv2) {
            try {
                _zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId));
        _zzECFSetRowCellFnsh:
        if (_sv1 <_sv2) {
            try {
                _zzECFSetRowCell();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            _zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value _zzECFSetRowCell() {
        _sv1 .set((_sv1 + 1));
        value.set(cells);
        _sv3 = Text.length(value);
        if (((Text.length(_ecfOutBuf)+ _sv3)+ 6)>_ecfPckSz) {
            _ecfOutBuf = (_ecfOutBuf + zchrW(0));
            _.set(zECFSndRsltPckt());
        } else {
            _ecfOutBuf = (_ecfOutBuf +((zchrW(_sv1)+ zchrW(_sv3))+ value));
            try {
                _zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId))+ zchrW(_sv1))+ zchrW(_sv3))+ value);
        if ((_sv3 + 13)<= _ecfPckSz) {
            try {
                _zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = (_ecfOutBuf + zchrW(0));
        _.set(zECFSndRsltPckt());
        if (_sv1 == _sv2) {
            try {
                _zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId));
        _zzECFSetRowCellFnsh:
        if (_sv1 <_sv2) {
            try {
                _zzECFSetRowCell();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            _zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value _zzECFSetRowCellFnsh() {
        if (_sv1 <_sv2) {
            try {
                _zzECFSetRowCell();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            _zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value _zzECFSetRowCellEnc() {
        _sv1 .set((_sv1 + 1));
        value.set(cells);
        _.set(zcrypt(value, _ecfToken, (ju()+ _ecfToken), value));
        value.set(Text.extractAssign(value, 1, (Text.length(value)- 1)));
        _sv3 = Text.length(value);
        if (((Text.length(_ecfOutBuf)+ _sv3)+ 6)>_ecfPckSz) {
            _ecfOutBuf = (_ecfOutBuf + zchrW(0));
            _.set(zECFSndRsltPckt());
        } else {
            _ecfOutBuf = (_ecfOutBuf +((zchrW(_sv1)+ zchrW(Text.length(value)))+ value));
            try {
                _zzECFSetRowCellEncFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(192))+ zchrL(tableId))+ zchrW(_sv1))+ zchrW(_sv3))+ value);
        if ((_sv3 + 13)<= _ecfPckSz) {
            try {
                _zzECFSetRowCellEncFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = (_ecfOutBuf + zchrW(0));
        _.set(zECFSndRsltPckt());
        if (_sv1 == _sv2) {
            try {
                _zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(192))+ zchrL(tableId));
        _zzECFSetRowCellEncFnsh:
        if (_sv1 <_sv2) {
            try {
                _zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _zzECFSetRowFnsh:
        if (Text.length(_ecfOutBuf)> 0) {
            _ecfOutBuf = (_ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * %zzECFSetRowCellEncFnshG:%sv1<%sv2 %zzECFSetRowCellEnc()
     * %zzECFSetRowFnshS:$L(%ecfOutBuf)>0 %ecfOutBuf=%ecfOutBuf_$$zchrW(0)
     * 
     * 
     */
    public Value _zzECFSetRowCellEncFnsh() {
        if (_sv1 <_sv2) {
            try {
                _zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _zzECFSetRowFnsh:
        if (Text.length(_ecfOutBuf)> 0) {
            _ecfOutBuf = (_ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value _zzECFSetRowFnsh() {
        if (Text.length(_ecfOutBuf)> 0) {
            _ecfOutBuf = (_ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * 
     */
    public Value zECFStream(final Value data) {
        if (!_ecfStreamCmd) {
            _.set(zECFThrow("ECF-INVALID-FUNCTION-CALL", "The zECFStream function can only be used with StreamCommand type ECF commands."));
            return  0;
        }
        _zzECFStreamLenChk:
        _sv1 = Text.length(data);
        _sv2 = Text.length(_ecfOutBuf);
        if (((_sv1 + _sv2)+ 5)>_ecfPckSz) {
            _ecfOutBuf = (_ecfOutBuf + Text.extractAssign(data, 1, ((_ecfPckSz-_sv2)- 5)));
            data.set(Text.extractAssign(data, ((_ecfPckSz-_sv2)- 4), _sv1));
            _.set(zECFSndRsltPckt());
            try {
                _zzECFStreamLenChk();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = (_ecfOutBuf + data);
        return  1;
    }

    /**
     * %zzECFStreamLenChkS %sv1=$L(data),%sv2=$L(%ecfOutBuf);only send up to max packet length at a time, subtract 5 for control characters tacked on in zECFSndRsltPckt
     * I %sv1+%sv2+5>%ecfPckSz S %ecfOutBuf=%ecfOutBuf_$E(data,1,%ecfPckSz-%sv2-5),data=$E(data,%ecfPckSz-%sv2-4,%sv1) S %=$$zECFSndRsltPckt() G %zzECFStreamLenChk()
     * S %ecfOutBuf=%ecfOutBuf_data
     * 
     * 
     */
    public Value _zzECFStreamLenChk() {
        _sv1 = Text.length(data);
        _sv2 = Text.length(_ecfOutBuf);
        if (((_sv1 + _sv2)+ 5)>_ecfPckSz) {
            _ecfOutBuf = (_ecfOutBuf + Text.extractAssign(data, 1, ((_ecfPckSz-_sv2)- 5)));
            data.set(Text.extractAssign(data, ((_ecfPckSz-_sv2)- 4), _sv1));
            _.set(zECFSndRsltPckt());
            try {
                _zzECFStreamLenChk();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _ecfOutBuf = (_ecfOutBuf + data);
        return  1;
    }

    /**
     * 
     */
    public Value zECFMrgOut(final Value propName, final Value parentPropId, final Value varName, final Value dataStore, final Value enc) {
        _sv6 .set(Value.nullValue());
        _sv7 .set(Value.nullValue());
        _sv8 .set(Value.nullValue());
        _sv9 .set(Value.nullValue());
        _sv10 .set(Value.nullValue());
        _sv11 .set(Value.nullValue());
        enc.set((_ecfEncrypt||enc));
        _sv6 .set(zECFNew(propName, parentPropId, dataStore, enc));
        _sv7 = 1;
        if (dataStore == "D") {
            try {
                _zzECFMrgOutDct();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _sv8 = 0;
        _sv9 = 0;
        if (!Reflect.dataType(VariableContext.lookup(varName).get(1).get(1))) {
            while (true) {
                _sv8 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv8));
                if (_sv8 == Value.nullValue()) {
                    return ;
                }
                _sv9 .set((_sv9 + 1));
                _sv7 .set(zECFSetElmt(_sv6, VariableContext.lookup(varName).get(_sv8), enc));
                if (!_sv7) {
                    return ;
                }
            }
        } else {
            while (true) {
                _sv8 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv8));
                if (_sv8 == Value.nullValue()) {
                    return ;
                }
                _sv9 .set((_sv9 + 1));
                _sv10 = 0;
                _sv11 = 0;
                while (true) {
                    _sv10 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv8).get(_sv10));
                    if (_sv10 == Value.nullValue()) {
                        return ;
                    }
                    _sv7 .set(zECFSetElmt(_sv6, VariableContext.lookup(varName).get(_sv8).get(_sv10), _sv11, enc));
                    _sv11 = 1;
                    if (!_sv7) {
                        return ;
                    }
                }
            }
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        return _sv7;
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value _zzECFMrgOutDct() {
        _sv8 = Builtin.followingKey(VariableContext.lookup(varName).get(Value.nullValue()), (- 1));
        if (_sv8 == Value.nullValue()) {
            return _sv6;
        }
        if (!Reflect.dataType(VariableContext.lookup(varName).get(_sv8).get(1))) {
            _sv8 .set(Value.nullValue());
            while (true) {
                _sv8 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv8));
                if (_sv8 == Value.nullValue()) {
                    return ;
                }
                _sv7 .set(zECFSetElmt(_sv6, VariableContext.lookup(varName).get(_sv8), _sv8, enc));
                if (!_sv7) {
                    return ;
                }
            }
        } else {
            _sv8 .set(Value.nullValue());
            while (true) {
                _sv8 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv8));
                if (_sv8 == Value.nullValue()) {
                    return ;
                }
                _sv10 = 0;
                while (true) {
                    _sv10 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv8).get(_sv10));
                    if (_sv10 == Value.nullValue()) {
                        return ;
                    }
                    _sv7 .set(zECFSetElmt(_sv6, VariableContext.lookup(varName).get(_sv8).get(_sv10), _sv8, enc));
                    if (!_sv7) {
                        return ;
                    }
                }
            }
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        return _sv7;
    }

    /**
     * 
     */
    public Value zECFTblMrgOut(final Value propName, final Value parentPropId, final Value varName, final Value enc) {
        _sv6 .set(Value.nullValue());
        _sv7 .set(Value.nullValue());
        _sv8 .set(Value.nullValue());
        _sv9 .set(Value.nullValue());
        enc.set((_ecfEncrypt||enc));
        _sv6 .set(zECFNew(propName, parentPropId, "T", enc));
        while (true) {
            _sv7 = Builtin.followingKey(VariableContext.lookup(varName).get(0).get(_sv7));
            if (_sv7 == Value.nullValue()) {
                return ;
            }
            VariableContext.lookup(varName).get(0).get(_sv7).set(zECFNewCol(_sv6, _sv7, enc));
        }
        _sv9 = 1;
        _sv8 = 0;
        while (true) {
            _sv8 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv8));
            if (_sv8 == Value.nullValue()) {
                return ;
            }
            do {
                while (true) {
                    _sv7 = Builtin.followingKey(VariableContext.lookup(varName).get(_sv8).get(_sv7));
                    if (_sv7 == Value.nullValue()) {
                        return ;
                    }
                    if (Reflect.dataType(VariableContext.lookup(varName).get(_sv8).get(_sv7))) {
                        cells.get(VariableContext.lookup(varName).get(0).get(_sv7)).set(VariableContext.lookup(varName).get(_sv8).get(_sv7));
                    }
                }
                _sv9 .set(zECFSetRow(_sv6, cells, enc));
                if (!_sv9) {
                    return ;
                }
                cells.set(Value.nullValue());
                VariableContext.lookup(varName).get(_sv8).set(Value.nullValue());
            } while (false);
            if (!_sv9) {
                return ;
            }
        }
        VariableContext.lookup(varName).set(Value.nullValue());
        return _sv9;
    }

    /**
     * 
     */
    public Value zECFNewTbl(final Value propName, final Value parentPropId, final Value colDefs, final Value enc) {
        _ecfFtrs.set((_ecfFtrs + 1));
        _sv1 .set(_ecfFtrs);
        if (_ecfMode == (- 1)) {
            return _sv1;
        }
        enc.set((_ecfEncrypt||enc));
        if (parentPropId == Value.nullValue()) {
            parentPropId = 0;
        }
        if (enc) {
            _.set(zcrypt(propName, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), propName));
        }
        if (((Text.length(_ecfOutBuf)+ Text.length(propName))+ 12)>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +((((((Text.character(4)+ Text.character(64))+ Text.character((enc* 128)))+ zchrL(_sv1))+ Text.character((Text.length(propName)+ 4)))+ zchrL(parentPropId))+ propName));
        _sv2 = 0;
        _zzECFNewTblCol:
        _sv2 .set((_sv2 + 1));
        if (_sv2 >colDefs) {
            try {
                _zzECFNewTblDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _sv3 .set(Value.nullValue());
        _sv4 .set(Value.nullValue());
        _ecfFtrs.get(_sv1).set(_sv2);
        while (true) {
            _sv3 = Builtin.followingKey(colDefs);
            if (_sv3 == Value.nullValue()) {
                return ;
            }
            _sv5 .set(colDefs);
            _sv4 = ((((_sv4 + Text.character(Text.length(_sv3)))+ _sv3)+ Text.character(Text.length(_sv5)))+ _sv5);
        }
        if (enc) {
            _.set(zcrypt(_sv4, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), _sv4));
        }
        if (((Text.length(_ecfOutBuf)+ Text.length(_sv4))+ 9)>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +(((((Text.character(20)+ Text.character(80))+ Text.character((enc* 128)))+ zchrL(_sv1))+ zchrW(Text.length(_sv4)))+ _sv4));
        try {
            _zzECFNewTblCol();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value _zzECFNewTblCol() {
        _sv2 .set((_sv2 + 1));
        if (_sv2 >colDefs) {
            try {
                _zzECFNewTblDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        _sv3 .set(Value.nullValue());
        _sv4 .set(Value.nullValue());
        _ecfFtrs.get(_sv1).set(_sv2);
        while (true) {
            _sv3 = Builtin.followingKey(colDefs);
            if (_sv3 == Value.nullValue()) {
                return ;
            }
            _sv5 .set(colDefs);
            _sv4 = ((((_sv4 + Text.character(Text.length(_sv3)))+ _sv3)+ Text.character(Text.length(_sv5)))+ _sv5);
        }
        if (enc) {
            _.set(zcrypt(_sv4, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), _sv4));
        }
        if (((Text.length(_ecfOutBuf)+ Text.length(_sv4))+ 9)>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +(((((Text.character(20)+ Text.character(80))+ Text.character((enc* 128)))+ zchrL(_sv1))+ zchrW(Text.length(_sv4)))+ _sv4));
        try {
            _zzECFNewTblCol();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value _zzECFNewTblDone() {
        _sv4 .set(Value.nullValue());
        _sv5 .set(Value.nullValue());
        return _sv1;
    }

    /**
     * 
     */
    public Value zECFSetTblPrms(final Value tblId, final Value params, final Value enc) {
        if (_ecfMode == (- 1)) {
            return  0;
        }
        enc.set((_ecfEncrypt||enc));
        _sv3 .set(Value.nullValue());
        _sv4 .set(Value.nullValue());
        while (true) {
            _sv3 = Builtin.followingKey(params);
            if (_sv3 == Value.nullValue()) {
                return ;
            }
            _sv5 .set(params);
            _sv4 = ((((_sv4 + Text.character(Text.length(_sv3)))+ _sv3)+ Text.character(Text.length(_sv5)))+ _sv5);
        }
        if (enc) {
            _.set(zcrypt(_sv4, _ecfToken, (ju()+ _ecfToken), Value.nullValue(), Value.nullValue(), _sv4));
        }
        if (((Text.length(_ecfOutBuf)+ Text.length(_sv4))+ 9)>= _ecfPckSz) {
            _.set(zECFSndRsltPckt());
        }
        _ecfOutBuf = (_ecfOutBuf +(((((Text.character(20)+ Text.character(96))+ Text.character((enc* 128)))+ zchrL(tblId))+ zchrW(Text.length(_sv4)))+ _sv4));
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
        if (_ecfAsync) {
            return  0;
        }
        _ecfOutBuf = ("L"+ _ecfOutBuf);
        ReadWrite.use(zECFTCPDev());
        ReadWrite.write(zchrW(0), zchrW(Text.length(_ecfOutBuf)), _ecfOutBuf);
        ReadWrite.use(zUniqNullDev());
        if (_ecfDbg) {
            EGECFS2 .lgRespLn(_elogID, "Body", _ecfOutBuf);
        }
        if (_EA) {
            EGEI.logPacket("RESP", (Text.length(_ecfOutBuf)+ 4));
        }
        _ecfOutBuf.set(Value.nullValue());
        return  1;
    }

    /**
     * 
     */
    public Value zECFTCPDev() {
        return _ecfCurrTCPdev;
    }

    /**
     * 
     */
    public Value zShrToStr(final Value short) {
        return zascW(_short);
    }

    /**
     * 
     */
    public Value zIntToStr(final Value int) {
        return zascL(_int);
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
        return ((zPerfTimer()<_ecfSrvTmout)? 0 :(true? 1 :Value.nullValue()));
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
            _zErrLog(("EGECFS2: <zECFThrow>"+ zzCodeLn), Value.nullValue(), 1, zzErrNum);
            zzErrNum = Text.piece(zzErrNum, ",", 2);
        }
        EGECFS2 .sndError(zzErrCode, zzDetails, zzErrNum, zzCodeLn);
        zzErrCode = ((((Text.extractAssign(zzErrCode, 1, "3") == ",U-")?Value.nullValue():(true?",U-":Value.nullValue()))+ zzErrCode)+((Text.extractAssign(zzErrCode, Text.length(zzErrCode)) == ",")?Value.nullValue():(true?",":Value.nullValue())));
        _ecfFromThrow = 1;
        Builtin.ecode().set(zzErrCode);
        return  1;
    }

}
