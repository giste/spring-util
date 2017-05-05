package org.giste.spring.util.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Base entity for applications. Base entity has a Long identifier.
 * 
 * @author Giste
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 7518450465957129917L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Creates a BaseEntity without identifier.
	 */
	public BaseEntity() {

	}

	/**
	 * Creates a Base Entity with a given identifier.
	 * 
	 * @param id The identifier for this entity.
	 */
	public BaseEntity(Long id) {
		this.id = id;
	}

	/**
	 * Gets the identifier of this entity.
	 * 
	 * @return The identifier.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the identifier for this entity.
	 * 
	 * @param id The identifier for this entity.
	 */
	public void setId(Long id) {
		this.id = id;
	}
}
