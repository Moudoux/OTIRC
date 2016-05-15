/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import java.io.IOException;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.ConfigManager;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandBan extends Command {

	private void banIP(String ip, User executor, String reason) {

		if (this.getServer().opUsers.contains(ip)) {
			this.getServer().opUsers.remove(ip);
		}

		if (!this.getServer().permBannedUsers.contains(ip)) {
			this.getServer().permBannedUsers.add(ip);
		}

		User bannedUser = this.getUserFromIP(ip);

		if (bannedUser != null) {
			String banner = (executor == null) ? "Console" : executor.getUsername();
			bannedUser.WriteToClient(new P02PacketString(null, "You were permanently banned by " + banner));

			if (!reason.equals("")) {
				bannedUser.WriteToClient(new P02PacketString(null, "Ban reason: " + reason));
			}

			bannedUser.Destroy();
		}

		if (executor != null) {
			this.notifyOpsAndConsole(executor.getUsername() + " permanently banned ip " + ip);
		} else {
			this.notifyOpsAndConsole("Console permanently banned ip " + ip);
		}

		try {
			ConfigManager.getInstance().addListValue("permBanned", ip);
			ConfigManager.getInstance().removeListValue("ops", ip);
			ConfigManager.getInstance().removeListValue("opsplus", ip);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		boolean PermissionPlus = false;
		if (user != null && !isConsole) {
			if (user.PermissionLevel == 1) {
				PermissionPlus = true;
			}
		}
		if (!PermissionPlus && this.getServer().isUserOP(line.getString().split(" ")[1]) && !isConsole) {
			this.permissionDenied();
		} else {
			boolean found = false;
			String ip = line.getString().split(" ")[1];

			if (StringUtils.isIPAddress(ip)) {
				found = true;
			} else {
				for (User u : this.getServer().users) {
					if (u.getUsername().toLowerCase().equals(ip.toLowerCase())) {
						found = true;
						ip = u.getIp();
						break;
					}
				}
			}

			if (!found) {
				this.userNotFound();
			} else {

				String reason = Main.getInstance().getServer().e.CountArgs(line.getString()) >= 2 ? line.getString()
						.replace(line.getString().split(" ")[0] + " " + line.getString().split(" ")[1] + " ", "") : "";

				banIP(ip, user, reason);
			}
		}
	}

	public CommandBan(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/ban [Username/IP] [Message]", line, 1);
	}

}
