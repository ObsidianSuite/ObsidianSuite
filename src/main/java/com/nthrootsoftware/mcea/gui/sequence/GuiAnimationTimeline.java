package com.nthrootsoftware.mcea.gui.sequence;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.nthrootsoftware.mcea.Util;
import com.nthrootsoftware.mcea.animation.AnimationData;
import com.nthrootsoftware.mcea.animation.AnimationPart;
import com.nthrootsoftware.mcea.animation.AnimationSequence;
import com.nthrootsoftware.mcea.gui.GuiBlack;
import com.nthrootsoftware.mcea.gui.GuiHandler;
import com.nthrootsoftware.mcea.gui.GuiInventoryChooseItem;
import com.nthrootsoftware.mcea.gui.animation.MainGUI;
import com.nthrootsoftware.mcea.render.objRendering.EntityObj;
import com.nthrootsoftware.mcea.render.objRendering.parts.Part;

import net.minecraft.client.Minecraft;

public class GuiAnimationTimeline extends GuiEntityRendererWithTranslation implements ExternalFrame
{

	public AnimationSequence currentAnimation;
	private int animationVersion;
	private List<AnimationSequence> animationVersions;

	private DecimalFormat df = new DecimalFormat("#.##");
	private float time = 0.0F;
	private float timeMultiplier = 1.0F;
	private TimelineFrame timelineFrame;
	protected Map<String, List<Keyframe>> keyframes = new HashMap<String, List<Keyframe>>();

	private String exceptionPartName = "";

	private boolean boolPlay;	
	private boolean boolLoop;
	
	//Nano time at which the animation started playing (play button pressed).
	private long playStartTimeNano;
	//Frame time at which the animation started playing (play button pressed).
	private float playStartTimeFrame;
	

	public GuiAnimationTimeline(String entityName, AnimationSequence animation)
	{
		super(entityName);

		this.currentAnimation = animation;
		boolPlay = false;

		loadKeyframes();
		loadFrames();

		animationVersion = 0;
		animationVersions = new ArrayList<AnimationSequence>();
		updateAnimation();

		((EntityObj) entityToRender).setCurrentItem(AnimationData.getAnimationItem(animation.getName()));   	

	}

	/* ---------------------------------------------------- *
	 * 						Setup							*
	 * ---------------------------------------------------- */

	@Override
	public void initGui()
	{
		super.initGui();
	}

