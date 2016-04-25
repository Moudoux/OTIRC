package com.opentexon.Server.Server;

import com.opentexon.Server.Main.Main;

/**
 * All messages the server will do
 * 
 * @author Alexander
 *
 */
public class MessageManager {

	/*
	 * Settings
	 */
	private String consoleUser = "Console";

	/*
	 * Ban messages
	 */
	public String banMessage(User user, String line, boolean isConsole) {
		return isConsole ? "§7You were §cbanned §7by §a" + consoleUser : "§7You were §cbanned§7 by §a" + user.Username;
	}

	public String banMessage1(User user, String line, boolean isConsole, User bannedUser) {
		return "§cBanned §a" + line.split(" ")[1] + "@" + bannedUser.Ip;
	}

	public String banMessage2(User user, String line, boolean isConsole) {
		return isConsole ? "§a" + consoleUser + "§7 banned §a" + line.split(" ")[1]
				: "§a" + user.Username + "§7 banned §a" + line.split(" ")[1];
	}

	/*
	 * ChannelMessageCommand messages
	 */
	public String channelMessage(User user, String line, boolean isConsole) {
		return "§7Your message contains §cillegal§7 chracters";
	}

	/**
	 * The main function that handles the message from a user and converts it
	 * into a message for the channel
	 * 
	 * @param user
	 * @param line
	 * @param isConsole
	 * @return
	 */
	public String channelMessage1(User user, String line, boolean isConsole) {
		String message = line;
		message = message.replace("channelmsg ", "");
		String inPrefix = "§eUser";

		/*
		 * Change this to users who should have special prefixes
		 */
		if (user.Username.toLowerCase().equals("thijminecraft02")) {
			inPrefix = "§bBeta Tester";
		}
		if (user.Username.toLowerCase().equals("deftware")) {
			inPrefix = "§cCreator";
		}

		String prefix = "§7[" + inPrefix + "§7] ";
		return user.isOP ? "§7[§aOP§7] " + prefix + " §a" + user.Username + " §7-> " + message
				: prefix + " §a" + user.Username + " §7-> " + message;
	}

	/*
	 * Deop messages
	 */
	public String deopMessage(User user, String line, boolean isConsole) {
		return isConsole ? "§aYou §7were §cdeopped §7by §a" + consoleUser
				: "§aYou §7were §cdeopped §7by §a" + user.Username;
	}

	public String deopMessage1(User user, String line, boolean isConsole, User deoppedUser) {
		return "§cDeopped §a" + line.split(" ")[1] + "@" + deoppedUser.Ip;
	}

	public String deopMessage2(User user, String line, boolean isConsole, User deoppedUser) {
		return isConsole ? "§a" + consoleUser + " §cdeopped §a" + line.split(" ")[1]
				: "§a" + user.Username + " §cdeopped §a" + line.split(" ")[1];
	}

	/*
	 * Help messages
	 */
	public String helpMessage() {
		return "§7== §6All commands§7 ==";
	}

	/*
	 * Join messages
	 */
	public String joinMessage() {
		return "§7Client §cdisconnected";
	}

	public String joinMessage1() {
		return "§cInvalid client login arguments, login failed";
	}

	public String joinMessage2() {
		return "§cUsername taken, login failed";
	}

	public String joinMessage3() {
		return "§7You are §cbanned §7from this server, login failed";
	}

	public String joinMessage5() {
		return "§7Invalid login arguments, please specify channel and usernam, login failede";
	}

	public String joinMessage6() {
		return "§7A user with your username is alredy logged in, login failed";
	}

	public String joinMessage8() {
		return "§7You have reached the max server connections, login failed";
	}

	public String joinMessage4(User u) {
		return "§7User §a" + u.Username + "@" + u.socket.getInetAddress().toString().substring(1) + " §7has joined";
	}

	public String joinMessage7() {
		return "§7Welcome to §a" + Main.getInstance().getServer().ServerName;
	}

	/*
	 * Kick messages
	 */
	public String kickMessage(User user, String line, boolean isConsole, User kickedUser) {
		return isConsole ? "§aYou §7were §ckicked §7by §a" + consoleUser
				: "§aYou §7were §ckicked§7 by §a" + user.Username;
	}

	public String kickMessage1(User user, String line, boolean isConsole, User kickedUser) {
		return "§cKicked §a" + kickedUser.Username + "@" + kickedUser.Ip;
	}

	public String kickMessage2(User user, String line, boolean isConsole) {
		return isConsole ? "§a" + consoleUser + " §ckicked §a" + line.split(" ")[1]
				: "§a" + user.Username + " §ckicked §a" + line.split(" ")[1];
	}

	/*
	 * Msg command messages
	 */
	public String msgMessage(boolean isConsole, User sender, User reciver, String line) {
		return isConsole
				? "§7[§a" + consoleUser + " §7-> §aMe§7] §7-> " + line.replace("/msg " + line.split(" ")[1] + " ", "")
				: "§7[§a" + sender.Username + " §7-> §aMe§7] §7-> "
						+ line.replace("/msg " + line.split(" ")[1] + " ", "");
	}

