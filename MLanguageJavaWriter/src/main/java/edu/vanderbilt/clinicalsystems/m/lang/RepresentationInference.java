package edu.vanderbilt.clinicalsystems.m.lang;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.command.CommandFactory;

import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

public class RepresentationInference {

	private StatelessKieSession m_kieSession;

	public RepresentationInference() {
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieContainer = kieServices.getKieClasspathContainer();
		
		m_kieSession = kieContainer.newStatelessKieSession();
	}

	public void assign(String string, Representation b) {
		VariableAssignment variableDeclaration = new VariableAssignment( "x", Representation.BOOLEAN );
		m_kieSession.execute( variableDeclaration );
	}

	public Representation representationOfVariable(String string) {
		Command<QueryResults> query = CommandFactory.<QueryResults>newQuery("variableMustRepresentAtLeast", "variable must represent at least", new Object[] { "x" }) ;
		QueryResults queryResults = m_kieSession.execute( query ) ;
		VariableMustRepresentAtLeast variableMustRepresentAtLeast = (VariableMustRepresentAtLeast)queryResults.iterator().next().get("variableMustRepresentAtLeast") ; 
		return variableMustRepresentAtLeast.getRepresentation() ;
	}
	
}
