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
		if(lastModifiedLocal == null)
			return Status.New;
		if(lastModifiedLocal.before(lastModifiedRemote))
			return Status.Behind;
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

	public enum Action{Push, Pull, None};
	
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
		Behind (Color.ORANGE, Action.Pull),
		Ahead (Color.BLUE, Action.Push),
		InSync (Color.GREEN, Action.None),
		Conflicted (Color.RED, Action.None),
		New (Color.PINK, Action.Pull),
		Local (Color.GRAY, Action.Push);
		

		public final Color color;
		public final Action action;
		Status(Color color, Action action)
		{
			this.color = color;
			this.action = action;
		}
	}


}