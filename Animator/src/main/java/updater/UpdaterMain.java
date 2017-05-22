package updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;

public class UpdaterMain {

	private static final String versionLink = "https://raw.githubusercontent.com/DaBigJoe/ObsidianSuite/updater/VERSION.md";
	private static final String baseDownloadLink = "https://github.com/DaBigJoe/ObsidianSuite/releases/download/";
	private static final String animatorID = "Animator";
	
	public static void main(String[] args) {
		try {
			String version = getVersion(animatorID);
			URL url = generateDownloadURL(animatorID, version, "zip");
			download(url, new File(generateFileName(animatorID, version, "zip")));
		} catch (Exception e) {e.printStackTrace();}
	}

	private static String getVersion(String softwareID) throws IOException, SoftwareNotFoundException {
		String softwareName = "Obsidian" + softwareID;
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

	/**
	 * E.g. https://github.com/DaBigJoe/ObsidianSuite/releases/download/Animator_v0.3.0-Alpha/ObsidianAnimator_v0.3.0-Alpha.zip
	 * 																			^							^
	 * @throws MalformedURLException 
	 */
	private static URL generateDownloadURL(String softwareID, String version, String extension) throws MalformedURLException {
		return new URL(baseDownloadLink + softwareID + "_" + version + "/" + generateFileName(softwareID, version, extension));
	}
	
	private static String generateFileName(String softwareID, String version, String extension) {
		return String.format("Obsidian%s_%s.%s", softwareID, version, extension);
	}
	
	private static void download(URL url, File file) throws IOException {
		FileUtils.copyURLToFile(url, file);
	}
	
}
