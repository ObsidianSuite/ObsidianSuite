package updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdaterMain {

	private static final String versionLink = "https://raw.githubusercontent.com/DaBigJoe/ObsidianSuite/updater/VERSION.md";

	public static void main(String[] args) {
		try {
			System.out.println(getVersion("ObsidianAnimator"));
		} catch (Exception e) {e.printStackTrace();}
	}

	private static String getVersion(String softwareName) throws IOException, SoftwareNotFoundException {
		URLConnection connection = new URL(versionLink).openConnection();
		connection.setUseCaches(false);
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String version = null;
		for (String line; (line = reader.readLine()) != null;) {
			if(line.contains(softwareName))
				version = line.substring(softwareName.length()+1);
		}
		reader.close();
		if(version == null)
			throw new SoftwareNotFoundException(softwareName);
		return version;
	}
	
}
