package edu.vanderbilt.clinicalsystems.m.lang;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

public class BuiltinSymbolSupport {

	public static boolean matchesSymbol( BuiltinSymbol builtinSymbol, String symbolOrAbbreviation ) {
		return builtinSymbol.canonicalSymbol().equalsIgnoreCase( symbolOrAbbreviation )
				|| builtinSymbol.canonicalAbbreviation().equalsIgnoreCase( symbolOrAbbreviation )
				;
	}

	public static <T extends Enum<T> & BuiltinSymbol> T valueOfSymbol(Class<T> enumClass, String symbolOrAbbreviation, Compatibility ... additionalCompatibilities) {
		EnumSet<Compatibility> compatibility = EnumSet.of(Compatibility.ANSI_1995_X11_1);
		compatibility.addAll( Arrays.asList(additionalCompatibilities) ) ;
		
		Optional<T> matchingSymbol =
			Arrays.stream(enumClass.getEnumConstants())
			.filter( bs->compatibility.containsAll( bs.compatibility() ) )
			.filter( bs->bs.canonicalSymbol().equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		if ( matchingSymbol.isPresent() )
			return matchingSymbol.get() ;
		
		Optional<T> matchingAbbreviation =
			Arrays.stream(enumClass.getEnumConstants())
			.filter( bs->compatibility.containsAll( bs.compatibility() ) )
			.filter( bs->bs.canonicalAbbreviation().equalsIgnoreCase(symbolOrAbbreviation) )
			.findFirst()
			;
		
		return matchingAbbreviation
				.orElseThrow( ()->new IllegalArgumentException("\"" + symbolOrAbbreviation + "\" not recognized as a " + enumClass.getSimpleName() ) )
				;
	}
}
