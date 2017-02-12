package obsidianAnimator.gui;

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

import obsidianAPI.animation.PartGroups;
import obsidianAPI.render.part.PartObj;
import obsidianAnimator.gui.sequence.timeline.BlankMouseListener;

public class GuiPartSetup extends GuiEntityRenderer
{

	private PartGroups partGroups;
	private SetupFrame setupFrame;

	public GuiPartSetup(String entityName) 
	{
		super(entityName);		
		partGroups = entityModel.partGroups;
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

		public SetupFrame()
		{
			super("Part Setup");

			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			//Init variables
			currentGroups = partGroups.getGroupListAsString();
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
					partGroups.addGroup(groupNameTextField.getText());
					currentGroups = partGroups.getGroupListAsString();
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
			setResizable(false);
			setAlwaysOnTop(true);
			setLocation(5, 35);
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
			updateGroupComboBoxes();
		}
		
		/**
		 * Remove the part panels.
		 */
		private void removePartPanels()
		{
			for(PartPanel p : partPanels)
				partMainPanel.remove(p);
		}

		private void showInsertPoint(PartPanel movingPanel, int mouseY)
		{
			PartPanel lowerPanel = null, upperPanel = null;
			boolean flag = false;
			for(PartPanel p : partPanels)
			{
				p.drawLowerInsert = false;
				p.drawUpperInsert = false;
				if(p.getCentreY() < mouseY)
					lowerPanel = p;
				else if(!flag)
				{
					upperPanel = p;
					flag = true;
				}
				p.revalidate();
				p.repaint();
			}

			if(lowerPanel != null)
			{
				lowerPanel.drawLowerInsert = true;
				lowerPanel.revalidate();
				lowerPanel.repaint();
			}

			if(upperPanel != null)
			{
				upperPanel.drawUpperInsert = true;
				upperPanel.revalidate();
				upperPanel.repaint();
			}
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
				p.drawUpperInsert = false;
				p.drawLowerInsert = false;
				p.repaint();
			}
			
			//Add moving panel if not added yet (last panel).
			if(!movingAdded)
				newOrder.add(movingPanel);
			
			/*
			 * Determine if order has been changed.
			 * moved int = 1 if up, 0 if still and -1 if down.
			 */
			int oldPos = 0;
			int newPos = 0;
			for(int i = 0; i < partPanels.size(); i++)
			{
				if(partPanels.get(i).equals(movingPanel))
					oldPos = i;
				if(newOrder.get(i).equals(movingPanel))
					newPos = i;
			}
			partGroups.changeOrder(movingPanel.part, newPos - oldPos);
			
			//Update and redraw.
			partPanels = newOrder;
			removePartPanels();
			addPartPanels();
			mainPanel.revalidate();
		}

		/**
		 * A JPanel that contains controls for editing a part.
		 * Border turns red when selected.
		 */
		private class PartPanel extends JPanel
		{
			
			private PartObj part;
			private boolean drawUpperInsert = false, drawLowerInsert = false;
			private JComboBox box;
			
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
				box = new JComboBox();
				box.setPreferredSize(new Dimension(200,20));
				box.addActionListener(new GroupDropDownActionListener(part));
				box.setSelectedItem(partGroups.getPartGroup(part));
				add(box, c);

				c.weightx = 0;
				c.gridx = 3;
				DragPanel dp = new DragPanel(this);
				c.anchor = GridBagConstraints.EAST;
				add(dp, c);
				
				//Mouse listener to highlight part when panel entered.
				addMouseListener(new BlankMouseListener()
				{
					@Override
					public void mouseEntered(MouseEvent e) 
					{
						selectedPart = part;
					}
				});
			}

			private int getCentreY()
			{
				return getY() + getHeight()/2;
			}
			
			@Override
			public void paint(Graphics g)
			{			
				super.paint(g);
				g.setColor(Color.BLUE);
				if(drawUpperInsert)
				{
					g.drawLine(0, 1, getWidth(), 1);
					for(int i = 0; i < 10; i++)
						g.drawLine(this.getWidth() - (10 - i), i + 1, this.getWidth(), i + 1);
				}
				if(drawLowerInsert)
				{
					g.drawLine(0, this.getHeight() - 1, getWidth(), this.getHeight() - 1);
					for(int i = 0; i < 10; i++)
						g.drawLine(this.getWidth() - i, this.getHeight() - (10 - i), this.getWidth(), this.getHeight() - (10 - i));
				}
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
				addMouseListener(new BlankMouseListener()
				{
					@Override
					public void mouseEntered(MouseEvent arg0) 
					{
						selectedPart = partPanel.part;
					}

					@Override
					public void mousePressed(MouseEvent arg0) 
					{
						partPanel.setBorder(BorderFactory.createLineBorder(Color.red));
						partPanel.repaint();
					}

					@Override
					public void mouseReleased(MouseEvent e) 
					{
						int abs = partPanel.getY() + e.getY();			
						recalculateOrder(partPanel, abs);
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
						showInsertPoint(partPanel, abs);
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
			for(PartPanel p : partPanels)
			{
				p.box.setModel(new DefaultComboBoxModel(partGroups.getGroupListAsArray()));
				p.box.setSelectedItem(partGroups.getPartGroup(p.part));
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
				partGroups.setPartGroup((String) ((JComboBox) e.getSource()).getSelectedItem(), part);
			}
		}
	}
}
