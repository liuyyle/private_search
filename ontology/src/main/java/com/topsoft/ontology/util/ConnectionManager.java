/**
 * 
 */
package com.topsoft.ontology.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topsoft.ontology.util.ConfigFactory;

/**
 * @author Hanbing Luo (hanbing.luo@announcemedia.com)
 *
 */
public class ConnectionManager {
	private static ConnectionManager instance;
	private final Properties dbProperties;
	private Log logger = LogFactory.getLog(this.getClass());

	private String url;
	private String username;
	private String password;
	private String dbName;
	
	private ConnectionManager() {
		// load property file
		dbProperties = ConfigFactory.getInstance().getConfigProperties(
				"/ontology.properties");
		url = dbProperties.getProperty("db.url");
		username = dbProperties.getProperty("db.username");
		password = dbProperties.getProperty("db.password");
		dbName = dbProperties.getProperty("db.dbName");				
		// initialize the DB driver
		final String driver = dbProperties.getProperty("jdbc.driver");
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			logger.error("Cann't load db driver!",e);
		}
	}
	
	synchronized public static ConnectionManager getInstance() {
		if (instance == null) {
			instance = new ConnectionManager();
		}
		return instance;
	}

	public Connection getConnection() throws SQLException{
		return getConnection(dbName, url);
	}

	public Connection getConnection(final String dbName) throws SQLException{
		return getConnection(dbName, url);
	}

	public Connection getConnection(String dbName, String url) throws SQLException {
		// use default URL if the url is null
		if (url == null) {
			url = this.url;
		}
		// use default dbName if the dbName is null
		if (dbName == null) {
			dbName=this.dbName;
		}

		Connection conn = null;
		String fullUrl = url + "/" + dbName + "?&user=" + username
				+ "&password=" + password;
//		logger.info(fullUrl);
		conn = DriverManager.getConnection(fullUrl);
		return conn;
	}
	
	public void close(Connection con, Statement stat) {
		close(con,stat,null);
	}
	
	public void close(Connection con, Statement stat, ResultSet result) {
		if (result != null) {
			try {
				result.close();
			} catch (SQLException e) {
				logger.warn("ConnectionManager close ResultSet has error!", e);
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				logger.warn("ConnectionManager close Statement has error!", e);
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				logger.warn("ConnectionManager close Connection has error!", e);
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stat = null;
		try {
			con = ConnectionManager.getInstance().getConnection();
			stat = con.createStatement();
			System.out.println(con == null);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
