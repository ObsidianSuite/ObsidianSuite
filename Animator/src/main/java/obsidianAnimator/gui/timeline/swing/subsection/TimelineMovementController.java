package obsidianAnimator.gui.timeline.swing.subsection;

import javax.swing.JOptionPane;

import obsidianAnimator.gui.entityRenderer.EntityAutoMove;
import obsidianAnimator.gui.entityRenderer.EntityAutoMove.Direction;
import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.swing.TimelineControllerSub;

public class TimelineMovementController extends TimelineControllerSub
{
	
	public final TimelineMovementPanel panel;
	
	private float speed = 0.0F; 
	private boolean movementActive = false;
	private EntityAutoMove entityMovement;
	
	public TimelineMovementController(TimelineController controller)
	{
		super(controller);
		
		this.panel = new TimelineMovementPanel(this);
	}
	
	public void updateEntityMovement()
	{
		updateEntityMovement(getCurrentAnimation().getFPS());
	}
	
	public void updateEntityMovement(int fps)
	{
		entityMovement = new EntityAutoMove(speed, (Direction) panel.directionBox.getSelectedItem(), fps);
	}
	
	protected void getUserSpeed()
	{
		Float speed = null;			
		while(true)
		{
			String input = JOptionPane.showInputDialog(getTimelineFrame(), "Set Speed (greater than 0)");
			if(input == null)
				break;
			speed = getSpeedFromString(input);
			if(speed != null)
			{
				this.speed = speed;
				panel.updateSpeedLabel();
				updateEntityMovement();
				break;
			}
			else
				JOptionPane.showMessageDialog(getTimelineFrame(), "Invalid input");
		}
	}


	private Float getSpeedFromString(String input)
	{
		Float speed = null;

		try
		{
			speed = Float.parseFloat(input);
			if(speed <= 0.0F)
				speed = null;
		}
		catch(NumberFormatException e){}

		return speed;
	}

	public void setMovementActive(boolean movementActive)
	{
		this.movementActive = movementActive;
	}
	
	public boolean isMovementActive()
	{
		return movementActive;
	}
	
	public void setEntityMovement(EntityAutoMove entityMovement)
	{
		this.entityMovement = entityMovement;
	}
	
	public EntityAutoMove getEntityMovement()
	{
		return entityMovement;
	}
	
	public float getSpeed()
	{
		return speed;
	}
	
}
