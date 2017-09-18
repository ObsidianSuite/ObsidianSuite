package com.dabigjoe.obsidianAnimator.gui.timeline.changes;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;

public interface AnimationChange
{
    void apply(TimelineController controller, AnimationSequence animation);

    void undo(TimelineController controller, AnimationSequence animation);
}
