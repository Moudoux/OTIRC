/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;

public class CommandBanlist extends Command {

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		this.sendMessage("== Banned users ==");

		int banned = 0;
		String bannedUsers = "";
		for (String ip : Main.getInstance().getServer().permBannedUsers) {
			banned += 1;
			if (bannedUsers.equals("")) {
				bannedUsers = ip;
			} else {
				bannedUsers = bannedUsers + ", " + ip;
			}
		}

		if (banned == 0) {
			this.sendMessage("There are no banned users");
		} else {
			this.sendMessage(bannedUsers);
		}
	}

	public CommandBanlist(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(true, "/banlist", line, 0);
	}

}
