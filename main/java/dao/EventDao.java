package dao;

import model.Event;

import java.sql.SQLException;
import java.util.List;

public interface EventDao {
    void setup() throws SQLException;
    List<Event> getAllEvents() throws SQLException;
    void saveEvents(List<Event> events) throws SQLException;
    Event findEvent(String title, String location, String day) throws SQLException;
    void updateEventEnabledStatus(int eventId, boolean isEnabled) throws SQLException;
    void addEvent(Event event) throws SQLException;
    void updateEventDetails(Event event) throws SQLException;
    void deleteEvent(int eventId) throws SQLException;
}
