package org.giste.spring.util.error.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

/**
 * DTO class for returning a REST error from server.
 * 
 * @author Giste
 */
public class RestErrorDto implements Serializable {

	private static final long serialVersionUID = 7026894240730324882L;

	private HttpStatus status;
	private String code;
	private String message;
	private String developerInfo;
	private List<FieldErrorDto> fieldErrorList = new ArrayList<FieldErrorDto>();

	/**
	 * Default constructor with no arguments.
	 */
	public RestErrorDto() {

	}

	/**
	 * Constructs a new RestErrorDto.
	 * 
	 * @param HTTP status of the response.
	 * @param code Error code.
	 * @param message Error message for the user.
	 * @param developerInfo More technical error message for developer.
	 */
	public RestErrorDto(HttpStatus status, String code, String description, String developerInfo) {
		this.status = status;
		this.code = code;
		this.message = description;
		this.developerInfo = developerInfo;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getDeveloperInfo() {
		return developerInfo;
	}

	public List<FieldErrorDto> getFieldErrorList() {
		return fieldErrorList;
	}

	public void setFieldErrorList(List<FieldErrorDto> fieldErrorList) {
		this.fieldErrorList = fieldErrorList;
	}

	/**
	 * Adds a field error to the error list.
	 * 
	 * @param fieldError {@link FieldErrorDto} representing a field error.
	 */
	public void addFieldError(FieldErrorDto fieldError) {
		fieldErrorList.add(fieldError);
	}
}
