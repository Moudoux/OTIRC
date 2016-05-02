/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
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
 * Note: As you can see, the messages are sent unencrypted to the encryption
 * server. But they are transferred over SSL.
 * 
 * @author Alexander
 *
 */
public class CryptoManager {

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
		// If you wish to use your own encryption servers, make sure to use SSL
		String url = "https://dl.opentexon.com/.irc/crypto.php?ref=irc&action=decode";
		return getPage(url, message);
	}

	public static String encode(String message) {
		// If you wish to use your own encryption servers, make sure to use SSL
		String url = "https://dl.opentexon.com/.irc/crypto.php?ref=irc&action=encode";
		return getPage(url, message);
	}

}
