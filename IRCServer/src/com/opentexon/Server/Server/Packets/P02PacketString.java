package com.opentexon.Server.Server.Packets;

import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.User;

public class P02PacketString {

	private User sender = null;
	private String string;

	public User getSender() {
		if (sender != null) {
			/*
			 * User
			 */
			return sender;
		}
		/*
		 * Console
		 */
		return Main.getInstance().getServer().serverUser;
	}

	public String getString() {
		return string;
	}

	public P02PacketString(User sender, String string) {
		this.sender = sender;
		this.string = string;
	}

}
