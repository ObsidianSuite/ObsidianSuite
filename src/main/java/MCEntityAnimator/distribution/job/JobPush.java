package MCEntityAnimator.distribution.job;

import MCEntityAnimator.distribution.DataHandler;

public class JobPush extends Job
{
	
	private String path;

	public JobPush(String pathHRF, String path)
	{
		super("Push " + pathHRF, new String[]{"Starting push", "Uploading " + path, "Done"});
		this.path = path;
	}

	@Override
	public void run() 
	{
		super.run();
		incrementStage();
		DataHandler.push(path);
		incrementStage();
	}

}
