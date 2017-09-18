package com.dabigjoe.obsidianAnimator.gui.timeline.changes;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;
import com.google.common.base.Splitter;

public class ChangeActionPoints implements AnimationChange {

	private int time;
	private String newActionPoints;
	private String prevActionPoints;
	
	public ChangeActionPoints(int time, String newActionPoints, String prevActionPoints) {
		this.time = time;
		this.newActionPoints = newActionPoints;
		this.prevActionPoints = prevActionPoints;
	}
	
	
	@Override
	public void apply(TimelineController controller, AnimationSequence animation) {
		setActionPoints(newActionPoints, controller, animation);
	}

	@Override
	public void undo(TimelineController controller, AnimationSequence animation) {
		setActionPoints(prevActionPoints, controller, animation);
	}
	
	private void setActionPoints(String actionPoints, TimelineController controller, AnimationSequence animation) {
		for (String s : animation.getActionPoints(time)) {
            animation.removeActionPoint(time,s);
        }

        for (String s : Splitter.on(',').split(actionPoints)) {
            animation.addActionPoint(time, s);
        }
        
        controller.timelineFrame.actionsPanel.updateText();
	}

}
