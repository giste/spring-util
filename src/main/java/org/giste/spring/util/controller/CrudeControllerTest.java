package org.giste.spring.util.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.giste.spring.util.service.CrudeRestService;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.NonRemovableDto;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Base abstract class for testing CRUDE controllers. It performs common tests
 * for this kind of controllers.
 * 
 * Subclasses has to implement the following methods:
 * <ul>
 * <li>{@link #getMockService()} to get the mocked service used by the
 * controller.</li>
 * <li>{@link #getBasePath()} to get the base path for the requests managed by
 * the controller under testing.</li>
 * <li>{@link #getBaseView()} to get the base name of the views returned by the
 * controller.</li>
 * <li>{@link #getDtoType()} to get the type of the DTO of the actions managed
 * by the controller.</li>
 * <li>{@link #getNewDto()} to get a new DTO for testing.</li>
 * <li>{@link #getInvalidDto(NonRemovableDto)} to construct a DTO with invalid
 * properties.</li>
 * <li>{@link #addRequestParams(MockHttpServletRequestBuilder, NonRemovableDto)}
 * to add the additional DTO properties to the request to be sent to the
 * controller.</li>
 * <li>{@link #checkInvalidProperties(ResultActions)} to check that the response
 * from the controller has the errors when an invalid DTO is passed as
 * argument.</li>
 * </ul>
 * 
 * Subclasses should override the following methods.
 * <ul>
 * <li>{@link #checkModelList(ResultActions, NonRemovableDto)} to check that a
 * DTO is included in the response from the controller when it returns a
 * list.</li>
 * <li>{@link #checkModel(ResultActions, NonRemovableDto)} to check that a DTO
 * is included in the response from the controller when it returns a single
 * DTO.</li>
 * <li>{@link #checkDto(NonRemovableDto, NonRemovableDto, boolean)} to check
 * that a given DTO matches the properties of another target DTO.</li>
 * </ul>
 * 
 * @author Giste
 *
 */
public abstract class CrudeControllerTest<DTO extends NonRemovableDto> extends BaseControllerTest<DTO> {

	private String pathEnable;
	private String pathDisable;

	@Override
	protected abstract CrudeRestService<DTO> getMockService();

	/**
	 * Checks the <code>disable()</code> method of the controller.
	 * <ul>
	 * <li>Gets a new DTO from superclass to be returned by the mocked service
	 * and set the enabled property to <code>false</code>.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is FOUND.</li>
	 * <li>Checks that returned view is redirected to the base path
	 * (<code>"redirect:/entities"</code>).</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void disableIsValid() throws Exception {
		DTO dto = getNewDto();
		dto.setEnabled(false);

		when(getMockService().disable(dto.getId())).thenReturn(dto);

		getMockMvc().perform(post(pathDisable, dto.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:" + getPathBase()))
				.andExpect(redirectedUrl(getPathBase()));

		verify(getMockService()).disable(dto.getId());
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Checks the <code>disable()</code> method of the controller when the
	 * entity doesn't exist.
	 * <ul>
	 * <li>Gets a new DTO from superclass to be returned by the mocked service
	 * and set the enabled property to <code>false</code>.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is NOT_FOUND.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void disableClubNotFound() throws Exception {
		DTO dto = getNewDto();
		dto.setEnabled(false);

		when(getMockService().disable(dto.getId())).thenThrow(new EntityNotFoundException("message"));

		getMockMvc().perform(post(pathDisable, dto.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isNotFound());

		verify(getMockService()).disable(dto.getId());
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Checks the <code>enable()</code> method of the controller.
	 * <ul>
	 * <li>Gets a new DTO from superclass to be returned by the mocked service
	 * and set the enabled property to <code>true</code>.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is FOUND.</li>
	 * <li>Checks that returned view is redirected to the base path
	 * (<code>"redirect:/entities"</code>).</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void enableIsValid() throws Exception {
		DTO dto = getNewDto();
		dto.setEnabled(true);

		when(getMockService().enable(dto.getId())).thenReturn(dto);

		getMockMvc().perform(post(pathEnable, dto.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:" + getPathBase()))
				.andExpect(redirectedUrl(getPathBase()));

		verify(getMockService()).enable(dto.getId());
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Checks the <code>enable()</code> method of the controller when the entity
	 * doesn't exist.
	 * <ul>
	 * <li>Gets a new DTO from superclass to be returned by the mocked service
	 * and set the enabled property to <code>true</code>.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is NOT_FOUND.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void enableClubNotFound() throws Exception {
		DTO dto = getNewDto();
		dto.setEnabled(true);

		when(getMockService().enable(dto.getId())).thenThrow(new EntityNotFoundException("message"));

		getMockMvc().perform(post(pathEnable, dto.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isNotFound());

		verify(getMockService()).enable(dto.getId());
		verifyNoMoreInteractions(getMockService());
	}

	@Override
	protected void constructPaths() {
		super.constructPaths();
		pathEnable = getPathBase() + CrudeController.PATH_ENABLE;
		pathDisable =getPathBase() +  CrudeController.PATH_DISABLE;
	}

	/**
	 * Gets the path for the enable action in a single entity. it's
	 * <code>"/entity/{id}/enable"</code>.
	 * 
	 * @return The path for the enable action.
	 */
	protected String getPathEnable() {
		return pathEnable;
	}

	/**
	 * Gets the path for the disable action in a single entity. it's
	 * <code>"/entity/{id}/disable"</code>.
	 * 
	 * @return The path for the disable action.
	 */
	protected String getPathDisable() {
		return pathDisable;
	}

	@Override
	protected ResultActions checkModelList(ResultActions result, DTO target) throws Exception {
		return super.checkModelList(result, target)
				.andExpect(model().attribute("entityList", hasItem(hasProperty("enabled", is(target.isEnabled())))));
	}

	@Override
	protected ResultActions checkModel(ResultActions result, DTO target) throws Exception {
		return super.checkModel(result, target)
				.andExpect(model().attribute("entity", hasProperty("enabled", is(target.isEnabled()))));
	}

	@Override
	protected void checkDto(DTO dto, DTO target, boolean checkId) {
		super.checkDto(dto, target, checkId);
		assertThat(dto.isEnabled(), is(target.isEnabled()));
	}

	@Override
	protected MockHttpServletRequestBuilder addRequestParams(MockHttpServletRequestBuilder request,
			NonRemovableDto dto) {
		return request.param("enabled", String.valueOf(dto.isEnabled()));
	}
}