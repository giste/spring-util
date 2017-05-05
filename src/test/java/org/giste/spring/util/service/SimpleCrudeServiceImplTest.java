package org.giste.spring.util.service;

import org.giste.spring.util.dto.SimpleNonRemovableDto;
import org.giste.spring.util.entity.SimpleNonRemovableEntity;
import org.giste.spring.util.repository.CrudeRepository;
import org.giste.spring.util.repository.SimpleCrudeRepository;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class SimpleCrudeServiceImplTest extends CrudeServiceImplTest<SimpleNonRemovableDto, SimpleNonRemovableEntity> {

	@MockBean
	private SimpleCrudeRepository repository;
	
	@Override
	protected CrudeRepository<SimpleNonRemovableEntity> getRepositoryMock() {
		return repository;
	}

	@Override
	protected CrudeServiceImpl<SimpleNonRemovableDto, SimpleNonRemovableEntity> getService() {
		return new SimpleCrudeServiceImpl(repository);
	}

	@Override
	protected SimpleNonRemovableEntity getNewEntity() {
		return new SimpleNonRemovableEntity(1L, true);
	}

}
