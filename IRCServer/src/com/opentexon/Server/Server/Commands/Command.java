package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.Events;
import com.opentexon.Server.Server.Server;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;

public abstract class Command {

	private User executor = null;

	public Command(User executor) {
		if (executor == null) {
			this.executor = null;
		} else {
			this.executor = executor;
		}
	}

	public abstract void runCommand(User user, P02PacketString line, boolean isConsole);

	public void execute(boolean requiresOP, String correctUssage, P02PacketString command, int args) {
		boolean hasPerm = false;

		if (executor == null) {
			hasPerm = true;
		} else {
			if (executor.isOP()) {
				hasPerm = true;
			}
		}

		if (!requiresOP) {
			hasPerm = true;
		}

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(command.getString()) >= args) {
				this.runCommand((executor == null) ? null : executor, command, (executor == null));
			} else {
				this.correctUssage(correctUssage);
			}
		} else {
			this.permissionDenied();
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
		boolean flag = false;
		if (executor != null) {
			flag = true;
		}
		this.getEvents().NotifyOpsAndConsole(new P02PacketString(null, message), flag ? null : executor);
	}

	public User getUserFromIP(String ip) {
		for (User u : this.getServer().users) {
			if (u.getIp().equals(ip)) {
				return u;
			}
		}
		return null;
	}

	public void couldNotSaveConfig() {
		this.getMain().getLogger().printErrorMessage("Could not save config file");
	}

	public User getUserFromUsername(String username) {
		for (User u : this.getServer().users) {
			if (u.getUsername().toLowerCase().equals(username.toLowerCase())) {
				return u;
			}
		}
		return null;
	}

	public void sendMessage(String line) {
		if (executor == null) {
			this.getEvents().printMessageToUserOrConsole(null, new P02PacketString(null, line));
		} else {
			this.getEvents().printMessageToUserOrConsole(executor, new P02PacketString(null, line));
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
