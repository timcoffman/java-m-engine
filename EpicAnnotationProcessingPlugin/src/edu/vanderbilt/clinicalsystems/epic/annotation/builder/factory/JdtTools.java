package edu.vanderbilt.clinicalsystems.epic.annotation.builder.factory;

import javax.lang.model.element.TypeElement;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JdtTools {

	public static CompilationUnit createCompilationUnit( TypeElement annotatedType ) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot() ;
		for ( IProject project : root.getProjects() ) {
			try {
				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
					IJavaProject javaProject = JavaCore.create(project);
					CompilationUnit compilationUnit = createCompilationUnit( javaProject, annotatedType ) ;
					if ( null != compilationUnit )
						return compilationUnit ;
				}
			} catch ( CoreException ex ) {
				throw new RuntimeException("failed to find the compilation unit in " + project,ex);
			}
		}
		throw new RuntimeException("failed to find the compilation unit in " + root );
	}

	private static CompilationUnit createCompilationUnit( IJavaProject javaProject, TypeElement annotatedType ) {
		try {
			for (IPackageFragment packageFragment : javaProject.getPackageFragments()) {
				for (ICompilationUnit compilationUnit : packageFragment.getCompilationUnits()) {
					IType type = compilationUnit.findPrimaryType() ; //.getType( annotatedType.getQualifiedName().toString() );
					if ( null != type && annotatedType.getQualifiedName().toString().equals( type.getFullyQualifiedName() ) /* null != type */ ) {
						ASTParser astParser = ASTParser.newParser(AST.JLS8) ;
						astParser.setKind(ASTParser.K_COMPILATION_UNIT);
						astParser.setSource(compilationUnit);
						astParser.setResolveBindings(true);
						return (CompilationUnit)astParser.createAST(null);
					}
				}
			}
		} catch ( JavaModelException ex ) {
			throw new RuntimeException("failed to find the compilation unit in " + javaProject,ex);
		}
		return null;
	}
}

