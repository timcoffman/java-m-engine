package edu.vanderbilt.clinicalsystems.m.engine;

public enum ErrorCode {

	TAG_TOO_DEEP("M14","tag {tag}^{routine} level greater than 1"),
	MISSING_ROUTINE("MISSING_ROUTINE","routine {routine} not found"),
	MISSING_TAG("MISSING_TAG","tag {tag} in routine {routine} not found");
	
	private final String m_code;
	private final String m_messageFormat;

	ErrorCode(String code, String messageFormat) {
		m_code = code ;
		m_messageFormat = messageFormat ;
	}
	
	public String code() { return m_code ; }
	public String messageFormat() { return m_messageFormat ; }
}