package obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import net.minecraft.client.Minecraft;
import obsidianAnimator.gui.GuiInventoryChooseItem;
import obsidianAnimator.gui.timeline.GuiAnimationTimeline;
import obsidianAnimator.render.entity.EntityObj;

public class TimelineItemPanel extends JPanel
{
	
	public TimelineItemPanel(TimelineItemController controller)
	{		
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2,5,2,5);
		c.ipadx = 10;
		c.fill = GridBagConstraints.BOTH;

		JButton itemButton = new JButton("Choose Right Hand Item");
		itemButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				Minecraft.getMinecraft().displayGuiScreen(new GuiInventoryChooseItem(controller, controller.getEntityToRender()));
			}
		});
		add(itemButton, c);

		c.gridy = 1;
		JButton emptyItemButton = new JButton("Empty Right Hand");
		emptyItemButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//TODO animation item
//				AnimationData.setAnimationItem(timeline.currentAnimation.getName(), -1);
//				((EntityObj) timeline.entityToRender).setCurrentItem(null); 
			}
		});
		add(emptyItemButton, c);
		setBorder(BorderFactory.createTitledBorder("Item"));
	}

}
