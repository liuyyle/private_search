/**
 * 
 */
package com.topsoft.ontology;

import java.util.List;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.topsoft.ontology.OntologyService;
import com.topsoft.ontology.QueryParseResult;
import com.topsoft.ontology.dictionary.RelationNode;
import com.topsoft.ontology.entity.SingleOntologyNode;

/**
 * @author yanyong
 *
 */
public class OntologyServiceTest extends TestCase {
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
	 * Test method for {@link com.topsoft.ontology.OntologyService#lookup(java.lang.String)}.
	 */
	@Test
	public void testLookup() {
		System.out.println("testLookup...");
		SingleOntologyNode node = (SingleOntologyNode)service.lookup("entity");
		assertNotNull(node);
		System.out.println("node name = " +node.getName()+", nodeType = "+ node.getContext());
	}

	/**
	 * Test method for {@link com.topsoft.ontology.OntologyService#parseQueryString(java.lang.String)}.
	 */
	@Test
	public void testParseQueryString() {
		System.out.println("testParseQueryString...");
		QueryParseResult pr = service.parseQueryString("used car very cheap");
		assertTrue(pr.getFeatures().size() == 1);
		assertTrue(pr.getEntities().size() == 1);
		System.out.println(pr.getFeatures());
		System.out.println(pr.getEntities());
	}

	/**
	 * Test method for {@link com.topsoft.ontology.OntologyService#isTypeOf(com.topsoft.ontology.entity.OntologyNode, com.topsoft.ontology.entity.OntologyNode)}.
	 */
	@Test
	public void testIsTypeOf() {
		System.out.println("testIsTypeOf...");
		SingleOntologyNode sourceNode = (SingleOntologyNode)service.lookup("used car");
		SingleOntologyNode targetNode = (SingleOntologyNode)service.lookup("entity");
		assertTrue(service.isTypeOf(sourceNode, targetNode));
	}

	/**
	 * Test method for {@link com.topsoft.ontology.OntologyService#isSubtypeOf(com.topsoft.ontology.entity.OntologyNode, com.topsoft.ontology.entity.OntologyNode)}.
	 */
	@Test
	public void testIsSubtypeOf() {
		System.out.println("testIsSubtypeOf...");
		SingleOntologyNode sourceNode = (SingleOntologyNode)service.lookup("used car");
		SingleOntologyNode targetNode = (SingleOntologyNode)service.lookup("entity");
		assertTrue(service.isSubtypeOf(sourceNode, targetNode));
	}
	
	/**
	 * Test method for {@link com.topsoft.ontology.OntologyService#getForwardRelatedNodes(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetForwardRelatedNodes() {
		System.out.println("testGetForwardRelatedNodes...");
		SingleOntologyNode node = (SingleOntologyNode)service.lookup("price");
		List<RelationNode>nodes = OntologyService.getInstance().getForwardRelatedNodes(node,"value_of", 0);
		System.out.println(nodes);
	}	
	
	/**
	 * Test method for {@link com.topsoft.ontology.OntologyService#getBackardRelatedNodes(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetBackwardRelatedNodes() {
		System.out.println("testGetBackwardRelatedNodes...");
		SingleOntologyNode node = (SingleOntologyNode)service.lookup("entity");
		List<RelationNode>nodes = OntologyService.getInstance().getBackwardRelatedNodes(node,"subtype_of", 0);
		System.out.println(nodes);
	}	
}
