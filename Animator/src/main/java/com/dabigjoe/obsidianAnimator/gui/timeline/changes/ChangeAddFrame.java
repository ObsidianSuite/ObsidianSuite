package com.dabigjoe.obsidianAnimator.gui.timeline.changes;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAnimator.gui.timeline.Keyframe;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;

public class ChangeAddFrame implements AnimationChange
{
    private String partName;
    private int time;

    public ChangeAddFrame(String partName, int time)
    {
        this.partName = partName;
        this.time = time;
    }

    @Override
    public void apply(TimelineController controller, AnimationSequence animation)
    {
        if (partName == null)
        {
            for (Keyframe frame : controller.keyframeController.getAllFrames())
            {
                if (frame.frameTime >= time)
                {
                    frame.frameTime++;
                }
            }
        } else
        {
            Part part = controller.timelineGui.entityModel.getPartFromName(partName);
            for (Keyframe frame : controller.keyframeController.getPartKeyframes(part))
            {
                if (frame.frameTime >= time)
                {
                    frame.frameTime++;
                }
            }
        }
    }

    @Override
    public void undo(TimelineController controller, AnimationSequence animation)
    {
        if (partName == null)
        {
            for (Keyframe frame : controller.keyframeController.getAllFrames())
            {
                if (frame.frameTime >= time)
                {
                    frame.frameTime--;
                }
            }
        } else
        {
            Part part = controller.timelineGui.entityModel.getPartFromName(partName);
            for (Keyframe frame : controller.keyframeController.getPartKeyframes(part))
            {
                if (frame.frameTime >= time)
                {
                    frame.frameTime--;
                }
            }
        }
    }
}
