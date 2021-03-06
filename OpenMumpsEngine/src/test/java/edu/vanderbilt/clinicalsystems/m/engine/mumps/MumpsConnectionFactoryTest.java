package edu.vanderbilt.clinicalsystems.m.engine.mumps;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.Connection;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionStringBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.ExpressionList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;

public class MumpsConnectionFactoryTest {

	private MumpsConnectionFactory m_factory ;
	
	private String connectionString( String name ) {
		return
			connectionStringBuilder(name)
			.toString()
			;
	}

	private String connectionString( String name, String initializationCommand ) {
		return
			connectionStringBuilder(name)
			.withOption("init", initializationCommand )
			.toString()
;
	}
	
	private ConnectionStringBuilder connectionStringBuilder(String name) {
		return
			new ConnectionStringBuilder(name)
			.withOption("location", "/Users/timvanderbilt/mumps/mumps")
			.withOption("executable", "/Users/timvanderbilt/mumps/mumps/mumps")
			.withOption("auto-start", "yes")
			.withOption("auto-create", "no")
			;
	}
	
	@Before
	public void configureFactory() {
		m_factory = new MumpsConnectionFactory() ;
	}
	
	@Test
	public void canMakeConnection() throws Exception {
		try ( Connection cxn = m_factory.createConnection( connectionString("local") ) ) {
			
		}
	}
	
	@Test
	public void canMakeConnectionWithInitialization() throws Exception {
		try ( Connection cxn = m_factory.createConnection( connectionString("local","W \"Hello world!\",!") ) ) {
			
		}
	}

	@Test
	public void canRead() throws Exception {
		try ( Connection cxn = m_factory.createConnection( connectionString("local") ) ) {
			
		}
	}

	@Ignore("can't assign requested address")
	@Test
	public void canWrite() throws Exception {
		try ( Connection cxn = m_factory.createConnection( connectionString("local") ) ) {
			Command cmd = new Command( CommandType.WRITE, new ExpressionList( Constant.from("Hello world!") ) ) ;
			
			StringWriter writer = new StringWriter() ; 
			
			cxn.submit( cmd, null, writer );
			
			assertThat( writer.toString(), equalTo( "Hello world!" ) ) ;
		}
	}

}
