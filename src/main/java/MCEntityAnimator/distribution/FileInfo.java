package MCEntityAnimator.distribution;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfo implements Comparable<FileInfo>
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
	

	public String getPath() 
	{
		return path;
	}

	public Status getStatus()
	{
		if(lastModifiedLocal == null)
			return Status.New;
		if(lastModifiedRemote == null)
			return Status.Local;
		if(lastModifiedLocal.before(lastModifiedRemote))
		{
			System.out.println(lastModifiedLocal + " " + lastModifiedRemote);
			return Status.Behind;
		}
		else if(lastModifiedLocal.after(lastModifiedRemote))
			return Status.Ahead;
		else
			return Status.InSync;
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
		String hrf = "";

		String[] split = path.split("/");
		String entityName = split[0];
		String file = split[1];

		if(file.contains("."))
		{
			String[] split2 = file.split("\\.");
			String fileName = split2[0];
			String ext = split2[1];
			hrf = String.format("%s animation - %s", entityName, fileName);
		}
		else
			hrf = String.format("%s setup", entityName);	

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
	
	@Override
	public int compareTo(FileInfo o)
	{
		String[] split = path.split("/");
		String entityName = split[0];
		String file = split[1];
		
		String[] split2 = o.getPath().split("/");
		String o_entityName = split2[0];
		String o_file = split2[1];
		
		//Compare names - if names differ return string comparison.
		int nameCompare = entityName.compareTo(o_entityName);
		if(nameCompare != 0)
			return nameCompare;
		
		//Setup file should come first.
		int fileCompare = 0;
		if(file.contains("."))
			fileCompare += 1;
		if(o_file.contains("."))
			fileCompare -= 1;
		
		return fileCompare;
	}

	public enum StatusAction{Push, Pull, None};
	
	public enum Status 
	{
		/**
		 * Behind - Local behind remote, pull
		 * Ahead - Local ahead of remote, push
		 * InSync - Local and remote in sync, none
		 * Conflicted - Local and remote both changed, TODO conflict...
		 * New - New file on remote, pull
		 * Local - New file on server, push
		 */
		Behind (Color.ORANGE, StatusAction.Pull),
		Ahead (Color.BLUE, StatusAction.Push),
		InSync (Color.GREEN, StatusAction.None),
		Conflicted (Color.RED, StatusAction.None),
		New (Color.PINK, StatusAction.Pull),
		Local (Color.GRAY, StatusAction.Push);
		

		public final Color color;
		public final StatusAction action;
		Status(Color color, StatusAction action)
		{
			this.color = color;
			this.action = action;
		}
	}



}