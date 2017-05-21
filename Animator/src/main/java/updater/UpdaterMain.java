package updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdaterMain {

	private static final String versionLink = "https://raw.githubusercontent.com/DaBigJoe/ObsidianSuite/updater/VERSION.md";

	public static void main(String[] args) {
		try {
			System.out.println(getVersion("Test"));
		} catch (Exception e) {e.printStackTrace();}
	}

	private static String getVersion(String softwareName) throws IOException, SoftwareNotFoundException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(versionLink).openStream(), "UTF-8"));
		for (String line; (line = reader.readLine()) != null;) {
			if(line.contains(softwareName))
				return line.substring(softwareName.length()+1);
		}
		throw new SoftwareNotFoundException(softwareName);
	}
	
}
