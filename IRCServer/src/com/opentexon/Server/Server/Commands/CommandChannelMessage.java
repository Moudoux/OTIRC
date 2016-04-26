/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandChannelMessage {

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
					u.WriteToClient(Main.getInstance().getServer().messages.channelMessage1(user, line, false));
				}
			}
			Main.getInstance().getLogger().printInfoMessage(user.Channel + "@" + user.Username + " -> " + message);
		} else {
			user.WriteToClient(Main.getInstance().getServer().messages.channelMessage(user, line, false));
		}
	}

	public CommandChannelMessage(User user, String line) {
		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) >= 1) {
				runCommand(user, line);
			} else {
				String correctUssage = Main.getInstance().getServer().messages.correctUssage(user, line, false)
						+ " /channelmsg [Message]";
				user.WriteToClient(correctUssage);
			}
		} else {
			user.WriteToClient(Main.getInstance().getServer().messages.permissionDenied(user, line, false));
		}
	}

}
