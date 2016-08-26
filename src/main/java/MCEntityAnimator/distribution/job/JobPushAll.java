package MCEntityAnimator.distribution.job;

import java.util.List;

import MCEntityAnimator.distribution.DataHandler;
import MCEntityAnimator.distribution.FileInfo;

public class JobPushAll extends Job
{

	private List<FileInfo> files;
	
	public JobPushAll(List<FileInfo> files) 
	{
		super("Push all", generateStatuses(files));
		this.files = files;
		// TODO Auto-generated constructor stub
	}
	
	private static String[] generateStatuses(List<FileInfo> files) 
	{
		String[] sArr = new String[files.size() + 2];
		sArr[0] = "Starting push all";
		for(int i = 0; i < files.size(); i++)
			sArr[i + 1] = "Uploading " + files.get(i).getPath();
		sArr[sArr.length - 1] = "Done";
		return sArr;
	}

	@Override
	public void run() 
	{
		super.run();
		for(FileInfo file : files)
		{
			incrementStage();
			DataHandler.push(file.getPath());
		}
		incrementStage();
	}
	
	

}
