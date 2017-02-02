package obsidianAnimator.gui.sequence.timeline;

import obsidianAPI.render.part.Part;

class Keyframe
{
    final Part part;
    int frameTime;
    //Rotation for parts and position for entityPosition
    final float[] values;
    //Is current keyframe, or is a selected keyframe (multiple selected).
    boolean isCurrent;
    boolean isSelected;

    public Keyframe(int frameTime, Part part, float[] values)
    {
        this.frameTime = frameTime;
        this.part = part;
        this.values = values;
    }
}
