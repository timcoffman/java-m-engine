EALIBECF1  ; ; ;2015-01-21 11:44:22;8.2;Y+qQAnjAnf6LXuHjD4U25nmZ4CFwj/OL8yd5zxgbvvnz+unQEfQoG0ThGsxe7XIp
	;;#lglob#EALIB
	; Copyright (C) Epic Systems Corporation 2006-2015
	;*********************************************************************
	; TITLE:   EALIBECF1
	; PURPOSE: Epic Communication Foundation Server API
	; AUTHOR:  Mike Lee
	; CALLABLE TAGS:
	;   zECFSetDestGlobal  Set the destination of all set commands to a global, instead of the client
	;   zECFSetDestClient  Set the destination of all set commands to the client, instead of a global
	;   zECFInitDest   Initialize the destination to one previously set
	;   zECFGetDest    Get the identifier of the current destination mode
	;   zECFReleaseDestGlobal  Release the global created via zECFSetDestGlobal
	;   zECFGet        Get the value of a single property value
	;   zECFGetWasSent Get whether the single property value was sent from the client
	;   zECFGetElmt    Get the value of an element of an array, list, or dictionary property
	;   zECFGetCell    Get the value of a cell of a table property
	;   zECFNumElmts   Get the number of elements in an array or list property
	;   zECFDctNxKey   Get the next key in a dictionary property
	;   zECFTblNxCol   Get the next column name in a table property
	;   zECFMrgIn      Get a structure representing an incoming property
	;   zECFTblMrgIn   Return a structure representing an incoming parameter
	;   zECFNew        Create a new response property
	;   zECFNewElmtObj Create a new object as an element in an array, list, or dictionary property
	;   zECFReset      Reset a property value back to its default
	;   zECFSet        Set the value of a property
	;   zECFSetElmt    Set the value of an element in an array, list, or dictionary property
	;   zECFNewCol     Create a new column on a table property
	;   zECFSetRow     Set a row on a table property
	;   zECFStream     Stream an unstructured response back to the client
	;   zECFMrgOut     Merge an array into an array, list, or dictionary property
	;   zECFTblMrgOut  Merge an array into a table outgoing parameter
	;   zECFTmout      Determines whether the server command has exceeded its time limit
	;   zECFThrow      Throws an exception during command execution and code execution will return to the client process
	;
	; REVISION HISTORY:
	; *JAM    02/12 M7016113 - zECFThrow: add zzNoLog param
	; *DLM    07/12 M7017485 - zECFGet*: support global-mode with positive property numbers
	; *MAT    06/13 M7020868 - Restore behavior to only log error once per day in zECFThrow
	; *MAT    08/13 M7021456 - Fix logic issues for buffer flush in zECFSet*
	; *AGG    02/14 M7023368 - Remove use of %ecfStrongEnc variable
	; *MAT    03/14 M7023709 - Change name of variable from %saved in zECFThrow
	; *MAT    10/14 M7026422 - Add functions to stream data to client without response object
	;*********************************************************************
	;---------
	; NAME:         zECFSetDestGlobal (PUBLIC)
	; DESCRIPTION:  Set the destination of all set commands to a global, instead of the client
	; KEYWORDS:     ECF,Set,Destination,Global
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  shared       (I,OPT,DEF:0) - whether to create a global that can be shared across processes
	; RETURNS:      A mode identifier that can be passed in to zECFInitDest or zECFGet* functions
	; ASSUMES:      %ecfMode,%ecfFtrs
zECFSetDestGlobal(shared)  ;
	n tmpGlo
	i shared s tmpGlo=$$zGtTmpGloShrd(),%ecfMode="-1"_$c(1)_tmpGlo_$c(1)_"1" i 1
	e  s tmpGlo=$$zGtTmpGlo(),%ecfMode="-1"_$c(1)_tmpGlo
	s @tmpGlo=-1
	q %ecfMode  ;;#eof#
	;
	;---------
	; NAME:         zECFSetDestClient (PUBLIC)
	; DESCRIPTION:  Set the destination of all set commands to the client, instead of a global
	; KEYWORDS:     ECF,Set,Destination,Client
	; CALLED BY:    ECF Application code.
	; RETURNS:      A mode identifier that can be passed in to zECFInitDest or zECFGet* functions
	; ASSUMES:      %ecfMode
zECFSetDestClient()  ;
	;;#strip# only set the first piece, to leave the global piece intact for future calls to zECFGet* with negative numbers
	s $p(%ecfMode,$c(1),1)="0"
	q %ecfMode  ;;#eof#
	;
	;---------
	; NAME:         zECFInitDest (PUBLIC)
	; DESCRIPTION:  Initialize the destination to one previously set
	; KEYWORDS:     ECF,Set,Destination
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  mode         (I,REQ) - the mode identifier created via zECFSetDestGlobal or zECFSetDestClient
	; RETURNS:      1 if successful
	; ASSUMES:      %ecfMode
zECFInitDest(mode)  ;
	s %ecfMode=mode
	q 1  ;;#eof#
	;
	;---------
	; NAME:         zECFGetDest (PUBLIC)
	; DESCRIPTION:  Get the identifier of the current destination mode
	; KEYWORDS:     ECF,Get,Destination
	; CALLED BY:    ECF Application code.
	; PARAMETERS:  None
	; RETURNS:      Identifier of the current destination mode, for use in calling zECFGet cross-modes
	; ASSUMES:      %ecfMode
zECFGetDest()  ;
	s:%ecfMode="" %ecfMode="0"
	q %ecfMode   ;;#eof#
	;
	;---------
	; NAME:         zECFReleaseDestGlobal (PUBLIC)
	; DESCRIPTION:  Release the destination global created via zECFSetDestGlobal
	; KEYWORDS:     ECF,Set,Destination
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  mode         (I,OPT:DEFAULT:previously set mode) - the mode identifier created via zECFSetDestGlobal
	; RETURNS:      1 if successful
	; ASSUMES:    %ecfMode
zECFReleaseDestGlobal(mode)  ;
	s:mode="" mode=%ecfMode
	i $p(mode,$c(1),3) d %zRelTmpGloShrd($p(mode,$c(1),2)) i 1
	e  d %zRelTmpGlo($p(mode,$c(1),2))
	q 1  ;;#eof#
	;
	;---------
	; NAME:         zECFGet (PUBLIC)
	; DESCRIPTION:  Get the value of a single property
	; KEYWORDS:     ECF,Get,Incoming,Single
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the property to retrieve
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number if this property is child of a complex property
	;  more         (IO,OPT)- If returns > 0, pass this in to the next call to get the next piece of data
	;  mode         (I,OPT,DEFAULT:%ecfMode) - the mode that determines whether to retrieve the data from the client of the global
	;               used only in the case that parentPropId is not supplied.  Only necessary if parentPropId
	;               is the root object (null), or you are trying to get a property that was set in a mode
	;               that is not the last set global mode.
	; RETURNS:      The value of the property, or the property number if the property is a complex property.
	;               more = 0 if all the data is returned, otherwise > 0
	;               if (local) is used for the timeZone, timeZone will have the actual timeZone used for optimization of the next call
	; ASSUMES:      %ecfInBufLoc,%ecfMode,%sv1,%sv5
	; SIDE EFFECTS: uses %sv1,%sv5 without newing
	; %sv1  = value
	; %sv5  = global to get data from
	; *DLM 07/12 M7017485 - support global-mode with positive property numbers
