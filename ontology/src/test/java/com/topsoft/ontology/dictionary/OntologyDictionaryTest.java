/**
 * 
 */
package com.topsoft.ontology.dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.topsoft.ontology.daoimp.NodeDao;
import com.topsoft.ontology.daoimp.RelationshipDao;
import com.topsoft.ontology.daoimp.RelationshipTypeDao;
import com.topsoft.ontology.dictionary.OntologyDictionary;
import com.topsoft.ontology.dictionary.RelationNode;
import com.topsoft.ontology.entity.OntologyNode;
import com.topsoft.ontology.entity.OntologyRelationship;
import com.topsoft.ontology.entity.OntologyRelationshipType;
import com.topsoft.ontology.entity.SingleOntologyNode;
import com.topsoft.ontology.util.OntologyConstant;

import junit.framework.TestCase;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class OntologyDictionaryTest extends TestCase {
	private OntologyDictionary dictionary;	

	@Before
	public void setUp() {
		dictionary=OntologyDictionary.getInstance();
	}
	
	@Test
	public void testNodeMap() {
		Map<String, OntologyNode> nodeMap = dictionary.getNodeMap();
		assertNotNull(nodeMap);
		OntologyNode any = nodeMap.get(OntologyConstant.ANY);
		assertNotNull(any);
		assertEquals(any.getName(), OntologyConstant.ANY);
		SingleOntologyNode facet = OntologyConstant.NODE_FACET;
		assertNotNull(facet);
		assertEquals(facet.getContext(), OntologyConstant.ANY);
		SingleOntologyNode feature = OntologyConstant.NODE_FEATURE;
		assertNotNull(feature);
		assertEquals(feature.getContext(), "any.facet");			
	}

	@Test
	public void testForwardGraph() {
		Map<OntologyNode, ArrayList<RelationNode>> forwardGraph = dictionary
				.getForwardGraph();
		List<RelationNode> facetParentList = forwardGraph.get(OntologyConstant.NODE_FACET);		
		assertTrue(facetParentList.size() == 1);
	}
	
	@Test
	public void testBackwardGraph() {
		NodeDao nodeDao = new NodeDao();
		RelationshipDao relationDao = new RelationshipDao();
		RelationshipTypeDao relationTypeDao = new RelationshipTypeDao();
		SingleOntologyNode root = nodeDao.read(OntologyConstant.ANY);
		OntologyRelationshipType subtype_of = relationTypeDao.read(OntologyConstant.RELATIONSHIP_SUBTYPE_OF);		
		Map<OntologyNode, ArrayList<RelationNode>> backwardGraph = dictionary.getBackwardGraph();
		final String prefix = " where target_node_id=";
		final String suffix = " and relationship_type_id=" + subtype_of.getId();
		// root
		String condition = prefix + root.getId() + suffix;
		List<OntologyRelationship> rlist = relationDao.readAll(condition);
		List<RelationNode> rootChilrenList = backwardGraph.get(dictionary.getNodeMap().get(OntologyConstant.ANY));
		assertTrue(rootChilrenList.size() == rlist.size());
		System.out.println("node any has "+rlist.size()+" 'subtype_of' children");

	}
}
