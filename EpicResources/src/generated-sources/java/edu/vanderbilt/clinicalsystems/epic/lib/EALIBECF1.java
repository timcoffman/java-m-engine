
package edu.vanderbilt.clinicalsystems.epic.lib;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.VariableContext;
import edu.vanderbilt.clinicalsystems.m.core.annotation.InjectRoutine;
import edu.vanderbilt.clinicalsystems.m.core.lib.Builtin;
import edu.vanderbilt.clinicalsystems.m.core.lib.HaltCondition;
import edu.vanderbilt.clinicalsystems.m.core.lib.Math;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;
import edu.vanderbilt.clinicalsystems.m.core.lib.Reflect;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;

public class EALIBECF1 {

    @InjectRoutine
    public long $ecfFromThrow;
    @InjectRoutine
    public String $ecfInBufLoc;
    @InjectRoutine
    public Value $ecfToken;
    @InjectRoutine
    public double $ecfSrvTmout;
    @InjectRoutine
    public long appendToPrevious;
    @InjectRoutine
    public Value $;
    @InjectRoutine
    public boolean $ecfVersion;
    @InjectRoutine
    public Value colDefs;
    @InjectRoutine
    public String varName;
    @InjectRoutine
    public Value $ecfCurrTCPdev;
    @InjectRoutine
    public boolean $ecfEncrypt;
    @InjectRoutine
    public String key;
    @InjectRoutine
    public double $sv9;
    @InjectRoutine
    public Value $sv7;
    @InjectRoutine
    public Value $sv8;
    @InjectRoutine
    public String data;
    @InjectRoutine
    public String $ecfMode;
    @InjectRoutine
    public Value $sv1;
    @InjectRoutine
    public Value $sv2;
    @InjectRoutine
    public Value $sv5;
    @InjectRoutine
    public Value $sv6;
    @InjectRoutine
    public Value $sv3;
    @InjectRoutine
    public Value $sv4;
    @InjectRoutine
    public Value $ecfFtrs;
    @InjectRoutine
    public Value $EA;
    @InjectRoutine
    public boolean $ecfStreamCmd;
    @InjectRoutine
    public Value $ecfOutBuf;
    @InjectRoutine
    public Value $sv11;
    @InjectRoutine
    public String $sv10;
    @InjectRoutine
    public Value enc;
    @InjectRoutine
    public Value value;
    @InjectRoutine
    public Value $elogID;
    @InjectRoutine
    public Value containerPropId;
    @InjectRoutine
    public Value cells;
    @InjectRoutine
    public double $ecfPckSz;
    @InjectRoutine
    public Value tableId;

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
     * zECFSetDestGlobal(shared
     * 
     */
    public Value main() {
    }

    /**
     * zECFSetDestGlobal(shared
     * 
     */
    public String zECFSetDestGlobal(final boolean shared) {
        String tmpGlo = "";
        if (shared) {
            tmpGlo = ZefnLib.acquireSharedTempGlobal();
            $ecfMode = (((("-1"+ Text.character(1))+ tmpGlo)+ Text.character(1))+"1");
        } else {
            tmpGlo = ZefnLib.acquirePrivateTempGlobal();
            $ecfMode = (("-1"+ Text.character(1))+ tmpGlo);
        }
        VariableContext.lookup(tmpGlo).assign((- 1));
        return $ecfMode;
    }

    /**
     * zECFSetDestClient;
     * ;;#strip# only set the first piece, to leave the global piece intact for future calls to zECFGet* with negative numbers
     * S $P(%ecfMode,$C(1),1)=0
     * Q %ecfMode
     * 
     */
    public String zECFSetDestClient() {
        Text.pieceAssign($ecfMode.toString(), Text.character(1), 1);
        return $ecfMode;
    }

    /**
     * zECFInitDest(mode
     * 
     */
    public long zECFInitDest(final Value mode) {
        $ecfMode.assign(mode);
        $ecfMode = mode;
        return  1;
    }

    /**
     * zECFGetDest;
     * S:%ecfMode="" %ecfMode=0
     * Q %ecfMode
     * 
     */
    public Value zECFGetDest() {
        $ecfMode.assign(0);
        $ecfMode = 0;
        if ($ecfMode == Value.nullValue()) {
            $ecfMode.assign(0);
            $ecfMode = 0;
        }
        return $ecfMode;
    }

    /**
     * zECFReleaseDestGlobal(mode
     * 
     */
    public long zECFReleaseDestGlobal(final Value mode) {
        mode.assign($ecfMode);
        mode = $ecfMode;
        if (mode == Value.nullValue()) {
            mode.assign($ecfMode);
            mode = $ecfMode;
        }
        if (Text.piece(mode, Text.character(1), 3)) {
            ZefnLib.releaseSharedTempGlobal(Text.piece(mode.toString(), Text.character(1), 2));
        } else {
            ZefnLib.releasePrivateTempGlobal(Text.piece(mode.toString(), Text.character(1), 2));
        }
        return  1;
    }

    /**
     * zECFGet(propName,parentPropId,more,mode
     * 
     */
    public Value zECFGet(final String propName, final Value parentPropId, final String more, final Value mode) {
        $sv1 .clear();
        $sv1 = Value.nullValue();
        mode.assign($ecfMode);
        mode = $ecfMode;
        if (mode == Value.nullValue()) {
            mode.assign($ecfMode);
            mode = $ecfMode;
        }
        parentPropId.assign(mode);
        parentPropId = mode;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(mode);
            parentPropId = mode;
        }
        $sv5 .assign(Text.piece(mode.toString(), Text.character(1), 2));
        $sv5 = Text.piece(mode.toString(), Text.character(1), 2);
        $sv5 .assign((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        $sv5 = (Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue()));
        more = 1;
        if (more == 0) {
            more = 1;
        }
        while (true) {
            $sv1 .assign(($sv1 + VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get("1").get(more)));
            $sv1 = ($sv1 + VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get("1").get(more));
            more = Builtin.followingKey(VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get("1").get(more));
            return ;
            if (more == Value.nullValue()) {
                return ;
            }
            if ((Text.length($sv1)+ Text.length(VariableContext.lookup($sv5).get(parentPropId).get(propName).get("1").get(more).toString()))>$EA.get("maxCacheString")) {
                return ;
            }
        }
        return $sv1;
    }

    /**
     * zECFGetWasSent(propName,parentPropId,lineOrKey,mode
     * 
     */
    public Value zECFGetWasSent(final String propName, final Value parentPropId, final String lineOrKey, final Value mode) {
        mode.assign($ecfMode);
        mode = $ecfMode;
        if (mode == Value.nullValue()) {
            mode.assign($ecfMode);
            mode = $ecfMode;
        }
        parentPropId.assign(mode);
        parentPropId = mode;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(mode);
            parentPropId = mode;
        }
        $sv5 .assign(Text.piece(mode.toString(), Text.character(1), 2));
        $sv5 = Text.piece(mode.toString(), Text.character(1), 2);
        $sv5 .assign((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        $sv5 = (Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue()));
        return Text.follows(Builtin.followingKey(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(lineOrKey).get(Value.nullValue().toString())), Value.nullValue().toString());
        if (Text.follows(lineOrKey, Value.nullValue().toString())) {
            return Text.follows(Builtin.followingKey(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(lineOrKey).get(Value.nullValue().toString())), Value.nullValue().toString());
        }
        if (!$ecfVersion) {
            do {
                Value errRsn = Value.nullValue();
                errRsn.assign("The communication assembly Epic.Core.Ecf.dll does not support ");
                errRsn = "The communication assembly Epic.Core.Ecf.dll does not support ";
                errRsn.assign((errRsn +"ignoring properties and renders a call to $$zECFGetWasSent invalid."));
                errRsn = (errRsn +"ignoring properties and renders a call to $$zECFGetWasSent invalid.");
                $.assign(zECFThrow("CLIENT-CANNOT-IGNORE-PROPERTIES", errRsn));
                $ = zECFThrow("CLIENT-CANNOT-IGNORE-PROPERTIES", errRsn);
            } while (false);
        }
        return (Builtin.followingKey(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(Value.nullValue().toString()))!= Value.nullValue());
    }

    /**
     * zECFGetElmt(propName,parentPropId,lineOrKey,more,mode
     * 
     */
    public Value zECFGetElmt(final String propName, final Value parentPropId, final String lineOrKey, final Value more, final Value mode) {
        $sv1 .clear();
        $sv1 = Value.nullValue();
        mode.assign($ecfMode);
        mode = $ecfMode;
        if (mode == Value.nullValue()) {
            mode.assign($ecfMode);
            mode = $ecfMode;
        }
        parentPropId.assign(mode);
        parentPropId = mode;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(mode);
            parentPropId = mode;
        }
        $sv5 .assign(Text.piece(mode.toString(), Text.character(1), 2));
        $sv5 = Text.piece(mode.toString(), Text.character(1), 2);
        $sv5 .assign((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        $sv5 = (Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue()));
        if (more == Value.nullValue()) {
            if (zbitAnd(VariableContext.lookup($sv5).get(parentPropId).get(propName), 32) == 32) {
                return Value.nullValue();
                if (VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(lineOrKey).get("1") == 0) {
                    return Value.nullValue();
                }
            }
        }
        more.assign(("1,"+ VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(lineOrKey).get("0")));
        more = ("1,"+ VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(lineOrKey).get("0"));
        if (more == Value.nullValue()) {
            more.assign(("1,"+ VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(lineOrKey).get("0")));
            more = ("1,"+ VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(lineOrKey).get("0"));
        }
        while (true) {
            $sv1 .assign(($sv1 + VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(lineOrKey).get(more.toString())));
            $sv1 = ($sv1 + VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(lineOrKey).get(more.toString()));
            more.assign(Value.nullValue());
            more = Value.nullValue();
            if ((1 + more)>Text.piece(more.toString(), ",", 2)) {
                more.assign(Value.nullValue());
                more = Value.nullValue();
            }
            return ;
            if (more == Value.nullValue()) {
                return ;
            }
            Text.pieceAssign(more.toString(), ",");
            if ((Text.length($sv1)+ Text.length(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(lineOrKey).get(more.toString()).toString()))>$EA.get("maxCacheString")) {
                return ;
            }
        }
        return $sv1;
    }

    /**
     * zECFGetCell(propName,parentPropId,row,column,more,mode
     * 
     */
    public Value zECFGetCell(final String propName, final Value parentPropId, final String row, final String column, final String more, final Value mode) {
        $sv1 .clear();
        $sv1 = Value.nullValue();
        mode.assign($ecfMode);
        mode = $ecfMode;
        if (mode == Value.nullValue()) {
            mode.assign($ecfMode);
            mode = $ecfMode;
        }
        parentPropId.assign(mode);
        parentPropId = mode;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(mode);
            parentPropId = mode;
        }
        $sv5 .assign(Text.piece(mode.toString(), Text.character(1), 2));
        $sv5 = Text.piece(mode.toString(), Text.character(1), 2);
        $sv5 .assign((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        $sv5 = (Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue()));
        more = 1;
        if (more == Value.nullValue()) {
            more.assign(1);
            more = 1;
        }
        while (true) {
            $sv1 .assign(($sv1 + VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(row).get(column).get(more.toString())));
            $sv1 = ($sv1 + VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(row).get(column).get(more.toString()));
            more.assign(Builtin.followingKey(VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(row).get(column).get(more.toString())));
            more = Builtin.followingKey(VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(row).get(column).get(more.toString()));
            return ;
            if (more == Value.nullValue()) {
                return ;
            }
            if ((Text.length($sv1)+ Text.length(VariableContext.lookup($sv5).get(parentPropId).get(propName).get(row).get(column).get(more).toString()))>$EA.get("maxCacheString")) {
                return ;
            }
        }
        return $sv1;
    }

    /**
     * zECFNumElmts(propName,parentPropId,mode
     * 
     */
    public Value zECFNumElmts(final String propName, final Value parentPropId, final Value mode) {
        mode.assign($ecfMode);
        mode = $ecfMode;
        if (mode == Value.nullValue()) {
            mode.assign($ecfMode);
            mode = $ecfMode;
        }
        parentPropId.assign(mode);
        parentPropId = mode;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(mode);
            parentPropId = mode;
        }
        $sv5 .assign(Text.piece(mode.toString(), Text.character(1), 2));
        $sv5 = Text.piece(mode.toString(), Text.character(1), 2);
        return VariableContext.lookup((Text.follows($sv5, Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())).toString()).get(parentPropId).get(propName).get("0");
    }