zECFGet(propName,parentPropId,more,mode)  ;
	k %sv1
	s:mode="" mode=%ecfMode
	s:parentPropId="" parentPropId=+mode
	s %sv5=$p(mode,$c(1),2)
	s %sv5=$s(%sv5]"":%sv5,1:%ecfInBufLoc)
	s:+more=0 more=1
	f  s %sv1=%sv1_@%sv5@(parentPropId,propName,1,more),more=$o(@%sv5@(parentPropId,propName,1,more)) q:more=""  i $l(%sv1)+$l(@%sv5@(parentPropId,propName,1,more))>%EA("maxCacheString") q
	q %sv1  ;;#eof#
	;
	;---------
	; NAME:         zECFGetWasSent (PUBLIC)
	; DESCRIPTION:  Get whether the value of a single property or collection element was sent
	; KEYWORDS:     ECF,Get,Incoming,Single,Property,Sent
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the property to check was sent
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number if this property is child of a complex property
	;  lineOrKey    (I,REQ) - The line number if the property is an array or list, or the key if the property is a dictionary
	;  mode         (I,OPT,DEFAULT:%ecfMode) - the mode that determines whether to retrieve the data from the client of the global
	;               used only in the case that parentPropId is not supplied.  Only necessary if parentPropId
	;               is the root object (null), or you are trying to get a property that was set in a mode
	;               that is not the last set global mode.
	; RETURNS:      Whether the value of the property was sent
	; ASSUMES:      %ecfInBufLoc,%ecfMode,%sv5,%ecfVersion
	; SIDE EFFECTS: uses %sv5 without newing
	; %sv5  = global to get data from
	; *DLM 07/27/09 M709339 - created (Note: %ecfVersion="" is version 1 Ecf in Epic.Core.Ecf.dll)
	; *DLM 07/12 M7017485 - support global-mode with positive property numbers
zECFGetWasSent(propName,parentPropId,lineOrKey,mode)  ;
	s:mode="" mode=%ecfMode
	s:parentPropId="" parentPropId=+mode
	s %sv5=$p(mode,$c(1),2)
	s %sv5=$s(%sv5]"":%sv5,1:%ecfInBufLoc)
	q:lineOrKey]"" $o(@%sv5@(parentPropId,propName,lineOrKey,""))]""
	i '%ecfVersion d
	. n errRsn s errRsn="The communication assembly Epic.Core.Ecf.dll does not support "
	. s errRsn=errRsn_"ignoring properties and renders a call to $$zECFGetWasSent invalid."
	. s %=$$zECFThrow("CLIENT-CANNOT-IGNORE-PROPERTIES",errRsn)
	q $O(@%sv5@(parentPropId,propName,""))'=""  ;;#eof#
	;
	;---------
	; NAME:         zECFGetElmt (PUBLIC)
	; DESCRIPTION:  Get the value of an element in an array, list, or dictionary property
	; KEYWORDS:     ECF,Get,Incoming,Array,List,Dictionary
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the property to retrieve
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number if this property is child of a complex property
	;  lineOrKey    (I,REQ) - The line number if the property is an array or list, or the key if the property is a dictionary
	;  more         (IO,OPT) - If returns > 0, pass this in to the next call to get the next piece of data
	;  mode         (I,OPT,DEFAULT:%ecfMode) - the mode that determines whether to retrieve the data from the client of the global
	;               used only in the case that parentPropId is not supplied.  Only necessary if parentPropId
	;               is the root object (null), or you are trying to get a property that was set in a mode
	;               that is not the last set global mode.
	; RETURNS:      The value of the property element, or the property number if the property is a complex property.
	;               more = 0 if all the data is returned, otherwise > 0
	; ASSUMES:      %ecfInBufLoc,%ecfMode,%sv1,%sv5
	; SIDE EFFECTS: uses %sv1,%sv5 without newing
	; %sv1  = value
	; %sv5  = global to get data from
	; *DLM 07/12 M7017485 - support global-mode with positive property numbers
zECFGetElmt(propName,parentPropId,lineOrKey,more,mode)  ;
	k %sv1
	s:mode="" mode=%ecfMode
	s:parentPropId="" parentPropId=+mode
	s %sv5=$p(mode,$c(1),2)
	s %sv5=$s(%sv5]"":%sv5,1:%ecfInBufLoc)
	i more="" i $$zbitAnd(@%sv5@(parentPropId,propName),32)=32 q:@%sv5@(parentPropId,propName,lineOrKey,1)=0 ""
	s:more="" more="1,"_@%sv5@(parentPropId,propName,lineOrKey,0)
	f  s %sv1=%sv1_@%sv5@(parentPropId,propName,lineOrKey,+more) s:((1+more)>$p(more,",",2)) more="" q:more=""  s $p(more,",")=1+more i $l(%sv1)+$l(@%sv5@(parentPropId,propName,lineOrKey,+more))>%EA("maxCacheString") q
	q %sv1  ;;#eof#
	;
	;---------
	; NAME:         zECFGetCell (PUBLIC)
	; DESCRIPTION:  Get the value of a cell of a table property
	; KEYWORDS:     ECF,Get,Incoming,Table,Cell
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the property to retrieve
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number if this property is child of a complex property
	;  row          (I,REQ) - The row number of the value to retrieve
	;  column       (I,REQ) - the column name of the value to retrieve
	;  more         (IO,OPT) - If returns > 0, pass this in to the next call to get the next piece of data
	;  mode         (I,OPT,DEFAULT:%ecfMode) - the mode that determines whether to retrieve the data from the client of the global
	;               used only in the case that parentPropId is not supplied.  Only necessary if parentPropId
	;               is the root object (null), or you are trying to get a property that was set in a mode
	;               that is not the last set global mode.
	; RETURNS:      The value of the cell.
	;               more = 0 if all the data is returned, otherwise > 0
	; ASSUMES:      %ecfInBufLoc,%ecfMode,%sv1,%sv5
	; SIDE EFFECTS: uses %sv1,%sv5 without newing
	; %sv1  = value
	; %sv5  = global to get data from
	; *DLM 07/12 M7017485 - support global-mode with positive property numbers
zECFGetCell(propName,parentPropId,row,column,more,mode)  ;
	k %sv1
	s:mode="" mode=%ecfMode
	s:parentPropId="" parentPropId=+mode
	s %sv5=$p(mode,$c(1),2)
	s %sv5=$s(%sv5]"":%sv5,1:%ecfInBufLoc)
	s:more="" more=1
	f  s %sv1=%sv1_@%sv5@(parentPropId,propName,row,column,more),more=$o(@%sv5@(parentPropId,propName,row,column,more)) q:more=""  i $l(%sv1)+$l(@%sv5@(parentPropId,propName,row,column,more))>%EA("maxCacheString") q
	q %sv1  ;;#eof#
	;
	;---------
	; NAME:         zECFNumElmts (PUBLIC)
	; DESCRIPTION:  Get the number of lines in an incoming property of array or list datastore or table type
	; KEYWORDS:     ECF,Incoming,Number,Lines,Array,Table,List
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the parameter
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number if this property is child of a complex property
	;  mode         (I,OPT,DEFAULT:%ecfMode) - the mode that determines whether to retrieve the data from the client of the global
	;               used only in the case that parentPropId is not supplied.  Only necessary if parentPropId
	;               is the root object (null), or you are trying to get a property that was set in a mode
	;               that is not the last set global mode.
	; RETURNS:      Number of lines in the parameter
	; ASSUMES:      %ecfInBufLoc,%ecfMode,%sv5
	; %sv5  = global to get data from
	; *DLM 07/12 M7017485 - support global-mode with positive property numbers
