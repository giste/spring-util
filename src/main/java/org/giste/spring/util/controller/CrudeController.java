package org.giste.spring.util.controller;

import org.giste.spring.util.service.CrudeRestService;
import org.giste.util.dto.NonRemovableDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Base class for CRUDE controllers. It provides the following mappings:
 * <ul>
 * <li>GET on "basePath" to get a view with the list of all entities.</li>
 * <li>GET on "basePath/{id}" to get the view of a single entity.</li>
 * <li>POST on "basePath" to create a new entity.</li>
 * <li>POST on "basePath/{id}" to update a single entity.
 * <li>POST on "basePath/{id}/enable" to enable an entity.</li>
 * <li>POST on "basePath/{id}/disable" to disable an entity.</li>
 * <li>GET on "basePath/new" to get a view for creating an entity.</li>
 * </ul>
 * 
 * Subclasses have to implement the following methods:
 * <ul>
 * <li>{@link #getBasePath()} to provide the base path of the entity to be
 * managed.</li>
 * <li>{@link #getBaseView()} to provide the base view name of the entity.</li>
 * <li>{@link #getNewDto()} to provide a new DTO for the entity.</li>
 * </ul>
 * 
 * @author Giste
 *
 * @param <DTO> DTO of the entity to be managed by this controller.
 */
public abstract class CrudeController<DTO extends NonRemovableDto> extends BaseController<DTO> {

	// Paths
	protected static final String PATH_ENABLE = PATH_ID + "/enable";
	protected static final String PATH_DISABLE = PATH_ID + "/disable";

	/**
	 * Constructs a CrudeController with a given CrudeRestService.
	 * 
	 * @param restService Service used to communicate with the REST server.
	 */
	public CrudeController(CrudeRestService<DTO> restService) {
		super(restService);
	}

	@Override
	protected CrudeRestService<DTO> getRestService() {
		return (CrudeRestService<DTO>) super.getRestService();
	}

	/**
	 * Disables an entity returns the <code>"entityList"</code> view.
	 * 
	 * @param id The identifier of the entity to disable.
	 * @return The view to show.
	 */
	@PostMapping(PATH_DISABLE)
	public String disable(@PathVariable(PROPERTY_ID) long id) {
		getRestService().disable(id);

		return "redirect:" + getBasePath();
	}

	/**
	 * Enables an entity returns the <code>"entityList"</code> view.
	 * 
	 * @param id The identifier of the entity to enable.
	 * @return The view to show.
	 */
	@PostMapping(PATH_ENABLE)
	public String enable(@PathVariable(PROPERTY_ID) long id) {
		getRestService().enable(id);

		return "redirect:" + getBasePath();
	}

}