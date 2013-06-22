/**
 * 
 */
package com.topsoft.ontology.dictionary;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.topsoft.ontology.dictionary.OntologySynonymDictionary;
import com.topsoft.ontology.dictionary.SNCWrods;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class OntologySynonymDictionaryTest {
	
	private OntologySynonymDictionary dictionary;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dictionary = OntologySynonymDictionary.getInstance();
	}

	/**
	 * Test method for {@link com.topsoft.ontology.dictionary.OntologySynonymDictionary#getCanonicalTerm(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetCanonicalTermStringString() {
		/*String term="big apple";
		String context="any.facet.location";
		String canonical=dictionary.getCanonicalTerm(term,context);
		assertTrue("NY".equalsIgnoreCase(canonical));*/
	}

	/**
	 * Test method for {@link com.topsoft.ontology.dictionary.OntologySynonymDictionary#getCanonicalTerm(java.lang.String)}.
	 */
	@Test
	public void testGetCanonicalTermString() {
		/*String term="babys";
		String canonical=dictionary.getCanonicalTerm(term, null);
		assertTrue("baby".equalsIgnoreCase(canonical));*/
	}

	/**
	 * Test method for {@link com.topsoft.ontology.dictionary.OntologySynonymDictionary#getAllSynonyms(java.lang.String, java.lang.String, double)}.
	 */
	@Test
	public void testGetAllSynonyms() {
		/*String term = "big apple";		
		List<SNCWrods> list = dictionary.getAllSynonyms(term, null);
		for (SNCWrods words : list) {
			System.out.println(term +"'s synonym is "+ words.getWords());
		}		
		term = "kid";		
		list = dictionary.getAllSynonyms(term, null);
		for (SNCWrods words : list) {
			System.out.println(term +"'s synonym is "+ words.getWords());
		}*/
	}

	/**
	 * Test method for {@link com.topsoft.ontology.dictionary.OntologySynonymDictionary#getExcludingWords(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetExcludingWords() {
		/*String term = "big apple";		
		List<SNCWrods> list = dictionary.getExcludingWords(term, null);		
		for(SNCWrods words :list){
			System.out.println(term+" is oppasite with "+words.getWords());
		}
		term = "rental";		
		list = dictionary.getExcludingWords(term, null);		
		for(SNCWrods words :list){
			System.out.println(term+" is oppasite with "+words.getWords());
		}*/
	}

}
