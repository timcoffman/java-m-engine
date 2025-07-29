%Zefnlih ; ; ;2016-12-08 12:59:43;8.3;0AQaw5bSkEw21SwIKITdB1xiUPg/8Oc9xZWEhpEYGAQzbkKYVwXlbOnzL7yEZhP2
	q
	; Copyright (C) 2004-2016 Epic Systems Corporation
	;*********************************************************************
	; TITLE:   %Zefnlih
	; PURPOSE: Epic standard Core Library
	; AUTHOR:  Kulvinder Singh
	;
	; References to keep for backwards compatibility:
	;;#keepfun#isEMP
	;;#keepfun#zEMPini
	;;#keepfun#%zSetECode
	;
	; REVISION HISTORY:
	; *JCK 04/12/13 M7019657 - Move (%)zSetECode to %ZcfnliError
	; *gt      3/14 M7023648 - Support locale collated temp globals
	; *APG 12/14 345280 - Modify (%)zRelAllTmpGlo to call checkBatchesServer^EAETBT4
	; *APG 01/15 346701 - Remove the audit global option from zGtTmpGlo. %zetGlo no longer calls zGtTmpGlo.
	;                     zGtTmpGloType no longer returns a collated audit global.
	; *gt 1/15 350745 - Use correct variable for collation check in zGtTmpGloShrd
	; *APG 02/15 354932 - Kill rqst("%ET") in %zRelAllTmpGlo
	; *ted 02/23/15 355251 - zGtTmpGloType: Add comment reminding folks to update getTmpGloSubs^%ZeGLOUTIL
	; *gt  03/15 361502 - zPort: return default port
	; *SWF 05/15 T7065  - zGtTmpGloShrd: Add caller info
	;                   - (%)zRelTmpGloShrd: Kill at first subscript
	;                   - zNumMaxIncrement: Created
	;                   - zIncSafe: Created
	;                   - (%)zzIncSafeReset: Created
	; *JCK 05/15 T6951 - Mark zGtTmpGloShrd with coreBootstrap
	; *APG 05/15 T7144 - Support directly calling the zOIDType^%Zefnlih
	; *ted 05/15 T7576 - Add zzzGtTmpGloAllocPPG, change zGtTmpGlo to use it
	; *DJK 05/15 T7024 - Modified temp global registration. Created zGetGloCleanupMode.
	; *AH  12/15 399014 - Use hard-coded values for MAXSTRING APIs
	; *MAT 12/15 398373 - Add API for comm process read timeout, separate from zHangTm
	; *SBA 12/15 394107 - Append the unique nodeID to shared global's name
	; *YZH 07/15 401180 - Remove the dependency on non-Core code from zGtTmpGlo and %zRelTmpGlo
	; *JCK 02/16 407277 - Decouple Core library functions from Chronicles library functions
	; *YZH 12/16 I8304552 - Handle DBINTERNAL scheme in zzzGtAuditTmpGloAllocPPG
	;*********************************************************************
	;
	;;#lglob#%Zefnlib
	;
	;*********************************************************************
	; IMPORTANT NOTE:
	;*********************************************************************
	; This routine is intended to only hold code for "EMP only" INIs.
	; Please don't put other code here , that will help reduce object
	; contention in the future.
	;*********************************************************************
	;
	;---------
	; NAME:         zPckSz
	; SCOPE:        INTERNAL
	;               _Foundations
	; DESCRIPTION:  The maximum length of data in a packet.
	;               For more information, visit:
	;               http://wiki/main/Maximum_String_Length_Settings
	; RETURNS:      The maximum string length
	; REVISIONS:
	; *AH 12/15 399014 - Use hard-coded value
	;---------
zPckSz() q 8192
	;;#eof# ;;#inline# ;;#coreBootPrep#
	;
	;---------
	; NAME:         zPort
	; SCOPE:        INTERNAL
	; DESCRIPTION:  Returns EpicCommTCP's Port number
	; *gt 3/15 361502 - return default port
zPort() n port
	s port=^%ZeOSUNQ("EPICCOMM","TCP","PORT")
	q:port="" 4068
	q port
	;;#eof#
	;
	;---------
	; NAME:         zHangTm
	; SCOPE:        INTERNAL
	; DESCRIPTION:  Returns EpicCommTCP's Timeout Value
zHangTm() q ^%ZeOSUNQ("EPICCOMM","TCP","HANGTIME")     ;;#eof#  ;;#inline#
	;---------
	; NAME:         zCommReadTm
	; SCOPE:        INTERNAL
	; DESCRIPTION:  Returns EpicComm/ECF read timeout
	; RETURNS:      Return timeout override if configured
	;---------
