/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Server.Server;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Alexander
 *
 */
public class Filter {

	private boolean hideLinks = true;
	private boolean hideIpAddresses = true;

	public String getHiddenIP(String ip) {
		String result = "";
		for (String s : ip.split("")) {
			if (!s.equals(".")) {
				result = result + "*";
			} else {
				result = result + s;
			}
		}
		return result;
	}

	public String proccessIPAddresses(String line) {
		String result = "";
		Pattern p = Pattern.compile(
				"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

		for (String s : line.split(" ")) {
			Matcher m = p.matcher(s);
			if (m.find()) {
				if (hideIpAddresses) {
					s = getHiddenIP(s);
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
			Matcher m = p.matcher(s.toLowerCase());
			if (m.find()) {
				if (hideLinks && !s.contains("dl.opentexon.com")) {
					s = getHiddenIP(s);
				}
				result = result.equals("") ? s : result + " " + s;
			} else {
				result = result.equals("") ? s : result + " " + s;
			}
		}
		return result;
	}

	public boolean containsIP(String line) {
		Pattern p = Pattern.compile(
				"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		for (String s : line.split(" ")) {
			Matcher m = p.matcher(s);
			if (m.find()) {
				return true;
			}
		}
		return false;
	}

	public boolean containsLink(String line) {
		String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
		Pattern p = Pattern.compile(URL_REGEX);
		for (String s : line.split(" ")) {
			Matcher m = p.matcher(s);
			if (m.find()) {
				return true;
			}
		}
		return false;
	}

	public boolean isSwearWord(String line) {
		boolean result = false;

		line = line.toLowerCase();

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
