package obsidianAnimator.gui.timeline.changes;

import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.Keyframe;
import obsidianAnimator.gui.timeline.TimelineController;

public class ChangeMoveKeyframe implements AnimationChange
{
    private String partName;
    private int from;
    private int to;

    private Keyframe removedKeyframe;

    public ChangeMoveKeyframe(String partName, int from, int to)
    {
        this.partName = partName;
        this.from = from;
        this.to = to;
    }

    @Override
    public void apply(TimelineController controller, AnimationSequence animation)
    {
        Part part = controller.timelineGui.entityModel.getPartFromName(partName);
        Keyframe keyframe = controller.keyframeController.getKeyframe(part, from);

        removedKeyframe = controller.keyframeController.getKeyframe(part, to);
        if (removedKeyframe != null)
        {
            controller.keyframeController.deleteKeyframe(part, to);
        }

        keyframe.frameTime = to;
    }

    @Override
    public void undo(TimelineController controller, AnimationSequence animation)
    {
        Part part = controller.timelineGui.entityModel.getPartFromName(partName);
        Keyframe keyframe = controller.keyframeController.getKeyframe(part, to);
        keyframe.frameTime = from;

        if (removedKeyframe != null)
        {
            controller.keyframeController.addKeyframe(removedKeyframe);
            removedKeyframe = null;
        }
    }
}
