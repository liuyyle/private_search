/**
 * AbstractOntologyParser.java provide all common methods and abstract interfaces that derive a 
 * node's information, it's a base class, different type of node parser need to extends from it,
 * such as entity, facet, business, etc.
 */
package com.topsoft.ontology;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.ontology.dictionary.OntologyDictionary;
import com.topsoft.ontology.dictionary.RelationNode;
import com.topsoft.ontology.entity.SingleOntologyNode;
import com.topsoft.ontology.util.OntologyConstant;

/**
 * @author Yanyong
 * @time 8:50:43 AM Nov 5, 2010
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public abstract class AbstractOntologyService extends AbstractService {	
	protected OntologyDictionary dictionary;

	public AbstractOntologyService() {
		dictionary = OntologyDictionary.getInstance();
	}
	
	/**
	 * get the distance between sourceNode and targetNode
	 * @param sourceNode
	 * @param targetNode
	 * @return
	 */
	public int getInheritanceDistance(SingleOntologyNode sourceNode, SingleOntologyNode targetNode) {
		if (sourceNode == null || targetNode == null)
			return -1;
		
		//this list is used to save all founded nodes, and used it to remove duplicated nodes
		ArrayList<SingleOntologyNode> list = new ArrayList<SingleOntologyNode>();
		
		ArrayList<SingleOntologyNode> sourceList = new ArrayList<SingleOntologyNode>();
		sourceList.add(sourceNode);

		int distance = 0;
		ArrayList<SingleOntologyNode> parentList = new ArrayList<SingleOntologyNode>();
		do {
			for (int i = 0; i < sourceList.size(); i++) {
				String sourceName = sourceList.get(i).getName();
				String sourceContext = sourceList.get(i).getContext();

				// check if the source node equals the target node
				if (sourceName.equals(targetNode.getName()) && sourceContext.equals(targetNode.getContext())) {
					return distance;
				}

				// otherwise get all the parent nodes of the source node
				ArrayList<SingleOntologyNode> parents = getDirectParentNodes(sourceList.get(i));
				if (parents != null) {
					parents.removeAll(list);
					list.addAll(parents);
					parentList.addAll(parents);
				}
			}

			// use the parent list as the source list of next round
			copy(sourceList, parentList);
			parentList.clear();
			distance++;
		} while (sourceList.size() > 0);

		// we did not find the super type if we reach here
		return -1;
	}
	
	/**
	 * get all direct parent node by a sourceNode
	 * @param sourceNode
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getDirectParentNodes(SingleOntologyNode sourceNode) {
		ArrayList<SingleOntologyNode> parentList = new ArrayList<SingleOntologyNode>();
		ArrayList<RelationNode> relationshipList = getForwardRelatedNodes(sourceNode);
		if (relationshipList == null || relationshipList.size() <= 0)
			return parentList;

		for (int i = 0; i < relationshipList.size(); i++) {
			RelationNode relationship = relationshipList.get(i);
			if (OntologyConstant.RELATIONSHIP_SUBTYPE_OF.equals(relationship.getRelationship()) 
					|| OntologyConstant.RELATIONSHIP_VALUE_OF.equals(relationship.getRelationship())) {
				parentList.add(relationship.getNode());
			}
		}

		return parentList;
	}
	
	/**
	 * get all parent node by a sourceNode
	 * @param sourceNode
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getAllParentNodes(SingleOntologyNode sourceNode) {
		ArrayList<SingleOntologyNode> parentList = new ArrayList<SingleOntologyNode>();
		
		ArrayList<SingleOntologyNode> sourceList = new ArrayList<SingleOntologyNode>();
		sourceList.add(sourceNode);
		ArrayList<SingleOntologyNode> parents = new ArrayList<SingleOntologyNode>();
		do {
			for (int i = 0; i < sourceList.size(); i++) {
				SingleOntologyNode tempSource = sourceList.get(i);

				// otherwise get all the parent nodes of the source node
				ArrayList<SingleOntologyNode> list = getDirectParentNodes(tempSource);
				if (list != null && list.size() > 0){
					list.removeAll(parentList);					
					parents.addAll(list);
					for(SingleOntologyNode node: list){
						if(!parentList.contains(node))
							parentList.add(node);
					}					
				}
			}

			// use the parents as the source list of next round
			copy(sourceList, parents);
			parents.clear();
		} while (sourceList.size() > 0);

		return parentList;
	}
	
	/**
	 * get all direct children node by a targetNode
	 * @param sourceNode
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getDirectChildrenNodes(SingleOntologyNode targetNode) {
		ArrayList<SingleOntologyNode> childrenList = new ArrayList<SingleOntologyNode>();
		ArrayList<RelationNode> relationshipList = getBackwardRelatedNodes(targetNode);
		if (relationshipList == null || relationshipList.size() <= 0)
			return childrenList;

		for (int i = 0; i < relationshipList.size(); i++) {
			RelationNode relationship = relationshipList.get(i);
			if (OntologyConstant.RELATIONSHIP_SUBTYPE_OF.equals(relationship.getRelationship()) 
					|| OntologyConstant.RELATIONSHIP_VALUE_OF.equals(relationship.getRelationship())) {
				childrenList.add(relationship.getNode());
			}
		}

		return childrenList;
	}
	
	/**
	 * get all children node by a targetNode
	 * @param sourceNode
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getAllChildrenNodes(SingleOntologyNode targetNode) {
		ArrayList<SingleOntologyNode> childrenList = new ArrayList<SingleOntologyNode>();
		
		ArrayList<SingleOntologyNode> sourceList = new ArrayList<SingleOntologyNode>();
		sourceList.add(targetNode);
		ArrayList<SingleOntologyNode> children = new ArrayList<SingleOntologyNode>();
		do {
			for (int i = 0; i < sourceList.size(); i++) {
				SingleOntologyNode tempSource = sourceList.get(i);

				// otherwise get all the parent nodes of the source node
				ArrayList<SingleOntologyNode> list = getDirectChildrenNodes(tempSource);
				if (list != null && list.size() > 0){
					list.removeAll(childrenList);					
					children.addAll(list);
					for(SingleOntologyNode node: list){
						if(!childrenList.contains(node))
							childrenList.add(node);
					}					
				}
			}

			// use the parents as the source list of next round
			copy(sourceList, children);
			children.clear();
		} while (sourceList.size() > 0);

		return childrenList;
	}	

	/**
	 * get all forward related nodes(that means from this node to other node) of one node by a specific relationshipType,
	 * and the related weight must greater than minimumConfidenceLevel
	 * @param node, target node
	 * @param relationshipType, such as "subtype_of","similar_to" etc
	 * @param minimumConfidenceLevel
	 * @return ArrayList
	 */
	public List<RelationNode> getForwardRelatedNodes(SingleOntologyNode node, String relationshipType, double minimumConfidenceLevel) {
		List<RelationNode>  result = new ArrayList<RelationNode>();
		
		ArrayList<RelationNode> forwardList = getForwardRelatedNodes(node);
		if (forwardList != null && forwardList.size() > 0) {
			// check if the target node has the specific relationshipType
			for (int i = 0; i < forwardList.size(); i++) {
				RelationNode relationship = forwardList.get(i);
				if (relationshipType.equals(relationship.getRelationship())) {
					if(relationship.getConfidenceLevel()>minimumConfidenceLevel)
						result.add(relationship);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * get all backward related nodes(that means from other node to this node) of one node by a specific relationshipType,
	 * and the related weight must greater than minimumConfidenceLevel
	 * @param node, target node
	 * @param relationshipType, such as "subtype_of","similar_to" etc
	 * @param minimumConfidenceLevel
	 * @return ArrayList
	 */
	public List<RelationNode> getBackwardRelatedNodes(SingleOntologyNode node, String relationshipType, double minimumConfidenceLevel) {
		List<RelationNode>  result = new ArrayList<RelationNode>();
		
		ArrayList<RelationNode> backwardList = getBackwardRelatedNodes(node);
		if (backwardList != null && backwardList.size() > 0) {
			// check if the target node has the specific relationshipType
			for (int i = 0; i < backwardList.size(); i++) {
				RelationNode relationship = backwardList.get(i);
				if (relationshipType.equals(relationship.getRelationship())) {
					if(relationship.getConfidenceLevel()>minimumConfidenceLevel)
						result.add(relationship);
				}
			}
		}
		
		return result;
	}
	
	public ArrayList<RelationNode> getForwardRelatedNodes(SingleOntologyNode source) {
		ArrayList<RelationNode> result = dictionary.getForwardGraph().get(source);
		if(result != null)
			return result;
		else
			return new ArrayList<RelationNode>();
	}

	public ArrayList<RelationNode> getBackwardRelatedNodes(SingleOntologyNode target) {
		ArrayList<RelationNode> result = dictionary.getBackwardGraph().get(target);
		if(result != null)
			return result;
		else
			return new ArrayList<RelationNode>();
	}
	
	protected void copy(ArrayList<SingleOntologyNode> target, ArrayList<SingleOntologyNode> source) {
		if (target == null || source == null)
			return;

		target.clear();
		for (int i = 0; i < source.size(); i++) {
			target.add(source.get(i));
		}
	}
}
