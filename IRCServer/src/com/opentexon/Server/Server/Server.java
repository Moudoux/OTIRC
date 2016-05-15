/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import com.opentexon.Server.Server.Packets.P02PacketString;
import com.opentexon.Utils.StringUtils;
import com.opentexon.Utils.WebHelper;

/**
 * The actual server and the listener for clients
 * 
 * @author Alexander
 *
 */
public class Server {

	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<String> opUsers = new ArrayList<String>();
	public ArrayList<String> opUsersPlus = new ArrayList<String>();
	public ArrayList<Channel> channels = new ArrayList<Channel>();
	public ArrayList<String> whitelistedUsers = new ArrayList<String>();

	public boolean whitelist = false;

	/*
	 * Format: ip,time
	 */
	public ArrayList<String> tempMutedUsers = new ArrayList<String>();
	public ArrayList<String> tempBannedUsers = new ArrayList<String>();

	public ArrayList<String> permMutedUsers = new ArrayList<String>();
	public ArrayList<String> permBannedUsers = new ArrayList<String>();

	public int ServerPort;
	public String ServerName;

	public Events e = new Events();

	public User serverUser = new User("Server");

	public int totalLogins = 0;

	public String serverIP = "";
	public int Tip = 0;
	public String uptime = "";
	private long uptimeMili = 0;

	public boolean isUserOP(String user) {
		if (StringUtils.containsIPAddress(user)) {
			if (opUsers.contains(user)) {
				return true;
			}
		} else {
			for (User u : users) {
				if (u.getUsername().toLowerCase().equals(user)) {
					return true;
				}
			}
		}

		return false;
	}

	public long getCurrentTime() {
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmm").format(Calendar.getInstance().getTime());
		return Long.valueOf(timeStamp);
	}

