/**
 * 
 */
package com.topsoft.ontology;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.topsoft.ontology.entity.OntologyNode;

/**
 * @author yanyong
 *
 */
public class FeatureOntologyServiceTest {
    private FeatureOntologyService service;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		service = FeatureOntologyService.getInstance();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		service = null;
	}

	/**
	 * Test method for {@link com.topsoft.ontology.FeatureOntologyService#getMostLikelyEntityByFeatures(java.util.List)}.
	 */
	@Test
	public void testGetMostLikelyEntityByFeatures() {
		List<OntologyNode> features = new ArrayList<OntologyNode>();
		features.add(service.lookup("styling"));
		features.add(service.lookup("gas mileage"));
		features.add(service.lookup("ride"));
		//features.add(service.lookup("price"));
		//features.add(service.lookup("quality"));
		
		//features.add(service.lookup("comfort"));
		features.add(service.lookup("liner"));
		//features.add(service.lookup("leather"));
		features.add(service.lookup("weight"));
		//features.add(service.lookup("design"));
		
		assertTrue(service.getMostLikelyEntityByFeatures(features).getName().equals("used car"));
	}

	/**
	 * Test method for {@link com.topsoft.ontology.FeatureOntologyService#isFeature(java.lang.String)}.
	 */
	@Test
	public void testIsFeatureString() {
		assertTrue(service.isFeature("ride"));
		assertFalse(service.isFeature("xxxx"));
	}

	/**
	 * Test method for {@link com.topsoft.ontology.FeatureOntologyService#isFeature(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testIsFeatureStringString() {
		assertTrue(service.isFeature("ride", "used cars"));
		assertFalse(service.isFeature("ride", "motorsport accessory"));
		assertTrue(service.isFeature("leather", "used car"));
		assertTrue(service.isFeature("price", "used car"));
		assertTrue(service.isFeature("price", "motorsports accessory"));
	}

}