zCommReadTm() n timeout
	s timeout=^%ZeOSUNQ("EPICCOMM","TCP","READTMOUT")
	q $s(timeout="":30,timeout'>0:30,1:timeout)
	;;#eof#
	;
	;---------
	; NAME:         zKeepAliveInterval
	; SCOPE:        PRIVATE
	; DESCRIPTION:  Returns the interval in seconds which the client should
	;               communicate with the database server process.
	;
	;               A value of 0 or any negative value means the feature should be disabled
	;               Any value less than 60 will be increased to 60 on a customer system
	;               The default of 120 will be used if the system-wide setting is left blank
	; RETURNS:      Interval in seconds
	;---------
	;*MAT 10/15 382490 - Created
zKeepAliveInterval() n value
	s value=^%ZeOSUNQ("EPICCOMM","TCP","KeepAliveInterval")
	i value="" q 120
	s value=value\1 ;Convert to an integer
	;Make sure we don't get a value less than 60 at a customer site
	;Allow artificially low values internally for testing
	;;#if# ^%ZeOSDF("OID","ETYPE")'=1
	i value>0,value<60 s value=60
	;;#endif#
	q $s(value'>0:0,1:value)
	;;#eof#
	;---------
	; NAME:         zKeepAliveGrace
	; SCOPE:        PRIVATE
	; DESCRIPTION:  Returns the period in seconds which the database server will
	;               wait beyond the keepalive interval before closing down a connection
	; RETURNS:      Grace period in seconds
	;---------
	;*MAT 10/15 382490 - Created
zKeepAliveGrace() n value
	s value=^%ZeOSUNQ("EPICCOMM","TCP","KeepAliveGrace")
	i value="" q 30
	s value=value\1 ;Convert to an integer
	q $s(value'<0:value,1:30)
	;;#eof#
	;---------
	; NAME:         zKeepAliveGraceProxy
	; SCOPE:        PRIVATE
	; DESCRIPTION:  Returns the period in seconds which the database server will
	;               wait beyond the keepalive interval before closing down a connection
	;               for a connection initiated through the wireless proxy
	; RETURNS:      Grace period in seconds for proxy connections
	;---------
	;*MAT 10/15 382490 - Created
zKeepAliveGraceProxy() n value
	s value=^%ZeOSUNQ("EPICCOMM","TCP","KeepAliveGraceProxy")
	i value="" q 1080
	s value=value\1 ;Convert to an integer
	q $s(value'<0:value,1:1080)
	;;#eof#
	;
	;
	;---------
	; NAME:         zPkTLm
	; SCOPE:        INTERNAL
	;               _Foundations
	; DESCRIPTION:  Maximum string length for switching from local to global storage.
	;               For more information, visit:
	;               http://wiki/main/Maximum_String_Length_Settings
	; RETURNS:      The maximum string length
	; NOTES:        The local storage space available for a process depends on various factors.
	;               Epic's communication infrastructure uses this space to store the data from a GUI client.
	;               If there is insufficient space, then the process MAY crash with a <STORE> error.
	;               This value defines the system-wide threshold setting in bytes beyond which
	;               the infrastructure will switch from the local storage to a global storage space.
	; REVISIONS:
	; *AH 12/15 399014 - Use hard-coded value
	;---------
zPkTLm() q 32768
	;;#eof# ;;#inline# ;;#coreBootPrep#
	;
	;---------
	; NAME:         zCommPersistTm
	; SCOPE:        INTERNAL
	; DESCRIPTION:  Returns the amount of time an ECF process is allowed to have no devices connected
	;---------
	;*txu 09/15 385570 - Created
zCommPersistTm() q 300 ;300 is 5 minutes, this has to be greater than the heartbeat value in Shell.js (55 seconds), and equal to the timeout in SCP's ClientConnection.cs.
	;;#eof# ;;#inline#
	;---------
	; NAME:         zGtTmpGlo
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Returns the name of temporary space created in process private
	;               global space.
	;               If a key is passed, it returns a new PPG if there's none associated to that key, but if there's
	;               a PPG that uses that key, this PPG is returned (even if it should be mapped to CACHETEMP or not)
	; PARAMETERS:
	;  notCT      (I,OPT) - Determines whether returned global is mapped to CACHETEMP or not:
	;                          "" : mapped to CACHETEMP
	;                           1 : mapped to SCRATCHDB
	;  key        (I,OPT) - Unique key passed that will be pointing to this PPG.
	;  localeColl (I,OPT) - Set to 1 if the global should collate using the locale collation, otherwise
	;                       will use the default environment collation. Not supported in core bootstrap mode.
	; RETURNS:      Temporary global name as ^PPGGLOBAL(...)
	;               The PPG will be new if key="" or if the key is unique. If the key is used, it'll return a used PPG.
	; REVISIONS:
	; *ted 05/21/15 T7576 - Use zzzGtTmpGloAllocPPG^%Zelibh instead of allocatePPG^%ZeGLOUTIL
	;---------
zGtTmpGlo(notCT,key,localeColl) ;
	n gloName,uniq,node,caller
	;;#if#{CoreModeLocalVersion}
	s node=$na(^ETMPEPC($I(^ETMPEPC)))
	k @node
	q node
	;;#else#
	;;#if# '$$zCollEnvLocaleEnabled^%ZefnliCollation()
	s localeColl=0  ; we don't need to use a separate global if the environment has no locale collation
	;;#endif#
	i notCT=2 k notCT ;prevent caller from getting audit global
	;(Note: Call ($$)zzzGtTmpGloAllocPPG in a call-out routine (%Zelibh) to avoid needing to compile-in
	; that library function and its various dependencies into all the application routines that use ($$)zGtTmpGlo)
	s gloName=$$zzzGtTmpGloAllocPPG^%Zelibh(notCT,"",localeColl)
	i key'="" s node=@gloName@("KEY",key) q:node'="" node ;If key already points to an existing node, return it
	S uniq=$I(@gloName,1) k:$D(@gloName@(uniq)) @gloName@(uniq) ;Get a unique node in the global
	s $ec="",caller=$stack($stack-1,"PLACE"),@gloName@("CALLER",uniq)=caller
	s node=$name(@gloName@(uniq))
	i $$zGetGloCleanupMode()>0 d %zRegGlobal(node,caller) ;*DJK 05/15 T7024 - register only if global leak checking is on
	s:key'="" @gloName@("KEY",key)=node,@gloName@("KEY",1,node)=key
	q node
	;;#endif#
	;;#eof# ;;#coreBootstrap#
	;
	;---------
	; NAME:         zzzGtTmpGloAllocPPG
	; SCOPE:        PRIVATE
	; DESCRIPTION:  This function returns the name of private global which can be used by this process.
	;               When called with no parameter, the global it returns is mapped to CACHETEMP
	;               and is not shared between nodes. The global is private to the process and it's privacy should respected!
	;               When called with a single parameter of 1, the global it returns is not mapped to
	;               CACHETEMP, but is still private to the process.
	;               Applications can then use indirection to access the global.
	; CALLED BY:    ($$)zGtPrGlo & ($$)zGtTmpGlo
	; PARAMETERS:
	;  notCT (I,OPT,DEFAULT:None)-Determines whether returned global is mapped to CACHETEMP or not:
	;                              "" : mapped to CACHETEMP
	;                               1 : mapped to SCRATCHDB
	;                               2 : used exclusively by audit batches
	;  checkSame  (I,OPT) - If passed, it doesn't kill the new global if it matches the old global
	;  localeColl (I,OPT) - If the global needs to use locale collation, set to 1
	; RETURNS:      Unique Private global for the current process. Same global is returned for multiple calls
	; ASSUMES:      %EA
	; SIDE EFFECTS: Sets %EA("E","pGlo") [notCt null or zero], %EA("E","pGloRg") [notCT=1] or %EA("E","%etGlo") [notCT=2]
	; REVISIONS:
	; *gt 03/14 M7023648 - support locale collated temp globals
	; *ted 05/21/15 T7576 - Move allocatePPG^%ZeGLOUTIL -> zzzGtTmpGloAllocPPG^%Zefnlih
	; *YZH 07/15 401180   - split the part for audit batches to a separate function to remove the dependency to non core code
	;---------
zzzGtTmpGloAllocPPG(notCT,checkSame,localeColl) n type,gSchm,baseG,global,ws,error
	i notCT=2 k notCT ;*YZH 07/15 401180 prevent caller from getting audit global
	i '$$zCollEnvLocaleEnabled() s localeColl=0
	s type=$$zGtTmpGloType(notCT,localeColl)
	i 'checkSame,$d(%EA("E",type)) q %EA("E",type)
	i %EA("G") s ws=$$zgCurLWS()
	i 'notCT,(ws]""),^debug("LWS PPG Override",ws) s gSchm="PIDBASED"
	s:gSchm="" gSchm=^%ZeOSUNQ("PPG","SCHEME") s:gSchm="" gSchm=^%ZeOSDF("PPG","SCHEME")
	i gSchm="" s gSchm=$s(notCT:"PSLICED",localeColl:"PSLICED",$$zAllowPPGasDBINTERNAL():"DBINTERNAL",1:"PSLICED") i 1  ;Cannot use DBINTERNAL when not mapped to CACHETEMP
	e  i gSchm="DBINTERNAL",(notCT)!('$$zAllowPPGasDBINTERNAL())!localeColl s gSchm="PSLICED"
	i gSchm="DBINTERNAL" s global=$$zGetPPGwhenDBINTERNAL(notCT) i 1
	e  d
	. s baseG="^"_$s((notCT=1):"ERTMPRG",1:"ERTEMP")_$s(localeColl:"COLL",1:"")
	. s global=$$zzzGtGloFromBase(gSchm,baseG) ;*YZH 07/15 401180 Move duplicated code to a new tag
	i checkSame,%EA("E",type)=global,$d(@global@("OWNER")) q global ;*gt 3/14 M7023648 - check if global has owner
	k @global
	i localeColl,$d(@$na(@global,0))=0,'$$zCollCreateGlobal($na(@global,0),$$zCollGetLocaleCode(),.error) d %zErrLog("Unable to create locale collated temp global: "_error,"",1)
	s %EA("E",type)=global
	s @global@("OWNER")=$$zzzGtGloOwner()  ;*YZH 07/15 401180 Move duplicated code to a new tag
	i $$zGetGloCleanupMode()'>0,'$$zIsISCPpg(global) d %zRegGlobal(global)  ;*DJK 05/15 T7024 - Register root when applicable
	q global
	;;#eof#
	;
	;---------
	; NAME:         zzzGtAuditTmpGloAllocPPG
	; SCOPE:        PRIVATE
	; DESCRIPTION:  This function returns the name of private global which can be used by audit batches.
	; PARAMETERS:
	;  checkSame  (I,OPT) - If passed, it doesn't kill the new global if it matches the old global
	; RETURNS:      Unique Private global for the current process. Same global is returned for multiple calls
	; REVISIONS:
	; *YZH 07/15 401180 - Created
	; *YZH 12/16 I8304552 - Handle DBINTERNAL scheme in zzzGtAuditTmpGloAllocPPG
	;---------
zzzGtAuditTmpGloAllocPPG(checkSame) n gSchm,global,ws
	i 'checkSame,$d(%EA("E","%etGlo")) q %EA("E","%etGlo")
	i %EA("G") s ws=$$zgCurLWS()
	i (ws]""),($d(^debug("AuditDebug",ws))) s gSchm="AUDITDEBUG"
	s:gSchm="" gSchm=^%ZeOSUNQ("PPG","SCHEME") s:gSchm="" gSchm=^%ZeOSDF("PPG","SCHEME") s:gSchm="" gSchm="PSLICED"
	i gSchm="DBINTERNAL" s gSchm="PSLICED"  ;*YZH 12/16 I8304552
	i gSchm="AUDITDEBUG" s global=$name(^EADETDEBUG($$ju()))
	e  d
	. s global=$$zzzGtGloFromBase(gSchm,"^ERTMPRGB")
	i checkSame,%EA("E","%etGlo")=global,$d(@global@("OWNER")) q global ;*gt 3/14 M7023648 - check if global has owner
	i $d(@global)>0 d saveLostBatch^EAETBKG(global)
	k @global
	s %EA("E","%etGlo")=global
	s @global@("OWNER")=$$zzzGtGloOwner()
	i $$zGetGloCleanupMode()'>0,'$$zIsISCPpg(global) d %zRegGlobal(global)  ;*DJK 05/15 T7024 - Register root when applicable
	q global
	;;#eof#
	;
	;---------
	; NAME:         zzzGtGloFromBase
	; SCOPE:        PRIVATE
	; DESCRIPTION:  Return the global name for different mapping methods with a name base
	; PARAMETERS:
	;  gSchm (I,REQ) - The mapping method
	;  baseG (I,REQ) - The name base of the global
	; RETURNS:      The global name for different mapping methods based on the name base
	; REVISIONS:
	; *YZH 07/15 401180 - Created
	;---------
zzzGtGloFromBase(gSchm,baseG) n global,gSpread
	i gSchm="PIDBASED" s global=baseG_$JOB i 1
	e  d
	. s:gSchm="RSLICED" gSchm="PSLICED"  ; If RSLICED, make it PSLICED
	. i gSchm="PSLICED" d
	. . s gSpread=+^%ZeOSUNQ("PPG","GLOSPREAD") s:gSpread<1 gSpread=+^%ZeOSDF("PPG","GLOSPREAD")
	. . s:gSpread<100 gSpread=100
	. . s global=baseG_"P"_($JOB#gSpread)_"("_($JOB\gSpread)_")"
	q global
	;;#eof#
	;
	;---------
	; NAME:         zzzGtGloOwner
	; SCOPE:        PRIVATE
	; DESCRIPTION:  The inline function to form the value for the "OWNER" node of the global
	; RETURNS:      $ju<9>CurrentUserID<9><9>CurrentTerminal<9>CurrentTime<9>PIDLong
	; REVISIONS:
	; *YZH 07/15 401180 - Created
	;---------
zzzGtGloOwner() ;
	q $$ju()_$c(9)_$$zgUserID()_$c(9)_$c(9)_$I_$c(9)_$H_$c(9)_$$zPIDLong()
	;;#eof#  ;;#inline#
	;
	;---------
	; NAME:         zGtTmpGloType
	; SCOPE:        PRIVATE
	; DESCRIPTION:  Returns the %EA subscript to use for caching
	;               the temp global used for this process
	; PARAMETERS:
	;  notCT      (I,OPT) - Determines whether returned global is mapped to CACHETEMP or not:
	;                          "" : mapped to CACHETEMP
	;                           1 : mapped to SCRATCHDB
	;                           2 : mapped to PRODLOC, used by audit batches
	;  localeColl (I,OPT) - Returns the locale-collated subscript when set to true. Ignored if notCT=2
	; RETURNS:      the %EA subscript to use for caching the temp global used for this process
	; NOTES:
	; + ***REMINDER***
	;   Make sure to update getTmpGloSubs^%ZeGLOUTIL to call ($$)zGtTmpGloType
	;   with all possible parameter-combinations.
	;---------
zGtTmpGloType(notCT,localeColl) ;
	q $s(notCT=2:"%etGlo",1:($s(notCT=1:"pGloRg",1:"pGlo")_$s(localeColl:"C",1:"")))
	;;#eof#  ;;#inline#
	;---------
	; NAME:         %zRelTmpGlo
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Kill the temporary space in PPG space.
	;               !!! Caution !!! Don't use on whole global. only use on temporary space
	; PARAMETERS:
	;   gloName (I,REQ) - pointer to process private global sub-node allocated by zGtTmpGlo (above)
	; SIDE EFFECTS: kill gloName and reference to "CALLER" if any
	;  *gt   3/14 M7023648 - Check for locale-collated globals
%zRelTmpGlo(gloName) ;
	q:gloName=""
	;;#if#{CoreModeLocalVersion}
	k @gloName
	q
	;;#else#
	n numSubs,baseGlo,uniq,fullGloName,key
	;;#strip# Convert to canonical form since $QL and $QS expect that
	s fullGloName=$name(@gloName)
	s numSubs=$QL(fullGloName) i numSubs>0 s baseGlo=$name(@fullGloName,numSubs-1),uniq=$QS(fullGloName,numSubs)
	i uniq'="" k @baseGlo@("CALLER",uniq)
	i baseGlo'="" d
	. s key=@baseGlo@("KEY",1,gloName)
	. k:key'="" @baseGlo@("KEY",key),@baseGlo@("KEY",1,gloName)
	i $$zGetGloCleanupMode()>0 d %zUnregGlobal(fullGloName) i 1  ;*DJK 05/15 T7024 - unregister only if glo leak checking is enabled
	e  k @fullGloName
	i numSubs>1,$D(@baseGlo)=1 d
	. i %EA("E","pGlo")'="",baseGlo=$name(@%EA("E","pGlo")) q
	. i %EA("E","pGloRg")'="",baseGlo=$name(@%EA("E","pGloRg")) q
	. i %EA("E","pGloC")'="",baseGlo=$name(@%EA("E","pGloC")) q             ;*gt+1 3/14 M7023648 - check for locale collated globals
	. i %EA("E","pGloRgC")'="",baseGlo=$name(@%EA("E","pGloRgC")) q
	. i %EA("E","%etGlo")'="",baseGlo=$name(@%EA("E","%etGlo")) q
	. d %zRelTmpGlo(baseGlo)
	q
	;;#endif#
	;;#eof# ;;#coreBootstrap#
	;---------
	; NAME:         %zRelAllTmpGlo
	; SCOPE:        INTERNAL
	;               _Foundations
	; DESCRIPTION:  Releases all non-shared temporary globals allocated
	;               to this process. Cleans up open audit batches.
%zRelAllTmpGlo() ;
	;call zTagExists since core libraries (%Zefnlih) load before chronicles routines (EAETBT4). This check can be removed in 2015+1
	i $$zTagExists("checkBatchesServer^EAETBT4") s %=$$checkBatchesServer^EAETBT4() ;cleans up any active batches
	i %EA("E","pGlo")'="" d %zRelTmpGlo(%EA("E","pGlo"))
	i %EA("E","pGloRg")'="" d %zRelTmpGlo(%EA("E","pGloRg"))
	i %EA("E","pGloC")'="" d %zRelTmpGlo(%EA("E","pGloC"))
	i %EA("E","pGloRgC")'="" d %zRelTmpGlo(%EA("E","pGloRgC"))
	i %EA("E","%etGlo")'="" d %zRelTmpGlo(%EA("E","%etGlo"))
	s %=$$GlobalCleanUp^%ZeCLEANUP($J,$$zgUserID(),$$zClient^elibEALIBG(1)) ;*DJK 05/15 T7024 - Clean up globals  ;Uses extended reference to avoid direct dependency on ^EALIB
	k rqst("%ET") ;Caches the current batch ID
	q
	;;#eof#
	;
	;---------
	; NAME:         zGtTmpGloShrd
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Returns the name of temporary space that can be shared cross-process
	; PARAMETERS:
	;  notCT      (I,OPT) - Determines whether returned global is mapped to CACHETEMP
	;                       (parameter not valued) or not (parameter set to 1).
	;  localeColl (I,OPT) - Set to 1 if the global should collate using the locale collation, otherwise
	;                       will use the default environment collation.
	; RETURNS:      Temporary global name as ^GLOBAL(...)
	; REVISION HISTORY:
	; *gt 3/14 M7023648 - support locale collated globals
	; *gt 1/15 350745 - Use correct variable for collation check
	; *SWF 05/15 T7065 - Add caller info
	;                    Use zNumMaxIncrement
	; *JCK 05/15 T6951 - Mark coreBootstrap, add CoreModeLocalVersion
	; *SBA 12/15 394107 - Append the unique nodeID
	;---------
zGtTmpGloShrd(notCT,localeColl) n baseGloName,gloName,uniq,error
	s baseGloName=$s(notCT:"^ESNTEMPUNIQ",1:"^ESTEMPUNIQ")
	;;#if#{CoreModeLocalVersion}
	; Core bootstrap version does not support collation
	;;#else#
	;;#if# $$zCollEnvLocaleEnabled^%ZefnliCollation()
	i localeColl s baseGloName=baseGloName_"COLL"
	e  s baseGloName=baseGloName_$$zGtNodeID()_"A"  ;*SBA+2 12/15 394107 - Append nodeID +"A"(sanity check to ensure if zGtNodeID ever returns "COLL", it doesn't interfere with COLLATION globals)
	;;#else#
	s baseGloName=baseGloName_$$zGtNodeID()_"A"
	;;#endif#
	;;#endif#
	f  d  q:'$d(@gloName)
	. s uniq=$$zIncSafe(baseGloName,1)  ;*SWF 05/15 T7065 use new function
	. s gloName=$name(@(baseGloName_(uniq#100))@(uniq\100))
	;;#if#{CoreModeLocalVersion}
	; Core bootstrap version does not support collation and extra tracking information
	q gloName
	;;#else#
	;;#if# $$zCollEnvLocaleEnabled^%ZefnliCollation()
	i localeColl,$d(@$na(@gloName,0))=0,'$$zCollIsGlobalLocale(gloName),'$$zCollCreateGlobal($na(@gloName,0),$$zCollGetLocaleCode(),.error) d %zErrLog("Unable to create locale collated shared temp global: "_error,"",1)
	;;#endif#
	;*SWF 05/15 T7065 add caller info AFTER collation magic
	s @gloName=$$zPIDLong()_$c(1)_$$time(1)_$c(1)_$$zProcGetStackString(1)
	q $na(@gloName@(1))
	;;#endif#
	;;#eof# ;;#coreBootstrap#
	;
	;---------
	; NAME:         %zRelTmpGloShrd
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Kill the temporary space in shared space
	; PARAMETERS:
	;   gloName (I,REQ) - pointer to global sub-node allocated by zGtTmpGloShrd (above)
	; REVISIONS:
	;  *SWF 05/15 T7065 - Kill at first subscript level
	;                     Inline
	;---------
%zRelTmpGloShrd(gloName) k:gloName'="" @$na(@gloName,1)
	q  ;;#eof#  ;;#inline# ;;#coreBootstrap#
	;
	;---------
	; NAME:         zNumMaxIncrement
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Returns a "big enough" safe maximum for $INCREMENT
	; RETURNS:      99999999999999999
	; REVISIONS:
	;  *SWF 05/15 T7065 Created
	;---------
zNumMaxIncrement() q 99999999999999999 ;;#eof#  ;;#inline#  ;;#coreBootstrap#
	;
	;---------
	; NAME:         zIncSafe
	; SCOPE:        PUBLIC
	; DESCRIPTION:  safely increment a global
	; PARAMETERS:
	;  glo (I,REQ) - the $name of the global to increment (my include subscripts)
	;  resetCnt (I,OPT,DEFAULT: 0) - if the global needs to be reset because the counter is too high,
	;                                it will be reset to this number
	;                                the next number returned will be at least resetCnt+1
	; RETURNS:      incremented value
	; REVISIONS:
	;  *SWF 05/15 T7065 Created
	;---------
zIncSafe(glo,resetCnt) ;
	n ret
	s ret=$i(@glo)
	q:ret<$$zNumMaxIncrement() ret
	d %zzIncSafeReset(glo,resetCnt)
	q $i(@glo)  ;;#eof#  ;;#coreBootstrap#
	;
	;---------
	; NAME:         %zzIncSafeReset
	; SCOPE:        EPIC
	; DESCRIPTION:  reset a global that has been $I'd too high
	; PARAMETERS:
	;  glo (I,REQ) - the $name of the global to reset (may include subscripts)
	;  resetCnt (I,OPT,DEFAULT: 0) - the number to reset to
	; REVISIONS:
	;  *SWF 05/15 T7065 Created
	;---------
%zzIncSafeReset(glo,resetCnt) ;
	n lockGlo
	s lockGlo=$na(^HFLAG("E","INC-RESET",glo))
	l +@lockGlo  ;zunlockG isn't coreBootstrap
	s:@glo'<$$zNumMaxIncrement() @glo=+resetCnt
	l -@lockGlo  ;zunlockG isn't coreBootstrap
	q  ;;#eof#  ;;#coreBootstrap#
	;---------
	; NAME:         zGetGloCleanupMode
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Gets the global cleanup setting.
	; RETURNS:      The global cleanup setting
	; REVISIONS:
	;  *DJK 06/15 T7024 - Created
	;---------
zGetGloCleanupMode() ;
	q $$zGetGloCleanupMode^%Zelibh() ;;#strip#
	;;#if#$$zOIDType^%Zefnlih()=1
	;;#if#^%ZeOSDF("Cleanup")'=1
	q 0
	;;#elsif#^%ZeOSDF("Cleanup","Global")=1
	q 1
	;;#elsif#^%ZeOSDF("Cleanup","Global")=2
	q 2
	;;#else#
	q 0
	;;#endif#
	;;#else#
	q $S(%EA("E","CLEANUP","Global")'="":%EA("E","CLEANUP","Global"),1:$$zGetGloCleanupModeCustNoCache())
	;;#endif#
	;;#eof# ;;#inline#
	;---------
	; NAME:         zGetGloCleanupModeCustNoCache
	; SCOPE:        PRIVATE
	; DESCRIPTION:  Helper function for zGetGloCleanupMode.
	;               Performs full (non-cached) check for whether global leak checking is turned on at a customer site,
	;               caches the result in %EA, and returns the result.
	; RETURNS:      The global cleanup setting
	; REVISIONS:
	; *YZH 07/15 401180 - use zgCurLWSIDEnt to remove the dependency to non-core code
	;---------
zGetGloCleanupModeCustNoCache() ;
	n wsid,gloClnupSetting
	s wsid=$$zgCurLWSIDEnt()
	i wsid="" s %EA("E","CLEANUP","Global")=0 q 0
	s gloClnupSetting=+^debug("GlobalCleanup",wsid)
	s %EA("E","CLEANUP","Global")=gloClnupSetting
	q gloClnupSetting ;;#eof# ;;#private-zGetGloCleanupSetting#
	;
	;---------
	; NAME:         zOIDType
	; SCOPE:        PUBLIC
	; DESCRIPTION:  This returns the environment type stored in ^%ZeOSDF("OID","ETYPE")
	; RETURNS:      1 - Epic (internal only)
	;               2 - Production (customer)
	;               3 - Non-Production (customer)
	; REVISION HISTORY:
	;   *APG 05/15 T7144 - Support directly calling the tag
zOIDType() ;
	n val s val=^%ZeOSDF("OID","ETYPE") q $s(val=1:1,val=2:2,1:3)  ;;#strip#
	;;#if#^%ZeOSDF("OID","ETYPE")=1
	q 1
	;;#elsif#^%ZeOSDF("OID","ETYPE")=2
	q 2
	;;#else#
	q 3
	;;#endif#
	;;#eof# ;;#inline#
	;---------
	; NAME:         zIsEpicOID
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Returns whether ^%ZeOSDF("OID","ETYPE")=1
	; RETURNS:
	;  Returns 1 if ^%ZeOSDF("OID","ETYPE")=1 (epic environment)
	; REVISION HISTORY:
	;  *SV 03/09 M708616 - Created
zIsEpicOID() ;
	;;#if# ^%ZeOSDF("OID","ETYPE")=1
	Q 1
	;;#else#
	Q 0
	;;#endif#
	;;#eof#  ;;#inline# ;;#coreBootstrap#
	;
	;---------
	; NAME:         %zetGlo
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Initializes %etGlo
	; ASSUMES:      %etGlo
	; REVISIONS:
	; *YZH 07/15 401180 - Call new API to allocate %etGlo
	;---------
%zetGlo() s %etGlo=$s(%etGlo'="":%etGlo,1:$$zzzGtAuditTmpGloAllocPPG())
	q
	;;#eof# ;;#inline#
	;
	;---------
	; NAME:         %zsetPCMPCD
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Sets PCM and PCD variables from ^%ZeOSDF setting
	; ASSUMES:      PCM,PCD
	;---------
%zsetPCMPCD() ;
	S PCM=^%ZeOSDF("PCM"),PCD=^%ZeOSDF("PCD") S:PCM="" PCM=1,PCD=2
	q  ;;#eof#
	;
	;---------
	; NAME:         zNeedsExtDirRef
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Check to see whether a global needs an extended directory reference
	; PARAMETERS:
	; RETURNS:      1 if the variable entered is a global (begins with a "^") that requires
	;               an extended directory reference (doesn't begin with "^||" or "^["); and 0 otherwise
	;               NOTE: Does not validate the string is actually a legitimate variable, e.g.
	;                     this will return 1 on something like ^%A%, even though that isn't a legal name
	; REVISION HISTORY:
	;  *RHO 06/09 M708897 - Created
	;
zNeedsExtDirRef(var) q $s($e(var)'="^":0,$e(var,2)?1A:1,$e(var,2)="%":1,1:0) ;;#eof#  ;;#inline#
	;
	;---------
	; NAME:         %zWrapPrint
	; SCOPE:        PUBLIC
	; DESCRIPTION:  Wrap a given string to fit a terminal's width and
	;               then write out the text to the current open device.
	; PARAMETERS:
	;  str  (I,REQ)  - The string to wrap and display
	;  wd   (I,OPT)  - The width of the terminal/device which will be
	;                  written to. If null, the default is 80 characters
	;  prfx (I,OPT) - If the text wraps, this prefix will be placed in
	;                 front of lines 2+.
	;  fwd  (I,OPT) - Width of the first line of text
	;  lead (I,OPT) - Pass 1 to preserve leading spaces
	;  txt  (O,OPT)  - The wrapped text array if needed by calling code.
	; SIDE EFFECTS: Kills txt completely before using it, writes out
	;               formatted string, make sure current open device is
	;               appropriate before calling this function
	;
	; REVISION HISTORY:
	;  *omh 09/21/10 M7011386 - Created
	;
	; NOTE: This function will not output blank lines before or after
	;       the lines of the wrapped string
	;
%zWrapPrint(str,wd,prfx,fwd,lead,txt) n ln
	k txt s:+wd=0 wd=80
	d %zwrap(str,.txt,wd,prfx,fwd,lead)
	f ln=1:1:txt(0) w:ln'=1 ! w txt(ln)
	q
	;;#eof# ;;#coreBootstrap#
	;
	;*********************************************************************
	; DEPRECATED FUNCTIONS
	;*********************************************************************
	;
	;---------
	; NAME:         %zEMPLst
	; STATUS:       DEPRECATED
	; DESCRIPTION:  Sets up %EA("E","EMP") with the list of INIs only
	;               installed in EMP.
	;
	;---------
%zEMPLst() n pc,list
	s list=$$zEMPini()
	f pc=1:1:$l(list,",") s %EA("E","EMP",$p(list,",",pc))=1
	s %EA("E","EMP")=1
	q  ;;#eof#  ;;#deprecated#If needed, use ($$)zEMPini instead.
	;
	;---------
	; NAME:         EMPLst
	; STATUS:       DEPRECATED
	; DESCRIPTION:  Wrapper around %zEMPLst
	;
	;---------
EMPLst() d %zEMPLst()
	q 1
	;;#eof#  ;;#deprecated#If needed, use ($$)zEMPini instead.
	;
	;---------
	; NAME:         zGetESUn
	; STATUS:       DEPRECATED
	; DESCRIPTION:  This function returns a unique node in the given ^ESTEMP* global or some other global.
	; PARAMETERS:
	;   global (I,REQ) - Global name in which unique node is desired
	;   fromGtTmpGlo (I,OPT) - set to 1 if called from zGtTmpGlo (parameter isn't used)
	; RETURNS:      Unique Node if ok else returns NULL
	; DECLARES:     fromGtTmpGlo
	;
zGetESUn(global,fromGtTmpGlo) q:global="" ""
	n uniq S uniq=$I(@global,1)
	s $ec="",@global@("CALLER",uniq)=$stack($stack-1,"PLACE")
	i $D(@global@(uniq)) k @global@(uniq)
	q uniq  ;;#eof#  ;;#deprecated# Please use ($$)zGtTmpGlo instead.
	;
	;---------
	; NAME:         zGtPrGlo
	; STATUS:       DEPRECATED
	; DESCRIPTION:  This function is a wrapper around zGtTmpGlo
	; PARAMETERS:
	;  notCT (I,OPT) - <Please see zGtTmpGlo documentation>
	; RETURNS:      Name of PPG
	;---------
zGtPrGlo(notCT) q $$zGtTmpGlo(notCT) ;;#eof#  ;;#inline# ;;#deprecated# Please use ($$)zGtTmpGlo instead.
	;
	;
	q  ;;#eor#
	;Routine accessed by Timothy A Coffman
