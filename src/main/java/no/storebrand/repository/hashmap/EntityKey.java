package no.storebrand.repository.hashmap;

import java.io.Serializable;

public class EntityKey implements Serializable {

	private static final long serialVersionUID = 1L;

	private Serializable id;
	private Class<?> entityType;

	public EntityKey(Serializable id, Class<?> entityType) {
		this.id = id;
		this.entityType = entityType;
	}

	@Override
	public String toString() {
		return "EntityKey<" + entityType.getSimpleName() + "," + id + ">";
	}

	@Override
	public int hashCode() {
		return id.hashCode() ^ entityType.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof EntityKey))
			return false;
		EntityKey other = (EntityKey) obj;
		return nullSafeEquals(id, other.id) && nullSafeEquals(entityType, other.entityType);
	}

	private static <T> boolean nullSafeEquals(T a, T b) {
		return a != null ? a.equals(b) : (b == null);
	}
}