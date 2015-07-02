package no.storebrand.repository;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;

public class ObjectUtils {

	public static <T> T cloneEntity(T entity) {
		if (entity == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Class<T> entityType = (Class<T>) entity.getClass();
		try {
			Constructor<T> constructor = entityType.getDeclaredConstructor();
			constructor.setAccessible(true);
			T clone = constructor.newInstance();

			for (Field field : entityType.getDeclaredFields()) {
				if (!Modifier.isStatic(field.getModifiers())) {
					field.setAccessible(true);
					field.set(clone, field.get(entity));
				}
			}

			return clone;
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(entityType + " does not support cloning", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("setAccessible is called!", e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(entityType + " does not support cloning", e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(entityType + " must have default construtor", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(entityType + " does not support cloning", e);
		}
	}

	public static Field findFieldWithAnnotation(Class<?> entityType, Class<? extends Annotation> annotation) {
		for (Field field : entityType.getDeclaredFields()) {
			if (field.getAnnotation(annotation) != null) {
				field.setAccessible(true);
				return field;
			}
		}
		return null;
	}

	public static Object fieldValue(Field field, Object object) {
		field.setAccessible(true);
		try {
			return field.get(object);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(field + " is made accessible", e);
		}
	}

	public static Serializable cloneSerializable(Serializable source) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ObjectOutputStream oos;
		Serializable clone = null;

		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(source);
			oos.close();
			byte[] data = baos.toByteArray();
			ObjectInputStream iis = new ObjectInputStream(new ByteArrayInputStream(data));
			clone = (Serializable) iis.readObject();
		} catch (IOException e) {
			throw new RuntimeException("Object not cloneable", e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class not found", e);
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				// Ignored
			}
		}
		return clone;
	}

	public static <T> boolean nullSafeEquals(T a, T b) {
		return a != null ? a.equals(b) : b == null;
	}

	public static int nullSafeHashCode(Object... fields) {
		int result = 17;
		for (Object field : fields) {
			if (field != null) {
				result = 37 * result + field.hashCode();
			}
		}
		return result;
	}
}
