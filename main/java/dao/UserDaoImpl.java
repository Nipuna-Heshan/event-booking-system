package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.User;

public class UserDaoImpl implements UserDao {
	private final String TABLE_NAME = "users";

	public UserDaoImpl() {
	}

	@Override
	public void setup() throws SQLException {
		try (Connection connection = DatabaseConnection.getInstance().getConnection();
				Statement stmt = connection.createStatement();) {
//			stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE_NAME);
			String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(username VARCHAR(10) NOT NULL," + "preferredName VARCHAR(20) NOT NULL,"
					+ "password VARCHAR(8) NOT NULL," + "PRIMARY KEY (username))";
			stmt.executeUpdate(sql);
		} 
	}

	@Override
	public User getUser(String username, String password) throws SQLException {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ? AND password = ?";
		try (Connection connection = DatabaseConnection.getInstance().getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setString(1, username);
			stmt.setString(2, password);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					User user = new User();
					user.setUsername(rs.getString("username"));
					user.setPreferredName(rs.getString("preferredName"));
					user.setPassword(rs.getString("password"));
					return user;
				}
				return null;
			} 
		}
	}

	@Override
	public User createUser(String username, String preferredName, String password) throws SQLException {
		String sql = "INSERT INTO " + TABLE_NAME + "(username, preferredName, password) VALUES (?, ?, ?)";
		try (Connection connection = DatabaseConnection.getInstance().getConnection();
				PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setString(1, username);
			stmt.setString(2, preferredName);
			stmt.setString(3, password);

			stmt.executeUpdate();
			return new User(username, preferredName, password);
		} 
	}

	public void updateUserProfile(String username,String newPreferredName, String newPassword) throws SQLException {
		String sql = "UPDATE users SET preferredName = ?, password = ? WHERE username = ?";
		try (Connection conn = DatabaseConnection.getInstance().getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, newPreferredName);
			stmt.setString(2, newPassword);
			stmt.setString(3, username);
			stmt.executeUpdate();
		}
	}
}
