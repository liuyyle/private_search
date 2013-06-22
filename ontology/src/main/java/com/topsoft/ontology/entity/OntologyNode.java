/**
 * 
 */
package com.topsoft.ontology.entity;

/**
 * @author yanyong
 * @time 1:25:51 PM Jan 20, 2011
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public abstract class OntologyNode {
	//this attribute isn't a table's filed, it only use to save a term's original form when parsing
	private String originalTerm;
	
	/**
	 * 
	 * @return the name of this ontology node
	 */
	public abstract String getName();
	
	
	/**
	 * @return the originalTerm
	 */
	public String getOriginalTerm() {
		return originalTerm;
	}

	/**
	 * @param originalTerm the originalTerm to set
	 */
	public void setOriginalTerm(String originalTerm) {
		this.originalTerm = originalTerm;
	}
}
