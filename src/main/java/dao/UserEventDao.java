package dao;

import model.Event;
import model.UserEvent;

import java.sql.SQLException;
import java.util.List;

public interface UserEventDao {
    void setup() throws SQLException;
    void addUserEvent(UserEvent event) throws SQLException;
    List<UserEvent> getEventsByUsername(String username) throws SQLException;
    boolean hasBookingsForEvent(String title, String location, String day) throws SQLException;
    List<UserEvent> getAllUserEvents() throws SQLException;
}
