package MCEntityAnimator.gui.sequence;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
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
import java.io.IOException;
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
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import org.lwjgl.input.Keyboard;

import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationData;
import MCEntityAnimator.animation.AnimationPart;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.distribution.SaveLoadHandler;
import MCEntityAnimator.distribution.ServerAccess;
import MCEntityAnimator.gui.GuiBlack;
import MCEntityAnimator.gui.GuiEntityRenderer;
import MCEntityAnimator.gui.GuiInventoryChooseItem;
import MCEntityAnimator.gui.animation.FileGUI;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.parts.Part;
import MCEntityAnimator.render.objRendering.parts.PartObj;
import net.minecraft.item.Item;

public class GuiAnimationTimelineWithFrames extends GuiEntityRenderer
{

	public AnimationSequence currentAnimation;
	private int animationVersion;
	private List<AnimationSequence> animationVersions;

	private DecimalFormat df = new DecimalFormat("#.##");
	private float time = 0.0F;
	private float timeIncrement = 1.0F;
	private ControllerFrame controllerFrame;
	private TimelineFrame timelineFrame;
	private SettingsFrame settingsFrame;
	protected Map<String, List<Keyframe>> keyframes = new HashMap<String, List<Keyframe>>();

	private String exceptionPartName = "";

	private boolean boolPlay;	
	private boolean boolLoop;


	public GuiAnimationTimelineWithFrames(String entityName, AnimationSequence animation)
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


	@Override
	public void initGui()
	{
		super.initGui();
		setup();
	}

	public void setup()
	{
		String setup = AnimationData.getAnimationSetup(entityName);
		if(setup != null)
		{
			String[] split = setup.split(",");
			horizontalPan = Integer.parseInt(split[0]);
			verticalPan = Integer.parseInt(split[1]);
			horizontalRotation = Float.parseFloat(split[2]);
			verticalRotation = Float.parseFloat(split[3]);
			scaleModifier = Integer.parseInt(split[4]);
			boolBase = Boolean.parseBoolean(split[5]);
		}
	}

	public void saveSetup()
	{
		String data = horizontalPan + "," + verticalPan + "," + horizontalRotation + "," 
				+ verticalRotation + "," + scaleModifier + "," + boolBase;
		AnimationData.setAnimationSetup(entityName, data);
	}

	public void loadFrames()
	{
		controllerFrame = new ControllerFrame();
		settingsFrame = new SettingsFrame();
		timelineFrame = new TimelineFrame();

		for(int i = 0; i < 3; i++)
		{
			JFrame frame = null;
			switch(i)
			{
			case 0: frame = controllerFrame; break;
			case 1: frame = settingsFrame; break;
			case 2: frame = timelineFrame; break;
			}
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

			for(int j = 0; j <= 9; j++)
			{
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + j, 0), "numpad" + j);
				actionMap.put("numpad" + j, new ChangeViewAction(j));
			}
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

	@Override
	public void onGuiClosed()
	{

		saveSetup();
		controllerFrame.dispose();
		settingsFrame.dispose();
		timelineFrame.dispose();
		SaveLoadHandler.upload();
	}

	public void drawScreen(int par1, int par2, float par3)
	{		
		if(boolPlay)
		{
			time += timeIncrement;
			exceptionPartName = "";
			if(time >= currentAnimation.getTotalTime())
			{
				if(boolLoop)
					time = 0.0F;
				else
				{
					boolPlay = false;
					controllerFrame.updatePlayPauseButton();
					time = currentAnimation.getTotalTime();
				}
			}
			timelineFrame.timeSlider.setValue((int) time);
			timelineFrame.repaint();
			settingsFrame.repaint();
		}

		this.currentAnimation.animateAll(time, entityModel, exceptionPartName);

		super.drawScreen(par1, par2, par3);
	}

	private void updatePartValues(double value, int dim)
	{
		Part part = Util.getPartFromName(currentPartName, entityModel.parts);
		//Negative for some reason - makes more sense when rotating..
		if(part instanceof PartObj)
			value = (float) (-value*Math.PI);
		part.setValue(dim, (float) value);
	}

