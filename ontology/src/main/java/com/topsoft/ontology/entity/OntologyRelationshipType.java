/**
 * 
 */
package com.topsoft.ontology.entity;

import java.util.Date;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class OntologyRelationshipType {
	/**
	 * id int(11) unsigned NOT NULL auto_increment,
		  created_at datetime,
		  updated_at datetime,
		  name varchar(63),
		  description varchar(255),
		  PRIMARY KEY  (id)
	 */
	private long id;
	private Date createTime;
	private Date updateTime;
	private String name;
	private String description;

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

	// for test
	public String toString() {
		return id + "," + createTime + "," + updateTime + "," + name + ","
				+ description;
	}
}
