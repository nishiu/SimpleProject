package com.fm.commons.domain;

import java.io.Serializable;

/**
 * 
 * @author L
 *
 */
public abstract class BaseEntity implements Serializable, Comparable<BaseEntity> {

	private static final long serialVersionUID = 1L;

	private Long entityId;

	public BaseEntity() {
	}

	@Override
	public int compareTo(BaseEntity o) {
		if (o == null)
			return -1;
		if (o.getId() == null)
			return -1;
		if (getId() == null)
			return 1;
		return this.getId().compareTo(o.getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (entityId == null)
			return false;
		if (!getClass().isInstance(obj))
			return false;
		BaseEntity entity = (BaseEntity) obj;
		return entityId.equals(entity.getId());
	}

	@Override
	public int hashCode() {
		if (entityId != null) {
			int result = 17;
			result = 37 * result + entityId.hashCode();
			return result;
		}
		return super.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getId() + "]";
	}

	public Long getId() {
		return entityId;
	}

	public void setId(Long id) {
		this.entityId = id;
	}

}