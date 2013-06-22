/**
 * 
 */
package com.topsoft.ontology.entity;

import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.topsoft.ontology.OntologyService;
import com.topsoft.ontology.entity.SingleOntologyNode;

/**
 * @author Hanbing
 * @date 2010-12-21
 * add more test cases
 * 
 * @author yanyong
 *
 */
public class OntologyNodeTest extends TestCase {
	private OntologyService service;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service = OntologyService.getInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		service = null;
	}

	/**
	 * Test method for {@link com.topsoft.ontology.entity.OntologyNode#getDirectParentNodes()}.
	 */
	@Test
	public void testGetDirectParentNodes() {		
		SingleOntologyNode node = (SingleOntologyNode)service.lookup("entity");
		List<SingleOntologyNode> parents = node.getDirectParentNodes();
		assertNotNull(parents);
		assertTrue(parents.size() == 1);
		assertEquals("any", parents.get(0).getName());
		
		node = (SingleOntologyNode)service.lookup("facet");
		parents = node.getDirectParentNodes();
		assertNotNull(parents);
		assertTrue(parents.size() == 1);
		assertEquals("any", parents.get(0).getName());			
	}

	/**
	 * Test method for {@link com.topsoft.ontology.entity.OntologyNode#getAllParentNodes()}.
	 */
	@Test
	public void testGetAllParentNodes() {		
		SingleOntologyNode node = (SingleOntologyNode)service.lookup("entity");
		List<SingleOntologyNode> parents = node.getAllParentNodes();
		assertNotNull(parents);
		assertTrue(parents.size() == 1);
		assertEquals("any", parents.get(0).getName());
		
		node = (SingleOntologyNode)service.lookup("facet");
		parents = node.getAllParentNodes();
		assertNotNull(parents);
		assertTrue(parents.size() == 1);
		assertEquals("any", parents.get(0).getName());		

		System.out.println("testGetAllParentNodes...");	
		SingleOntologyNode sourceNode = (SingleOntologyNode)service.lookup("car");
		List<SingleOntologyNode> nodeList = sourceNode.getAllParentNodes();
		for(SingleOntologyNode n : nodeList) {
			System.out.println("node name = " +n.getName()+", nodeType = "+ n.getContext());
		}
	}

	/**
	 * Test method for {@link com.topsoft.ontology.entity.OntologyNode#getDirectChildrenNodes()}.
	 */
	@Test
	public void testGetDirectChildrenNodes() {
		SingleOntologyNode node = (SingleOntologyNode)service.lookup("entity");
		List<SingleOntologyNode> children = node.getDirectChildrenNodes();
		assertNotNull(children);
		assertTrue(children.size() > 0);
	}

	/**
	 * Test method for {@link com.topsoft.ontology.entity.OntologyNode#getAllChildrenNodes()}.
	 */
	@Test
	public void testGetAllChildrenNodes() {
		SingleOntologyNode node = (SingleOntologyNode)service.lookup("entity");
		List<SingleOntologyNode> children = node.getAllChildrenNodes();
		assertNotNull(children);
		assertTrue(children.size() > 0);
	}

	/**
	 * Test method for {@link com.topsoft.ontology.entity.OntologyNode#getSimilarNodes()}.
	 */
	@Test
	public void testGetSimilarNodes() {
		System.out.println("testGetSimilarNodes...");
	}
	
	/**
	 * Test method for {@link com.topsoft.ontology.entity.OntologyNode#getSiblingNodes()}.
	 */
	@Test
	public void testGetSiblingNodes() {
		SingleOntologyNode node = (SingleOntologyNode)service.lookup("facet");
		List<SingleOntologyNode> sibList = node.getSiblingNodes();
		assertNotNull(sibList);
		assertTrue(sibList.size() > 0);
		assertNotNull(sibList.get(0));
		
		node = (SingleOntologyNode)service.lookup("entity");
		sibList = node.getSiblingNodes();
		assertNotNull(sibList);
		assertTrue(sibList.size() > 0);
		assertNotNull(sibList.get(0));
	}

	/**
	 * Test method for {@link com.announcemedia.ontology.entity.OntologyNode#getSynonyms()}.
	 */
/*	@Test
	public void testGetSynonyms() {
		System.out.println("testGetSynonyms...");
		OntologyNode node = service.lookup("kid");
		List<String> synonyms = node.getSynonyms();
		for(String synonym : synonyms) {
			System.out.println(node.getName()+" synonyms = " +synonym);
		}
	}
*/
	/**
	 * Test method for {@link com.announcemedia.ontology.entity.OntologyNode#getExcludingWords()}.
	 */
/*	@Test
	public void testGetExcludingWords() {
		System.out.println("testGetExcludingWords...");
		OntologyNode node = service.lookup("kid");
		List<String> excludingWords = node.getExcludingWords();
		for(String excludingWord : excludingWords) {
			System.out.println(node.getName()+" excludingWord = " + excludingWord);
		}
	}
*/
}
