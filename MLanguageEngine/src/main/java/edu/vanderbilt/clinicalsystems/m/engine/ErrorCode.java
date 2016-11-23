package edu.vanderbilt.clinicalsystems.m.engine;

public enum ErrorCode {

	TAG_TOO_DEEP("M14","tag {tag}^{routine} level greater than 1"),
	MISSING_ROUTINE("MISSING_ROUTINE","routine {routine} not found"),
	MISSING_TAG("MISSING_TAG","tag {tag} in routine {routine} not found"),
	CANNOT_PARSE("CANNOT_PARSE","failed to parse code \"{code}\""),
	SYNTAX_ERROR("SYNTAX_ERROR", "syntax error at \"{code}\""),
	INPUT_ERROR("INPUT_ERROR", "failed to receive input"),
	OUTPUT_ERROR("OUTPUT_ERROR", "failed to send output \"{text}\""),
	NOT_A_NUMBER("NAN","\"{text}\" does not represent a number"),
	UNSUPPORTED_FEATURE("UNSUPPORTED_FEATURE","{feature} \"{code}\" not supported at this time"),
	INFINITE_LOOP("INFINITE_LOOP","loop block never exits");
	
	private final String m_code;
	private final String m_messageFormat;

	ErrorCode(String code, String messageFormat) {
		m_code = code ;
		m_messageFormat = messageFormat ;
	}
	
	public String code() { return m_code ; }
	public String messageFormat() { return m_messageFormat ; }
}
