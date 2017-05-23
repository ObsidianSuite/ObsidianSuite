/**
 * This is the Updater main class.
 * The updater runs as its own jar, so outside of Minecraft.
 * It uses VERSION.md in the repository to get the latest version of a piece of software.
 * 
 * Each software has an 'id', eg Animator, API etc.
 * This id is used to generate check the version and create the download link.
 * On the repo, the software has a tag and a file name.
 * Tag is 'id'_'version'
 * Software is Obsidian'id'_'version' 
 * 
 * Download is a zip file, so the actual mod jar has to be extracted.
 * The name of the jar file is the same as the name of the zip just with a different extension.
 */
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
	
	private static void download(String softwareID) throws IOException, SoftwareNotFoundException {
		String version = getVersion(softwareID);
		String tag = generateTag(softwareID, version);
		String fileName = generateFileName(softwareID, version, "zip");
		URL downloadURL = generateDownloadURL(tag, fileName);
		File downloadFile = new File(fileName);
		FileUtils.copyURLToFile(downloadURL, downloadFile);
	}
	
	private static String getVersion(String softwareID) throws IOException, SoftwareNotFoundException {
		URLConnection connection = new URL(versionLink).openConnection();
		connection.setUseCaches(false);
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String version = null;
		for (String line; (line = reader.readLine()) != null;) {
			if(line.contains(softwareID))
				version = line.substring(softwareID.length()+1);
		}
		reader.close();
		if(version == null)
			throw new SoftwareNotFoundException(softwareID);
		return version;
	}
	
	private static URL generateDownloadURL(String tag, String fileName) throws MalformedURLException {
		return new URL(baseDownloadLink + tag + "/" + fileName);
	}
	
	private static String generateTag(String softwareID, String version) {
		return String.format("%s_%s", softwareID, version);
	}
	
	private static String generateFileName(String softwareID, String version, String extension) {
		return String.format("Obsidian%s_%s.%s", softwareID, version, extension);
	}
	
}
