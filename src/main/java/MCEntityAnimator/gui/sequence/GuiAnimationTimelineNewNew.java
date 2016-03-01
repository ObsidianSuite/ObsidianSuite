package MCEntityAnimator.gui.sequence;

import java.util.ArrayList;

import MCEntityAnimator.Util;
import MCEntityAnimator.animation.AnimationMovement;
import MCEntityAnimator.animation.AnimationPart;
import MCEntityAnimator.animation.AnimationSequence;
import MCEntityAnimator.animation.AnimationStance;
import MCEntityAnimator.gui.sequence.GuiAnimationTimelineNew.Keyframe;
import MCEntityAnimator.render.objRendering.EntityObj;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.PartObj;
import MCEntityAnimator.render.objRendering.RenderObj;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;

public class GuiAnimationTimelineNewNew extends GuiScreen 
{

	int posX;
	int posY;

	private String entityName;
	private EntityLivingBase entityToRender;
	private ModelObj entityModel;
	private ArrayList<String> parts = new ArrayList<String>();

	private final Block blockToRender = new BlockStone();

	private AnimationSequence animation;
	private ArrayList<Keyframe> keyframes = new ArrayList<Keyframe>();

	/**
	 * Load an animation for a type of entity into the animation timeline for editing.
	 */
	public GuiAnimationTimelineNewNew(String entityName, AnimationSequence animation) 
	{
		this(entityName, animation, null);
	}

	/**
	 * Load an animation for a type of entity into the animation timeline for editing, starting from a certain stance.
	 * This should only be used if creating new animations, not loading existing ones (unless startingstance is null).
	 */
	public GuiAnimationTimelineNewNew(String entityName, AnimationSequence par1Sequence, AnimationStance startingStance)
	{
		//Init variables.
		this.mc = Minecraft.getMinecraft();
		this.entityName = entityName;
		entityToRender = new EntityObj(Minecraft.getMinecraft().theWorld, entityName);
		entityModel = ((RenderObj) RenderManager.instance.getEntityRenderObject(entityToRender)).getModel(entityName);
		blockToRender.setBlockBounds(0.0F, 1.0F, 0.0F, 4.0F, 0.9F, 4.0F);
		animation = par1Sequence;
		//Setup parts list.
		parts.add("entitypos");
		for(PartObj obj : entityModel.parts)
		{
			parts.add(obj.getName());
			obj.setToOriginalRotation();
		}
		//Create keyframes for starting stance. 
		if(startingStance != null)
		{
			for(String partName : parts)
			{
				float[] rot = startingStance.getRotation(partName);
				Keyframe kf = new Keyframe(0, partName); 
				kf.setValues(startingStance.getRotation(partName));
				this.keyframes.add(kf);	
			}
		}
		this.loadAnimations();
		guiState = 0;
	}

	/**
	 * Creates keyframes from the animation sequence. 
	 */
	public void loadAnimations()
	{
		//Part animations
		for(AnimationPart animpart : animation.getAnimations())
		{
			PartObj mr = Util.getPartFromName(animpart.getPartName(), entityModel.parts);	
			float[] defaults = animpart.getPart().getOriginalRotation();
			//If the movement starts at time zero, and the part isn't in its original position, add a keyframe at time zero.
			if(animpart.getStartTime() == 0.0F && (animpart.xBase != defaults[0] || animpart.yBase != defaults[1] || animpart.zBase != defaults[2]))
			{
				Keyframe kf = new Keyframe(0.0F, animpart.getPartName());
				kf.setValues(animpart.xBase, animpart.yBase, animpart.zBase);
				keyframes.add(kf);
			}
			//Add a keyframe for the final position 
			Keyframe kf = new Keyframe(animpart.getFinishTime(), animpart.getPartName());
			kf.setValues(animpart.getFinalRotation()[0], animpart.getFinalRotation()[1], animpart.getFinalRotation()[2]);
			keyframes.add(kf);
		}
		//Entity movement
		for(AnimationMovement animmove : animation.getMovements())
		{
			//If the movement starts at time zero, and the entity isn't in its original position, add a keyframe at time zero.
			if(animmove.getStartTime() == 0.0F && (animmove.getStartPosition()[0] != 0.0F || animmove.getStartPosition()[1] != 0.0F || animmove.getStartPosition()[2] != 0.0F))
			{
				Keyframe kf = new Keyframe(0.0F, "entitypos");
				kf.setValues(animmove.getStartPosition()[0], animmove.getStartPosition()[1], animmove.getStartPosition()[2]);
				keyframes.add(kf);
			}
			Keyframe kf = new Keyframe(animmove.getFinishTime(), "entitypos");
			kf.setValues(animmove.getEndPosition()[0], animmove.getEndPosition()[1], animmove.getEndPosition()[2]);
			keyframes.add(kf);
		}
		this.updateAnimations();
	}
	