zECFNumElmts(propName,parentPropId,mode)  ;
	s:mode="" mode=%ecfMode
	s:parentPropId="" parentPropId=+mode
	s %sv5=$p(mode,$c(1),2)
	q @$s(%sv5]"":%sv5,1:%ecfInBufLoc)@(parentPropId,propName,0)  ;;#eof#
	;
	;---------
	; NAME:         zECFDctNxKey (PUBLIC)
	; DESCRIPTION:  Get the next key in an incoming parameter of dictionary datasource
	; KEYWORDS:     ECF,Incoming,Next Key,Key,Dictionary
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the dictionary property
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number if this property is child of a complex property
	;  key          (I,OPT) - The key to get the next key from
	;  mode         (I,OPT,DEFAULT:%ecfMode) - the mode that determines whether to retrieve the data from the client of the global
	;               used only in the case that parentPropId is not supplied.  Only necessary if parentPropId
	;               is the root object (null), or you are trying to get a property that was set in a mode
	;               that is not the last set global mode.
	; RETURNS:      The next key, or "" if the key was the last key
	; ASSUMES:      %ecfInBufLoc,%ecfMode,%sv5
	; %sv5  = global to get data from
	; *DLM 07/12 M7017485 - support global-mode with positive property numbers
zECFDctNxKey(propName,parentPropId,key,mode) ;
	s:mode="" mode=%ecfMode
	s:parentPropId="" parentPropId=+mode
	k %sv5 s %sv5=$p(mode,$c(1),2)
	s %sv5=$s(%sv5]"":%sv5,1:%ecfInBufLoc)
	f  s key=$o(@%sv5@(parentPropId,propName,key)) q:key=""  q:$d(@%sv5@(parentPropId,propName,key))>1
	q key ;;#eof#
	;
	;---------
	; NAME:         zECFTblNxCol (PUBLIC)
	; DESCRIPTION:  Get the next column of a table property
	; KEYWORDS:     ECF,Incoming,Next Column,Column,Table
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the table property
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number if this property is child of a complex property
	;  row          (I,OPT;DEFAULT=1) - the row number to use as a reference (not all lines contain values for all columns)
	;  column       (I,OPT) - The column used to get the next column
	;  mode         (I,OPT,DEFAULT:%ecfMode) - the mode that determines whether to retrieve the data from the client of the global
	;               used only in the case that parentPropId is not supplied.  Only necessary if parentPropId
	;               is the root object (null), or you are trying to get a property that was set in a mode
	;               that is not the last set global mode.
	; RETURNS:      The next column or null if the column was the last column
	; ASSUMES:      %ecfInBufLoc,%ecfMode,%sv5
	; %sv5  = global to get data from
	; *DLM 07/12 M7017485 - support global-mode with positive property numbers
zECFTblNxCol(propName,parentPropId,row,column,mode)  ;
	s:mode="" mode=%ecfMode
	s:parentPropId="" parentPropId=+mode
	s %sv5=$p(mode,$c(1),2)
	s:row="" row=1
	q $o(@$s(%sv5]"":%sv5,1:%ecfInBufLoc)@(parentPropId,propName,row,column))  ;;#eof#
	;
	;---------
	; NAME:         zECFMrgIn (PUBLIC)
	; DESCRIPTION:  Merge the value of an incoming property into the given variable name
	; KEYWORDS:     ECF,Merge,Incoming
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the property to retrieve
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number if this parameter is child of a complex parameter
	;  varName      (I,REQ) - The name of the variable in which to place the parameter value
	; RETURNS:      1 if successful, 0 if not
	; ASSUMES:      %ecfInBufLoc
	; SIDE EFFECTS: Kills the input variable stored in %ecfInBufLoc and stores in in varName
zECFMrgIn(propName,parentPropId,varName)  ;
	s:parentPropId="" parentPropId=0
	k @varName
	m @varName=@%ecfInBufLoc@(parentPropId,propName)
	k @%ecfInBufLoc@(parentPropId,propName)
	q 1  ;;#eof#
	;
	;---------
	; NAME:         zECFTblMrgIn (PUBLIC)
	; DESCRIPTION:  Merge the value of an incoming table property into the given variable name
	; KEYWORDS:     ECF,Merge,Table,Incoming
	; CALLED BY:    ECF Application code.
	; PARAMETERS:
	;  propName    (I,REQ) - The name of the parameter to retrieve
	;  parentNum    (I,OPT,DEFAULT:0) - the parent parameter number if this parameter is child of a complex parameter
	;                 this number is retrieved by calling zECFGet on the parent parameter
	;  varName      (I,REQ) - The name of the array in which to place the parameter value
	; RETURNS:      1 if successful, 0 if not
	; ASSUMES:      %ecfInBufLoc,%sv1,%sv2,%sv3,%sv4
	; SIDE EFFECTS: Kills the input variable stored in %ecfInBufLoc and stores in in varName
	;               uses %sv1,%sv2,%sv3,%sv4 without newing
	; %sv1 = row
	; %sv2 = col
	; %sv3 = more
	; %sv4 = line iterator
