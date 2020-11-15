package com.javaws.threads.repository;


public interface KeysRepository {

	Object create(KeyItem keyItem) throws RepositoryException;
}
