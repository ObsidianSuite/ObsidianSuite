package MCEntityAnimator.animation;

import java.util.ArrayList;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

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

	public void animateAll(float f, float speedMultiplier, boolean inGame, Entity entity) 
	{
		float time = f;
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
			if(time > s.getStartTime() && time <= s.getFinishTime())
			{
				s.animatePart(time - s.getStartTime());
			}
		}
		for(AnimationMovement m : this.movements)
		{
			if(time > m.getStartTime() && time <= m.getFinishTime())
			{
				m.moveEntity(time - m.getStartTime(), entity);
			}
		}
	}
	
	public float getTotalTime() 
	{
		float max = 0.0F;
		for(AnimationPart animation : animations)
		{
			if(animation.getFinishTime() > max)
			{
				max = animation.getFinishTime();
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
