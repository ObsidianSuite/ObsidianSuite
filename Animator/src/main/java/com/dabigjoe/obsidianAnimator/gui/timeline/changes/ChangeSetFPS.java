package com.dabigjoe.obsidianAnimator.gui.timeline.changes;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;

public class ChangeSetFPS implements AnimationChange
{
    private final int prevFPS;
    private final int newFPS;

    public ChangeSetFPS(int prevFPS, int newFPS)
    {
        this.prevFPS = prevFPS;
        this.newFPS = newFPS;
    }

    @Override
    public void apply(TimelineController controller, AnimationSequence animation)
    {
        animation.setFPS(newFPS);
    }

    @Override
    public void undo(TimelineController controller, AnimationSequence animation)
    {
        animation.setFPS(prevFPS);
    }
}
