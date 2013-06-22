/**
 * 
 */
package com.topsoft.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.topsoft.ontology.dictionary.RelationNode;
import com.topsoft.ontology.entity.CompositeOntologyNode;
import com.topsoft.ontology.entity.OntologyNode;
import com.topsoft.ontology.entity.SingleOntologyNode;
import com.topsoft.ontology.util.OntologyConstant;

/**
 * @author yanyong
 *
 */
public class FeatureOntologyService extends OntologyService {
	private static FeatureOntologyService instance = new FeatureOntologyService();
	
	public static FeatureOntologyService getInstance() {
		return instance;
	}

	private FeatureOntologyService() {
		super();
	}
	
	/**
	 * get most likely entity according to some features
	 * @param features
	 * @return
	 */
	public SingleOntologyNode getMostLikelyEntityByFeatures(List<OntologyNode> features) {
		if(features == null || features.size() == 0)
			return null;
		
		Map<SingleOntologyNode, List<EntityFeature>> entityFeatureMaps = new HashMap<SingleOntologyNode, List<EntityFeature>>();		
		for(OntologyNode feature : features) {
			List<SingleOntologyNode> featureNodes = new ArrayList<SingleOntologyNode>();
			if(feature instanceof CompositeOntologyNode) {
				featureNodes.addAll(((CompositeOntologyNode) feature).getNodeList());
			}else if(feature instanceof SingleOntologyNode) {
				featureNodes.add((SingleOntologyNode)feature);
			}	
			
			for(SingleOntologyNode featureNode : featureNodes) {
				if(hasRealtionship(featureNode, OntologyConstant.NODE_FEATURE, OntologyConstant.RELATIONSHIP_VALUE_OF)) {
					List<RelationNode> rNodes = getForwardRelatedNodes(featureNode, OntologyConstant.RELATIONSHIP_FACET_OF, 0);
					for(RelationNode rNode : rNodes) {
						if(isSubtypeOf(rNode.getNode(), OntologyConstant.NODE_ENTITY)) {
							EntityFeature ef = new EntityFeature(featureNode.getName(), rNode.getConfidenceLevel(), (double)1.0/rNodes.size());
							if(entityFeatureMaps.containsKey(rNode.getNode())) {
								entityFeatureMaps.get(rNode.getNode()).add(ef);
							} else {
								List<EntityFeature> efs = new ArrayList<EntityFeature>();
								efs.add(ef);
								entityFeatureMaps.put(rNode.getNode(), efs);
							}
						}
					}
				}
			}
		}
		
		//start to sort the entities by raw count of feature and weighted sum of features
		List<SingleOntologyNode> entities = new ArrayList<SingleOntologyNode>();
		for(SingleOntologyNode entity : entityFeatureMaps.keySet()) {
			entities.add(entity);
		}
		sortEntities(entities, entityFeatureMaps);
		
		return entities.get(0);
	}
	
	/**
	 * check if a term is a featue
	 * @param term
	 * @return
	 */
	public boolean isFeature(String term) {
		return isFeature(term, null);		
	}
	
	/**
	 * check if a term is a specific entity's feature
	 * @param term
	 * @param entity
	 * @return
	 */
	public boolean isFeature(String term, String entity) {
		OntologyNode feature = lookup(term);
		if(feature == null)
			return false;
				
		OntologyNode entityNode = lookup(entity);
		if(entityNode != null) {
			if(entityNode instanceof CompositeOntologyNode) {
				for(SingleOntologyNode node : ((CompositeOntologyNode) entityNode).getNodeList()) {
					if(isSubtypeOf(node, OntologyConstant.NODE_ENTITY)) {
						entityNode = node;
						break;
					}
				}
			}
		}
		
		List<SingleOntologyNode> nodes = new ArrayList<SingleOntologyNode>();
		if(feature instanceof CompositeOntologyNode) {
			nodes.addAll(((CompositeOntologyNode) feature).getNodeList());
		}else if(feature instanceof SingleOntologyNode) {
			nodes.add((SingleOntologyNode)feature);
		}	

		for(SingleOntologyNode n : nodes) {
			if(hasRealtionship(n, OntologyConstant.NODE_FEATURE, OntologyConstant.RELATIONSHIP_VALUE_OF)) {
				if(entity == null) {
					return true;
				} else if(entityNode != null){
					if(hasRealtionship(n, (SingleOntologyNode)entityNode, OntologyConstant.RELATIONSHIP_FACET_OF))
						return true;
				}
			}
		}
		
		return false;		
	}

	/**
	 * sort the entities by raw count of feature and weighted sum of features
	 * @param valueList
	 * @param valuePositions
	 */
	private void sortEntities(List<SingleOntologyNode> entityList, 
			final Map<SingleOntologyNode, List<EntityFeature>> entityFeatureMaps) {
		if(entityList == null || entityList.size() == 0)
			return;

		Collections.sort(entityList, new Comparator<SingleOntologyNode>() {
			public int compare(SingleOntologyNode value1, SingleOntologyNode value2) {
				int featuresCount1 = entityFeatureMaps.get(value1).size();
				int featuresCount2 = entityFeatureMaps.get(value2).size();
				
				double featuresWeight1 = 0, featuresWeight2 = 0;
				for(EntityFeature feature : entityFeatureMaps.get(value1)) {
					featuresWeight1 += feature.getConfidenceLevel()*feature.getUniqueness();
				}
				for(EntityFeature feature : entityFeatureMaps.get(value2)) {
					featuresWeight2 += feature.getConfidenceLevel()*feature.getUniqueness();
				}

				if(featuresCount1*1.5 <= featuresCount2)
					return featuresCount2-featuresCount1;
				else
					return featuresWeight2-featuresWeight1 > 0 ? 1:-1;
			}
		});		
	}
	
	private class EntityFeature {
		private String name;
		private double confidenceLevel;
		private double uniqueness;
		
		public EntityFeature(String name, double confidenceLevel, double uniqueness) {
			this.name = name;
			this.confidenceLevel = confidenceLevel;
			this.uniqueness = uniqueness;
		}
		
		public String getName() {
			return name;
		}
		
		public double getConfidenceLevel() {
			return confidenceLevel;
		}
		
		public double getUniqueness() {
			return uniqueness;
		}		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String queryStr = "When my husband and I moved into our new house that we had just built, we didn¡¯t think about what problem we were going to soon come across with our son just learning how to walk and crawl. We were able to install child safety gates at the bottom and top of the staircase but we didn¡¯t know how to block our son from getting out of the family room into the kitchen and away from the dog room. I searched everywhere and came across the Summer Infant 07160 Super Wide Custom Fit Gate. This child safety gate was the answer to our problem. ";
		QueryParseResult pr = FeatureOntologyService.getInstance().parseQueryString(queryStr);
		
		List<OntologyNode> features = pr.getFeatures();
		if(features != null) {
			for(OntologyNode feature : features)
				System.out.println("feature="+feature);
		}
		
		OntologyNode entity = FeatureOntologyService.getInstance().getMostLikelyEntityByFeatures(features);
		if(entity!= null)
			System.out.println("entity="+entity.getName());
	}

}
