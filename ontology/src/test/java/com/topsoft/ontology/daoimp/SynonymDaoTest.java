/**
 * 
 */
package com.topsoft.ontology.daoimp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.topsoft.ontology.daoimp.SynonymDao;
import com.topsoft.ontology.entity.OntologySynonym;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 * @date 2010-11-20
 */
public class SynonymDaoTest {
	
	private SynonymDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dao = new SynonymDao();
	}

	/**
	 * Test method for {@link com.topsoft.ontology.daoimp.SynonymDao#ifExist(com.topsoft.ontology.entity.OntologySynonym)}.
	 */
	@Test
	public void testIfExist() {
		String synonym="kid";
		String canonicalName="child";
		String context="any.facet.demographics";
		OntologySynonym syn= new OntologySynonym();
		syn.setSynonym(synonym);
		syn.setCanonicalName(canonicalName);
		syn.setContext(context);
		OntologySynonym synExist=dao.ifExist(syn);
		assertNotNull(synExist);
		System.out.println("testIfExist synExist id="+synExist.getId());
		
		syn.setSynonym(" kid ");
		synExist=dao.ifExist(syn);
		assertNull(synExist);
		syn.setSynonym(synonym);
		
		syn.setContext("any.facet.price");
		synExist=dao.ifExist(syn);
		assertNull(synExist);
	}
}
