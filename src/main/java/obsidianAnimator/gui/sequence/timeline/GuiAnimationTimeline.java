package obsidianAnimator.gui.sequence.timeline;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import obsidianAPI.animation.AnimationPart;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.render.part.Part;
import obsidianAnimator.Util;
import obsidianAnimator.file.FileHandler;
import obsidianAnimator.gui.GuiBlack;
import obsidianAnimator.gui.frames.HomeFrame;
import obsidianAnimator.gui.sequence.EntityAutoMove;
import obsidianAnimator.gui.sequence.ExternalFrame;
import obsidianAnimator.gui.sequence.GuiEntityRendererWithTranslation;

public class GuiAnimationTimeline extends GuiEntityRendererWithTranslation implements ExternalFrame
{

	public AnimationSequence currentAnimation;
	private int animationVersion;
	private List<AnimationSequence> animationVersions;

	private DecimalFormat df = new DecimalFormat("#.##");
	float time = 0.0F;
	float timeMultiplier = 1.0F;
	TimelineFrame timelineFrame;
	protected Map<Part, List<Keyframe>> keyframes = new HashMap<Part, List<Keyframe>>();

	private Part exceptionPart = null;

	boolean boolPlay;	
	boolean boolLoop;
	boolean boolMovementActive;
	
	//Nano time at which the animation started playing (play button pressed).
	long playStartTimeNano;
	//Frame time at which the animation started playing (play button pressed).
	float playStartTimeFrame;

	EntityAutoMove entityMovement;
	
	private File animationFile;

	private KeyMappings keyMappings;

	public GuiAnimationTimeline(File animationFile, AnimationSequence animation)
	{
		super(animation.getEntityName());

		this.currentAnimation = animation;
		this.animationFile = animationFile;
		boolPlay = false;
		boolMovementActive = false;
		
		loadKeyframes();
		loadFrames();

		animationVersion = 0;
		animationVersions = new ArrayList<AnimationSequence>();
		updateAnimationParts();

		//Animation Item
//		((EntityObj) entityToRender).setCurrentItem(AnimationData.getAnimationItem(animation.getName()));   	

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
        keyMappings = new KeyMappings(timelineFrame);

        keyMappings.addKey(KeyEvent.VK_SPACE, Keyboard.KEY_SPACE, "spacePressed", new SpaceAction());
        keyMappings.addKey(KeyEvent.VK_W, Keyboard.KEY_W, "wPressed", new WAction());
        keyMappings.addKey(KeyEvent.VK_S, Keyboard.KEY_S, "sPressed", new SAction());
        keyMappings.addKey(KeyEvent.VK_A, Keyboard.KEY_A, "aPressed", new AAction());
        keyMappings.addKey(KeyEvent.VK_D, Keyboard.KEY_D,"dPressed", new DAction());
        keyMappings.addCtrlKey(KeyEvent.VK_Z,Keyboard.KEY_Z, "undoReleased", new UndoAction());
        keyMappings.addCtrlKey(KeyEvent.VK_Y, Keyboard.KEY_Y,"redoReleased", new RedoAction());
        keyMappings.addKey(KeyEvent.VK_ESCAPE,Keyboard.KEY_ESCAPE, "escPressed", new EscAction());
		keyMappings.addKey(KeyEvent.VK_DELETE, Keyboard.KEY_DELETE, "escPressed", new DeleteAction());

        int[] numpadKey = new int[] {
        		Keyboard.KEY_NUMPAD0, Keyboard.KEY_NUMPAD1, Keyboard.KEY_NUMPAD2, Keyboard.KEY_NUMPAD3,
				Keyboard.KEY_NUMPAD4, Keyboard.KEY_NUMPAD5, Keyboard.KEY_NUMPAD6, Keyboard.KEY_NUMPAD7,
				Keyboard.KEY_NUMPAD8, Keyboard.KEY_NUMPAD9};

		for (int j = 0; j <= 9; j++)
        {
			keyMappings.addKey(KeyEvent.VK_NUMPAD0 + j, numpadKey[j], "numpad" + j, new ChangeViewAction(j));
        }

        timelineFrame.refresthLineColours();
    }

