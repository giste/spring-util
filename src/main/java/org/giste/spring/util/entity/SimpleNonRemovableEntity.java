package org.giste.spring.util.entity;
import javax.persistence.Entity;

import org.giste.spring.util.entity.NonRemovableEntity;

@Entity
public class SimpleNonRemovableEntity extends NonRemovableEntity {

	private static final long serialVersionUID = 4570558194177329729L;

	public SimpleNonRemovableEntity() {
		super();
	}

	public SimpleNonRemovableEntity(Long id, boolean enabled) {
		super(id, enabled);
	}

}
