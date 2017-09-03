package obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.google.common.base.Joiner;

import obsidianAPI.animation.AnimationSequence;
import obsidianAnimator.gui.timeline.changes.ChangeActionPoints;

public class ActionPointsFrame extends JFrame
{
    public ActionPointsFrame(final ActionPointsPanel panel) throws HeadlessException
    {
        super("Action Points");

        final int time = (int) panel.controller.getTime();
        final AnimationSequence animation = panel.controller.currentAnimation;

        final JTextField actionField = new JTextField();

        final JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new AbstractAction()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                panel.controller.versionController.applyChange(new ChangeActionPoints(time, actionField.getText(), getTextForAnimations(animation, time)));
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

        actionField.setText(getTextForAnimations(animation, time));
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