zECFTblMrgIn(propName,parentPropId,varName)  ;
	k %sv1,%sv2,%sv3,%sv4
	s:parentPropId="" parentPropId=0
	k @varName
	m @varName=@%ecfInBufLoc@(parentPropId,propName)
	k @%ecfInBufLoc@(parentPropId,propName)
	f %sv1=1:1:@varName@(0) d
	. f  s %sv2=$o(@varName@(%sv1,%sv2)) q:%sv2=""  d
	. . f %sv3=1:1:@varName@(%sv1,%sv2,0) d
	. . . s @varName@(%sv1,%sv2)=@varName@(%sv1,%sv2)_@varName@(%sv1,%sv2,%sv3)
	. . . k @varName@(%sv1,%sv2,%sv3)
	. . k @varName@(%sv1,%sv2,0)
	f  s %sv4=$o(@varName@(%sv4)) q:%sv4=""  f  s %sv2=$o(@varName@(%sv4,%sv2)) q:%sv2=""  s:'$d(@varName@(0,%sv2)) @varName@(0,%sv2)=$i(@varName@(0))
	q 1  ;;#eof#
	;
	;---------
	; NAME:         zECFNew (PUBLIC)
	; DESCRIPTION:  Create a new property to send to the client
	; KEYWORDS:     ECF,Outgoing,Response,Property,Definition
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the property to create
	;  parentPropId (I,OPT,DEFAULT:0 - the parent property number, if this property is child of a complex property
	;  propStore    (I,OPT,DEFAULT:S) - The datastore of the property
	;                  "S" = Single, "A" = Array, "D" = Dictionary, "L" = List, "T" = Table
	;  merge        (I,OPT,DEFAULT:0) - Not implemented.  When set, if this property is a list or dictionary, will merge rather than overwrite the property on the client (must be 0 or 1)
	;  enc          DEPRECATED
	; RETURNS:      The property number to use to set data for this parameter
	; ASSUMES:      %ecfOutBuf,%ecfFtrs,%ecfPckSz,%ecfMode,%sv1,%sv2,%ecfToken,%ecfEncrypt
	; SIDE EFFECTS:  uses %sv1,%sv2 without newing
	; %sv1 = propNum
	; %sv2 (in global mode) = global to set data into
zECFNew(propName,parentPropId,propStore,merge,enc)  ;
	g:((+%ecfMode)=-1) %zzECFNewGlobal^EGECFUTL
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s %ecfFtrs=%ecfFtrs+1,%sv1=%ecfFtrs
	s:parentPropId="" parentPropId=0
	i enc s %=$$zcrypt(propName,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.propName)
	s:($l(%ecfOutBuf)+$l(propName)+12'<%ecfPckSz) %=$$zECFSndRsltPckt()
	s %ecfOutBuf=%ecfOutBuf_($c(2)_$c($s(propStore="S":0,propStore="A":16,propStore="L":16,propStore="D":32,1:0))_$c((+merge*64)+(+enc*128))_$$zchrL(%sv1)_$$zchrL(parentPropId)_$c($l(propName))_propName)
	q %sv1  ;;#eof#
	;
	;---------
	; NAME:         zECFNewElmtObj (PUBLIC)
	; DESCRIPTION:  Create a new property object as an element of an array, list, or dictionary of complex objects
	; KEYWORDS:     ECF,Outgoing,Response,Property,Definition
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  containerPropId (I,REQ) - The Id of the property of which this property is an element
	;  key             (I,OPT,DEFAULT:"") - If the containing property is a dictionary, the key to associate with this property
	;  enc          DEPRECATED
	; RETURNS:      The property number to use to set data for this property
	; ASSUMES:      %ecfOutBuf,%ecfFtrs,%ecfPckSz,%ecfMode,%sv1,%ecfToken,%ecfEncrypt
	; SIDE EFFECTS:  uses %sv1,%sv2,%sv3,%sv4,%sv5 without newing
	; %sv1 = propNum
	; %sv2 (in global mode) = global to set data into
	; %sv3 (in global mode) = parent number of the container object
	; %sv4 (in global mode) = property name of the container object
	; %sv5 (in global mode) = line number for the new object
zECFNewElmtObj(containerPropId,key,enc)
	g:((+%ecfMode)=-1) %zzECFNewElmtObjGlobal^EGECFUTL
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s %ecfFtrs=%ecfFtrs+1,%sv1=%ecfFtrs
	i enc s:key]"" %=$$zcrypt(key,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.key)
	s:($l(%ecfOutBuf)+$s(key]"":$l(key)+12,1:11)'<%ecfPckSz) %=$$zECFSndRsltPckt()
	i key]"" s %ecfOutBuf=%ecfOutBuf_($c(2)_$c(2)_$c(+enc*128)_$$zchrL(%sv1)_$$zchrL(containerPropId)_$c($l(key))_key) i 1
	e  s %ecfOutBuf=%ecfOutBuf_($c(2)_$c(1)_$c(+enc*128)_$$zchrL(%sv1)_$$zchrL(containerPropId))
	q %sv1  ;;#eof#
	;---------
	; NAME:         zECFReset (PUBLIC)
	; DESCRIPTION:  Reset a property back to it's default value (as if the server never set it)
	; KEYWORDS:     ECF,Outgoing,Response,Property,Reset
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the property to reset
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number, if this property is child of a complex property
	;  enc          DEPRECATED
	; RETURNS:      1 if successful, 0 if not
	; ASSUMES:      %ecfOutBuf,%ecfFtrs,%ecfPckSz,%ecfMode,%ecfToken,%ecfEncrypt
zECFReset(propName,parentPropId,enc) ;
	g:((+%ecfMode)=-1) %zzECFResetGlobal^EGECFUTL
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s:parentPropId="" parentPropId=0
	i enc s:(propName]"") %=$$zcrypt(propName,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),,,.propName)
	s:($l(%ecfOutBuf)+$l(propName)+8'<%ecfPckSz) %=$$zECFSndRsltPckt()  ;*MAT 8/13 M7021456 - there are only 8 control characters, not 12
	s %ecfOutBuf=%ecfOutBuf_($c(3)_$c(0)_$c(0)_$$zchrL(parentPropId)_$c($l(propName))_propName)
	q 1  ;;#eof#
	;
	;---------
	; NAME:         zECFNewCol (PUBLIC)
	; DESCRIPTION:  Add a column to a table property
	; KEYWORDS:     ECF,Outgoing,Response,Property,Table,Column,Definition
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  tableId    (I,REQ) - The Id of the table property
	;  columnName (I,REQ) - The name of the column to add
	;  enc          DEPRECATED
	; RETURNS:      The column number to use to set data for this column
	; ASSUMES:      %ecfOutBuf,%ecfFtrs,%ecfPckSz,%ecfMode,%ecfEncrypt,%ecfToken,%sv1
	; SIDE EFFECTS:  uses %sv1,%sv2,%sv3,%sv4 without newing
	; %sv1 = colNum
	; %sv2 (in global mode) = global to set data into
	; %sv3 (in global mode) = parent id of the table
	; %sv4 (in global mode) = table property name
zECFNewCol(tableId,columnName,enc)
	g:((+%ecfMode)=-1) %zzECFNewColGlobal^EGECFUTL
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s %ecfFtrs(tableId)=%ecfFtrs(tableId)+1,%sv1=%ecfFtrs(tableId)
	i enc s %=$$zcrypt(columnName,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.columnName)
	s:($l(%ecfOutBuf)+$l(columnName)+10'<%ecfPckSz) %=$$zECFSndRsltPckt()
	s %ecfOutBuf=%ecfOutBuf_($c(1)_$c(0)_$c(0)_$$zchrL(tableId)_$$zchrW(%sv1)_$c($l(columnName))_columnName)
	q %sv1  ;;#eof#
	;
	;---------
	; NAME:         zECFSet (PUBLIC)
	; DESCRIPTION:  Set the value of a property
	; KEYWORDS:     ECF,Send,Outgoing,Single
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propNameOrId (I,REQ) - The property name if the function is being called for the first time
	;                  for this property, and $$zECFNew was not called for this property  Or,
	;                  the property number returned from $$zECFNew or the first call to
	;                  $$zECFSet for this property
	;  value        (I,REQ) - The value of the property  If zECFSnd was called before for this
	;                  property, the value will be appended to the previous value.
	;  parentPropId (I,OPT,DEFAULT:0) - The parent property id.  Only needed if propNameOrNum
	;                  is the property name and this property has a parent parameter.
	;  enc          DEPRECATED
	; RETURNS:      The property number to use in future calls to $$zECFSet for this parameter
	;               if (local) is used for timeZone, will set the actual timeZone used for future optimization
	; ASSUMES:      %ecfOutBuf,%ecfPckSz,%ecfFtrs,%ecfMode,%ecfEncrypt,%ecfToken,%sv1,%sv2,%sv3,%sv4,%sv5
	; SIDE EFFECTS:  uses %sv1,%sv2,%sv3,%sv4,%sv5 without newing
	; %sv1 = propNum
	; %sv2 (in client mode) = second part of the value after splitting for packet length
	; %sv3 (in client mode) = length of the value
	; %sv4 (in client mode) = length of the first part of the value
	; %sv5 (in client mode) = free space in the pack
	; %sv2 (in global mode) = global to set data into
zECFSet(propNameOrId,value,parentPropId,enc) ;
	s %sv1=+propNameOrId
	g:((+%ecfMode)=-1) %zzECFSetGlobal^EGECFUTL
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	g:(%sv1=propNameOrId) %zzECFSetValSwitch
	s %ecfFtrs=%ecfFtrs+1,%sv1=%ecfFtrs
	s:parentPropId="" parentPropId=0
	i enc s %=$$zcrypt(propNameOrId,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.propNameOrId)
	s:($l(%ecfOutBuf)+$l(propNameOrId)+12'<%ecfPckSz) %=$$zECFSndRsltPckt()
	s %ecfOutBuf=%ecfOutBuf_($c(2)_$c(0)_$c(+enc*128)_$$zchrL(%sv1)_$$zchrL(parentPropId)_$c($l(propNameOrId))_propNameOrId)
%zzECFSetValSwitch g:enc %zzECFSetValEnc
%zzECFSetVal s %sv2="",%sv3=$l(value)
	i %sv3=0 s:$l(%ecfOutBuf)+9>%ecfPckSz %=$$zECFSndRsltPckt() s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(0)_$c(0)_$$zchrL(%sv1)_$$zchrW(0)) q %sv1
	i ($l(%ecfOutBuf)+%sv3+9)'<%ecfPckSz s %sv4=%ecfPckSz-$l(%ecfOutBuf)-8,%sv2=$e(value,%sv4+1,999999999),value=$e(value,1,%sv4),%sv3=$l(value)
	i (%sv3>0)!($l(%sv2)=0) s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(0)_$c(0)_$$zchrL(%sv1)_$$zchrW(%sv3)_value)
	i %sv2]"" s %=$$zECFSndRsltPckt(),value=%sv2 g %zzECFSetVal
	q %sv1
