/**
 * This class is ontology_relationships CRUD dao
 */
package com.topsoft.ontology.daoimp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.topsoft.ontology.dao.AbstractDaoBase;
import com.topsoft.ontology.entity.OntologyRelationship;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 * @date 2010-11-20
 */
public class RelationshipDao extends
		AbstractDaoBase<OntologyRelationship, Long> {
	
	public RelationshipDao() {
		TABLE_NAME = "ontology_relationships";
	}
	
	/**
	 * whether exist in database
	 * @param o
	 * @return
	 */
	public OntologyRelationship ifExist(OntologyRelationship o) {
		StringBuffer sb = new StringBuffer(" where source_node_id=");
		sb.append(o.getSourceNodeId());
		sb.append(" and target_node_id=");
		sb.append(o.getTargetNodeId());
		sb.append(" and relationship_type_id=");
		sb.append(o.getRelationTypeId());
//		sb.append(" and confidence_level=");
//		sb.append(o.getConfidenceLevel());
//		sb.append(" and importance=");
//		sb.append(o.getImportance());		
//		sb.append(" and direction='");
//		sb.append(o.getDirection());
//		sb.append("'");
		List<OntologyRelationship> list = readAll(sb.toString());
		return list != null && list.size() > 0 ? list.get(0) : null;
	}

	@Override
	protected String getCreateSql(OntologyRelationship newInstance) {
		StringBuffer sb = new StringBuffer("insert into ");
		sb.append(TABLE_NAME);
		sb.append(" (created_at,updated_at,source_node_id,target_node_id,relationship_type_id,confidence_level,importance,direction)");
		sb.append(" values (now(),now(),");
		sb.append(newInstance.getSourceNodeId()).append(",");
		sb.append(newInstance.getTargetNodeId()).append(",");
		sb.append(newInstance.getRelationTypeId()).append(",");
		sb.append(newInstance.getConfidenceLevel()).append(",'");
		sb.append(newInstance.getImportance()).append(",'");
		sb.append(newInstance.getDirection()).append("'");
		sb.append(")");
		return sb.toString();
	}

	@Override
	protected String getUpdateSql(OntologyRelationship newInstance) {
		StringBuffer sb = new StringBuffer("update ");
		sb.append(TABLE_NAME);
		sb.append(" set source_node_id=");		
		sb.append(newInstance.getSourceNodeId());
		sb.append(" ,target_node_id=");
		sb.append(newInstance.getTargetNodeId());
		sb.append(" ,relationship_type_id=");
		sb.append(newInstance.getRelationTypeId());
		sb.append(" ,confidence_level=");
		sb.append(newInstance.getConfidenceLevel());
		sb.append(" ,importance=");
		sb.append(newInstance.getImportance());		
		sb.append(" ,direction='");
		sb.append(newInstance.getDirection());
		sb.append("' ,updated_at=now()");
		sb.append(" where id=");
		sb.append(newInstance.getId());
		return sb.toString();
	}

	@Override
	protected String getDeleteSql(OntologyRelationship newInstance) {
		StringBuffer sb = new StringBuffer("delete from ");
		sb.append(TABLE_NAME);
		sb.append(" where id=");
		sb.append(newInstance.getId());
		return sb.toString();
	}

	@Override
	protected List<OntologyRelationship> wrapper(ResultSet result) {
		List<OntologyRelationship> list = null;
		if (result == null)
			return list;
		list = new ArrayList<OntologyRelationship>();
		try {
			while (result.next()) {
				OntologyRelationship o = new OntologyRelationship();
				o.setId(result.getLong("id"));
				o.setCreateTime(result.getTime("created_at"));
				o.setUpdateTime(result.getTimestamp("updated_at"));
				o.setSourceNodeId(result.getLong("source_node_id"));
				o.setTargetNodeId(result.getLong("target_node_id"));
				o.setRelationTypeId(result.getLong("relationship_type_id"));
				o.setConfidenceLevel(result.getDouble("confidence_level"));
				o.setImportance(result.getDouble("importance"));
				o.setDirection(result.getString("direction").charAt(0));
				list.add(o);
			}
		} catch (SQLException e) {
			logger.warn("in wrapper has error", e);
		}
		return list;
	}

	@Override
	protected void setId(OntologyRelationship newInstance, Long id) {
		if (newInstance != null)
			newInstance.setId(id);
	}
}
