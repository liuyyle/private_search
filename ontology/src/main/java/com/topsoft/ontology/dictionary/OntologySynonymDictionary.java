/**
 * OntologySynonymDictionary.java provide the api which used to parse term's synonyms, canonical name
 * and negative words in ontology
 */
package com.topsoft.ontology.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.topsoft.ontology.daoimp.SynonymDao;
import com.topsoft.ontology.entity.OntologySynonym;

/**
 * @author Yanyong
 * @time 2:16:22 PM Nov 4, 2010
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class OntologySynonymDictionary {
	private static Logger logger = Logger.getLogger(OntologySynonymDictionary.class);
	private static OntologySynonymDictionary instance = new OntologySynonymDictionary();
	
	private Map<String, List<OntologySynonym>> synonymMap = new ConcurrentHashMap<String, List<OntologySynonym>>();
	private Map<String, List<OntologySynonym>> cananicalMap = new ConcurrentHashMap<String, List<OntologySynonym>>();
	private Map<String, List<OntologySynonym>> excludeMap = new ConcurrentHashMap<String, List<OntologySynonym>>();

	// this map is used to refresh data in memory when reload dictionary from database
	private Map<String, Object> refreshMap = new HashMap<String, Object>();
	//lock all query operation on each maps above when doing refresh
	private boolean refreshLock = false;
	
	public static OntologySynonymDictionary getInstance() {
		return instance;
	}
	
	private OntologySynonymDictionary() {
		loadDictionary();
	}
	
	/**
	 * load synonym dictionary from database
	 */
	protected void loadDictionary() {
		logger.info("start to load synonym info from database");
		//clear refreshMap, prepare for refreshing data into memory 
		refreshMap.clear();		
		
		Map<String, List<OntologySynonym>> newSynonymMap = new ConcurrentHashMap<String, List<OntologySynonym>>();
		Map<String, List<OntologySynonym>> newCananicalMap = new ConcurrentHashMap<String, List<OntologySynonym>>();
		Map<String, List<OntologySynonym>> newExcludeMap = new ConcurrentHashMap<String, List<OntologySynonym>>();
		
		SynonymDao dao = new SynonymDao();
		// List synonymList = dao.findAll();
		final String condition = " order by synonym, confidence_level desc";
		List<OntologySynonym> synonymList = dao.readAll(condition);

		for (OntologySynonym syn : synonymList) {
			String synonym = syn.getSynonym();
			String canonicalName = syn.getCanonicalName();
			double level = syn.getConfidenceLevel();

			synonym =synonym.toLowerCase();
			canonicalName = canonicalName.toLowerCase();
			if (newSynonymMap.containsKey(synonym)) {
				List<OntologySynonym> srList = (List<OntologySynonym>) newSynonymMap.get(synonym);
				srList.add(syn);
			} else {
				List<OntologySynonym> srList = new ArrayList<OntologySynonym>();
				srList.add(syn);
				newSynonymMap.put(synonym, srList);
			}
			
			if (newCananicalMap.containsKey(canonicalName)) {
				List<OntologySynonym> cList = (List<OntologySynonym>) newCananicalMap.get(canonicalName);
				cList.add(syn);
			} else {
				List<OntologySynonym> cList = new ArrayList<OntologySynonym>();
				cList.add(syn);
				newCananicalMap.put(canonicalName, cList);
			}
			
			if (level < 0) {
				if (newExcludeMap.containsKey(synonym)) {
					List<OntologySynonym> canonicalList = newExcludeMap.get(synonym);
					canonicalList.add(syn);
				} else {
					List<OntologySynonym> canonicalList = new ArrayList<OntologySynonym>();
					canonicalList.add(syn);
					newExcludeMap.put(synonym, canonicalList);
				}				
				if (newExcludeMap.containsKey(canonicalName)) {
					List<OntologySynonym> canonicalList = newExcludeMap.get(canonicalName);
					canonicalList.add(syn);
				} else {
					List<OntologySynonym> canonicalList = new ArrayList<OntologySynonym>();
					canonicalList.add(syn);
					newExcludeMap.put(canonicalName, canonicalList);
				}
			}
		}
		synonymList.clear();
		
		refreshMap.put("synonymMap", newSynonymMap);
		refreshMap.put("cananicalMap", newCananicalMap);
		refreshMap.put("excludeMap", newExcludeMap);
		
		//refresh data into memory
		refreshMemory();
		logger.info("finish load synonym info from database");
	}

	/**
	 * refresh data into memory when load or reload dictionary from database
	 */
	@SuppressWarnings("unchecked")
	private void refreshMemory() {
		refreshLock = true;
		for(String mapName : refreshMap.keySet()) {
			if(mapName.equalsIgnoreCase("synonymMap")) {
				synonymMap.clear();
				synonymMap = (Map<String, List<OntologySynonym>>) refreshMap.get(mapName);
			}
			else if(mapName.equalsIgnoreCase("cananicalMap")) {
				cananicalMap.clear();
				cananicalMap = (Map<String, List<OntologySynonym>>) refreshMap.get(mapName);
			}
			else if(mapName.equalsIgnoreCase("excludeMap")) {
				excludeMap.clear();
				excludeMap = (Map<String, List<OntologySynonym>>) refreshMap.get(mapName);
			}
		}		
		refreshMap.clear();	
		refreshLock = false;
	}
	
	protected List<OntologySynonym> lookup(String key) {
		waitForRefreshCompletion();
		return synonymMap.get(key.toLowerCase());
	}	

	/**
	 * get a word's all canonical names according the word and the context of
	 * ontology, and the canonical name's confidence level must high than
	 * minConfidenceLevel
	 * 
	 * @param term
	 * @param context
	 * @param domain, a specific domain, used to get a specific domain's canonical name
	 * @param minConfidenceLevel
	 * @return
	 */
	private List<SNCWrods> getAllMatchedCanonicalNames(String term, String context, 
			String domain, double minConfidenceLevel) {
		if (term == null)
			return null;

		List<SNCWrods> result = new ArrayList<SNCWrods>();
		String key = term.toLowerCase();
		List<OntologySynonym> srList = lookup(key);

		if (srList != null && srList.size() > 0) {
			for (OntologySynonym syn : srList) {				
				boolean isMatch=isMatch(syn, context, domain, minConfidenceLevel);
				if (isMatch) {
					SNCWrods canonical = new SNCWrods(syn.getId(),
							syn.getCanonicalName(), syn.getDomain());
					if (!result.contains(canonical)) {
						result.add(canonical);
					}
				}			
			}
		}

		// if not find, return term itself
		if (result.size() == 0)
			result.add(new SNCWrods(0, key, domain));
		return result;
	}
	
	/**
	 * get a word's the highest confidence level canonical term according the word and the context of ontology
	 * @param term
	 * @param context
	 * @param domain, a specific domain, used to get a specific domain's canonical name
	 * @return
	 */
	public String getCanonicalTerm(String term, String context, String domain) {
		if (term == null)
			return null;
		
		return getAllMatchedCanonicalNames(term, context, domain, 0).get(0).getWords();
	}

	/**
	 * get a word's the highest confidence level canonical term
	 * @param term
	 * @param domain, a specific domain, used to get a specific domain's canonical name
	 * @return
	 */
	public String getCanonicalTerm(String term, String domain) {
		return getCanonicalTerm(term, null, domain);
	}
	
	/**
	 * get a word's all synonyms according the word and the context
	 * which confidenceLevel is higher than minConfidenceLevel
	 * @param term
	 * @param domain, a specific domain, used to get a specific domain's synonym
	 * @param minConfidenceLevel
	 * @return
	 */
	public List<SNCWrods> getAllSynonyms(final String term, final String domain) {
		final String context = null;
		double level = 0;
		List<SNCWrods> canonicalList = getAllMatchedCanonicalNames(term, context, domain, level);
		
		SNCWrods orinalWords = new SNCWrods(0, term.toLowerCase(), domain);
		if(!canonicalList.contains(orinalWords))
			canonicalList.add(orinalWords);
		List<SNCWrods> synonymList = getWordsByCanonicalList(canonicalList,
				context, domain, level);

		// remove itself
		synonymList.remove(orinalWords);
		return synonymList;
	}
	/**
	 * get all excluding words that is opposite of term
	 * @param term
	 * @param domain, a specific domain, used to get a specific domain's excluding words
	 * @return
	 */
	public List<SNCWrods> getExcludingWords(final String term, final String domain) {
		waitForRefreshCompletion();
		final String context = null;
		double level = 0;
		List<SNCWrods> result = new ArrayList<SNCWrods>();
		List<SNCWrods> synonyms = getAllSynonyms(term, domain);
		synonyms.add(new SNCWrods(0, term.toLowerCase(),domain));
		for (SNCWrods key : synonyms) {
			if (excludeMap.containsKey(key.getWords())) {
				List<OntologySynonym> excludeList = excludeMap.get(key.getWords());
				if (excludeList == null)
					continue;

				List<SNCWrods> canonicalList = new ArrayList<SNCWrods>();
				for(OntologySynonym syn : excludeList) {					
					if(domainMatch(domain, syn.getDomain())) {
						if(key.getWords().equalsIgnoreCase(syn.getCanonicalName()))
							canonicalList.add(new SNCWrods(syn.getId(), syn.getSynonym(), syn.getDomain()));
						if(key.getWords().equalsIgnoreCase(syn.getSynonym()))
							canonicalList.add(new SNCWrods(syn.getId(), syn.getCanonicalName(), syn.getDomain()));
					}
				}
				
				List<SNCWrods> negativeList = getWordsByCanonicalList(canonicalList, context, level);
				for (SNCWrods negative : negativeList) {
					if (!result.contains(negative))
						result.add(negative);
				}
			}
		}
		return result;
	}

	/**
	 * check context,domain and level whether is matched with syn
	 * @param syn
	 * @param context
	 * @param domain
	 * @param level
	 * @return
	 */
	private boolean isMatch(OntologySynonym syn, final String context,
			final String domain, double level) {
		if (syn == null) {
			return false;
		}
		return contextMatch(context, syn.getContext())
				&& syn.getConfidenceLevel() > level
				&& domainMatch(domain, syn.getDomain());
	}
	
	/**
	 * compare if the a synonymContext match a requiredContext
	 * @param requiredContext
	 * @param synonymContext
	 * @return
	 */
	private boolean contextMatch(String requiredContext, String synonymContext) {
		// if context not required or the synonym is generic, it's considered match
		if (requiredContext == null || synonymContext == null || synonymContext.length() == 0)
			return true;
		
		if(synonymContext.toLowerCase().indexOf(requiredContext.toLowerCase()) >= 0)
			return true;
		
		int iPos = synonymContext.lastIndexOf(".", synonymContext.length());		
		while(iPos >=0)
		{
			synonymContext = synonymContext.substring(0, iPos);
			if(requiredContext.equals(synonymContext+".*"))
				return true;
			
			iPos = synonymContext.lastIndexOf(".", synonymContext.length());
		}

		return false;
	}	
	
	/**
	 * compare if the a synonym's domain match a requiredDomain
	 * @param requiredDomain
	 * @param synonymDomain
	 * @return
	 */
	private boolean domainMatch(String requiredDomain, String synonymDomain) {
		// if context not required or the synonym is generic, it's considered match
		if (requiredDomain == null || requiredDomain.trim().length() == 0 
				|| requiredDomain.trim().equalsIgnoreCase("any")
				|| synonymDomain == null || synonymDomain.trim().length() == 0
				|| synonymDomain.trim().equalsIgnoreCase("any"))
			return true;
		
		if(synonymDomain.trim().equalsIgnoreCase(requiredDomain.trim()) || synonymDomain.startsWith(requiredDomain))
			return true;

		return false;
	}	
	
	/**
	 * get synonym/negative words of a canonicalList, it's base method, invoke by getAllSynonyms() and 
	 * getExcludingWords()
	 * @param canonicalList
	 * @param context
	 * @param level
	 * @return
	 */
	private List<SNCWrods> getWordsByCanonicalList(
			List<SNCWrods> canonicalList, final String context, double level) {
		return getWordsByCanonicalList(canonicalList, context, null, level);
	}
	
	/**
	 * get synonym/negative words of a canonicalList
	 * check domain,context,level match
	 * @param canonicalList
	 * @param context
	 * @param domain
	 * @param level
	 * @return
	 */
	private List<SNCWrods> getWordsByCanonicalList(
			List<SNCWrods> canonicalList, final String context,
			final String domain, double level) {
		waitForRefreshCompletion();
		List<SNCWrods> result = new ArrayList<SNCWrods>();
		if (canonicalList == null)
			return result;
		for (SNCWrods canonical : canonicalList) {
			if (!result.contains(canonical))
				result.add(canonical);
			String lowerCanonical = canonical.getWords().toLowerCase();
			List<OntologySynonym> cList = cananicalMap.get(lowerCanonical);
			if (cList == null)
				continue;
			for (OntologySynonym csyn : cList) {
				boolean match = isMatch(csyn, context, domain, level);
				if (match) {
					SNCWrods word = new SNCWrods(csyn.getId(),
							csyn.getSynonym(), csyn.getDomain());
					if (!result.contains(word)) {
						result.add(word);
					}
				}
			}
		}
		return result;
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
	
	public Map<String, List<OntologySynonym>> getSynonymMap() {
		return synonymMap;
	}

	public Map<String, List<OntologySynonym>> getCananicalMap() {
		return cananicalMap;
	}

	public Map<String, List<OntologySynonym>> getExcludeMap() {
		return excludeMap;
	}

	/**
	 * check if a synonym is existed, if existed, return id
	 * @param syn
	 * @return
	 */
	public int getExistedSynonymId(OntologySynonym syn) {
		String key = syn.getSynonym().toLowerCase();
		if(!synonymMap.containsKey(key))
			return 0;
		
		List<OntologySynonym> synonyms = synonymMap.get(key);
		for(OntologySynonym synonym : synonyms) {
			if(synonym.getCanonicalName().equalsIgnoreCase(syn.getCanonicalName()))
				return (int)synonym.getId();
		}
		return 0;			
	}
	
	public static void main(String[] args) {
		OntologySynonymDictionary sd = OntologySynonymDictionary.getInstance();
		System.out.println(sd.getExcludingWords("rental", null));
	}
}