	private void addKeyframe()
	{
		Part part = Util.getPartFromName(currentPartName, entityModel.parts);
		Keyframe kf = new Keyframe((int) time, currentPartName, part.getValues());
		addKeyframe(kf);
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
		settingsFrame.refreshButtons();
		timelineFrame.refresthLineColours();
		if(!keyframeExists)
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
				updateAnimation();
		}
		refreshFrames();
	}

	/**
	 * Create a new keyframe based off another keyframe.
	 * @param kf - Keyframe to copy.
	 * @param partName - Name of part to be copied to.
	 */
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
	}

	private void undo()
	{
		if(animationVersion > 0)
		{
			animationVersion --;
			currentAnimation = animationVersions.get(animationVersion);
			AnimationData.addSequence(entityName, currentAnimation);
			loadKeyframes();
			refreshFrames();
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
			refreshFrames();
		}
		else
			Toolkit.getDefaultToolkit().beep();
	}

	private void refreshFrames()
	{		
		settingsFrame.updateRotationSliderValues();
		settingsFrame.refreshButtons();
		timelineFrame.refresthLineColours();
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
		}


		super.keyTyped(par1, par2);
	}

	private void updatePart(String newPartName)
	{
		currentPartName = newPartName;
		controllerFrame.updatePartDropDown();
		controllerFrame.repaint();
		Part part = Util.getPartFromName(currentPartName, entityModel.parts);
		settingsFrame.updateRotationSliderValues();
		timelineFrame.refresthLineColours();
		settingsFrame.refreshButtons();
	}

	private class ControllerFrame extends JFrame
	{
		JButton playPauseButton;
		JComboBox<String> partDropDown;
		JPanel mainPanel;

		private ControllerFrame()
		{
			super("Controls");

			mainPanel = new JPanel();

			playPauseButton = new JButton("Play");
			playPauseButton.setPreferredSize(new Dimension(100,50));
			playPauseButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					if(time >= currentAnimation.getTotalTime())
						time = 0;
					boolPlay = !boolPlay; 		
					updatePlayPauseButton();
				}
			});

			final SliderPanel animationSpeedSliderPanel = new SliderPanel("Animation Speed", 0, 3, 1, 0.01F, 1); 
			animationSpeedSliderPanel.slider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					timeIncrement = (float) animationSpeedSliderPanel.slider.getScaledValue();
				}
			});

			partDropDown = new JComboBox<String>();
			for(String s : parts)
			{
				partDropDown.addItem(s);
			}
			partDropDown.setRenderer(new BasicComboBoxRenderer()
			{
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
				{
					if (isSelected) 
					{
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
						if (index > -1) 
							additionalHighlightPartName = parts.get(index);
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
			});

			partDropDown.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					updatePart((String) ((JComboBox<String>) e.getSource()).getSelectedItem());
				}
			});

			mainPanel.add(playPauseButton);
			mainPanel.add(animationSpeedSliderPanel);
			mainPanel.add(partDropDown);

			setContentPane(mainPanel);
			pack();
			setAlwaysOnTop(true);
			setLocation(50, 50);
			setVisible(true);
		}

		private void updatePlayPauseButton()
		{
			playPauseButton.setText(boolPlay ? "Pause" : "Play");
			pack();
		}

		private void updatePartDropDown()
		{
			for(int i = 0; i < partDropDown.getItemCount(); i++)
			{
				if(partDropDown.getItemAt(i).equals(currentPartName))
				{
					partDropDown.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	private class SettingsFrame extends JFrame
	{
		SliderPanel xRotPanel, yRotPanel, zRotPanel;
		JTabbedPane tabbedPane;
		JButton addKeyframeButton, deleteKeyframeButton;

		private SettingsFrame()
		{
			super("Settings");

			JPanel generalPanel = new JPanel();
			generalPanel.setLayout(new GridLayout(5,1));

			JButton choosePropButton = new JButton("Choose Right Hand Item");
			choosePropButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					mc.displayGuiScreen(new GuiInventoryChooseItem(GuiAnimationTimelineWithFrames.this, (EntityObj) entityToRender));
				}
			});
			
			JButton emptyHandButton = new JButton("Empty Right Hand Item");
			emptyHandButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
			    	AnimationData.setAnimationItem(currentAnimation.getName(), -1);
					((EntityObj) entityToRender).setCurrentItem(null); 
				}
			});

			JButton setActionPointButton = new JButton("Set Action Point");
			setActionPointButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					//TODO action point
				}
			});
			setActionPointButton.setEnabled(false);

			JButton deleteButton = new JButton("Delete Animation");
			deleteButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					AnimationData.deleteSequence(entityName, currentAnimation);
					mc.displayGuiScreen(new GuiBlack());
					ServerAccess.gui = new FileGUI();

				}
			});
			deleteButton.setEnabled(false);

			JButton backButton = new JButton("Back");
			backButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					mc.displayGuiScreen(new GuiBlack());
					ServerAccess.gui = new FileGUI();
				}
			});

			generalPanel.add(choosePropButton);
			generalPanel.add(emptyHandButton);
			generalPanel.add(setActionPointButton);
			generalPanel.add(deleteButton);
			generalPanel.add(backButton);

			JPanel rotationPanel = new JPanel();
			rotationPanel.setLayout(new GridBagLayout());
			xRotPanel = new SliderPanel("X Rotation", -1, 1, 0, 0.01F, 1);
			RotationSliderListener xRotListener = new RotationSliderListener(0);
			xRotPanel.slider.addChangeListener(xRotListener);
			xRotPanel.slider.addMouseListener(xRotListener);

			yRotPanel = new SliderPanel("Y Rotation", -1, 1, 0, 0.01F, 1);
			RotationSliderListener yRotListener = new RotationSliderListener(1);
			yRotPanel.slider.addChangeListener(yRotListener);
			yRotPanel.slider.addMouseListener(yRotListener);

			zRotPanel = new SliderPanel("Z Rotation", -1, 1, 0, 0.01F, 1);
			RotationSliderListener zRotListener = new RotationSliderListener(2);
			zRotPanel.slider.addChangeListener(zRotListener);
			zRotPanel.slider.addMouseListener(zRotListener);

			addKeyframeButton = new JButton("Add keyframe");
			addKeyframeButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					addKeyframe();
					refreshButtons();
				}
			});

			deleteKeyframeButton = new JButton("Delete keyframe");
			deleteKeyframeButton.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					deleteKeyframe();
				}
			});



			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 1;			
			c.gridy = 0;
			c.weightx = 1;
			c.insets = new Insets(0,0,10,0);

			c.gridx = 0;
			c.gridy = 1;
			c.gridwidth = 2;	
			c.insets = new Insets(0,0,0,0);
			rotationPanel.add(xRotPanel, c);

			c.gridy = 2;
			rotationPanel.add(yRotPanel, c);

			c.gridy = 3;
			c.insets = new Insets(0,0,10,0);
			rotationPanel.add(zRotPanel, c);


			c.gridy = 4;
			c.gridwidth = 1;
			c.insets = new Insets(0,0,0,0);
			rotationPanel.add(addKeyframeButton, c);

			c.gridx = 1;
			rotationPanel.add(deleteKeyframeButton, c);

			JPanel renderPanel = new JPanel();
			renderPanel.setLayout(new GridLayout(4,2));
			for(int i = 0; i < 4; i++)
			{
				JCheckBox cb = new JCheckBox();
				cb.setHorizontalAlignment(JCheckBox.RIGHT);
				renderPanel.add(cb);
				String s = "";
				switch(i)
				{
				case 0: 
					s = "Loop"; 
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
				renderPanel.add(new JLabel(s));
			}

			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("General", generalPanel);
			tabbedPane.addTab("Rotation", rotationPanel);
			tabbedPane.addTab("Render", renderPanel);

			setContentPane(tabbedPane);
			pack();
			setAlwaysOnTop(true);
			setLocation(50, 200);
			setVisible(true);
		}

		private void updateRotationSliderValues()
		{
			currentAnimation.animateAll(time, entityModel);
			Part part = Util.getPartFromName(currentPartName, entityModel.parts);
			double x = part.getValue(0);
			double y = part.getValue(1);
			double z = part.getValue(2);
			if(part instanceof PartObj)
			{
				x = -x/Math.PI;
				y = -y/Math.PI;
				z = -z/Math.PI;
			}
			xRotPanel.slider.setDoubleValue(x);
			yRotPanel.slider.setDoubleValue(y);
			zRotPanel.slider.setDoubleValue(z);
		}

		private Double[] getSliderValues()
		{
			Double[] sliderVals = new Double[3];
			sliderVals[0] = xRotPanel.slider.getScaledValue();
			sliderVals[1] = yRotPanel.slider.getScaledValue();
			sliderVals[2] = zRotPanel.slider.getScaledValue();
			return getSliderValues();
		}

		private void refreshButtons()
		{
			addKeyframeButton.setEnabled(true);
			deleteKeyframeButton.setEnabled(true);
			if(keyframeExists())
				addKeyframeButton.setEnabled(false);
			else
				deleteKeyframeButton.setEnabled(false);	
		}

		private class RotationSliderListener implements ChangeListener, MouseListener
		{

			private int dim;

			private RotationSliderListener(int dim)
			{
				this.dim = dim;
			}

			@Override
			public void mousePressed(MouseEvent arg0) 
			{
				exceptionPartName = currentPartName;
				settingsFrame.xRotPanel.slider.shouldUpdate = true;
				settingsFrame.yRotPanel.slider.shouldUpdate = true;
				settingsFrame.zRotPanel.slider.shouldUpdate = true;
			}

			@Override
			public void stateChanged(ChangeEvent e) 
			{
				if(((JDoubleSlider) e.getSource()).shouldUpdate)
				{
					updatePartValues(((JDoubleSlider) e.getSource()).getScaledValue(), dim);
					if(keyframeExists() && !boolPlay)
					{
						Part part = Util.getPartFromName(currentPartName, entityModel.parts);
						getExistingKeyframe().values = part.getValues();
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) 
			{
				settingsFrame.xRotPanel.slider.shouldUpdate = false;
				settingsFrame.yRotPanel.slider.shouldUpdate = false;
				settingsFrame.zRotPanel.slider.shouldUpdate = false;
				if(keyframeExists())
					updateAnimation();	
			}			

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

		}
	}

	private class TimelineFrame extends JFrame
	{
		KeyframeLine[] lines;
		JSlider timeSlider;
		int timelineLength = 100;
		int timelineLengthMax = 300;
		int timelineLengthMin = 50;
		JPanel mainPanel;
		JLabel[] partLabels;

		private TimelineFrame()
		{
			super("Timeline");

			final KeyframeLine[] lines = new KeyframeLine[parts.size()];
			for(int i = 0; i < parts.size(); i++)
			{
				lines[i] = new KeyframeLine(parts.get(i));
			}

			mainPanel = new JPanel();
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
					settingsFrame.updateRotationSliderValues();
					settingsFrame.refreshButtons();
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
					settingsFrame.refreshButtons();
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

			mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;

			c.gridx = 0;
			c.gridy = 0;
			c.fill = GridBagConstraints.HORIZONTAL;
			mainPanel.add(timeTextField, c);

			c.fill = GridBagConstraints.BOTH;
			c.gridx = 1;
			c.weighty = 1;
			mainPanel.add(timeSlider, c);

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
				//TODO keyframe line part selection boxes
				//				JComboBox<String> partDropDown = new JComboBox<String>();
				//				for(String partName : parts)
				//				{
				//					partDropDown.addItem(partName);
				//				}
				//				partDropDown.setSelectedIndex(i);
				//				mainPanel.add(partDropDown, c);

				JLabel partLabel = new JLabel(Util.getDisplayName(parts.get(i), entityModel.parts));
				partLabels[i] = partLabel;
				mainPanel.add(partLabel, c);

				c.gridx = 1;
				c.weightx = 1;
				c.weighty = 1;
				c.insets = new Insets(0, 10, 0, 0);
				c.fill = GridBagConstraints.BOTH;
				mainPanel.add(lines[i], c);
			}

			setContentPane(mainPanel);
			pack();
			setAlwaysOnTop(true);
			setLocation(50, 600);
			setVisible(true);
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
			settingsFrame.refreshButtons();
		}

		private void refresthLineColours()
		{
			for(int i = 0; i < partLabels.length; i++)
			{
				if(partLabels[i].getText().equals(Util.getDisplayName(currentPartName, entityModel.parts)))
					partLabels[i].setForeground(Color.red);
				else
					partLabels[i].setForeground(Color.black);
			}
			repaint();
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
							settingsFrame.tabbedPane.setSelectedIndex(1);
							settingsFrame.updateRotationSliderValues();
							settingsFrame.refreshButtons();
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

	private class SpaceAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			addKeyframe();
			settingsFrame.refreshButtons();
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
			settingsFrame.repaint();
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

	private class SliderPanel extends JPanel
	{	

		private JDoubleSlider slider;

		private SliderPanel(String name, int min, int max, int value, float delta, int majorSpacing)
		{
			JLabel label = new JLabel(name);

			final JTextField textField = new JTextField(3);
			textField.setText(Integer.toString(value));
			slider = new JDoubleSlider(min, max, value, (int) (1/delta), majorSpacing);
			slider.addChangeListener(new ChangeListener()
			{
				@Override
				public void stateChanged(ChangeEvent e) 
				{
					textField.setText(df.format(slider.getScaledValue()));
				}
			});
			textField.addKeyListener(new KeyAdapter()
			{
				@Override
				public void keyReleased(KeyEvent ke) 
				{
					String typed = textField.getText();
					slider.setValue(0);
					Double d;
					try
					{
						d = Double.parseDouble(typed);
					}
					catch(NumberFormatException e)
					{
						return;
					}
					double value = d*slider.scale;
					boolean prev = slider.shouldUpdate;
					slider.shouldUpdate = true;
					slider.setValue((int)value);
				}
			});

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			add(label, c);

			c.weightx = 0;
			c.gridy = 1;
			c.gridwidth = 2;
			add(slider, c);

			c.gridx = 1;
			c.gridy = 0;
			c.gridwidth = 1;
			c.insets = new Insets(0, 0, 0, 20);
			add(textField, c);
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

			this.setLabelTable(labels);
			this.setPaintLabels(true);
		}

		public double getScaledValue() 
		{
			return ((double)super.getValue()) / this.scale;
		}

		public void setDoubleValue(double d)
		{
			setValue((int) (d*this.scale));
		}
	}
}
