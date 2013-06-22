/**
 * This class is ontology_synonyms CRUD dao
 */
package com.topsoft.ontology.daoimp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.topsoft.ontology.dao.AbstractDaoBase;
import com.topsoft.ontology.entity.OntologySynonym;

/**
 * 
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 * @date 2010-11-20
 */
public class SynonymDao extends AbstractDaoBase<OntologySynonym, Long> {

	public SynonymDao() {
		TABLE_NAME = "ontology_synonyms";
		keywordName = "synonym";
	}
	/**
	 * whether exist in database
	 * @param o
	 * @return
	 */
	public OntologySynonym ifExist(OntologySynonym o) {
		String synonym = transferStr(o.getSynonym(), true);
		String cName = transferStr(o.getCanonicalName(), true);
		String context = transferStr(o.getContext(), true);
		String domain = transferStr(o.getDomain(), false);
		StringBuffer sb = new StringBuffer(" where synonym=");
		sb.append(synonym);
		sb.append(" and canonical_name=");
		sb.append(cName);
		// now context is not essential
//		sb.append(" and context=");
//		sb.append(context);	
		// now there is no multiple domain with the same synonym and canonicalName
//		if("NULL".equalsIgnoreCase(domain)){
//			sb.append(" and domain is null");			
//		}else{
//			sb.append(" and domain=");
//			sb.append(domain);
//		}		
		sb.append(" and confidence_level=");
		sb.append(o.getConfidenceLevel());
		List<OntologySynonym> list = readAll(sb.toString());
		return list != null && list.size() > 0 ? list.get(0) : null;
	}
	@Override
	protected String getCreateSql(OntologySynonym newInstance) {
		StringBuffer sb = new StringBuffer("insert into ");
		sb.append(TABLE_NAME);
		sb.append(" (created_at,updated_at,synonym,canonical_name,context,confidence_level,domain)");
		sb.append(" values (now(),now(),");
		final String synonym = transferStr(newInstance.getSynonym(), true);
		sb.append(synonym);
		sb.append(",");
		final String c = transferStr(newInstance.getCanonicalName(), true);
		sb.append(c);
		sb.append(",");
		sb.append(transferStr(newInstance.getContext()));
		sb.append(",");
		sb.append(newInstance.getConfidenceLevel());
		sb.append(",");
		sb.append(transferStr(newInstance.getDomain()));
		sb.append(")");
		return sb.toString();
	}

	@Override
	protected String getUpdateSql(OntologySynonym newInstance) {
		StringBuffer sb = new StringBuffer("update ");
		sb.append(TABLE_NAME);
		sb.append(" set synonym=");
		final String name = transferStr(newInstance.getSynonym(), true);
		sb.append(name);
		sb.append(" ,canonical_name=");
		final String cName = transferStr(newInstance.getCanonicalName(), true);
		sb.append(cName);
		sb.append(" ,context=");
		sb.append(transferStr(newInstance.getContext()));
		sb.append(" ,confidence_level=");
		sb.append(newInstance.getConfidenceLevel());
		sb.append(" ,domain=");
		sb.append(transferStr(newInstance.getDomain()));
		sb.append(" ,updated_at=now()");
		sb.append(" where id=");
		sb.append(newInstance.getId());
		return sb.toString();
	}

	@Override
	protected String getDeleteSql(OntologySynonym newInstance) {
		StringBuffer sb = new StringBuffer("delete from ");
		sb.append(TABLE_NAME);
		sb.append(" where id=");
		sb.append(newInstance.getId());
		return sb.toString();
	}

	@Override
	protected List<OntologySynonym> wrapper(ResultSet result) {
		List<OntologySynonym> list = null;
		if (result == null)
			return list;
		list = new ArrayList<OntologySynonym>();
		try {
			while (result.next()) {
				OntologySynonym o = new OntologySynonym();
				o.setId(result.getLong("id"));
				o.setCreateTime(result.getTime("created_at"));
				o.setUpdateTime(result.getTimestamp("updated_at"));
				o.setSynonym(result.getString("synonym"));
				o.setCanonicalName(result.getString("canonical_name"));
				o.setContext(result.getString("context"));
				o.setConfidenceLevel(result.getDouble("confidence_level"));
				o.setDomain(result.getString("domain"));
				list.add(o);
			}
		} catch (SQLException e) {
			logger.warn("in wrapper has error", e);
		}
		return list;
	}

	@Override
	protected void setId(OntologySynonym newInstance, Long id) {
		if (newInstance != null)
			newInstance.setId(id);
	}
	
//	public static void main(String[] args) {
//		SynonymDao dao = new SynonymDao();
//		OntologySynonym o = new OntologySynonym();
//		final String name = "test";
//		o.setSynonym(name);
//		o.setCanonicalName("lhb_test");
//		o = dao.create(o);
//		OntologySynonym os = dao.read(name);
//		System.out.println(os.getDomain()== null);
//		System.out.println(os != null);
//		dao.delete(os);
//		System.out.println("finished");
//	}

}
