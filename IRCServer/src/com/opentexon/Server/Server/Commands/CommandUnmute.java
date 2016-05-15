/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;

public class CommandUnmute extends Command {

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		boolean PermissionPlus = false;
		if (user != null && !isConsole) {
			if (user.PermissionLevel == 1) {
				PermissionPlus = true;
			}
		}
		boolean flag = !PermissionPlus && this.getServer().isUserOP(line.getString().split(" ")[1]) && !isConsole;
		if (!isConsole) {
			if (line.getString().split(" ")[1].toLowerCase().equals(user.getUsername().toLowerCase())
					|| line.getString().split(" ")[1].toLowerCase().equals(user.getIp().toLowerCase())) {
				flag = true;
			}
		}
		if (flag) {
			this.permissionDenied();
		} else {
			boolean found = false;

			for (User u : Main.getInstance().getServer().users) {
				if (u.getUsername().toLowerCase().equals(line.getString().split(" ")[1].toLowerCase())) {
					u.setMuted(false);

					if (Main.getInstance().getServer().isTempMuted(u.getIp())) {
						Main.getInstance().getServer().removeTempMute(u.getIp());
					}

					String executor = isConsole ? "Console" : user.getUsername();

					if (Main.getInstance().getServer().permMutedUsers.contains(u.getIp())) {
						Main.getInstance().getServer().permMutedUsers.remove(u.getIp());
					}

					u.WriteToClient(new P02PacketString(null, executor + " unmuted you"));

					this.sendMessage("Unmuted " + u.getUsername());
					this.notifyOpsAndConsole(executor + " unmuted " + u.getUsername());

					found = true;
					break;
				}
			}

			if (!found) {
				this.userNotFound();
			}
		}
	}

	public CommandUnmute(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/unmute [Username]", line, 1);
	}

}
