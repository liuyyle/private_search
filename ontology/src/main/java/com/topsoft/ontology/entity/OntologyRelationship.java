/**
 * 
 */
package com.topsoft.ontology.entity;

import java.util.Date;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class OntologyRelationship {
	/**
	 * id int(11) unsigned NOT NULL auto_increment,
	  created_at datetime,
	  updated_at datetime,
	  source_node_id int(11) unsigned,
	  target_node_id int(11) unsigned,
	  relationship_type_id int(11) unsigned,
	  confidence_level double NOT NULL default '1'  COMMENT 'how relevant between this two nodes',
	  importance double not null default 1 COMMENT 'how important to the related node',
	  direction char(1) NOT NULL default '1' COMMENT 'indicate the relationship is one-way or two-way, 1 is one-way, 2 is two-way',
	  PRIMARY KEY  (id),
	 */
	private long id;
	private Date createTime;
	private Date updateTime;
	private long sourceNodeId;
	private long targetNodeId;
	private long relationTypeId;
	private double confidenceLevel = 1;
	private double importance = 1;
	private char direction = '1';

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public long getSourceNodeId() {
		return sourceNodeId;
	}

	public void setSourceNodeId(long sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}

	public long getTargetNodeId() {
		return targetNodeId;
	}

	public void setTargetNodeId(long targetNodeId) {
		this.targetNodeId = targetNodeId;
	}

	public long getRelationTypeId() {
		return relationTypeId;
	}

	public void setRelationTypeId(long relationTypeId) {
		this.relationTypeId = relationTypeId;
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
}
