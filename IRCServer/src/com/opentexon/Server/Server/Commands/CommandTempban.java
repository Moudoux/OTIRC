package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Utils.StringUtils;

public class CommandTempban extends Command {

	private void banIP(String ip, int minutes, User executor, String reason) {
		if (this.getServer().opUsers.contains(ip)) {
			this.getServer().opUsers.remove(ip);
		}
		User bannedUser = this.getUserFromIP(ip);
		try {
			this.getServer().tempBanUser(ip, minutes);
			if (bannedUser != null) {
				String banner = (executor == null) ? "Console" : executor.Username;
				bannedUser.WriteToClient("You were temporarily banned by " + banner);
				bannedUser.WriteToClient("You will be unbanned at: " + this.getServer().getUnbanTime(ip));
				if (!reason.equals("")) {
					bannedUser.WriteToClient("Ban reason: " + reason);
				}
				bannedUser.Destroy();
			}
			if (bannedUser != null) {
				this.notifyOpsAndConsole(
						executor.Username + " tempbanned ip " + ip + " until " + this.getServer().getUnbanTime(ip));
			} else {
				this.notifyOpsAndConsole("Console tempbanned ip " + ip + " until " + this.getServer().getUnbanTime(ip));
			}
		} catch (Exception ex) {
			this.sendMessage("Could not ban user");
		}
	}

	private void runCommand(User user, String line, boolean isConsole) {
		boolean found = false;

		String ip = line.split(" ")[1];
		String minutes = line.split(" ")[2];

		if (StringUtils.isIPAddress(ip)) {
			found = true;
		} else {
			for (User u : this.getServer().users) {
				if (u.Username.toLowerCase().equals(ip.toLowerCase())) {
					found = true;
					ip = u.Ip;
					break;
				}
			}
		}

		if (!found) {
			this.userNotFound();
		} else {

			String reason = Main.getInstance().getServer().e.CountArgs(line) >= 3
					? line.replace(line.split(" ")[0] + " " + line.split(" ")[1] + " " + line.split(" ")[2] + " ", "")
					: "";

			banIP(ip, Integer.valueOf(minutes), user, reason);
		}
	}

	public CommandTempban(User user, String line, boolean isConsole) {
		super(isConsole ? null : user);

		boolean hasPerm = false;

		if (isConsole) {
			hasPerm = true;
		} else {
			if (user.isOP) {
				hasPerm = true;
			}
		}

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) >= 2) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				this.correctUssage("/tempban [Username/IP] [Minutes] [Reason]");
			}
		} else {
			this.permissionDenied();
		}
	}

}
