/**
 * 
 */
package com.topsoft.ontology.daoimp;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.topsoft.ontology.daoimp.NodeDao;
import com.topsoft.ontology.entity.SingleOntologyNode;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class NodeDaoTest {

	private NodeDao dao;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dao = new NodeDao();
	}
	
	@Test
	public void testCRUD() {
		final String name = "lhb' test";
		SingleOntologyNode test = dao.read(name);
		if (test == null) {
			SingleOntologyNode o = new SingleOntologyNode();
			o.setName(name);
			test = dao.create(o);
		}
		System.out.println(" NodeDao test read and create is OK, id="
				+ test.getId());
		final String testName = "test's test";
		test.setName(testName);
		dao.update(test);
		System.out.println(" NodeDao test update is OK");
		test = dao.read(testName);
		assertNotNull(test);
		assertTrue(testName.equals(test.getName()));
		assertTrue(testName.equals(test.getDescription()));
		assertTrue(testName.equals(test.getDisplayName()));
		dao.delete(test);
		System.out.println(" NodeDao test delete is OK");
		test = dao.read(testName);
		assertNull(test);

	}

}
