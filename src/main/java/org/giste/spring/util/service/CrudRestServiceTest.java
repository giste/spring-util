package org.giste.spring.util.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.BaseDto;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponents;

public abstract class CrudRestServiceTest<DTO extends BaseDto> extends BaseRestServiceTest<DTO> {

	@Override
	protected CrudRestService<DTO> getService() {
		return (CrudRestService<DTO>) super.getService();
	}

	/**
	 * Checks that correct HTTP method and URI are used when calling REST server
	 * and that <code>delete()</code> method returns no error.
	 * <ul>
	 * <li>Check that correct HTTP method and URI are used when calling REST
	 * server.</li>
	 * </ul>
	 */
	@Test
	public void deleteIsValid() {
		final Long id = 1L;

		final UriComponents uri = getUriBuilder().path(getPathId()).buildAndExpand(id);

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.DELETE))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON_UTF8));
		// withSuccess("", MediaType.APPLICATION_JSON_UTF8));

		getService().delete(id);

		getMockServer().verify();
	}

	@Test
	public void deleteEntityNotFound() throws Exception {
		final Long id = 1L;
		final RestErrorDto error = new RestErrorDto(HttpStatus.NOT_FOUND, "10001001", "Message", "Developer info");

		final UriComponents uri = getUriBuilder().path(getPathId()).buildAndExpand(id);

		getMockServer().expect(requestTo(uri.toUriString()))
				.andExpect(method(HttpMethod.DELETE))
				.andRespond(withStatus(HttpStatus.NOT_FOUND)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.body(getObjectMapper().writeValueAsBytes(error)));

		try {
			getService().delete(id);
			fail("EntityNotFoundException expected.");
		} catch (EntityNotFoundException e) {
			assertThat(e.getMessage(), is(error.getMessage()));
		}

		getMockServer().verify();
	}
}
