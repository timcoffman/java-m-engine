package edu.vanderbilt.clinicalsystems.m.engine.virtual;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;

import edu.vanderbilt.clinicalsystems.m.engine.Connection;
import edu.vanderbilt.clinicalsystems.m.engine.ConnectionStringBuilder;
import edu.vanderbilt.clinicalsystems.m.lang.CommandType;
import edu.vanderbilt.clinicalsystems.m.lang.model.Command;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.FormatCommand;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutput;
import edu.vanderbilt.clinicalsystems.m.lang.model.argument.InputOutputList;
import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Constant;

public class MumpsConnectionFactoryTest {

	private VirtualConnectionFactory m_factory ;
	
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
			.withOption("auto-start", "yes")
			.withOption("auto-create", "no")
			;
	}
	
	@Before
	public void configureFactory() {
		m_factory = new VirtualConnectionFactory() ;
	}
	
	@Test
	public void canMakeConnection() throws Exception {
		try ( Connection cxn = m_factory.createConnection( connectionString("virtual") ) ) {
			assertThat( cxn, notNullValue() ) ;
		}
	}
	
	@Test
	public void canMakeConnectionWithInitialization() throws Exception {
		try ( Connection cxn = m_factory.createConnection( connectionString("virtual","W \"Hello world!\",!") ) ) {
			assertThat( cxn, notNullValue() ) ;
		}
	}

	@Test
	public void canRead() throws Exception {
		try ( Connection cxn = m_factory.createConnection( connectionString("virtual") ) ) {
			
		}
	}

	@Test
	public void canWrite() throws Exception {
		try ( Connection cxn = m_factory.createConnection( connectionString("virtual") ) ) {
			Command cmd = new Command( CommandType.WRITE, new InputOutputList( InputOutput.wrap(Constant.from("Hello world!")), FormatCommand.carriageReturn() ) ) ;
			
			StringWriter writer = new StringWriter() ; 
			
			cxn.submit( cmd, null, writer );
			
			assertThat( writer.toString(), equalTo( "Hello world!\n" ) ) ;
		}
	}

}
