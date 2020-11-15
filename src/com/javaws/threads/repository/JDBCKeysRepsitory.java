package com.javaws.threads.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * CREATE DATABASE keys_db;
 * 
 * CREATE TABLE KEYS_TBL ( ID INT NOT NULL AUTO_INCREMENT, PATH VARCHAR(250) NOT
 * NULL, KEY_STR VARCHAR(100) NOT NULL, PRIMARY KEY ( ID ) );
 *
 */
public class JDBCKeysRepsitory implements KeysRepository {

	// These should be configurations
	private final static String CONN_STR = "jdbc:mysql://localhost:3306/keys_db?serverTimezone=UTC";

	private final static String USER = "root";

	private final static String PWD = "root";

	@Override
	public Integer create(KeyItem keyItem) throws RepositoryException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(CONN_STR, USER, PWD);

			String sql = "INSERT INTO KEYS_TBL(PATH, KEY_STR) VALUES(?,?)";

			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, keyItem.getPath());
			pstmt.setString(2, keyItem.getKey());

			int updates = pstmt.executeUpdate();
			if (updates == 1) {
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					return rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			throw new RepositoryException(e);
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}

		// ID Was not generated
		return null;
	}

}
