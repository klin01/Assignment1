/**
 * 
 */
package Assignment1;

/**
 * @author Kevin Lin (KL2495)
 *
 */

import java.net.*;
import java.io.*;

public class CIAFactbookUI {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		URL dataSource = new URL("https://www.cia.gov/library/publications/the-world-factbook/");
		BufferedReader in = new BufferedReader(
				new InputStreamReader(dataSource.openStream()));
		
		String inputLine; 
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		
		in.close();
	}

}
