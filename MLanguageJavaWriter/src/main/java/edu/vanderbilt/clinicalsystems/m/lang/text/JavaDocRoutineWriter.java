package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.Writer;

import com.sun.codemodel.JDocComment;

class JavaDocRoutineWriter extends Writer {
	private final JDocComment javadoc;
	private final StringBuffer m_lineBuffer = new StringBuffer();

	public JavaDocRoutineWriter(JDocComment javadoc) {
		this.javadoc = javadoc;
	}

	@Override public void write(char[] cbuf, int off, int len) throws IOException {
		for ( int i = 0; i < len ; ++i ) {
			char c = cbuf[off+i] ;
			m_lineBuffer.append(c) ;
			if ( c == '\n' )
				flush() ;
		}
	}

	@Override public void flush() throws IOException {
		if ( m_lineBuffer.length() != 0 ) {
			javadoc.append( m_lineBuffer.toString() ) ;
			m_lineBuffer.setLength(0);
		}
	}

	@Override public void close() throws IOException { flush() ; }
}