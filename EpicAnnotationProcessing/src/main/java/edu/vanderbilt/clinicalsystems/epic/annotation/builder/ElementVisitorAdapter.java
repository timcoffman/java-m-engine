package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

public abstract class ElementVisitorAdapter<R,P> implements ElementVisitor<R,P> {
	
	protected RuntimeException unsupportedElementTypeException(Element element, P parameter) {
		return new UnsupportedOperationException(element.getClass().getSimpleName() + " not supported here") ;
	}
	
	protected R unsupportedElementTypeResult(Element element, P parameter) {
		throw unsupportedElementTypeException(element,parameter);
	}

	@Override public R visit(Element e) { return visit(e,null) ; }
	
	@Override public R visit(Element e, P p) { return unsupportedElementTypeResult(e,p) ; }
	@Override public R visitPackage(PackageElement e, P p) { return unsupportedElementTypeResult(e,p) ; }
	@Override public R visitType(TypeElement e, P p) { return unsupportedElementTypeResult(e,p) ; }
	@Override public R visitVariable(VariableElement e, P p) { return unsupportedElementTypeResult(e,p) ; }
	@Override public R visitExecutable(ExecutableElement e, P p) { return unsupportedElementTypeResult(e,p) ; }
	@Override public R visitTypeParameter(TypeParameterElement e, P p) { return unsupportedElementTypeResult(e,p) ; }
	@Override public R visitUnknown(Element e, P p) { return unsupportedElementTypeResult(e,p) ; }	
}