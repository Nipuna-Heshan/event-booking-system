package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	//The Singleton Instance of the Class
	private static DatabaseConnection instance;
	private Connection connection;
	// URL pattern for database
	private static final String DB_URL = "jdbc:sqlite:main/resources/database/application.db";

	// Private constructor prevents instantiation from other classes
	private DatabaseConnection() throws SQLException{
		try {
			this.connection = DriverManager.getConnection(DB_URL);
			System.out.println("Database connection has established");
		} catch (SQLException e) {
			System.out.println("Database connection failed: " + e.getMessage());
			throw e;
		}
	}

	//Global Access Point
	public static DatabaseConnection getInstance() throws SQLException {
		// DriverManager is the basic service for managing a set of JDBC drivers
		// Can also pass username and password
		if (instance == null){
			instance = new DatabaseConnection();
		} else if (instance.getConnection().isClosed()) {
			instance = new DatabaseConnection();
		}
		return instance;
	}

	public Connection getConnection() {
		return connection;
	}
}
