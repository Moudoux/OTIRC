/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Translates a string. It hides links, hides ip addresses, checks for
 * swearwords
 * 
 * @author Alexander
 *
 */
public class Filter {

	private boolean hideLinks = true;
	private boolean hideIpAddresses = true;

	private String removeCodes(String message) {
		String result = message;

		result = result.replace("§1", "");
		result = result.replace("§9", "");
		result = result.replace("§3", "");
		result = result.replace("§b", "");
		result = result.replace("§4", "");
		result = result.replace("§c", "");
		result = result.replace("§e", "");
		result = result.replace("§6", "");
		result = result.replace("§2", "");
		result = result.replace("§a", "");
		result = result.replace("§5", "");
		result = result.replace("§d", "");
		result = result.replace("§f", "");
		result = result.replace("§7", "");
		result = result.replace("§8", "");
		result = result.replace("§0", "");

		return result;
	}

	public String proccessIPAddresses(String line) {
		String result = "";
		Pattern p = Pattern.compile(
				"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		for (String s : line.split(" ")) {
			Matcher m = p.matcher(s);
			if (m.find()) {
				s = "§a" + s + "§7";
				if (hideIpAddresses) {
					s = "[HIDDEN]";
				}
				result = result.equals("") ? s : result + " " + s;
			} else {
				result = result.equals("") ? s : result + " " + s;
			}
		}
		return result;
	}

	public String proccessLinks(String line) {
		String result = "";
		String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
		Pattern p = Pattern.compile(URL_REGEX);
		for (String s : line.split(" ")) {
			Matcher m = p.matcher(s);
			if (m.find()) {
				s = "§a" + s + "§7";
				if (hideLinks) {
					s = "[HIDDEN]";
				}
				result = result.equals("") ? s : result + " " + s;
			} else {
				result = result.equals("") ? s : result + " " + s;
			}
		}
		return result;
	}

	public boolean isSwearWord(String line) {
		boolean result = false;

		line = removeCodes(line.toLowerCase());

		ArrayList<String> badWords = Badwords.getBadWords();

		for (String s : line.split(" ")) {
			if (badWords.contains(s)) {
				result = true;
				break;
			}
		}

		return result;
	}

}
