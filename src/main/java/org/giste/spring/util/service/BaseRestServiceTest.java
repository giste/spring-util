package org.giste.spring.util.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;

import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.config.RestPropertiesImpl;
import org.giste.spring.util.error.dto.FieldErrorDto;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.BaseDto;
import org.giste.util.dto.NonRemovableDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base test class for services accessing REST CRUDE controllers.
 * 
 * Subclasses have to implement the following methods:
 * <ul>
 * <li>{@link #getRestService(RestTemplate, RestPropertiesImpl)} to get the
 * service under testing.</li>
 * <li>{@link #getEmptyDtoArray(int)} to get and empty array of given size of
 * type DTO.</li>
 * <li>{@link #getErrorForInvalidDto()} to get the <code>RestErrorDto</code>
 * returned when calling REST server with an invalid DTO.</li>
 * <li>{@link #getNewDto()} to get a DTO for testing.</li>
 * <li>{@link #getServiceBasePath()} to get the base path for calls to REST
 * server.</li>
 * </ul>
 * 
 * Subclasses should override the following methods:
 * <ul>
 * <li>{@link #checkProperties(NonRemovableDto, NonRemovableDto)} to check that
 * the properties of a DTO matches the ones of a target DTO.</li>
 * </ul>
 * 
 * @author Giste
 *
 * @param <DTO> DTO of the entity controlled by the controller to test.
 */
public abstract class BaseRestServiceTest<DTO extends BaseDto> {

	// Constants for mocked RestPropertiesImpl.
	private static final String SCHEME = "http";
	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	private static final String PATH_REST = "/rest";

	// Constant for paths.
	private final String PATH_ID = "/{id}";

	// Paths for common CRUDE REST queries.
	private String pathBase;
	private String pathId;

	// Objects needed for testing.
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	private UriComponentsBuilder uriBuilder;
	private RestProperties restPropertiesImpl;
	private MockRestServiceServer mockServer;

	// Service under testing.
	private BaseRestService<DTO> service;

	/**
	 * Gets the base path of the service under testing. Base path is constructed
	 * with test parameters (scheme, host, port and path) and the service base
	 * path (usually "/entities") provided by subclasses through
	 * {@link #getServiceBasePath()} method.
	 * 
	 * It has the form <code>http://host:port/path/entities</code>.
	 * 
	 * @return The base path to be used by the service under testing.
	 */
	protected String getPathBase() {
		return pathBase;
	}

	/**
	 * Gets the path for single entity operations for service under testing.
	 * 
	 * It has the form <code>http://host:port/path/entities/{id}</code>.
	 * 
	 * @return The path for single entity operations.
	 */
	protected String getPathId() {
		return pathId;
	}

	/**
	 * Gets the <code>RestTemplate</code> to be used in the service under
	 * testing. Subclasses have to use the retrieved <code>RestTemplate</code>
	 * to construct the service under testing.
	 * 
	 * @return The <code>RestTemplate</code> to be used in the service under
	 *         testing.
	 */
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	/**
	 * Gets the component for building the URIs, populated with testing values
	 * for scheme, host, port and path.
	 * 
	 * @return Component for URI building.
	 */
	protected UriComponentsBuilder getUriBuilder() {
		return uriBuilder;
	}

	/**
	 * Gets the properties for the REST server in the testing environment.
	 * Subclasses have to use the retrieved object to construct the service
	 * under testing.
	 * 
	 * @return The properties for the REST server in the testing environment.
	 */
	protected RestProperties getRestProperties() {
		return restPropertiesImpl;
	}

	/**
	 * Gets the mocked server for testing REST services. Subclasses have to use
	 * this object for their own testing cases, as it's bounded to RestTemplate.
	 * 
	 * @return The mock server for testing REST services.
	 */
	protected MockRestServiceServer getMockServer() {
		return mockServer;
	}

	/**
	 * Gets the <code>ObjectMapper</code> for mapping request and responses to
	 * DTOs.
	 * 
	 * @return The <code>ObjectMapper</code> for mapping request and responses
	 *         to DTOs.
	 */
	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * Gets the service to be tested.
	 * 
	 * @return the service to be tested.
	 */
	protected BaseRestService<DTO> getService() {
		return service;
	}

	/**
	 * Checks if two DTO have the same properties. It's used in tests. Default
	 * implementation checks <code>id</code> and <code>enabled</code>
	 * properties. Subclasses should call <code>super(dto, targetDto)</code> and
	 * then check additional properties of the DTO.
	 * 
	 * @param dto The DTO to check.
	 * @param targetDto The DTO for comparing properties.
	 */
	protected void checkProperties(DTO dto, DTO targetDto) {
		assertThat(dto.getId(), is(targetDto.getId()));
	}

	/**
	 * Gets an empty DTO array of the required size.
	 * 
	 * @param size The size of the required array.
	 * @return The created array.
	 */
	protected abstract DTO[] getEmptyDtoArray(int size);

	/**
	 * Gets a new DTO. Base class can modify the properties of the DTO during
	 * testing.
	 * 
	 * @return The new created DTO.
	 */
	protected abstract DTO getNewDto();

	/**
	 * Gets the name of the entities to be used for constructing the URL for
	 * invoking the REST server. If the service to test is used for managing
	 * cars entities, this function should return "/cars" for constructing URLs
	 * as <code>http://localhost:8080/rest/cars/1/enable</code>.
	 * 
	 * @return The entity part of the URL.
	 */
	protected abstract String getServiceBasePath();

	/**
	 * Gets the service to test. Subclasses has to return the service to test
	 * created with the RestTemplate and RestPropertiesImpl passed as
	 * parameters.
	 * 
	 * @param restTemplate RestTemplate used by the service to communicate with
	 *            REST server.
	 * @param restPropertiesImpl RestPropertiesImpl used by the service to know
	 *            the REST server address.
	 * @return The created service to test.
	 */
	protected abstract BaseRestService<DTO> getRestService(RestTemplate restTemplate,
			RestProperties restPropertiesImpl);

	/**
	 * Gets the <code>RestErrorDto</code> returned when testing for actions with
	 * invalid DTO.
	 * 
	 * @return The <code>RestErrorDto</code>.
	 */
	protected abstract RestErrorDto getErrorForInvalidDto();

	/**
	 * Setup the needed object for testing.
	 * <ul>
	 * <li>Creates <code>ObjectMapper</code>.</li>
	 * <li>Builds and mock <code>RestPropertiesImpl</code> for testing.</li>
	 * <li>Creates URI builder and populate it with testing values for scheme,
	 * host, port and path.</li>
	 * <li>Builds different REST paths from base path.</li>
	 * <li>Creates new <code>RestTemplate</code> for testing.</li>
	 * <li>Retrieves service to test from subclass calling
	 * {@link #getRestService(RestTemplate, RestPropertiesImpl)}, passing the
	 * created <code>RestTemplate</code> and <code>RestPropertiesImpl</code> as
	 * parameters.</li>
	 * <li>Creates mock server for testing bound to the created
	 * <code>RestTemplate</code>.</li>
	 * </ul>
	 * 
	 * Subclasses may override it, but have to call <code>super()</code> as the
	 * first line of code.
	 */
	@Before
	public void setUp() {
		objectMapper = new ObjectMapper();

		restPropertiesImpl = mock(RestPropertiesImpl.class);
		when(restPropertiesImpl.getScheme()).thenReturn(SCHEME);
		when(restPropertiesImpl.getHost()).thenReturn(HOST);
		when(restPropertiesImpl.getPort()).thenReturn(PORT);
		when(restPropertiesImpl.getPath()).thenReturn(PATH_REST);

		uriBuilder = UriComponentsBuilder.newInstance()
				.scheme(restPropertiesImpl.getScheme())
				.host(restPropertiesImpl.getHost())
				.port(restPropertiesImpl.getPort())
				.path(restPropertiesImpl.getPath());

		pathBase = getServiceBasePath();
		pathId = pathBase + PATH_ID;

		restTemplate = new RestTemplate();
		service = getRestService(restTemplate, restPropertiesImpl);
		mockServer = MockRestServiceServer.bindTo(restTemplate).build();
	}

	/**
	 * Test for findAll() method.
	 * <ul>
	 * <li>Ask subclass for two DTO.</li>
	 * <li>Change their properties to <code>(1L, true)</code> and
	 * <code>(2L, false)</code>.</li>
	 * <li>Check that correct HTTP method and URI are used when calling REST
	 * server.</li>
	 * <li>Check that returned DTO list has two items.</li>
	 * <li>Call {@link #checkProperties(NonRemovableDto, NonRemovableDto)}
	 * method for each DTO in the returned list.</li>
	 * </ul>
	 * 
	 * @throws Exception If response can't be mapped to DTO.
	 */
	@Test
	public void testFindAllIsOk() throws Exception {
		final DTO dto1 = getNewDto();
		dto1.setId(1L);
		final DTO dto2 = getNewDto();
		dto2.setId(2L);
		final DTO[] dtoList = getEmptyDtoArray(2);

		dtoList[0] = dto1;
		dtoList[1] = dto2;

		final UriComponents uri = uriBuilder.path(pathBase).build();

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.GET))
				// .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(dtoList), MediaType.APPLICATION_JSON_UTF8));

		List<DTO> readDtoList = service.findAll();

		mockServer.verify();

		assertThat(readDtoList.size(), is(dtoList.length));
		checkProperties(readDtoList.get(0), dto1);
		checkProperties(readDtoList.get(1), dto2);
	}

	/**
	 * Checks that correct HTTP method and URI are used when calling REST server
	 * and that <code>findAll()</code> method returns an empty DTO list if there
	 * are no entities.
	 * <ul>
	 * <li>Check that correct HTTP method and URI are used when calling REST
	 * server.</li>
	 * <li>Check that returned DTO list has no items.</li>
	 * </ul>
	 * 
	 * @throws Exception If response can't be mapped to DTO.
	 */
	@Test
	public void testFindAllIsEmpty() throws Exception {
		final DTO[] dtoList = getEmptyDtoArray(0);
		
		final UriComponents uri = uriBuilder.path(pathBase).build();

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(dtoList), MediaType.APPLICATION_JSON_UTF8));

		List<DTO> readList = service.findAll();

		mockServer.verify();

		assertThat(readList.size(), is(0));
	}

	/**
	 * Checks that correct HTTP method and URI are used when calling REST server
	 * and checks that returned DTO corresponds to read entity.
	 * <ul>
	 * <li>Ask subclass for one DTO.</li>
	 * <li>Check that correct HTTP method and URI are used when calling REST
	 * server.</li>
	 * <li>Call {@link #checkProperties(NonRemovableDto, NonRemovableDto)}
	 * method for the read DTO.</li>
	 * </ul>
	 * 
	 * @throws Exception If response can't be mapped to DTO.
	 */
	@Test
	public void testFindByIdIsOk() throws Exception {
		final DTO dto = getNewDto();

		final UriComponents uri = uriBuilder.path(pathId).buildAndExpand(dto.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(dto), MediaType.APPLICATION_JSON_UTF8));

		DTO readDto = service.findById(dto.getId());

		mockServer.verify();

		checkProperties(readDto, dto);
	}

	/**
	 * Checks that service throws <code>HttpClientErrorException</code> when
	 * receives a REST message with status NOT_FOUND and checks that the read
	 * error matches the received one.
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testFindByIdEntityNotFound() throws Exception {
		RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "10001001", "Message", "Developer info");

		final UriComponents uri = uriBuilder.path(pathId).buildAndExpand(1);

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			service.findById(1);
			fail("EntityNotFoundException expected.");
		} catch (EntityNotFoundException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		mockServer.verify();
	}

	/**
	 * Checks the creation of an entity when the DTO passed as parameter is
	 * valid.
	 * <ul>
	 * <li>Ask subclass for one DTO.</li>
	 * <li>Check that correct HTTP method and URI are used when calling REST
	 * server.</li>
	 * <li>Call {@link #checkProperties(NonRemovableDto, NonRemovableDto)}
	 * method for the read DTO.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testCreateIsValid() throws Exception {
		final DTO dto = getNewDto();

		final UriComponents uri = uriBuilder.path(pathBase).build();

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.POST))
				.andExpect(content().bytes(objectMapper.writeValueAsBytes(dto)))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(dto), MediaType.APPLICATION_JSON_UTF8));

		DTO readDto = service.create(dto);

		mockServer.verify();

		checkProperties(readDto, dto);
	}

	/**
	 * Check the update of an entity.
	 * <ul>
	 * <li>Ask subclass for one DTO.</li>
	 * <li>Check that correct HTTP method and URI are used when calling REST
	 * server.</li>
	 * <li>Call {@link #checkProperties(NonRemovableDto, NonRemovableDto)}
	 * method for the read DTO.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testUpdateIsValid() throws Exception {
		final DTO dto = getNewDto();

		final UriComponents uri = uriBuilder.path(pathId).buildAndExpand(dto.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andExpect(content().bytes(objectMapper.writeValueAsBytes(dto)))
				.andRespond(withSuccess(objectMapper.writeValueAsBytes(dto), MediaType.APPLICATION_JSON_UTF8));

		DTO readDto = service.update(dto);

		mockServer.verify();

		checkProperties(readDto, dto);
	}

	/**
	 * Checks that an <code>HttpClientErrorException</code> is thrown when
	 * trying to update an entity that doesn't exist. Checks that exception has
	 * the right data into embedded <code>RestErrorDto</code>.
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testUpdateEntityNotFound() throws Exception {
		final RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "0", "Message", "Developer info");
		final DTO dto = getNewDto();

		final UriComponents uri = uriBuilder.path(pathId).buildAndExpand(dto.getId());

		mockServer.expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(objectMapper.writeValueAsBytes(error)));

		try {
			service.update(dto);
			fail("EntityNotFoundException expected.");
		} catch (EntityNotFoundException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		mockServer.verify();
	}

	/**
	 * Checks that a proper <code>RestErrorDto</code> is read when REST server
	 * returns an error for invalid DTO when trying to create an entity.
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testCreateIsInvalid() throws Exception {
		final DTO dto = getNewDto();
		final RestErrorDto error = getErrorForInvalidDto();

		List<FieldErrorDto> fieldErrorList = error.getFieldErrorList();

		final UriComponents uri = getUriBuilder().path(getPathBase()).build();

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withBadRequest()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().create(dto);
			fail("Expected HttpClientErrorException");
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.BAD_REQUEST));
			RestErrorDto readError = getObjectMapper().readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));

			List<FieldErrorDto> readFieldErrorList = readError.getFieldErrorList();
			assertThat(readFieldErrorList.size(), is(fieldErrorList.size()));

			for (int i = 0; i < fieldErrorList.size(); i++) {
				assertThat(readFieldErrorList.get(i).getField(), is(fieldErrorList.get(i).getField()));
				assertThat(readFieldErrorList.get(i).getMessage(), is(fieldErrorList.get(i).getMessage()));
			}
		}

		getMockServer().verify();
	}

	/**
	 * Checks that a proper <code>RestErrorDto</code> is read when REST server
	 * returns an error for invalid DTO when trying to update an entity.
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testUpdateIsInvalid() throws Exception {
		final DTO dto = getNewDto();
		final RestErrorDto error = getErrorForInvalidDto();

		List<FieldErrorDto> fieldErrorList = error.getFieldErrorList();

		final UriComponents uri = getUriBuilder().path(getPathId()).buildAndExpand(dto.getId());

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withBadRequest()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().update(dto);
			fail("Expected HttpClientErrorException");
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(HttpStatus.BAD_REQUEST));
			RestErrorDto readError = getObjectMapper().readValue(e.getResponseBodyAsByteArray(), RestErrorDto.class);
			assertThat(readError.getStatus(), is(error.getStatus()));
			assertThat(readError.getCode(), is(error.getCode()));
			assertThat(readError.getMessage(), is(error.getMessage()));
			assertThat(readError.getDeveloperInfo(), is(error.getDeveloperInfo()));

			List<FieldErrorDto> readFieldErrorList = readError.getFieldErrorList();
			assertThat(readFieldErrorList.size(), is(fieldErrorList.size()));

			for (int i = 0; i < fieldErrorList.size(); i++) {
				assertThat(readFieldErrorList.get(i).getField(), is(fieldErrorList.get(i).getField()));
				assertThat(readFieldErrorList.get(i).getMessage(), is(fieldErrorList.get(i).getMessage()));
			}
		}

		getMockServer().verify();
	}

}