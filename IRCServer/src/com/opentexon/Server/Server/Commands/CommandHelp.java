/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server.Commands;

import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Packets.P02PacketString;

public class CommandHelp extends Command {

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		this.sendMessage("== All commands ==");

		this.sendMessage("/msg [Username | IP] [Message] - Sends a private message to a user");
		this.sendMessage("/r [Message] - Quick reply to a private message");
		this.sendMessage("/lockdown - Toggles server lockdown");
		this.sendMessage("/users - Lists total users online");
		this.sendMessage("/ops - Lists all operators online");
		this.sendMessage("/ver - Displays the server version");

		boolean flag = isConsole ? true : user.isOP();

		if (flag) {
			this.sendMessage("/kick [Username | IP | *] [Reason] - Kicks a user or ip");
			this.sendMessage("/ban [Username | IP] [Reason] - Bans a user or ip");
			this.sendMessage("/tempban [Username | IP] [Minutes] [Reason] - Tempbans a user or ip");
			this.sendMessage("/tempmute [Username | IP] [Minutes] [Reason] - Tempmutes a user or ip");
			this.sendMessage("/mute [Username | IP] [Reason] - Mutes a user or ip");

			this.sendMessage("/op [Username | IP] - Ops a user or ip");
			this.sendMessage("/deop [Username | IP] - Deops a user or ip");
			this.sendMessage("/whois [Username | IP] - Lookup on a user or ip");
			this.sendMessage("/unban [Username | IP] - Unbans a user or ip");
			this.sendMessage("/banlist - Shows all banned users");
			this.sendMessage("/whitelist [add | remove | list] [Username | Ip] - Shows all banned users");
		}

		if (isConsole) {
			this.sendMessage("/glob [Message] - Sends a global message to all channels");
			this.sendMessage("/channel [Channel] [Message] - Sends a message to a specific channel");
		} else {
			this.sendMessage("/ignore [Username] - Ignores a user");
			this.sendMessage("/quit - Disconnects from the server");
			this.sendMessage("/ping - Returns pong");
		}

	}

	public CommandHelp(User user, P02PacketString line, boolean isConsole) {
		super(isConsole ? null : user);
		this.execute(false, "/help", line, 0);
	}

}
