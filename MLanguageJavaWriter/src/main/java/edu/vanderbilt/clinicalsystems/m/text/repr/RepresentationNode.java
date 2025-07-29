package edu.vanderbilt.clinicalsystems.m.text.repr;

import edu.vanderbilt.clinicalsystems.m.lang.text.Representation;

public interface RepresentationNode {
	
	RepresentationInference getSystem() ;
	
	default void usedAs( Representation representation) { getSystem().isUsedAs(this, representation) ; }
	
	default OperationNode isComparedWith( RepresentationNode rhs ) { return getSystem().isComparedWith(this, rhs) ; }
	default OperationNode or(RepresentationNode alternativeNode ) { return getSystem().areAlternatives(this, alternativeNode) ; }
	
	default Representation representation() { return getSystem().representationFor(this) ; }
	default OperationNode combinesWith(RepresentationNode representationNode ) { return getSystem().combines(this, representationNode) ; }
	default OperationNode transformedInto(Representation producingRepresentation) { return getSystem().transforms(this, producingRepresentation) ; }


}
