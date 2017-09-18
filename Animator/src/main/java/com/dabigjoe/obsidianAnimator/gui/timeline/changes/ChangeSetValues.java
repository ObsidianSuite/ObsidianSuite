package com.dabigjoe.obsidianAnimator.gui.timeline.changes;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.render.part.Part;
import com.dabigjoe.obsidianAnimator.gui.timeline.Keyframe;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;

public class ChangeSetValues implements AnimationChange
{
    private float prevX;
    private float prevY;
    private float prevZ;
    private float newX;
    private float newY;
    private float newZ;

    private String partName;
    private int time;

    public ChangeSetValues(float[] prevValues, float[] newValues, String partName, int time)
    {
        this.prevX = prevValues[0];
        this.prevY = prevValues[1];
        this.prevZ = prevValues[2];
        this.newX = newValues[0];
        this.newY = newValues[1];
        this.newZ = newValues[2];
        this.partName = partName;
        this.time = time;
    }

    @Override
    public void apply(TimelineController controller, AnimationSequence animation)
    {
        Part part = controller.timelineGui.entityModel.getPartFromName(partName);
        Keyframe toReset = controller.keyframeController.getKeyframe(part, time);
        if (toReset != null)
        {
            toReset.values[0] = newX;
            toReset.values[1] = newY;
            toReset.values[2] = newZ;

            //part.setValues(toReset.values);
        }
    }

    @Override
    public void undo(TimelineController controller, AnimationSequence animation)
    {
        Part part = controller.timelineGui.entityModel.getPartFromName(partName);
        Keyframe toReset = controller.keyframeController.getKeyframe(part, time);
        if (toReset != null)
        {
            toReset.values[0] = prevX;
            toReset.values[1] = prevY;
            toReset.values[2] = prevZ;

            //part.setValues(toReset.values);
        }
    }
}
