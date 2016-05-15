/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Main;

import java.util.Calendar;
import java.util.Scanner;

import com.opentexon.Server.Server.Server;
import com.opentexon.Utils.Logger;
import com.opentexon.Utils.StringUtils;
import com.opentexon.Utils.WebHelper;

public class Main {

	private static Main instance;
	private Logger logger;
	private Server server;

	private Thread inputListener;
	private Thread ircThread;

	public boolean setName = false;
	public boolean setPort = false;

	public String executionPath;

	/**
	 * The server version
	 */
	public String version = "Build: 1.2.4a Compiled: 2016/05/15";

	public String Name;

	public static Main getInstance() {
		return instance;
	}

	public Server getServer() {
		return server;
	}

	public Logger getLogger() {
		return logger;
	}

	public Thread getInputListenerThread() {
		return inputListener;
	}

	public Thread getIRCThread() {
		return ircThread;
	}

	private void listen() {
		try {
			Scanner scanIn = new Scanner(System.in);

			String line = null;

			while ((line = scanIn.nextLine()) != null) {
				if (!Main.getInstance().setName) {
					Main.getInstance().setName = true;
					Main.getInstance().Name = line;
					logger.printInfoMessage("Please type a port (Default/Recommended is 6687):");
				} else {
					if (!Main.getInstance().setPort && StringUtils.isNumeric(line.replace(" ", ""))) {
						Main.getInstance().setPort = true;
						Main.getInstance().server = new Server(Main.getInstance().Name, Integer.valueOf(line));
						Main.getInstance().ircThread.start();
					} else if (!Main.getInstance().setPort && !StringUtils.isNumeric(line.replace(" ", ""))) {
						logger.printWarningMessage("Invalid port");
						logger.printWarningMessage("Please type a valid port (Default/Recommended is 6687");
					} else {
						Main.getInstance().getServer().onInput(line);
					}
				}
			}

			scanIn.close();
		} catch (Exception ex) {
			listen();
		}
	}

	private void initInputListener() {
		inputListener = new Thread() {
			@Override
			public void run() {
				listen();
			}
		};
		inputListener.start();
	}

	public void init(String[] args) {
		logger = new Logger();

		if (WebHelper.getPage("https://dl.opentexon.com/.irc/service.php").equals("true")) {
			if (!this.version.contains(WebHelper.getPage("https://dl.opentexon.com/.irc/ver.php"))) {
				logger.printWarningMessage(
						"There is a update for the irc server, please download it from https://dl.opentexon.com/IRC.zip or compile it from https://github.com/Moudoux/OTIRC");
				logger.printWarningMessage("Shutting down");
				return;
			}
		}

		Calendar now = Calendar.getInstance();

		executionPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toString().replace("file:/",
				"");

		String jarName = executionPath;

		jarName = jarName.split("/")[jarName.split("/").length - 1];

		executionPath = executionPath.replace(jarName, "");

		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			executionPath = "/" + executionPath;
		}

		logger.printInfoMessage("Welcome to the OT IRC server version: " + version + "\nCopyright" + " © Alexander "
				+ String.valueOf(now.get(Calendar.YEAR)) + " All Rights Reserved");

		// Do NOT remove the following line
		logger.printInfoMessage(
				"Source and executables for client and server avaiable at: \n" + "https://github.com/Moudoux/OTIRC");

		initInputListener();

		ircThread = new Thread() {
			@Override
			public void run() {
				Main.getInstance().getServer().startServer();
			}
		};

		String argsString = "";

		for (String s : args) {
			if (argsString.equals("")) {
				argsString = s;
			} else {
				argsString = argsString + " " + s;
			}
		}

		if (!argsString.equals("")) {
			String name = "";
			int port = 0;

			for (String s : argsString.split("-")) {
				if (s.startsWith("name")) {
					name = s.replace("name ", "");
				} else if (s.startsWith("port")) {
					port = Integer.valueOf(s.replace("port ", ""));
				}
			}

			if (!name.equals("") && !(port == 0)) {
				setName = true;
				setPort = true;
				Main.getInstance().Name = name;
				Main.getInstance().server = new Server(Main.getInstance().Name, port);
				Main.getInstance().ircThread.start();
			}
		}

		logger.printInfoMessage("Please type a name for this server:");

	}

	public static void main(String[] args) {
		instance = new Main();
		instance.init(args);
	}

}
