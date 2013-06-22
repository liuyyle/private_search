/**
 * This is a generic dao interface including CRUD operations
 */
package com.topsoft.ontology.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 * @date 2010-11-20
 */
public interface GenericDao<T, PK extends Serializable> {

	/** Persist the newInstance object into database */
	T create(T newInstance);
	
	/** Persist the newList into database */
	List<T> createList(List<T> newList);

	/**
	 * Retrieve an object that was previously persisted to the database
	 * using the indicated id as primary key
	 */
	T read(PK id);
	/**
	 * Retrieve an object that was previously persisted to the database
	 * using the name as key word 
	 */
	T read(final String name);	
	
	/**
	 * Retrieve all object with conditionSql
	 */
	List<T> readAll(final String condition);

	/** Save changes made to a persistent object. */
	void update(T transientObject);

	/** Remove an object from persistent storage in the database */
	void delete(T persistentObject);
}
