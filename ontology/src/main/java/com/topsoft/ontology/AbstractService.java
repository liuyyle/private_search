/**
 * LookupServiceBase.java  This is the base class for all lookup service. It provides some common 
 * functionality, such as lookup phrases and terms
 */
package com.topsoft.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yanyong
 * @time 8:42:43 AM Nov 4, 2010
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public abstract class AbstractService {
	/**
	 * This method is used by lookupValues method. It returns the target value
	 * for a given term. It must be implemented in the subclasses
	 * @param term
	 * @param domain, lookup term on a specific domain
	 * @return
	 */
	protected abstract Object lookupValue(String term, String domain);
	
	/**
	 * This method allows the subclasses to specify the maximum phrase size. 
	 * The default value is 3.
	 * @return the maximum phrase size to check
	 */
	protected int getMaxPhraseSize() {
		return 10;
	}
	
	public List lookupValues(List<String> candidateTerms, boolean remove, String domain) {	
		return lookupValues(candidateTerms, new ArrayList<Integer>(), remove, domain);
	}

	/**
	 * This method finds all the values for the candidate terms.
	 * It checks multiple-term phrases and single terms
	 * @param candidateTerms a list of terms
	 * @param valuePositons, remember value's position in candidateTerms if we find a valid value
	 * @param remvoe if set true, the found terms are removed from candidate terms
	 * @param domain this parameter only use in ontology service, lookup node on
	 * a specific domain
	 * @return a list of found values
	 */
	private List lookupValues(List<String> candidateTerms, List<Integer> valuePositions, boolean remove, 
			String domain) {
		if (candidateTerms == null || candidateTerms.size() == 0)
			return null;
		
		/*
		 * copy the candidate terms to an array, so that we can mark which are found
		 * We may need to remove these found terms from the original list if required
		 */		
		String[] termArray = candidateTerms.toArray(new String[0]);
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		// check for N-word, ... 3-word, 2-word phrases and single words
		int maxSize = getMaxPhraseSize();
		if (termArray.length < maxSize)
			maxSize = termArray.length;
		for (int i = maxSize; i > 0; i--) {
			checkForPhrases(termArray, valueList, valuePositions, i, domain);
		}
		
		// remove the found terms from the origial term list if required
		if (remove) {
			candidateTerms.clear();
			for (int i = 0; i < termArray.length; i++) {
				if (termArray[i] != null)
					candidateTerms.add(termArray[i]);
			}
		}
		
		sortValues(valueList, valuePositions);
		
		return valueList;
	}
	
	/**
	 * check if a specific n-gram Phrase can be parsed to a valid value
	 * @param termArray
	 * @param valueList
	 * @param phraseSize
	 * @param domain
	 */
	private void checkForPhrases(String[] termArray, List<Object> valueList, List<Integer> valuePositions, 
			int phraseSize, String domain) {
		for (int i = termArray.length - phraseSize ; i >= 0;) {
		//for (int i = 0; i < termArray.length - phraseSize + 1;) {
			
			// If any of the N terms is null due to earlier rounds, we move to next term.
			String phrase = getPhrase(termArray, i, phraseSize);
			if (phrase == null)	{
				i--;
				continue;
			}
				
			Object value = lookupValue(phrase, domain);			
			if (value != null) {
				// mark the found terms as null
				for (int k = 0; k < phraseSize; k++) {
					termArray[i + k] = null;
				}
				
				// add the found value to value list	
				valueList.add(value);
				valuePositions.add(i);
				
				// skip the phrase, and continue
				i -= phraseSize;
			} else {
				// move to the next word
				i--;
			}
		}		
	}
	
    /**
     * build n-word phrase, skipping the null words
     * @param termArray
     * @param startingIndex
     * @param phraseSize
     * @return
     */
	private String getPhrase(String[] termArray, int startingIndex, int phraseSize) {
		
		// we return a phrase only if the N consecutive words are all non-null
		// some of the words may be null because of earlier rounds of lookup
		int endIndex = startingIndex + phraseSize;
		if (endIndex > termArray.length)
			return null;
		
		int i = 0;
		for (i = startingIndex; i < endIndex; i++) {
			if (termArray[i] == null)
				return null;
		}
		
		// we have got N consecutive non-null words, build the phrase 
		StringBuilder phraseBuilder = new StringBuilder(termArray[startingIndex]);
		for (i = startingIndex + 1; i < endIndex; i++) {
			phraseBuilder.append(' ').append(termArray[i]);
		}
		
		return phraseBuilder.toString();
	}
	
	/**
	 * sort values in valueList according to valuePosition, desc sort
	 * @param valueList
	 * @param valuePositions
	 */
	public static void sortValues(List<Object> valueList, List<Integer> valuePositions) {
		if(valueList == null || valueList.size() == 0)
			return;
		
		final Map<Object, Integer> valueMap = new HashMap<Object, Integer>();
		for(int i = 0 ; i < valueList.size(); i++) {
			valueMap.put(valueList.get(i), valuePositions.get(i));
		}

		Collections.sort(valueList, new Comparator<Object>() {
			public int compare(Object value1, Object value2) {
				return (int)valueMap.get(value2)-valueMap.get(value1);
			}
		});
		
		Collections.sort(valuePositions, new Comparator<Integer>() {
			public int compare(Integer value1, Integer value2) {
				return (int)value2-value1;
			}
		});
	}
}