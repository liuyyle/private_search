/**
 * OntologyDictionary.java load ontology data into dictionary
 */
package com.topsoft.ontology.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.topsoft.ontology.util.OntologyConstant;
import com.topsoft.ontology.util.OntologyUtil;
import com.topsoft.ontology.daoimp.NodeDao;
import com.topsoft.ontology.daoimp.RelationshipDao;
import com.topsoft.ontology.daoimp.RelationshipTypeDao;
import com.topsoft.ontology.entity.CompositeOntologyNode;
import com.topsoft.ontology.entity.OntologyNode;
import com.topsoft.ontology.entity.OntologyRelationship;
import com.topsoft.ontology.entity.OntologyRelationshipType;
import com.topsoft.ontology.entity.SingleOntologyNode;

/**
 * @author Yanyong
 * @time 10:27:17 PM Nov 11, 2010
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class OntologyDictionary {
	private static Logger logger = Logger.getLogger(OntologyDictionary.class);
	private static OntologyDictionary instance = new OntologyDictionary();
	
	// nodeMap, save all node object in database except brands, key is nodeName, value is node object
	private HashMap<String, OntologyNode> nodeMap = new HashMap<String, OntologyNode>();

	// store OntologyNode by id
	private Map<Long, OntologyNode> idMap = new HashMap<Long, OntologyNode>();
	// store OntologyRelationshipType by id
	private Map<Long, OntologyRelationshipType> typeMap = new HashMap<Long, OntologyRelationshipType>();
	/*
	 * There are two graphs, forward graph and backward graph. The former allows
	 * quick retrieval of forward relationships and latter allows quick
	 * retrieval of backward relationships. Each relationship is represented
	 * once in each graph.
	 * 
	 * Each graph is represented by a hash table. In the forward relationship
	 * graph, the key is the source and the value is a list of forward
	 * relationships with this source. In the backward relationship graph, the
	 * key is the target and the value is a list of backward relationships.
	 */
	private HashMap<OntologyNode, ArrayList<RelationNode>> forwardGraph = new HashMap<OntologyNode, ArrayList<RelationNode>>();
	private HashMap<OntologyNode, ArrayList<RelationNode>> backwardGraph = new HashMap<OntologyNode, ArrayList<RelationNode>>();
	// this map is used to refresh data in memory when reload dictionary from database
	private Map<String, Object> refreshMap = new HashMap<String, Object>();
	//lock all query operation on each maps above when doing refresh
	private boolean refreshLock = false;

	public static OntologyDictionary getInstance() {
		return instance;
	}
	
	/**
	 * 
	 */
	private OntologyDictionary() {		
		loadDictionary();
	}

	/**
	 * load product type ontology dictionary from database
	 */
	protected void loadDictionary()
	{
		logger.info("start to load ontology dictionary from database");
		//clear refreshMap, prepare for refreshing data into memory 
		refreshMap.clear();		
		addOntologyNode();
		
		Map<Long, OntologyRelationshipType> newTypeMap = new HashMap<Long, OntologyRelationshipType>();
		HashMap<OntologyNode, ArrayList<RelationNode>> newForwardGraph = new HashMap<OntologyNode, ArrayList<RelationNode>>();
		HashMap<OntologyNode, ArrayList<RelationNode>> newBackwardGraph = new HashMap<OntologyNode, ArrayList<RelationNode>>();
		RelationshipDao dao = new RelationshipDao();
		RelationshipTypeDao typeDao=new RelationshipTypeDao();
		
		OntologyRelationshipType ort = typeDao.read(OntologyConstant.RELATIONSHIP_SUBTYPE_OF);
		newTypeMap.put(ort.getId(), ort);
		ort = typeDao.read(OntologyConstant.RELATIONSHIP_VALUE_OF);
		newTypeMap.put(ort.getId(), ort);
		ort = typeDao.read(OntologyConstant.RELATIONSHIP_FACET_OF);
		newTypeMap.put(ort.getId(), ort);
		ort = typeDao.read(OntologyConstant.RELATIONSHIP_SIMILAR_TO);
		newTypeMap.put(ort.getId(), ort);
				
		final String condition="";
		List<OntologyRelationship> relationList = dao.readAll(condition);		
		for(OntologyRelationship r : relationList) {
			int relationId = (int)r.getId();
			OntologyNode source = idMap.get(r.getSourceNodeId());
			OntologyNode target = idMap.get(r.getTargetNodeId());
			Long relationTypeID = r.getRelationTypeId();
			OntologyRelationshipType type = newTypeMap.get(relationTypeID);
			if (type == null) {
				type = typeDao.read(relationTypeID);
				newTypeMap.put(relationTypeID, type);
			}
			String relationship = type.getName();
			double confidenceLevel = r.getConfidenceLevel();
			double importance = r.getImportance();
			char direction = r.getDirection();
			
			addRelationship(source, target, relationId, relationship, confidenceLevel, importance, direction, 
					newForwardGraph, newBackwardGraph);		
		}
		relationList.clear();
		
		refreshMap.put("typeMap", newTypeMap);
		refreshMap.put("forwardGraph", newForwardGraph);
		refreshMap.put("backwardGraph", newBackwardGraph);
		
		//refresh data into memory
		refreshMemory();
		logger.info("finish load ontology dictionary");
	}

	/**
	 * refresh data into memory when load or reload dictionary from database
	 */
	@SuppressWarnings("unchecked")
	private void refreshMemory() {
		refreshLock = true;
		for(String mapName : refreshMap.keySet()) {
			if(mapName.equalsIgnoreCase("typeMap")) {
				typeMap.clear();
				typeMap = (Map<Long, OntologyRelationshipType>) refreshMap.get(mapName);
			}
			else if(mapName.equalsIgnoreCase("forwardGraph")) {
				forwardGraph.clear();
				forwardGraph = (HashMap<OntologyNode, ArrayList<RelationNode>>) refreshMap.get(mapName);
			}
			else if(mapName.equalsIgnoreCase("backwardGraph")) {
				backwardGraph.clear();
				backwardGraph = (HashMap<OntologyNode, ArrayList<RelationNode>>) refreshMap.get(mapName);
			}
			else if(mapName.equalsIgnoreCase("nodeMap")) {
				nodeMap.clear();
				nodeMap = (HashMap<String, OntologyNode>) refreshMap.get(mapName);
			}
		}		
		refreshMap.clear();	
		refreshLock = false;
	}
	
	/**
	 * put the node object into nodeMap
	 */
	private void addOntologyNode() {
		HashMap<String, OntologyNode> newNodeMap = new HashMap<String, OntologyNode>();	
		
		NodeDao nodedao = new NodeDao();
		final String condition = "";
		List<SingleOntologyNode> ontologyList = nodedao.readAll(condition);
		for (SingleOntologyNode node : ontologyList) {
			idMap.put(node.getId(), node);	
			loadOntologyNode(node, newNodeMap);

		}
		ontologyList.clear();
		
		refreshMap.put("nodeMap", newNodeMap);
	}

	/**
	 * add a node's Forward Relationship and backward relationship
	 * @param source
	 * @param target
	 * @param relationship
	 */
	private void addRelationship(OntologyNode source, OntologyNode target, int id, 
			String relationship, double confidenceLevel, double importance, char direction, 
			HashMap<OntologyNode, ArrayList<RelationNode>> newForwardGraph,
			HashMap<OntologyNode, ArrayList<RelationNode>> newBackwardGraph) {
		
		ArrayList<RelationNode> forwardRelationshipList = newForwardGraph.get(source);
		if (forwardRelationshipList == null) {
			forwardRelationshipList = new ArrayList<RelationNode>();
			newForwardGraph.put(source, forwardRelationshipList);
		}
		forwardRelationshipList.add(new RelationNode(id, (SingleOntologyNode)target, relationship, confidenceLevel, importance, direction));
		
		ArrayList<RelationNode> backwardRelationshipList = newBackwardGraph.get(target);
		if (backwardRelationshipList == null) {
			backwardRelationshipList = new ArrayList<RelationNode>();
			newBackwardGraph.put(target, backwardRelationshipList);
		}		
		backwardRelationshipList.add(new RelationNode(id, (SingleOntologyNode)source,relationship, confidenceLevel, importance, direction));		
	}

	/*
	 * check if we already have this location in location map, add it if not
	 */
	private void loadOntologyNode(SingleOntologyNode node, HashMap<String, OntologyNode> nodeMap) 
	{
		String name = OntologyUtil.getOntologyNormalizedKey(node.getName());
		OntologyNode existingNode = nodeMap.get(name);
		if (existingNode == null) {
			nodeMap.put(name, node);
		} else if (existingNode instanceof CompositeOntologyNode) {
			// the existing node is a composite node
			mergeWithCompositNode((CompositeOntologyNode)existingNode, node);
		} else {
			// the existing node is a single ontology node.
			mergeWithSingleNode((SingleOntologyNode)existingNode, node, nodeMap);
		}
	}
	
	private void mergeWithCompositNode(CompositeOntologyNode compositeNode, SingleOntologyNode node) 
	{
		compositeNode.addNode(node);
	}
	
	private void mergeWithSingleNode(SingleOntologyNode existingSingleNode, SingleOntologyNode node, 
			HashMap<String, OntologyNode> nodeMap) 
	{
		String name = OntologyUtil.getOntologyNormalizedKey(node.getName());
		// Otherwise, the name is shared by existing node and the new node
		CompositeOntologyNode compositeNode = new CompositeOntologyNode(name);
		compositeNode.addNode(existingSingleNode);
		compositeNode.addNode(node);

		nodeMap.put(name, compositeNode);
	}
	
	/**
	 * waiting for the Completion of refreshing data into memory 
	 * @return
	 */
	private boolean waitForRefreshCompletion() {
		while(refreshLock) {
			;
		}
		return true;
	}
	
	public HashMap<String, OntologyNode> getNodeMap() {
		waitForRefreshCompletion();
		return nodeMap;
	}


	/**
	 * @return the typeMap
	 */
	public Map<Long, OntologyRelationshipType> getTypeMap() {
		waitForRefreshCompletion();
		return typeMap;
	}

	/**
	 * @return the forwardGraph
	 */
	public HashMap<OntologyNode, ArrayList<RelationNode>> getForwardGraph() {
		waitForRefreshCompletion();
		return forwardGraph;
	}

	/**
	 * @return the backwardGraph
	 */
	public HashMap<OntologyNode, ArrayList<RelationNode>> getBackwardGraph() {
		waitForRefreshCompletion();
		return backwardGraph;
	}
	
	/**
	 * check if a relation is existed, if existed, return id
	 * @param source
	 * @param target
	 * @param relationship
	 * @return
	 */
	public int getExistedRelationId(OntologyNode source, OntologyNode target, String relationship) {
		if(!forwardGraph.containsKey(source))
			return 0;
		
		ArrayList<RelationNode> nodes = forwardGraph.get(source);
		for(RelationNode node : nodes) {
			if(node.getNode().equals(target) && node.getRelationship().equalsIgnoreCase(relationship))
				return node.getId();
		}		
		return 0;
	}
}
