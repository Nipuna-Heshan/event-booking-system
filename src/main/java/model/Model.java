package model;

import java.io.IOException;
import java.sql.SQLException;

import dao.*;

public class Model {
	private UserDao userDao;
	private User currentUser;
	private UserEventDao userEventDao;
	private UserCartDao userCartDao;
	private EventDao eventDao;

	public Model() {
		this.userDao = new UserDaoImpl();
		this.userEventDao = new UserEventDaoImpl();
		this.userCartDao = new UserCartDaoImpl();
		this.eventDao = new EventDaoImpl();
	}
	
	public void setup() throws SQLException {
		userDao.setup();
		userEventDao.setup();
        userCartDao.setup();
		eventDao.setup();
	}
	public UserDao getUserDao() {
		return userDao;
	}
	
	public User getCurrentUser() {
		return this.currentUser;
	}
	
	public void setCurrentUser(User user) {
		currentUser = user;
	}

	public UserEventDao getUserEventDao() {
		return userEventDao;
	}

	public UserCartDao getUserCartDao() { return userCartDao; }

	public EventDao getEventDao() { return eventDao; }
}
