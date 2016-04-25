package com.opentexon.Server.Server;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.Commands.CommandBan;
import com.opentexon.Server.Server.Commands.CommandBanlist;
import com.opentexon.Server.Server.Commands.CommandChannelMessage;
import com.opentexon.Server.Server.Commands.CommandDeop;
import com.opentexon.Server.Server.Commands.CommandHelp;
import com.opentexon.Server.Server.Commands.CommandKick;
import com.opentexon.Server.Server.Commands.CommandMsg;
import com.opentexon.Server.Server.Commands.CommandMute;
import com.opentexon.Server.Server.Commands.CommandOp;
import com.opentexon.Server.Server.Commands.CommandUnban;
import com.opentexon.Server.Server.Commands.CommandUnmute;
import com.opentexon.Server.Server.Commands.CommandUsers;
import com.opentexon.Server.Server.Commands.CommandWhois;

/**
 * All events
 * 
 * @author Alexander
 *
 */
public class Events {

	/**
	 * Prints a message to either console or a user
	 * 
	 * @param u
	 * @param isConsole
	 * @param line
	 */
	public void printMessageToUserOrConsole(User u, boolean isConsole, String line) {
		if (isConsole) {
			Main.getInstance().getLogger().printInfoMessage(line);
		} else {
			u.WriteToClient(line);
		}
	}

	/**
	 * Sends a message to all users who are op and the console
	 * 
	 * @param message
	 */
	public void NotifyOpsAndConsole(String message) {
		for (User u : Main.getInstance().getServer().users) {
			if (u.isOP) {
				u.WriteToClient(message);
			}
		}
		Main.getInstance().getLogger().printInfoMessage(message);
	}

	/**
	 * Counts all args in a string
	 * 
	 * @param s
	 * @return
	 */
	@SuppressWarnings("unused")
	public int CountArgs(String s) {
		int res = -1;
		for (String n : s.split(" ")) {
			res += 1;
		}
		return res;
	}

	@SuppressWarnings("unused")
	public void proccessCommand(String line, User user, boolean isConsole) {
		line = line.substring(1);
		if (line.equals("help")) {
			CommandHelp j = new CommandHelp(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("kick")) {
			CommandKick j = new CommandKick(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("glob") && CountArgs(line) >= 1 && isConsole) {
			for (User u : Main.getInstance().getServer().users) {
				u.WriteToClient(Main.getInstance().getServer().messages.globMessageFromConsole(line));
			}
		} else if (line.startsWith("users")) {
			CommandUsers j = new CommandUsers(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("ban") && !line.startsWith("banlist")) {
			CommandBan j = new CommandBan(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("mute")) {
			CommandMute j = new CommandMute(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("unmute")) {
			CommandUnmute j = new CommandUnmute(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("op")) {
			CommandOp j = new CommandOp(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("deop")) {
			CommandDeop j = new CommandDeop(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("whois")) {
			CommandWhois j = new CommandWhois(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("msg")) {
			CommandMsg j = new CommandMsg(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("banlist")) {
			CommandBanlist j = new CommandBanlist(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("unban")) {
			CommandUnban j = new CommandUnban(isConsole ? null : user, line, isConsole);
		} else if (line.startsWith("ver")) {
			if (isConsole) {
				Main.getInstance().getLogger()
						.printWarningMessage(Main.getInstance().getServer().messages.ServerVersion() + " "
								+ Main.getInstance().getServer().version);
			} else {
				user.WriteToClient(Main.getInstance().getServer().messages.ServerVersion() + " "
						+ Main.getInstance().getServer().version);
			}
		} else if (line.startsWith("channel") && CountArgs(line) >= 2 && isConsole) {
			for (User u : Main.getInstance().getServer().users) {
				if (u.Channel.equals(line.split(" ")[1])) {
					u.WriteToClient(Main.getInstance().getServer().messages.channelMessageFromConsole(line));
				}
			}
		} else if (line.startsWith("channelmsg") && !isConsole) {
			CommandChannelMessage j = new CommandChannelMessage(user, line);
		} else if (line.startsWith("quit") && !isConsole) {
			user.WriteToClient("Bye");
			user.Destroy();
		} else {
			if (isConsole) {
				Main.getInstance().getLogger()
						.printWarningMessage(Main.getInstance().getServer().messages.commandNotFound());
			} else {
				user.WriteToClient(Main.getInstance().getServer().messages.commandNotFound());
			}
		}
	}

	/**
	 * Input from console event
	 * 
	 * @param line
	 */
	public void onInput(String line) {
		proccessCommand(line, null, true);
	}

	/**
	 * On recive from client event
	 * 
	 * @param line
	 * @param user
	 */
	public void onRecive(String line, User user) {
		proccessCommand(line, user, false);
	}

	/**
	 * User disconnect event
	 * 
	 * @param user
	 */
	public void onDisconnect(User user) {
		Main.getInstance().getServer().users.remove(user);
		Main.getInstance().getLogger().printInfoMessage(Main.getInstance().getServer().messages.userLeave(user));
		for (User u : Main.getInstance().getServer().users) {
			if (u.Channel.equals(user.Channel)) {
				u.WriteToClient(Main.getInstance().getServer().messages.userLeave(user));
			}
		}
	}

	/**
	 * User join event
	 * 
	 * @param user
	 */
	public void onJoin(User user) {
		for (User u : Main.getInstance().getServer().users) {
			if (u.Channel.equals(user.Channel) && !u.equals(user)) {
				u.WriteToClient(Main.getInstance().getServer().messages.userJoin(user));
			}
		}
	}

}