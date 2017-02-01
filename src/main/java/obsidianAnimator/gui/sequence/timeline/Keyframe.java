package obsidianAnimator.gui.sequence.timeline;

class Keyframe
{
    final String partName;
    int frameTime;
    //Rotation for parts and position for entityPosition
    final float[] values;
    //Is current keyframe, or is a selected keyframe (multiple selected).
    boolean isCurrent;
    boolean isSelected;

    public Keyframe(int frameTime, String partName, float[] values)
    {
        this.frameTime = frameTime;
        this.partName = partName;
        this.values = values;
    }
}
