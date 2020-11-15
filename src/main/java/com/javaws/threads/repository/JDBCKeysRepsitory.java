package com.javaws.threads.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

/**
 	Mysql (shell) :
 	
 		CREATE DATABASE keys_db; 
 		CREATE TABLE KEYS_TBL ( ID INT NOT NULL AUTO_INCREMENT, PATH VARCHAR(250) NOT NULL, KEY_STR VARCHAR(100) NOT NULL, PRIMARY KEY ( ID ) );
 
	Oracle (sql plus) :
	
		CREATE USER ora_keys_db IDENTIFIED BY 123456789;
		ALTER USER ora_keys_db QUOTA unlimited ON SYSTEM;
		GRANT CREATE SESSION, CONNECT, RESOURCE, DBA TO ora_keys_db;
		GRANT ALL PRIVILEGES TO ora_keys_db;
		
		
		CREATE TABLE ora_keys_db.KEYS_TBL
		( 	ID INTEGER NOT NULL,
			PATH VARCHAR2(250) NOT NULL,
		    KEY_STR VARCHAR2(100) NOT NULL,
			CONSTRAINT ID_PK PRIMARY KEY (ID)
		);
		
		CREATE SEQUENCE ora_keys_db.SEQ_KEYS_TBL_ID
		START WITH 1
		MAXVALUE 9999999
		MINVALUE 1
		CYCLE
		NOCACHE
		NOORDER;
		
		
		CREATE OR REPLACE TRIGGER ora_keys_db.KEYS_TBL_AUTO_ID
		  BEFORE INSERT ON ora_keys_db.KEYS_TBL
		  FOR EACH ROW
			BEGIN
			  SELECT ora_keys_db.SEQ_KEYS_TBL_ID.nextval
			  INTO :new.ID
			  FROM dual;
			END;
		/

 */
public class JDBCKeysRepsitory implements KeysRepository {

	
	private final DataSource dataSource;
	
	public JDBCKeysRepsitory(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	@Override
	public Object create(KeyItem keyItem) throws RepositoryException {
		Connection conn = null;
		try {
			conn = this.dataSource.getConnection();

			String sql = "INSERT INTO KEYS_TBL(PATH, KEY_STR) VALUES(?,?)";

			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, keyItem.getPath());
			pstmt.setString(2, keyItem.getKey());

			int updates = pstmt.executeUpdate();
			if (updates == 1) {
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					return rs.getObject(1);
				}
			}

		} catch (SQLException e) {
			throw new RepositoryException(e);
			
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				throw new RepositoryException(e);
			}
		}

		// ID Was not generated
		return null;
	}

}
