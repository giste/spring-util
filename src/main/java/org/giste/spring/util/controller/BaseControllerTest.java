package org.giste.spring.util.controller;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.giste.spring.util.service.BaseRestService;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.BaseDto;
import org.giste.util.dto.NonRemovableDto;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
public abstract class BaseControllerTest<DTO extends BaseDto> {

	private String pathBase;
	private String pathId;
	private String pathNew;

	private String viewBase;
	private String viewList;

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Constructs the paths managed by the controller. Subclasses can add new
	 * paths, but should call <code>super.constructPaths()</code>.
	 */
	protected void constructPaths() {
		pathBase = getBasePath();
		pathId = pathBase + BaseController.PATH_ID;
		pathNew = pathBase + BaseController.PATH_NEW;
	}

	/**
	 * Constructs the names of the views managed by the controller. Subclasses
	 * can add new views, but should call <code>super.constructViews()</code>.
	 */
	protected void constructViews() {
		viewBase = getBaseView();
		viewList = viewBase + BaseController.VIEW_LIST;
	}

	/**
	 * Set up the testing scenario. Gets the mocked service, base path and base
	 * view from superclass, and construct all needed paths and views for
	 * testing.
	 */
	@Before
	public void setUp() {
		constructPaths();
		constructViews();
	}

