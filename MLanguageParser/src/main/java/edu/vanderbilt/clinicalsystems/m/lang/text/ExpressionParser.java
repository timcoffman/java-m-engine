package edu.vanderbilt.clinicalsystems.m.lang.text;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import edu.vanderbilt.clinicalsystems.m.lang.model.expression.Expression;

public interface ExpressionParser {

	Expression parseExpression(InputStream source) throws IOException;
	Expression parseExpression(Reader reader) throws IOException;

	Expression parseExpression(String text) ;

}
