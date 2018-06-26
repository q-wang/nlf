/**
 * 
 */
package net.lc4ever.framework.exception;

/**
 * @author q-wang
 */
public class AuthenticationException extends NoneStackTraceException {

	protected boolean accountLocked;

	protected boolean accountDisabled;

	protected boolean passwordExpired;

	protected boolean badCredentials;

	protected boolean accountNotExits;

	protected String principal;

	protected int code;
	
	public AuthenticationException(String principal, String message) {
		super(message);
		this.principal = principal;
	}
	
	public AuthenticationException(String principal, int code) {
		this.principal = principal;
		this.code = code;
	}
	
	public AuthenticationException(String principal, int code, String message) {
		super(message);
		this.principal = principal;
		this.code = code;
	}

}
