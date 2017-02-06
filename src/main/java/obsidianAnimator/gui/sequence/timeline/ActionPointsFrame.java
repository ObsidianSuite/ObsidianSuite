package obsidianAnimator.gui.sequence.timeline;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import obsidianAPI.animation.AnimationSequence;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

public class ActionPointsFrame extends JFrame
{
    public ActionPointsFrame(final ActionPointsPanel panel) throws HeadlessException
    {
        super("Action Points");

        final int time = (int) panel.timeline.time;
        final AnimationSequence animation = panel.timeline.currentAnimation;

        final JTextField actionField = new JTextField();

        final JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (String s : animation.getActionPoints(time))
                {
                    animation.removeActionPoint(time,s);
                }

                for (String s : Splitter.on(',').split(actionField.getText()))
                {
                    animation.addActionPoint(time, s);
                }

                panel.updateText();
                dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });

        actionField.setText(getTextForAnimations(animation,time));
        actionField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                saveButton.setEnabled(isValidText(actionField.getText()));
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                saveButton.setEnabled(isValidText(actionField.getText()));
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                saveButton.setEnabled(isValidText(actionField.getText()));
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 5, 2, 5);
        mainPanel.add(new JLabel("Points (Separate with ',')"), c);

        c.gridx = 0;
        c.gridy = 1;
        c.ipadx = 5;
        c.ipady = 5;
        mainPanel.add(actionField, c);
        c.ipadx = 0;
        c.ipady = 0;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(buttonPanel, c);

        c.gridx = 0;
        c.gridy = 0;
        buttonPanel.add(saveButton, c);

        c.gridx = 1;
        c.gridy = 0;
        buttonPanel.add(cancelButton, c);

        setMinimumSize(new Dimension(200, 0));
        setAlwaysOnTop(true);
        setLocationRelativeTo(panel);
        setResizable(false);
        setContentPane(mainPanel);
        pack();
        setVisible(true);
    }

    private boolean isValidText(String s)
    {
        return s.isEmpty() || s.matches("[a-zA-Z]+(,[a-zA-Z]+)*");
    }

    private String getTextForAnimations(AnimationSequence anim, int time)
    {
        Collection<String> points = anim.getActionPoints(time);
        return Joiner.on(',').join(points);
    }
}
