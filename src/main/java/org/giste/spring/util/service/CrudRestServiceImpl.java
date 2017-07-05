package org.giste.spring.util.service;

import org.giste.spring.util.config.RestProperties;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.giste.util.dto.BaseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

public abstract class CrudRestServiceImpl<DTO extends BaseDto> extends BaseRestServiceImpl<DTO>
		implements CrudRestService<DTO> {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public CrudRestServiceImpl(RestTemplate restTemplate, RestProperties restPropertiesImpl) {
		super(restTemplate, restPropertiesImpl);
	}

	@Override
	public void delete(Long id) throws EntityNotFoundException {
		UriComponents uri = constructUriBuilder().path(getPathId()).build();

		try {
			getRestTemplate().delete(uri.toUriString(), id);
		} catch (HttpClientErrorException e) {
			LOGGER.debug("Catched exception {}", e);

			handleHttpClientErrorException(e);
		}
	}

}
