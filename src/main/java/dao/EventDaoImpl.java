package dao;

import model.Event;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDaoImpl implements EventDao{

    public EventDaoImpl(){
    }

    // 🔧 Setup method to create event table if not exists
    @Override
    public void setup() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS event (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                location TEXT,
                day TEXT,
                price REAL,
                soldTickets INTEGER,
                totalTickets INTEGER,
                is_enabled BOOLEAN DEFAULT 1
            );
        """;
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             Statement stmt = conn.createStatement()) {
//            stmt.executeUpdate("DROP TABLE IF EXISTS event");
            stmt.execute(sql);
            loadInitialDataFromResourceIfEmpty(conn);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // 📥 Load from events.dat in resources if table is empty
    private void loadInitialDataFromResourceIfEmpty(Connection conn) throws IOException, SQLException {
        String countSql = "SELECT COUNT(*) FROM event";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (InputStream in = getClass().getResourceAsStream("/events.dat");
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                    String line;
                    String insertSql = "INSERT INTO event (title, location, day, price, soldTickets, totalTickets) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                        while ((line = reader.readLine()) != null) {
                            String[] parts = line.split(";");
                            if (parts.length < 6) continue;

                            pstmt.setString(1, parts[0]);
                            pstmt.setString(2, parts[1]);
                            pstmt.setString(3, parts[2]);
                            pstmt.setDouble(4, Double.parseDouble(parts[3]));
                            pstmt.setInt(5, Integer.parseInt(parts[4]));
                            pstmt.setInt(6, Integer.parseInt(parts[5]));
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                    }
                }
            }
        }
    }

    @Override
    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM event ORDER BY title ASC";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Event event = new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("day"),
                        rs.getDouble("price"),
                        rs.getInt("soldTickets"),
                        rs.getInt("totalTickets"),
                        rs.getBoolean("is_enabled")
                );
                events.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public void saveEvents(List<Event> events) throws SQLException {
        String deleteSql = "DELETE FROM event";
        String insertSql = "INSERT INTO event (title, location, day, price, soldTickets, totalTickets, is_enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             Statement deleteStmt = conn.createStatement();
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            deleteStmt.execute(deleteSql);

            for (Event event : events) {
                insertStmt.setString(1, event.getTitle());
                insertStmt.setString(2, event.getLocation());
                insertStmt.setString(3, event.getDay());
                insertStmt.setDouble(4, event.getPrice());
                insertStmt.setInt(5, event.getSoldTickets());
                insertStmt.setInt(6, event.getTotalTickets());
                insertStmt.setBoolean(7, event.getEnabled());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Event findEvent(String title, String location, String day) throws SQLException {
        String sql = "SELECT * FROM event WHERE title = ? AND location = ? AND day = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, location);
            stmt.setString(3, day);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("day"),
                        rs.getDouble("price"),
                        rs.getInt("soldTickets"),
                        rs.getInt("totalTickets"),
                        rs.getBoolean("is_enabled")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void updateEventEnabledStatus(int eventId, boolean isEnabled) throws SQLException {
        String sql = "UPDATE event SET is_enabled = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, isEnabled);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void addEvent(Event event) throws SQLException {
        String sql = "INSERT INTO event (title, location, day, price, soldTickets, totalTickets, is_enabled) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getLocation());
            stmt.setString(3, event.getDay());
            stmt.setDouble(4, event.getPrice());
            stmt.setInt(5, event.getSoldTickets());  // allow setting soldTickets manually
            stmt.setInt(6, event.getTotalTickets());
            stmt.setBoolean(7, event.getEnabled());

            stmt.executeUpdate();
        }
    }

    @Override
    public void updateEventDetails(Event event) throws SQLException {
        String sql = "UPDATE event SET location = ?, day = ?, price = ?, totalTickets = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, event.getLocation());
            stmt.setString(2, event.getDay());
            stmt.setDouble(3, event.getPrice());
            stmt.setInt(4, event.getTotalTickets());
            stmt.setInt(5, event.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteEvent(int eventId) throws SQLException {
        String sql = "DELETE FROM event WHERE id = ?";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
    }



}
