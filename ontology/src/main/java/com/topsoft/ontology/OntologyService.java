/**
 * OntologyService.java provide all interfaces which used to parse query term 
 * using ontology data
 */
package com.topsoft.ontology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.topsoft.ontology.util.OntologyConstant;
import com.topsoft.ontology.util.StringUtil;
import com.topsoft.ontology.dictionary.OntologyDictionary;
import com.topsoft.ontology.dictionary.OntologySynonymDictionary;
import com.topsoft.ontology.dictionary.RelationNode;
import com.topsoft.ontology.entity.OntologyNode;
import com.topsoft.ontology.entity.OntologyRelationshipType;
import com.topsoft.ontology.entity.SingleOntologyNode;
import com.topsoft.ontology.util.OntologyUtil;

/**
 * @author Yanyong
 * @time 9:50:43 PM Nov 4, 2010
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class OntologyService extends AbstractOntologyService {	
	private static Logger logger = Logger.getLogger(OntologyService.class);
	private static OntologyService instance = new OntologyService();
	
	public static OntologyService getInstance() {
		return instance;
	}

	protected OntologyService() {		
		OntologyDictionary.getInstance();
		OntologySynonymDictionary.getInstance();
	}

	/**
	 * check if a term is an ontology node, if true, return an OntologyNode object
	 * @param term
	 * @param domain, each entity has a specific mapping with domain, we use this param to get
	 * a proper node that match with this domain
	 * @return
	 */
	public OntologyNode lookup(String term, String domain) {
		return (OntologyNode)lookupValue(term, domain);
	}
	
	public OntologyNode lookup(String term) {
		return lookup(term, null);
	}
	
	/**
	 * parse all valid nodes in a query String
	 * @param queryStr
	 * @param domain, a specific domain,it will be used to deal with different type synonym
	 * @return
	 */
	public QueryParseResult parseQueryString(String queryStr, String domain) {
		if (queryStr == null)
			return new QueryParseResult(null, null, null);
		
		// tokenize the query into words
		List<String> wordList = StringUtil.stringToStringList(queryStr, "[ ]");
		
		if (wordList == null || wordList.size() == 0)
			return new QueryParseResult(null, null, null);
		
		// do some necessary cleanup
		dropSpecialCharacters(wordList);
						
		// parse individual terms
		List<OntologyNode> nodeList = lookupValues(wordList, true, domain);

		if (nodeList == null || nodeList.size() == 0) {
			return new QueryParseResult(queryStr, queryStr, null);
		}
		
		//remove invalid prefix words from remain word list, such as "and",",","&", "/"
		if(wordList != null && wordList.size() > 0) {
			if(OntologyUtil.isInvalidPrefix(wordList.get(0)))
				wordList.remove(0);
		}
				
		String remainWords = StringUtil.listToString(wordList, " ");
		QueryParseResult pr =new QueryParseResult(queryStr, remainWords, nodeList);		
		return pr;
	}
	
	/**
	 * parse all valid nodes in a query String
	 * @param queryStr
	 * @return
	 */
	public QueryParseResult parseQueryString(String queryStr) {		
		return parseQueryString(queryStr, null);
	}
				
	/**
	 * check if source node has a specific relationship with targetNode
	 * @param sourceNode
	 * @param targetNode
	 * @param relationshipType
	 * @return
	 */
	public boolean hasRealtionship(SingleOntologyNode sourceNode, 
			SingleOntologyNode targetNode, String relationshipType) {
		List<RelationNode> relationNodes = getForwardRelatedNodes(sourceNode, relationshipType, 0);
		if(relationNodes == null || relationNodes.size() == 0)
			return false;
		
		for(RelationNode relationNode : relationNodes) {
			if(relationNode.getNode().equals(targetNode))
				return true;
		}
		
		return false;
	}
	
	/**
	 * check if a source is type or subtype of a target
	 * @param source
	 * @param target
	 * @return
	 */
	public boolean isTypeOf(SingleOntologyNode sourceNode, SingleOntologyNode targetNode) {
		return (getInheritanceDistance(sourceNode, targetNode) >= 0);
	}

	/**
	 * check if a source is subtype of a target
	 * @param source
	 * @param target
	 * @return
	 */
	public boolean isSubtypeOf(SingleOntologyNode sourceNode, SingleOntologyNode targetNode) {	
		return (getInheritanceDistance(sourceNode, targetNode) > 0);
	}
	
	/**
	 * get all relationship types that ontology supported, such as "facet_of","subtype_of", etc
	 * @return
	 */
	public List<String> getAllRelationshipType() {
		List<String> result = new ArrayList<String>();		
		Collection<OntologyRelationshipType> types = 
			OntologyDictionary.getInstance().getTypeMap().values();
		for(OntologyRelationshipType type : types) {
			result.add(type.getName());
		}		
		return result;
	}
	
	/**
	 * get all top level nodes under "any" node
	 * @return
	 */
	public List<SingleOntologyNode> getAllTopNodes() {
		return getDirectChildrenNodes(OntologyConstant.NODE_ANY);
	}
	
	@Override
	protected Object lookupValue(String term, String domain) {
		OntologyNode node = getOntologyNode(term);		
		
		/* if ontology dictionary don't contain this term, use this term's canonical name to check
		 * if this term or it's canonical name can find a node which match the domain, return the
		 * node, if we still can't find a node which match the domain, return a node even if it don't
		 * match the domain
		 */
		if (node == null) {
			String canonicalTerm = OntologySynonymDictionary.getInstance().getCanonicalTerm(term, domain);
			OntologyNode Cnode = getOntologyNode(canonicalTerm);
			
			if(Cnode != null)
				node = Cnode;						
		}		
				
		//add original term into node
		if(node != null) {
			node.setOriginalTerm(term);	
		}
		
		return node;
	}
	
	/**
	 * parse a term to an ontology node if it's a valid ontology node, we parse a term in this
	 * order: ontology node(entity, facet(except brand), business), location node, brand node
	 * @param term
	 * @return
	 */
	private OntologyNode getOntologyNode(String term) {
		String key = OntologyUtil.getOntologyNormalizedKey(term);
		//lookup ontology node map except brand nodes
		OntologyNode node = OntologyDictionary.getInstance().getNodeMap().get(key);	

		// check if the returned node is the root node of "Any"
		if (node != null && node.getName().equalsIgnoreCase("any"))
			node = null;
			
		return node;
	}
	
	/**
	 * drop some special characters from a word list when doing parsing
	 * @param wordList
	 */
	private void dropSpecialCharacters(List<String> wordList) {
		for (int i = 0; i < wordList.size(); i++) {
			String word = wordList.get(i);
			if (word != null) {
				// drop the dot at the end of each word
				if (word.endsWith("."))
					wordList.set(i, word.substring(0, word.length() - 1));
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		OntologyNode node = OntologyService.getInstance().lookup("Silicon Valley");
	}
}
