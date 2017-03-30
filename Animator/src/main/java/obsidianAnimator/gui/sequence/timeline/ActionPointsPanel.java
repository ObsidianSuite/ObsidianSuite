package obsidianAnimator.gui.sequence.timeline;

import com.google.common.base.Joiner;
import obsidianAPI.animation.AnimationSequence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

public class ActionPointsPanel extends JPanel
{
    final GuiAnimationTimeline timeline;
    final JLabel label;

    private ActionPointsFrame actionPointsFrame;

    public ActionPointsPanel(GuiAnimationTimeline timeline)
    {
        this.timeline = timeline;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (actionPointsFrame != null)
                    actionPointsFrame.dispose();
                actionPointsFrame = new ActionPointsFrame(ActionPointsPanel.this);
            }
        });

        this.label = new JLabel("");
        add(editButton);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(this.label);

        setBorder(BorderFactory.createTitledBorder("Action Points"));

        updateText();
    }

    void updateText()
    {
        final int time = (int) timeline.time;
        final AnimationSequence anim = timeline.currentAnimation;

        Collection<String> points = anim.getActionPoints(time);
        label.setText(Joiner.on(',').join(points));
    }
}
