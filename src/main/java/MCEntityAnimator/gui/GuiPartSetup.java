package MCEntityAnimator.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import MCEntityAnimator.animation.PartGroupsAndNames;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartObj;

public class GuiPartSetup extends GuiEntityRenderer
{

	private PartGroupsAndNames groupsAndNames;
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

		//CSV list of current groups.
		private String currentGroups;
		private JPanel mainPanel, partMainPanel;
		//List of panels - one per part.
		private List<PartPanel> partPanels;
		private List<JComboBox> groupComboBoxes;

		public SetupFrame()
		{
			super("Part Setup");

			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			//Init variables
			currentGroups = groupsAndNames.getGroupListAsString();
			groupComboBoxes = new ArrayList<JComboBox>();
			partPanels = new ArrayList<PartPanel>();
			//Add a part panel for each part.
			for(PartObj p : entityModel.getPartObjs())
				partPanels.add(new PartPanel(p));

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
					mc.displayGuiScreen(new GuiAnimationParenting(entityName)); 
				}
			});

			//Add components
			c.insets = new Insets(5,5,5,5);
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

			c.gridx = 0;
			c.gridy = 5;
			c.gridwidth = 5;
			mainPanel.add(generateScrollPane(), c);

			c.gridx = 1;
			c.gridy = 6;
			c.gridwidth = 2;
			mainPanel.add(saveAndExitButton, c);

			setContentPane(mainPanel);
			pack();
			setAlwaysOnTop(true);
			setLocation(10, 10);
			setVisible(true);
		}

		/**
		 * Create a JScrollPane containing all the part panels.
		 */
		private JScrollPane generateScrollPane()
		{
			partMainPanel = new JPanel();
			partMainPanel.setLayout(new GridLayout(entityModel.getPartObjs().size(),0));
			addPartPanels();
			JScrollPane scrollPane = new JScrollPane(partMainPanel);
			scrollPane.setPreferredSize(new Dimension(700,300));
			return scrollPane;
		}

		/**
		 * Add part panels to partMainPanel.
		 */
		private void addPartPanels()
		{
			for(PartPanel p : partPanels)
				partMainPanel.add(p);
		}

		/**
		 * Recalculate the order of the part panels.
		 * Used when dragging a panel.
		 * @param movingPanel - the currently selected panel that is being dragged.
		 * @param mouseY - the absolute y coordinate of the mouse
		 */
		private void recalculateOrder(PartPanel movingPanel, int mouseY)
		{
			//Get new order of panels.
			List<PartPanel> newOrder = new ArrayList<PartPanel>();
			boolean movingAdded = false;
			for(PartPanel p : partPanels)
			{
				if(!p.equals(movingPanel))
				{
					if(!movingAdded && mouseY < p.getCentreY())
					{
						newOrder.add(movingPanel);
						movingAdded = true;
					}
					newOrder.add(p);
				}
			}
			
			//Add moving panel if not added yet (last panel).
			if(!movingAdded)
				newOrder.add(movingPanel);
			
			/*
			 * Determine if order has been changed.
			 * moved int = 1 if up, 0 if still and -1 if down.
			 */
			int moved = 0;
			for(int i = 0; i < partPanels.size(); i++)
			{
				if(!partPanels.get(i).equals(newOrder.get(i)))
				{
					moved = newOrder.get(i).equals(movingPanel) ? 1 : -1;
					break;
				}
			}
			if(moved != 0)
				groupsAndNames.changeOrder(movingPanel.part, moved == 1);
			
			//Update and redraw.
			partPanels = newOrder;
			removePartPanels();
			addPartPanels();
			mainPanel.revalidate();
			mainPanel.repaint();
		}

		/**
		 * REmove the part panels.
		 */
		private void removePartPanels()
		{
			for(PartPanel p : partPanels)
				partMainPanel.remove(p);
		}

		/**
		 * A JPanel that contains controls for editing a part.
		 * Border turns red when selected.
		 */
		private class PartPanel extends JPanel
		{
			
			private PartObj part;

			private PartPanel(final PartObj part)
			{
				this.part = part;
				
				setLayout(new GridBagLayout());
				setBorder(BorderFactory.createLineBorder(Color.black));

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(0,3,0,3);
				c.anchor = GridBagConstraints.CENTER;
				c.gridx = 0;
				c.gridy = 0;
				c.weightx = 1;
				add(new JLabel(part.getName()),c);

				c.gridx = 1;
				c.weightx = 0;
				c.anchor = GridBagConstraints.EAST;
				JTextField textField = new JTextField(part.getDisplayName());
				textField.getDocument().addDocumentListener(new DisplayNameDocumentListener(part, textField));
				textField.setPreferredSize(new Dimension(200,22));
				add(textField, c);

				c.gridx = 2;
				c.weightx = 0;
				JComboBox box = new JComboBox();
				box.setPreferredSize(new Dimension(200,20));
				box.addActionListener(new GroupDropDownActionListener(part));
				box.setSelectedItem(groupsAndNames.getPartGroup(part));
				groupComboBoxes.add(box);
				add(box, c);

				c.weightx = 0;
				c.gridx = 3;
				DragPanel dp = new DragPanel(this);
				c.anchor = GridBagConstraints.EAST;
				add(dp, c);
				
				//Mouse listener to highlight part when panel entered.
				addMouseListener(new MouseListener()
				{
					@Override
					public void mouseClicked(MouseEvent arg0) {}

					@Override
					public void mouseEntered(MouseEvent e) 
					{
						currentPartName = part.getName();
					}

					@Override
					public void mouseExited(MouseEvent e) {}

					@Override
					public void mousePressed(MouseEvent e) {}

					@Override
					public void mouseReleased(MouseEvent e) {}
				});
			}

			private int getCentreY()
			{
				return getY() + getHeight()/2;
			}
		}

		/**
		 * A drag icon. 
		 */
		private class DragPanel extends JPanel
		{

			private DragPanel(final PartPanel partPanel)
			{
				setPreferredSize(new Dimension(30, 24));
				addMouseListener(new MouseListener()
				{
					@Override
					public void mouseClicked(MouseEvent arg0) {}

					@Override
					public void mouseEntered(MouseEvent arg0) {}

					@Override
					public void mouseExited(MouseEvent arg0) {}

					@Override
					public void mousePressed(MouseEvent arg0) 
					{
						partPanel.setBorder(BorderFactory.createLineBorder(Color.red));
						partPanel.repaint();
					}

					@Override
					public void mouseReleased(MouseEvent arg0) 
					{
						partPanel.setBorder(BorderFactory.createLineBorder(Color.black));
						partPanel.repaint();
					}
				});
				addMouseMotionListener(new MouseMotionListener()
				{

					@Override
					public void mouseDragged(MouseEvent e) 
					{
						int abs = partPanel.getY() + e.getY();			
						recalculateOrder(partPanel, abs);
					}

					@Override
					public void mouseMoved(MouseEvent arg0){}
				});
			}

			@Override
			public void paint(Graphics g)
			{
				g.setColor(Color.gray);
				g.fillRect(0, 5, 30, 14);
				g.setColor(Color.black);
				g.fillRect(2, 7, 26, 2);
				g.fillRect(2, 11, 26, 2);
				g.fillRect(2, 15, 26, 2);
			}

		}	

		private void updateGroupComboBoxes()
		{
			for(int i = 0; i < entityModel.getPartObjs().size(); i++)
			{
				PartObj part = entityModel.getPartObjs().get(i);
				groupComboBoxes.get(i).setModel(new DefaultComboBoxModel(groupsAndNames.getGroupListAsArray()));
				groupComboBoxes.get(i).setSelectedItem(groupsAndNames.getPartGroup(part));
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
	}
}
