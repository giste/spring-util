package org.giste.spring.util.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an entity is not found when looked up by its identifier.
 * 
 * @author Giste
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -5139937197269190138L;

	/**
	 * Creates a new exception with a message.
	 * 
	 * @param message message for this exception.
	 */
	public EntityNotFoundException(String message) {
		super(message);
	}

}
