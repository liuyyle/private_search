/**
 * this class is used to save synonym or negative or canonical words that match with domain and context
 */
package com.topsoft.ontology.dictionary;

/**
 * @author yanyong
 * @time 10:03:31 PM Feb 15, 2011
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class SNCWrods {
	private long id;
	private String words;
	private String domain;

	public SNCWrods(long id, String words, String domain) {
		this.id = id;
		this.words = words;
		this.domain = domain;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the words
	 */
	public String getWords() {
		return words;
	}

	/**
	 * @param words the words to set
	 */
	public void setWords(String words) {
		this.words = words;
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

	public boolean equals(Object other) {			
	    if(this == other)
	    	return true;	    
	    if(other == null)   
	    	return false;
	    if(!(other instanceof SNCWrods))
	    	return false;
	 
	    final SNCWrods words = (SNCWrods)other;
		
		if (!getWords().equals(words.getWords())) {
			return false;
		}
		
		if (getDomain() == null) {
			if (words.getDomain() != null)
				return false;
		} else if (!getDomain().equals(words.getDomain())) {
			return false;
		}
		
	    return true;
	}
	 
	public int hashCode() {
		final int prime = 31;
		int result = prime;
		result = prime * result
				+ ((getWords() == null) ? 0 : getWords().hashCode());
		result = prime * result
				+ ((getDomain() == null) ? 0 : getDomain().hashCode());
		return result;
	}
	
	public String toString() {
		return id+":"+words+":"+domain;
	}
}
