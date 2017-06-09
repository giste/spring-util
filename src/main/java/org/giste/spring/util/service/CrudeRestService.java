package org.giste.spring.util.service;

import java.util.List;

import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.NonRemovableDto;

/**
 * Base interface for services that communicate with REST servers and performs
 * CRUDE operations on the entity managed by them. It has the following methods:
 * <ul>
 * <li>{@link #findAll()} to get a list of all entities.</li>
 * <li>{@link #findById(long)} to get a single entity.</li>
 * <li>{@link #create(NonRemovableDto)} to create a new entity.</li>
 * <li>{@link #update(NonRemovableDto)} to update a single entity.</li>
 * <li>{@link #enable(long)} to enable a single entity.</li>
 * <li>{@link #disable(long)} to disable a single entity.</li>
 * </ul>
 * 
 * @author Giste
 *
 * @param <DTO> The DTO of the managed entity.
 */
public interface CrudeRestService<DTO extends NonRemovableDto> {

	/**
	 * Retrieves all items.
	 * 
	 * @return List populated with existing items in the application.
	 */
	List<DTO> findAll();

	/**
	 * Retrieves one item by its identifier.
	 * 
	 * @param id Identifier for the looked up item.
	 * @return DTO with the retrieved club.
	 * @throws EntityNotFoundException If the entity to find does not exist.
	 */
	DTO findById(long id) throws EntityNotFoundException;

	/**
	 * Creates a new item in the application.
	 * 
	 * @param dto DTO with the data for the new item.
	 * @return DTO of the created item.
	 */
	DTO create(DTO dto);

	/**
	 * Updates the data of an existing club.
	 * 
	 * @param dto DTO with the data of the club to update.
	 * @return DTO of the updated entity.
	 * @throws EntityNotFoundException If the entity to update does not exist.
	 */
	DTO update(DTO dto) throws EntityNotFoundException;

	/**
	 * Disables a club in the application. It's data won't be accessible.
	 * 
	 * @param id Identifier of the club to disable.
	 * @return DTO of the disabled entity.
	 * @throws EntityNotFoundException If the entity to disable does not exist.
	 */
	DTO disable(long id) throws EntityNotFoundException;

	/**
	 * Enables a club in the application. It's data will be accessible.
	 * 
	 * @param id Identifier of the club to enable.
	 * @return DTO of the enabled entity.
	 * @throws EntityNotFoundException If the entity to enable does not exist.
	 */
	DTO enable(long id) throws EntityNotFoundException;

}