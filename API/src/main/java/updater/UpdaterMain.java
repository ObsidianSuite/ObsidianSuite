package updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdaterMain {

	public static void main(String[] args) {
		
		
		

		try {
			URL url = new URL("https://raw.githubusercontent.com/DaBigJoe/ObsidianSuite/updater/VERSION.md");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		    for (String line; (line = reader.readLine()) != null;) {
		        System.out.println(line);
		    }
		}
		catch (Exception e) {
			
		}
	}
	
}