	private void updateAnimations()
	{
		animation.clearAnimations();
		for(Keyframe kf : keyframes)
		{   
			if(kf.frameTime != 0.0F)
			{
				if(!kf.partName.equals("entitypos"))
				{
					System.out.println("Creating animation");
					PartObj part = Util.getPartFromName(kf.partName, entityModel.parts);
					Keyframe prevKf = kf.getPreviousKeyframe();
					float[] baseRotations = prevKf.rotation;
					AnimationPart partAnimation = new AnimationPart(part);
					partAnimation.update(baseRotations[0], Util.calculateSwing(kf.rotation[0] - baseRotations[0], kf.frameTime - prevKf.frameTime), 
							baseRotations[1], Util.calculateSwing(kf.rotation[1] - baseRotations[1], kf.frameTime - prevKf.frameTime), 
							baseRotations[2], Util.calculateSwing(kf.rotation[2] - baseRotations[2], kf.frameTime - prevKf.frameTime));
					partAnimation.setStartTime(prevKf.frameTime);
					partAnimation.setFinishTime(kf.frameTime);
					animation.addAnimation(partAnimation);
				}
				else
				{
					Keyframe prevKf = kf.getPreviousKeyframe();
					animation.addMovement(new AnimationMovement(prevKf.frameTime, kf.frameTime, prevKf.position, kf.position));
				}
			}
			else
			{
				if(!kf.partName.equals("entitypos"))
				{
					PartObj part = Util.getPartFromName(kf.partName, entityModel.parts);
					if(doesPartOnlyHaveOneKeyframe(part.getName()))
					{
						AnimationPart partAnimation = new AnimationPart(part);
						partAnimation.update(kf.rotation[0], 0, kf.rotation[1], 0, kf.rotation[2], 0);
						partAnimation.setStartTime(0);
						partAnimation.setFinishTime(this.getLastKeyFrameTime());
						animation.addAnimation(partAnimation);
					}
				}
				else
				{
					if(doesPartOnlyHaveOneKeyframe("entitypos"))
					{
						animation.addMovement(new AnimationMovement(0, this.getLastKeyFrameTime(), kf.position, kf.position));
					}
				}
			}
		}
	}
	
	private class Keyframe 
	{
		String partName;
		float frameTime;
		int yPos;
		//Rotation for parts and position for entityPosition
		float[] values;

		private Keyframe(float frameTime, String partName)
		{
			this.frameTime = frameTime;		
			this.partName = partName;
			values = new float[3];
			//Setup y position (ie which line it is on).
			for(int i = 0; i < parts.size(); i++)
			{
				if(parts.get(i).equals(partName))
				{
					yPos = posY + 132 + 10*i;
				}
			}
		}

		/**
		 * Return true if there is a keyframe for the same part that comes after this one.
		 */
		public boolean hasNextKeyframe() 
		{
			for(Keyframe kf : keyframes)
			{
				if(kf.partName.equals(partName) && kf.frameTime > frameTime)
				{
					return true;
				}
			}
			return false;
		}

		/**
		 * Gets the keyframe that comes before this one, for the same part, or null if none exists. TODO should it return null?
		 */
		private Keyframe getPreviousKeyframe()
		{
			Keyframe previousKf = null;
			Float prevFt;
			for(Keyframe kf : keyframes)
			{
				if(kf.partName.equals(partName) && kf.frameTime < frameTime && (kf.frameTime > prevFt || (prevFt == null && kf.frameTime == 0.0F)))
				{
					previousKf = kf;
					prevFt = kf.frameTime;
				}
			}
			//TODO should it return null?
			//			if(previousKf == null)
			//			{
			//				if(partName.equals("entitypos"))
			//				{
			//					previousKf = new Keyframe(0.0F, partName);
			//					previousKf.setEntityPosition(0.0F, 0.0F, 0.0F);
			//				}
			//				else
			//				{
			//					PartObj part = Util.getPartFromName(this.partName, entityModel.parts);
			//					float[] defaults = part.getOriginalRotation();
			//					previousKf = new Keyframe(0.0F, part.getName());
			//					previousKf.setPartRotation(defaults[0], defaults[1], defaults[2]);
			//				}
			//			}
			return previousKf;
		}

		/**
		 * Returns true if the (x,y) coordinate is within the area marked out by the keyframe.
		 */
		private boolean withinBounds(int x, int y)
		{
			int frameX = (int) (posX + 79 + frameTime);
			if(x == frameX && y >= yPos && y <= yPos + 8)
			{
				return true;
			}					
			return false;
		}

		/**
		 * Draws the keyframe. //TODO selected checks...
		 */
		private void draw()
		{
			int colour;
			if(currentKeyframe == this){colour = 0xff00ff00;}
			else if(selectedFrames.contains(this)){colour = 0xff0000ff;}
			else {colour = 0xffff0000;}				
			drawVerticalLine((int) (posX + 79 + frameTime), yPos, yPos + 8, colour);
		}
		
		private void setValues(float x, float y, float z)
		{
			values[0] = x;
			values[1] = y;
			values[2] = z;
		}
		
		private void setValues(float[] values)
		{
			this.values = values;
		}

	}
}
