package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandMute {

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;

		String reason = Main.getInstance().getServer().e.CountArgs(line) >= 2
				? line.replace(line.split(" ")[0] + " " + line.split(" ")[1] + " ", "") : "";

		for (User u : Main.getInstance().getServer().users) {
			if (u.Username.toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
				u.WriteToClient(Main.getInstance().getServer().messages.muteMessage(user, line, isConsole, u));

				Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
						Main.getInstance().getServer().messages.muteMessage1(user, line, isConsole, u));

				u.isMuted = true;

				if (!reason.equals("")) {
					u.WriteToClient("§7Mute reason: §c" + reason);
				}

				Main.getInstance().getServer().e.NotifyOpsAndConsole(
						Main.getInstance().getServer().messages.muteMessage2(user, line, isConsole, u));

				found = true;
				break;
			}
		}

		if (!found) {
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
					Main.getInstance().getServer().messages.userNotFound(user, line, isConsole));
		}
	}

	public CommandMute(User user, String line, boolean isConsole) {
		boolean hasPerm = false;
		if (isConsole) {
			hasPerm = true;
		} else {
			if (user.isOP) {
				hasPerm = true;
			}
		}

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) >= 1) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				String correctUssage = Main.getInstance().getServer().messages.correctUssage(user, line, isConsole)
						+ " /mute [Username] [Message]";
				if (isConsole) {
					Main.getInstance().getLogger().printWarningMessage(correctUssage);
				} else {
					user.WriteToClient(correctUssage);
				}
			}
		} else {
			user.WriteToClient(Main.getInstance().getServer().messages.permissionDenied(user, line, isConsole));
		}
	}

}
