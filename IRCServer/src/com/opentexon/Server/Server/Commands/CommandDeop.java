/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Server.ConfigManager;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandDeop extends Command {

	private void deopIP(User user, String ip, String executor) {
		if (this.getServer().opUsers.contains(ip)) {
			this.getServer().opUsers.remove(ip);
		}
		if (this.getServer().opUsersPlus.contains(ip)) {
			this.getServer().opUsersPlus.remove(ip);
		}
		if (user != null) {
			user.setOP(false);
			user.WriteToClient(new P02PacketString(null, "You were deopped by " + executor));
		}
		this.notifyOpsAndConsole(executor + " deopped " + ((user == null) ? ip : user.getUsername()));
		try {
			ConfigManager.getInstance().removeListValue("ops", ip);
			ConfigManager.getInstance().removeListValue("opsplus", ip);
		} catch (Exception ex) {
			this.couldNotSaveConfig();
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
			String ip = line.getString().split(" ")[1];
			if (!StringUtils.containsIPAddress(line.getString())) {
				User oppedUser = this.getUserFromUsername(ip);
				if (oppedUser != null) {
					ip = oppedUser.getIp();
				}
			}
			if (this.getServer().opUsers.contains(ip) || this.getServer().opUsersPlus.contains(ip)) {
				this.deopIP(this.getUserFromIP(ip), ip, isConsole ? "Console" : user.getUsername());
			} else {
				this.userNotFound();
			}
		}
	}

	public CommandDeop(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/deop [Username | IP]", line, 1);
	}

}
