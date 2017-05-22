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
			download(animatorID);
		} catch (IOException | SoftwareNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void download(String softwareName) throws IOException, SoftwareNotFoundException {
		String version = getVersion(softwareName);
		String tag = generateTag(softwareName, version);
		String fileName = generateFileName(softwareName, version, "zip");
		URL downloadURL = generateDownloadURL(tag, fileName);
		File downloadFile = new File(fileName);
		FileUtils.copyURLToFile(downloadURL, downloadFile);
	}
	
	private static String getVersion(String softwareName) throws IOException, SoftwareNotFoundException {
		String completeName = "Obsidian" + softwareName;
		URLConnection connection = new URL(versionLink).openConnection();
		connection.setUseCaches(false);
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String version = null;
		for (String line; (line = reader.readLine()) != null;) {
			if(line.contains(completeName))
				version = line.substring(completeName.length()+1);
		}
		reader.close();
		if(version == null)
			throw new SoftwareNotFoundException(completeName);
		return version;
	}
	
	private static URL generateDownloadURL(String tag, String fileName) throws MalformedURLException {
		return new URL(baseDownloadLink + tag + "/" + fileName);
	}
	
	private static String generateTag(String softwareName, String version) {
		return String.format("%s_%s", softwareName, version);
	}
	
	private static String generateFileName(String softwareID, String version, String extension) {
		return String.format("Obsidian%s_%s.%s", softwareID, version, extension);
	}
	
}
