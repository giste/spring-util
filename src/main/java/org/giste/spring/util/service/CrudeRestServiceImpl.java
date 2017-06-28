package org.giste.spring.util.service;

import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.NonRemovableDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

/**
 * Base implementation class for services that performs CRUDE operations with a
 * REST server. Subclasses have to implement the following methods:
 * <ul>
 * <li>{@link #getDtoType()} to provide the type of the DTO.</li>
 * <li>{@link #getArrayType()} to provide the type of a DTO array.</li>
 * <li>{@link #getBasePath()} to provide the base path of actions at REST server
 * for the entity managed by this service.</li>
 * <li>{@link #handleHttpStatusConflict(RestErrorDto)} to handle responses from
 * REST server with status CONFLICT.</li>
 * </ul>
 * 
 * @author Giste
 *
 * @param <DTO> The DTO of the entity to be managed by this service.
 */
public abstract class CrudeRestServiceImpl<DTO extends NonRemovableDto> extends BaseRestServiceImpl<DTO>
		implements CrudeRestService<DTO> {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private String pathDisable;
	private String pathEnable;

	/**
	 * Construct a new <code>CrudeRestserviceImpl</code>.
	 * 
	 * @param restTemplate <code>RestTemplate</code> used for communicating with
	 *            REST service.
	 * @param restPropertiesImpl <code>RestPropertiesImpl</code> with data for
	 *            connecting to REST service.
	 */
	public CrudeRestServiceImpl(RestTemplate restTemplate, RestProperties restPropertiesImpl) {
		super(restTemplate, restPropertiesImpl);
		constructPaths();
	}
	
	protected String getPathDisable() {
		return pathDisable;
	}

	protected String getPathEnable() {
		return pathEnable;
	}

	private void constructPaths() {
		pathEnable = getPathId() + "/enable";
		pathDisable = getPathId() + "/disable";
	}

	@Override
	public DTO disable(long id) throws EntityNotFoundException {
		UriComponents uri = constructUriBuilder().path(pathDisable).build();

		DTO dto = null;
		try {
			dto = getRestTemplate().exchange(uri.toUriString(), HttpMethod.PUT, null,
					getDtoType(), id).getBody();
		} catch (HttpClientErrorException e) {
			LOGGER.debug("Catched exception {}", e);
			
			handleHttpClientErrorException(e);
		}

		return dto;
	}

	@Override
	public DTO enable(long id) throws EntityNotFoundException {
		UriComponents uri = constructUriBuilder().path(pathEnable).build();

		DTO dto = null;
		try {
			dto = getRestTemplate().exchange(uri.toUriString(), HttpMethod.PUT, null,
					getDtoType(), id).getBody();
		} catch (HttpClientErrorException e) {
			LOGGER.debug("Catched exception {}", e);
			
			handleHttpClientErrorException(e);
		}

		return dto;
	}

}