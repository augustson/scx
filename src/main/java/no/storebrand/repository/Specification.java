package no.storebrand.repository;

public interface Specification<T> {

	Class<T> getEntityClass();

}
