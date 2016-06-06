package MCEntityAnimator.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import MCEntityAnimator.animation.PartGroupsAndNames;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartObj;

public class GuiPartSetup extends GuiEntityRenderer
{
	
	PartGroupsAndNames groupsAndNames;
	private SetupFrame setupFrame;
	
	public GuiPartSetup(String entityName) 
	{
		super(entityName);		
		groupsAndNames = entityModel.groupsAndNames;
		setupFrame = new SetupFrame();
	}

	@Override
	public void onGuiClosed()
	{
		setupFrame.dispose();
	}
	
	private class SetupFrame extends JFrame
	{

		String currentGroups;
		JComboBox[] groupComboBoxes;
		JTextField[] partDisplayName;
		JButton[] upperButtons;
		JButton[] lowerButtons;
		JPanel mainPanel;
		
		List<Component> componentsToRemove;

		private SetupFrame()
		{
			super("Part Setup");

			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			//Load variables
			currentGroups = groupsAndNames.getGroupListAsString();
			int size = entityModel.getPartObjs().size();
			groupComboBoxes = new JComboBox[size];
			partDisplayName = new JTextField[size];
			upperButtons = new JButton[size];
			lowerButtons = new JButton[size];

			updateComponents();

			//Setup components
			final JTextField groupNameTextField = new JTextField("New Group Name");
			final JLabel currentGroupsLabel = new JLabel(currentGroups);

			JButton addGroupButton = new JButton("Add Group");
			addGroupButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{		
					groupsAndNames.addGroup(groupNameTextField.getText());
					currentGroups = groupsAndNames.getGroupListAsString();
					currentGroupsLabel.setText(currentGroups);
					updateGroupComboBoxes();
				}
			});
			
			JButton saveAndExitButton = new JButton("Save and Exit");
			saveAndExitButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0) 
				{
					mc.displayGuiScreen(new GuiAnimationHome(entityName));
				}
			});

			//Add components
			c.gridx = 0;
			c.gridy = 0;
			mainPanel.add(new JLabel("Groups"), c);

			c.gridx = 1;
			c.gridy = 1;
			mainPanel.add(new JLabel("Current Groups:"), c);
			c.gridx = 2;
			c.gridwidth = 4;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			mainPanel.add(currentGroupsLabel, c);

			c.gridx = 1;
			c.gridy = 2;
			c.gridwidth = 1;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			mainPanel.add(new JLabel("Create new group:"), c);
			c.gridx = 2;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			mainPanel.add(groupNameTextField, c);
			c.gridx = 3;
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			mainPanel.add(addGroupButton, c);

			c.gridx = 0;
			c.gridy = 3;
			mainPanel.add(new JLabel("Parts"), c);

			c.gridx = 1;
			c.gridy = 4;
			mainPanel.add(new JLabel("Original Name"), c);
			c.gridx = 2;
			mainPanel.add(new JLabel("Display Name"), c);
			c.gridx = 3;
			mainPanel.add(new JLabel("Group"), c);
			c.gridx = 4;
			c.gridwidth = 2;
			mainPanel.add(new JLabel("Order"), c);
			
			drawPartSection();
			
			c.gridx = 2;
			c.gridy = 5 + entityModel.getPartObjs().size();
			c.gridwidth = 2;
			mainPanel.add(saveAndExitButton, c);

			
			setContentPane(mainPanel);
			pack();
			setAlwaysOnTop(true);
			setLocation(10, 10);
			setVisible(true);
		}
		
		private void updateComponents()
		{
			for(int i = 0; i < entityModel.getPartObjs().size(); i++)
			{
				PartObj part = entityModel.getPartObjs().get(i);
				
				groupComboBoxes[i] = new JComboBox();
				groupComboBoxes[i].addActionListener(new GroupDropDownActionListener(part));
				groupComboBoxes[i].addMouseListener(new HighlightListener(part));
				groupComboBoxes[i].setSelectedItem(groupsAndNames.getPartGroup(part));

				
				partDisplayName[i] = new JTextField(part.getDisplayName());
				partDisplayName[i].getDocument().addDocumentListener(new DisplayNameDocumentListener(part, partDisplayName[i]));
				partDisplayName[i].addMouseListener(new HighlightListener(part));
				
				upperButtons[i] = new JButton("Up");
				upperButtons[i].addActionListener(new UpDownActionListener(part, true));
				upperButtons[i].addMouseListener(new HighlightListener(part));
				
				lowerButtons[i] = new JButton("Down");
				lowerButtons[i].addActionListener(new UpDownActionListener(part, false));
				lowerButtons[i].addMouseListener(new HighlightListener(part));
			}	
			
			updateGroupComboBoxes();
		}
		
		private void drawPartSection()
		{
			GridBagConstraints c = new GridBagConstraints();
			componentsToRemove = new ArrayList<Component>();
			
			for(int i = 0; i < entityModel.getPartObjs().size(); i++)
			{
				c.gridx = 1;
				c.gridy = 5 + i;
				c.gridwidth = 1;
				JLabel label = new JLabel(entityModel.getPartObjs().get(i).getName());
				mainPanel.add(label, c);
				c.gridx = 2;
				c.weightx = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				mainPanel.add(partDisplayName[i], c);
				c.gridx = 3;
				mainPanel.add(groupComboBoxes[i], c);
				c.weightx = 0;
				c.fill = GridBagConstraints.NONE;
				c.gridx = 4;
				mainPanel.add(upperButtons[i], c);
				c.gridx = 5;
				mainPanel.add(lowerButtons[i], c);
				
				componentsToRemove.add(label);
				componentsToRemove.add(partDisplayName[i]);
				componentsToRemove.add(groupComboBoxes[i]);
				componentsToRemove.add(upperButtons[i]);
				componentsToRemove.add(lowerButtons[i]);
			}
		}

		private void updateGroupComboBoxes()
		{
			for(int i = 0; i < entityModel.getPartObjs().size(); i++)
			{
				PartObj part = entityModel.getPartObjs().get(i);
				groupComboBoxes[i].setModel(new DefaultComboBoxModel(groupsAndNames.getGroupListAsArray()));
				groupComboBoxes[i].setSelectedItem(groupsAndNames.getPartGroup(part));
			}
		}
		
		private class DisplayNameDocumentListener implements DocumentListener
		{	
			private PartObj part;
			private JTextField textField;
			
			private DisplayNameDocumentListener(PartObj part, JTextField textField)
			{
				this.part = part;
				this.textField = textField;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {update();}

			@Override
			public void insertUpdate(DocumentEvent e) {update();}

			@Override
			public void removeUpdate(DocumentEvent e) {update();}
			
			private void update()
			{
				part.setDisplayName(textField.getText());	
			}
		}
		
		private class GroupDropDownActionListener implements ActionListener
		{
			private PartObj part;
			
			private GroupDropDownActionListener(PartObj part)
			{
				this.part = part;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				groupsAndNames.setPartGroup((String) ((JComboBox) e.getSource()).getSelectedItem(), part);
			}
		}
		
		private class UpDownActionListener implements ActionListener
		{
			private PartObj part;
			private boolean up;
						
			private UpDownActionListener(PartObj part, boolean up)
			{
				this.part = part;
				this.up = up;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				groupsAndNames.changeOrder(part, up);
				
				for(Component c : componentsToRemove)
				{
					mainPanel.remove(c);
				}
				updateComponents();
				drawPartSection();
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		}
		
		private class HighlightListener implements MouseListener
		{
			private PartObj part;
			
			private HighlightListener(PartObj part)
			{
				this.part = part;
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) 
			{
				currentPartName = part.getName();
			}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		}
	}

}
