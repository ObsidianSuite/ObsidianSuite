package obsidianAnimator.gui.timeline.swing.subsection;

import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.swing.TimelineControllerSub;

public class TimelineInputController extends TimelineControllerSub {
	
	public final TimelineInputPanel panel;
	
	private boolean boolPlay = false;	
	
	//Milli time at which the animation started playing (play button pressed).
	private long playStartTimeMilli;
	//Frame time at which the animation started playing (play button pressed).
	private float playStartTimeFrame;
	
	public TimelineInputController(TimelineController controller)
	{
		super(controller);
		this.panel = new TimelineInputPanel(this);
	}
	
	public boolean isPlaying()
	{
		return boolPlay;
	}
	
	public void setPlaying(boolean play)
	{
		boolPlay = play;
	}
	
	public void updatePlayPauseButton()
	{
		panel.playPauseButton.setText(boolPlay ? "Pause" : "Play");
	}
	
	public long getPlayStartTimeMilli()
	{
		return playStartTimeMilli;
	}

	public void setPlayStartTimeMilli(long playStartTimeMilli)
	{
		this.playStartTimeMilli = playStartTimeMilli;
	}

	public float getPlayStartTimeFrame() 
	{
		return playStartTimeFrame;
	}

	public void setPlayStartTimeFrame(float playStartTimeFrame) 
	{
		this.playStartTimeFrame = playStartTimeFrame;
	}
	
	
	public TimelineAnimationPanel getAnimationPanel()
	{
		return mainController.animationController.panel;
	}
	
	public TimelinePartPanel getPartPanel()
	{
		return mainController.partController.panel;
	}
	
	public TimelineMovementPanel getMovementPanel()
	{
		return mainController.movementController.panel;
	}

	public TimelineRenderPanel getRenderPanel()
	{
		return mainController.renderController.panel;
	}
	
	public TimelineItemPanel getItemPanel()
	{
		return mainController.itemController.panel;
	}
	
}
