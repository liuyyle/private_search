/**
 * 
 */
package com.topsoft.ontology.dataprocessor.loading;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.topsoft.ontology.dictionary.OntologyDictionary;
import com.topsoft.ontology.entity.OntologyRelationshipType;
import com.topsoft.ontology.entity.SingleOntologyNode;
import com.topsoft.ontology.util.ConfigFactory;
import com.topsoft.ontology.util.ConnectionManager;
import com.topsoft.ontology.util.OntologyConstant;
import com.topsoft.ontology.util.OntologyUtil;
import com.topsoft.ontology.util.Stopwords;
import com.topsoft.ontology.util.StringUtil;

/**
 * @author yanyong
 *
 */
public class DataLoader {
	private FileReader fileReader = null;
	private static Connection conn = null;
	public static PreparedStatement preQueryNode = null;
	public static PreparedStatement preCreateNode = null;
	public static PreparedStatement preQueryRelation = null;
	public static PreparedStatement preCreateRelation = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			conn = ConnectionManager.getInstance().getConnection();
			
			String queryNodeSql = "select id from ontology_nodes where name=? and context=?";
			preQueryNode = conn.prepareStatement(queryNodeSql);			
			String nodeSql = "INSERT INTO ontology_nodes values(null,now(),now(),?,?,?,?)"; 
			preCreateNode = conn.prepareStatement(nodeSql, PreparedStatement.RETURN_GENERATED_KEYS);
			
			String querySql = "select id from ontology_relationships where source_node_id=? and target_node_id=? and relationship_type_id=?";
			preQueryRelation = conn.prepareStatement(querySql);
			String sql = "INSERT INTO ontology_relationships values(null,now(),now(),?,?,?,?,?,?)"; 
			preCreateRelation = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		Properties confProperties = ConfigFactory.getInstance().getConfigProperties("/ontology.properties");
		String filePath =confProperties.getProperty("data.source.path");
		