	public long getCurrentFiniteTime() {
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
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

	public void addUptimeSecond() throws ParseException {

		long newUptime = uptimeMili;

		long days = TimeUnit.MILLISECONDS.toDays(newUptime);
		newUptime -= TimeUnit.DAYS.toMillis(days);

		long hours = TimeUnit.MILLISECONDS.toHours(newUptime);
		newUptime -= TimeUnit.HOURS.toMillis(hours);

		long minutes = TimeUnit.MILLISECONDS.toMinutes(newUptime);
		newUptime -= TimeUnit.MINUTES.toMillis(minutes);

		long seconds = TimeUnit.MILLISECONDS.toSeconds(newUptime);

		uptime = String.valueOf(days) + " day(s), " + String.valueOf(hours) + " hour(s), " + String.valueOf(minutes)
				+ " minute(s), " + String.valueOf(seconds) + " second(s)";

	}

	public void startUptimeCounter() {
		ScheduledExecutorService executor;

		Runnable unbanTimer = new Runnable() {
			@Override
			public void run() {
				uptimeMili += 1;
			}
		};

		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(unbanTimer, 0, 1, TimeUnit.MILLISECONDS);
	}

	public String tempBanUser(String ip, int minutes) throws ParseException {
		String key = ip + "," + getTempBanTime(minutes);
		if (!tempBannedUsers.contains(key)) {
			tempBannedUsers.add(key);
		}
		return key.split(",")[1];
	}

	public String tempMuteUser(String ip, int minutes) throws ParseException {
		String key = ip + "," + getTempBanTime(minutes);
		if (!tempMutedUsers.contains(key)) {
			tempMutedUsers.add(key);
		}
		return key.split(",")[1];
	}

	public String formatLongDate(String date) {
		String result = "";

		String temp = date;

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

	public String getUnmuteTime(String ip) {
		String result = "";

		long unbanTime = 0;

		for (String bannedUser : tempMutedUsers) {
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

		System.out.println(temp);

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

	public boolean isTempMuted(String ip) {
		for (String bannedUser : tempMutedUsers) {
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

	public void removeTempMute(String ip) {
		for (String bannedUser : tempMutedUsers) {
			String bannedIP = bannedUser.split(",")[0];
			if (bannedIP.equals(ip)) {
				tempMutedUsers.remove(bannedUser);
				return;
			}
		}
	}

	public void startTips() {

		ScheduledExecutorService executor;

		Runnable unbanTimer = new Runnable() {
			@Override
			public void run() {
				for (User u : Main.getInstance().getServer().users) {
					if (Tip == 0) {
						if (u.isMc()) {
							u.WriteToClient(new P02PacketString(null, "You are connected to the IRC"));
							u.WriteToClient(new P02PacketString(null, "You can chat by typing # [Message]"));
							u.WriteToClient(new P02PacketString(null, "Example: # Hi everyone"));
						} else {

						}
					} else if (Tip == 1) {
						u.WriteToClient(new P02PacketString(null, "You can view stats about the irc server here:"));
						u.WriteToClient(new P02PacketString(null, "https://dl.opentexon.com/.irc/status.php"));
					} else if (Tip == 2) {
						if (!u.isMc()) {
							u.WriteToClient(new P02PacketString(null,
									"Download our hacked client Moudoux for Minecraft 1.9 at:"));
							u.WriteToClient(new P02PacketString(null, "https://dl.opentexon.com/Moudoux.zip"));
						}
						Tip = -1;
					}
				}
				Tip += 1;
			}
		};

		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(unbanTimer, 0, 3, TimeUnit.MINUTES);
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
						e.NotifyOpsAndConsole(
								new P02PacketString(null, "Unbanned tempbanned ip: " + bannedUser.split(",")[0]), null);
					}
				}
				for (String bannedUser : tempMutedUsers) {
					long time = Long.valueOf(bannedUser.split(",")[1]);
					if (time <= getCurrentTime()) {
						tempMutedUsers.remove(bannedUser);
						e.NotifyOpsAndConsole(
								new P02PacketString(null, "Unmuted tempmuted ip: " + bannedUser.split(",")[0]), null);
					}
				}
				try {
					addUptimeSecond();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(unbanTimer, 0, 1, TimeUnit.SECONDS);
	}

	/**
	 * Max connections from the same ip
	 */
	public int maxConnections = 2;

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

	private String getPHPFetchInfo() {
		String result = "";

		result = "<h3>OT IRC Server</h3>";

		result = result + "Server version: " + Main.getInstance().version;
		result = result + "<br>";
		result = result + "Server name: " + Main.getInstance().Name;
		result = result + "<br>";
		result = result + "Server address: " + this.serverIP;
		result = result + "<br>";
		result = result + "Server port: " + String.valueOf(this.ServerPort);
		result = result + "<br>";
		result = result + "Online users: " + String.valueOf(this.users.size());
		result = result + "<br>";
		result = result + "Banned users: " + String.valueOf(this.permBannedUsers.size() + this.tempBannedUsers.size());
		result = result + "<br>";
		result = result + "Total logins: " + String.valueOf(this.totalLogins);
		result = result + "<br>";
		result = result + "Server uptime: " + this.uptime;
		result = result + "<br><br>";
		result = result + "Current online users:";

		int maxUsers = 50;
		int current = 0;

		for (User u : this.users) {
			if (current > maxUsers) {
				break;
			}

			result = result + "<br>";
			result = result + u.getUsername() + ":" + u.getChannel().getName() + ", Extra info: " + u.extraInfo;

			current += 1;
		}

		return result;
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

			boolean isEncrypted = true;

			while ((line = inFromClient.readLine()) != null) {
				if (line.toLowerCase().startsWith("ping")) {
					try {
						DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
						writer.writeBytes(getPHPFetchInfo() + "\n");
						writer.flush();
						inFromClient.close();
						socket.close();
					} catch (IOException e) {

					}

					return;
				}
				Main.getInstance().getLogger().printInfoMessage(
						"Client@" + socket.getInetAddress().toString().substring(1) + " is connecting...");
				Main.getInstance().getLogger().printInfoMessage("Listening for authentication...");
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

			Main.getInstance().getLogger().printInfoMessage("Loading config...");

			// Load config

			this.opUsers = ConfigManager.getInstance().getListValue("ops", "0.0.0.0");
			this.opUsersPlus = ConfigManager.getInstance().getListValue("opsplus", "0.0.0.0");
			this.permMutedUsers = ConfigManager.getInstance().getListValue("permMuted", "0.0.0.0");
			this.permBannedUsers = ConfigManager.getInstance().getListValue("permBanned", "0.0.0.0");
			this.whitelistedUsers = ConfigManager.getInstance().getListValue("whitelisted", "0.0.0.0");

			this.whitelist = Boolean.valueOf(ConfigManager.getInstance().getValue("whitelist", "false"));

			for (String user : opUsers) {
				Main.getInstance().getLogger().printInfoMessage("Loaded operator " + user);
			}

			for (String user : opUsersPlus) {
				Main.getInstance().getLogger().printInfoMessage("Loaded operator plus " + user);
			}

			for (String user : permMutedUsers) {
				Main.getInstance().getLogger().printInfoMessage("Loaded permanently muted user " + user);
			}

			for (String user : permBannedUsers) {
				Main.getInstance().getLogger().printInfoMessage("Loaded permanently banned user " + user);
			}

			for (String user : whitelistedUsers) {
				Main.getInstance().getLogger().printInfoMessage("Loaded whitelisted user " + user);
			}

			Main.getInstance().getLogger().printInfoMessage("Loaded config successfully");

			InetSocketAddress insa = new InetSocketAddress("0.0.0.0", ServerPort);
			welcomeSocket = new ServerSocket();

			welcomeSocket.bind(insa);

			Main.getInstance().getLogger().printInfoMessage("Server started");
			serverIP = WebHelper.getPage("https://api.ipify.org/");
			Main.getInstance().getLogger().printInfoMessage("Listening for clients on " + serverIP + ":"
					+ String.valueOf(ServerPort) + " & localhost:" + String.valueOf(ServerPort));

			startTempbanUnbanner();
			startTips();
			startUptimeCounter();

			while (true) {
				final Socket connectionSocket = welcomeSocket.accept();

				Thread listen;

				listen = new Thread() {
					@Override
					public void run() {
						Main.getInstance().getLogger().printInfoMessage("Incomming client...");
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
	public void onReciveFromClient(P02PacketString pChat, User u) {
		e.onRecive(pChat, u);
	}

}
