/**
 * 
 */
package com.topsoft.ontology.util;

import java.util.HashSet;

import com.topsoft.ontology.entity.SingleOntologyNode;

/**
 * 
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 * @date 2010-11-10
 */
public class OntologyUtil {
	private static final String[] shortWordArray = {"and", ",", "/", "&", "of", "for", "in", "at", "on", "as"};
	private static HashSet<String> invalidPrefix = new HashSet<String>();
	
	static {
		for (String shortWord : shortWordArray) {
			invalidPrefix.add(shortWord);
		}
	}
	
	/**
	 * check if a prefix is an invalid prefix, this method is used when parsing a query
	 * string, remove invalid prefix from remain words, such as "and",",","&", "/" 
	 * @param prefix
	 * @return
	 */
    public static boolean isInvalidPrefix(String prefix) {        
        if(invalidPrefix.contains(prefix.trim().toLowerCase()))
        	return true;
        
        return false;
    }
    
	/**
	 * filter special char in sql
	 * @param s
	 * @return
	 */
	public static String filterSpecialChar(final String s) {
		String str = "";
		if (s != null) {
			String tmp = s;
			if (s.contains("\\'")) {
				tmp = s.replaceAll("\\\\", "");
			}						
			if (tmp.contains("'")) {
				str = tmp.replaceAll("'", "\\\\'");
			} else {
				str = tmp;
			}
		}
		return str;
	}
	
	public static String transferStr(final String s) {
		StringBuffer sb = new StringBuffer();
		if (s == null) {
			sb.append("NULL");
		} else {
			sb.append("'");
			sb.append(filterSpecialChar(s));
			sb.append("'");
		}
		return sb.toString();
	}
	
	/**
	 * check existNode's context(nodeType) whether is matched with context in name
	 * Note: When find exist node by name (in ontology service's lookup method),
	 * may find an existed node by short name, 
	 * but maybe its context is not exact matched with name.
	 * 
	 * @param existNode
	 * @param name	
	 */
	public static boolean checkContextMatch(final SingleOntologyNode existNode,
			final String name) {
		String existParent = existNode.getContext().toLowerCase();
		String fullParent = OntologyUtil.getFullParentName(name);
		
		// if name is short name, return true
		if (name.equalsIgnoreCase(fullParent)) {
			return true;
		}		
		// full path
		if (isFullPath(fullParent)) {
			return existParent.equalsIgnoreCase(fullParent);
		} else {
			return existParent.endsWith(fullParent.toLowerCase());
		}
	}
	
	/**
	 * check name whether is full path,like this:"any.entity.apparel"
	 * 	
	 * @param name	
	 */
	public static boolean isFullPath(final String name) {
		if (name == null)
			return false;
		return name.toLowerCase().startsWith(OntologyConstant.ANY + ".");
	}
	
	/**
	 * get short name, 
	 * for example, if fullName="any.entity.apparel.clothing",
	 * return "clothing"
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getShortName(final String fullName) {
		if (fullName == null)
			return null;
		String[] names = fullName.split("\\.");
		return names[names.length - 1].trim();
	}
	
	/**
	 * get full parent name, 
	 * for example, if fullName="any.entity.apparel.clothing",
	 * return "any.entity.apparel"
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getFullParentName(final String fullName) {
		if (fullName == null)
			return null;
		int pos = fullName.lastIndexOf(".");
		if (pos < 0) {
			return fullName;
		} else {
			return fullName.substring(0, pos);
		}		
	}

	/**
	 * standardize the input word, this method in ontology service to normalized ontology Key
	 * @param name
	 * @return
	 */
	public static String getOntologyNormalizedKey(String name) {
		if (name == null)
			return null;
		name = name.trim().toLowerCase();
		name = name.replaceAll("-", " ");
		name = name.replaceAll("\\.", "");
		//name = name.replaceAll("\\'", "");		

		name = Inflector.getInstance().singularizeTerm(name);

		return name;
	}
	
	public static void main(String[] args) {		
	
	}
}
