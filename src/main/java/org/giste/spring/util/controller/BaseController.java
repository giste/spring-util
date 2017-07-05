package org.giste.spring.util.controller;

import java.util.List;

import javax.validation.Valid;

import org.giste.spring.util.service.BaseRestService;
import org.giste.util.dto.BaseDto;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Base class for controllers. It provides the following mappings:
 * <ul>
 * <li>GET on "basePath" to get a view with the list of all entities.</li>
 * <li>GET on "basePath/{id}" to get the view of a single entity.</li>
 * <li>POST on "basePath" to create a new entity.</li>
 * <li>POST on "basePath/{id}" to update a single entity.
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
public abstract class BaseController<DTO extends BaseDto> {

	// Model objects.
	protected static final String ENTITY = "entity";
	protected static final String ENTITY_LIST = "entityList";

	// Properties.
	protected static final String PROPERTY_ID = "id";

	// Paths
	protected static final String PATH_ID = "/{id}";
	protected static final String PATH_NEW = "/new";

	// Suffix for list view.
	protected static final String VIEW_LIST = "List";

	// View names.
	private String viewList;
	private String viewEntity;
	private String viewBase;
	
	// Rest service.
	private BaseRestService<DTO> restService;

	/**
	 * Constructs a BaseController with a given BaseRestService.
	 * 
	 * @param restService Service used to communicate with the REST server.
	 */
	public BaseController(BaseRestService<DTO> restService) {
		this.restService = restService;
		this.viewBase = getBaseView();
		viewList = viewBase + VIEW_LIST;
		viewEntity = viewBase;
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

	/**
	 * Gets the service used to communicate with REST server.
	 * 
	 * @return The service used to communicate with REST server.
	 */
	protected BaseRestService<DTO> getRestService() {
		return restService;
	}

	/**
	 * Gets the name of the view for showing the list of entities.
	 * 
	 * @return The name of the view for showing the list of entities.
	 */
	protected String getViewList() {
		return viewList;
	}

	/**
	 * Gets the name of the view for showing one single entity.
	 * 
	 * @return The name of the view for showing one single entity.
	 */
	protected String getViewEntity() {
		return viewEntity;
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

		List<DTO> dtoList = getRestService().findAll();
		model.addAttribute(ENTITY_LIST, dtoList);

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
	@GetMapping(PATH_ID)
	public String findById(@PathVariable(PROPERTY_ID) long id, Model model) {
		DTO dto = getRestService().findById(id);
		model.addAttribute(ENTITY, dto);

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
	public String create(@Valid @ModelAttribute(ENTITY) final DTO dto, BindingResult result) {
		if (result.hasErrors()) {
			return viewEntity;
		}

		getRestService().create(dto);

		return "redirect:" + getBasePath();
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
	@PostMapping(PATH_ID)
	public String update(@PathVariable(PROPERTY_ID) long id, @Valid @ModelAttribute(ENTITY) final DTO dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return viewEntity;
		}

		getRestService().update(dto);

		return "redirect:" + getBasePath();
	}

	/**
	 * Adds an empty DTO to the model as "entity" object and returns the
	 * <code>"entity"</code> view.
	 * 
	 * @param model The model where the empty entity is stored.
	 * @return The view to show.
	 */
	@GetMapping(PATH_NEW)
	public String getNew(Model model) {
		DTO dto = getNewDto();
		model.addAttribute(ENTITY, dto);

		return viewEntity;
	}

}