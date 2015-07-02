package no.storebrand.repository.hashmap;

import no.storebrand.repository.Specification;

public interface HashMapSpecification<T> extends Specification<T> {

	boolean matches(T entity);

}
