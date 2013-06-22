/**
 * 
 */
package com.topsoft.ontology.dictionary;

/**
 * @author yanyong
 * @time 2:23:15 PM Jan 4, 2011
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class DictionarySynchroniser {
	private static DictionarySynchroniser instance = new DictionarySynchroniser();
	
	public static DictionarySynchroniser getInstance() {
		return instance;
	}
	
	/**
	 * refresh ontology data in memory after we update ontology data in database
	 */
    public void refreshDictionary() {
    	OntologyDictionary.getInstance().loadDictionary();
    	OntologySynonymDictionary.getInstance().loadDictionary();
    }
    
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) {
		OntologyDictionary.getInstance();
		System.out.println("vvv");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DictionarySynchroniser.getInstance().refreshDictionary();
	}

}