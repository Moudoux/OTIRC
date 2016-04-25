package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandMsg {

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;
		for (User u : Main.getInstance().getServer().users) {
			if (u.Username.toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
				u.WriteToClient(Main.getInstance().getServer().messages.msgMessage(isConsole, user, u, line));

				found = true;
				break;
			}
		}

		if (!found) {
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
					Main.getInstance().getServer().messages.userNotFound(user, line, isConsole));
		}
	}

	public CommandMsg(User user, String line, boolean isConsole) {
		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) >= 2) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				String correctUssage = Main.getInstance().getServer().messages.correctUssage(user, line, isConsole)
						+ " /msg [Username] [Message]";
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
