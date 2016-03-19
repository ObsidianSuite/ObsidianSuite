package MCEntityAnimator.animation;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import MCEntityAnimator.Util;
import MCEntityAnimator.gui.sequence.timeline.GuiAnimationTimeline;
import MCEntityAnimator.gui.sequence.timeline.GuiAnimationTimeline.Keyframe;
import MCEntityAnimator.render.objRendering.ModelObj;
import MCEntityAnimator.render.objRendering.PartObj;

public class AnimationSequence 
{

	private String animationName;
	ArrayList<AnimationPart> animations = new ArrayList<AnimationPart>();
	ArrayList<AnimationMovement> movements = new ArrayList<AnimationMovement>();
	private float actionPoint = 0.0F;

	public AnimationSequence(String par0Str) 
	{
		this.animationName = par0Str;
	}

	public String getName() 
	{
		return animationName;
	}

	public ArrayList<AnimationPart> getAnimations() 
	{
		return animations;
	}

	public void addAnimation(AnimationPart par0Animation)
	{
		animations.add(par0Animation);
	}

	public ArrayList<AnimationMovement> getMovements() 
	{
		return movements;
	}

	public void addMovement(AnimationMovement par0Movement)
	{
		movements.add(par0Movement);
	}

	/**
	 * Clear all the animation parts and movements.
	 */
	public void clearAnimations() 
	{
		animations.clear();
		movements.clear();
	}

	public void setActionPoint(float time)
	{
		this.actionPoint = time;
	}

	public float getActionPoint()
	{
		return actionPoint;
	}

	public void animateAll(float time, float speedMultiplier, boolean inGame, Entity entity, String notAnimated) 
	{
		if(inGame)
		{
			time = time*speedMultiplier;
			if(time > this.getTotalTime())
			{
				int i = (int) (time/this.getTotalTime());
				time = time - this.getTotalTime()*i;
			}
		}
		for(AnimationPart s : this.animations)
		{
			if(!notAnimated.equals(s.getPart().getName()) && time >= s.getStartTime() && time <= s.getEndTime())
			{
				s.animatePart(time - s.getStartTime());
			}
		}
		if(!notAnimated.equals("entitypos"))
		{
			for(AnimationMovement m : this.movements)
			{
				if(time >= m.getStartTime() && time <= m.getFinishTime())
				{
					m.moveEntity(time - m.getStartTime(), entity);
				}
			}
		}

	}

	public float getTotalTime() 
	{
		float max = 0.0F;
		for(AnimationPart animation : animations)
		{
			if(animation.getEndTime() > max)
			{
				max = animation.getEndTime();
			}
		}
		for(AnimationMovement movement : movements)
		{
			if(movement.getFinishTime() > max)
			{
				max = movement.getFinishTime();
			}
		}
		return max;
	}	

	/**
	 * Creates keyframes from the animation sequence. 
	 */
	public List<Keyframe> getKeyframes(GuiAnimationTimeline gui, ModelObj entityModel)
	{
		List<Keyframe> keyframes = new ArrayList<Keyframe>();
		//Part animations
		for(AnimationPart animpart : getAnimations())
		{
			String partName = animpart.getPart().getName();
			PartObj mr = Util.getPartFromName(animpart.getPart().getName(), entityModel.parts);	
			float[] defaults = animpart.getPart().getOriginalRotation();
			//If the movement starts at time zero, and the part isn't in its original position, add a keyframe at time zero.
			if(animpart.getStartTime() == 0.0F && !animpart.compareRotation(defaults))
			{
				Keyframe kf = gui.new Keyframe(0.0F, partName, animpart.getStartPosition());
				keyframes.add(kf);
			}
			//Add a keyframe for the final position 
			Keyframe kf = gui.new Keyframe(animpart.getEndTime(), partName, animpart.getEndPosition());
			keyframes.add(kf);
		}
		//Entity movement
		for(AnimationMovement animmove : getMovements())
		{
			//If the movement starts at time zero, and the entity isn't in its original position, add a keyframe at time zero.
			if(animmove.getStartTime() == 0.0F && (animmove.getStartPosition()[0] != 0.0F || animmove.getStartPosition()[1] != 0.0F || animmove.getStartPosition()[2] != 0.0F))
			{
				Keyframe kf = gui.new Keyframe(0.0F, "entitypos", animmove.getStartPosition());
				keyframes.add(kf);
			}
			Keyframe kf = gui.new Keyframe(animmove.getFinishTime(), "entitypos", animmove.getEndPosition());
			keyframes.add(kf);
		}
		return keyframes;
	}

	public NBTBase getSaveData() 
	{
		NBTTagCompound sequenceData = new NBTTagCompound();
		NBTTagList animationList = new NBTTagList();
		NBTTagList movementList = new NBTTagList();
		for(AnimationPart animation : animations)
		{
			animationList.appendTag(animation.getSaveData());
		}
		for(AnimationMovement movement : movements)
		{
			movementList.appendTag(movement.getSaveData());
		}
		sequenceData.setTag("Animations", animationList);
		sequenceData.setTag("Movements", movementList);
		sequenceData.setString("Name", animationName);
		sequenceData.setFloat("ActionPoint", actionPoint);
		return sequenceData;
	}

	public void loadData(String entityName, NBTTagCompound compound) 
	{
		NBTTagList segmentList = compound.getTagList("Animations", 10);
		for(int i = 0; i < segmentList.tagCount(); i++)
		{
			AnimationPart animation = new AnimationPart();
			animation.loadData(entityName, segmentList.getCompoundTagAt(i));
			animations.add(animation);
		}		
		NBTTagList movementList = compound.getTagList("Movements", 10);
		for(int i = 0; i < movementList.tagCount(); i++)
		{
			AnimationMovement movement = new AnimationMovement();
			movement.loadData(movementList.getCompoundTagAt(i));
			movements.add(movement);
		}	
		animationName = compound.getString("Name");
		actionPoint= compound.getFloat("ActionPoint");
	}

}
