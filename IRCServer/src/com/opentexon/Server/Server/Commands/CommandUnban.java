/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Server.ConfigManager;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;

public class CommandUnban extends Command {

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		boolean found = false;
		String ip = line.getString().split(" ")[1];

		if (this.getServer().permBannedUsers.contains(ip)) {
			found = true;
			this.getServer().permBannedUsers.remove(ip);
			String executor = isConsole ? "Console" : user.getUsername();
			this.notifyOpsAndConsole(executor + " unbanned ip " + ip);
			if (this.getServer().isTempBanned(ip)) {
				this.getServer().removeTempBan(ip);
			}
		} else {
			if (this.getServer().isTempBanned(ip)) {
				found = true;
				this.getServer().removeTempBan(ip);
				String executor = isConsole ? "Console" : user.getUsername();
				this.notifyOpsAndConsole(executor + " unbanned ip " + ip);
			}
		}

		if (!found) {
			this.userNotFound();
		} else {
			try {
				ConfigManager.getInstance().removeListValue("permBanned", ip);
			} catch (Exception ex) {
				this.couldNotSaveConfig();
			}
		}
	}

	public CommandUnban(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/unban [IP]", line, 1);
	}

}
