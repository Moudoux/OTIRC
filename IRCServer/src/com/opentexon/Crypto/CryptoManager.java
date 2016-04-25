package com.opentexon.Crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Encrypts all messages between the server and clients. The messages are
 * encrypted with a one time key via a external encryption server.
 * 
 * @author Alexander
 *
 */
public class CryptoManager {

	private static String translate(String message) {
		String result = message;

		result = result.replace("&1", "§1");
		result = result.replace("&9", "§9");
		result = result.replace("&3", "§3");
		result = result.replace("&b", "§b");
		result = result.replace("&4", "§4");
		result = result.replace("&c", "§c");
		result = result.replace("&e", "§e");
		result = result.replace("&6", "§6");
		result = result.replace("&2", "§2");
		result = result.replace("&a", "§a");
		result = result.replace("&5", "§5");
		result = result.replace("&d", "§d");
		result = result.replace("&f", "§f");
		result = result.replace("&7", "§7");
		result = result.replace("&8", "§8");
		result = result.replace("&0", "§0");

		return result;
	}

	private static String translate1(String message) {
		String result = message;

		result = result.replace("§1", "&1");
		result = result.replace("§9", "&9");
		result = result.replace("§3", "&3");
		result = result.replace("§b", "&b");
		result = result.replace("§4", "&4");
		result = result.replace("§c", "&c");
		result = result.replace("§e", "&e");
		result = result.replace("§6", "&6");
		result = result.replace("§2", "&2");
		result = result.replace("§a", "&a");
		result = result.replace("§5", "&5");
		result = result.replace("§d", "&d");
		result = result.replace("§f", "&f");
		result = result.replace("§7", "&7");
		result = result.replace("§8", "&8");
		result = result.replace("§0", "&0");

		return result;
	}

	public static String getPage(String url, String message) {
		String result = "";
		try {

			URL url1 = new URL(url);
			URLConnection urlConn = url1.openConnection();

			urlConn.addRequestProperty("msg", message);

			BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

			String text;

			while ((text = in.readLine()) != null) {
				result = result + text;
			}

			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String decode(String message) {
		String url = "https://dl.opentexon.com/.irc/crypto.php?ref=irc&action=decode";
		return getPage(url, translate(message));
	}

	public static String encode(String message) {
		String url = "https://dl.opentexon.com/.irc/crypto.php?ref=irc&action=encode";
		return getPage(url, translate1(message));
	}

}
