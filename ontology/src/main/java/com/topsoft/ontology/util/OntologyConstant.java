/**
 *
 */
package com.topsoft.ontology.util;

import com.topsoft.ontology.OntologyService;
import com.topsoft.ontology.dictionary.OntologyDictionary;
import com.topsoft.ontology.entity.SingleOntologyNode;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class OntologyConstant {
	/** root */
	public static final String ANY = "any";
	public static final SingleOntologyNode NODE_ANY = (SingleOntologyNode)OntologyDictionary.getInstance().getNodeMap().get("any");
	/** first level children of root */
	public static final SingleOntologyNode NODE_ENTITY = (SingleOntologyNode)OntologyService.getInstance().lookup("entity");
	public static final SingleOntologyNode NODE_FACET = (SingleOntologyNode)OntologyService.getInstance().lookup("facet");;
	/** second level children of facet */
	public static final SingleOntologyNode NODE_FEATURE = (SingleOntologyNode)OntologyService.getInstance().lookup("feature");;
	/** relationship type
   * NOTE: If more relationship types are added or existing ones changed
   * the RelationshipTypeValidator must be modified
   */
	public static final String RELATIONSHIP_SUBTYPE_OF  = "subtype_of";
	public static final String RELATIONSHIP_FACET_OF    = "facet_of";
	public static final String RELATIONSHIP_VALUE_OF  = "value_of";
	public static final String RELATIONSHIP_SIMILAR_TO  = "similar_to";
}
