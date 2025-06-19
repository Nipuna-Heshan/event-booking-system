package dao;

import model.CartItem;
import model.Event;
import model.Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserCartDaoImpl implements UserCartDao {
    private final String TABLE_NAME = "user_cart";
    private List<Event> events;

    public UserCartDaoImpl(){}


    @Override
    public void setup() throws SQLException {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        try (
             Statement stmt = connection.createStatement();) {
//			stmt.executeUpdate("DROP TABLE IF EXISTS " + TABLE_NAME);
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    "username TEXT NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "location TEXT NOT NULL, " +
                    "day TEXT NOT NULL, " +
                    "quantity INTEGER NOT NULL" +
                    ")";
            stmt.executeUpdate(sql);
        }
    }


    @Override
    public void addToCart(String username, Event event, int quantity) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + "(username, title, location, day, quantity) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, event.getTitle());
            stmt.setString(3, event.getLocation());
            stmt.setString(4, event.getDay());
            stmt.setInt(5, quantity);
            stmt.executeUpdate();
        }
    }

    public void updateQty(String title, String location, String day, int quantity) throws SQLException{
        String sql = "UPDATE " + TABLE_NAME + " SET quantity = ? WHERE title = ? AND location = ? AND day = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, title);
            stmt.setString(3, location);
            stmt.setString(4, day);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<CartItem> getUserCart(String username, Model model) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        events = model.getEventDao().getAllEvents();
        String sql = "SELECT title, location, day, quantity FROM " + TABLE_NAME + " WHERE username = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    String location = rs.getString("location");
                    String day = rs.getString("day");
                    int quantity = rs.getInt("quantity");
                    for (Event item: events) {
                        if (title.equals(item.getTitle()) && location.equals(item.getLocation()) && day.equals(item.getDay())){
                            items.add(new CartItem(item, quantity));
                        }
                    }
                }
            }
        }
        return items;
    }

    @Override
    public void removeUserCart(String username) throws SQLException {
        String sql = "DELETE FROM user_cart WHERE username = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
}
