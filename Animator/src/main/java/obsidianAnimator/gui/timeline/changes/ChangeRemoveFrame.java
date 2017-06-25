package obsidianAnimator.gui.timeline.changes;

import com.google.common.collect.Lists;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.Keyframe;
import obsidianAnimator.gui.timeline.TimelineController;

import java.util.List;

public class ChangeRemoveFrame implements AnimationChange
{
    private String partName;
    private int time;

    private List<Keyframe> removedKeyFrames = Lists.newArrayList();

    public ChangeRemoveFrame(String partName, int time)
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
                if (frame.frameTime == time)
                {
                    removedKeyFrames.add(frame);
                    controller.keyframeController.deleteKeyframe(frame.part, frame.frameTime);
                }
            }

            for (Keyframe frame : controller.keyframeController.getAllFrames())
            {
                if (frame.frameTime > time)
                {
                    frame.frameTime--;
                }
            }
        } else
        {
            Part part = controller.timelineGui.entityModel.getPartFromName(partName);
            for (Keyframe frame : Lists.newArrayList(controller.keyframeController.getPartKeyframes(part)))
            {
                if (frame.frameTime == time)
                {
                    removedKeyFrames.add(frame);
                    controller.keyframeController.deleteKeyframe(frame.part, frame.frameTime);
                }
            }

            for (Keyframe frame : controller.keyframeController.getPartKeyframes(part))
            {
                if (frame.frameTime > time)
                {
                    frame.frameTime--;
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

        for (Keyframe removedKeyFrame : removedKeyFrames)
        {
            controller.keyframeController.addKeyframe(removedKeyFrame);
        }

        removedKeyFrames.clear();
    }
}
