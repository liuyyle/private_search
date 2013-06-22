/**
 * This class is ontology_nodes CRUD dao
 */
package com.topsoft.ontology.daoimp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.topsoft.ontology.dao.AbstractDaoBase;
import com.topsoft.ontology.entity.SingleOntologyNode;


/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 * @date 2010-11-20
 * @param <T>
 *
 */
public class NodeDao extends AbstractDaoBase<SingleOntologyNode, Long> {

	public NodeDao() {
		TABLE_NAME = "ontology_nodes";
	}
	
	public void deleteByName(final String name) {
		StringBuffer sb = new StringBuffer("delete from ");
		sb.append(TABLE_NAME);
		sb.append(" where name=");
		sb.append(transferStr(name, true));
		execUpdate(sb.toString());
	}

	@Override
	protected String getCreateSql(SingleOntologyNode newInstance) {
		StringBuffer sb = new StringBuffer("insert into ");
		sb.append(TABLE_NAME);
		sb.append(" (created_at,updated_at,name,description,display_name,context)");
		sb.append(" values (now(),now(),");
		final String name = transferStr(newInstance.getName(), true);
		// set name,description,display_name with same string
		sb.append(name).append(",").append(name).append(",").append(name);
		final String context = transferStr(newInstance.getContext());
		sb.append(",").append(context);
		sb.append(")");
		return sb.toString();
	}

	@Override
	protected String getUpdateSql(SingleOntologyNode newInstance) {
		StringBuffer sb = new StringBuffer("update ");
		sb.append(TABLE_NAME);
		sb.append(" set name=");
		final String name = transferStr(newInstance.getName(), true);
		sb.append(name);
		sb.append(" ,description=");
		sb.append(name);
		sb.append(" ,display_name=");
		sb.append(name);
		sb.append(" ,node_type=");
		final String context = transferStr(newInstance.getContext(), true);
		sb.append(context);
		sb.append(" ,updated_at=now()");
		sb.append(" where id=");
		sb.append(newInstance.getId());
		return sb.toString();
	}

	@Override
	protected String getDeleteSql(SingleOntologyNode newInstance) {
		StringBuffer sb = new StringBuffer("delete from ");
		sb.append(TABLE_NAME);
		sb.append(" where id=");
		sb.append(newInstance.getId());
		return sb.toString();
	}

	@Override
	protected List<SingleOntologyNode> wrapper(ResultSet result) {
		List<SingleOntologyNode> list = null;
		if (result == null)
			return list;
		list = new ArrayList<SingleOntologyNode>();
		try {
			while (result.next()) {
				SingleOntologyNode o = new SingleOntologyNode();
				o.setId(result.getLong("id"));				
				o.setCreateTime(result.getTime("created_at"));
				o.setUpdateTime(result.getTimestamp("updated_at"));				
				o.setName(result.getString("name"));				
				o.setDescription(result.getString("description"));
				o.setDisplayName(result.getString("display_name"));
				o.setContext(result.getString("context"));
				list.add(o);
			}
		} catch (SQLException e) {
			logger.warn("in wrapper has error", e);
		}
		return list;
	}

	@Override
	protected void setId(SingleOntologyNode newInstance, Long id) {
		if (newInstance != null)
			newInstance.setId(id);
	}
}
