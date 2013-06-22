/**
 * 
 */
package com.topsoft.ontology.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.topsoft.ontology.OntologyService;
import com.topsoft.ontology.dictionary.RelationNode;
import com.topsoft.ontology.util.OntologyConstant;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class SingleOntologyNode extends OntologyNode {
	/**
	 * id int(11) unsigned NOT NULL auto_increment,
	  created_at datetime,
	  updated_at datetime,
	  name varchar(63),
	  description varchar(255),
	  display_name varchar(63) COMMENT 'the node name which will be diaplayed on screen, now it sames with name',
	  context varchar(255) COMMENT 'indicate node hierarchy, such as node is car, context=any.entity means car under any.entity',
	  PRIMARY KEY  (id)
	 */
	private long id;
//	private String tableName;
	private Date createTime;
	private Date updateTime;
	private String name;
	private String description;
	private String displayName;
	private String context;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * get this node's all direct parents
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getDirectParentNodes() {
		return OntologyService.getInstance().getDirectParentNodes(this);
	}

	/**
	 * get this node's all parent nodes
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getAllParentNodes() {		
		return OntologyService.getInstance().getAllParentNodes(this);
	}

	/**
	 * get this node's all direct children nodes
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getDirectChildrenNodes() {	
		return OntologyService.getInstance().getDirectChildrenNodes(this);
	}

	/**
	 * get this node's all children nodes
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getAllChildrenNodes() {		
		return OntologyService.getInstance().getAllChildrenNodes(this);
	}

	/**
	 * get all related nodes that similar_to this node
	 * @return List<RelationNode>
	 */
	public List<RelationNode> getSimilarNodes() {
		List<RelationNode> similarNodes = 
			OntologyService.getInstance().getForwardRelatedNodes(this, OntologyConstant.RELATIONSHIP_SIMILAR_TO, 0);
		
		List<RelationNode> backwardNodes = 
			OntologyService.getInstance().getBackwardRelatedNodes(this, OntologyConstant.RELATIONSHIP_SIMILAR_TO, 0);
		if (backwardNodes != null && backwardNodes.size() > 0) {			
			for (int i = 0; i < backwardNodes.size(); i++) {
				RelationNode relationship = backwardNodes.get(i);
				if ('2'==relationship.getDirection())
					similarNodes.add(relationship);
			}
		}		
		return similarNodes;
	}
	
	/**
	 * get all sibling nodes
	 * @return
	 */
	public ArrayList<SingleOntologyNode> getSiblingNodes() {
		ArrayList<SingleOntologyNode> result = new ArrayList<SingleOntologyNode>();
		
		//parent nodes and sibling nodes
		ArrayList<SingleOntologyNode> parents = getDirectParentNodes();
		if(parents != null && parents.size()>0) {
			for(SingleOntologyNode parent: parents) {
				//sibling nodes
				ArrayList<SingleOntologyNode> siblingList = OntologyService.getInstance().getDirectChildrenNodes(parent);
				if(siblingList != null && siblingList.size()>0) {
					for (SingleOntologyNode sibling : siblingList) {						
						if (!this.getName().equals(sibling.getName()))
							result.add(sibling);
					}
				}
			}
		}
		
		return result;
	}

	public boolean equals(Object other) {			
	    if(this == other)
	    	return true;	    
	    if(other == null)   
	    	return false;
	    if(!(other instanceof SingleOntologyNode))
	    	return false;
	 
	    final SingleOntologyNode node = (SingleOntologyNode)other;
	    
		if (getName() == null) {
			if (node.getName() != null)
				return false;
		} else if (!getName().equals(node.getName())) {
			return false;
		}
		
		if (getContext() == null) {
			if (node.getContext() != null)
				return false;
		} else if (!getContext().equals(node.getContext())) {
			return false;
		}
		
		if (getId() != node.getId())
			return false;
		
	    return true;
	}
	 
	public int hashCode() {
		final int prime = 31;
		int result = prime;
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result
				+ ((getContext() == null) ? 0 : getContext().hashCode());
		result = prime * result + (int) (getId() ^ (getId() >> 32));
		return result;
	}
	
	public String toString() {
		return name+":"+context;
	}
}
