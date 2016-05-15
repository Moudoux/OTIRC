/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.ConfigManager;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandMute extends Command {

	private void muteUser(String ip, String reason, String executor) {
		if (!Main.getInstance().getServer().permMutedUsers.contains(ip)) {
			Main.getInstance().getServer().permMutedUsers.add(ip);
		}
		if (this.getUserFromIP(ip) != null) {
			User u = this.getUserFromIP(ip);
			u.WriteToClient(new P02PacketString(null, "You were permanently muted by " + executor));
			if (!reason.equals("")) {
				u.WriteToClient(new P02PacketString(null, "Reason: " + reason));
			}
			ip = u.getUsername();
		}
		this.notifyOpsAndConsole(executor + " permanently muted " + ip);
		try {
			ConfigManager.getInstance().addListValue("permMuted", ip);
		} catch (Exception ex) {
			couldNotSaveConfig();
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
			String reason = Main.getInstance().getServer().e.CountArgs(line.getString()) >= 2 ? line.getString()
					.replace(line.getString().split(" ")[0] + " " + line.getString().split(" ")[1] + " ", "") : "";
			String ip = line.getString().split(" ")[1];
			boolean flag = false;
			if (!StringUtils.containsIPAddress(ip)) {
				User u = this.getUserFromUsername(ip);
				if (u != null) {
					ip = u.getIp();
				} else {
					flag = true;
				}
			}
			if (!flag) {
				this.muteUser(ip, reason, isConsole ? "Console" : user.getUsername());
			} else {
				this.userNotFound();
			}
		}
	}

	public CommandMute(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/mute [Username | IP] [Reason]", line, 1);
	}

}
