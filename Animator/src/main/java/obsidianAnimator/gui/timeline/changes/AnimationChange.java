package obsidianAnimator.gui.timeline.changes;

import obsidianAPI.animation.AnimationSequence;
import obsidianAnimator.gui.timeline.TimelineController;

public interface AnimationChange
{
    void apply(TimelineController controller, AnimationSequence animation);

    void undo(TimelineController controller, AnimationSequence animation);
}
