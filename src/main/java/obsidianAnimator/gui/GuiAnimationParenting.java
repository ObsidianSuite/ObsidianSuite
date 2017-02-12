package obsidianAnimator.gui;

import net.minecraft.client.Minecraft;
import obsidianAPI.Util;
import obsidianAPI.animation.AnimationParenting;
import obsidianAPI.render.part.Part;
import obsidianAPI.render.part.PartObj;
import obsidianAnimator.gui.frames.HomeFrame;
import org.lwjgl.input.Mouse;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiAnimationParenting extends GuiEntityRenderer 
{

	private ParentingFrame parentingFrame;
	private RelationFrame relationFrame;

	public GuiAnimationParenting(String entityName)
	{
		super(entityName);
		parentingFrame = new ParentingFrame();
		relationFrame = new RelationFrame();
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		parentingFrame.dispose();
		relationFrame.dispose();
	}

	@Override
	public void handleMouseInput()
	{
		scaleModifier += Mouse.getEventDWheel()/40;
		super.handleMouseInput();
	}

	@Override
	protected void updatePart(Part newPartName)
	{
		if (selectedPart != null && newPartName != null && selectedPart instanceof PartObj && newPartName instanceof PartObj)
		{
			PartObj parent = (PartObj) this.selectedPart;
			PartObj child = (PartObj) newPartName;
			attemptParent(parent, child);
		}
		else
		{
			super.updatePart(newPartName);
		}
	}

	private void attemptParent(PartObj parent, PartObj child)
	{
		if(parent.getName().equals(child.getName()))
		{
			JOptionPane.showMessageDialog(parentingFrame, "Cannot parent a part to itself.", "Parenting issue", JOptionPane.ERROR_MESSAGE);
		} else if (!AnimationParenting.areUnrelated(child, parent) || !AnimationParenting.areUnrelated(parent, child))
		{
			JOptionPane.showMessageDialog(parentingFrame, "Parts are already related.", "Parenting issue", JOptionPane.ERROR_MESSAGE);
		}
		else if(child.getParent() != null)
		{
			Object[] options = {"OK", "Remove bend"};
			int n = JOptionPane.showOptionDialog(parentingFrame, child.getDisplayName() + " already has a parent.", "Parenting issue",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
			if(n == 1)
			{
				entityModel.setParent(child, null);
				relationFrame.updateLabels();
			}
		}
		else
		{
			int n = JOptionPane.showConfirmDialog(parentingFrame, "Parent " + child.getDisplayName() + " to " + parent.getDisplayName() + "?", "Parenting", 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if(n == 0)
				parent(parent, child);
		}
	}

	private void parent(PartObj parent, PartObj child) 
	{
		try
		{

			entityModel.setParent(child, parent);
			relationFrame.updateLabels();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(parentingFrame, "Issue creating relation with bend. Parenting aborted.", "Parenting issue", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean doesGuiPauseGame()
	{
		return false;
	}

	private PartObj getParent()
	{
		return entityModel.getPartObjFromName((String) parentingFrame.parentDropDown.getSelectedItem());
	}

	private PartObj getChild()
	{
		return entityModel.getPartObjFromName((String) parentingFrame.childDropDown.getSelectedItem());
	}

	private class ParentingFrame extends JFrame
	{
		JComboBox<Part> parentDropDown;
		JComboBox<Part> childDropDown;

		private ParentingFrame()
		{
			super("Parenting");

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			parentDropDown = new JComboBox<Part>();
			childDropDown = new JComboBox<Part>();
			for(Part p : parts)
			{
				if(p instanceof PartObj)
				{
					parentDropDown.addItem(p);
					childDropDown.addItem(p);
				}
			}
			parentDropDown.setRenderer(new PartComboBoxRenderer(true));
			childDropDown.setRenderer(new PartComboBoxRenderer(false));

			parentDropDown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					selectedPart = (Part) ((JComboBox<String>) e.getSource()).getSelectedItem();
				}
			});

			childDropDown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					hoveredPart = (Part) ((JComboBox<String>) e.getSource()).getSelectedItem();
				}
			});

			JButton relationButton = new JButton("Add relation");
			relationButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					attemptParent(GuiAnimationParenting.this.getParent(), getChild());
				}
			});

			JButton clearButton = new JButton("Clear");
			clearButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					for (PartObj partObj : entityModel.getPartObjs())
					{
						entityModel.setParent(partObj, null);
						partObj.getChildren().clear();
					}
					relationFrame.updateLabels();
				}
			});

			JButton doneButton = new JButton("Done");
			doneButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					Minecraft.getMinecraft().displayGuiScreen(new GuiBlack());
					new HomeFrame().display();
				}
			});

			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			mainPanel.add(new JLabel("Parent: "), c);
			c.gridx = 1;
			mainPanel.add(parentDropDown, c);
			c.gridx = 2;
			mainPanel.add(new JLabel("Child: "), c);
			c.gridx = 3;
			mainPanel.add(childDropDown, c);

			c.gridx = 0;
			c.gridy = 1;
			c.weightx = 2;
			c.gridwidth = 2;
			mainPanel.add(relationButton, c);
			c.weightx = 1;
			c.gridwidth = 1;
			c.gridx = 2;
			mainPanel.add(clearButton, c);
			c.gridx = 3;
			mainPanel.add(doneButton, c);

			setContentPane(mainPanel);
			pack();
			setAlwaysOnTop(true);
			setLocation(50, 50);
			setResizable(false);
			setVisible(true);
		}

	}

	private class RelationFrame extends JFrame
	{
		
		private JPanel mainPanel;
		private GridBagConstraints c;
		
		private RelationFrame()
		{
			super("Parenting");
			
			setMinimumSize(new Dimension(270,50));
			
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			c = new GridBagConstraints();
			
			c.insets = new Insets(5,10,5,10);
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			updateLabels();
			
			setContentPane(mainPanel);
			pack();
			setAlwaysOnTop(true);
			setLocation(50, 150);
			setResizable(false);
			setVisible(true);
		}
		
		private void updateLabels()
		{
			mainPanel.removeAll();
			
			c.gridx = 0;
			c.gridy = 0;
			mainPanel.add(new JLabel("Parents"),c);
			c.gridx = 1;
			mainPanel.add(new JLabel("Children"),c);
			
			int h = 1;
			for(PartObj parent : entityModel.getPartObjs())
			{
				if (!parent.getChildren().isEmpty())
				{
					c.gridx = 0;
					c.gridy = h;
					mainPanel.add(new JLabel(parent.getDisplayName()), c);
					c.gridx = 1;
					String s = "";
					for (PartObj child : parent.getChildren())
					{
						s = s + child.getDisplayName() + ",";
					}
					if (s.length() > 1)
						s = s.substring(0, s.length() - 1);

					mainPanel.add(new JLabel(s), c);
					h++;
				}
			}
			revalidate();
			pack();
		}
	}

	private class PartComboBoxRenderer extends BasicComboBoxRenderer
	{
		private boolean parentPart;

		private PartComboBoxRenderer(boolean parentPart)
		{
			this.parentPart = parentPart;
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
		{
			if(isSelected) 
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (index > -1) 
				{
					if(parentPart)
						selectedPart = parts.get(index);
					else
						hoveredPart = parts.get(index);
				}
			} 
			else 
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText((value == null) ? "" : ((Part)value).getName());
			return this;
		}
	}

}


