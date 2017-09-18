package com.dabigjoe.obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAnimator.gui.timeline.TimelineController;
import com.google.common.base.Joiner;

public class ActionPointsPanel extends JPanel
{
    final TimelineController controller;
    final JLabel label;

    private ActionPointsFrame actionPointsFrame;

    public ActionPointsPanel(TimelineController controller)
    {
        this.controller = controller;

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

    public void updateText()
    {
        final int time = (int) controller.getTime();
        final AnimationSequence anim = controller.currentAnimation;

        Collection<String> points = anim.getActionPoints(time);
        label.setText(Joiner.on(',').join(points));
    }
}
