package com.opentexon.Server.Server.Commands.Whitelist;

import com.opentexon.Server.Server.ConfigManager;
import com.opentexon.Server.Server.User;
import com.opentexon.Server.Server.Commands.Command;
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;

public class CommandWhitelist extends Command {

	private WhitelistCmd type;

	public CommandWhitelist(User user, P02PacketString line, boolean isConsole, WhitelistCmd type) {
		super(isConsole ? null : user);
		this.type = type;
		this.execute(true, "/whitelist [Add | Remove | List] [Username | IP]", line, 1);
	}

	@Override
	public void runCommand(User user, P02PacketString line, boolean isConsole) {
		if (!isConsole && !user.isOP()) {
			this.permissionDenied();
		} else {
			String ip = line.getString().split(" ")[2];
			if (!StringUtils.containsIPAddress(ip)) {
				User u = this.getUserFromUsername(line.getString().split(" ")[2]);
				if (u != null) {
					ip = u.getIp();
				} else {
					ip = "";
				}
			}
			if (type.equals(WhitelistCmd.Add)) {
				if (!ip.equals("")) {
					this.addIP(ip, isConsole ? "Console" : user.getUsername());
				} else {
					this.userNotFound();
				}
			} else if (type.equals(WhitelistCmd.Remove)) {
				if (!ip.equals("")) {
					this.removeIP(ip, isConsole ? "Console" : user.getUsername());
				} else {
					this.userNotFound();
				}
			} else {
				this.listIPS();
			}
		}
	}

	private void addIP(String ip, String executor) {
		if (!this.getServer().whitelistedUsers.contains(ip)) {
			this.getServer().whitelistedUsers.add(ip);
		}
		this.notifyOpsAndConsole(executor + " added ip " + ip + " to the whitelist");
		try {
			ConfigManager.getInstance().addListValue("whitelisted", ip);
		} catch (Exception ex) {
			this.couldNotSaveConfig();
		}
	}

	private void removeIP(String ip, String executor) {
		if (this.getServer().whitelistedUsers.contains(ip)) {
			this.getServer().whitelistedUsers.remove(ip);
		}
		this.notifyOpsAndConsole(executor + " removed ip " + ip + " from the whitelist");
		try {
			ConfigManager.getInstance().removeListValue("whitelisted", ip);
		} catch (Exception ex) {
			this.couldNotSaveConfig();
		}
	}

	private void listIPS() {
		String result = "";
		String ip = "";
		for (String s : this.getServer().whitelistedUsers) {
			User u = this.getUserFromIP(s);
			if (u != null) {
				ip = u.getUsername();
			} else {
				ip = s;
			}
			if (!result.equals("")) {
				result = ip;
			} else {
				result = result + ", " + ip;
			}
		}
		this.sendMessage("== Whitelisted users ==");
		this.sendMessage(result);
	}

}