%zzECFSetValEnc i $l(value)=0 s:$l(%ecfOutBuf)+9>%ecfPckSz %=$$zECFSndRsltPckt() s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(0)_$c(128)_$$zchrL(%sv1)_$$zchrW(0)) q %sv1  ;*MAT 8/13 M7021456 - don't append null value without buffer check
	s %sv2="",%sv5=%ecfPckSz-$l(%ecfOutBuf)-9,%sv5=%sv5-(%sv5#4)
	s %sv3=%sv5-(%sv5/32),%sv3=%sv3/4*3
	i $l(value)>%sv3 s %sv2=$e(value,%sv3+1,999999999),value=$e(value,1,%sv3)
	i (%sv3>0)!($l(%sv2)=0) s %=$$zcrypt(value,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.value),value=$e(value,1,$l(value)-1),%ecfOutBuf=%ecfOutBuf_($c(0)_$c(0)_$c(128)_$$zchrL(%sv1)_$$zchrW($l(value))_value)
	i %sv2]"" s %=$$zECFSndRsltPckt(),value=%sv2 g %zzECFSetValEnc
	q %sv1  ;;#eof#
	;
	;---------
	; NAME:         zECFSetElmt (PUBLIC)
	; DESCRIPTION:  Set an element in an array, list, or dictionary of simple types
	; KEYWORDS:     ECF,Send,Outgoing,Element
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  containerPropId  (I,REQ) - The array, list, or dictionary property id
	;  value            (I,REQ) - The value of the property element.
	;  key              (I,OPT,DEFAULT:0 - If the property is a dictionary, the key for this value.
	;  appendToPrevious (I,OPT,DEFAULT:0) - If the property is an array or list, whether to append this value to the previous element.
	;                       by default, this function will add a new element
	;  enc          DEPRECATED
	; RETURNS:      The property number
	; ASSUMES:      %ecfOutBuf,%ecfPckSz,%ecfFtrs,%ecfMode,%ecfEncrypt,%ecfToken,%sv2,%sv3,%sv4,%sv5
	; SIDE EFFECTS:  uses %sv2,%sv3,%sv4,%sv5 without newing
	; %sv2 (in client mode) = second part of the value after splitting for packet length
	; %sv3 (in client mode) = length of the value
	; %sv4 (in client mode) = length of the first part of the value
	; %sv5 (in client mode) = free space in the pack
	; %sv2 (in global mode) = global to set data into
	; %sv3 (in global mode) = parent id of the container
	; %sv4 (in global mode) = property name of the container
zECFSetElmt(containerPropId,value,key,appendToPrevious,enc)  ;
	g:((+%ecfMode)=-1) %zzECFSetElmtGlobal^EGECFUTL
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	g:('enc&(key="")) %zzECFSetElmtArrVal
	g:'enc %zzECFSetElmtDctVal
	g:(key="") %zzECFSetElmtArrValEnc
	s %=$$zcrypt(key,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),,,.key)
%zzECFSetElmtDctValEnc i '$l(value) s:$l(%ecfOutBuf)+$l(key)+10>%ecfPckSz %=$$zECFSndRsltPckt() s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(32)_$c(128)_$$zchrL(containerPropId)_$c($l(key))_key_$$zchrW(0)) q containerPropId  ;*MAT 8/13 M7021456 - don't append null value without buffer check
	s %sv2="",%sv5=%ecfPckSz-$l(%ecfOutBuf)-$l(key)-9,%sv5=%sv5-(%sv5#4)
	s %sv3=%sv5-(%sv5/32),%sv3=%sv3/4*3
	i $l(value)>%sv3 s %sv2=$e(value,%sv3+1,999999999),value=$e(value,1,%sv3)
	s %=$$zcrypt(value,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),,,.value),value=$e(value,1,$l(value)-1)
	s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(32)_$c(128)_$$zchrL(containerPropId)_$c($l(key))_key_$$zchrW($l(value))_value)
	i %sv2]"" s %=$$zECFSndRsltPckt(),value=%sv2 g %zzECFSetElmtDctValEnc
	q containerPropId
%zzECFSetElmtArrVal s %sv2="",%sv3=$l(value)
	i %sv3=0 s:$l(%ecfOutBuf)+9>%ecfPckSz %=$$zECFSndRsltPckt() s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(16)_$c(+appendToPrevious*64)_$$zchrL(containerPropId)_$$zchrW(0)) q containerPropId  ;*MAT 8/13 M7021456 - don't append null value without buffer check
	i ($l(%ecfOutBuf)+%sv3+9)'<%ecfPckSz s %sv4=%ecfPckSz-$l(%ecfOutBuf)-8,%sv2=$e(value,%sv4+1,999999999),value=$e(value,1,%sv4),%sv3=$l(value)
	s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(16)_$c(+appendToPrevious*64)_$$zchrL(containerPropId)_$$zchrW(%sv3)_value)
	i %sv2]"" s %=$$zECFSndRsltPckt(),value=%sv2,appendToPrevious=1 g %zzECFSetElmtArrVal
	q containerPropId
%zzECFSetElmtDctVal s %sv2="",%sv3=$l(value)
	i %sv3=0 s:$l(%ecfOutBuf)+$l(key)+10>%ecfPckSz %=$$zECFSndRsltPckt() s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(32)_$c(0)_$$zchrL(containerPropId)_$c($l(key))_key_$$zchrW(0)) q containerPropId  ;*MAT 8/13 M7021456 - don't append null value without buffer check
	i ($l(%ecfOutBuf)+%sv3+$l(key)+10)'<%ecfPckSz s %sv4=%ecfPckSz-$l(%ecfOutBuf)-$l(key)-9,%sv2=$e(value,%sv4+1,999999999),value=$e(value,1,%sv4),%sv3=$l(value)
	s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(32)_$c(0)_$$zchrL(containerPropId)_$c($l(key))_key_$$zchrW(%sv3)_value)
	i %sv2]"" s %=$$zECFSndRsltPckt(),value=%sv2 g %zzECFSetElmtDctVal
	q containerPropId
