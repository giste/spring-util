package org.giste.spring.util.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.giste.spring.util.service.CrudRestService;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.BaseDto;
import org.giste.util.dto.NonRemovableDto;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Base abstract class for testing CRUD controllers. It performs common tests
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
public abstract class CrudControllerTest<DTO extends BaseDto> extends BaseControllerTest<DTO> {

	private String pathDelete;

	@Override
	protected abstract CrudRestService<DTO> getMockService();

	/**
	 * Gets the path for the delete action in the controller. It's
	 * <code>"/entity/{id}/delete"</code>.
	 * 
	 * @return The path for the delete action in the controller.
	 */
	protected String getPathDelete() {
		return pathDelete;
	}

	@Override
	protected void constructPaths() {
		super.constructPaths();
		pathDelete = getPathBase() + CrudController.PATH_DELETE;
	}
	
	/**
	 * Checks the <code>delete()</code> method of the controller.
	 * <ul>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is FOUND.</li>
	 * <li>Checks that returned view is redirected to the base path
	 * (<code>"redirect:/entities"</code>).</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void deleteIsValid() throws Exception {
		final Long id = 1L;

		getMockMvc().perform(post(pathDelete, id)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isFound())
				.andExpect(view().name("redirect:" + getPathBase()))
				.andExpect(redirectedUrl(getPathBase()));

		verify(getMockService()).delete(id);
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Checks the <code>delete()</code> method of the controller when the entity
	 * doesn't exist.
	 * <ul>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is NOT_FOUND.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void deleteEntityNotFound() throws Exception {
		final Long id = 1L;

		doThrow(new EntityNotFoundException("Message")).when(getMockService()).delete(id);

		getMockMvc().perform(post(pathDelete, id)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED))
				.andExpect(status().isNotFound());

		verify(getMockService()).delete(id);
		verifyNoMoreInteractions(getMockService());
	}
}
