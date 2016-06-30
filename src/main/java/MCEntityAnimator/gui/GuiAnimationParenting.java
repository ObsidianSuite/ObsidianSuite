package MCEntityAnimator.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.lwjgl.input.Mouse;

import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.distribution.ServerAccess;
import MCEntityAnimator.gui.animation.FileGUI;
import MCEntityAnimator.render.objRendering.Bend;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraft.client.Minecraft;


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

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui()
	{
		super.initGui();
		setup();
	}

	public void setup()
	{
		String setup = AnimationData.getParentingSetup(entityName);
		if(setup != null)
		{
			String[] split = setup.split(",");
			horizontalPan = Integer.parseInt(split[0]);
			verticalPan = Integer.parseInt(split[1]);
			scaleModifier = Integer.parseInt(split[2]);
		}
	}

	public void saveSetup()
	{
		AnimationData.setParentingSetup(entityName, horizontalPan + "," + verticalPan + "," + scaleModifier);
	}

	@Override
	public void onGuiClosed()
	{
		saveSetup();
		parentingFrame.dispose();
		relationFrame.dispose();
	}

	@Override
	public void handleMouseInput()
	{
		scaleModifier += Mouse.getEventDWheel()/40;
		super.handleMouseInput();
	}

	private void attemptParent()
	{
		PartObj parent = getParent();
		PartObj child = getChild();
		if(parent.getName().equals(child.getName()))
			JOptionPane.showMessageDialog(parentingFrame, "Cannot parent a part to itself.", "Parenting issue", JOptionPane.ERROR_MESSAGE);
		else if(AnimationData.getAnipar(entityName).hasParent(child))
		{
			Object[] options = {"OK", "Remove bend"};
			int n = JOptionPane.showOptionDialog(parentingFrame, child.getDisplayName() + " already has a parent.", "Parenting issue",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
			if(n == 1)
			{
				AnimationData.getAnipar(entityName).unParent(child);
				relationFrame.updateLabels();
			}
		}
		else
		{
			int n = JOptionPane.showConfirmDialog(parentingFrame, "Parent " + child.getDisplayName() + " to " + parent.getDisplayName() + "?", "Parenting", 
					JOptionPane.YES_NO_CANCEL_OPTION);
			if(n == 0)
			{
				if(Bend.canCreateBend(child, parent) && JOptionPane.showConfirmDialog(parentingFrame, "Parent with bend?", "Parenting", JOptionPane.YES_NO_OPTION) == 0)
					parent(parent, child, true);
				else
					parent(parent, child, false);
			}
		}
	}

	private void parent(PartObj parent, PartObj child, boolean bend) 
	{
		try
		{
			entityModel.setParent(child, parent, bend);
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
		return Util.getPartObjFromName((String) parentingFrame.parentDropDown.getSelectedItem(), entityModel.parts);
	}

	private PartObj getChild()
	{
		return Util.getPartObjFromName((String) parentingFrame.childDropDown.getSelectedItem(), entityModel.parts);
	}

	private class ParentingFrame extends JFrame
	{
		JComboBox<String> parentDropDown;
		JComboBox<String> childDropDown;

		private ParentingFrame()
		{
			super("Parenting");

			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			parentDropDown = new JComboBox<String>();
			childDropDown = new JComboBox<String>();
			for(String s : parts)
			{
				Part p = Util.getPartFromName(s, entityModel.parts);
				if(p instanceof PartObj)
				{
					parentDropDown.addItem(s);
					childDropDown.addItem(s);
				}
			}
			parentDropDown.setRenderer(new PartComboBoxRenderer(true));
			childDropDown.setRenderer(new PartComboBoxRenderer(false));

			parentDropDown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					currentPartName = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();
				}
			});

			childDropDown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					additionalHighlightPartName = (String) ((JComboBox<String>) e.getSource()).getSelectedItem();
				}
			});

			JButton relationButton = new JButton("Add relation");
			relationButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					attemptParent();
				}
			});

			JButton clearButton = new JButton("Clear");
			clearButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					AnimationData.getAnipar(entityName).clear();
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
					ServerAccess.gui = new FileGUI();
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
			for(PartObj parent : AnimationData.getAnipar(entityName).getAllParents())
			{
				c.gridx = 0;
				c.gridy = h;
				mainPanel.add(new JLabel(parent.getDisplayName()),c);
				c.gridx = 1;
				String s = "";
				for(PartObj child : AnimationData.getAnipar(entityName).getChildren(parent))
				{
					s = s + child.getDisplayName() + ",";
				}
				if(s.length() > 1)
					s = s.substring(0, s.length() - 1);
				
				mainPanel.add(new JLabel(s),c);
				h++;
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
						currentPartName = parts.get(index);
					else
						additionalHighlightPartName = parts.get(index);
				}
			} 
			else 
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText((value == null) ? "" : Util.getDisplayName(value.toString(), entityModel.parts));
			return this;
		}
	}

}


