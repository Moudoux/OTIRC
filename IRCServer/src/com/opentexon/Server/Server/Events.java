/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.Commands.CommandBan;
import com.opentexon.Server.Server.Commands.CommandBanlist;
import com.opentexon.Server.Server.Commands.CommandDeop;
import com.opentexon.Server.Server.Commands.CommandHelp;
import com.opentexon.Server.Server.Commands.CommandKick;
import com.opentexon.Server.Server.Commands.CommandMsg;
import com.opentexon.Server.Server.Commands.CommandMute;
import com.opentexon.Server.Server.Commands.CommandOp;
import com.opentexon.Server.Server.Commands.CommandOperators;
import com.opentexon.Server.Server.Commands.CommandReply;
import com.opentexon.Server.Server.Commands.CommandTempMute;
import com.opentexon.Server.Server.Commands.CommandTempban;
import com.opentexon.Server.Server.Commands.CommandUnban;
import com.opentexon.Server.Server.Commands.CommandUnmute;
import com.opentexon.Server.Server.Commands.CommandUsers;
import com.opentexon.Server.Server.Commands.CommandWhois;
import com.opentexon.Server.Server.Commands.Whitelist.CommandWhitelist;
import com.opentexon.Server.Server.Commands.Whitelist.WhitelistCmd;
import com.opentexon.Server.Server.Packets.P01PacketChat;
import com.opentexon.Server.Server.Packets.P02PacketString;

