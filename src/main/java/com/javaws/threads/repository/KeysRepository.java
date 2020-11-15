package com.javaws.threads.repository;


public interface KeysRepository {

	Integer create(KeyItem keyItem) throws RepositoryException;
}
