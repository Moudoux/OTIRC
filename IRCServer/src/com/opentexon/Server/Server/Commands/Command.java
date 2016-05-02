package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.Events;
import com.opentexon.Server.Server.Server;
import com.opentexon.Server.Server.User;

public class Command {

	private User executor = null;

	public Command(User executor) {
		if (executor == null) {
			this.executor = null;
		} else {
			this.executor = executor;
		}
	}

	public Main getMain() {
		return Main.getInstance();
	}

	public Server getServer() {
		return Main.getInstance().getServer();
	}

	public Events getEvents() {
		return Main.getInstance().getServer().e;
	}

	public void notifyOpsAndConsole(String message) {
		this.getEvents().NotifyOpsAndConsole(message, (executor == null) ? null : executor);
	}

	public User getUserFromIP(String ip) {
		for (User u : this.getServer().users) {
			if (u.Ip.equals(ip)) {
				return u;
			}
		}
		return null;
	}

	public User getUserFromUsername(String username) {
		for (User u : this.getServer().users) {
			if (u.Username.toLowerCase().equals(username.toLowerCase())) {
				return u;
			}
		}
		return null;
	}

	public void sendMessage(String line) {
		if (executor == null) {
			this.getEvents().printMessageToUserOrConsole(null, true, line);
		} else {
			this.getEvents().printMessageToUserOrConsole(executor, false, line);
		}
	}

	/*
	 * Messages
	 */

	public void userNotFound() {
		this.sendMessage("Could not find the specified user");
	}

	public void correctUssage(String ussage) {
		this.sendMessage("Correct ussage is: " + ussage);
	}

	public void permissionDenied() {
		this.sendMessage("You are not permitted to perform this command");
	}

}