	public void loadFrames()
	{
		timelineFrame = new TimelineFrame();

		JFrame frame = timelineFrame;
		InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = frame.getRootPane().getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "spacePressed");
		actionMap.put("spacePressed", new SpaceAction());		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "wPressed");
		actionMap.put("wPressed", new WAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "sPressed");
		actionMap.put("sPressed", new SAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "aPressed");
		actionMap.put("aPressed", new AAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "dPressed");
		actionMap.put("dPressed", new DAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK, true), "undoReleased");
		actionMap.put("undoReleased", new UndoAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK, true), "redoReleased");
		actionMap.put("redoReleased", new RedoAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deletePressed");
		actionMap.put("deletePressed", new DeleteAction());
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escPressed");
		actionMap.put("escPressed", new EscAction());

		for(int j = 0; j <= 9; j++)
		{
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + j, 0), "numpad" + j);
			actionMap.put("numpad" + j, new ChangeViewAction(j));
		}

		timelineFrame.refresthLineColours();
	}

	/**
	 * Creates keyframes from the animation sequence. 
	 */
	public void loadKeyframes()
	{	
		keyframes.clear();
		for(AnimationPart animpart : currentAnimation.getAnimations())
		{
			String partName = animpart.getPart().getName();
			List<Keyframe> partKfs = keyframes.get(partName);
			if(keyframes.get(partName) == null)
				partKfs = new ArrayList<Keyframe>();			
			Part mr = Util.getPartFromName(animpart.getPart().getName(), entityModel.parts);	
			float[] defaults = animpart.getPart().getOriginalValues();
			//If the movement starts at time zero, and the part isn't in its original position, add a keyframe at time zero.
			if(animpart.getStartTime() == 0.0F)
			{
				if(!animpart.isStartPos(defaults))
				{
					Keyframe kf = new Keyframe(0, partName, animpart.getStartPosition());
					partKfs.add(kf);
				}
				if(animpart.isEndPosDifferentToStartPos() || currentAnimation.multiPartSequence(partName))
				{
					Keyframe kf2 = new Keyframe((int) animpart.getEndTime(), partName, animpart.getEndPosition());
					partKfs.add(kf2);
				}
			}
			else
			{
				Keyframe kf = new Keyframe((int) animpart.getEndTime(), partName, animpart.getEndPosition());
				partKfs.add(kf);
			}
			keyframes.put(partName, partKfs);
		}
	}

	/* ---------------------------------------------------- *
	 * 						General							*
	 * ---------------------------------------------------- */

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		timelineFrame.dispose();
		if(animationVersion != 0)
			AnimationData.addChangedSequence(entityName, currentAnimation.getName());
	}

	public void drawScreen(int par1, int par2, float par3)
	{				
		if(boolPlay)
		{
			time = Util.getAnimationFrameTime(playStartTimeNano, playStartTimeFrame, 25, timeMultiplier);
			exceptionPartName = "";
			if(time >= currentAnimation.getTotalTime())
			{
				if(boolLoop)
				{
					time = 0.0F;
					playStartTimeNano = System.nanoTime();
					playStartTimeFrame = 0;
				}
				else
				{
					boolPlay = false;
					time = currentAnimation.getTotalTime();
				}
			}
			timelineFrame.timeSlider.setValue((int) time);
			timelineFrame.repaint();
		}

		this.currentAnimation.animateAll(time, entityModel, exceptionPartName);
		
		updateExternalFrameFromDisplay();
		timelineFrame.optionsPanel.updatePlayPauseButton();
		timelineFrame.optionsPanel.updatePartLabels();

		super.drawScreen(par1, par2, par3);
	}

	/* ---------------------------------------------------- *
	 * 				   Keyframe manipulation				*
	 * ---------------------------------------------------- */

	private void addKeyframe()
	{
		if(!currentPartName.equals(""))
		{
			Part part = Util.getPartFromName(currentPartName, entityModel.parts);
			Keyframe kf = new Keyframe((int) time, currentPartName, part.getValues());
			addKeyframe(kf);
		}
	}

	private void addKeyframe(Keyframe kf)
	{
		List<Keyframe> partKeyframes = keyframes.get(kf.partName);
		boolean keyframeExists = false;
		if(partKeyframes == null)
			partKeyframes = new ArrayList<Keyframe>();
		else 
		{
			Keyframe keyframeToRemove = null;
			for(Keyframe pkf : partKeyframes)
			{
				if(pkf.frameTime == kf.frameTime)
					keyframeToRemove = pkf;
			}
			if(keyframeToRemove != null)
			{
				keyframeExists = true;
				partKeyframes.remove(keyframeToRemove);
			}
		}
		partKeyframes.add(kf);
		keyframes.put(kf.partName, partKeyframes);
		timelineFrame.refresthLineColours();
		updateAnimation();
	}

	private void deleteKeyframe()
	{
		List<Keyframe> partKeyframes = keyframes.get(currentPartName);
		if(partKeyframes != null)
		{
			Keyframe keyframeToRemove = null;
			for(Keyframe pkf : partKeyframes)
			{
				if(pkf.frameTime == time)
					keyframeToRemove = pkf;
			}
			boolean keyframeRemoved = false;
			if(keyframeToRemove != null)
			{
				keyframeRemoved = true;
				partKeyframes.remove(keyframeToRemove);
			}
			keyframes.put(currentPartName, partKeyframes);
			timelineFrame.repaint();
			if(keyframeRemoved)
			{
				exceptionPartName = "";
				updateAnimation();
			}
		}
		timelineFrame.refresh();
	}

	private void copyKeyframe(Keyframe kf, String partName, int time)
	{
		if(partName.equals("entitypos") && !kf.partName.equals("entitypos"))
			JOptionPane.showMessageDialog(timelineFrame, "Only entitypos can copy to entitypos.");
		else if(partName.equals("prop_rot") && !kf.partName.equals("prop_rot"))
			JOptionPane.showMessageDialog(timelineFrame, "Only prop_rot can copy to prop_rot.");
		else if(partName.equals("prop_trans") && !kf.partName.equals("prop_trans"))
			JOptionPane.showMessageDialog(timelineFrame, "Only prop_trans can copy to prop_trans.");
		else if((kf.partName.equals("entitypos") || kf.partName.equals("prop_rot") || kf.partName.equals("prop_trans")) && !kf.partName.equals(partName))
			JOptionPane.showMessageDialog(timelineFrame, kf.partName + " can only copy to itself.");
		else
			addKeyframe(new Keyframe(time, partName, kf.values.clone()));

	}

	private boolean keyframeExists()
	{
		List<Keyframe> partKeyframes = keyframes.get(currentPartName);
		if(partKeyframes != null)
		{
			for(Keyframe kf : partKeyframes)
			{
				if((int)kf.frameTime == (int)time)
					return true;
			}
		}
		return false;
	}

	private Keyframe getExistingKeyframe()
	{
		List<Keyframe> partKeyframes = keyframes.get(currentPartName);
		if(partKeyframes != null)
		{
			for(Keyframe kf : partKeyframes)
			{
				if((int)kf.frameTime == (int)time)
					return kf;
			}
		}
		return null;
	}

	private float getLastKeyFrameTime() 
	{
		float lastFrameTime = 0;
		for(String part : parts)
		{
			if(keyframes.get(part) != null)
			{
				for(Keyframe kf : keyframes.get(part))
				{
					if(kf.frameTime > lastFrameTime)
						lastFrameTime = kf.frameTime;
				}
			}

		}
		return lastFrameTime;
	}

	private boolean doesPartOnlyHaveOneKeyframe(String partName) 
	{
		List<Keyframe> kfs = keyframes.get(partName);
		return (kfs != null && kfs.size() == 1);
	}

	/* ---------------------------------------------------- *
	 * 				   Animation manipulation				*
	 * ---------------------------------------------------- */

	private void updateAnimation()
	{
		//Create new animation object if new version
		AnimationSequence sequence = new AnimationSequence(currentAnimation.getName());
		//Generate animation from keyframes.
		for(String partName : keyframes.keySet())
		{
			Part part = Util.getPartFromName(partName, entityModel.parts);
			for(Keyframe kf : keyframes.get(partName))
			{
				if(kf.frameTime != 0.0F)
				{
					Keyframe prevKf = kf.getPreviousKeyframe();
					sequence.addAnimation(new AnimationPart(prevKf.frameTime, kf.frameTime, prevKf.values, kf.values, part));
				}
				else if(doesPartOnlyHaveOneKeyframe(part.getName()))
				{
					//Used for parts that only have one keyframe and where that keyframe is at the beginning 
					//The part will maintain that rotation throughout the whole animation.
					sequence.addAnimation(new AnimationPart(0.0F, getLastKeyFrameTime(), kf.values, kf.values, part));
				}
			}
		}
		//Remove all animations in front of current animation.
		//If undo has been called and then changes made, the state that was undone from is now out of sync, so remove it.
		//Several undo's could have been done together, so remove all in front.
		Iterator<AnimationSequence> iter = animationVersions.iterator();
		int i = 0;
		while(iter.hasNext())
		{
			iter.next();
			if(i > animationVersion)
				iter.remove();
			i++;
		}
		//Add new version to animation versions and update animationVersion and currentAnimation
		animationVersions.add(sequence);
		animationVersion = animationVersions.size() - 1;
		currentAnimation = sequence;

		//Update animation sequence in AnimationData.
		AnimationData.addSequence(entityName, currentAnimation);
		
		onAnimationLengthChange();
	}

	/* ---------------------------------------------------- *
	 * 				  	Part manipulation					*
	 * ---------------------------------------------------- */

	@Override
	protected void updatePart(String newPartName)
	{
		super.updatePart(newPartName);
		exceptionPartName = newPartName;
		timelineFrame.refresh();
	}	

	/* ---------------------------------------------------- *
	 * 				  		 Undo/redo						*
	 * ---------------------------------------------------- */

	private void undo()
	{
		if(animationVersion > 0)
		{
			animationVersion --;
			currentAnimation = animationVersions.get(animationVersion);
			AnimationData.addSequence(entityName, currentAnimation);
			loadKeyframes();
			timelineFrame.refresh();
		}
		else
			Toolkit.getDefaultToolkit().beep();
	}

	private void redo()
	{
		if(animationVersion < animationVersions.size() - 1)
		{
			animationVersion ++;
			currentAnimation = animationVersions.get(animationVersion);
			AnimationData.addSequence(entityName, currentAnimation);
			loadKeyframes();
			timelineFrame.refresh();
		}
		else
			Toolkit.getDefaultToolkit().beep();
	}

	/* ---------------------------------------------------- *
	 * 				   		Control							*
	 * ---------------------------------------------------- */

	@Override
	protected void keyTyped(char par1, int par2)
	{
		switch(par2)
		{	
		case Keyboard.KEY_SPACE:
			new SpaceAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
			break;
		case Keyboard.KEY_W:
			new WAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
			break;
		case Keyboard.KEY_S:
			new SAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
			break;
		case Keyboard.KEY_A:
			new AAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
			break;
		case Keyboard.KEY_D:
			new DAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
			break;
		case Keyboard.KEY_Z:
			if(this.isCtrlKeyDown())
				new UndoAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
			break;
		case Keyboard.KEY_Y:
			if(this.isCtrlKeyDown())
				new RedoAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
			break;
		case Keyboard.KEY_DELETE:
			new DeleteAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
			break;	
			//LWJGL's assignment of keys for the numpad is dumb so we have to do this manually...
		case Keyboard.KEY_NUMPAD1:
			new ChangeViewAction(1).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "")); break;
		case Keyboard.KEY_NUMPAD2:
			new ChangeViewAction(2).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "")); break;
		case Keyboard.KEY_NUMPAD4:
			new ChangeViewAction(4).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "")); break;
		case Keyboard.KEY_NUMPAD5:
			new ChangeViewAction(5).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "")); break;
		case Keyboard.KEY_NUMPAD6:
			new ChangeViewAction(6).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "")); break;
		case Keyboard.KEY_NUMPAD7:
			new ChangeViewAction(7).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "")); break;
		case Keyboard.KEY_NUMPAD8:
			new ChangeViewAction(8).actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, "")); break;
		case Keyboard.KEY_ESCAPE:
			new EscAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_FIRST, ""));
		}

		if(par2 != Keyboard.KEY_ESCAPE)
			super.keyTyped(par1, par2);
	}

	@Override
	protected void onControllerDrag()
	{
		super.onControllerDrag();
		exceptionPartName = currentPartName;
	}

	@Override	
	protected void onControllerRelease()
	{
		super.onControllerRelease();
		if(keyframeExists())
			addKeyframe();
	}

	@Override
	public void updateExternalFrameFromDisplay() 
	{
		timelineFrame.setAlwaysOnTop(Display.isActive());
	}

	private void close()
	{
		mc.displayGuiScreen(new GuiBlack());
		GuiHandler.mainGui = new MainGUI();
	}
	
	public void onAnimationLengthChange()
	{
		System.out.println(timelineFrame);
		timelineFrame.optionsPanel.lengthFrameLabel.setText((int)currentAnimation.getTotalTime() + " frames");
		//TODO substitute 25F for FPS
		timelineFrame.optionsPanel.lengthSecondsLabel.setText(currentAnimation.getTotalTime()/25F + " seconds");
	}

	/* ---------------------------------------------------- *
	 * 				   	Timeline Frame						*
	 * ---------------------------------------------------- */

	private class TimelineFrame extends JFrame
	{
		KeyframeLine[] lines;
		JSlider timeSlider;
		int timelineLength = 100;
		int timelineLengthMax = 300;
		int timelineLengthMin = 50;
		JPanel mainPanel;
		JLabel[] partLabels;
		OptionsPanel optionsPanel;
		CopyLabel copyLabel;

		private TimelineFrame()
		{
			super("Timeline");

			final KeyframeLine[] lines = new KeyframeLine[parts.size()];
			for(int i = 0; i < parts.size(); i++)
			{
				lines[i] = new KeyframeLine(parts.get(i));
			}


			mainPanel = new JPanel();
			optionsPanel = new OptionsPanel();

			JPanel timelinePanel = new JPanel();
			final JTextField timeTextField = new JTextField("0");
			timeSlider = new JSlider(0, timelineLengthMax, 0);
			updateTimelineLength(0);
			timeSlider.setPaintLabels(true);
			timeSlider.setPaintTicks(true);
			timeSlider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					timeTextField.setText(df.format(timeSlider.getValue()));
					time = timeSlider.getValue();
					for(int i = 0; i < parts.size(); i++)
					{
						lines[i].repaint();
					}
				}
			});
			timeSlider.addMouseListener(new MouseListener()
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
					exceptionPartName = "";
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {}	
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
					time = (float) value;
				}
			});

			addMouseWheelListener(new MouseWheelListener()
			{
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) 
				{
					updateTimelineLength((int) (e.getPreciseWheelRotation()*5));
				}
			});

			timelinePanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;

			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			timelinePanel.add(timeTextField, c);

			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.weighty = 1;
			timelinePanel.add(timeSlider, c);

			partLabels = new JLabel[parts.size()];
			for(int i = 0; i < parts.size(); i++)
			{
				String s = parts.get(i);
				c.gridx = 0;
				c.gridy = i+1;
				c.weightx = 0;
				c.weighty = 0;
				c.insets = new Insets(0, 0, 0, 0);
				c.fill = GridBagConstraints.HORIZONTAL;

				JLabel partLabel = new JLabel(Util.getDisplayName(parts.get(i), entityModel.parts));
				partLabels[i] = partLabel;
				timelinePanel.add(partLabel, c);

				c.gridx = 1;
				c.weightx = 1;
				c.weighty = 1;
				c.insets = new Insets(0, 10, 0, 0);
				c.fill = GridBagConstraints.BOTH;
				timelinePanel.add(lines[i], c);
			}

			JScrollPane scrollPane = new JScrollPane(timelinePanel);
			scrollPane.setPreferredSize(new Dimension(700,400));
			scrollPane.setWheelScrollingEnabled(false);

			mainPanel.add(optionsPanel);
			mainPanel.add(scrollPane);

			setContentPane(mainPanel);
			pack();
			setAlwaysOnTop(true);
			if(Display.isVisible())
				setLocation(Display.getX() + 50, Display.getY() + 520);
			else
			{
				setLocationRelativeTo(null);
				setLocation(50, 520);
			}


			copyLabel = new CopyLabel();
			JLayeredPane layeredPane = getRootPane().getLayeredPane();
			layeredPane.add(copyLabel, JLayeredPane.DRAG_LAYER);
			copyLabel.setBounds(0, 0, getWidth(), getHeight());

			setVisible(true);
			setResizable(false);
			
			addWindowListener(new WindowAdapter()
			{
				
				@Override
				public void windowClosing(WindowEvent e)
				{
					close();
				}
				
			});
		}

		private void refresh()
		{
			optionsPanel.updatePlayPauseButton();
			optionsPanel.updatePartLabels();
			refresthLineColours();
			revalidate();
			repaint();
		}

		private void updateTimelineLength(int delta)
		{
			int newLength = timelineLength + delta;
			if(newLength < timelineLengthMin)
				newLength = timelineLengthMin;
			else if(newLength > timelineLengthMax)
				newLength = timelineLengthMax;
			timelineLength = newLength;

			timeSlider.setMaximum(timelineLength);
			int majorIncrements = (int) (timelineLength/6F);
			timeSlider.setMajorTickSpacing((int) (majorIncrements/5F));
			timeSlider.setMinorTickSpacing(majorIncrements);
			timeSlider.setLabelTable(timeSlider.createStandardLabels(majorIncrements));
			repaint();
		}

		private void refresthLineColours()
		{
			for(int i = 0; i < partLabels.length; i++)
			{
				if(!currentPartName.equals("") && partLabels[i].getText().equals(Util.getDisplayName(currentPartName, entityModel.parts)))
					partLabels[i].setForeground(Color.red);
				else
					partLabels[i].setForeground(Color.black);
			}
			repaint();
		}

		private void updateCopyLabel(int x, int y, int time, boolean draw)
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
			String partName;
			boolean mouseWithin;
			boolean keyframeTimeChanged;

			private KeyframeLine(final String partName)
			{
				setPreferredSize(new Dimension(500, 25));
				this.partName = partName;
				mouseWithin = false;
				keyframeTimeChanged = false;
				this.addMouseListener(new MouseListener()
				{
					@Override
					public void mouseClicked(MouseEvent e) 
					{
						Keyframe kf = getExistingKeyframe();
						if(kf != null && e.isControlDown())
							copyKeyframe(kf, partName, xToKeyframeTime(e.getX()));
						else if(closestKeyframe != null)
						{
							time = closestKeyframe.frameTime;
							timelineFrame.timeSlider.setValue((int) time);
							currentAnimation.animateAll(time, entityModel);
							updatePart(partName);
						}
					}


					@Override
					public void mouseEntered(MouseEvent e) 
					{
						mouseWithin = true; 				
						additionalHighlightPartName = partName;
					}

					@Override
					public void mouseExited(MouseEvent e) 
					{
						mouseWithin = false; 
						repaint();
						additionalHighlightPartName = "";
					}

					@Override
					public void mousePressed(MouseEvent e) {}

					@Override
					public void mouseReleased(MouseEvent e) 
					{
						if(keyframeTimeChanged)
							updateAnimation();
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
									timelineFrame.timeSlider.setValue(t);
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
				return (int)(keyframeTime/(float)timelineLength*(getWidth() - 10));
			}

			private int xToKeyframeTime(int x)
			{
				return (int) (x*timelineLength/(float)(getWidth() - 10));
			}

			public void updateClosestKeyframe(int mouseX)
			{
				Keyframe closestKf = null;
				Integer closestDistance = null;
				if(keyframes.get(partName) != null)
				{
					for(Keyframe kf : keyframes.get(partName))
					{
						int kfx = (int)(kf.frameTime/(float)timelineLength*(getWidth() - 10));
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
				g.drawLine((int)(time/(float)timelineLength*(getWidth() - 10)), 0, (int)(time/(float)timelineLength*(getWidth() - 10)), getHeight());

				//Draw keyframes for this line.
				//TODO when name has changed (dropboxes..)
				if(keyframes.get(partName) != null)
				{
					for(Keyframe kf : keyframes.get(partName))
					{
						if(currentPartName.equals(partName) && time ==  kf.frameTime)
							g.setColor(Color.green);
						else if(kf.equals(closestKeyframe) && mouseWithin)
							g.setColor(Color.green);
						else
							g.setColor(Color.red);
						g.drawLine((int)(kf.frameTime/(float)timelineLength*(getWidth() - 10)), 4, (int)(kf.frameTime/(float)timelineLength*(getWidth() - 10)), getHeight() - 4);
					}
				}
			}
		}
	}

	/* ---------------------------------------------------- *
	 * 				  	   Options panel					*
	 * ---------------------------------------------------- */

	private class OptionsPanel extends JPanel
	{
		JButton playPauseButton;
		JLabel partName, partX, partY, partZ, lengthFrameLabel, lengthSecondsLabel;

		private OptionsPanel()
		{				
			playPauseButton = new JButton("Play");
			playPauseButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					if(time >= currentAnimation.getTotalTime())
						time = 0;
					boolPlay = !boolPlay; 		
					if(boolPlay)
					{
						playStartTimeNano = System.nanoTime();
						playStartTimeFrame = time;
					}
						
					updatePlayPauseButton();
				}
			});

			JPanel animationPanel = new JPanel();
			
			lengthFrameLabel = new JLabel();
			lengthSecondsLabel = new JLabel();
			
			JButton fpsButton = new JButton("Set FPS");
			fpsButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					getUserFPS();
				}
			});
			
			final JLabel valueLabel = new JLabel();
			valueLabel.setPreferredSize(new Dimension(30, 16));
			valueLabel.setText("100%");

			final JSlider slider = new JSlider(0, 200, 100);
			slider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e)
				{
					valueLabel.setText(slider.getValue() + "%");
					timeMultiplier = slider.getValue()/100F;
				}
			});

			JButton resetButton = new JButton("Reset");
			resetButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					slider.setValue(100);
				}
			});

			animationPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.gridwidth = 2;		
			c.anchor = c.CENTER;
			c.insets = new Insets(2,2,2,2);
			animationPanel.add(new JLabel("Length"), c);
			
			c.gridwidth = 1;
			c.gridy = 1;
			animationPanel.add(lengthFrameLabel, c);
			c.gridx = 1;
			animationPanel.add(lengthSecondsLabel, c);
			
			c.gridx = 0;
			c.gridy = 2;
			animationPanel.add(new JLabel("25 FPS"), c);
			c.gridx = 1;
			animationPanel.add(fpsButton, c);
			
			c.gridwidth = 2;
			c.gridx = 0;
			c.gridy = 3;
			animationPanel.add(new JLabel("Play speed"), c);
			
			c.gridy = 4;
			animationPanel.add(slider, c);
			
			c.gridwidth = 1;
			c.gridy = 5;
			animationPanel.add(valueLabel,c);
			c.gridx = 1;
			animationPanel.add(resetButton,c);

			animationPanel.setBorder(BorderFactory.createTitledBorder("Animation"));

			JPanel partPanel = new JPanel();

			partName = new JLabel();
			partX = new JLabel();
			partY = new JLabel();
			partZ = new JLabel();

			updatePartLabels();

			partPanel.setLayout(new GridBagLayout());
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			partPanel.add(partName,c);
			c.gridy = 1;
			partPanel.add(partX,c);
			c.gridy = 2;
			partPanel.add(partY,c);
			c.gridy = 3;
			partPanel.add(partZ,c);

			partPanel.setBorder(BorderFactory.createTitledBorder("Part"));
			
			JPanel checkboxPanel = new JPanel();
			checkboxPanel.setLayout(new GridBagLayout());
			c.gridx = 0;
			c.gridy = 0;
			c.ipadx = 0;
			for(int i = 0; i < 4; i++)
			{	
				c.gridx = 0;
				c.gridy = i;
				c.anchor = GridBagConstraints.EAST;
				JCheckBox cb = new JCheckBox();
				cb.setHorizontalAlignment(JCheckBox.RIGHT);
				checkboxPanel.add(cb, c);
				String s = "";
				switch(i)
				{
				case 0: 
					s = "Loop"; 
					cb.setSelected(boolLoop);
					cb.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent actionEvent) 
						{
							AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
							boolLoop = abstractButton.getModel().isSelected();
						}
					});
					break;
				case 1: s = "Shield"; break; //TODO shield??
				case 2: 
					s = "Base";
					cb.setSelected(boolBase);
					cb.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent actionEvent) 
						{
							AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
							boolBase = abstractButton.getModel().isSelected();
						}
					});
					break;
				case 3:
					s = "Grid";
					cb.setSelected(boolGrid);
					cb.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent actionEvent) 
						{
							AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
							boolGrid = abstractButton.getModel().isSelected();
						}
					});
					break;
				}
				c.gridx = 1;
				c.anchor = GridBagConstraints.WEST;
				checkboxPanel.add(new JLabel(s),c);
			}
			checkboxPanel.setBorder(BorderFactory.createTitledBorder("Render"));

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());

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
					mc.displayGuiScreen(new GuiInventoryChooseItem(GuiAnimationTimeline.this, (EntityObj) entityToRender));
				}
			});
			buttonPanel.add(itemButton, c);

			c.gridy = 1;
			JButton emptyItemButton = new JButton("Empty Right Hand");
			emptyItemButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					AnimationData.setAnimationItem(currentAnimation.getName(), -1);
					((EntityObj) entityToRender).setCurrentItem(null); 
				}
			});
			buttonPanel.add(emptyItemButton, c);
			buttonPanel.setBorder(BorderFactory.createTitledBorder("Item"));

			JButton duplicateButton = new JButton("Duplicate");
			duplicateButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					String newName = JOptionPane.showInputDialog(timelineFrame, "Name of duplicate animation: ");
					if(newName == null || newName.equals("") || newName.equals(" "))
						JOptionPane.showMessageDialog(timelineFrame, "Invalid name");
					else if(AnimationData.sequenceExists(entityName, newName))
						JOptionPane.showMessageDialog(timelineFrame, "An animation with this name already exists.");
					else
					{
						AnimationSequence sequence = currentAnimation.copy(newName);
						AnimationData.addSequence(entityName, sequence);
						Minecraft.getMinecraft().displayGuiScreen(new GuiAnimationTimeline(entityName, sequence));
					}
				}
			});

			JButton backButton = new JButton("Back");
			backButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					close();
				}
			});

			setLayout(new GridBagLayout());
			c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.insets = new Insets(2,5,2,5);
			add(playPauseButton,c);
			c.insets = new Insets(0,2,0,2);
			c.gridy = 1;
			add(animationPanel,c);
			c.gridy = 2;
			add(partPanel,c);
			c.gridy = 3;
			add(checkboxPanel,c);
			c.gridy = 4;
			add(buttonPanel,c);
			c.gridy = 5;
			c.insets = new Insets(2,5,2,5);
			add(duplicateButton,c);
			c.gridy = 6;
			add(backButton,c);
		}

		private void updatePartLabels()
		{
			String name = "No part selected";
			String x="-",y="-",z="-";
			if(currentPartName != null && !currentPartName.equals(""))
			{
				Part part = Util.getPartFromName(currentPartName, entityModel.parts);
				name = part.getDisplayName();
				x = df.format(part.getValue(0));
				y = df.format(part.getValue(1));
				z = df.format(part.getValue(2));
			}
			partName.setText(name);
			partX.setText("X: " + x);
			partY.setText("Y: " + y);
			partZ.setText("Z: " + z);
		}

		private void updatePlayPauseButton()
		{
			playPauseButton.setText(boolPlay ? "Pause" : "Play");
		}
		
		private void getUserFPS()
		{
			Integer fps = null;			
			while(true)
			{
				String input = JOptionPane.showInputDialog(timelineFrame, "Set FPS (20-60)");
				if(input == null)
					break;
				fps = getFPSFromString(input);
				if(fps != null)
					break;
				else
					JOptionPane.showMessageDialog(timelineFrame, "Invalid input");
			}
			System.out.println(fps);
		}
		
		private Integer getFPSFromString(String input)
		{
			Integer fps = null;
			
			try
			{
				fps = Integer.parseInt(input);
				if(fps < 20 || fps > 60)
					fps = null;
			}
			catch(NumberFormatException e)
			{
				//e.printStackTrace();
			}
			
			return fps;
		}
	}

	/* ---------------------------------------------------- *
	 * 				   		Keyframe						*
	 * ---------------------------------------------------- */

	private class Keyframe 
	{
		String partName;
		int frameTime;
		//Rotation for parts and position for entityPosition
		float[] values;
		//Is current keyframe, or is a selected keyframe (multiple selected).
		boolean isCurrent;
		boolean isSelected;

		public Keyframe(int frameTime, String partName, float[] values)
		{
			this.frameTime = frameTime;		
			this.partName = partName;
			this.values = values;
		}

		/**
		 * Gets the keyframe that comes before this one, for the same part, or a default keyframe at time zero if none exists. 
		 */
		private Keyframe getPreviousKeyframe()
		{
			Keyframe previousKf = null;
			Integer prevFt = null;
			for(Keyframe kf : keyframes.get(partName))
			{
				if(kf.frameTime < frameTime && (prevFt == null || kf.frameTime > prevFt))
				{
					previousKf = kf;
					prevFt = kf.frameTime;
				}
			}
			if(previousKf == null)
			{
				if(partName.equals("entitypos"))
				{
					previousKf = new Keyframe(0, partName, new float[]{0.0F, 0.0F, 0.0F});
				}
				else
				{
					Part part = Util.getPartFromName(this.partName, entityModel.parts);
					float[] defaults = part.getOriginalValues();
					previousKf = new Keyframe(0, this.partName, new float[]{0.0F, 0.0F, 0.0F});
				}
			}
			return previousKf;
		}
	}

	/* ---------------------------------------------------- *
	 * 				   		Actions							*
	 * ---------------------------------------------------- */

	private class SpaceAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			addKeyframe();
		}
	}

	private class WAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			for(int i = 0; i < parts.size(); i++)
			{
				if(parts.get(i).equals(currentPartName))
				{
					if(i > 0)
						updatePart(parts.get(i-1));
					else
						updatePart(parts.get(parts.size() - 1));
					break;
				}
			}			
		}
	}

	private class SAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			for(int i = 0; i < parts.size(); i++)
			{
				if(parts.get(i).equals(currentPartName))
				{					
					if(i < parts.size() - 1)
						updatePart(parts.get(i+1));
					else
						updatePart(parts.get(0));
					break;
				}
			}		
		}
	}

	private class AAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			exceptionPartName = "";
			time = time > 0 ? time - 1 : time;
			timelineFrame.timeSlider.setValue((int) time);
			timelineFrame.repaint();
		}
	}

	private class DAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			exceptionPartName = "";
			time = time < timelineFrame.timelineLength ? time + 1 : time;
			timelineFrame.timeSlider.setValue((int) time);
		}
	}

	private class UndoAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			undo();		
		}
	}

	private class RedoAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			redo();		
		}
	}

	private class DeleteAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			deleteKeyframe();		
		}
	}

	private class EscAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			close();		
		}
	}

	private class ChangeViewAction extends AbstractAction
	{

		private int numpadNumber;

		private ChangeViewAction(int numpadNumber)
		{
			this.numpadNumber = numpadNumber;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			changeView(numpadNumber);
		}
	}

	private class JDoubleSlider extends JSlider 
	{

		final int scale;
		private boolean shouldUpdate;

		public JDoubleSlider(int min, int max, int value, int scale, int majorSpacing) 
		{
			super(min*scale, max*scale, value*scale);
			this.scale = scale;
			this.setMajorTickSpacing(scale*majorSpacing);
			Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
			for(int i = min; i <= max; i+=majorSpacing)
			{
				labels.put(scale*i, new JLabel(Integer.toString(i)));
			}

			int width = (int) ((this.getMaximum() - this.getMinimum())*1.5F);
			this.setPreferredSize(new Dimension(width, 50));
			this.setLabelTable(labels);
			this.setPaintLabels(true);
		}

		public double getScaledValue() 
		{
			return ((double)super.getValue()) / this.scale;
		}

		public void setDoubleValue(double d)
		{
			setValue((int) Math.round(d*this.scale));
		}
	}

	private class CopyLabel extends JComponent
	{
		public int x;
		public int y;
		public int time;
		public boolean draw;

		protected void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			if(draw)
			{
				String s = String.valueOf(time);
				g.setColor(Color.red);
				g.drawString(s, x, y);
			}
		}
	}


}
