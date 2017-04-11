package obsidianAnimator.gui.timeline.swing.subsection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import obsidianAPI.render.part.Part;
import obsidianAnimator.gui.timeline.BlankMouseListener;
import obsidianAnimator.gui.timeline.Keyframe;
import obsidianAnimator.gui.timeline.swing.component.CopyLabel;

public class TimelineKeyframePanel extends JPanel
{

	private TimelineKeyframeController controller;
	
	public JSlider timeSlider;
	
	private JLabel[] partLabels;
	private DecimalFormat df = new DecimalFormat("#.##");
	
	protected CopyLabel copyLabel;

	public TimelineKeyframePanel(TimelineKeyframeController controller)
	{
		this.controller = controller;
		
		final KeyframeLine[] lines = new KeyframeLine[controller.getTimelineGui().parts.size()];
		for(int i = 0; i < controller.getTimelineGui().parts.size(); i++)
		{
			lines[i] = new KeyframeLine(controller.getTimelineGui().parts.get(i));
		}
		
		final JTextField timeTextField = new JTextField("0");
		timeSlider = new JSlider(0, TimelineKeyframeController.TIMELINE_LENGTH, 0);
		timeSlider.setPaintLabels(true);
		timeSlider.setPaintTicks(true);
		int majorIncrements = (int) (TimelineKeyframeController.TIMELINE_LENGTH/10F);
		timeSlider.setMajorTickSpacing((int) (majorIncrements/5F));
		timeSlider.setMinorTickSpacing(majorIncrements);
		timeSlider.setLabelTable(timeSlider.createStandardLabels(majorIncrements));
		
		timeSlider.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e) 
			{
				timeTextField.setText(df.format(timeSlider.getValue()));
				controller.setTime(timeSlider.getValue());
				for(int i = 0; i < controller.getTimelineGui().parts.size(); i++)
				{
					lines[i].repaint();
				}
				controller.mainController.timelineFrame.actionsPanel.updateText();
			}
		});
		timeSlider.addMouseListener(new BlankMouseListener()
		{
			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				controller.setExceptionPart(null);
			}
		});
		timeTextField.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent ke) 
			{
				String typed = timeTextField.getText();
				timeSlider.setValue(0);
				if(!typed.matches("\\d+(\\.\\d*)?")) 
				{
					return;
				}
				double value = Double.parseDouble(typed);
				timeSlider.setValue((int)value);
				controller.setTime((float) value);
			}
		});
		
	    JScrollBar hbar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 50, 0, TimelineKeyframeController.TIMELINE_LENGTH);
	    JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL, 0, 15, 0, controller.getTimelineGui().parts.size());

		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(timeTextField, c);

		c.fill = GridBagConstraints.BOTH;
		c.gridx = 1;
		c.weighty = 1;
		add(timeSlider, c);

		partLabels = new JLabel[controller.getTimelineGui().parts.size()];
		for(int i = 0; i < controller.getTimelineGui().parts.size(); i++)
		{
			c.gridx = 0;
			c.gridy = i+1;
			c.weightx = 0;
			c.weighty = 0;
			c.insets = new Insets(0, 0, 0, 0);
			c.fill = GridBagConstraints.HORIZONTAL;

			JLabel partLabel = new JLabel(controller.getTimelineGui().parts.get(i).getDisplayName());
			partLabels[i] = partLabel;
			add(partLabel, c);

			c.gridx = 1;
			c.weightx = 1;
			c.weighty = 1;
			c.insets = new Insets(0, 10, 0, 0);
			c.fill = GridBagConstraints.BOTH;
			add(lines[i], c);
		}
		
		c.gridx = 1;
		c.gridy = controller.getTimelineGui().parts.size() + 2;
		c.insets = new Insets(5, 5, 5, 5);
		add(hbar, c);
		
		c.gridx = 2;
		c.gridy = 1;
		c.gridheight = controller.getTimelineGui().parts.size();
		add(vbar, c);
	}

	public void refresthLineColours()
	{
		for(int i = 0; i < partLabels.length; i++)
		{
			if(controller.getTimelineGui().selectedPart != null && partLabels[i].getText().equals(controller.getTimelineGui().selectedPart.getDisplayName()))
				partLabels[i].setForeground(Color.red);
			else
				partLabels[i].setForeground(Color.black);
		}
		revalidate();
		repaint();
	}
	
	public void updateCopyLabel(int x, int y, int time, boolean draw)
	{
		copyLabel.draw = draw;
		copyLabel.time = time;
		copyLabel.x = x;
		copyLabel.y = y;
		copyLabel.repaint();
	}
	
	private class KeyframeLine extends JPanel
	{		
		Keyframe closestKeyframe;
		Part part;
		boolean mouseWithin;
		boolean keyframeTimeChanged;

		private KeyframeLine(final Part part)
		{
			setPreferredSize(new Dimension(500, 25));
			this.part = part;
			mouseWithin = false;
			keyframeTimeChanged = false;
			this.addMouseListener(new BlankMouseListener()
			{
				@Override
				public void mouseClicked(MouseEvent e) 
				{
					Keyframe kf = controller.getExistingKeyframe();
					if(kf != null && e.isControlDown())
						controller.copyKeyframe(kf, part, xToKeyframeTime(e.getX()));
					else if(closestKeyframe != null)
					{
						controller.setTime(closestKeyframe.frameTime);
						timeSlider.setValue((int) controller.getTime());
						controller.getCurrentAnimation().animateAll(controller.getTime(), controller.getTimelineGui().entityModel);
						controller.getTimelineGui().updatePart(part);
					}
				}


				@Override
				public void mouseEntered(MouseEvent e) 
				{
					mouseWithin = true;
					controller.getTimelineGui().hoveredPart = part;
				}

				@Override
				public void mouseExited(MouseEvent e) 
				{
					mouseWithin = false; 
					repaint();
					controller.getTimelineGui().hoveredPart = null;
				}

				@Override
				public void mouseReleased(MouseEvent e) 
				{
					if(keyframeTimeChanged)
						controller.mainController.updateAnimationParts();
					keyframeTimeChanged = false;
				}		
			});
			this.addMouseMotionListener(new MouseMotionListener()
			{
				@Override
				public void mouseDragged(MouseEvent e) 
				{
					if(closestKeyframe != null)
					{
						int prevFrameTime = closestKeyframe.frameTime;
						int kfx = keyframeTimeToX(prevFrameTime);
						int dx = Math.abs(kfx - e.getX());
						if(dx < 15)
						{
							int t = xToKeyframeTime(e.getX());
							if(t >= 0 && t <= 300)
							{
								closestKeyframe.frameTime = t;
								timeSlider.setValue(t);
								repaint();
							}
							if(t != prevFrameTime)
								keyframeTimeChanged = true;
						}
					}
				}

				@Override
				public void mouseMoved(MouseEvent e)
				{
					updateClosestKeyframe(e.getX());
					repaint();
					int x = 200 + KeyframeLine.this.getX() + e.getX();
					int y = KeyframeLine.this.getY() + e.getY();
					updateCopyLabel(x, y, xToKeyframeTime(e.getX()), e.isControlDown());
				}			
			});
		}

		private int keyframeTimeToX(int keyframeTime)
		{
			return (int)(keyframeTime/(float)TimelineKeyframeController.TIMELINE_LENGTH*(getWidth() - 10));
		}

		private int xToKeyframeTime(int x)
		{
			return (int) (x*TimelineKeyframeController.TIMELINE_LENGTH/(float)(getWidth() - 10));
		}

		public void updateClosestKeyframe(int mouseX)
		{
			Keyframe closestKf = null;
			Integer closestDistance = null;
			List<Keyframe> partKeyframes = controller.getPartKeyframes(part);
			if(partKeyframes != null)
			{
				for(Keyframe kf : partKeyframes)
				{
					int kfx = (int)(kf.frameTime/TimelineKeyframeController.TIMELINE_LENGTH*(getWidth() - 10));
					int dx = Math.abs(kfx - mouseX);
					if(closestDistance == null || dx < closestDistance)
					{
						closestDistance = dx;
						closestKf = kf;
					}
				}
			}
			closestKeyframe = closestKf;
		}

		@Override
		public void paint(Graphics g)
		{
			super.paint(g);
			g.drawLine(0, 3, 0, getHeight() - 3);
			g.drawLine(0, getHeight()/2, getWidth() - 10, getHeight()/2);
			
			int sliderX = (int)(controller.getTime()/(float)TimelineKeyframeController.TIMELINE_LENGTH*(getWidth() - 10));
			g.drawLine(sliderX, 0, sliderX, getHeight());

			//Draw controller.keyframes for this line.
			List<Keyframe> partKeyframes = controller.getPartKeyframes(part);
			if(partKeyframes != null)
			{
				for(Keyframe kf : partKeyframes)
				{
					if(controller.getTimelineGui().selectedPart != null && controller.getTimelineGui().selectedPart.equals(part) && controller.getTime() == kf.frameTime)
						g.setColor(Color.green);
					else if(kf.equals(closestKeyframe) && mouseWithin)
						g.setColor(Color.green);
					else
						g.setColor(Color.red);
					g.drawLine((int)(kf.frameTime/(float)TimelineKeyframeController.TIMELINE_LENGTH*(getWidth() - 10)), 4, (int)(kf.frameTime/(float)TimelineKeyframeController.TIMELINE_LENGTH*(getWidth() - 10)), getHeight() - 4);
				}
			}
		}
	}
	
}
