/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.opentexon.Crypto.CryptoManager;
import com.opentexon.Server.Main.Main;
import com.opentexon.Server.Server.Packets.P00PacketLogin;
import com.opentexon.Utils.WebHelper;

/**
 * The actual server and the listener for clients
 * 
 * @author Alexander
 *
 */
public class Server {

	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<String> bannedUsers = new ArrayList<String>();
	public ArrayList<String> opUsers = new ArrayList<String>();

	/**
	 * Format: ip,time
	 */
	public ArrayList<String> tempBannedUsers = new ArrayList<String>();

	public int ServerPort;
	public String ServerName;

	public Events e = new Events();

	public int totalLogins = 0;

	public long getCurrentTime() {
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmm").format(Calendar.getInstance().getTime());
		return Long.valueOf(timeStamp);
	}

	public long getTempBanTime(int minutes) throws ParseException {
		String myTime = String.valueOf(getCurrentTime());
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
		Date d = df.parse(myTime);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.MINUTE, minutes);
		String newTime = df.format(cal.getTime());

		return Long.valueOf(newTime);
	}

	public void tempBanUser(String ip, int minutes) throws ParseException {
		String key = ip + "," + getTempBanTime(minutes);
		if (!tempBannedUsers.contains(key)) {
			tempBannedUsers.add(key);
		}
	}

	public String getUnbanTime(String ip) {
		String result = "";

		long unbanTime = 0;

		for (String bannedUser : tempBannedUsers) {
			String bannedIP = bannedUser.split(",")[0];
			if (bannedIP.equals(ip)) {
				unbanTime = Long.valueOf(bannedUser.split(",")[1]);
				break;
			}
		}

		String temp = String.valueOf(unbanTime);

		result = temp.substring(0, 4);
		result = result + "/";
		result = result + temp.substring(4, 6);
		result = result + "/";
		result = result + temp.substring(6, 8);
		result = result + " ";
		result = result + temp.substring(8, 10);
		result = result + ":";
		result = result + temp.substring(10, 12);

		return result;
	}

	public boolean isTempBanned(String ip) {
		for (String bannedUser : tempBannedUsers) {
			String bannedIP = bannedUser.split(",")[0];
			if (bannedIP.equals(ip)) {
				return true;
			}
		}
		return false;
	}

	public void removeTempBan(String ip) {
		for (String bannedUser : tempBannedUsers) {
			String bannedIP = bannedUser.split(",")[0];
			if (bannedIP.equals(ip)) {
				tempBannedUsers.remove(bannedUser);
				return;
			}
		}
	}

	public void startTempbanUnbanner() {

		ScheduledExecutorService executor;

		Runnable unbanTimer = new Runnable() {
			@Override
			public void run() {
				for (String bannedUser : tempBannedUsers) {
					long time = Long.valueOf(bannedUser.split(",")[1]);
					if (time <= getCurrentTime()) {
						tempBannedUsers.remove(bannedUser);
						e.NotifyOpsAndConsole("Unbanned tempbanned ip: " + bannedUser.split(",")[0], null);
					}
				}
			}
		};

		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(unbanTimer, 0, 1, TimeUnit.SECONDS);
	}

	/**
	 * Max connections from the same ip
	 */
	public int maxConnections = 3;

	/**
	 * Server object
	 * 
	 * @param ServerName
	 * @param Port
	 */
	public Server(String ServerName, int Port) {
		this.ServerName = ServerName;
		this.ServerPort = Port;
	}

	/**
	 * On recive client login event
	 * 
	 * @param socket
	 */
	@SuppressWarnings("unused")
	public void onRecive(Socket socket) {
		try {
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String line = null;

			Main.getInstance().getLogger().printInfoMessage("Listening for authentication...");

			boolean isEncrypted = true;

			while ((line = inFromClient.readLine()) != null) {
				if (line.contains("JOIN")) {
					isEncrypted = false;
				}
				if (isEncrypted) {
					line = CryptoManager.decode(line);
					Main.getInstance().getLogger().printInfoMessage("Client is using encryption");
				} else {
					Main.getInstance().getLogger().printWarningMessage("Client is not using encryption");
				}
				if (!line.startsWith("JOIN")) {
					Main.getInstance().getLogger().printWarningMessage("Client failed authentication, aborting");
					inFromClient.close();
					socket.close();
					return;
				} else {
					Main.getInstance().getLogger().printInfoMessage("Recived client authentication...");
					P00PacketLogin j = new P00PacketLogin(line, socket, isEncrypted);
					return;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the server
	 */
	public void startServer() {
		ServerSocket welcomeSocket = null;
		try {

			welcomeSocket = new ServerSocket();

			InetSocketAddress insa = new InetSocketAddress("0.0.0.0", ServerPort);

			welcomeSocket.bind(insa);

			Main.getInstance().getLogger().printInfoMessage("Server started");
			Main.getInstance().getLogger()
					.printInfoMessage("Listening for clients on " + WebHelper.getPage("https://api.ipify.org/") + ":"
							+ String.valueOf(ServerPort) + " & localhost:" + String.valueOf(ServerPort));

			startTempbanUnbanner();

			while (true) {
				final Socket connectionSocket = welcomeSocket.accept();

				Thread listen;

				listen = new Thread() {
					@Override
					public void run() {
						Main.getInstance().getLogger().printInfoMessage("Client@"
								+ connectionSocket.getInetAddress().toString().substring(1) + " is connecting...");
						onRecive(connectionSocket);
					}
				};

				listen.start();

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				welcomeSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Main.getInstance().getLogger().printErrorMessage("Failed to start server");
		}
	}

	/**
	 * On console input event
	 * 
	 * @param line
	 */
	public void onInput(String line) {
		e.onInput(line);
	}

	/**
	 * On recive raw from client
	 * 
	 * @param line
	 * @param u
	 */
	public void onReciveFromClient(String line, User u) {
		e.onRecive(line, u);
	}

}
