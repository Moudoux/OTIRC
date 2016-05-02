/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandChannelMessage extends Command {

	private String channelMessage(User user, String line) {
		String message = line;
		message = message.replace("channelmsg ", "");
		String inPrefix = "User";

		/*
		 * Change this to users who should have special prefixes
		 */
		if (user.Username.toLowerCase().equals("thijminecraft02")) {
			inPrefix = "Beta Tester";
		}
		if (user.Username.toLowerCase().equals("deftware")) {
			inPrefix = "Creator";
		}

		String prefix = "[" + inPrefix + "] ";
		return user.isOP ? "[OP] " + prefix + " " + user.Username + " -> " + message
				: prefix + " " + user.Username + " -> " + message;
	}

	private void runCommand(User user, String line) {
		String message = line;
		message = message.replace("channelmsg ", "");

		boolean sendMessage = true;

		if (message.contains("#")) {
			sendMessage = false;
		}

		if (sendMessage) {
			for (User u : Main.getInstance().getServer().users) {
				if (u.Channel.equals(user.Channel)) {
					u.WriteToClient(channelMessage(user, line));
				}
			}
			Main.getInstance().getLogger().printInfoMessage(user.Channel + "@" + user.Username + " -> " + message);
		} else {
			this.sendMessage("Your message contains illegal characters");
		}
	}

	public CommandChannelMessage(User user, String line) {
		super(user);

		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) >= 1) {
				runCommand(user, line);
			} else {
				this.correctUssage("/channelmsg [Message]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
