/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opentexon.Server.Server.User;

/**
 * A collection of functions needed within the server and client, eg translating
 * strings, checking numbers etc
 * 
 * @author Alexander
 *
 */
public class StringUtils {

	/*
	 * Pattern checking
	 */

	public static String getPrefix(User u) {
		if (u != null) {
			String inPrefix = "[";
			if (u.getUsername().toLowerCase().equals("thijminecraft02")) {
				inPrefix = inPrefix + "Beta Tester";
			}

			if (u.getUsername().toLowerCase().equals("mumbojet")) {
				inPrefix = inPrefix + "Beta Tester";
			}

			if (u.getUsername().toLowerCase().equals("deftware")) {
				inPrefix = inPrefix + "Creator";
			}

			if (inPrefix.equals("[")) {
				inPrefix = inPrefix + "User]";
			} else {
				inPrefix = inPrefix + "]";
			}

			if (u.isOP()) {
				String opPrefix = "[OP";

				if (u.PermissionLevel == 1) {
					opPrefix = opPrefix + "+]";
				} else {
					opPrefix = opPrefix + "]";
				}
				inPrefix = inPrefix + " " + opPrefix;
			}

			return inPrefix;
		}
		return "";
	}

	/**
	 * Checks if a given string only contains numbers
	 * 
	 * @param line
	 * @return
	 */
	public static boolean isNumeric(String line) {
		String regex = "[0-9]+";
		if (line.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Reads a text file
	 * 
	 * @param path
	 * @param encoding
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	/**
	 * Checks if a string is a ip address
	 * 
	 * @param line
	 * @return
	 */
	public static boolean isIPAddress(String line) {
		Pattern p = Pattern.compile(
				"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
		Matcher m = p.matcher(line);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if a string contains a ip address
	 * 
	 * @param line
	 * @return
	 */
	public static boolean containsIPAddress(String line) {
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

	/*
	 * Stream readers
	 */

	public static String readStream(InputStream is) {
		StringBuilder sb = new StringBuilder(512);
		try {
			Reader r = new InputStreamReader(is, "UTF-8");
			int c = 0;
			while ((c = r.read()) != -1) {
				sb.append((char) c);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sb.toString();
	}

}
