package com.javaws.threads.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {

	private final String connStr;

	private final String user;

	private final String pwd;

	public DatabaseConfig() throws IOException {
		super();

		InputStream is = DatabaseConfig.class.getResourceAsStream("/resources/database.properties");
		Properties p = new Properties();

		p.load(is);

		this.connStr = p.getProperty("com.javaws.threads.db.conn_str");
		this.user = p.getProperty("com.javaws.threads.db.user");
		this.pwd = p.getProperty("com.javaws.threads.db.password");
	}

	public String getConnStr() {
		return connStr;
	}

	public String getUser() {
		return user;
	}

	public String getPwd() {
		return pwd;
	}

}
