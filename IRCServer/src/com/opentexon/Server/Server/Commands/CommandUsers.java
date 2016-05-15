/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;

public class CommandUsers extends Command {

	@Override
	@SuppressWarnings("unused")
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		int counter = 0;
		int opCounter = 0;
		int banned = 0;

		for (User u : Main.getInstance().getServer().users) {
			counter += 1;
			if (u.isOP()) {
				opCounter += 1;
			}
		}

		for (String s : Main.getInstance().getServer().permBannedUsers) {
			banned += 1;
		}

		int tempbanedUsers = 0;

		for (String s : Main.getInstance().getServer().tempBannedUsers) {
			tempbanedUsers += 1;
		}

		this.sendMessage("== All users ==");
		this.sendMessage("Users online: " + String.valueOf(counter));
		this.sendMessage("Operators online: " + String.valueOf(opCounter));
		this.sendMessage("Permbanned users: " + String.valueOf(banned));
		this.sendMessage("Tempbanned users: " + String.valueOf(tempbanedUsers));
		this.sendMessage("Total logins: " + String.valueOf(Main.getInstance().getServer().totalLogins));

	}

	public CommandUsers(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(false, "/users", line, 0);
	}

}
