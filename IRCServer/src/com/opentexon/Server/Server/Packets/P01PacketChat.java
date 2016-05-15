/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Packets;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Commands.Command;
import com.opentexon.Utils.StringUtils;

public class P01PacketChat extends Command {

	private String channelMessage(User user, String line) {
		String message = line;
		message = message.replace("channelmsg ", "");

		String prefix = StringUtils.getPrefix(user);

		if (!user.isOP()) {
			message = message.substring(0, 1).toUpperCase() + message.substring(1).toLowerCase();
		}

		return prefix + " " + user.getUsername() + " -> " + message;
	}

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		String message = line.getString();
		message = message.replace("channelmsg ", "");

		boolean sendMessage = true;

		if (message.contains("#")) {
			sendMessage = false;
		}

		if (message.contains("§")) {
			sendMessage = false;
		}

		if (sendMessage) {
			for (User u : Main.getInstance().getServer().users) {
				if (u.getChannel().getName().equals(user.getChannel().getName()) && !u.ignoredUsers.contains(user)) {
					u.WriteToClient(new P02PacketString(user, channelMessage(user, line.getString())));
				}
			}
			if (!user.isOP()) {
				message = message.substring(0, 1).toUpperCase() + message.substring(1).toLowerCase();
			}
			Main.getInstance().getLogger()
					.printInfoMessage(user.getChannel().getName() + "@" + user.getUsername() + " -> " + message);
		} else {
			this.sendMessage("Your message contains illegal characters");
		}
	}

	public P01PacketChat(User user, String line) {
		super(user);

		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) >= 1) {
				runCommand(user, new P02PacketString(null, line), false);
			} else {
				this.correctUssage("/channelmsg [Message]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
