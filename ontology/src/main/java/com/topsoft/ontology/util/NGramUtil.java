/**
 * 
 */
package com.topsoft.ontology.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanyong
 * @time 3:09:10 PM Jan 17, 2011
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class NGramUtil {
	/**
	 * only get the grams that length = n
	 * @param n
	 * @param str
	 * @return
	 */
    public static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++)
            ngrams.add(concat(words, i, i+n));
        return ngrams;
    }

    private static String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++)
            sb.append((i > start ? " " : "") + words[i]);
        return sb.toString();
    }

    /**
     * get all grams that length n
     * @param n
     * @param str
     * @return
     */
    public static List<String> getAllGramsUnderN(int n, String str) {
    	List<String> ngrams = new ArrayList<String>();
        for (int i = n; i >= 1; i--) {
        	ngrams.addAll(ngrams(i, str));
        }
        
        return ngrams;
    }
    
    public static void main(String[] args) {
    	String keyword = "automotive management jobs";
    	System.out.println(NGramUtil.getAllGramsUnderN(keyword.split(" ").length-1, keyword));
    }
}
