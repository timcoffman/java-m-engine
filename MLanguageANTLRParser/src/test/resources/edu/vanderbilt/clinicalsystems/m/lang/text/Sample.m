EALIBECF1  ; ; ;2015-01-21 11:44:22;8.2;Y+qQAnjAnf6LXuHjD4U25nmZ4CFwj/OL8yd5zxgbvvnz+unQEfQoG0ThGsxe7XIp
	;;#lglob#EALIB
	; Copyright (C) Epic Systems Corporation 2006-2015
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