		DataLoader loader = new DataLoader();
		loader.loadFile(filePath);
	}

	private void loadFile(String filePath) {		
		try {
			File f=new File(filePath);
			if(f.isDirectory()) {
				File[] fList=f.listFiles();
				for(int j=0;j<fList.length;j++) {
					if(fList[j].isDirectory()) {
						//System.out.println(fList[j].getPath());
						loadFile(fList[j].getPath());
					}
				}
				
				for(int j=0;j<fList.length;j++) {				
					if(fList[j].isFile() && fList[j].getName().endsWith(".txt")) {
						fileContentPersistent(fList[j]);
					}				
				}
			}
		} catch(Exception e) {
			System.out.println("Error£º " + e);
		}

	} 
	
	private void fileContentPersistent(File file) {
		String fileName = file.getName();
		fileName = fileName.substring(0, fileName.length()-4);
		List<String> nameList = StringUtil.stringToStringList(fileName, "[_]");
		nameList.remove(0);
		String entityName = OntologyUtil.getOntologyNormalizedKey(StringUtil.listToString(nameList, " ").trim());
		long entityId = saveNode(entityName, "any.entity", OntologyConstant.NODE_ENTITY, OntologyConstant.RELATIONSHIP_SUBTYPE_OF);
		if(entityId <= 0)
			return;
		System.out.println(entityName);
		
		BufferedReader br = null;
		try{
			fileReader = new FileReader(file);
			br = new BufferedReader(fileReader);
			
			int maxFeatureCount = 0;
			String feature = null;
			int i = 0;
			while((feature = br.readLine()) != null){
				if(i == 0) {
					String[] featureArr = feature.split("[,]");
					String featureCount = featureArr[1].split("[=]")[1].trim();
					maxFeatureCount = Integer.parseInt(featureCount);
				}
				i++;
				dealWithFeature(entityId, entityName, feature, maxFeatureCount);
			}
		}catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
            	br.close();
            } catch (Exception ex) {
            }
        }
	}
	
	private void dealWithFeature(long entityId, String entityName, String feature, int maxFeatureCount) {
		String[] featureArr = feature.split("[,]");
		String featureName = OntologyUtil.getOntologyNormalizedKey(featureArr[0].split("[=]")[1].trim());
		int featureCount = Integer.parseInt(featureArr[1].split("[=]")[1].trim());
		//System.out.println(featureName+":"+featureCount);
		
		if(featureCount < 3 || featureName.equalsIgnoreCase("feature") 
				|| featureName.equalsIgnoreCase(entityName) 
				|| featureName.equalsIgnoreCase("entity")
				|| Stopwords.getInstance().isStopword(featureName.toLowerCase()))
			return;

		if(featureCount < maxFeatureCount*0.01 && featureName.split("[ ]").length == 1)
			return;

		long featureId = saveNode(featureName, "any.facet.feature", OntologyConstant.NODE_FEATURE, OntologyConstant.RELATIONSHIP_VALUE_OF);
		double confidenceLevel = Math.log(featureCount)/Math.log(maxFeatureCount);
		double importance = (double)featureCount/maxFeatureCount;

		saveRelationship(featureId, entityId, OntologyConstant.RELATIONSHIP_FACET_OF, confidenceLevel, importance, "1");
	}
	
	private long saveNode(String nodeName, String nodeContext, SingleOntologyNode parent, String relation) {
		long id = -1;
		try {			
			//String querySql = "select id from ontology_nodes where name=? and context=?";
			//PreparedStatement pre=conn.prepareStatement(querySql);
			preQueryNode.setString(1, nodeName);
			preQueryNode.setString(2, nodeContext);
			preQueryNode.executeQuery();
			ResultSet rs = preQueryNode.getResultSet();
			
			if(rs.next()) {
				id = rs.getLong("id");
			} else {
				//String sql = "INSERT INTO ontology_nodes values(null,now(),now(),?,?,?,?)"; 
				//pre=conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				preCreateNode.setString(1, nodeName);
				preCreateNode.setString(2, nodeName);
				preCreateNode.setString(3, nodeName);
				preCreateNode.setString(4, nodeContext);
				preCreateNode.executeUpdate();
				rs = preCreateNode.getGeneratedKeys();
				if(rs.next()){
					id=rs.getLong(1);
					
				}	
			}			
			rs.close();
			//System.out.println("id="+id);			
			saveRelationship(id, parent.getId(), relation, 1, 1, "1");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return id;
	}
	
	private void saveRelationship(long sourceNodeId, long targetNodeId, String relation, 
			double confidenceLevel, double importance, String direction) {
		
		long relationTypeId = -1;
		Collection<OntologyRelationshipType> types = 
			OntologyDictionary.getInstance().getTypeMap().values();
		for(OntologyRelationshipType type : types) {
			if(relation.equalsIgnoreCase(type.getName())) {
				relationTypeId = type.getId();
				break;
			}
		}

		long id = -1;
		try {						
			//String querySql = "select id from ontology_relationships where source_node_id=? and target_node_id=? and relationship_type_id=?";
			//PreparedStatement pre=conn.prepareStatement(querySql);
			preQueryRelation.setLong(1, sourceNodeId);
			preQueryRelation.setLong(2, targetNodeId);
			preQueryRelation.setLong(3, relationTypeId);
			preQueryRelation.executeQuery();
			ResultSet rs = preQueryRelation.getResultSet();
			
			if(rs.next()) {
				id = rs.getLong("id");
			} else {
				//String sql = "INSERT INTO ontology_relationships values(null,now(),now(),?,?,?,?,?,?)"; 
				//pre=conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
				preCreateRelation.setLong(1, sourceNodeId);
				preCreateRelation.setLong(2, targetNodeId);
				preCreateRelation.setLong(3, relationTypeId);
				preCreateRelation.setDouble(4, confidenceLevel);
				preCreateRelation.setDouble(5, importance);
				preCreateRelation.setString(6, direction);
				preCreateRelation.executeUpdate();
				rs = preCreateRelation.getGeneratedKeys();
				if(rs.next()){
					id=rs.getLong(1);
					
				}	
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
