package org.giste.spring.util.service;

import org.giste.spring.util.dto.SimpleNonRemovableDto;
import org.giste.spring.util.entity.SimpleNonRemovableEntity;
import org.giste.spring.util.repository.CrudeRepository;
import org.giste.spring.util.service.exception.DuplicatedPropertyException;
import org.giste.spring.util.service.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SimpleCrudeServiceImpl extends CrudeServiceImpl<SimpleNonRemovableDto, SimpleNonRemovableEntity>
		implements SimpleCrudeService {

	public SimpleCrudeServiceImpl(CrudeRepository<SimpleNonRemovableEntity> repository) {
		super(repository);
	}

	@Override
	protected SimpleNonRemovableEntity getEntityFromDto(SimpleNonRemovableDto dto) {
		return new SimpleNonRemovableEntity(dto.getId(), dto.isEnabled());
	}

	@Override
	protected SimpleNonRemovableDto getDtoFromEntity(SimpleNonRemovableEntity entity) {
		return new SimpleNonRemovableDto(entity.getId(), entity.isEnabled());
	}

	@Override
	protected SimpleNonRemovableEntity updateEntityFromDto(SimpleNonRemovableEntity entity, SimpleNonRemovableDto dto) {
		entity.setId(dto.getId());
		entity.setEnabled(dto.isEnabled());
		
		return entity;
	}

	@Override
	protected EntityNotFoundException getEntityNotFoundException(Long id) {
		return new EntityNotFoundException(id, "Code", "Message", "Developer info");
	}

	@Override
	protected void checkDuplicatedProperties(SimpleNonRemovableDto dto) throws DuplicatedPropertyException {
		
	}

}