	/**
	 * Tests the <code>findAll()</code> method of the controller.
	 * <ul>
	 * <li>Gets a list of DTOs from superclass to be returned by the mocked
	 * service.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is OK.</li>
	 * <li>Checks that returned view is the list view
	 * (<code>"entitiesList"</code>).</li>
	 * <li>Checks that the size of the <code>"entityList"</code> model attribute
	 * matches the size of the DTO list.</li>
	 * <li>For each DTO in the list, check that it's included in the model.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void findAllIsValid() throws Exception {
		DTO dto1 = getNewDto();
		dto1.setId(1L);
		DTO dto2 = getNewDto();
		dto2.setId(2L);
		List<DTO> dtoList = new ArrayList<DTO>();
		dtoList.add(dto1);
		dtoList.add(dto2);

		when(getMockService().findAll()).thenReturn(dtoList);

		ResultActions result = mockMvc.perform(get(pathBase))
				.andExpect(status().isOk())
				.andExpect(view().name(viewList))
				.andExpect(model().attribute("entityList", hasSize(dtoList.size())));

		for (int i = 0; i < dtoList.size(); i++) {
			checkModelList(result, dtoList.get(i));
		}

		verify(getMockService()).findAll();
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Checks the <code>findAll()</code> method of the controller when an empty
	 * list is returned.
	 * <ul>
	 * <li>Constructs an empty list to be returned by the mocked service.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is OK.</li>
	 * <li>Checks that returned view is the list view
	 * (<code>"entitiesList"</code>).</li>
	 * <li>Checks that the size of the <code>"entityList"</code> model attribute
	 * is 0.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void findAllIsEmpty() throws Exception {
		when(getMockService().findAll()).thenReturn(Arrays.asList());

		mockMvc.perform(get(pathBase))
				.andExpect(status().isOk())
				.andExpect(view().name(viewList))
				.andExpect(model().attribute("entityList", hasSize(0)));

		verify(getMockService()).findAll();
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Checks the <code>findById()</code> method of the controller when a valid
	 * identifier is passed as parameter.
	 * <ul>
	 * <li>Gets a new DTO from superclass to be returned by the mocked
	 * service.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is OK.</li>
	 * <li>Checks that returned view is the base view
	 * (<code>"entities"</code>).</li>
	 * <li>Checks that the model DTO matches the returned one.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void findByIdIsValid() throws Exception {
		DTO dto = getNewDto();

		when(getMockService().findById(dto.getId())).thenReturn(dto);

		ResultActions result = mockMvc.perform(get(pathId, dto.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name(viewBase));

		checkModel(result, dto);

		verify(getMockService()).findById(dto.getId());
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Checks the <code>findById()</code> method of the controller when an
	 * invalid identifier is passed as parameter.
	 * <ul>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is NOT_FOUND.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void findByIdEntityNotFound() throws Exception {
		final Long id = 1L;
		when(getMockService().findById(id)).thenThrow(new EntityNotFoundException("message"));

		mockMvc.perform(get(pathId, id))
				.andExpect(status().isNotFound());

		verify(getMockService()).findById(id);
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Checks the <code>create()</code> method of the controller when a valid
	 * DTO is passed as parameter.
	 * <ul>
	 * <li>Gets a new DTO from superclass to be returned by the mocked
	 * service.</li>
	 * <li>Adds DTO attributes to the request as parameters.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is FOUND.</li>
	 * <li>Checks that returned view is redirected to the base path
	 * (<code>"redirect:/entities"</code>).</li>
	 * <li>Checks that the DTO passed as argument to the mocked service matches
	 * the one got from superclass.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void createIsValid() throws Exception {
		DTO dto = getNewDto();

		when(getMockService().create(any(getDtoType()))).thenReturn(dto);

		MockHttpServletRequestBuilder request = post(pathBase)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED);

		request = addRequestParams(request, dto);

		mockMvc.perform(request)
				.andExpect(status().isFound())
				.andExpect(view().name(is("redirect:" + pathBase)))
				.andExpect(redirectedUrl(pathBase));

		ArgumentCaptor<DTO> dtoCaptor = ArgumentCaptor.forClass(getDtoType());
		verify(getMockService()).create(dtoCaptor.capture());
		verifyNoMoreInteractions(getMockService());

		DTO capturedDto = dtoCaptor.getValue();
		checkDto(capturedDto, dto, false);
	}

	/**
	 * Checks the <code>create()</code> method of the controller when an invalid
	 * DTO is passed as parameter.
	 * <ul>
	 * <li>Gets a new DTO from superclass with invalid properties.</li>
	 * <li>Adds DTO attributes to the request as parameters.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is OK.</li>
	 * <li>Checks that returned view is the single entity one
	 * (<code>"entity"</code>).</li>
	 * <li>Checks that the response from the controller has the invalid
	 * properties as errors in the model.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void createIsInvalid() throws Exception {
		DTO dto = getInvalidDto(getNewDto());

		MockHttpServletRequestBuilder request = post(pathBase)
				.contentType(MediaType.APPLICATION_FORM_URLENCODED);

		request = addRequestParams(request, dto);

		ResultActions result = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(view().name(is(viewBase)));

		checkInvalidProperties(result);

		verifyZeroInteractions(getMockService());
	}

	/**
	 * Checks the <code>getNew()</code> method of the controller.
	 * <ul>
	 * <li>Checks that status is OK.</li>
	 * <li>Checks that the returned view is the single entity one
	 * (<code>"entity"</code>).</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void getNew() throws Exception {
		mockMvc.perform(get(pathNew))
				.andExpect(status().isOk())
				.andExpect(view().name(is(viewBase)));

		verifyZeroInteractions(getMockService());
	}

	/**
	 * Checks the <code>update()</code> method of the controller when a valid
	 * DTO is passed as parameter.
	 * <ul>
	 * <li>Gets a new DTO from superclass to be returned by the mocked
	 * service.</li>
	 * <li>Adds DTO attributes to the request as parameters.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is FOUND.</li>
	 * <li>Checks that returned view is redirected to the base path
	 * (<code>"redirect:/entities"</code>).</li>
	 * <li>Checks that the DTO passed as argument to the mocked service matches
	 * the one got from superclass.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void updateIsValid() throws Exception {
		DTO dto = getNewDto();

		when(getMockService().update(any(getDtoType()))).thenReturn(dto);

		MockHttpServletRequestBuilder request = post(pathId, dto.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(dto.getId()));

		request = addRequestParams(request, dto);

		mockMvc.perform(request)
				.andExpect(status().isFound())
				.andExpect(view().name(is("redirect:" + pathBase)))
				.andExpect(redirectedUrl(pathBase));

		ArgumentCaptor<DTO> dtoCaptor = ArgumentCaptor.forClass(getDtoType());
		verify(getMockService()).update(dtoCaptor.capture());
		verifyNoMoreInteractions(getMockService());

		DTO capturedDto = dtoCaptor.getValue();
		checkDto(capturedDto, dto, true);
	}

	/**
	 * Checks the <code>update()</code> method of the controller when an invalid
	 * DTO is passed as parameter.
	 * <ul>
	 * <li>Gets a new DTO from superclass with invalid properties.</li>
	 * <li>Adds DTO attributes to the request as parameters.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is OK.</li>
	 * <li>Checks that returned view is the single entity one
	 * (<code>"entity"</code>).</li>
	 * <li>Checks that the response from the controller has the invalid
	 * properties as errors in the model.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void updateIsInvalid() throws Exception {
		DTO dto = getInvalidDto(getNewDto());

		MockHttpServletRequestBuilder request = post(pathId, dto.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(dto.getId()));

		request = addRequestParams(request, dto);

		ResultActions result = mockMvc.perform(request)
				.andExpect(status().isOk())
				.andExpect(view().name(is(viewBase)));

		checkInvalidProperties(result);

		verifyZeroInteractions(getMockService());
	}

	/**
	 * Checks the <code>update()</code> method of the controller when the entity
	 * to update doesn't exist.
	 * <ul>
	 * <li>Gets a new DTO from superclass to be returned by the mocked
	 * service.</li>
	 * <li>Adds DTO attributes to the request as parameters.</li>
	 * <li>Performs the request to the controller.</li>
	 * <li>Checks status is NOT_FOUND.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error calling the controller.
	 */
	@Test
	public void updateEntityNotFound() throws Exception {
		DTO dto = getNewDto();

		when(getMockService().update(any(getDtoType()))).thenThrow(new EntityNotFoundException("message"));

		MockHttpServletRequestBuilder request = post(pathId, dto.getId())
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("id", String.valueOf(dto.getId()));

		request = addRequestParams(request, dto);

		mockMvc.perform(request).andExpect(status().isNotFound());

		verify(getMockService()).update(any(getDtoType()));
		verifyNoMoreInteractions(getMockService());
	}