%zzECFSetElmtArrValEnc i '$l(value) s:$l(%ecfOutBuf)+9>%ecfPckSz %=$$zECFSndRsltPckt() s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(16)_$c(128+(+appendToPrevious*64))_$$zchrL(containerPropId)_$$zchrW(0)) q containerPropId  ;*MAT 8/13 M7021456 - don't append null value without buffer check
	s %sv2="",%sv5=%ecfPckSz-$l(%ecfOutBuf)-8,%sv5=%sv5-(%sv5#4)
	s %sv3=%sv5-(%sv5/32),%sv3=%sv3/4*3
	i $l(value)>%sv3 s %sv2=$e(value,%sv3+1,999999999),value=$e(value,1,%sv3)
	s %=$$zcrypt(value,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),,,.value),value=$e(value,1,$l(value)-1)
	s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(16)_$c(128+(+appendToPrevious*64))_$$zchrL(containerPropId)_$$zchrW($l(value))_value)
	i %sv2]"" s %=$$zECFSndRsltPckt(),value=%sv2,appendToPrevious=1 g %zzECFSetElmtArrValEnc
	q containerPropId  ;;#eof#
	;
	;---------
	; NAME:         zECFSetRow (PUBLIC)
	; DESCRIPTION:  Set a row of a table property
	; KEYWORDS:     ECF,Send,Outgoing,Table,Row
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  tableId (I,REQ) - The table property number
	;  cells   (I,REQ) - An array containing the cell values in the following format:
	;                 cells(colNum)=cellValue where colNum is the number returned from zECFNewCol for the column.
	;  enc          DEPRECATED
	; RETURNS:      1 if successful, 0 if not
	; ASSUMES:      %ecfOutBuf,%ecfFtrs,%ecfPckSz,%ecfMode,outBuf,%ecfDbg,%ecfEncrypt,%ecfToken,%elogID,
	;               %sv1,%sv2,%sv3,value
	; SIDE EFFECTS:  uses %sv1,%sv2,%sv3 without newing
	; %sv1 = col
	; %sv2 (in client mode) = maxCol
	; %sv3 (in client mode) = length of the value
	; %sv5 (in client mode) = free space in the pack
	; %sv2 (in global mode) = global to set data into
	; %sv3 (in global mode) = parent id of the table
zECFSetRow(tableId,cells,enc)  ;
	g:((+%ecfMode)=-1) %zzECFSetRowGlobal^EGECFUTL
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s %sv1=0,%sv2=%ecfFtrs(tableId)
	i $l(%ecfOutBuf)+9>%ecfPckSz s %=$$zECFSndRsltPckt()  ; check for 2 extra characters (7 characters are added in the line below, 2 to account for the zchrW(0) that might get added later)
	s %ecfOutBuf=%ecfOutBuf_($c(0)_$c(48)_$c(+enc*128)_$$zchrL(tableId))
	g:enc %zzECFSetRowCellEnc
%zzECFSetRowCell s %sv1=%sv1+1,value=cells(%sv1),%sv3=$l(value)
	i $l(%ecfOutBuf)+%sv3+6>%ecfPckSz s %ecfOutBuf=%ecfOutBuf_$$zchrW(0) s %=$$zECFSndRsltPckt()
	e  s %ecfOutBuf=%ecfOutBuf_($$zchrW(%sv1)_$$zchrW(%sv3)_value) g %zzECFSetRowCellFnsh
	s %ecfOutBuf=$c(0)_$c(48)_$c(64)_$$zchrL(tableId)_$$zchrW(%sv1)_$$zchrW(%sv3)_value
	i %sv3+13'>%ecfPckSz g %zzECFSetRowCellFnsh
	s %ecfOutBuf=%ecfOutBuf_$$zchrW(0) s %=$$zECFSndRsltPckt()
	g:%sv1=%sv2 %zzECFSetRowFnsh
	s %ecfOutBuf=$c(0)_$c(48)_$c(64)_$$zchrL(tableId)
%zzECFSetRowCellFnsh g:%sv1<%sv2 %zzECFSetRowCell
	g %zzECFSetRowFnsh
%zzECFSetRowCellEnc s %sv1=%sv1+1,value=cells(%sv1)
	s %=$$zcrypt(value,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),,,.value),value=$e(value,1,$l(value)-1),%sv3=$l(value)
	i $l(%ecfOutBuf)+%sv3+6>%ecfPckSz s %ecfOutBuf=%ecfOutBuf_$$zchrW(0) s %=$$zECFSndRsltPckt()
	e  s %ecfOutBuf=%ecfOutBuf_($$zchrW(%sv1)_$$zchrW($l(value))_value) g %zzECFSetRowCellEncFnsh
	s %ecfOutBuf=$c(0)_$c(48)_$c(192)_$$zchrL(tableId)_$$zchrW(%sv1)_$$zchrW(%sv3)_value
	i %sv3+13'>%ecfPckSz g %zzECFSetRowCellEncFnsh
	s %ecfOutBuf=%ecfOutBuf_$$zchrW(0) s %=$$zECFSndRsltPckt()
	g:%sv1=%sv2 %zzECFSetRowFnsh
	s %ecfOutBuf=$c(0)_$c(48)_$c(192)_$$zchrL(tableId)
%zzECFSetRowCellEncFnsh g:%sv1<%sv2 %zzECFSetRowCellEnc
%zzECFSetRowFnsh s:$l(%ecfOutBuf)>0 %ecfOutBuf=%ecfOutBuf_$$zchrW(0)
	q 1  ;;#eof#
	;
	;---------
	; NAME:         zECFStream (PUBLIC)
	; DESCRIPTION:  Use this API to stream back unstructured data to the client.
	;               This should only be used if the current request was called
	;               through a StreamCommand otherwise this will cause an error.
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  data (I,REQ) - The data to be sent back to the client
	; ASSUMES:      %ecfOutBuf, %ecfPckSz, %ecfStreamCmd
	;               %sv1 - length of data parameter
	;               %sv2 - current length of output buffer
	; RETURNS:      True for success
	; SIDE EFFECTS: Throws an exception if not called via StreamCommand
	;---------
	;*MAT 10/14 M7026422 - Created
zECFStream(data) ;traditional ECF commands should not use this function as there is no deserialization on the client
	i '%ecfStreamCmd s %=$$zECFThrow("ECF-INVALID-FUNCTION-CALL","The zECFStream function can only be used with StreamCommand type ECF commands.") q 0
