package MCEntityAnimator.distribution;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfo 
{
	
	private String path;
	private Date lastModifiedLocal;
	private Date lastModifiedRemote;
	//The date at which the file was last modified on the server, at the time of downloading. 
	private Date lastModifiedRemoteOnDownload;
	private static final DateFormat dateHRF = new SimpleDateFormat("HH:mm, EEE MMM d, yyyy");
	
	
	public FileInfo(String path, Date lastModifiedLocal, Date lastModifiedRemote) 
	{
		this.path = path;
		this.lastModifiedLocal = lastModifiedLocal;
		this.lastModifiedRemote = lastModifiedRemote;
	}

	public Status getStatus()
	{
		if(lastModifiedLocal == null || lastModifiedLocal.before(lastModifiedRemote))
			return Status.Behind;
		else if(lastModifiedLocal.after(lastModifiedRemote))
			return Status.Ahead;
		else
			return Status.Synced;
	}

	@Override
	public String toString() 
	{
		return "FileInfo [path=" + path + ", status=" + getStatus() + "]";
	}

	/**
	 * Create a human readable form of the file path.
	 */
	public String getFileHRF() 
	{
		System.out.println("Path: " + path);
		
		String[] split = path.split("/");
		String entityName = split[0];
		String file = split[1];
		
		System.out.println("file: " + file);
		String[] split2 = file.split("\\.");
		System.out.println(split2.length);
		String fileName = split2[0];
		String ext = split2[1];
		
		String hrf = "";
		if(ext.equals("mcea"))
			hrf = String.format("%s animation - %s", entityName, fileName);
		else 
			hrf = String.format("%s %s file", entityName, ext);	
		
		return hrf;
	}

	public String getLastModifiedLocalHRF() 
	{
		return getDateHRF(lastModifiedLocal);
	}

	public String getLastModifiedRemoteHRF() 
	{
		return getDateHRF(lastModifiedRemote);
	}
	

	private static String getDateHRF(Date date)
	{
		if(date != null)
			return dateHRF.format(date);
		return "-";
	}
	
	public enum Status 
	{
		Behind (Color.ORANGE),
		Ahead (Color.BLUE),
		Synced (Color.GREEN),
		Conflicted (Color.RED); 
		
		public final Color color;
		Status(Color color)
		{
			this.color = color;
		}
	}

	
}