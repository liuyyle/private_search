/**
 * This class is ontology_relationship_types CRUD dao
 */
package com.topsoft.ontology.daoimp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.topsoft.ontology.dao.AbstractDaoBase;
import com.topsoft.ontology.entity.OntologyRelationshipType;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 * @date 2010-11-20
 */
public class RelationshipTypeDao extends AbstractDaoBase<OntologyRelationshipType, Long> {
	
	public RelationshipTypeDao() {
		TABLE_NAME = "ontology_relationship_types";
	}
	@Override
	protected String getCreateSql(OntologyRelationshipType newInstance) {
		StringBuffer sb = new StringBuffer("insert into ");
		sb.append(TABLE_NAME);
		sb.append(" (created_at,updated_at,name,description)");
		sb.append(" values (now(),now(),");
		final String name = transferStr(newInstance.getName());
		// set name,description with same string
		sb.append(name).append(",").append(name);
		sb.append(")");
		return sb.toString();
	}

	@Override
	protected String getUpdateSql(OntologyRelationshipType newInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDeleteSql(OntologyRelationshipType newInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<OntologyRelationshipType> wrapper(ResultSet result) {
		List<OntologyRelationshipType> list = null;
		if (result == null)
			return list;
		list = new ArrayList<OntologyRelationshipType>();
		try {
			while (result.next()) {
				OntologyRelationshipType o = new OntologyRelationshipType();
				o.setId(result.getLong("id"));				
				o.setCreateTime(result.getTime("created_at"));
				o.setUpdateTime(result.getTimestamp("updated_at"));
				o.setName(result.getString("name"));
				o.setDescription(result.getString("description"));
				list.add(o);
			}
		} catch (SQLException e) {
			logger.warn("in Type wrapper has error", e);
		}
		return list;
	}

	@Override
	protected void setId(OntologyRelationshipType newInstance, Long id) {
		if (newInstance != null)
			newInstance.setId(id);
	}

}
