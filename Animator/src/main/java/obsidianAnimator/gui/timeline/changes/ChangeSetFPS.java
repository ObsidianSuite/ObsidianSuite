package obsidianAnimator.gui.timeline.changes;

import obsidianAPI.animation.AnimationSequence;
import obsidianAnimator.gui.timeline.TimelineController;

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