	/*
	 * Unban messages
	 */
	public String unbanMessage(String ip, User u) {
		return u.equals(null) ? "§a" + consoleUser + " unbanned ip " + ip : "§a" + u.Username + " unbanned ip " + ip;
	}

	/*
	 * Mute messages
	 */
	public String muteMessage(User user, String line, boolean isConsole, User muted) {
		return isConsole ? "§aYou§7 were §cmuted by §a" + consoleUser : "§aYou§7 were §cmuted§7 by §a" + user.Username;
	}

	public String muteMessage1(User user, String line, boolean isConsole, User muted) {
		return "§cMuted §a" + line.split(" ")[1] + "@" + muted.Ip;
	}

	public String muteMessage2(User user, String line, boolean isConsole, User muted) {
		return isConsole ? "§a" + consoleUser + " §cmuted §a" + line.split(" ")[1]
				: "§a" + user.Username + " §cmuted §a" + line.split(" ")[1];
	}

	/*
	 * Unmute messages
	 */
	public String unmuteMessage(User user, String line, boolean isConsole, User muted) {
		return isConsole ? "§aYou§7 were §aunmuted by §a" + consoleUser
				: "§aYou§7 were §aunmuted§7 by §a" + user.Username;
	}

	public String unmuteMessage1(User user, String line, boolean isConsole, User muted) {
		return "§aUnmuted §a" + line.split(" ")[1] + "@" + muted.Ip;
	}

	public String unmuteMessage2(User user, String line, boolean isConsole, User muted) {
		return isConsole ? "§a" + consoleUser + " §aunmuted §a" + line.split(" ")[1]
				: "§a" + user.Username + " §aunmuted §a" + line.split(" ")[1];
	}

	/*
	 * Op messages
	 */
	public String opMessage(User user, String line, boolean isConsole, User Opped) {
		return isConsole ? "§aYou §7were §aopped§7 by §a" + consoleUser
				: "§aYou §7were §aopped §7by §a" + user.Username;
	}

	public String opMessage1(User user, String line, boolean isConsole, User Opped) {
		return "§aOpped " + line.split(" ")[1] + "@" + Opped.Ip;
	}

	public String opMessage2(User user, String line, boolean isConsole, User Opped) {
		return isConsole ? "§a" + consoleUser + " opped " + line.split(" ")[1]
				: "§a" + user.Username + " opped " + line.split(" ")[1];
	}

	/*
	 * Users command messages
	 */
	public String usersMessage() {
		return "== §6All users online§7 ==";
	}

	public String usersMessage1() {
		return "Online users:§a";
	}

	public String usersMessage2() {
		return "Online ops:§a";
	}

	public String usersMessage3() {
		return "Banned users:§a";
	}

	public String usersMessage4() {
		return "Total logins:§a";
	}

	/*
	 * Whois command Messages
	 */
	public String whoisMessage(User user, String line, boolean isConsole, User lookedUpUser) {
		return "== §6Whois " + lookedUpUser.Username + " §7==";
	}

	public String whoisMessage1(User user, String line, boolean isConsole, User lookedUpUser) {
		return "IP:§a " + lookedUpUser.Ip;
	}

	public String whoisMessage2(User user, String line, boolean isConsole, User lookedUpUser) {
		return "Channel:§a " + lookedUpUser.Channel;
	}

	public String whoisMessage3(User user, String line, boolean isConsole, User lookedUpUser) {
		return isConsole ? "§a" + consoleUser + " §7made a whois lookup on §a" + line.split(" ")[1]
				: "§a" + user.Username + " §7made a whois lookup on §a" + line.split(" ")[1];
	}

	/*
	 * Events messages
	 */
	public String userJoin(User u) {
		return "User §a" + u.Username + "§7 has §ajoined";
	}

	public String userLeave(User u) {
		return "User §a" + u.Username + " §7has §cleft";
	}

	public String globMessageFromConsole(String message) {
		return "§a" + consoleUser + " §7-> " + message.replace("glob ", "");
	}

	public String channelMessageFromConsole(String message) {
		return "§a" + consoleUser + " §7-> " + message.replace("channel " + message.split(" ")[1] + " ", "");
	}

	/*
	 * General messages
	 */
	public String muted() {
		return "§cYou are muted";
	}

	public String ServerVersion() {
		return "§7Server version:§a";
	}

	public String commandNotFound() {
		return "§cUnkown command, type §6/help §cfor all commands";
	}

	public String permissionDenied(User user, String line, boolean isConsole) {
		return "§cPermission denied";
	}

	public String correctUssage(User user, String line, boolean isConsole) {
		return "§cCorrect ussage is§6";
	}

	public String userNotFound(User user, String line, boolean isConsole) {
		return "§cUser not found";
	}
}
