package no.storebrand.repository.hashmap;

import static no.storebrand.repository.ObjectUtils.cloneEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EmbeddedId;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import no.storebrand.repository.ObjectUtils;
import no.storebrand.repository.Repository;
import no.storebrand.repository.RepositoryException;
import no.storebrand.repository.Specification;

public class HashMapRepository implements Repository, Serializable {

	private static final long serialVersionUID = 1L;

	private final Map<EntityKey, Object> store = new HashMap<EntityKey, Object>();

	private int intIdSequence = 0;
	private long longIdSequence = 0;

	@Override
	@SuppressWarnings("unchecked")
	public <T> List<T> findAll(Class<T> entityClass) {
		ArrayList<T> result = new ArrayList<T>();
		for (Object o : store.values()) {
			if (entityClass.isAssignableFrom(o.getClass())) {
				result.add(ObjectUtils.cloneEntity((T) o));
			}
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T retrieve(Class<T> entityClass, Serializable id) {
		return cloneEntity((T) store.get(new EntityKey(id, entityClass)));
	}

	@Override
	public Serializable update(Object entity) {
		return save(entity);
	}

	@Override
	public Serializable save(Object entity) {
		Serializable id = getOrGenerateId(entity);
		store.put(new EntityKey(id, entity.getClass()), entity);
		Field idField = ObjectUtils.findFieldWithAnnotation(entity.getClass(), Id.class);
		setId(entity, id, idField);
		return id;
	}

	private void setId(Object entity, Serializable id, Field idField) {
		try {
			idField.set(entity, id);
		} catch (IllegalArgumentException e) {
			throw new RepositoryException("Unable to set entity ID", e);
		} catch (IllegalAccessException e) {
			throw new RepositoryException("Unable to set entity ID", e);
		}
	}

	@Override
	public void saveAll(Object... entities) {
		for (Object entity : entities) {
			save(entity);
		}
	}

	@Override
	public <T> void saveAll(Collection<T> entities) {
		for (Object entity : entities) {
			save(entity);
		}
	}

	@Override
	public void deleteAll(Class<?> entityType) {
		for (Object entity : findAll(entityType)) {
			delete(entity);
		}
	}

	@Override
	public void delete(Class<?> entityClass, Serializable id) {
		store.remove(new EntityKey(id, entityClass));
	}

	@Override
	public void delete(Object entity) {
		delete(entity.getClass(), (Serializable) ObjectUtils.fieldValue(findIdField(entity), entity));
	}

	@Override
	public void delete(Specification<?> specification) {
		for (Object entity : find(specification)) {
			delete(entity);
		}
	}

	@Override
	public <T> T retrieve(Specification<T> specification) {
		HashMapSpecification<T> hashMapSpecification = getSpecification(specification);
		for (Entry<EntityKey, Object> entry : store.entrySet()) {
			@SuppressWarnings("unchecked")
			T entity = (T) entry.getValue();
			if (hashMapSpecification.matches(entity)) {
				return cloneEntity(entity);
			}
		}
		return null;
	}

	@Override
	public <T> List<T> find(Specification<T> specification) {
		HashMapSpecification<T> hashMapSpecification = getSpecification(specification);
		List<T> entities = new ArrayList<T>();
		for (Entry<EntityKey, Object> entry : store.entrySet()) {
			@SuppressWarnings("unchecked")
			T entity = (T) entry.getValue();
			if (hashMapSpecification.matches(entity)) {
				entities.add(cloneEntity(entity));
			}
		}
		return entities;
	}

	@Override
	public long rowCount(Specification<?> specification) {
		return find(specification).size();
	}

	private <T> HashMapSpecification<T> getSpecification(Specification<T> specification) {
		if (!(specification instanceof HashMapSpecification)) {
			throw new RepositoryException(specification.getClass() + " isn't instance of " + HashMapSpecification.class);
		}
		return (HashMapSpecification<T>) specification;
	}

	private Serializable getOrGenerateId(Object entity) {
		Field idField = findIdField(entity);
		Serializable id = ObjectUtils.cloneSerializable((Serializable) ObjectUtils.fieldValue(idField, entity));

		if (id != null) {
			return id;
		} else if (idField.getAnnotation(GeneratedValue.class) != null) {
			try {
				if (idField.getType().isAssignableFrom(Integer.class)) {
					int intId = intIdSequence++;
					idField.set(entity, intId);
					return intId;
				} else {
					long longId = longIdSequence++;
					idField.set(entity, longId);
					return longId;
				}
			} catch (IllegalAccessException e) {
				throw new RepositoryException(idField + " should be accessible", e);
			}
		} else {
			throw new IllegalArgumentException("Assigned id " + idField
					+ " can't be null if not annotated with @GeneratedValue");
		}
	}

	private Field findIdField(Object entity) {
		Field idField = ObjectUtils.findFieldWithAnnotation(entity.getClass(), Id.class);
		if (idField == null) {
			idField = ObjectUtils.findFieldWithAnnotation(entity.getClass(), EmbeddedId.class);
		}
		if (idField == null) {
			throw new IllegalArgumentException(entity.getClass() + " must have field with  @Id or @EmbeddedId");
		}
		return idField;
	}

	@Override
	public Serializable saveOrUpdate(Object entity) {
		Serializable id = getOrGenerateId(entity);
		store.put(new EntityKey(id, entity.getClass()), entity);
		return id;
	}

	public static HashMapRepository load(File file) {
		ObjectInputStream in = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
				return new HashMapRepository();
			}
			in = new ObjectInputStream(new FileInputStream(file));
			return (HashMapRepository) in.readObject();
		} catch (Exception e) {
			throw new RepositoryException("Unable to load data", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public void store(File file) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(this);
		} catch (Exception e) {

		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

}
