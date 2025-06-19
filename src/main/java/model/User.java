package model;

public class User {
	private String username;
	private String password;
	private String preferredName;

	public User(String username, String preferredName, String password) {
		this.username = username;
		this.preferredName = preferredName;
		this.password = password;
	}

	public User() {

	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPreferredName(){return preferredName;}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPreferredName(String preferredName){this.preferredName = preferredName;}
}
