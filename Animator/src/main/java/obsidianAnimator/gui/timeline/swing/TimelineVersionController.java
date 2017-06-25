package obsidianAnimator.gui.timeline.swing;

import com.google.common.collect.Lists;
import obsidianAnimator.gui.timeline.TimelineController;
import obsidianAnimator.gui.timeline.changes.AnimationChange;

import java.awt.*;
import java.util.List;

public class TimelineVersionController extends TimelineControllerSub
{

    private int animationVersion = -1;
    private List<AnimationChange> changes = Lists.newArrayList();

    public TimelineVersionController(TimelineController controller)
    {
        super(controller);
    }

    public void applyChange(AnimationChange change)
    {
        while (changes.size() > animationVersion + 1)
        {
            changes.remove(changes.size() - 1);
        }
        animationVersion++;

        change.apply(mainController, mainController.currentAnimation);
        onChange();

        changes.add(change);
    }

    public void undo()
    {
        if (animationVersion >= 0)
        {
            changes.get(animationVersion).undo(mainController, mainController.currentAnimation);
            animationVersion--;
            onChange();
        } else
        {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void redo()
    {
        if (animationVersion < changes.size() - 1 && changes.size() > 0)
        {
            changes.get(animationVersion + 1).apply(mainController, mainController.currentAnimation);
            animationVersion++;
            onChange();
        } else
        {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void onChange()
    {
        mainController.updateAnimationParts();
        mainController.setExceptionPart(null);
        mainController.refresh();
        mainController.animationController.onAnimationLengthChange();
    }
}
