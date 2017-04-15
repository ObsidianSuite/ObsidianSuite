package obsidianAnimator.gui.timeline.swing.subsection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import obsidianAPI.animation.AnimationPart;
import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.Keyframe;
import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.swing.TimelineControllerSub;
import obsidianAnimator.gui.timeline.swing.component.CopyLabel;

public class TimelineKeyframeController extends TimelineControllerSub
{

	private Map<Part, List<Keyframe>> keyframes = new HashMap<Part, List<Keyframe>>();

	public final TimelineKeyframePanel panel;
	
	protected Part hoveredPart;

	public TimelineKeyframeController(TimelineController controller)
	{
		super(controller);		
		this.panel = new TimelineKeyframePanel(this);
	}

	public void initCopyLabel()
	{
		panel.copyLabel = new CopyLabel();
		JLayeredPane layeredPane = mainController.timelineFrame.getRootPane().getLayeredPane();
		layeredPane.add(panel.copyLabel, JLayeredPane.DRAG_LAYER);
		panel.copyLabel.setBounds(0, 0, mainController.timelineFrame.getWidth(), mainController.timelineFrame.getHeight());
	}

	public void refreshSliderAndTextBox()
	{
		int time = (int) getTime();
		if(!panel.timeTextField.hasFocus())
			panel.timeTextField.setText(Integer.toString(time));
		panel.timeSlider.setValue((int) getTime());
	}

	public Part getHoveredPart()
	{
		return hoveredPart;
	}
	
	/**
	 * Creates keyframes from the animation sequence. 
	 */
	public void loadKeyframes()
	{	
		keyframes.clear();
		for(AnimationPart animpart : getCurrentAnimation().getAnimationList())
		{
			Part mr = mainController.timelineGui.entityModel.getPartFromName(animpart.getPartName());

			String partName = animpart.getPartName();
			List<Keyframe> partKfs = keyframes.get(mr);
			if(keyframes.get(mr) == null)
				partKfs = new ArrayList<Keyframe>();			

			float[] defaults = mr.getOriginalValues();
			//If the movement starts at time zero, and the part isn't in its original position, add a keyframe at time zero.
			if(animpart.getStartTime() == 0.0F)
			{
				if(!animpart.isStartPos(defaults))
				{
					Keyframe kf = new Keyframe(0, mr, animpart.getStartPosition());
					partKfs.add(kf);
				}
				if(animpart.isEndPosDifferentToStartPos() || getCurrentAnimation().multiPartSequence(partName))
				{
					Keyframe kf = new Keyframe((int) animpart.getEndTime(), mr, animpart.getEndPosition());
					partKfs.add(kf);
				}
			}
			else
			{
				Keyframe kf = new Keyframe((int) animpart.getEndTime(), mr, animpart.getEndPosition());
				partKfs.add(kf);
			}
			keyframes.put(mr, partKfs);
		}
	}

	/**
	 * Get a keyframe for a given part at a certain time.
	 * Will return null if no keyframe exists.
	 */
	public Keyframe getKeyframe(Part part, int frameTime)
	{
		List<Keyframe> partKeyframes = keyframes.get(part);
		if (partKeyframes != null)
		{
			for (Keyframe keyframe : partKeyframes)
			{
				if (keyframe.frameTime == frameTime)
					return keyframe;
			}
		}
		return null;
	}

	public void addKeyframe()
	{
		if(mainController.timelineGui.selectedPart != null)
		{
			Part part = mainController.timelineGui.selectedPart;
			Keyframe kf = new Keyframe((int) getTime(), mainController.timelineGui.selectedPart, part.getValues());
			addKeyframe(kf);
		}
	}

	public void addKeyframe(Keyframe kf)
	{
		List<Keyframe> partKeyframes = keyframes.get(kf.part);

		if (partKeyframes == null)
		{
			partKeyframes = new ArrayList<Keyframe>();
			keyframes.put(kf.part, partKeyframes);
		} 
		else
		{
			deleteKeyframe(kf.part, kf.frameTime);
		}

		partKeyframes.add(kf);
		panel.refresthLineColours();
		mainController.updateAnimationParts();
	}

	public void deleteKeyframe()
	{
		boolean removed = deleteKeyframe(mainController.timelineGui.selectedPart, (int) getTime());

		mainController.timelineFrame.repaint();

		if (removed)
		{
			mainController.setExceptionPart(null);
			mainController.updateAnimationParts();
		}

		mainController.refresh();
	}

	public boolean deleteKeyframe(Part part, int time)
	{
		Keyframe toRemove = getKeyframe(part, time);
		if (toRemove != null)
		{
			keyframes.get(part).remove(toRemove);
			return true;
		}

		return false;
	}

	public void copyKeyframe(Keyframe kf, Part part, int time)
	{
		String partName = kf.part.getName();

		if((partName.equals("entitypos") || partName.equals("prop_rot") || partName.equals("prop_trans")) && !partName.equals(part.getName()))
			JOptionPane.showMessageDialog(mainController.timelineFrame, partName + " can only copy to itself.");
		else
			addKeyframe(new Keyframe(time, part, kf.values.clone()));
	}

	public boolean keyframeExists()
	{
		return getExistingKeyframe() != null;
	}

	public Keyframe getExistingKeyframe()
	{
		List<Keyframe> partKeyframes = getPartKeyframes(mainController.getSelectedPart());
		if(partKeyframes != null)
		{
			for(Keyframe kf : partKeyframes)
			{
				if((int)kf.frameTime == (int)getTime())
					return kf;
			}
		}
		return null;
	}

	public int getLastKeyFrameTime()
	{
		int lastFrameTime = 0;
		for(Part part : mainController.timelineGui.parts)
		{
			List<Keyframe> partKeyframes = getPartKeyframes(part);
			if(partKeyframes != null)
			{
				for(Keyframe kf : partKeyframes)
				{
					if(kf.frameTime > lastFrameTime)
						lastFrameTime = kf.frameTime;
				}
			}

		}
		return lastFrameTime;
	}

	public boolean doesPartOnlyHaveOneKeyframe(Part part)
	{
		List<Keyframe> kfs = getPartKeyframes(part);
		return (kfs != null && kfs.size() == 1);
	}

	/**
	 * Gets the keyframe that comes before this one, for the same part, or a default keyframe at time zero if none exists.
	 */
	public Keyframe getPreviousKeyframe(Keyframe keyframe)
	{
		Part part = keyframe.part;
		int frameTime = keyframe.frameTime;

		Keyframe previousKf = null;
		Integer prevFt = null;
		for(Keyframe kf : getPartKeyframes(part))
		{
			if(kf.frameTime < frameTime && (prevFt == null || kf.frameTime > prevFt))
			{
				previousKf = kf;
				prevFt = kf.frameTime;
			}
		}
		if(previousKf == null)
		{
			float[] defaults = part.getOriginalValues();
			previousKf = new Keyframe(0, part, defaults);
		}
		return previousKf;
	}

	public Set<Part> getPartsWithKeyframes()
	{
		return keyframes.keySet();
	}

	public List<Keyframe> getPartKeyframes(Part part)
	{
		return keyframes.get(part);
	}

}