/**
 * Main event file, all triggers like Commands, Input from console, Input from
 * users etc will be processed here.
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
	public void printMessageToUserOrConsole(User u, P02PacketString pChat) {
		if (u == null) {
			Main.getInstance().getLogger().printInfoMessage(pChat.getString());
		} else {
			u.WriteToClient(pChat);
		}
	}

	/**
	 * Sends a message to all users who are op and the console
	 * 
	 * @param message
	 */
	public void NotifyOpsAndConsole(P02PacketString message, User executor) {
		for (User u : Main.getInstance().getServer().users) {
			if (u.isOP()) {
				if (executor == null) {
					u.WriteToClient(message);
				} else {
					if (!u.equals(executor)) {
						u.WriteToClient(message);
					}
				}
			}
		}
		Main.getInstance().getLogger().printInfoMessage(message.getString());
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
	public void proccessCommand(P02PacketString pChat, User user, boolean isConsole) {
		String line = pChat.getString();

		if (!isConsole && user != null && !line.startsWith("channelmsg")) {
			Main.getInstance().getLogger().printInfoMessage(user.getUsername() + ": " + line);
		}

		line = line.substring(1);
		if (line.equals("help")) {
			CommandHelp j = new CommandHelp(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("kick")) {
			CommandKick j = new CommandKick(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("glob") && CountArgs(line) >= 1 && isConsole) {
			for (User u : Main.getInstance().getServer().users) {
				u.WriteToClient(new P02PacketString(null, "Console" + " -> " + line.replace("glob ", "")));
			}
		} else if (line.startsWith("users")) {
			CommandUsers j = new CommandUsers(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("tempmute")) {
			CommandTempMute j = new CommandTempMute(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("ban") && !line.startsWith("banlist")) {
			CommandBan j = new CommandBan(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("tempban")) {
			CommandTempban j = new CommandTempban(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("mute")) {
			CommandMute j = new CommandMute(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("unmute")) {
			CommandUnmute j = new CommandUnmute(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("op") && !line.startsWith("ops")) {
			CommandOp j = new CommandOp(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("deop")) {
			CommandDeop j = new CommandDeop(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("whois")) {
			CommandWhois j = new CommandWhois(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("msg")) {
			CommandMsg j = new CommandMsg(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("banlist")) {
			CommandBanlist j = new CommandBanlist(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("unban")) {
			CommandUnban j = new CommandUnban(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("ops")) {
			CommandOperators j = new CommandOperators(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("ver")) {
			this.printMessageToUserOrConsole(isConsole ? null : user,
					new P02PacketString(null, "Server version: " + Main.getInstance().version));
		} else if (line.startsWith("channel") && CountArgs(line) >= 2 && isConsole) {
			for (User u : Main.getInstance().getServer().users) {
				if (u.getChannel().getName().toLowerCase().equals(line.split(" ")[1].toLowerCase())) {
					u.WriteToClient(new P02PacketString(null,
							"Console" + " -> " + line.replace("channel " + line.split(" ")[1] + " ", "")));
				}
			}
		} else if (line.startsWith("r") && !isConsole) {
			CommandReply j = new CommandReply(isConsole ? null : user, pChat, isConsole);
		} else if (line.startsWith("channelmsg") && !isConsole) {
			P01PacketChat j = new P01PacketChat(user, line);
		} else if (line.startsWith("whitelist")) {
			if (!isConsole && !user.isOP()) {
				this.printMessageToUserOrConsole(isConsole ? null : user,
						new P02PacketString(null, "You are not permitted to perform that command"));
			} else {
				if (line.contains("on")) {
					Main.getInstance().getServer().whitelist = true;
					this.NotifyOpsAndConsole(new P02PacketString(null,
							(isConsole ? "Console" : user.getUsername()) + " turned on whitelist"), null);
					try {
						ConfigManager.getInstance().setValue("whitelist", "true");
					} catch (Exception ex) {

					}
				} else if (line.contains("off")) {
					Main.getInstance().getServer().whitelist = false;
					this.NotifyOpsAndConsole(new P02PacketString(null,
							(isConsole ? "Console" : user.getUsername()) + " turned off whitelist"), null);
					try {
						ConfigManager.getInstance().setValue("whitelist", "false");
					} catch (Exception ex) {

					}
				} else if (line.contains("add")) {
					CommandWhitelist j = new CommandWhitelist(isConsole ? null : user, pChat, isConsole,
							WhitelistCmd.Add);
				} else if (line.contains("remove")) {
					CommandWhitelist j = new CommandWhitelist(isConsole ? null : user, pChat, isConsole,
							WhitelistCmd.Remove);
				} else if (line.contains("list")) {
					CommandWhitelist j = new CommandWhitelist(isConsole ? null : user, pChat, isConsole,
							WhitelistCmd.List);
				}
			}
		} else if (line.equals("uptime")) {
			this.printMessageToUserOrConsole(isConsole ? null : user,
					new P02PacketString(null, "Uptime: " + Main.getInstance().getServer().uptime));
		} else if (line.startsWith("quit") && !isConsole) {
			user.WriteToClient(new P02PacketString(null, "Bye"));
			user.Destroy();
		} else {
			this.printMessageToUserOrConsole(isConsole ? null : user,
					new P02PacketString(null, "Unkown command, type /help for all commands"));
		}
	}

	/**
	 * Input from console event
	 * 
	 * @param line
	 */
	public void onInput(String line) {
		proccessCommand(new P02PacketString(null, line), null, true);
	}

	/**
	 * On recive from client event
	 * 
	 * @param line
	 * @param user
	 */
	public void onRecive(P02PacketString pChat, User user) {
		proccessCommand(pChat, user, false);
	}

	/**
	 * User disconnect event
	 * 
	 * @param user
	 */
	public void onDisconnect(User user) {
		user.getChannel().removeUser(user);
		if (user.getChannel().getUsers() == 0) {
			Main.getInstance().getLogger().printInfoMessage("Removed channel " + user.getChannel().getName());
			if (Main.getInstance().getServer().channels.contains(user.getChannel())) {
				Main.getInstance().getServer().channels.remove(user.getChannel());
			}
		}
		for (User u : Main.getInstance().getServer().users) {
			if (!u.equals(user)) {
				if (u.ignoredUsers.contains(user)) {
					u.ignoredUsers.remove(user);
				}
			}
		}
		Main.getInstance().getServer().users.remove(user);
		Main.getInstance().getLogger().printInfoMessage("User " + user.getUsername() + " has left");
		for (User u : Main.getInstance().getServer().users) {
			if (u.getChannel().getName().equals(user.getChannel().getName()) && !u.equals(user)) {
				u.WriteToClient(new P02PacketString(null, "User " + user.getUsername() + " has left"));
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
			if (u.getChannel().getName().equals(user.getChannel().getName()) && !u.equals(user)) {
				u.WriteToClient(new P02PacketString(null, "User " + user.getUsername() + " has joined"));
			}
		}
	}

}
