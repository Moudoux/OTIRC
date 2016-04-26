/**
 * This software is licensed under the MIT license.
 * If you wish to modify this software please give credit and link to the git: https://github.com/Moudoux/OTIRC.
 */
package com.opentexon.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Can retrive content from websites, used for encryption
 * 
 * @author Alexander
 *
 */
public class WebHelper {

	public static String getPage(String url) {
		String result = "";
		try {

			URL url1 = new URL(url);
			URLConnection urlConn = url1.openConnection();

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

}
