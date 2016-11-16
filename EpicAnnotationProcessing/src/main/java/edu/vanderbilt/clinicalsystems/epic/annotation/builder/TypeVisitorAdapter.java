package edu.vanderbilt.clinicalsystems.epic.annotation.builder;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;

public class TypeVisitorAdapter<R,P> implements TypeVisitor<R, P> {

	protected RuntimeException unsupportedTypeMirrorTypeException(TypeMirror typeMirror, P parameter) {
		return new UnsupportedOperationException(typeMirror.getClass().getSimpleName() + " not supported here") ;
	}
	
	protected R unsupportedTypeMirrorTypeResult(TypeMirror typeMirror, P parameter) {
		throw unsupportedTypeMirrorTypeException(typeMirror,parameter);
	}
	
	@Override public R visit(TypeMirror t) { return visit(t,null); }

	@Override public R visit(TypeMirror t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitPrimitive(PrimitiveType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitNull(NullType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitArray(ArrayType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitDeclared(DeclaredType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitError(ErrorType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitTypeVariable(TypeVariable t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitWildcard(WildcardType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitExecutable(ExecutableType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitNoType(NoType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitUnknown(TypeMirror t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitUnion(UnionType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }
	@Override public R visitIntersection(IntersectionType t, P p) { return unsupportedTypeMirrorTypeResult(t,p); }

}
