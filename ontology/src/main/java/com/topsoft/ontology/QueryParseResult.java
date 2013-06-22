/**
 * QueryParseResult.java this class is a helper class, it used to transfer an ontology list that 
 * return from query parse result to an object, this object classify the list to entity, facet and
 * business, this can help engineer to simplify his code when using query parse result
 * and negative words in ontology
 */
package com.topsoft.ontology;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.ontology.entity.CompositeOntologyNode;
import com.topsoft.ontology.entity.OntologyNode;
import com.topsoft.ontology.entity.SingleOntologyNode;
import com.topsoft.ontology.util.OntologyConstant;
import com.topsoft.ontology.util.OntologyUtil;

/**
 * @author Yanyong
 * @time 4:16:22 PM Dec 4, 2010
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class QueryParseResult {
	private boolean hasValidResult;
	private String originalString;
	private String remainWords;
	private List<OntologyNode> nodeList;
	private List<OntologyNode> entities;
	private List<OntologyNode> features;
	
	
	public QueryParseResult(String originalString, String remainWords, List<OntologyNode> nodeList) {
		this.originalString = originalString;
		this.remainWords = remainWords;
		if(nodeList == null || nodeList.size() == 0) {
			hasValidResult = false;
		} else {
			hasValidResult = true;
			this.nodeList = nodeList;
		}
	}
	
	/**
	 * @return the hasValidResult
	 */
	public boolean hasValidResult() {
		return hasValidResult;
	}

	/**
	 * @return the originalString
	 */
	public String getOriginalString() {
		return originalString;
	}

	/**
	 * @param originalString the originalString to set
	 */
	public void setOriginalString(String originalString) {
		this.originalString = originalString;
	}

	/**
	 * @return the remainWords
	 */
	public String getRemainWords() {
		return remainWords;
	}

	/**
	 * @param remainWords the remainWords to set
	 */
	public void setRemainWords(String remainWords) {
		this.remainWords = remainWords;
	}
	
	/**
	 * @return the features
	 */
	public List<OntologyNode> getFeatures() {
		if(features == null) {
			features = new ArrayList<OntologyNode>();
			features = classifyNode(nodeList, OntologyConstant.NODE_FEATURE, OntologyConstant.RELATIONSHIP_VALUE_OF);
		}
		return features;
	}

	/**
	 * @return the entities
	 */
	public List<OntologyNode> getEntities() {
		if(entities == null) {
			entities = new ArrayList<OntologyNode>();
			entities = classifyNode(nodeList, OntologyConstant.NODE_ENTITY, OntologyConstant.RELATIONSHIP_SUBTYPE_OF);
		}		
		return entities;
	}
	
	/**
	 * classify nodes according to the relationship which related to a target node
	 * @param nodeList
	 * @param targetNode
	 * @param reltionship
	 */
	private List<OntologyNode> classifyNode(List<OntologyNode> nodeList, SingleOntologyNode targetNode, String relationship) {
		if(nodeList == null || nodeList.size() == 0)
			return null;
		
		List<SingleOntologyNode> nodes = new ArrayList<SingleOntologyNode>();
		for(OntologyNode node: nodeList) {
			if(node instanceof CompositeOntologyNode) {
				List<SingleOntologyNode> sns = ((CompositeOntologyNode) node).getNodeList();
				for(SingleOntologyNode sn : sns) {
					if(!nodes.contains(sn))
						nodes.add((SingleOntologyNode)sn);
				}
			}else if(node instanceof SingleOntologyNode) {
				if(!nodes.contains((SingleOntologyNode)node))
					nodes.add((SingleOntologyNode)node);
			}	
		}

		List<OntologyNode> result = new ArrayList<OntologyNode>();
		for(SingleOntologyNode node : nodes) {
			if(OntologyService.getInstance().hasRealtionship(node, targetNode, relationship)) {
				fillNodeList(node, result);
			}
		}
							
		return result;
	}
	
	private void fillNodeList(SingleOntologyNode node, List<OntologyNode> nodeList) {
		OntologyNode existingNode = null;
		for(OntologyNode child : nodeList) {
			String childName= OntologyUtil.getOntologyNormalizedKey(child.getName());
			String newName= OntologyUtil.getOntologyNormalizedKey(node.getName());
			if(childName.equalsIgnoreCase(newName)) {
				existingNode = child;
				break;
			}
		}
		
		if (existingNode == null) {
			nodeList.add(node);
		} else if (existingNode instanceof CompositeOntologyNode) {
			// the existing node is a composite node
			((CompositeOntologyNode)existingNode).addNode(node);
		} else {
			// the existing node is a single location node.
			String name = OntologyUtil.getOntologyNormalizedKey(node.getName());
			// Otherwise, the name is shared by existing node and the new node
			CompositeOntologyNode compositeNode = new CompositeOntologyNode(name);
			compositeNode.addNode((SingleOntologyNode)existingNode);
			compositeNode.addNode(node);

			nodeList.remove(existingNode);
			nodeList.add(compositeNode);
		}		
	}
}
