package com.dabigjoe.obsidianAnimator.gui.timeline;

import com.dabigjoe.obsidianAPI.render.part.Part;

public class Keyframe
{
    public final Part part;
    public int frameTime;
    //Rotation for parts and position for entityPosition
    public float[] values;
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
