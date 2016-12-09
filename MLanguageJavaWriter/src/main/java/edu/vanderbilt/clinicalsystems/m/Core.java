package edu.vanderbilt.clinicalsystems.m;

import edu.vanderbilt.clinicalsystems.m.core.Value;
import edu.vanderbilt.clinicalsystems.m.core.lib.Builtin;
import edu.vanderbilt.clinicalsystems.m.core.lib.Math;
import edu.vanderbilt.clinicalsystems.m.core.lib.ReadWrite;
import edu.vanderbilt.clinicalsystems.m.core.lib.Reflect;
import edu.vanderbilt.clinicalsystems.m.core.lib.Text;
import edu.vanderbilt.clinicalsystems.m.lang.text.RoutineJavaBuilderEnvironment;

public class Core {

	public static RoutineJavaBuilderEnvironment useLibrariesIn( RoutineJavaBuilderEnvironment env ) {
		return env
			.use( Value.class )
			.use( Builtin.class )
			.use( Math.class )
			.use( Text.class )
			.use( ReadWrite.class )
			.use( Reflect.class )
			;
	}
}
