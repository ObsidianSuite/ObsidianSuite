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
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import obsidianAnimator.data.ModelHandler;
import obsidianAnimator.gui.GuiInventoryChooseItem;

public class TimelineItemPanel extends JPanel
{
	
	public TimelineItemPanel(TimelineItemController controller)
	{		
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2,2,2,2);
		c.ipadx = 10;
		c.fill = GridBagConstraints.BOTH;

		JButton itemButton = new JButton("Choose Right");
		itemButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				controller.getTimelineFrame().setVisible(false);
				Minecraft.getMinecraft().displayGuiScreen(new GuiInventoryChooseItem(EnumHandSide.RIGHT, controller, controller.getEntityToRender()));
			}
		});
		add(itemButton, c);

		c.gridx = 1;
		JButton emptyItemButton = new JButton("Empty");
		emptyItemButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
		    	controller.getEntityToRender().setCurrentItem(ItemStack.EMPTY, EnumHandSide.RIGHT); 
			}
		});
		add(emptyItemButton, c);

		c.gridy=1;
		c.gridx = 0;
		itemButton = new JButton("Choose Left");
		itemButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				controller.getTimelineFrame().setVisible(false);
				Minecraft.getMinecraft().displayGuiScreen(new GuiInventoryChooseItem(EnumHandSide.LEFT, controller, controller.getEntityToRender()));
			}
		});
		add(itemButton, c);

		c.gridy=1;
		c.gridx = 1;
		emptyItemButton = new JButton("Empty");
		emptyItemButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
		    	controller.getEntityToRender().setCurrentItem(ItemStack.EMPTY, EnumHandSide.LEFT); 
			}
		});
		add(emptyItemButton, c);
		setBorder(BorderFactory.createTitledBorder("Item"));
	}

}
