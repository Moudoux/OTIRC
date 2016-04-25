package com.opentexon.Server.Server.Commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandBan {

	/**
	 * If a operator can be banned
	 */
	private boolean allowOPBan = false;

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

	/**
	 * Can also ban ip addresses
	 * 
	 * @param user
	 * @param line
	 * @param isConsole
	 */
	private void runCommand(User user, String line, boolean isConsole) {
		if (isIP(line.split(" ")[1])) {
			if (!Main.getInstance().getServer().bannedUsers.contains(line.split(" ")[1])) {
				Main.getInstance().getServer().bannedUsers.add(line.split(" ")[1]);
			}
			Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
					"§cBanned ip " + line.split(" ")[1]);
		} else {
			boolean found = false;

			User bannedUser = null;

			String reason = Main.getInstance().getServer().e.CountArgs(line) >= 2
					? line.replace(line.split(" ")[0] + " " + line.split(" ")[1] + " ", "") : "";

			for (User u : Main.getInstance().getServer().users) {
				if (u.Username.toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
					found = true;
					bannedUser = u;
					break;
				}
			}

			if (found) {

				if (bannedUser.isOP && !allowOPBan) {
					Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
							"You cannot ban a operator");
				} else {

					bannedUser.WriteToClient(Main.getInstance().getServer().messages.banMessage(user, line, isConsole));

					if (!reason.equals("")) {
						bannedUser.WriteToClient("§7Ban reason: §c" + reason);
					}

					if (!Main.getInstance().getServer().bannedUsers.contains(bannedUser.Ip)) {
						Main.getInstance().getServer().bannedUsers.add(bannedUser.Ip);
					}

					Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
							Main.getInstance().getServer().messages.banMessage1(user, line, isConsole, bannedUser));

					if (Main.getInstance().getServer().opUsers.contains(bannedUser.Ip)) {
						Main.getInstance().getServer().opUsers.add(bannedUser.Ip);
					}

					bannedUser.Destroy();

					Main.getInstance().getServer().e.NotifyOpsAndConsole(
							Main.getInstance().getServer().messages.banMessage2(user, line, isConsole));
				}

			}

			if (!found) {
				Main.getInstance().getServer().e.printMessageToUserOrConsole(user, isConsole,
						Main.getInstance().getServer().messages.userNotFound(user, line, isConsole));
			}
		}
	}

	public CommandBan(User user, String line, boolean isConsole) {
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
						+ " /ban [Username] [Message]";
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
