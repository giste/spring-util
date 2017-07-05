package org.giste.spring.util.service;

import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.BaseDto;

public interface CrudRestService<DTO extends BaseDto> extends BaseRestService<DTO> {

	/**
	 * Deletes the entity with the identifier passed as parameter.
	 * 
	 * @param id The identifier of the entity to delete.
	 * @throws EntityNotFoundException If the entity doesn't exist.
	 */
	void delete(Long id) throws EntityNotFoundException;
}