%zzECFStreamLenChk s %sv1=$l(data),%sv2=$l(%ecfOutBuf)  ;only send up to max packet length at a time, subtract 5 for control characters tacked on in zECFSndRsltPckt
	i (%sv1+%sv2+5)>%ecfPckSz s %ecfOutBuf=%ecfOutBuf_$e(data,1,%ecfPckSz-%sv2-5),data=$e(data,%ecfPckSz-%sv2-4,%sv1) s %=$$zECFSndRsltPckt() g %zzECFStreamLenChk
	s %ecfOutBuf=%ecfOutBuf_data
	q 1 ;;#eof#
	;
	;---------
	; NAME:         zECFMrgOut (PRIVATE)
	; DESCRIPTION:  Populate an array, list, or dictionary property from an existing array
	; KEYWORDS:     ECF,Merge,Outgoing,Array
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propName  (I,REQ) - The name of the property to send
	;  parentPropId (I,OPT,DEFAULT:0 - the parent property number, if this property is child of a complex property
	;  varName   (I,REQ) - The name of the array to use to populate the property in the format:
	;               @varName@(line/key) where line starts with 1 or
	;               @varName@(line/key,overflow) where line and overflow start with 1
	;  dataStore (I,REQ) - The datastore of the property: "A" = Array, "L" = List, or "D" = Dictionary
	;  enc          DEPRECATED
	; RETURNS:      1 if successful, 0 if not
	; ASSUMES:      %ecfOutBuf,%ecfPckSz,%sv10,%sv11,%sv6,%sv7,%sv8,%sv9,%ecfEncrypt
	; SIDE EFFECTS: kills @varName
	;        uses %sv1,%sv2,%sv3,%sv4,%sv5,%sv6,%sv7,%sv8,%sv9,%sv10,%sv11 without newing
	; %sv1 = used by $$zECFNew
	; %sv2,%sv3,%sv4,%sv5 = used by $$zECFSetElmt
	; %sv6 = propNum
	; %sv7 = valid
	; %sv8 = line/key iterator
	; %sv9 = line incrementor (different than %sv8)
	; %sv10 = overflow
	; %sv11 = append
zECFMrgOut(propName,parentPropId,varName,dataStore,enc)  ;
	k %sv6,%sv7,%sv8,%sv9,%sv10,%sv11
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s %sv6=$$zECFNew(propName,parentPropId,dataStore,,enc)
	s %sv7=1
	g:(dataStore="D") %zzECFMrgOutDct
	s %sv8=0,%sv9=0
	i '$d(@varName@(1,1)) f  s %sv8=$o(@varName@(%sv8)) q:%sv8=""  s %sv9=%sv9+1 s %sv7=$$zECFSetElmt(%sv6,@varName@(%sv8),,,enc) q:'%sv7  i 1
	e  f  s %sv8=$o(@varName@(%sv8)) q:%sv8=""  s %sv9=%sv9+1,%sv10=0,%sv11=0 f  s %sv10=$o(@varName@(%sv8,%sv10)) q:%sv10=""  s %sv7=$$zECFSetElmt(%sv6,@varName@(%sv8,%sv10),,%sv11,enc),%sv11=1 q:'%sv7
	k @varName
	q %sv7
%zzECFMrgOutDct s %sv8=$o(@varName@(""),-1) q:%sv8="" %sv6
	i '$d(@varName@(%sv8,1)) s %sv8="" f  s %sv8=$o(@varName@(%sv8)) q:%sv8=""  s %sv7=$$zECFSetElmt(%sv6,@varName@(%sv8),%sv8,,enc) q:'%sv7  i 1
	e  s %sv8="" f  s %sv8=$o(@varName@(%sv8)) q:%sv8=""  s %sv10=0 f  s %sv10=$o(@varName@(%sv8,%sv10)) q:%sv10=""  s %sv7=$$zECFSetElmt(%sv6,@varName@(%sv8,%sv10),%sv8,,enc) q:'%sv7
	k @varName
	q %sv7  ;;#eof#
	;
	;---------
	; NAME:         zECFTblMrgOut (PRIVATE)
	; DESCRIPTION:  Populate a table property using an existing array
	;               Note: this method is not the most efficient way to send a table
	;               It is provided only as a complement to zECFTblMrgIn
	; KEYWORDS:     ECF,Merge,Outgoing,Table
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propName    (I,REQ) - The name of the property to send
	;  parentPropId (I,OPT,DEFAULT:0 - the parent property number, if this property is child of a complex property
	;  varName      (I,REQ) - The name of the array to use to populate the parameter in the form
	;                 @varName@(0,colName) to define the column names
	;                 @varName@(lineNum,colName) where lineNum starts with 1
	;  enc          DEPRECATED
	; RETURNS:      1 if successful, 0 if not
	; ASSUMES:      %ecfOutBuf,%ecfFtrs,%ecfPckSz,%sv6,%sv7,%sv8,%sv9,cells,%ecfEncrypt
	; SIDE EFFECTS: kills @varName
	;         uses %sv1,%sv2,%sv3,%sv5,%sv6,%sv7,%sv8,%sv9 without newing
	; %sv1 = used by $$zECFNew, $$zECFNewCol, and $$zECFSetRow (each kills it so it's ok to share between the three)
	; %sv2,%sv3,%sv5,%sv10 = used by $$zECFSetRow
	; %sv6 = propNum
	; %sv7 = col
	; %sv8 = row
	; %sv9 = valid
zECFTblMrgOut(propName,parentPropId,varName,enc)  ;
	k %sv6,%sv7,%sv8,%sv9
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s %sv6=$$zECFNew(propName,parentPropId,"T",,enc)
	f  s %sv7=$o(@varName@(0,%sv7)) q:%sv7=""  s @varName@(0,%sv7)=$$zECFNewCol(%sv6,%sv7,enc)
	s %sv9=1,%sv8=0
	f  s %sv8=$o(@varName@(%sv8)) q:%sv8=""  d  q:'%sv9
	. f  s %sv7=$o(@varName@(%sv8,%sv7)) q:%sv7=""  s:$d(@varName@(%sv8,%sv7)) cells(@varName@(0,%sv7))=@varName@(%sv8,%sv7)
	. s %sv9=$$zECFSetRow(%sv6,.cells,enc) q:'%sv9
	. k cells,@varName@(%sv8)
	k @varName
	q %sv9  ;;#eof#
	;
	;---------
	; NAME:         zECFNewTbl (PUBLIC)
	; DESCRIPTION:  Create a Table (This function is only compatible with ECF V2).
	;               In Global Mode, this call is essentially a no-op, since there are no zECFGet* functions
	;               to acccess V2-style tables
	; KEYWORDS:     ECF,Outgoing,Table
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  propName     (I,REQ) - The name of the property to send
	;  parentPropId (I,OPT,DEFAULT:0) - the parent property number, if this property is child of a complex property
	;  colDefs      (I,OPT) - an array containing the column definitions, in the following format:
	;                colDefs[0] = # of columns
	;                colDefs[#,"ParameterName"] = value
	;                the parameter names are defined by the object that implement IDataColumn on the client
	;  enc          DEPRECATED
	; RETURNS:      the table ID
	; ASSUMES:      %ecfOutBuf,%ecfFtrs,%ecfPckSz,%ecfMode,%ecfEncrypt,%ecfToken,%sv1,%sv2,%sv3,%sv4,%sv5
	; SIDE EFFECTS: uses %sv1,%sv2,%sv3,%sv4,%sv5 without newing
	;   %sv1 = property number
	;   %sv2 = column number
	;   %sv3 = column parameter name
	;   %sv4 = composite column parameters
	;   %sv5 = column parameter value
zECFNewTbl(propName,parentPropId,colDefs,enc)
	s %ecfFtrs=%ecfFtrs+1,%sv1=%ecfFtrs
	q:((+%ecfMode)=-1) %sv1
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s:parentPropId="" parentPropId=0
	i enc s %=$$zcrypt(propName,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.propName)
	s:($l(%ecfOutBuf)+$l(propName)+12'<%ecfPckSz) %=$$zECFSndRsltPckt()
	s %ecfOutBuf=%ecfOutBuf_($c(4)_$c(64)_$c(+enc*128)_$$zchrL(%sv1)_$c($l(propName)+4)_$$zchrL(parentPropId)_propName)
	s %sv2=0
%zzECFNewTblCol s %sv2=%sv2+1 g:%sv2>+colDefs(0) %zzECFNewTblDone
	s %sv3="",%sv4="",%ecfFtrs(%sv1)=%sv2 f  s %sv3=$o(colDefs(%sv2,%sv3)) q:%sv3=""  s %sv5=colDefs(%sv2,%sv3),%sv4=%sv4_$c($l(%sv3))_%sv3_$c($l(%sv5))_%sv5
	i enc s %=$$zcrypt(%sv4,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.%sv4)
	s:($l(%ecfOutBuf)+$l(%sv4)+9'<%ecfPckSz) %=$$zECFSndRsltPckt()
	s %ecfOutBuf=%ecfOutBuf_($c(20)_$c(80)_$c(+enc*128)_$$zchrL(%sv1)_$$zchrW($l(%sv4))_%sv4)
	g %zzECFNewTblCol
%zzECFNewTblDone k %sv4,%sv5 q %sv1  ;;#eof#
	;
	;---------
	; NAME:         zECFSetTblPrms (PUBLIC)
	; DESCRIPTION:  Set parameters on a table (This function is only compatible with ECF V2)
	;               In Global Mode, this call is essentially a no-op, since there are no zECFGet* functions
	;               to acccess V2-style tables
	; KEYWORDS:     ECF,Outgoing,Table
	; CALLED BY:    ECF Application Code
	; PARAMETERS:
	;  tblId        (I,REQ) - The ID of the table returned from zECFNewTbl
	;  params       (I,OPT) - an array containing table parameters, in the following format:
	;                params["ParameterName"] = value
	;                the parameter names are defined by the object that implement IDataTable on the client
	;  enc          DEPRECATED
	; RETURNS:      1 if successful, 0 if not (only returns 0 if called in global mode)
	; ASSUMES:      %ecfOutBuf,%ecfFtrs,%ecfPckSz,%ecfEncrypt,%ecfMode,%ecfToken,%sv3,%sv4,%sv5
	; SIDE EFFECTS: uses %sv3,%sv4,%sv5 without newing
zECFSetTblPrms(tblId,params,enc)
	q:((+%ecfMode)=-1) 0
	s enc=(%ecfEncrypt!enc)  ;*AGG 02/14 M7023368
	s %sv3="",%sv4="" f  s %sv3=$o(params(%sv3)) q:%sv3=""  s %sv5=params(%sv3),%sv4=%sv4_$c($l(%sv3))_%sv3_$c($l(%sv5))_%sv5
	i enc s %=$$zcrypt(%sv4,%ecfToken("Key"),$$ju()_%ecfToken("Pid"),"","",.%sv4)
	s:($l(%ecfOutBuf)+$l(%sv4)+9'<%ecfPckSz) %=$$zECFSndRsltPckt()
	s %ecfOutBuf=%ecfOutBuf_($c(20)_$c(96)_$c(+enc*128)_$$zchrL(tblId)_$$zchrW($l(%sv4))_%sv4)
	q 1  ;;#eof#
	;
	;---------
	; NAME:         zECFSndRsltPckt (PRIVATE)
	; DESCRIPTION:  Send a results packet corresponding to the current %ecfOutBuf
	; RETURNS:      1 if successful, 0 if not.  Kills %ecfOutBuf if successful
	; todo: validate output parameters if %ecfHdrs("V",1)
	; ASSUMES:      %ecfOutBuf,%ecfAsync,%ecfDbg,%elogID
	; SIDE EFFECTS: Logs sending of result packet if debugging is enabled
zECFSndRsltPckt()  ;
	q:%ecfAsync 0
	s %ecfOutBuf="L"_%ecfOutBuf
	u $$zECFTCPDev()
	w $$zchrW(0),$$zchrW($l(%ecfOutBuf)),%ecfOutBuf
	u $$zUniqNullDev()
	d:%ecfDbg LgRespLn^EGECFS2(%elogID,"Body",%ecfOutBuf)
	d:%EA("E","EI","Level") logPacket^EGEI("RESP",$l(%ecfOutBuf)+4)
	s %ecfOutBuf=""
	q 1  ;;#eof#
	;
	;---------
	; NAME:         zECFTCPDev (PRIVATE)
	; DESCRIPTION:  Returns the current TCP device for the ECF connection
	; CALLED BY:    EGECFS2 and EALIBECF1 only
	; ASSUMES:      %ecfCurrTCPdev
	; RETURNS:      Current TCP device for the ECF connection
	;---------
zECFTCPDev()
	q %ecfCurrTCPdev  ;;#eof# ;;#inline#
	;---------
	; NAME:         zShrToStr (PRIVATE)
	; DESCRIPTION:  Convert two bytes representing a short to a Cache string
	; PARAMETERS:
	;  short (I,REQ)-Two byte value representing a short
	; RETURNS:      The string representation of the short
zShrToStr(short) q $$zascW(short)  ;;#eof# ;;#inline#
	;---------
	; NAME:         zIntToStr (PRIVATE)
	; DESCRIPTION:  Convert four bytes representing an int to a Cache string
	; PARAMETERS:
	;  int          (I,REQ) - Four byte value representing an int
	; RETURNS:      The string representation of the int
zIntToStr(int) q $$zascL(int)  ;;#eof# ;;#inline#
	;
	;---------
	; NAME:         zStrToShr (PRIVATE)
	; DESCRIPTION:  Convert the string reprentation of an int to four bytes
	; PARAMETERS:
	;  str          (I,REQ)-The string to convert
	;  ltlEndian    (I,OPT:DEFAULT="0")-Whether to force the bytes to little endian
	;                 Used when sending a length before the server has specified its
	;                 endianness to the client
	; RETURNS:      The four byte representation of the integer string
zStrToShr(str,ltlEndian) q $S('ltlEndian:$$zchrW(str),1:$c(str#256,str\256))  ;;#eof# ;;#inline#
	;
	;---------
	; NAME:         zStrToInt (PRIVATE)
	; DESCRIPTION:  Convert the string reprentation of a short to two bytes
	; PARAMETERS:
	;  str          (I,REQ) - The string to convert
	; RETURNS:      The two byte representation of the short string
zStrToInt(str) q $$zchrL(str)  ;;#eof# ;;#inline#
	;
	;---------
	; NAME:         zECFTmout (PRIVATE)
	; DESCRIPTION:  Determines if the current command has exceeded its allowed server time
	; RETURNS:      1 if the command has exceed the limit, otherwise 0
	; ASSUMES:    %ecfSrvTmout
zECFTmout() q $S(($$zPerfTimer()<%ecfSrvTmout):0,1:1)  ;;#eof# ;;#inline#
	;
	;---------
	; NAME:         zECFThrow (PUBLIC)
	; DESCRIPTION:  Throws an exception during command execution and
	;               code execution will return to the client process.
	; PARAMETERS:
	;  zzErrCode    (I,REQ) - The reason code for the exception (ie: INVALID-USER-ID)
	;  zzDetails    (I,OPT,DEFAULT:"") - A readible reason for this exception (ie: No user id supplied)
	;  zzNoLog      (I,OPT,DEFAULT:"") - Set to not log an error in the Error Log
	; RETURNS:      None.
	; ASSUMES:      %ecfFromThrow
	; REVISION HISTORY:
	;  *JAM    02/12 M7016113 - add zzNoLog param to skip logging an error
	;---------
zECFThrow(zzErrCode,zzDetails,zzNoLog) s:(zzErrCode="") $EC=",U-EMPTY-THROW-REASON,"
	n zzCodeLn,zzErrNum
	s zzCodeLn=$STACK(-1)-1,zzCodeLn=$STACK(zzCodeLn,"PLACE")
	i 'zzNoLog d %zErrLog("EGECFS2: <zECFThrow>"_zzCodeLn,"",1,.zzErrNum) s zzErrNum=$p(zzErrNum,",",2)  ;*JAM 02/12 M7016113 respect noLog, fix zzErrNum lookup ;*MAT 6/13 M7020868 - revert behavior to only log error once per day
	d SndError^EGECFS2(zzErrCode,zzDetails,+zzErrNum,zzCodeLn)
	s zzErrCode=$s($e(zzErrCode,1,3)=",U-":"",1:",U-")_zzErrCode_$s($e(zzErrCode,$l(zzErrCode))=",":"",1:",")
	s %ecfFromThrow=1,$EC=zzErrCode ;*MAT 3/14 M7023709 - change variable name to be more indicative of its use
	q 1  ;;#eof#
	;
	q  ;;#eor#
	;Routine accessed by Timothy A Coffman