    /**
     * zECFDctNxKey(propName,parentPropId,key,mode
     * 
     */
    public String zECFDctNxKey(final String propName, final Value parentPropId, final String key, final Value mode) {
        mode.assign($ecfMode);
        mode = $ecfMode;
        if (mode == Value.nullValue()) {
            mode.assign($ecfMode);
            mode = $ecfMode;
        }
        parentPropId.assign(mode);
        parentPropId = mode;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(mode);
            parentPropId = mode;
        }
        $sv5 .clear();
        $sv5 = Value.nullValue();
        $sv5 .assign(Text.piece(mode.toString(), Text.character(1), 2));
        $sv5 = Text.piece(mode.toString(), Text.character(1), 2);
        $sv5 .assign((Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())));
        $sv5 = (Text.follows($sv5 .toString(), Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue()));
        while (true) {
            key = Builtin.followingKey(VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(key));
            return ;
            if (key == Value.nullValue()) {
                return ;
            }
            return ;
            if (Reflect.dataType(VariableContext.lookup($sv5 .toString()).get(parentPropId.toString()).get(propName).get(key.toString()))> 1) {
                return ;
            }
        }
        return key;
    }

    /**
     * zECFTblNxCol(propName,parentPropId,row,column,mode
     * 
     */
    public String zECFTblNxCol(final String propName, final Value parentPropId, final String row, final String column, final Value mode) {
        mode.assign($ecfMode);
        mode = $ecfMode;
        if (mode == Value.nullValue()) {
            mode.assign($ecfMode);
            mode = $ecfMode;
        }
        parentPropId.assign(mode);
        parentPropId = mode;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(mode);
            parentPropId = mode;
        }
        $sv5 .assign(Text.piece(mode.toString(), Text.character(1), 2));
        $sv5 = Text.piece(mode.toString(), Text.character(1), 2);
        row = 1;
        if (row == Value.nullValue()) {
            row.assign(1);
            row = 1;
        }
        return Builtin.followingKey(VariableContext.lookup((Text.follows($sv5, Value.nullValue().toString())?$sv5 :(true?$ecfInBufLoc:Value.nullValue())).toString()).get(parentPropId).get(propName).get(row).get(column));
    }

    /**
     * zECFMrgIn(propName,parentPropId,varName
     * 
     */
    public long zECFMrgIn(final String propName, final String parentPropId, final String varName) {
        parentPropId = 0;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(0);
            parentPropId = 0;
        }
        VariableContext.lookup(varName).clear();
        VariableContext.lookup(varName).assign(VariableContext.lookup($ecfInBufLoc).get(parentPropId.toString()).get(propName));
        VariableContext.lookup($ecfInBufLoc).get(parentPropId).get(propName).clear();
        return  1;
    }

    /**
     * zECFTblMrgIn(propName,parentPropId,varName
     * 
     */
    public long zECFTblMrgIn(final String propName, final String parentPropId, final String varName) {
        $sv1 .clear();
        $sv1 = Value.nullValue();
        $sv2 .clear();
        $sv2 = Value.nullValue();
        $sv3 .clear();
        $sv3 = Value.nullValue();
        $sv4 .clear();
        $sv4 = Value.nullValue();
        parentPropId = 0;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(0);
            parentPropId = 0;
        }
        VariableContext.lookup(varName).clear();
        VariableContext.lookup(varName).assign(VariableContext.lookup($ecfInBufLoc).get(parentPropId.toString()).get(propName));
        VariableContext.lookup($ecfInBufLoc).get(parentPropId).get(propName).clear();
        for (Value $sv1 = 1; ($sv1 >= VariableContext.lookup(varName).get("0")); $sv1 += 1) {
            do {
                while (true) {
                    $sv2 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv1 .toString()).get($sv2 .toString())));
                    $sv2 = Builtin.followingKey(VariableContext.lookup(varName).get($sv1 .toString()).get($sv2 .toString()));
                    return ;
                    if ($sv2 == Value.nullValue()) {
                        return ;
                    }
                    do {
                        for (Value $sv3 = 1; ($sv3 >= VariableContext.lookup(varName).get($sv1).get($sv2).get("0")); $sv3 += 1) {
                            do {
                                VariableContext.lookup(varName).get($sv1).get($sv2).assign((VariableContext.lookup(varName).get($sv1 .toString()).get($sv2 .toString())+ VariableContext.lookup(varName).get($sv1 .toString()).get($sv2 .toString()).get($sv3 .toString())));
                                VariableContext.lookup(varName).get($sv1).get($sv2).get($sv3).clear();
                            } while (false);
                        }
                        VariableContext.lookup(varName).get($sv1).get($sv2).get("0").clear();
                    } while (false);
                }
            } while (false);
        }
        while (true) {
            $sv4 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv4 .toString())));
            $sv4 = Builtin.followingKey(VariableContext.lookup(varName).get($sv4 .toString()));
            return ;
            if ($sv4 == Value.nullValue()) {
                return ;
            }
            while (true) {
                $sv2 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv4 .toString()).get($sv2 .toString())));
                $sv2 = Builtin.followingKey(VariableContext.lookup(varName).get($sv4 .toString()).get($sv2 .toString()));
                return ;
                if ($sv2 == Value.nullValue()) {
                    return ;
                }
                VariableContext.lookup(varName).get("0").get($sv2).assign(Math.increment(VariableContext.lookup(varName).get("0")));
                if (!Reflect.dataType(VariableContext.lookup(varName).get("0").get($sv2 .toString()))) {
                    VariableContext.lookup(varName).get("0").get($sv2).assign(Math.increment(VariableContext.lookup(varName).get("0")));
                }
            }
        }
        return  1;
    }

    /**
     * zECFNew(propName,parentPropId,propStore,merge,enc
     * 
     */
    public Value zECFNew(final Value propName, final Value parentPropId, final Value propStore, final Value merge, final Value enc) {
        try {
            EGECFUTL.$zzECFNewGlobal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFNewGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.assign(($ecfEncrypt||enc));
        enc = ($ecfEncrypt||enc);
        $ecfFtrs.assign(($ecfFtrs + 1));
        $ecfFtrs = ($ecfFtrs + 1);
        $sv1 .assign($ecfFtrs);
        $sv1 = $ecfFtrs;
        parentPropId.assign(0);
        parentPropId = 0;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(0);
            parentPropId = 0;
        }
        if (enc) {
            $.assign(zcrypt(propName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), propName));
            $ = zcrypt(propName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), propName);
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if (((Text.length($ecfOutBuf.toString())+ Text.length(propName.toString()))+ 12)>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +((((((Text.character(2)+ Text.character(((propStore == "S")? 0 :((propStore == "A")? 16 :((propStore == "L")? 16 :((propStore == "D")? 32 :(true? 0 :Value.nullValue()))))).toLong()))+ Text.character(((merge* 64)+(enc* 128))))+ zchrL($sv1))+ zchrL(parentPropId))+ Text.character(Text.length(propName.toString())))+ propName)));
        $ecfOutBuf = ($ecfOutBuf +((((((Text.character(2)+ Text.character(((propStore == "S")? 0 :((propStore == "A")? 16 :((propStore == "L")? 16 :((propStore == "D")? 32 :(true? 0 :Value.nullValue()))))).toLong()))+ Text.character(((merge* 64)+(enc* 128))))+ zchrL($sv1))+ zchrL(parentPropId))+ Text.character(Text.length(propName.toString())))+ propName));
        return $sv1;
    }

    /**
     * zECFNewElmtObj(containerPropId,key,enc
     * 
     */
    public Value zECFNewElmtObj(final Value containerPropId, final Value key, final boolean enc) {
        try {
            EGECFUTL.$zzECFNewElmtObjGlobal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFNewElmtObjGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc = ($ecfEncrypt||enc);
        $ecfFtrs.assign(($ecfFtrs + 1));
        $ecfFtrs = ($ecfFtrs + 1);
        $sv1 .assign($ecfFtrs);
        $sv1 = $ecfFtrs;
        if (enc) {
            $.assign(zcrypt(key, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), key));
            $ = zcrypt(key, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), key);
            if (Text.follows(key.toString(), Value.nullValue().toString())) {
                $.assign(zcrypt(key, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), key));
                $ = zcrypt(key, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), key);
            }
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if ((Text.length($ecfOutBuf.toString())+(Text.follows(key.toString(), Value.nullValue().toString())?(Text.length(key.toString())+ 12):(true? 11 :Value.nullValue())))>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        if (Text.follows(key.toString(), Value.nullValue().toString())) {
            $ecfOutBuf.assign(($ecfOutBuf +((((((Text.character(2)+ Text.character(2))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(containerPropId))+ Text.character(Text.length(key.toString())))+ key)));
            $ecfOutBuf = ($ecfOutBuf +((((((Text.character(2)+ Text.character(2))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(containerPropId))+ Text.character(Text.length(key.toString())))+ key));
        } else {
            $ecfOutBuf.assign(($ecfOutBuf +((((Text.character(2)+ Text.character(1))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(containerPropId))));
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(2)+ Text.character(1))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(containerPropId)));
        }
        return $sv1;
    }

    /**
     * zECFReset(propName,parentPropId,enc
     * 
     */
    public long zECFReset(final Value propName, final Value parentPropId, final boolean enc) {
        try {
            EGECFUTL.$zzECFResetGlobal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFResetGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc = ($ecfEncrypt||enc);
        parentPropId.assign(0);
        parentPropId = 0;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(0);
            parentPropId = 0;
        }
        if (enc) {
            $.assign(zcrypt(propName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), propName));
            $ = zcrypt(propName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), propName);
            if (Text.follows(propName.toString(), Value.nullValue().toString())) {
                $.assign(zcrypt(propName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), propName));
                $ = zcrypt(propName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), propName);
            }
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if (((Text.length($ecfOutBuf.toString())+ Text.length(propName.toString()))+ 8)>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(3)+ Text.character(0))+ Text.character(0))+ zchrL(parentPropId))+ Text.character(Text.length(propName.toString())))+ propName)));
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(3)+ Text.character(0))+ Text.character(0))+ zchrL(parentPropId))+ Text.character(Text.length(propName.toString())))+ propName));
        return  1;
    }

    /**
     * zECFNewCol(tableId,columnName,enc
     * 
     */
    public Value zECFNewCol(final Value tableId, final Value columnName, final Value enc) {
        try {
            EGECFUTL.$zzECFNewColGlobal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFNewColGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.assign(($ecfEncrypt||enc));
        enc = ($ecfEncrypt||enc);
        $ecfFtrs.get(tableId).assign(($ecfFtrs.get(tableId.toString())+ 1));
        $ecfFtrs = ($ecfFtrs.get(tableId.toString())+ 1);
        $sv1 .assign($ecfFtrs.get(tableId.toString()));
        $sv1 = $ecfFtrs.get(tableId.toString());
        if (enc) {
            $.assign(zcrypt(columnName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), columnName));
            $ = zcrypt(columnName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), columnName);
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if (((Text.length($ecfOutBuf.toString())+ Text.length(columnName.toString()))+ 10)>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +((((((Text.character(1)+ Text.character(0))+ Text.character(0))+ zchrL(tableId))+ zchrW($sv1))+ Text.character(Text.length(columnName.toString())))+ columnName)));
        $ecfOutBuf = ($ecfOutBuf +((((((Text.character(1)+ Text.character(0))+ Text.character(0))+ zchrL(tableId))+ zchrW($sv1))+ Text.character(Text.length(columnName.toString())))+ columnName));
        return $sv1;
    }

    /**
     * zECFSet(propNameOrId,value,parentPropId,enc
     * 
     */
    public Value zECFSet(final Value propNameOrId, final Value value, final Value parentPropId, final boolean enc) {
        $sv1 .assign(propNameOrId);
        $sv1 = propNameOrId;
        try {
            EGECFUTL.$zzECFSetGlobal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFSetGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc = ($ecfEncrypt||enc);
        try {
            $zzECFSetValSwitch();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($sv1 == propNameOrId) {
            try {
                $zzECFSetValSwitch();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfFtrs.assign(($ecfFtrs + 1));
        $ecfFtrs = ($ecfFtrs + 1);
        $sv1 .assign($ecfFtrs);
        $sv1 = $ecfFtrs;
        parentPropId.assign(0);
        parentPropId = 0;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(0);
            parentPropId = 0;
        }
        if (enc) {
            $.assign(zcrypt(propNameOrId, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), propNameOrId));
            $ = zcrypt(propNameOrId, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), propNameOrId);
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if (((Text.length($ecfOutBuf.toString())+ Text.length(propNameOrId.toString()))+ 12)>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +((((((Text.character(2)+ Text.character(0))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(parentPropId))+ Text.character(Text.length(propNameOrId.toString())))+ propNameOrId)));
        $ecfOutBuf = ($ecfOutBuf +((((((Text.character(2)+ Text.character(0))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrL(parentPropId))+ Text.character(Text.length(propNameOrId.toString())))+ propNameOrId));
        $zzECFSetValSwitch:
        try {
            $zzECFSetValEnc();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if (enc) {
            try {
                $zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetVal:
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv3 .assign(Text.length(value.toString()));
        $sv3 = Text.length(value.toString());
        if ($sv3 == 0) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if ((Text.length($ecfOutBuf.toString())+ 9)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0)));
            return $sv1;
        }
        if (((Text.length($ecfOutBuf)+ $sv3)+ 9)>= $ecfPckSz) {
            $sv4 .assign((($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8));
            $sv4 = (($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8);
            $sv2 .assign(Text.extract(value.toString(), ($sv4 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv4 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv4 .toLong()));
            value = Text.extract(value.toString(), 1, $sv4 .toLong());
            $sv3 .assign(Text.length(value.toString()));
            $sv3 = Text.length(value.toString());
        }
        if (($sv3 > 0)||(Text.length($sv2 .toString()) == 0)) {
            $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value)));
            $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value));
        }
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
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
     * Q %sv1
     * 
     */
    public Value $zzECFSetValSwitch() {
        try {
            $zzECFSetValEnc();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if (enc) {
            try {
                $zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetVal:
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv3 .assign(Text.length(value.toString()));
        $sv3 = Text.length(value.toString());
        if ($sv3 == 0) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if ((Text.length($ecfOutBuf.toString())+ 9)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0)));
            return $sv1;
        }
        if (((Text.length($ecfOutBuf)+ $sv3)+ 9)>= $ecfPckSz) {
            $sv4 .assign((($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8));
            $sv4 = (($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8);
            $sv2 .assign(Text.extract(value.toString(), ($sv4 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv4 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv4 .toLong()));
            value = Text.extract(value.toString(), 1, $sv4 .toLong());
            $sv3 .assign(Text.length(value.toString()));
            $sv3 = Text.length(value.toString());
        }
        if (($sv3 > 0)||(Text.length($sv2 .toString()) == 0)) {
            $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value)));
            $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value));
        }
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
            try {
                $zzECFSetVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return $sv1;
    }

    /**
     * %zzECFSetValS %sv2="",%sv3=$L(value) IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetVal() {
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv3 .assign(Text.length(value.toString()));
        $sv3 = Text.length(value.toString());
        if ($sv3 == 0) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if ((Text.length($ecfOutBuf.toString())+ 9)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW(0)));
            return $sv1;
        }
        if (((Text.length($ecfOutBuf)+ $sv3)+ 9)>= $ecfPckSz) {
            $sv4 .assign((($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8));
            $sv4 = (($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8);
            $sv2 .assign(Text.extract(value.toString(), ($sv4 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv4 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv4 .toLong()));
            value = Text.extract(value.toString(), 1, $sv4 .toLong());
            $sv3 .assign(Text.length(value.toString()));
            $sv3 = Text.length(value.toString());
        }
        if (($sv3 > 0)||(Text.length($sv2 .toString()) == 0)) {
            $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value)));
            $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(0))+ zchrL($sv1))+ zchrW($sv3))+ value));
        }
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
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
     * Q %sv1
     * 
     */
    public Value $zzECFSetValEnc() {
        if (Text.length(value.toString()) == 0) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if ((Text.length($ecfOutBuf.toString())+ 9)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(128))+ zchrL($sv1))+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(0))+ Text.character(128))+ zchrL($sv1))+ zchrW(0)));
            return $sv1;
        }
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv5 .assign((($ecfPckSz-Text.length($ecfOutBuf.toString()))- 9));
        $sv5 = (($ecfPckSz-Text.length($ecfOutBuf.toString()))- 9);
        $sv5 .assign(($sv5 -($sv5 % 4)));
        $sv5 = ($sv5 -($sv5 % 4));
        $sv3 .assign(($sv5 -($sv5 / 32.0D)));
        $sv3 = ($sv5 -($sv5 / 32.0D));
        $sv3 .assign(($sv3 /(4 * 3)));
        $sv3 = ($sv3 /(4 * 3));
        if (Text.length(value.toString())>$sv3) {
            $sv2 .assign(Text.extract(value.toString(), ($sv3 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv3 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv3 .toLong()));
            value = Text.extract(value.toString(), 1, $sv3 .toLong());
        }
        if (($sv3 > 0)||(Text.length($sv2 .toString()) == 0)) {
            $.assign(zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), value));
            $ = zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), value);
            value.assign(Text.extract(value.toString(), 1, (Text.length(value.toString())- 1)));
            value = Text.extract(value.toString(), 1, (Text.length(value.toString())- 1));
            $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(128))+ zchrL($sv1))+ zchrW(Text.length(value.toString())))+ value)));
            $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(0))+ Text.character(128))+ zchrL($sv1))+ zchrW(Text.length(value.toString())))+ value));
        }
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
            try {
                $zzECFSetValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return $sv1;
    }

    /**
     * zECFSetElmt(containerPropId,value,key,appendToPrevious,enc
     * 
     */
    public Value zECFSetElmt(final Value containerPropId, final Value value, final Value key, final Value appendToPrevious, final Value enc) {
        try {
            EGECFUTL.$zzECFSetElmtGlobal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFSetElmtGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.assign(($ecfEncrypt||enc));
        enc = ($ecfEncrypt||enc);
        try {
            $zzECFSetElmtArrVal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ((!enc)&&(key == Value.nullValue())) {
            try {
                $zzECFSetElmtArrVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            $zzECFSetElmtDctVal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if (!enc) {
            try {
                $zzECFSetElmtDctVal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        try {
            $zzECFSetElmtArrValEnc();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if (key == Value.nullValue()) {
            try {
                $zzECFSetElmtArrValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $.assign(zcrypt(key, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), key));
        $ = zcrypt(key, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), key);
        $zzECFSetElmtDctValEnc:
        if (!Text.length(value)) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if (((Text.length($ecfOutBuf.toString())+ Text.length(key.toString()))+ 10)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key.toString())))+ key)+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key.toString())))+ key)+ zchrW(0)));
            return containerPropId;
        }
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv5 .assign(((($ecfPckSz-Text.length($ecfOutBuf.toString()))-Text.length(key.toString()))- 9));
        $sv5 = ((($ecfPckSz-Text.length($ecfOutBuf.toString()))-Text.length(key.toString()))- 9);
        $sv5 .assign(($sv5 -($sv5 % 4)));
        $sv5 = ($sv5 -($sv5 % 4));
        $sv3 .assign(($sv5 -($sv5 / 32.0D)));
        $sv3 = ($sv5 -($sv5 / 32.0D));
        $sv3 .assign(($sv3 /(4 * 3)));
        $sv3 = ($sv3 /(4 * 3));
        if (Text.length(value)>$sv3) {
            $sv2 .assign(Text.extract(value.toString(), ($sv3 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv3 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv3 .toLong()));
            value = Text.extract(value.toString(), 1, $sv3 .toLong());
        }
        $.assign(zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), value));
        $ = zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), value);
        value.assign(Text.extract(value.toString(), 1, (Text.length(value.toString())- 1)));
        value = Text.extract(value.toString(), 1, (Text.length(value.toString())- 1));
        $ecfOutBuf.assign(($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key.toString())))+ key)+ zchrW(Text.length(value.toString())))+ value)));
        $ecfOutBuf = ($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key.toString())))+ key)+ zchrW(Text.length(value.toString())))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
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
     * Q containerPropId
     * 
     */
    public Value $zzECFSetElmtDctValEnc() {
        if (!Text.length(value.toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if (((Text.length($ecfOutBuf.toString())+ Text.length(key))+ 10)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0)));
            return containerPropId;
        }
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv5 .assign(((($ecfPckSz-Text.length($ecfOutBuf.toString()))-Text.length(key))- 9));
        $sv5 = ((($ecfPckSz-Text.length($ecfOutBuf.toString()))-Text.length(key))- 9);
        $sv5 .assign(($sv5 -($sv5 % 4)));
        $sv5 = ($sv5 -($sv5 % 4));
        $sv3 .assign(($sv5 -($sv5 / 32.0D)));
        $sv3 = ($sv5 -($sv5 / 32.0D));
        $sv3 .assign(($sv3 /(4 * 3)));
        $sv3 = ($sv3 /(4 * 3));
        if (Text.length(value.toString())>$sv3) {
            $sv2 .assign(Text.extract(value.toString(), ($sv3 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv3 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv3 .toLong()));
            value = Text.extract(value.toString(), 1, $sv3 .toLong());
        }
        $.assign(zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), value));
        $ = zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), value);
        value.assign(Text.extract(value.toString(), 1, (Text.length(value.toString())- 1)));
        value = Text.extract(value.toString(), 1, (Text.length(value.toString())- 1));
        $ecfOutBuf.assign(($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(Text.length(value.toString())))+ value)));
        $ecfOutBuf = ($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(128))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(Text.length(value.toString())))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
            try {
                $zzECFSetElmtDctValEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        return containerPropId;
    }

    /**
     * %zzECFSetElmtArrValS %sv2="",%sv3=$L(value) IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetElmtArrVal() {
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv3 .assign(Text.length(value.toString()));
        $sv3 = Text.length(value.toString());
        if ($sv3 == 0) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if ((Text.length($ecfOutBuf.toString())+ 9)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((Text.character(0)+ Text.character(16))+ Text.character((appendToPrevious* 64)))+ zchrL(containerPropId))+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(16))+ Text.character((appendToPrevious* 64)))+ zchrL(containerPropId))+ zchrW(0)));
            return containerPropId;
        }
        if (((Text.length($ecfOutBuf)+ $sv3)+ 9)>= $ecfPckSz) {
            $sv4 .assign((($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8));
            $sv4 = (($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8);
            $sv2 .assign(Text.extract(value.toString(), ($sv4 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv4 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv4 .toLong()));
            value = Text.extract(value.toString(), 1, $sv4 .toLong());
            $sv3 .assign(Text.length(value.toString()));
            $sv3 = Text.length(value.toString());
        }
        $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(0)+ Text.character(16))+ Text.character((appendToPrevious* 64)))+ zchrL(containerPropId))+ zchrW($sv3))+ value)));
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(16))+ Text.character((appendToPrevious* 64)))+ zchrL(containerPropId))+ zchrW($sv3))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
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
     * %zzECFSetElmtDctValS %sv2="",%sv3=$L(value) IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetElmtDctVal() {
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv3 .assign(Text.length(value.toString()));
        $sv3 = Text.length(value.toString());
        if ($sv3 == 0) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if (((Text.length($ecfOutBuf.toString())+ Text.length(key))+ 10)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(0))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((((Text.character(0)+ Text.character(32))+ Text.character(0))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW(0)));
            return containerPropId;
        }
        if ((((Text.length($ecfOutBuf)+ $sv3)+ Text.length(key))+ 10)>= $ecfPckSz) {
            $sv4 .assign(((($ecfPckSz-Text.length($ecfOutBuf.toString()))-Text.length(key))- 9));
            $sv4 = ((($ecfPckSz-Text.length($ecfOutBuf.toString()))-Text.length(key))- 9);
            $sv2 .assign(Text.extract(value.toString(), ($sv4 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv4 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv4 .toLong()));
            value = Text.extract(value.toString(), 1, $sv4 .toLong());
            $sv3 .assign(Text.length(value.toString()));
            $sv3 = Text.length(value.toString());
        }
        $ecfOutBuf.assign(($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(0))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW($sv3))+ value)));
        $ecfOutBuf = ($ecfOutBuf +(((((((Text.character(0)+ Text.character(32))+ Text.character(0))+ zchrL(containerPropId))+ Text.character(Text.length(key)))+ key)+ zchrW($sv3))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
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
     * Q containerPropId
     * 
     */
    public Value $zzECFSetElmtArrValEnc() {
        if (!Text.length(value.toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            if ((Text.length($ecfOutBuf.toString())+ 9)>$ecfPckSz) {
                $.assign(zECFSndRsltPckt());
                $ = zECFSndRsltPckt();
            }
            $ecfOutBuf.assign(($ecfOutBuf +((((Text.character(0)+ Text.character(16))+ Text.character((128 +(appendToPrevious* 64))))+ zchrL(containerPropId))+ zchrW(0))));
            $ecfOutBuf = ($ecfOutBuf +((((Text.character(0)+ Text.character(16))+ Text.character((128 +(appendToPrevious* 64))))+ zchrL(containerPropId))+ zchrW(0)));
            return containerPropId;
        }
        $sv2 .assign(Value.nullValue());
        $sv2 = Value.nullValue();
        $sv5 .assign((($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8));
        $sv5 = (($ecfPckSz-Text.length($ecfOutBuf.toString()))- 8);
        $sv5 .assign(($sv5 -($sv5 % 4)));
        $sv5 = ($sv5 -($sv5 % 4));
        $sv3 .assign(($sv5 -($sv5 / 32.0D)));
        $sv3 = ($sv5 -($sv5 / 32.0D));
        $sv3 .assign(($sv3 /(4 * 3)));
        $sv3 = ($sv3 /(4 * 3));
        if (Text.length(value.toString())>$sv3) {
            $sv2 .assign(Text.extract(value.toString(), ($sv3 + 1), 999999999));
            $sv2 = Text.extract(value.toString(), ($sv3 + 1), 999999999);
            value.assign(Text.extract(value.toString(), 1, $sv3 .toLong()));
            value = Text.extract(value.toString(), 1, $sv3 .toLong());
        }
        $.assign(zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), value));
        $ = zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), value);
        value.assign(Text.extract(value.toString(), 1, (Text.length(value.toString())- 1)));
        value = Text.extract(value.toString(), 1, (Text.length(value.toString())- 1));
        $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(0)+ Text.character(16))+ Text.character((128 +(appendToPrevious* 64))))+ zchrL(containerPropId))+ zchrW(Text.length(value.toString())))+ value)));
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(0)+ Text.character(16))+ Text.character((128 +(appendToPrevious* 64))))+ zchrL(containerPropId))+ zchrW(Text.length(value.toString())))+ value));
        if (Text.follows($sv2 .toString(), Value.nullValue().toString())) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            value.assign($sv2);
            value = $sv2;
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
     * zECFSetRow(tableId,cells,enc
     * 
     */
    public Value zECFSetRow(final Value tableId, final Value cells, final Value enc) {
        try {
            EGECFUTL.$zzECFSetRowGlobal();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($ecfMode == (- 1)) {
            try {
                EGECFUTL.$zzECFSetRowGlobal();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        enc.assign(($ecfEncrypt||enc));
        enc = ($ecfEncrypt||enc);
        $sv1 .assign(0);
        $sv1 = 0;
        $sv2 .assign($ecfFtrs.get(tableId.toString()));
        $sv2 = $ecfFtrs.get(tableId.toString());
        if ((Text.length($ecfOutBuf)+ 9)>$ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +(((Text.character(0)+ Text.character(48))+ Text.character((enc* 128)))+ zchrL(tableId))));
        $ecfOutBuf = ($ecfOutBuf +(((Text.character(0)+ Text.character(48))+ Text.character((enc* 128)))+ zchrL(tableId)));
        try {
            $zzECFSetRowCellEnc();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if (enc) {
            try {
                $zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetRowCell:
        $sv1 .assign(($sv1 + 1));
        $sv1 = ($sv1 + 1);
        value.assign(cells.get($sv1 .toString()));
        value = cells.get($sv1 .toString());
        $sv3 .assign(Text.length(value.toString()));
        $sv3 = Text.length(value.toString());
        if (((Text.length($ecfOutBuf)+ $sv3)+ 6)>$ecfPckSz) {
            $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        } else {
            $ecfOutBuf.assign(($ecfOutBuf +((zchrW($sv1)+ zchrW($sv3))+ value)));
            $ecfOutBuf = ($ecfOutBuf +((zchrW($sv1)+ zchrW($sv3))+ value));
            try {
                $zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign(((((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value));
        $ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value);
        if (($sv3 + 13)<= $ecfPckSz) {
            try {
                $zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        try {
            $zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($sv1 == $sv2) {
            try {
                $zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId)));
        $ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId));
        $zzECFSetRowCellFnsh:
        try {
            $zzECFSetRowCell();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
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
     * %zzECFSetRowCellS %sv1=%sv1+1,value=cells(%sv1),%sv3=$L(value) IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetRowCell() {
        $sv1 .assign(($sv1 + 1));
        $sv1 = ($sv1 + 1);
        value.assign(cells.get($sv1 .toString()));
        value = cells.get($sv1 .toString());
        $sv3 .assign(Text.length(value.toString()));
        $sv3 = Text.length(value.toString());
        if (((Text.length($ecfOutBuf)+ $sv3)+ 6)>$ecfPckSz) {
            $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        } else {
            $ecfOutBuf.assign(($ecfOutBuf +((zchrW($sv1)+ zchrW($sv3))+ value)));
            $ecfOutBuf = ($ecfOutBuf +((zchrW($sv1)+ zchrW($sv3))+ value));
            try {
                $zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign(((((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value));
        $ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value);
        if (($sv3 + 13)<= $ecfPckSz) {
            try {
                $zzECFSetRowCellFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        try {
            $zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($sv1 == $sv2) {
            try {
                $zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign((((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId)));
        $ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(64))+ zchrL(tableId));
        $zzECFSetRowCellFnsh:
        try {
            $zzECFSetRowCell();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
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
     * %zzECFSetRowCellFnshG:%sv1<%sv2 %zzECFSetRowCell() IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFSetRowCellFnsh() {
        try {
            $zzECFSetRowCell();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
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
     * %zzECFSetRowCellEncS %sv1=%sv1+1,value=cells(%sv1) IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public long $zzECFSetRowCellEnc() {
        $sv1 .assign(($sv1 + 1));
        $sv1 = ($sv1 + 1);
        value.assign(cells.get($sv1 .toString()));
        value = cells.get($sv1 .toString());
        $.assign(zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), value));
        $ = zcrypt(value, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), value);
        value.assign(Text.extract(value.toString(), 1, (Text.length(value.toString())- 1)));
        value = Text.extract(value.toString(), 1, (Text.length(value.toString())- 1));
        $sv3 .assign(Text.length(value.toString()));
        $sv3 = Text.length(value.toString());
        if (((Text.length($ecfOutBuf)+ $sv3)+ 6)>$ecfPckSz) {
            $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        } else {
            $ecfOutBuf.assign(($ecfOutBuf +((zchrW($sv1)+ zchrW(Text.length(value.toString())))+ value)));
            $ecfOutBuf = ($ecfOutBuf +((zchrW($sv1)+ zchrW(Text.length(value.toString())))+ value));
            try {
                $zzECFSetRowCellEncFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign(((((((Text.character(0)+ Text.character(48))+ Text.character(192))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value));
        $ecfOutBuf = ((((((Text.character(0)+ Text.character(48))+ Text.character(192))+ zchrL(tableId))+ zchrW($sv1))+ zchrW($sv3))+ value);
        if (($sv3 + 13)<= $ecfPckSz) {
            try {
                $zzECFSetRowCellEncFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        try {
            $zzECFSetRowFnsh();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($sv1 == $sv2) {
            try {
                $zzECFSetRowFnsh();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign((((Text.character(0)+ Text.character(48))+ Text.character(192))+ zchrL(tableId)));
        $ecfOutBuf = (((Text.character(0)+ Text.character(48))+ Text.character(192))+ zchrL(tableId));
        $zzECFSetRowCellEncFnsh:
        try {
            $zzECFSetRowCellEnc();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($sv1 <$sv2) {
            try {
                $zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetRowFnsh:
        $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        if (Text.length($ecfOutBuf.toString())> 0) {
            $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * %zzECFSetRowCellEncFnshG:%sv1<%sv2 %zzECFSetRowCellEnc()
     * %zzECFSetRowFnshS:$L(%ecfOutBuf)>0 %ecfOutBuf=%ecfOutBuf_$$zchrW(0)
     * Q 1
     * 
     */
    public long $zzECFSetRowCellEncFnsh() {
        try {
            $zzECFSetRowCellEnc();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($sv1 <$sv2) {
            try {
                $zzECFSetRowCellEnc();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $zzECFSetRowFnsh:
        $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        if (Text.length($ecfOutBuf.toString())> 0) {
            $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * %zzECFSetRowFnshS:$L(%ecfOutBuf)>0 %ecfOutBuf=%ecfOutBuf_$$zchrW(0) IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public long $zzECFSetRowFnsh() {
        $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
        $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        if (Text.length($ecfOutBuf.toString())> 0) {
            $ecfOutBuf.assign(($ecfOutBuf + zchrW(0)));
            $ecfOutBuf = ($ecfOutBuf + zchrW(0));
        }
        return  1;
    }

    /**
     * zECFStream(data
     * 
     */
    public long zECFStream(final String data) {
        if (!$ecfStreamCmd) {
            $.assign(zECFThrow("ECF-INVALID-FUNCTION-CALL", "The zECFStream function can only be used with StreamCommand type ECF commands."));
            $ = zECFThrow("ECF-INVALID-FUNCTION-CALL", "The zECFStream function can only be used with StreamCommand type ECF commands.");
            return  0;
        }
        $zzECFStreamLenChk:
        $sv1 .assign(Text.length(data));
        $sv1 = Text.length(data);
        $sv2 .assign(Text.length($ecfOutBuf.toString()));
        $sv2 = Text.length($ecfOutBuf.toString());
        if ((($sv1 + $sv2)+ 5)>$ecfPckSz) {
            $ecfOutBuf.assign(($ecfOutBuf + Text.extract(data, 1, (($ecfPckSz-$sv2)- 5))));
            $ecfOutBuf = ($ecfOutBuf + Text.extract(data, 1, (($ecfPckSz-$sv2)- 5)));
            data = Text.extract(data, (($ecfPckSz-$sv2)- 4), $sv1 .toLong());
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            try {
                $zzECFStreamLenChk();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign(($ecfOutBuf + data));
        $ecfOutBuf = ($ecfOutBuf + data);
        return  1;
    }

    /**
     * %zzECFStreamLenChkS %sv1=$L(data),%sv2=$L(%ecfOutBuf);only send up to max packet length at a time, subtract 5 for control characters tacked on in zECFSndRsltPckt
     * I %sv1+%sv2+5>%ecfPckSz S %ecfOutBuf=%ecfOutBuf_$E(data,1,%ecfPckSz-%sv2-5),data=$E(data,%ecfPckSz-%sv2-4,%sv1) S %=$$zECFSndRsltPckt() G %zzECFStreamLenChk()
     * S %ecfOutBuf=%ecfOutBuf_data
     * Q 1
     * 
     */
    public long $zzECFStreamLenChk() {
        $sv1 .assign(Text.length(data));
        $sv1 = Text.length(data);
        $sv2 .assign(Text.length($ecfOutBuf.toString()));
        $sv2 = Text.length($ecfOutBuf.toString());
        if ((($sv1 + $sv2)+ 5)>$ecfPckSz) {
            $ecfOutBuf.assign(($ecfOutBuf + Text.extract(data, 1, (($ecfPckSz-$sv2)- 5))));
            $ecfOutBuf = ($ecfOutBuf + Text.extract(data, 1, (($ecfPckSz-$sv2)- 5)));
            data = Text.extract(data, (($ecfPckSz-$sv2)- 4), $sv1 .toLong());
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
            try {
                $zzECFStreamLenChk();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $ecfOutBuf.assign(($ecfOutBuf + data));
        $ecfOutBuf = ($ecfOutBuf + data);
        return  1;
    }

    /**
     * zECFMrgOut(propName,parentPropId,varName,dataStore,enc
     * 
     */
    public Value zECFMrgOut(final Value propName, final Value parentPropId, final String varName, final Value dataStore, final Value enc) {
        $sv6 .clear();
        $sv6 = Value.nullValue();
        $sv7 .clear();
        $sv7 = Value.nullValue();
        $sv8 .clear();
        $sv8 = Value.nullValue();
        $sv9 = 0.0D;
        $sv10 = "";
        $sv11 .clear();
        $sv11 = Value.nullValue();
        enc.assign(($ecfEncrypt||enc));
        enc = ($ecfEncrypt||enc);
        $sv6 .assign(zECFNew(propName, parentPropId, dataStore, enc));
        $sv6 = zECFNew(propName, parentPropId, dataStore, enc);
        $sv7 .assign(1);
        $sv7 = 1;
        try {
            $zzECFMrgOutDct();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if (dataStore == "D") {
            try {
                $zzECFMrgOutDct();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $sv8 .assign(0);
        $sv8 = 0;
        $sv9 = 0;
        if (!Reflect.dataType(VariableContext.lookup(varName).get("1").get("1"))) {
            while (true) {
                $sv8 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString())));
                $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()));
                return ;
                if ($sv8 == Value.nullValue()) {
                    return ;
                }
                $sv9 = ($sv9 + 1);
                $sv7 .assign(zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8 .toString()), enc));
                $sv7 = zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8 .toString()), enc);
                return ;
                if (!$sv7) {
                    return ;
                }
            }
        } else {
            while (true) {
                $sv8 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString())));
                $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()));
                return ;
                if ($sv8 == Value.nullValue()) {
                    return ;
                }
                $sv9 = ($sv9 + 1);
                $sv10 = 0;
                $sv11 .assign(0);
                $sv11 = 0;
                while (true) {
                    $sv10 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()).get($sv10));
                    return ;
                    if ($sv10 == Value.nullValue()) {
                        return ;
                    }
                    $sv7 .assign(zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8 .toString()).get($sv10 .toString()), $sv11, enc));
                    $sv7 = zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8 .toString()).get($sv10 .toString()), $sv11, enc);
                    $sv11 .assign(1);
                    $sv11 = 1;
                    return ;
                    if (!$sv7) {
                        return ;
                    }
                }
            }
        }
        VariableContext.lookup(varName).clear();
        return $sv7;
    }

    /**
     * %zzECFMrgOutDctS %sv8=$O(@varName@(""),-1) IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFMrgOutDct() {
        $sv8 .assign(Builtin.followingKey(VariableContext.lookup(varName).get(Value.nullValue().toString()), (- 1)));
        $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get(Value.nullValue().toString()), (- 1));
        return $sv6;
        if ($sv8 == Value.nullValue()) {
            return $sv6;
        }
        if (!Reflect.dataType(VariableContext.lookup(varName).get($sv8).get("1"))) {
            $sv8 .assign(Value.nullValue());
            $sv8 = Value.nullValue();
            while (true) {
                $sv8 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString())));
                $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()));
                return ;
                if ($sv8 == Value.nullValue()) {
                    return ;
                }
                $sv7 .assign(zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8 .toString()), $sv8, enc));
                $sv7 = zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8 .toString()), $sv8, enc);
                return ;
                if (!$sv7) {
                    return ;
                }
            }
        } else {
            $sv8 .assign(Value.nullValue());
            $sv8 = Value.nullValue();
            while (true) {
                $sv8 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString())));
                $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()));
                return ;
                if ($sv8 == Value.nullValue()) {
                    return ;
                }
                $sv10 .assign(0);
                $sv10 = 0;
                while (true) {
                    $sv10 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()).get($sv10 .toString())));
                    $sv10 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()).get($sv10 .toString()));
                    return ;
                    if ($sv10 == Value.nullValue()) {
                        return ;
                    }
                    $sv7 .assign(zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8 .toString()).get($sv10 .toString()), $sv8, enc));
                    $sv7 = zECFSetElmt($sv6, VariableContext.lookup(varName).get($sv8 .toString()).get($sv10 .toString()), $sv8, enc);
                    return ;
                    if (!$sv7) {
                        return ;
                    }
                }
            }
        }
        VariableContext.lookup(varName).clear();
        return $sv7;
    }

    /**
     * zECFTblMrgOut(propName,parentPropId,varName,enc
     * 
     */
    public double zECFTblMrgOut(final Value propName, final Value parentPropId, final String varName, final Value enc) {
        $sv6 .clear();
        $sv6 = Value.nullValue();
        $sv7 .clear();
        $sv7 = Value.nullValue();
        $sv8 .clear();
        $sv8 = Value.nullValue();
        $sv9 = 0.0D;
        enc.assign(($ecfEncrypt||enc));
        enc = ($ecfEncrypt||enc);
        $sv6 .assign(zECFNew(propName, parentPropId, "T", enc));
        $sv6 = zECFNew(propName, parentPropId, "T", enc);
        while (true) {
            $sv7 .assign(Builtin.followingKey(VariableContext.lookup(varName).get("0").get($sv7 .toString())));
            $sv7 = Builtin.followingKey(VariableContext.lookup(varName).get("0").get($sv7 .toString()));
            return ;
            if ($sv7 == Value.nullValue()) {
                return ;
            }
            VariableContext.lookup(varName).get("0").get($sv7).assign(zECFNewCol($sv6, $sv7, enc));
        }
        $sv9 = 1;
        $sv8 .assign(0);
        $sv8 = 0;
        while (true) {
            $sv8 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString())));
            $sv8 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()));
            return ;
            if ($sv8 == Value.nullValue()) {
                return ;
            }
            do {
                while (true) {
                    $sv7 .assign(Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()).get($sv7 .toString())));
                    $sv7 = Builtin.followingKey(VariableContext.lookup(varName).get($sv8 .toString()).get($sv7 .toString()));
                    return ;
                    if ($sv7 == Value.nullValue()) {
                        return ;
                    }
                    cells.get(VariableContext.lookup(varName).get("0").get($sv7 .toString())).assign(VariableContext.lookup(varName).get($sv8 .toString()).get($sv7 .toString()));
                    cells = VariableContext.lookup(varName).get($sv8 .toString()).get($sv7 .toString());
                    if (Reflect.dataType(VariableContext.lookup(varName).get($sv8 .toString()).get($sv7 .toString()))) {
                        cells.get(VariableContext.lookup(varName).get("0").get($sv7 .toString())).assign(VariableContext.lookup(varName).get($sv8 .toString()).get($sv7 .toString()));
                        cells = VariableContext.lookup(varName).get($sv8 .toString()).get($sv7 .toString());
                    }
                }
                $sv9 = zECFSetRow($sv6, cells, enc);
                return ;
                if (!$sv9) {
                    return ;
                }
                cells.clear();
                cells = Value.nullValue();
                VariableContext.lookup(varName).get($sv8).clear();
            } while (false);
            return ;
            if (!$sv9) {
                return ;
            }
        }
        VariableContext.lookup(varName).clear();
        return $sv9;
    }

    /**
     * zECFNewTbl(propName,parentPropId,colDefs,enc
     * 
     */
    public Value zECFNewTbl(final Value propName, final Value parentPropId, final Value colDefs, final boolean enc) {
        $ecfFtrs.assign(($ecfFtrs + 1));
        $ecfFtrs = ($ecfFtrs + 1);
        $sv1 .assign($ecfFtrs);
        $sv1 = $ecfFtrs;
        return $sv1;
        if ($ecfMode == (- 1)) {
            return $sv1;
        }
        enc = ($ecfEncrypt||enc);
        parentPropId.assign(0);
        parentPropId = 0;
        if (parentPropId == Value.nullValue()) {
            parentPropId.assign(0);
            parentPropId = 0;
        }
        if (enc) {
            $.assign(zcrypt(propName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), propName));
            $ = zcrypt(propName, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), propName);
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if (((Text.length($ecfOutBuf.toString())+ Text.length(propName.toString()))+ 12)>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +((((((Text.character(4)+ Text.character(64))+ Text.character((enc* 128)))+ zchrL($sv1))+ Text.character((Text.length(propName.toString())+ 4)))+ zchrL(parentPropId))+ propName)));
        $ecfOutBuf = ($ecfOutBuf +((((((Text.character(4)+ Text.character(64))+ Text.character((enc* 128)))+ zchrL($sv1))+ Text.character((Text.length(propName.toString())+ 4)))+ zchrL(parentPropId))+ propName));
        $sv2 .assign(0);
        $sv2 = 0;
        $zzECFNewTblCol:
        $sv2 .assign(($sv2 + 1));
        $sv2 = ($sv2 + 1);
        try {
            $zzECFNewTblDone();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($sv2 >colDefs.get("0")) {
            try {
                $zzECFNewTblDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $sv3 .assign(Value.nullValue());
        $sv3 = Value.nullValue();
        $sv4 .assign(Value.nullValue());
        $sv4 = Value.nullValue();
        $ecfFtrs.get($sv1).assign($sv2);
        $ecfFtrs = $sv2;
        while (true) {
            $sv3 .assign(Builtin.followingKey(colDefs.get($sv2 .toString()).get($sv3 .toString())));
            $sv3 = Builtin.followingKey(colDefs.get($sv2 .toString()).get($sv3 .toString()));
            return ;
            if ($sv3 == Value.nullValue()) {
                return ;
            }
            $sv5 .assign(colDefs.get($sv2 .toString()).get($sv3 .toString()));
            $sv5 = colDefs.get($sv2 .toString()).get($sv3 .toString());
            $sv4 .assign((((($sv4 + Text.character(Text.length($sv3 .toString())))+ $sv3)+ Text.character(Text.length($sv5 .toString())))+ $sv5));
            $sv4 = (((($sv4 + Text.character(Text.length($sv3 .toString())))+ $sv3)+ Text.character(Text.length($sv5 .toString())))+ $sv5);
        }
        if (enc) {
            $.assign(zcrypt($sv4, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), $sv4));
            $ = zcrypt($sv4, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), $sv4);
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if (((Text.length($ecfOutBuf.toString())+ Text.length($sv4 .toString()))+ 9)>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(20)+ Text.character(80))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrW(Text.length($sv4 .toString())))+ $sv4)));
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(20)+ Text.character(80))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrW(Text.length($sv4 .toString())))+ $sv4));
        try {
            $zzECFNewTblCol();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * %zzECFNewTblColS %sv2=%sv2+1 IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public void $zzECFNewTblCol() {
        $sv2 .assign(($sv2 + 1));
        $sv2 = ($sv2 + 1);
        try {
            $zzECFNewTblDone();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
        if ($sv2 >colDefs.get("0")) {
            try {
                $zzECFNewTblDone();
            } finally {
                throw new HaltCondition("return from GOTO");
            }
        }
        $sv3 .assign(Value.nullValue());
        $sv3 = Value.nullValue();
        $sv4 .assign(Value.nullValue());
        $sv4 = Value.nullValue();
        $ecfFtrs.get($sv1).assign($sv2);
        $ecfFtrs = $sv2;
        while (true) {
            $sv3 .assign(Builtin.followingKey(colDefs.get($sv2 .toString()).get($sv3 .toString())));
            $sv3 = Builtin.followingKey(colDefs.get($sv2 .toString()).get($sv3 .toString()));
            return ;
            if ($sv3 == Value.nullValue()) {
                return ;
            }
            $sv5 .assign(colDefs.get($sv2 .toString()).get($sv3 .toString()));
            $sv5 = colDefs.get($sv2 .toString()).get($sv3 .toString());
            $sv4 .assign((((($sv4 + Text.character(Text.length($sv3 .toString())))+ $sv3)+ Text.character(Text.length($sv5 .toString())))+ $sv5));
            $sv4 = (((($sv4 + Text.character(Text.length($sv3 .toString())))+ $sv3)+ Text.character(Text.length($sv5 .toString())))+ $sv5);
        }
        if (enc) {
            $.assign(zcrypt($sv4, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), $sv4));
            $ = zcrypt($sv4, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), $sv4);
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if (((Text.length($ecfOutBuf.toString())+ Text.length($sv4 .toString()))+ 9)>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(20)+ Text.character(80))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrW(Text.length($sv4 .toString())))+ $sv4)));
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(20)+ Text.character(80))+ Text.character((enc* 128)))+ zchrL($sv1))+ zchrW(Text.length($sv4 .toString())))+ $sv4));
        try {
            $zzECFNewTblCol();
        } finally {
            throw new HaltCondition("return from GOTO");
        }
    }

    /**
     * %zzECFNewTblDoneK %sv4,%sv5 IllegalStateException: block (depth:-1) concluded without starting a new line
     * 
     */
    public Value $zzECFNewTblDone() {
        $sv4 .clear();
        $sv4 = Value.nullValue();
        $sv5 .clear();
        $sv5 = Value.nullValue();
        return $sv1;
    }

    /**
     * zECFSetTblPrms(tblId,params,enc
     * 
     */
    public long zECFSetTblPrms(final Value tblId, final Value params, final boolean enc) {
        return  0;
        if ($ecfMode == (- 1)) {
            return  0;
        }
        enc = ($ecfEncrypt||enc);
        $sv3 .assign(Value.nullValue());
        $sv3 = Value.nullValue();
        $sv4 .assign(Value.nullValue());
        $sv4 = Value.nullValue();
        while (true) {
            $sv3 .assign(Builtin.followingKey(params.get($sv3 .toString())));
            $sv3 = Builtin.followingKey(params.get($sv3 .toString()));
            return ;
            if ($sv3 == Value.nullValue()) {
                return ;
            }
            $sv5 .assign(params.get($sv3 .toString()));
            $sv5 = params.get($sv3 .toString());
            $sv4 .assign((((($sv4 + Text.character(Text.length($sv3 .toString())))+ $sv3)+ Text.character(Text.length($sv5 .toString())))+ $sv5));
            $sv4 = (((($sv4 + Text.character(Text.length($sv3 .toString())))+ $sv3)+ Text.character(Text.length($sv5 .toString())))+ $sv5);
        }
        if (enc) {
            $.assign(zcrypt($sv4, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), $sv4));
            $ = zcrypt($sv4, $ecfToken.get("Key"), (ju()+ $ecfToken.get("Pid")), Value.nullValue(), Value.nullValue(), $sv4);
        }
        $.assign(zECFSndRsltPckt());
        $ = zECFSndRsltPckt();
        if (((Text.length($ecfOutBuf.toString())+ Text.length($sv4 .toString()))+ 9)>= $ecfPckSz) {
            $.assign(zECFSndRsltPckt());
            $ = zECFSndRsltPckt();
        }
        $ecfOutBuf.assign(($ecfOutBuf +(((((Text.character(20)+ Text.character(96))+ Text.character((enc* 128)))+ zchrL(tblId))+ zchrW(Text.length($sv4 .toString())))+ $sv4)));
        $ecfOutBuf = ($ecfOutBuf +(((((Text.character(20)+ Text.character(96))+ Text.character((enc* 128)))+ zchrL(tblId))+ zchrW(Text.length($sv4 .toString())))+ $sv4));
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
     * Q 1
     * 
     */
    public Value zECFSndRsltPckt() {
        return  0;
        if ($ecfAsync) {
            return  0;
        }
        $ecfOutBuf.assign(("L"+ $ecfOutBuf));
        $ecfOutBuf = ("L"+ $ecfOutBuf);
        ReadWrite.use(zECFTCPDev());
        ReadWrite.write(zchrW(0), zchrW(Text.length($ecfOutBuf.toString())), $ecfOutBuf);
        ReadWrite.use(zUniqNullDev());
        EGECFS2 .lgRespLn($elogID, "Body", $ecfOutBuf);
        if ($ecfDbg) {
            EGECFS2 .lgRespLn($elogID, "Body", $ecfOutBuf);
        }
        EGEI.logPacket("RESP", (Text.length($ecfOutBuf)+ 4));
        if ($EA.get("E").get("EI").get("Level")) {
            EGEI.logPacket("RESP", (Text.length($ecfOutBuf)+ 4));
        }
        $ecfOutBuf.assign(Value.nullValue());
        $ecfOutBuf = Value.nullValue();
        return  1;
    }

    /**
     * zECFTCPDevQ %ecfCurrTCPdev
     * 
     */
    public Value zECFTCPDev() {
        return $ecfCurrTCPdev;
    }

    /**
     * zShrToStr(short
     * 
     */
    public Value zShrToStr(final Value _short_) {
        return zascW(_short_);
    }

    /**
     * zIntToStr(int
     * 
     */
    public Value zIntToStr(final Value _int_) {
        return zascL(_int_);
    }

    /**
     * zStrToShr(str,ltlEndian
     * 
     */
    public Value zStrToShr(final Value str, final boolean ltlEndian) {
        return ((!ltlEndian)?zchrW(str):(true?Text.character((str% 256), (str/ 256)):Value.nullValue()));
    }

    /**
     * zStrToInt(str
     * 
     */
    public Value zStrToInt(final Value str) {
        return zchrL(str);
    }

    /**
     * zECFTmoutQ $S($$zPerfTimer()<%ecfSrvTmout:0,1:1)
     * 
     */
    public Value zECFTmout() {
        return ((zPerfTimer()<$ecfSrvTmout)? 0 :(true? 1 :Value.nullValue()));
    }

    /**
     * zECFThrow(zzErrCode,zzDetails,zzNoLog
     * 
     */
    public Value zECFThrow(final Value zzErrCode, final String zzDetails, final String zzNoLog) {
        Builtin.ecode().assign(",U-EMPTY-THROW-REASON,");
        if (zzErrCode == Value.nullValue()) {
            Builtin.ecode().assign(",U-EMPTY-THROW-REASON,");
        }
        String zzCodeLn = "";
        String zzErrNum = "";
        zzCodeLn = (Reflect.stackInfo((- 1))- 1);
        zzCodeLn = Reflect.stackInfo(zzCodeLn, "PLACE");
        if (!zzNoLog) {
            $zErrLog(("EGECFS2: <zECFThrow>"+ zzCodeLn), Value.nullValue(), 1, zzErrNum);
            zzErrNum = Text.piece(zzErrNum, ",", 2);
        }
        EGECFS2 .sndError(zzErrCode, zzDetails, zzErrNum, zzCodeLn);
        zzErrCode.assign(((((Text.extract(zzErrCode.toString(), 1, 3) == ",U-")?Value.nullValue():(true?",U-":Value.nullValue()))+ zzErrCode)+((Text.extract(zzErrCode.toString(), Text.length(zzErrCode.toString())) == ",")?Value.nullValue():(true?",":Value.nullValue()))));
        zzErrCode = ((((Text.extract(zzErrCode.toString(), 1, 3) == ",U-")?Value.nullValue():(true?",U-":Value.nullValue()))+ zzErrCode)+((Text.extract(zzErrCode.toString(), Text.length(zzErrCode.toString())) == ",")?Value.nullValue():(true?",":Value.nullValue())));
        $ecfFromThrow = 1;
        Builtin.ecode().assign(zzErrCode);
        return  1;
    }

}
