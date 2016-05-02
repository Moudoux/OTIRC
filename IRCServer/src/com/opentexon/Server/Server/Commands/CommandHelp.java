/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class CommandHelp extends Command {

	private void runCommand(User user, String line, boolean isConsole) {
		this.sendMessage("== All commands ==");

		this.sendMessage("/kick [Username] [Reason] (OP/Console)");
		this.sendMessage("/ban [Username/IP] [Reason] (OP/Console)");
		this.sendMessage("/tempban [Username/IP] [Minutes] [Reason] (OP/Console)");
		this.sendMessage("/mute [Username] [Reason] (OP/Console)");

		this.sendMessage("/op [Username/IP] (OP/Console)");
		this.sendMessage("/deop [Username/IP] (OP/Console)");
		this.sendMessage("/whois [Username/IP] (OP/Console)");
		this.sendMessage("/unban [Username/IP] (OP/Console)");
		this.sendMessage("/banlist (OP/Console)");
		this.sendMessage("/msg [Username] [Message]");
		this.sendMessage("/users");
		this.sendMessage("/ver");

		if (isConsole) {
			this.sendMessage("/glob [Message]");
			this.sendMessage("/channel [Channel] [Message]");
		} else {
			this.sendMessage("/quit");
			this.sendMessage("/ping");
		}

	}

	public CommandHelp(User user, String line, boolean isConsole) {
		super(isConsole ? null : user);

		boolean hasPerm = true;

		if (hasPerm) {
			if (Main.getInstance().getServer().e.CountArgs(line) == 0) {
				runCommand(isConsole ? null : user, line, isConsole);
			} else {
				this.correctUssage("/help");
			}
		} else {
			this.permissionDenied();
		}
	}

}
