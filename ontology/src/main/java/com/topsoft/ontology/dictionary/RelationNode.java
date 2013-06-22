/**
 * RelationNode.java save the relationship from a target node to a source node, for example, 
 * using this object we can get a node's children information
 */
package com.topsoft.ontology.dictionary;

import com.topsoft.ontology.entity.SingleOntologyNode;

/**
 * @author Yanyong
 * @time 11:33:15 PM Nov 11, 2010
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class RelationNode {
	private int id;
	private SingleOntologyNode node;
	private String relationship;
	private double confidenceLevel;
	private double importance;
	private char direction;
	
	public RelationNode(int id, SingleOntologyNode node, String relationship, 
			double confidenceLevel, double importance, char direction) {
		this.id = id;
		this.node = node;
		this.relationship = relationship;
		this.confidenceLevel = confidenceLevel;
		this.importance = importance;
		this.direction = direction;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public SingleOntologyNode getNode() {
		return node;
	}

	public void setNode(SingleOntologyNode node) {
		this.node = node;
	}

	public double getConfidenceLevel() {
		return confidenceLevel;
	}

	public void setConfidenceLevel(double confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	public double getImportance() {
		return importance;
	}

	public void setImportance(double importance) {
		this.importance = importance;
	}

	public char getDirection() {
		return direction;
	}

	public void setDirection(char direction) {
		this.direction = direction;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("node = ").append(node.getContext()+"."+node.getName())
			.append(", relationship = ").append(relationship)
			.append(", confidenceLevel = ").append(confidenceLevel)
			.append(", importance = ").append(importance)
			.append(", direction = ").append(direction);
		return builder.toString();
	}
}
