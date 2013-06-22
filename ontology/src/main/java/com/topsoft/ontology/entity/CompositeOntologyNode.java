/**
 * 
 */
package com.topsoft.ontology.entity;

import java.util.ArrayList;
import java.util.List;

import com.topsoft.ontology.util.StringUtil;
import com.topsoft.ontology.OntologyService;
import com.topsoft.ontology.util.OntologyUtil;


/**
 * @author yanyong
 * @time 1:20:43 PM Jan 20, 2011
 * @Copyright (c) 2010 AnnounceMedia All rights reserved
 */
public class CompositeOntologyNode extends OntologyNode {
	private ArrayList<SingleOntologyNode> nodeList = new ArrayList<SingleOntologyNode>();
	private String name; 		// the name shared by all the nodes
	
	public CompositeOntologyNode(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}
	
	public void addNode(SingleOntologyNode node) {
		nodeList.add(node);
	}
	
	public ArrayList<SingleOntologyNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(ArrayList<SingleOntologyNode> nodeList) {
		this.nodeList = nodeList;
	}
	
	public SingleOntologyNode singlizeByContext(String context) {
		if(context == null || context.trim().length() == 0)
			return null;
		String fullName = context;
		List<String> names = StringUtil.stringToStringList(context, "[.]");
		if (!name.equalsIgnoreCase(names.get(names.size() - 1))) {
			fullName = context + "." + name;
		}        	
        	
        List<SingleOntologyNode> matchedSingleNode = new ArrayList<SingleOntologyNode>();
		for (SingleOntologyNode node : getNodeList()) {
			boolean isMatch = OntologyUtil.checkContextMatch(node, fullName);
			if (isMatch && !matchedSingleNode.contains(node)) {
				matchedSingleNode.add(node);
			}
		}
		if(matchedSingleNode.size() == 1)
			return matchedSingleNode.get(0);
		
		return null;
	}
	
	public static void main(String[] args) {
		OntologyNode node = OntologyService.getInstance().lookup("air");
		if(node instanceof CompositeOntologyNode) {
			node = ((CompositeOntologyNode) node).singlizeByContext("travelc.flight");
			if(node != null) {
				System.out.println(((SingleOntologyNode) node).getId());
			}
		}
	}
}
