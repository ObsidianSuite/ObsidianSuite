package obsidianAnimator.gui.timeline.changes;

import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.Keyframe;
import obsidianAnimator.gui.timeline.TimelineController;

public class ChangeCreateKeyframe implements AnimationChange
{
    private String partName;
    private int time;
    private float valueX;
    private float valueY;
    private float valueZ;

    public ChangeCreateKeyframe(String partName, int time, float[] values)
    {
        this.partName = partName;
        this.time = time;
        this.valueX = values[0];
        this.valueY = values[1];
        this.valueZ = values[2];
    }

    @Override
    public void apply(TimelineController controller, AnimationSequence animation)
    {
        Part part = controller.timelineGui.entityModel.getPartFromName(partName);
        Keyframe keyframe = new Keyframe(time, part, new float[] {valueX, valueY, valueZ});
        controller.keyframeController.addKeyframe(keyframe);
    }

    @Override
    public void undo(TimelineController controller, AnimationSequence animation)
    {
        Part part = controller.timelineGui.entityModel.getPartFromName(partName);
        controller.keyframeController.deleteKeyframe(part, time);

        if (controller.keyframeController.getPartKeyframes(part).isEmpty())
        {
            part.setToOriginalValues();
        }
    }
}
