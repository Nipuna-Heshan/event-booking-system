package dao;

import model.UserEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserEventDaoImpl implements UserEventDao {
    private static final String TABLE_NAME = "user_events";

    public UserEventDaoImpl(){}

    @Override
    public void setup() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "day TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL, " +
                "timeStamp TEXT NOT NULL, " +
                "total DOUBLE NOT NULL" +
                ")";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
//            stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE_NAME);
            stmt.executeUpdate(sql);
        }
    }

    @Override
    public void addUserEvent(UserEvent event) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (username, title, location, day, quantity, timeStamp, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getUsername());
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getLocation());
            stmt.setString(4, event.getDay());
            stmt.setInt(5, event.getQuantity());
            stmt.setString(6, event.getTimeStamp());
            stmt.setDouble(7, event.getTotal());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<UserEvent> getEventsByUsername(String username) throws SQLException {
        List<UserEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new UserEvent(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("day"),
                        rs.getInt("quantity"),
                        rs.getString("timeStamp"),
                        rs.getDouble("total")
                ));
            }
        }
        return events;
    }

    @Override
    public boolean hasBookingsForEvent(String title, String location, String day) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE title = ? AND location = ? AND day = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setString(2, location);
            stmt.setString(3, day);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    @Override
    public List<UserEvent> getAllUserEvents() throws SQLException {
        List<UserEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(new UserEvent(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("day"),
                        rs.getInt("quantity"),
                        rs.getString("timeStamp"),
                        rs.getDouble("total")
                ));
            }
        }
        return events;
    }
}
