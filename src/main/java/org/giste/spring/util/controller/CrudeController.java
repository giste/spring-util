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

public abstract class CrudeController<DTO extends NonRemovableDto> {

	// Service used for communicating with REST server.
	private CrudeRestService<DTO> restService;

	private String viewList;
	private String viewEntity;
	private String viewBase;

	private String path;

	public CrudeController(CrudeRestService<DTO> restService) {
		this.restService = restService;
		this.viewBase = getBaseView();
		viewList = viewBase + "List";
		viewEntity = viewBase;
		path = getBasePath();
	}

	@GetMapping
	public String findAll(Model model) {

		List<DTO> dtoList = restService.findAll();
		model.addAttribute("entityList", dtoList);

		return viewList;
	}

	@GetMapping("/{id}")
	public String findById(@PathVariable("id") long id, Model model) {
		DTO dto = restService.findById(id);
		model.addAttribute("entity", dto);

		return viewEntity;
	}

	@PostMapping
	public String create(@Valid @ModelAttribute("entity") final DTO dto, BindingResult result) {
		if (result.hasErrors()) {
			return viewEntity;
		}

		restService.create(dto);

		return "redirect:/" + path;
	}

	@PostMapping("/{id}")
	public String update(@PathVariable("id") long id, @Valid @ModelAttribute("entity") final DTO dto,
			BindingResult result) {
		if (result.hasErrors()) {
			return viewEntity;
		}

		restService.update(dto);

		return "redirect:/" + path;
	}

	@PostMapping("/{id}/disable")
	public String disable(@PathVariable("id") long id) {
		restService.disable(id);

		return "redirect:/" + path;
	}

	@PostMapping("/{id}/enable")
	public String enable(@PathVariable("id") long id) {
		restService.enable(id);

		return "redirect:/" + path;
	}

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