/**
 * 
 */
package com.topsoft.ontology.entity;

import java.util.Date;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class OntologySynonym {
	/**
	 * id int(11) unsigned NOT NULL auto_increment,
	  created_at datetime,
	  updated_at datetime,
	  synonym varchar(63),
	  canonical_name varchar(63),
	  context varchar(255),
	  confidence_level double not null default 1,
	  domain varchar(255),
	  PRIMARY KEY  (id)
	 */
	private long id;
	private Date createTime;
	private Date updateTime;
	private String synonym;
	private String canonicalName;
	private String context;
	private double confidenceLevel = 1;
	private String domain;

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

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public double getConfidenceLevel() {
		return confidenceLevel;
	}

	public void setConfidenceLevel(double confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((synonym == null) ? 0 : synonym.hashCode());
		result = prime * result
				+ ((canonicalName == null) ? 0 : canonicalName.hashCode());
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + (int) confidenceLevel;
		result = prime * result + (int) id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if(obj == null)   
	    	return false;
		if(!(obj instanceof OntologySynonym))
			return false;
		
		final OntologySynonym other = (OntologySynonym) obj;
		
		if (synonym == null) {
			if (other.synonym != null)
				return false;
		} else if (!synonym.equals(other.synonym))
			return false;
		
		if (canonicalName == null) {
			if (other.canonicalName != null)
				return false;
		} else if (!canonicalName.equals(other.canonicalName))
			return false;
		
		if (context == null) {
			if (other.context != null)
				return false;			
		} else if (!context.equals(other.context))
			return false;

		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;

		if (confidenceLevel != other.confidenceLevel)
			return false;

		if (id != other.id)
			return false;

		return true;
	}
}
