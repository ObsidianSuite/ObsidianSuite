package com.dabigjoe.obsidianAnimator.gui.timeline.changes;

import java.util.List;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAnimator.gui.timeline.Keyframe;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;

public class ChangeReverse implements AnimationChange
{
    @Override
    public void apply(TimelineController controller, AnimationSequence animation)
    {
        int totalTime = animation.getTotalTime();
        for (Part part : controller.keyframeController.getPartsWithKeyframes())
        {
            List<Keyframe> frames = controller.keyframeController.getPartKeyframes(part);
            if (frames.size() != 1 || frames.get(0).frameTime > 0)
            {
                for (Keyframe frame : frames)
                {
                    frame.frameTime = totalTime - frame.frameTime;
                }
            }
        }
    }

    @Override
    public void undo(TimelineController controller, AnimationSequence animation)
    {
        apply(controller, animation);
    }
}
