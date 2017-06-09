package org.giste.spring.util.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.error.dto.RestErrorDto;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.NonRemovableDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base implementation class for services that performs CRUDE operations with a REST server.
 * Subclasses have to implement the following methods:
 * <ul>
 * <li>{@link #getDtoType()} to provide the type of the DTO.</li>
 * <li>{@link #getArrayType()} to provide the type of a DTO array.</li>
 * <li>{@link #getBasePath()} to provide the base path of actions at REST server for the
 * entity managed by this service.</li>
 * <li>{@link #handleHttpStatusConflict(RestErrorDto)} to handle responses from REST server with
 * status CONFLICT.</li>
 * </ul>
 *  
 * @author Giste
 *
 * @param <DTO> The DTO of the entity to be managed by this service.
 */
public abstract class CrudeRestServiceImpl<DTO extends NonRemovableDto> implements CrudeRestService<DTO> {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private RestTemplate restTemplate;
	private RestProperties restPropertiesImpl;

	private String pathBase;
	private String pathId;
	private String pathEnable;
	private String pathDisable;

	/**
	 * Construct a new <code>CrudeRestserviceImpl</code>.
	 * 
	 * @param restTemplate <code>RestTemplate</code> used for communicating with
	 *            REST service.
	 * @param restPropertiesImpl <code>RestPropertiesImpl</code> with data for
	 *            connecting to REST service.
	 */
	public CrudeRestServiceImpl(RestTemplate restTemplate, RestProperties restPropertiesImpl) {
		this.restTemplate = restTemplate;
		this.restPropertiesImpl = restPropertiesImpl;
		constructPaths();
	}

	private void constructPaths() {
		pathBase = getBasePath();
		pathId = pathBase + "/{id}";
		pathEnable = pathId + "/enable";
		pathDisable = pathId + "/disable";
	}

	/**
	 * Constructs an <code>UriComponentsBuilder</code> with the properties to
	 * communicate with the REST service. Scheme, host, port and base path for
	 * all requests are read from <code>RestPropertiesImpl</code>. Only the path for
	 * the entity should be added.
	 * 
	 * @return The <code>UriComponentsBuilder</code> with the properties to
	 *         communicate with the REST service.
	 */
	protected UriComponentsBuilder constructUriBuilder() {
		return UriComponentsBuilder.newInstance()
				.scheme(restPropertiesImpl.getScheme())
				.host(restPropertiesImpl.getHost())
				.port(restPropertiesImpl.getPort())
				.path(restPropertiesImpl.getPath());
	}

	/**
	 * Gets the base path for the entity managed by this service.
	 * 
	 * @return The base path for the entity managed by this service.
	 */
	protected String getPathBase() {
		return pathBase;
	}

	/**
	 * Gets the path for the disable action.
	 * 
	 * @return The path for the disable action.
	 */
	protected String getPathDisable() {
		return pathDisable;
	}

	/**
	 * Gets the path for the enable action.
	 * 
	 * @return The path for the enable action.
	 */
	protected String getPathEnable() {
		return pathEnable;
	}

	/**
	 * Gets the path for the actions on a single entity.
	 * 
	 * @return The path for the actions on a single entity.
	 */
	protected String getPathId() {
		return pathId;
	}

	/**
	 * Gets the <code>RestPropertiesImpl</code> with data for accessing the REST
	 * service.
	 * 
	 * @return The <code>RestPropertiesImpl</code> with data for accessing the REST
	 *         service.
	 */
	protected RestProperties getRestProperties() {
		return restPropertiesImpl;
	}

	/**
	 * Gets the <code>RestTemplate</code> used to communicate with REST service.
	 * 
	 * @return The <code>RestTemplate</code> used to communicate with REST
	 *         service.
	 */
	protected RestTemplate getRestTemplate() {
		return restTemplate;
	}

	@Override
	public DTO create(DTO club) {
		UriComponents uri = constructUriBuilder().path(pathBase).build();

		DTO dto = null;

		try {
			dto = restTemplate.postForObject(uri.toUriString(), club, getDtoType());
		} catch (HttpClientErrorException e) {
			handleHttpClientErrorException(e);
		}

		return dto;
	}

	@Override
	public DTO disable(long id) throws EntityNotFoundException {
		UriComponents uri = constructUriBuilder().path(pathDisable).build();

		DTO dto = null;
		try {
			dto = restTemplate.exchange(uri.toUriString(), HttpMethod.PUT, null,
					getDtoType(), id).getBody();
		} catch (HttpClientErrorException e) {
			handleHttpClientErrorException(e);
		}

		return dto;
	}

	@Override
	public DTO enable(long id) throws EntityNotFoundException {
		UriComponents uri = constructUriBuilder().path(pathEnable).build();

		DTO dto = null;
		try {
			dto = restTemplate.exchange(uri.toUriString(), HttpMethod.PUT, null,
					getDtoType(), id).getBody();
		} catch (HttpClientErrorException e) {
			handleHttpClientErrorException(e);
		}

		return dto;
	}

	@Override
	public List<DTO> findAll() {
		UriComponents uri = constructUriBuilder().path(getBasePath()).build();

		return Arrays.stream(restTemplate.getForObject(uri.toUriString(), getArrayType()))
				.collect(Collectors.toList());
	}

	@Override
	public DTO findById(long id) throws EntityNotFoundException {
		UriComponents uri = constructUriBuilder().path(pathId).build();

		DTO dto = null;

		try {
			dto = restTemplate.getForObject(uri.toUriString(), getDtoType(), id);
		} catch (HttpClientErrorException e) {
			handleHttpClientErrorException(e);
		}

		return dto;
	}

	@Override
	public DTO update(DTO club) throws EntityNotFoundException {
		UriComponents uri = constructUriBuilder().path(pathId).build();

		DTO dto = null;

		try {
			dto = restTemplate.exchange(uri.toUriString(), HttpMethod.PUT, new HttpEntity<>(club),
					getDtoType(), club.getId()).getBody();
		} catch (HttpClientErrorException e) {
			handleHttpClientErrorException(e);
		}

		return dto;
	}

	private void handleHttpClientErrorException(HttpClientErrorException hcee) {
		ObjectMapper objectMapper = new ObjectMapper();
		RestErrorDto error;

		try {
			error = objectMapper.readValue(hcee.getResponseBodyAsByteArray(), RestErrorDto.class);
			LOGGER.debug("RestError={}", error);
		} catch (Exception e) {
			// No RestErrorDto inside HttpClientErrorException.
			// Throw original exception.
			LOGGER.debug("No RestErrorDto, throwing original HttpClientErrorException");
			throw hcee;
		}

		switch (error.getStatus()) {
		case NOT_FOUND:
			// Throw EntityNotFoundException with message from RestErrorDto.
			LOGGER.debug("Throwing EntityNotFoundException");
			throw new EntityNotFoundException(error.getMessage());
		case CONFLICT:
			// Allow subclasses to treat this error.
			handleHttpStatusConflict(error);
			break;
		default:
			LOGGER.debug("Status different from NOT_FOUND, throwing original HttpClientErrorException");
			throw hcee;
		}
	}

	/**
	 * Gets the type of the entity to be managed by this service. Subclasses has
	 * to return <code>Entity.class</code>.
	 * 
	 * @return The type of the entity to be managed by this service.
	 */
	protected abstract Class<DTO[]> getArrayType();

	/**
	 * Gets the type of an array of entities to be managed by this service.
	 * Subclasses has to return <code>Entity[].class</code>.
	 * 
	 * @return The type of an array of entities to be managed by this service.
	 */
	protected abstract Class<DTO> getDtoType();

	/**
	 * Gets the base path for the entity to be managed.
	 * 
	 * @return The base path for the entity to be managed.
	 */
	protected abstract String getBasePath();

	/**
	 * Handles an error with HTTP status of CONFLICT. Usually it will be called
	 * due to duplicated properties. Subclasses have to handle it.
	 * 
	 * @param error <code>RestErrorDto</code> with the error returned by REST
	 *            service.
	 */
	protected abstract void handleHttpStatusConflict(RestErrorDto error);

}