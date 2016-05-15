/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Client.Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.opentexon.Client.Main.Main;
import com.opentexon.Crypto.CryptoManager;

public class Client {

	public String ip;
	public int port;
	public String username;
	public String channel;

	private DataOutputStream outToServer;
	private Socket socket;

	private boolean initialized = false;
	private boolean Connected = false;

	private int connectionDelay = 0;
	private ScheduledExecutorService executor;

	public boolean isConnected() {
		return Connected;
	}

	public Client(String ip, int port, String username, String channel) {
		this.ip = ip;
		this.port = port;
		this.username = username;
		this.channel = channel;
	}

	public void PrintMessage(String message) {
		Main.getInstance().getLogger().printInfoMessage(message);
	}

	public void Start() {
		Thread client = new Thread() {
			@Override
			public void run() {
				try {
					Main.getInstance().getClient().Run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		client.start();
	}

	public void Reconnect() {
		Runnable unbanTimer = new Runnable() {
			@Override
			public void run() {
				if (connectionDelay >= 3) {
					connectionDelay = 0;
					doReconnect();
				} else {
					connectionDelay += 1;
				}
			}
		};

		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(unbanTimer, 0, 1, TimeUnit.SECONDS);

	}

	private void doReconnect() {
		Thread client = new Thread() {
			@Override
			public void run() {
				try {
					Main.getInstance().getClient().Run();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		client.start();
		try {
			executor.shutdown();
		} catch (Exception ex) {

		}
	}

	private void Run() {
		if (!initialized) {
			initialized = true;
		}

		PrintMessage("Connecting to server...");

		try {
			socket = new Socket(ip, port);

			outToServer = new DataOutputStream(socket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			outToServer.writeBytes(CryptoManager.encode("JOIN " + username + " " + channel) + "\n");
			outToServer.flush();

			String line = null;

			while ((line = inFromServer.readLine()) != null && !socket.isClosed()) {
				Connected = true;
				onRecive(line);
			}

			try {
				outToServer.close();
				inFromServer.close();
				socket.close();
			} catch (Exception e) {

			}

			PrintMessage("You were disconnected by the server or the server is not online.");

		} catch (Exception ex) {
			try {
				socket.close();
			} catch (Exception e) {

			}
			PrintMessage("Failed to connect to server.");
		}

		Connected = false;
		PrintMessage("Reconnecting to server...");
		this.Reconnect();
	}

	private void onRecive(String line) {
		line = CryptoManager.decode(line);
		Main.getInstance().getLogger().printInfoMessage(line);
	}

	public void onInput(String line) {
		try {
			if (line.startsWith("/")) {
				outToServer.writeBytes(CryptoManager.encode(line) + "\n");
				outToServer.flush();
			} else {
				outToServer.writeBytes(CryptoManager.encode("/channelmsg " + line) + "\n");
				outToServer.flush();
			}
		} catch (Exception ex) {
			PrintMessage("You are not connected to a server.");
		}
	}

}
