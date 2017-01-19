package edu.vanderbilt.clinicalsystems.epic.lib;

import java.lang.reflect.Method;

import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineTag;
import edu.vanderbilt.clinicalsystems.m.core.annotation.RoutineUnit;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderEnvironment;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderEnvironment.Resolver;

@RoutineUnit
public class Epic {

	private static class EpicLibraryResolver implements Resolver {

		@Override public String namedLibrary(Class<?> environmentClass) {
			RoutineUnit libraryAnnotation = environmentClass.getAnnotation( RoutineUnit.class ) ;
			if ( null == libraryAnnotation )
				return null ;
			String name = RoutineUnit.DEFAULT_NAME.equals(libraryAnnotation.value()) ? environmentClass.getSimpleName() : libraryAnnotation.value() ;
			return name ;
		}
		
		@Override public String namedMethod(Method method) {
			RoutineTag methodAnnotation = method.getAnnotation( RoutineTag.class ) ;
			if ( null == methodAnnotation )
				return null ;
			String name = RoutineUnit.DEFAULT_NAME.equals(methodAnnotation.value()) ? method.getName() : methodAnnotation.value() ;
			return name ;
		}
	}
	
	public static RoutineJavaBuilderEnvironment useLibrariesIn( RoutineJavaBuilderEnvironment env ) {
		return env
			.use( new EpicLibraryResolver() )
			.use( AiLib.class )
			.use( AicmpLib.class )
			.use( AiemfLib.class )
			.use( BdLib.class )
			.use( CipgLib.class )
			.use( ClfnLib.class )
			.use( ClgLib.class )
			.use( ClgyLib.class )
			.use( CscLib.class )
			.use( CsiLib.class )
			.use( CsuLib.class )
			.use( EaLib.class )
			.use( EaLibv.class )
			.use( Epic.class )
			.use( EsecLib.class )
			.use( EupLib.class )
			.use( FpuLib.class )
			.use( GdiuLib.class )
			.use( GuLib.class )
			.use( HbuLib.class )
			.use( HppLib.class )
			.use( HtLib.class )
			.use( HuLib.class )
			.use( HuwLib.class )
			.use( IuLib.class )
			.use( JevtLib.class )
			.use( JuLib.class )
			.use( KeuLib.class )
			.use( KguLib.class )
			.use( KhuLib.class )
			.use( KnuLib.class )
			.use( KouLib.class )
			.use( KpuLib.class )
			.use( LonLib.class )
			.use( LuLib.class )
			.use( NuLib.class )
			.use( ObuLib.class )
			.use( OrLib.class )
			.use( PauLib.class )
			.use( PcLib.class )
			.use( PguLib.class )
			.use( PruLib.class )
			.use( RisddataLib.class )
			.use( RxxmlLib.class )
			.use( S2Lib1.class )
			.use( Slis1.class )
			.use( WhLib.class )
			.use( WpLib.class )
			.use( XRISDLIBX.class )
			.use( XRXLIBX.class )
			.use( ZefnLib.class )
			;
	}
	
}
