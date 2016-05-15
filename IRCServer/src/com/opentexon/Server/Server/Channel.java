package com.opentexon.Server.Server;

import java.util.ArrayList;

public class Channel {

	private ArrayList<User> users = new ArrayList<User>();
	private String password;
	private String channelName;

	public String getName() {
		return channelName;
	}

	public Channel(String channelName, String password) {
		this.password = password;
		this.channelName = channelName;
	}

	public Channel(String channelName) {
		this.password = "";
		this.channelName = channelName;
	}

	public boolean isPasswordProtected() {
		if (!password.equals("")) {
			return true;
		}
		return false;
	}

	public boolean checkPassword(String password) {
		if (this.password.equals(password)) {
			return true;
		}
		return false;
	}

	public void addUser(User u) {
		if (!users.contains(u)) {
			users.add(u);
		}
	}

	public void removeUser(User u) {
		if (users.contains(u)) {
			users.remove(u);
		}
	}

	@SuppressWarnings("unused")
	public int getUsers() {
		int result = 0;
		for (User u : users) {
			result += 1;
		}
		return result;
	}

}
