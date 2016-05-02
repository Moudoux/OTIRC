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

public class Main {

	private static Main instance;
	private Logger logger;
	private Server server;

	private Thread inputListener;
	private Thread ircThread;

	public boolean setName = false;
	public boolean setPort = false;

	/**
	 * The server version
	 */
	public String version = "1.0.7";

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

	private void initInputListener() {
		logger.printInfoMessage("Starting console input reader...");
		inputListener = new Thread() {
			@Override
			public void run() {
				try {
					Main.getInstance().getLogger().printInfoMessage("Started console input reader");

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
				} catch (StringIndexOutOfBoundsException ex) {
					Main.getInstance().getLogger().printErrorMessage(
							"Input reader crashed because of a invalid input. Please restart the server.");
				}
			}
		};
		inputListener.start();
	}

	public void init(String[] args) {
		logger = new Logger();
		Calendar now = Calendar.getInstance();

		System.out.println("Welcome to the IRC server version: " + version + "\nCopyright" + " © Alexander "
				+ String.valueOf(now.get(Calendar.YEAR)) + " All Rights Reserved");

		// Do NOT remove the following line
		System.out.println(
				"Source and executables for client and server avaiable at: \n" + "https://github.com/Moudoux/OTIRC");

		logger.printInfoMessage("Starting server...");
		initInputListener();

		ircThread = new Thread() {
			@Override
			public void run() {
				Main.getInstance().getServer().startServer();
			}
		};

		logger.printInfoMessage("Please type a name for this server:");

	}

	public static void main(String[] args) {
		instance = new Main();
		instance.init(args);
	}

}