	/**
	 * Gets the base path used by the controller to test. Usually it's
	 * <code>"/entities"</code>.
	 * 
	 * @return The base path.
	 */
	protected String getPathBase() {
		return pathBase;
	}

	/**
	 * Gets the path for actions for single entities. It's
	 * <code>"/entities/{id}"</code>.
	 * 
	 * @return The path for actions for single entities.
	 */
	protected String getPathId() {
		return pathId;
	}

	/**
	 * Gets the path for creating a new entity. It's
	 * <code>"/entities/new"</code>.
	 * 
	 * @return The path for creating a new entity.
	 */
	protected String getPathNew() {
		return pathNew;
	}

	/**
	 * Gets the base view. Usually it's <code>"entity"</code>.
	 * 
	 * @return The base view.
	 */
	protected String getViewBase() {
		return viewBase;
	}

	/**
	 * Gets the view for the list of entities. It's <code>"entitiesList"</code>.
	 * 
	 * @return The view for the list of entities.
	 */
	protected String getViewList() {
		return viewList;
	}

	/**
	 * Gets the mock MVC used for testing.
	 * 
	 * @return The mock MVC.
	 */
	protected MockMvc getMockMvc() {
		return mockMvc;
	}

	/**
	 * Gets the base path for the controller to test.
	 * 
	 * @return The base path.
	 */
	protected abstract String getBasePath();

	/**
	 * Gets the base view name used by the controller under testing.
	 * 
	 * @return The base view.
	 */
	protected abstract String getBaseView();

	/**
	 * Gets the mock service to be used by the controller under testing.
	 * 
	 * @return The mock service to be used by the controller under testing.
	 */
	protected abstract BaseRestService<DTO> getMockService();

	/**
	 * Gets the type of the DTO managed by the controller under testing.
	 * 
	 * @return The type of the DTO managed by the controller under testing.
	 */
	protected abstract Class<DTO> getDtoType();

	/**
	 * Gets a new DTO for being used in tests.
	 * 
	 * @return The new DTO.
	 */
	protected abstract DTO getNewDto();

	/**
	 * Gets an invalid DTO from a valid one filling the properties with invalid
	 * values.
	 * 
	 * @param dto The DTO to be filled with invalid properties.
	 * @return The invalid DTO.
	 */
	protected abstract DTO getInvalidDto(DTO dto);

	/**
	 * Checks that the <code>ResultActions</code> returned by the controller has
	 * the errors for the properties changed by
	 * {@link #getInvalidDto(NonRemovableDto)} method.
	 * 
	 * @param result The <code>ResultActions</code> returned by the controller
	 *            under testing.
	 * @throws Exception If there is an error accessing the
	 *             <code>ResultActions</code>.
	 */
	protected abstract void checkInvalidProperties(ResultActions result) throws Exception;

	/**
	 * Adds the DTO properties to the request to be sent to the controller.
	 * Should use
	 * <code>request.param("propertyName", dto.getPropertyName())</code>.
	 * 
	 * @param request The request to be sent to the controller.
	 * @param dto The DTO whose properties have to be added to the request.
	 * @return The request with the properties added as parameters.
	 */
	protected abstract MockHttpServletRequestBuilder addRequestParams(MockHttpServletRequestBuilder request, DTO dto);

	/**
	 * Checks that the <code>ResultActions</code> returned by the controller has
	 * the DTO passed as argument in the model with the right property values.
	 * In this case, the model is a list of DTOs.
	 * 
	 * @param result The <code>ResultActions</code> returned by the controller
	 *            under testing.
	 * @param target The DTO that should be in the model.
	 * @return The <code>ResultActions</code> to continue with further checking.
	 * @throws Exception If there is an error accessing the
	 *             <code>ResultActions</code>.
	 */
	protected ResultActions checkModelList(ResultActions result, DTO target) throws Exception {
		return result.andExpect(model().attribute("entityList", hasItem(
				hasProperty("id", is(target.getId())))));
	}

	/**
	 * Checks that the <code>ResultActions</code> returned by the controller has
	 * the DTO passed as argument in the model with the right property values.
	 * In this case, the model is a single DTO.
	 * 
	 * @param result The <code>ResultActions</code> returned by the controller
	 *            under testing.
	 * @param target The DTO that should be in the model.
	 * @return The <code>ResultActions</code> to continue with further checking.
	 * @throws Exception If there is an error accessing the
	 *             <code>ResultActions</code>.
	 */
	protected ResultActions checkModel(ResultActions result, DTO target) throws Exception {
		return result.andExpect(model().attribute("entity", hasProperty("id", is(target.getId()))));
	}

	/**
	 * Checks that a given DTO has the same properties than a target DTO.
	 * 
	 * @param dto The DTO to be checked.
	 * @param target The target DTO.
	 * @param checkId <code>true</code> if the identifier property has to be
	 *            checked or <code>false</code> otherwise.
	 */
	protected void checkDto(DTO dto, DTO target, boolean checkId) {
		if (checkId) {
			assertThat(dto.getId(), is(target.getId()));
		}
	}
}