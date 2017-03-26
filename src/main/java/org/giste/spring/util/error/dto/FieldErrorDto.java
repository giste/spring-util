package org.giste.spring.util.error.dto;

import java.io.Serializable;

/**
 * Represents a validation error for a field in a form.
 * 
 * @author Giste
 */
public class FieldErrorDto implements Serializable {

	private static final long serialVersionUID = 1969087151427173245L;

	private String field;
	private String message;

	/**
	 * Default constructor with no arguments.
	 */
	public FieldErrorDto() {
		
	}
	
	/**
	 * Constructs a new validation error for a field.
	 * 
	 * @param field Field with validation error.
	 * @param message Description of validation error.
	 */
	public FieldErrorDto(String field, String message) {
		this.field = field;
		this.message = message;
	}

	public String getField() {
		return field;
	}

	public String getMessage() {
		return message;
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