	/**
	 * Creates keyframes from the animation sequence. 
	 */
	public void loadKeyframes()
	{	
		keyframes.clear();
		for(AnimationPart animpart : currentAnimation.getAnimationList())
		{
			Part mr = Util.getPartFromName(animpart.getPartName(), entityModel.parts);

			String partName = animpart.getPartName();
			List<Keyframe> partKfs = keyframes.get(mr);
			if(keyframes.get(mr) == null)
				partKfs = new ArrayList<Keyframe>();			

			float[] defaults = mr.getOriginalValues();
			//If the movement starts at time zero, and the part isn't in its original position, add a keyframe at time zero.
			if(animpart.getStartTime() == 0.0F)
			{
				if(!animpart.isStartPos(defaults))
				{
					Keyframe kf = new Keyframe(0, mr, animpart.getStartPosition());
					partKfs.add(kf);
				}
				if(animpart.isEndPosDifferentToStartPos() || currentAnimation.multiPartSequence(partName))
				{
					Keyframe kf2 = new Keyframe((int) animpart.getEndTime(), mr, animpart.getEndPosition());
					partKfs.add(kf2);
				}
			}
			else
			{
				Keyframe kf = new Keyframe((int) animpart.getEndTime(), mr, animpart.getEndPosition());
				partKfs.add(kf);
			}
			keyframes.put(mr, partKfs);
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
		FileHandler.saveAnimationSequence(animationFile, currentAnimation);
	}

	public void drawScreen(int par1, int par2, float par3)
	{				
		if(boolPlay)
		{
			time = Util.getAnimationFrameTime(playStartTimeNano, playStartTimeFrame, currentAnimation.getFPS(), timeMultiplier);
			exceptionPart = null;
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

		if(entityMovement != null && boolMovementActive)
			entityMovement.moveEntity(time, entityToRender);
		this.currentAnimation.animateAll(time, entityModel, exceptionPart != null ? exceptionPart.getName() : "");

		updateExternalFrameFromDisplay();
		timelineFrame.controlPanel.updatePlayPauseButton();
		timelineFrame.controlPanel.partPanel.updatePartLabels();

		super.drawScreen(par1, par2, par3);
	}

	/* ---------------------------------------------------- *
	 * 				   Keyframe manipulation				*
	 * ---------------------------------------------------- */
	private Keyframe getKeyframe(Part part, int frameTime)
	{
		List<Keyframe> keyframes = this.keyframes.get(part);
		if (keyframes != null)
		{
			for (Keyframe keyframe : keyframes)
			{
				if (keyframe.frameTime == frameTime)
					return keyframe;
			}
		}

		return null;
	}

	private void addKeyframe()
	{
		if(selectedPart != null)
		{
			Part part = selectedPart;
			Keyframe kf = new Keyframe((int) time, selectedPart, part.getValues());
			addKeyframe(kf);
		}
	}

	private void addKeyframe(Keyframe kf)
	{
		List<Keyframe> partKeyframes = keyframes.get(kf.part);

		if (partKeyframes == null)
		{
			partKeyframes = new ArrayList<Keyframe>();
			keyframes.put(kf.part, partKeyframes);
		} else
		{
			deleteKeyframe(kf.part, kf.frameTime);
		}

		partKeyframes.add(kf);
		timelineFrame.refresthLineColours();
		updateAnimationParts();
	}

	private void deleteKeyframe()
	{
		boolean removed = deleteKeyframe(selectedPart, (int) time);

		timelineFrame.repaint();

		if (removed)
		{
			exceptionPart = null;
			updateAnimationParts();
		}

		timelineFrame.refresh();
	}

	private boolean deleteKeyframe(Part part, int time)
	{
		Keyframe toRemove = getKeyframe(part, time);
		if (toRemove != null)
		{
			keyframes.get(part).remove(toRemove);
			return true;
		}

		return false;
	}

	private void copyKeyframe(Keyframe kf, Part part, int time)
	{
		String partName = kf.part.getName();

		if((partName.equals("entitypos") || partName.equals("prop_rot") || partName.equals("prop_trans")) && !partName.equals(part.getName()))
			JOptionPane.showMessageDialog(timelineFrame, partName + " can only copy to itself.");
		else
			addKeyframe(new Keyframe(time, part, kf.values.clone()));
	}

	private boolean keyframeExists()
	{
		return getExistingKeyframe() != null;
	}

	private Keyframe getExistingKeyframe()
	{
		List<Keyframe> partKeyframes = keyframes.get(selectedPart);
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

	private int getLastKeyFrameTime()
	{
		int lastFrameTime = 0;
		for(Part part : parts)
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

	private boolean doesPartOnlyHaveOneKeyframe(Part part)
	{
		List<Keyframe> kfs = keyframes.get(part);
		return (kfs != null && kfs.size() == 1);
	}

	/* ---------------------------------------------------- *
	 * 				   Animation manipulation				*
	 * ---------------------------------------------------- */


	private void updateAnimationParts()
	{
		//Create new animation object if new version
		AnimationSequence sequence = new AnimationSequence(entityName, currentAnimation.getName());
		//Generate animation from keyframes.
		for(Part part : keyframes.keySet())
		{
			for(Keyframe kf : keyframes.get(part))
			{
				if(kf.frameTime != 0.0F)
				{
					Keyframe prevKf = getPreviousKeyframe(kf);
					sequence.addAnimation(new AnimationPart(prevKf.frameTime, kf.frameTime, prevKf.values, kf.values, part));
				}
				else if(doesPartOnlyHaveOneKeyframe(part))
				{
					//Used for parts that only have one keyframe and where that keyframe is at the beginning 
					//The part will maintain that rotation throughout the whole animation.
					sequence.addAnimation(new AnimationPart(0, getLastKeyFrameTime(), kf.values, kf.values, part));
				}
			}
		}
		sequence.setFPS(currentAnimation.getFPS());
		updateAnimation(sequence);
	}

	/**
	 * Gets the keyframe that comes before this one, for the same part, or a default keyframe at time zero if none exists.
	 */
	private Keyframe getPreviousKeyframe(Keyframe keyframe)
	{
		Part part = keyframe.part;
		int frameTime = keyframe.frameTime;

		Keyframe previousKf = null;
		Integer prevFt = null;
		for(Keyframe kf : keyframes.get(part))
		{
			if(kf.frameTime < frameTime && (prevFt == null || kf.frameTime > prevFt))
			{
				previousKf = kf;
				prevFt = kf.frameTime;
			}
		}
		if(previousKf == null)
		{
			if(part.getName().equals("entitypos"))
			{
				previousKf = new Keyframe(0, part, new float[]{0.0F, 0.0F, 0.0F});
			}
			else
			{
				float[] defaults = part.getOriginalValues();
				previousKf = new Keyframe(0, part, new float[]{0.0F, 0.0F, 0.0F});
			}
		}
		return previousKf;
	}

	private void updateAnimationFPS(int fps)
	{
		AnimationSequence sequence = new AnimationSequence(entityName, currentAnimation.getName());
		sequence.setAnimations(currentAnimation.getAnimationList());
		sequence.setFPS(fps);
		updateAnimation(sequence);
	}


	private void updateAnimation(AnimationSequence sequence)
	{
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

		onAnimationLengthChange();
	}

	/* ---------------------------------------------------- *
	 * 				  	Part manipulation					*
	 * ---------------------------------------------------- */

	@Override
	protected void updatePart(Part newPartName)
	{
		super.updatePart(newPartName);
		exceptionPart = newPartName;
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
			loadKeyframes();
			timelineFrame.refresh();
			onFPSChange(currentAnimation.getFPS());
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
			loadKeyframes();
			timelineFrame.refresh();
			onFPSChange(currentAnimation.getFPS());
		}
		else
			Toolkit.getDefaultToolkit().beep();
	}

	/* ---------------------------------------------------- *
	 * 					  Ray Trace							*
	 * ---------------------------------------------------- */

	@Override
	public void processRay()
	{
		GL11.glPushMatrix();
		if(entityMovement != null && boolMovementActive)
			entityMovement.matrixTranslate(time);
		super.processRay();
		GL11.glPopMatrix();
	}

	/* ---------------------------------------------------- *
	 * 				   		Control							*
	 * ---------------------------------------------------- */

	@Override
	protected void keyTyped(char par1, int par2)
	{
		keyMappings.handleMinecraftKey(par2);

		if(par2 != Keyboard.KEY_ESCAPE)
			super.keyTyped(par1, par2);
	}

	@Override
	protected void onControllerDrag()
	{
		super.onControllerDrag();
		exceptionPart = selectedPart;
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

	void close()
	{
		mc.displayGuiScreen(new GuiBlack());
		new HomeFrame().display();
	}

	void onFPSChange(int fps)
	{
		timelineFrame.controlPanel.animationPanel.fpsLabel.setText(fps + " FPS");
		timelineFrame.controlPanel.movementPanel.updateEntityMovement(fps);
		updateAnimationFPS(fps);
	}

	public void onAnimationLengthChange()
	{
		timelineFrame.controlPanel.animationPanel.lengthFrameLabel.setText((int)currentAnimation.getTotalTime() + " frames");
		timelineFrame.controlPanel.animationPanel.lengthSecondsLabel.setText(df.format(currentAnimation.getTotalTime()/(float)currentAnimation.getFPS()) + " seconds");
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
		ControlPanel controlPanel;
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
			controlPanel = new ControlPanel(GuiAnimationTimeline.this);

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
			timeSlider.addMouseListener(new BlankMouseListener()
			{
				@Override
				public void mousePressed(MouseEvent arg0) 
				{
					exceptionPart = null;
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
				c.gridx = 0;
				c.gridy = i+1;
				c.weightx = 0;
				c.weighty = 0;
				c.insets = new Insets(0, 0, 0, 0);
				c.fill = GridBagConstraints.HORIZONTAL;

				JLabel partLabel = new JLabel(parts.get(i).getDisplayName());
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

			mainPanel.add(controlPanel);
			mainPanel.add(scrollPane);

			setContentPane(mainPanel);
			pack();
			setAlwaysOnTop(true);
			if(Display.isVisible())
				setLocation(Display.getX() + 50, Display.getY() + 500);
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
			controlPanel.updatePlayPauseButton();
			controlPanel.partPanel.updatePartLabels();
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
				if(selectedPart != null && partLabels[i].getText().equals(selectedPart.getDisplayName()))
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
						Keyframe kf = getExistingKeyframe();
						if(kf != null && e.isControlDown())
							copyKeyframe(kf, part, xToKeyframeTime(e.getX()));
						else if(closestKeyframe != null)
						{
							time = closestKeyframe.frameTime;
							timelineFrame.timeSlider.setValue((int) time);
							currentAnimation.animateAll(time, entityModel);
							updatePart(part);
						}
					}


					@Override
					public void mouseEntered(MouseEvent e) 
					{
						mouseWithin = true;
						hoveredPart = part;
					}

					@Override
					public void mouseExited(MouseEvent e) 
					{
						mouseWithin = false; 
						repaint();
						hoveredPart = null;
					}

					@Override
					public void mouseReleased(MouseEvent e) 
					{
						if(keyframeTimeChanged)
							updateAnimationParts();
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
				if(keyframes.get(part) != null)
				{
					for(Keyframe kf : keyframes.get(part))
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
				if(keyframes.get(part) != null)
				{
					for(Keyframe kf : keyframes.get(part))
					{
						if(selectedPart != null && selectedPart.equals(part) && time == kf.frameTime)
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
	 * 				   		Keyframe						*
	 * ---------------------------------------------------- */

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
				if(parts.get(i).equals(selectedPart))
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
				if(parts.get(i).equals(selectedPart))
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
			exceptionPart = null;
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
			exceptionPart = null;
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
}
