package it.cnr.si.missioni.awesome.exception;

import it.cnr.si.missioni.util.CodiciErrore;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AwesomeException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6063577040643273974L;

	private final int code;

	public AwesomeException(int code) {
		this.code = code;
	}

	public AwesomeException(String message) {
		super(message);
		this.code = CodiciErrore.ERRGEN;
	}

	public AwesomeException(int code, String message) {
		super(message);
		this.code = code;
	}

	public AwesomeException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String  getMessage() {
		return super.getMessage()!=null?super.getMessage():(CodiciErrore.text[code] + " [" + code + "]");
	}

	public ResponseEntity<?> getResponse() {
		
		ResponseEntity<?> response = null;		
		if (this.getCode()==CodiciErrore.OK)
			response = new ResponseEntity<>(HttpStatus.CREATED);
		else
			response = new ResponseEntity<String>(this.getMessage(), HttpStatus.BAD_REQUEST);
		return response;
	}
}
