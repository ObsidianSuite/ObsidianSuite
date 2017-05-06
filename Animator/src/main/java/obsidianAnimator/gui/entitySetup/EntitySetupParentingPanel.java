package obsidianAnimator.gui.entitySetup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import obsidianAPI.render.part.PartObj;

public class EntitySetupParentingPanel extends JPanel
{

	private EntitySetupController controller;
	
	private JScrollPane scrollPane;

	public EntitySetupParentingPanel(EntitySetupController controller)
	{
		this.controller = controller;
		
		setLayout(new BorderLayout());
		
		addScrollPane();
		
		JButton clearButton = new JButton("Clear All");
		clearButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				for (PartObj partObj : controller.getEntityModel().getPartObjs())
				{
					controller.getEntityModel().setParent(partObj, null, false);
					partObj.getChildren().clear();
				}
				refreshScrollPane();
			}
		});
		
		add(clearButton, BorderLayout.SOUTH);
	}
	
	private void addScrollPane()
	{
		JPanel parentingTreePanel = new JPanel();
		parentingTreePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5,5,5,5);
		c.anchor = GridBagConstraints.WEST;
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		parentingTreePanel.add(new JLabel("PARENTS", SwingConstants.CENTER), c);
		c.gridx = 1;
		c.weightx = 1;
		parentingTreePanel.add(new JLabel("CHILDREN", SwingConstants.LEFT), c);
	
		List<PartObj> partObjs = controller.getEntityModel().getPartObjs();
		for(int i = 0; i < partObjs.size(); i++)
		{
			PartObj p = partObjs.get(i);
			
			c.gridx = 0;
			c.gridy = 1+i;
			c.weightx = 0;
			parentingTreePanel.add(new JLabel(p.getDisplayName(), SwingConstants.CENTER), c);
			c.gridx = 1;
			c.weightx = 1;
			parentingTreePanel.add(new JLabel(createChildList(p), SwingConstants.LEFT), c);
		}
		
		scrollPane = new JScrollPane(parentingTreePanel);
		scrollPane.setPreferredSize(new Dimension(300,700));
		add(scrollPane, BorderLayout.CENTER);
	}
	
	protected void refreshScrollPane()
	{
		remove(scrollPane);
		addScrollPane();
		revalidate();
		repaint();
	}
	
	private String createChildList(PartObj parent)
	{
		String childList = "";
		
		for(PartObj child : parent.getChildren())
			childList += child.getDisplayName() + ", ";
		
		if(parent.getChildren().size() > 0)
			childList = childList.substring(0, childList.length() - 2);
		
		return childList;
	}


}
