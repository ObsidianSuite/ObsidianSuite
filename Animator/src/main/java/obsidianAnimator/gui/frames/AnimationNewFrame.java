package obsidianAnimator.gui.frames;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.file.FileHandler;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.file.FileChooser;
import obsidianAnimator.file.FileNotChosenException;
import obsidianAnimator.gui.timeline.TimelineGui;
import obsidianAnimator.gui.timeline.TimelineController;

public class AnimationNewFrame extends BaseFrame {

    private JComboBox<String> entityDropDown;
    private String[] entites = ModelHandler.getModelList().toArray(new String[0]);

    public AnimationNewFrame() {
        super("New Animation");
        addComponents();
    }

    @Override
    protected void addComponents() {
        entityDropDown = new JComboBox<String>(entites);
        entityDropDown.setPreferredSize(new Dimension(100, 25));

        JButton create = new JButton("Create");
        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPressed();
            }
        });

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelPressed();
            }
        });

        // create.setPreferredSize(chooseFolder.getPreferredSize());

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 5, 2, 5);

        // Entity
        mainPanel.add(new JLabel("Entity"), c);
        c.gridy = 1;
        mainPanel.add(entityDropDown, c);

        // Buttons
        c.gridwidth = 1;
        c.gridy = 2;
        mainPanel.add(create, c);
        c.gridx = 1;
        mainPanel.add(cancel, c);

    }

    private void createPressed() {
        String entityName = (String) entityDropDown.getSelectedItem();
        AnimationSequence sequence = new AnimationSequence(entityName, "New");
        frame.dispose();
        new TimelineController(sequence).display();
    }

    private void cancelPressed() {
        frame.dispose();
        new HomeFrame().display();
    }

}
