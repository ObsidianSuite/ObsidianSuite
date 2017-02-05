package obsidianAPI.animation;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MathHelper;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;

import java.util.*;

/**
 * An actual animation. Comprised of animation parts - sections for each part of the model.
 *
 */
public class AnimationSequence
{

	private String animationName;
	private List<AnimationPart> animations = new ArrayList<AnimationPart>();
	private final Map<String, TreeMap<Integer,AnimationPart>> partsByPartName = Maps.newHashMap();
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

		partsByPartName.clear();
		for (AnimationPart part : animations)
		{
			addAnimationToMap(part);
		}
	}

	private void addAnimationToMap(AnimationPart part)
	{
        TreeMap<Integer, AnimationPart> parts = partsByPartName.get(part.getPartName());
        if (parts == null)
        {
            parts = new TreeMap<>();
            partsByPartName.put(part.getPartName(), parts);
        }

        parts.put(part.getStartTime(), part);
    }

	public List<AnimationPart> getAnimations()
	{
		return animations;
	}

	public Collection<AnimationPart> getAnimations(String partName)
	{
        TreeMap<Integer, AnimationPart> parts = partsByPartName.get(partName);
		if (parts == null)
		{
			return Collections.emptyList();
		}
		return parts.values();
	}

	public void addAnimation(AnimationPart part)
	{
		animations.add(part);
		addAnimationToMap(part);
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
	    return getAnimations(partName).size() >= 2;
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
		for(Part part : entityModel.parts)
		{
			if(!part.getName().equals(exceptionPartName))
			{
                TreeMap<Integer, AnimationPart> animations = partsByPartName.get(part.getName());
				if (animations != null && animations.size() > 0)
				{
                    AnimationPart anim = findPartForTime(animations, MathHelper.floor_float(time));
                    if (anim == null)
                        anim = animations.lastEntry().getValue();

                    anim.animatePart(part, time - anim.getStartTime());
                }
			}
		}
	}

	private AnimationPart findPartForTime(TreeMap<Integer,AnimationPart> parts, int time)
	{
        Map.Entry<Integer, AnimationPart> entry = parts.floorEntry(time);
        if (entry != null)
        {
            return entry.getValue();
        }

        return null;
	}

	public int getTotalTime()
	{
		int max = 0;
		for(AnimationPart animation : animations)
		{
			if(animation.getEndTime() > max)
			{
				max = animation.getEndTime();
			}
		}
		return max;
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
