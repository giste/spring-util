package org.giste.spring.util.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.giste.spring.util.config.RestPropertiesImpl;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.NonRemovableDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

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
public abstract class CrudeRestServiceTest<DTO extends NonRemovableDto> extends BaseRestServiceTest<DTO> {

	// Constant for paths.
	private final String PATH_ENABLE = "/enable";
	private final String PATH_DISABLE = "/disable";

	// Paths for common CRUDE REST queries.
	private String pathEnable;
	private String pathDisable;

	@Override
	protected CrudeRestService<DTO> getService() {
		return (CrudeRestService<DTO>) super.getService();
	}

	/**
	 * Gets the path for enabling a single entity for service under testing.
	 * 
	 * It has the form <code>http://host:port/path/entities/{id}/enable</code>.
	 * 
	 * @return The path for enabling a single entity.
	 */
	public String getPathEnable() {
		return pathEnable;
	}

	/**
	 * Gets the path for disabling a single entity for service under testing.
	 * 
	 * It has the form <code>http://host:port/path/entities/{id}/disable</code>.
	 * 
	 * @return The path for disabling a single entity.
	 */
	public String getPathDisable() {
		return pathDisable;
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
		super.checkProperties(dto, targetDto);
		assertThat(dto.isEnabled(), is(targetDto.isEnabled()));
	}

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
		super.setUp();
		pathEnable = getPathId() + PATH_ENABLE;
		pathDisable = getPathId() + PATH_DISABLE;
	}

	/**
	 * Checks the enabling of an entity.
	 * <ul>
	 * <li>Ask subclass for one DTO and change its enabled state to
	 * <code>true</code>.</li>
	 * <li>Check that correct HTTP method and URI are used when calling REST
	 * server.</li>
	 * <li>Call {@link #checkProperties(NonRemovableDto, NonRemovableDto)}
	 * method for the read DTO.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testEnableIsValid() throws Exception {
		final DTO dto = getNewDto();
		dto.setEnabled(true);

		final UriComponents uri = getUriBuilder().path(pathEnable).buildAndExpand(dto.getId());

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withSuccess(getObjectMapper().writeValueAsBytes(dto), MediaType.APPLICATION_JSON_UTF8));

		DTO readDto = getService().enable(dto.getId());

		getMockServer().verify();

		checkProperties(readDto, dto);
	}

	/**
	 * Checks that an <code>HttpClientErrorException</code> is thrown when
	 * trying to enable an entity that doesn't exist. Checks that exception has
	 * the right data into embedded <code>RestErrorDto</code>.
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testEnableEntityNotFound() throws Exception {
		final RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "0", "Message", "Developer info");
		final Long id = 1L;

		final UriComponents uri = getUriBuilder().path(pathEnable).buildAndExpand(id);

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().enable(id);
			fail("EntityNotFoundException expected.");
		} catch (EntityNotFoundException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		getMockServer().verify();
	}

	/**
	 * Checks the disabling of an entity.
	 * <ul>
	 * <li>Ask subclass for one DTO and change its enabled state to
	 * <code>false</code>.</li>
	 * <li>Check that correct HTTP method and URI are used when calling REST
	 * server.</li>
	 * <li>Call {@link #checkProperties(NonRemovableDto, NonRemovableDto)}
	 * method for the read DTO.</li>
	 * </ul>
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testDisableIsValid() throws Exception {
		final DTO dto = getNewDto();
		dto.setEnabled(false);

		final UriComponents uri = getUriBuilder().path(pathDisable).buildAndExpand(dto.getId());

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withSuccess(getObjectMapper().writeValueAsBytes(dto), MediaType.APPLICATION_JSON_UTF8));

		DTO readDto = getService().disable(dto.getId());

		getMockServer().verify();

		checkProperties(readDto, dto);
	}

	/**
	 * Checks that an <code>HttpClientErrorException</code> is thrown when
	 * trying to disable an entity that doesn't exist. Checks that exception has
	 * the right data into embedded <code>RestErrorDto</code>.
	 * 
	 * @throws Exception If there is an error with ObjectMapper.
	 */
	@Test
	public void testDisableEntityNotFound() throws Exception {
		final RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "0", "Message", "Developer info");
		final Long id = 1L;

		final UriComponents uri = getUriBuilder().path(pathDisable).buildAndExpand(id);

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.PUT))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().disable(id);
			fail("EntityNotFoundException expected.");
		} catch (EntityNotFoundException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		getMockServer().verify();
	}

}