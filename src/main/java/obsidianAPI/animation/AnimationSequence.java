package obsidianAPI.animation;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;

/**
 * An actual animation. Comprised of animation parts - sections for each part of the model.
 *
 */
public class AnimationSequence 
{

	private String animationName;
	List<AnimationPart> animations = new ArrayList<AnimationPart>();
	private float actionPoint = 0.0F;
	private int fps;
	private String entityName;

	public AnimationSequence(String entityName, String animationName) 
	{
		this.entityName = entityName;
		this.animationName = animationName;
		this.fps = 25;
	}
	
	public AnimationSequence(NBTTagCompound compound) 
	{
		this.loadData(compound);
	}

	public String getEntityName()
	{
		return entityName;
	}
	
	public String getName() 
	{
		return animationName;
	}
	
	public void setAnimations(List<AnimationPart> animations)
	{
		this.animations = animations;
	}
	
	public List<AnimationPart> getAnimations() 
	{
		return animations;
	}

	public void addAnimation(AnimationPart par0Animation)
	{
		animations.add(par0Animation);
	}

	public void setActionPoint(float time)
	{
		this.actionPoint = time;
	}

	public float getActionPoint()
	{
		return actionPoint;
	}
	
	public int getFPS()
	{
		return fps;
	}
	
	public void setFPS(int fps)
	{
		this.fps = fps;
	}
	
	/**
	 * Return true if the given partname has two or more animation parts associated with it.
	 */
	public boolean multiPartSequence(String partName)
	{
		boolean one = false;
		for(AnimationPart s : this.animations)
		{
			if(s.getPartName().equals(partName))
			{
				//If one is true, another animation part having this part name implies two or more.
				if(one)
					return true;
				else
					one = true;
			}
		}
		return false;
	}

	public void animateAll(float time, ModelObj entityModel) 
	{
		animateAll(time, entityModel, "");
	}
	
	/**
	 * Sets all the parts of a model to their rotation at a given time.
	 * The part with name = exceptionPartName will not be rotated.
	 */
	public void animateAll(float time, ModelObj entityModel, String exceptionPartName) 
	{
		//TODO this could be more efficient, currently O(num_anim_parts*num_body_parts)
		for(Part part : entityModel.parts)
		{
			if(!part.getName().equals(exceptionPartName))
			{
				AnimationPart lastAnimation = getLastAnimation(part.getName());
				if(lastAnimation != null && time > lastAnimation.getEndTime())
					lastAnimation.animatePart(part, lastAnimation.getEndTime() - lastAnimation.getStartTime());
				else
				{
					for(AnimationPart s : this.animations)
					{
						if(s.getPartName().equals(part.getName()) && time >= s.getStartTime() && time <= s.getEndTime())
							s.animatePart(part, time - s.getStartTime());
					}
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
		return max;
	}	

	/**
	 * Returns the time of the last frame for a given part.
	 */
	private AnimationPart getLastAnimation(String partName)
	{
		AnimationPart lastAnimation = null;
		for(AnimationPart s : this.animations)
		{
			if(s.getPartName().equals(partName))
				if(lastAnimation != null)
				{
					if(s.getEndTime() > lastAnimation.getEndTime())
						lastAnimation = s;
				}
				else
					lastAnimation = s;
		}
		return lastAnimation;
	}

	public NBTTagCompound getSaveData() 
	{
		NBTTagCompound sequenceData = new NBTTagCompound();
		NBTTagList animationList = new NBTTagList();
		for(AnimationPart animation : animations)
			animationList.appendTag(animation.getSaveData());
		sequenceData.setTag("Animations", animationList);
		sequenceData.setString("EntityName", entityName);
		sequenceData.setString("Name", animationName);
		sequenceData.setFloat("ActionPoint", actionPoint);
		sequenceData.setInteger("FPS", fps);
		return sequenceData;
	}

	public void loadData(NBTTagCompound compound) 
	{
		entityName = compound.getString("EntityName");
		NBTTagList segmentList = compound.getTagList("Animations", 10);
		for(int i = 0; i < segmentList.tagCount(); i++)
		{
			AnimationPart animation = new AnimationPart(segmentList.getCompoundTagAt(i));
			animations.add(animation);
		}		
		animationName = compound.getString("Name");
		actionPoint = compound.getFloat("ActionPoint");
		fps = compound.hasKey("FPS") ? compound.getInteger("FPS") : 25;
	}

}
