package org.giste.spring.util.controller;

import org.giste.spring.util.service.BaseRestService;
import org.giste.spring.util.service.CrudRestService;
import org.giste.util.dto.BaseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public abstract class CrudController<DTO extends BaseDto> extends BaseController<DTO> {

	protected static final String PATH_DELETE = PATH_ID + "/delete";
	
	public CrudController(BaseRestService<DTO> restService) {
		super(restService);
	}

	/**
	 * Deletes an entity and returns the <code>"entityList"</code> view.
	 * 
	 * @param id The identifier of the entity to delete.
	 * @return The view to show.
	 */
	@PostMapping(PATH_DELETE)
	public String delete(@PathVariable(PROPERTY_ID) long id) {
		getRestService().delete(id);

		return "redirect:" + getBasePath();
	}

	@Override
	protected CrudRestService<DTO> getRestService() {
		return (CrudRestService<DTO>) super.getRestService();
	}
}
