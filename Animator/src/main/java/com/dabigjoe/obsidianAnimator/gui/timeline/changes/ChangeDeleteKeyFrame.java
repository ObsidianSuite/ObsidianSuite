package com.dabigjoe.obsidianAnimator.gui.timeline.changes;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAnimator.gui.timeline.Keyframe;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;

public class ChangeDeleteKeyFrame implements AnimationChange
{
    private String partName;
    private int time;

    private Keyframe removedKeyFrame;

    public ChangeDeleteKeyFrame(String partName, int time)
    {
        this.partName = partName;
        this.time = time;
    }

    @Override
    public void apply(TimelineController controller, AnimationSequence animation)
    {
        Part part = controller.timelineGui.entityModel.getPartFromName(partName);
        Keyframe keyframe = controller.keyframeController.getKeyframe(part, time);
        if (controller.keyframeController.deleteKeyframe(part, time))
        {
            removedKeyFrame = keyframe;
            if (controller.keyframeController.getPartKeyframes(part).isEmpty())
            {
                part.setToOriginalValues();
            }
        }
    }

    @Override
    public void undo(TimelineController controller, AnimationSequence animation)
    {
        if (removedKeyFrame != null)
        {
            controller.keyframeController.addKeyframe(removedKeyFrame);
            removedKeyFrame = null;
        }
    }
}
