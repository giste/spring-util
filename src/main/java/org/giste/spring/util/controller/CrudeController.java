package org.giste.spring.util.controller;

import java.util.List;

import javax.validation.Valid;

import org.giste.spring.util.service.CrudeRestService;
import org.giste.util.dto.NonRemovableDto;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Base class for CRUDE controllers.
 * 
 * @author Giste
 *
 * @param <DTO> DTO of the entity managed by this controller.
 */
public abstract class CrudeController<DTO extends NonRemovableDto> {

	// Service used for communicating with REST server.
	private CrudeRestService<DTO> restService;

	private String viewList;
	private String viewEntity;
	private String viewBase;

	private String path;

	/**
	 * Constructs a CrudeController with a given CrudeRestService.
	 * 
	 * @param restService The {@link CrudeRestService} used to communicate with
	 *            REST server.
	 */
	public CrudeController(CrudeRestService<DTO> restService) {
		this.restService = restService;
		this.viewBase = getBaseView();
		viewList = viewBase + "List";
		viewEntity = viewBase;
		path = getBasePath();
	}

	/**
	 * Gets all the entities from REST server, put them into
	 * <code>"entityList"</code> model object, and returns
	 * <code>"entityList"</code> view.
	 * 
	 * @param model The model where the entity list is stored.
	 * @return The view to show.
	 */
	@GetMapping
	public String findAll(Model model) {

		List<DTO> dtoList = restService.findAll();
		model.addAttribute("entityList", dtoList);

		return viewList;
	}

	/**
	 * Gets one entity by it identifier, put it into <code>"entity"</code> model
	 * object and return the <code>"entity"</code> view.
	 * 
	 * @param id The identifier of the looked up entity.
	 * @param model The model where the found entity is stored.
	 * @return The view to show.
	 */
	@GetMapping("/{id}")
	public String findById(@PathVariable("id") long id, Model model) {
		DTO dto = restService.findById(id);
		model.addAttribute("entity", dto);

		return viewEntity;
	}

	/**
	 * Creates a new entity form its DTO and returns the
	 * <code>"entityList"</code> view. The DTO has to be present in the model as
	 * <code>"entity"</code> object.
	 * 
	 * @param dto The DTO with the data of the entity to be created.
	 * @param result Binding result.
	 * @return The view to show.
	 */
	@PostMapping
	public String create(@Valid @ModelAttribute("entity") final DTO dto, BindingResult result) {
		if (result.hasErrors()) {
			return viewEntity;
		}

		restService.create(dto);

		return "redirect:/" + path;
	}

	/**
	 * Updates an entity with data from DTO and returns the
	 * <code>"entityList"</code> view. The DTO has to be present in the model as
	 * <code>"entity"</code> object.
	 * 
	 * @param id The identifier of the entity to update.
	 * @param dto The DTO with the data of the entity to update.
	 * @param result The binding result for this request.
	 * @return The view to show.
	 */
	@PostMapping("/{id}")
	public String update(@PathVariable("id") long id, @Valid @ModelAttribute("entity") final DTO dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return viewEntity;
		}

		restService.update(dto);

		return "redirect:/" + path;
	}

	/**
	 * Disables an entity returns the <code>"entityList"</code> view.
	 * 
	 * @param id The identifier of the entity to disable.
	 * @return The view to show.
	 */
	@PostMapping("/{id}/disable")
	public String disable(@PathVariable("id") long id) {
		restService.disable(id);

		return "redirect:/" + path;
	}

	/**
	 * Enables an entity returns the <code>"entityList"</code> view.
	 * 
	 * @param id The identifier of the entity to enable.
	 * @return The view to show.
	 */
	@PostMapping("/{id}/enable")
	public String enable(@PathVariable("id") long id) {
		restService.enable(id);

		return "redirect:/" + path;
	}

	/**
	 * Adds an empty DTO to the model as "entity" object and returns the
	 * <code>"entity"</code> view.
	 * 
	 * @param model The model where the empty entity is stored.
	 * @return The view to show.
	 */
	@GetMapping("/new")
	public String getNew(Model model) {
		DTO dto = getNewDto();
		model.addAttribute("entity", dto);

		return viewEntity;
	}

	/**
	 * Gets a new DTO. Subclasses should return a new DTO constructed from its
	 * default constructor.
	 * 
	 * @return A new DTO.
	 */
	protected abstract DTO getNewDto();

	/**
	 * Gets the base name for the views to be returned for the entity managed by
	 * this controller. It's used for single entity view. <code>viewList</code>
	 * is used for the entity list view.
	 * 
	 * @return The name of the base view.
	 */
	protected abstract String getBaseView();

	/**
	 * Gets the path for the actions. It's used to construct all the paths for
	 * the managed entity.
	 * 
	 * @return The base path for the managed entity.
	 */
	protected abstract String getBasePath();
}