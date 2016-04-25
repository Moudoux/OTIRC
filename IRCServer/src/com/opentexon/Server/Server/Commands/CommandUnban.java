package com.opentexon.Server.Server.Commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandUnban {

	private boolean isIP(String line) {
		boolean result = false;
		Pattern p = Pattern.compile(
				"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		for (String s : line.split(" ")) {
			Matcher m = p.matcher(s);
			if (m.find()) {
				result = true;
			}
		}
		return result;
	}

	private void runCommand(User user, String line, boolean isConsole) {
		if (isIP(line.split(" ")[1])) {
			if (Main.getInstance().getServer().bannedUsers.contains(line.split(" ")[1])) {
				Main.getInstance().getServer().bannedUsers.remove(line.split(" ")[1]);
			}
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
					"§aUnbanned ip " + line.split(" ")[1]);
		} else {
			boolean found = false;
			String ip = line.split(" ")[1];

			if (Main.getInstance().getServer().bannedUsers.contains(ip)) {
				found = true;
				Main.getInstance().getServer().bannedUsers.remove(ip);

				Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
						Main.getInstance().getServer().messages.unbanMessage(ip, user));
			}

			if (!found) {
				Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
						Main.getInstance().getServer().messages.userNotFound(user, line, isConsole).replace("User",
								"IP"));
			}
		}
	}

	public CommandUnban(User user, String line, boolean isConsole) {
		boolean hasPerm = false;
		if (isConsole) {
			hasPerm = true;
		} else {
			if (user.isOP) {
				hasPerm = true;
			}
		}

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) == 1) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				String correctUssage = Main.getInstance().getServer().messages.correctUssage(user, line, isConsole)
						+ " /unban [IP Address]";
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
