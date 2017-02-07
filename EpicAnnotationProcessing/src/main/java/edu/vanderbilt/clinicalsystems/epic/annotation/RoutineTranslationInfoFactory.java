package edu.vanderbilt.clinicalsystems.epic.annotation;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.RoutineDependency;
import edu.vanderbilt.clinicalsystems.epic.annotation.builder.RoutineTools.TaggedRoutineDependency;
import edu.vanderbilt.clinicalsystems.m.lang.model.Routine;

public class RoutineTranslationInfoFactory {

	public RoutineTranslationInfoFactory() {
		
	}

	public RoutineTranslationInfo create(Routine routine, Collection<RoutineDependency> dependencies) {
		return new RoutineTranslationInfo(routine, dependencies);
	}

	public String resourceNameFor(RoutineTranslationInfo translation) {
		return resourceNameFor( translation.routineName() ) ;
	}
	
	public String resourceNameFor(String routineName) {
		return routineName + ".xml" ;
	}
	
	public RoutineTranslationInfo read(Class<?> type, String forRoutineName) {
		URL testClassMetaLocation = type.getResource( resourceNameFor(forRoutineName) ) ;
		if ( null == testClassMetaLocation )
			throw new RuntimeException( "missing resource \"" + resourceNameFor(forRoutineName) + "\" for " + type.getName() ) ;
		
		try ( InputStream testClassMetaStream = testClassMetaLocation.openStream() ) {

			javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance() ;
			javax.xml.parsers.DocumentBuilder documentBuilder = factory.newDocumentBuilder() ;
			org.w3c.dom.Document doc = documentBuilder.parse( testClassMetaStream ) ;
			
			javax.xml.xpath.XPath xpath = javax.xml.xpath.XPathFactory.newInstance().newXPath() ;
			org.w3c.dom.NodeList nodes ;
			
			nodes = (org.w3c.dom.NodeList)xpath.evaluate("/routine", doc, javax.xml.xpath.XPathConstants.NODESET) ;
			String routineName = ((org.w3c.dom.Element)nodes.item(0)).getAttribute("name") ;
			
			Set<String> tagNames = new HashSet<String>();
			nodes = (org.w3c.dom.NodeList)xpath.evaluate("/routine/tag", doc, javax.xml.xpath.XPathConstants.NODESET) ;
			for ( int i = 0 ; i < nodes.getLength() ; ++i ) {
				org.w3c.dom.Element tagElement = (org.w3c.dom.Element)nodes.item(i) ;
				String tagName = tagElement.getAttribute("name") ;
				tagNames.add( tagName ) ;
			}
			
			Set<RoutineDependency> dependencies = new HashSet<RoutineTools.RoutineDependency>();
			nodes = (org.w3c.dom.NodeList)xpath.evaluate("/routine/dependency", doc, javax.xml.xpath.XPathConstants.NODESET) ;
			for ( int i = 0 ; i < nodes.getLength() ; ++i ) {
				org.w3c.dom.Element dependencyElement = (org.w3c.dom.Element)nodes.item(i) ;
				String dependsOnRoutineName = dependencyElement.getAttribute("routine") ;
				String dependsOnClassName = dependencyElement.getAttribute("class") ;
				String dependsOnMethodName = dependencyElement.getAttribute("method") ;
				String dependsOnTagName = dependencyElement.getAttribute("tag") ;
				
				TaggedRoutineDependency routineDependency = new TaggedRoutineDependency() {
					@Override public String dependsOnRoutineName() { return dependsOnRoutineName ; }
					@Override public String dependsOnTypeName() { return dependsOnClassName ; }
					@Override public TypeElement dependsOnType() { throw new UnsupportedOperationException() ; }
					@Override public String dependsOnTagName() { return dependsOnTagName ; }
					@Override public String dependsOnMethodName() { return dependsOnMethodName ; }
					@Override public ExecutableElement dependsOnMethod() { throw new UnsupportedOperationException() ; }
				};
				dependencies.add( routineDependency ) ;
			}
			
			return new RoutineTranslationInfo( null, dependencies ) {
				@Override public Routine routine() { throw new UnsupportedOperationException() ; }
				@Override public String routineName() { return routineName ; }
				@Override public Collection<String> routineTagNames() { return Collections.unmodifiableCollection(tagNames) ; }
			};
				
		} catch (ParserConfigurationException | SAXException | XPathExpressionException ex) {
			throw new RuntimeException( ex );
		} catch (IOException ex) {
			throw new RuntimeException( ex );
		}
	}
	
	public void write( RoutineTranslationInfo translation, Writer resourceWriter ) throws IOException {
		try {
			javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance() ;
			javax.xml.parsers.DocumentBuilder documentBuilder = factory.newDocumentBuilder() ;
			org.w3c.dom.Document doc = documentBuilder.newDocument() ;
			org.w3c.dom.Element routineElement = doc.createElement("routine") ;
			doc.appendChild(routineElement) ;
			routineElement.setAttribute("name", translation.routineName() );
			
			for ( RoutineDependency dependendency : translation.dependencies() ) {
				org.w3c.dom.Element dependencyElement = doc.createElement("dependency") ;
				if ( null != dependendency.dependsOnRoutineName() )
					dependencyElement.setAttribute("routine", dependendency.dependsOnRoutineName() );
				if ( null != dependendency.dependsOnTypeName() )
					dependencyElement.setAttribute("class", dependendency.dependsOnTypeName() );
				if ( dependendency instanceof TaggedRoutineDependency ) {
					TaggedRoutineDependency tagDep = (TaggedRoutineDependency)dependendency ;
					if ( null != tagDep.dependsOnTagName() )
						dependencyElement.setAttribute("tag", tagDep.dependsOnTagName() );
					if ( null != tagDep.dependsOnMethodName() )
						dependencyElement.setAttribute("method", tagDep.dependsOnMethodName() );
				}
				routineElement.appendChild(dependencyElement) ;
			}
			
			for ( String tagName : translation.routineTagNames() ) {
				org.w3c.dom.Element tagElement = doc.createElement("tag") ;
				tagElement.setAttribute("name", tagName );
				routineElement.appendChild(tagElement) ;
			}
			
			javax.xml.transform.TransformerFactory transformerFactory = javax.xml.transform.TransformerFactory.newInstance();
			javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			try ( Writer bufferedResourceWriter = new BufferedWriter(resourceWriter) ) {
				transformer.transform(
						new javax.xml.transform.dom.DOMSource(doc),
						new javax.xml.transform.stream.StreamResult(bufferedResourceWriter)
					);
			}
		} catch (ParserConfigurationException | TransformerException ex) {
			ex.printStackTrace();
		}	}

}
