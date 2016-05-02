/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.opentexon.Crypto.CryptoManager;
import com.opentexon.Server.Main.Main;

/**
 * User object with listener
 * 
 * @author Alexander
 *
 */
public class User {

	public String Username;
	public String Channel;
	public String Ip;
	public Socket socket;
	public DataOutputStream clientWriter;

	public boolean isOP = false;
	public boolean isMuted = false;
	public boolean isMc = false;

	private Thread ReciverThread;

	private int swearWarnings = 0;
	private int maxSwear = 3;

	private String lastMsg = "";
	private int maxRepeat = 3;
	private int repeatWarnings = 0;

	public boolean isEncrypted = false;

	private void Reciver() {
		BufferedReader inFromClient = null;
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = null;

			while ((line = inFromClient.readLine()) != null && !socket.isClosed()) {
				if (isEncrypted) {
					line = CryptoManager.decode(line);
				}
				if (line.equals("/pong")) {

				} else if (line.equals("/ping")) {
					this.WriteToClient("pong");
				} else {
					if (!isMuted) {
						Filter f = new Filter();
						if (f.isSwearWord(line)) {
							swearWarnings += 1;
							if (swearWarnings > maxSwear) {
								this.WriteToClient("You got tempbanned for swearing");
								Main.getInstance().getServer().tempBanUser(this.Ip, 5040);
								this.WriteToClient("You will be unbanned at: "
										+ Main.getInstance().getServer().getUnbanTime(this.Ip));
								Main.getInstance().getServer().e
										.NotifyOpsAndConsole("Tempbanned " + this.Username + " for swearing", null);
								this.Destroy();
							} else {
								if (maxSwear == swearWarnings) {
									this.WriteToClient("This is your final warning, stop swearing");
								} else {
									this.WriteToClient("Don't swear, you will be tempbanned if you continue, warning "
											+ String.valueOf(swearWarnings) + "/" + String.valueOf(maxSwear));
								}
							}
						} else {

							boolean allowRecive = true;

							if (lastMsg.equals("")) {
								lastMsg = line;
							} else {
								if (lastMsg.toLowerCase().equals(line.toLowerCase())) {
									allowRecive = false;
									repeatWarnings += 1;
									if (repeatWarnings == maxRepeat) {
										this.WriteToClient("This is your final warning, stop spamming");
									} else {
										if (repeatWarnings > maxRepeat) {
											this.WriteToClient("You got tempbanned for spamming");
											Main.getInstance().getServer().tempBanUser(this.Ip, 1440);
											this.WriteToClient("You will be unbanned at: "
													+ Main.getInstance().getServer().getUnbanTime(this.Ip));
											Main.getInstance().getServer().e.NotifyOpsAndConsole(
													"Tempbanned " + this.Username + " for spamming", null);
											this.Destroy();
										} else {
											this.WriteToClient(
													"Don't spam, you will be tempbanned if you continue, warning "
															+ String.valueOf(repeatWarnings) + "/"
															+ String.valueOf(maxRepeat));
										}
									}
								} else {
									lastMsg = line;
									repeatWarnings = 0;
								}
							}

							if (allowRecive) {
								line = f.proccessIPAddresses(line);
								line = f.proccessLinks(line);
								Main.getInstance().getServer().onReciveFromClient(line, this);
							}
						}
					} else {
						this.WriteToClient("You are muted");
					}
				}
			}

			try {
				inFromClient.close();
				clientWriter.close();
				socket.close();
			} catch (IOException e) {

			}

			Main.getInstance().getServer().e.onDisconnect(this);
		} catch (Exception ex) {

			try {
				inFromClient.close();
				clientWriter.close();
				socket.close();
			} catch (IOException e) {

			}
			Main.getInstance().getServer().e.onDisconnect(this);
		}
	}

	public void WriteToClient(String message) {
		try {
			Filter f = new Filter();
			message = f.proccessIPAddresses(message);
			if (isEncrypted) {
				message = CryptoManager.encode(message);
			}
			clientWriter.writeBytes(message + "\n");
			clientWriter.flush();
		} catch (Exception e) {
			Destroy();
		}
	}

	public void Destroy() {
		try {
			socket.close();
			clientWriter.close();
		} catch (IOException e) {

		}
		Main.getInstance().getServer().users.remove(this);
	}

	public User(String username, String channel, Socket socket) {
		this.Username = username;
		this.Channel = channel;
		this.socket = socket;
		this.Ip = socket.getInetAddress().toString().substring(1);

		try {
			this.clientWriter = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {

		}

		ReciverThread = new Thread() {
			@Override
			public void run() {
				try {
					Reciver();
				} catch (Exception e) {

				}
			}
		};

		ReciverThread.start();
	}

}
