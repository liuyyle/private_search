/**
 * 
 */
package com.topsoft.ontology.dao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.lang.reflect.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topsoft.ontology.util.ConnectionManager;
import com.topsoft.ontology.util.OntologyUtil;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 * @date 2010-11-20
 * @param <T>
 * 
 */
public abstract class AbstractDaoBase<T, PK extends Serializable> implements
		GenericDao<T, PK> {
	public static final String SELECT = " select * from ";
	public static final String UPDATE = " update ";
	public static final String DELETE = " delete ";
	protected String TABLE_NAME;
	// keyword name for lookup, default is "name"
	protected String keywordName = "name";
	protected ConnectionManager cm = ConnectionManager.getInstance();
	protected Log logger = LogFactory.getLog(this.getClass());
	// Connection used to create T. It is useful for large scale data import.
	private Connection conCreate = null;
	// Connection used to select
	private Connection conSelect = null;

	public AbstractDaoBase() {		
	}

	// This is an abstract method to generate create sql
	protected abstract String getCreateSql(T newInstance);

	// This is an abstract method to generate update sql
	protected abstract String getUpdateSql(T newInstance);

	// This is an abstract method to generate delete sql
	protected abstract String getDeleteSql(T newInstance);

	// wrap ResultSet to List<T>
	protected abstract List<T> wrapper(ResultSet result);

	// set T's id
	protected abstract void setId(T newInstance, PK id);
		
	public T create(T newInstance) {
		final String sql = getCreateSql(newInstance);
		// Connection con = null;
		Statement stat = null;
		PK id = null;
		try {
			if (conCreate == null)
				conCreate = cm.getConnection();
			conCreate.setAutoCommit(false);
			stat = conCreate.createStatement(ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			stat.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			// get new id and set to newInstance
			ResultSet result = stat.getGeneratedKeys();
			if (result != null && result.next()) {
				id = (PK) result.getObject(1);
			}
			setId(newInstance, id);
			conCreate.commit();
		} catch (SQLException e) {
			logger.error("in create has SQLException sql=" + sql, e);
			try {
				conCreate.rollback();
			} catch (SQLException e1) {
				logger.error("cann't rollback", e);
			}
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					logger.error("cann't close stat", e);
				}
			}			
		}
		return newInstance;
	}

	// If necessary, change batch
	public List<T> createList(List<T> newList) {
		String sql = null;
		Connection con = null;
		Statement stat = null;
		PK id = null;
		try {
			con = cm.getConnection();
			con.setAutoCommit(false);
			for (T newInstance : newList) {
				sql = getCreateSql(newInstance);
				stat = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				stat.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
				// get new id and set to newInstance
				ResultSet result = stat.getGeneratedKeys();
				if (result != null && result.next()) {
					id = (PK) result.getObject(1);
					setId(newInstance, id);
				}
			}
			con.commit();
		} catch (SQLException e) {
			logger.error("in createList has SQLException sql=" + sql, e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.error("cann't rollback", e);
			}
		} finally {
			cm.close(con, stat);
		}
		return newList;
	}

	public T read(PK id) {
		T entity = null;
		final String condition = getReadByIdSql(id);
		List<T> list = readAll(condition);
		if (list != null && list.size() > 0) {
			entity = list.get(0);
		}
		return entity;
	}

	public T read(final String name) {
		T entity = null;
		String filterName = OntologyUtil.filterSpecialChar(name);
		final String condition = getReadByNameSql(filterName);
		List<T> list = readAll(condition);
		if (list != null && list.size() > 0) {
			entity = list.get(0);
		}
		return entity;
	}

	public List<T> readAll(final String condition) {
		List<T> list = null;
		Statement stat = null;
		ResultSet result = null;
		final String sql = getReadAllByConditionSql(condition);
		try {
			if (conSelect == null) {
				conSelect = cm.getConnection();
			}
			stat = conSelect.prepareStatement(sql);
			result = stat.executeQuery(sql);
			list = wrapper(result);
		} catch (SQLException e) {
			logger.error("in readAll has SQLException sql=" + sql, e);
		} catch (Exception e) {
			logger.error("in wrapper has exception", e);
		} finally {
			if (stat != null) {
				try {
					stat.close();
				} catch (SQLException e) {
					logger.error("cann't close stat", e);
				}
			}
			// cm.close(con, stat, result);
		}
		return list;
	}

	public void update(T transientObject) {
		final String sql = getUpdateSql(transientObject);
		execUpdate(sql);
	}

	public void delete(T persistentObject) {
		final String sql = getDeleteSql(persistentObject);
		execUpdate(sql);
	}
	
	/**
	 * execute Update or Delete sql
	 * 
	 * @param sql
	 */
	public void execUpdate(final String sql) {
		Connection con = null;
		Statement stat = null;
		try {
			con = cm.getConnection();
			con.setAutoCommit(false);
			stat = con.createStatement();
			stat.executeUpdate(sql);
			logger.debug("execute sql finished ,sql=" + sql);
			con.commit();
		} catch (SQLException e) {
			logger.error("in execUpdate has SQLException sql=" + sql, e);
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.error("cann't rollback", e);
			}
		} finally {
			cm.close(con, stat);
		}
	}	

	protected String getReadByIdSql(PK id) {
		return new StringBuffer(" where id=").append(id).toString();
	}

	protected String getReadByNameSql(String name) {
		StringBuffer sb = new StringBuffer(" where ");
		sb.append(keywordName).append("=");
		sb.append(transferStr(name));
		return sb.toString();
	}

	protected String getReadAllByConditionSql(final String condition) {
		StringBuffer sb = new StringBuffer(SELECT);
		sb.append(TABLE_NAME);
		sb.append(" ");
		sb.append(condition);
		return sb.toString();
	}

	protected String transferStr(final String s) {
		return transferStr(s, false);
	}

	protected String transferStr(final String s, boolean standard) {
		if (standard) {
			String lowercase = s.toLowerCase();
			return OntologyUtil.transferStr(lowercase);
		} else {
			return OntologyUtil.transferStr(s);
		}
	}	
}
