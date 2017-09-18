package com.dabigjoe.obsidianAPI;

import java.util.Map;

import com.dabigjoe.obsidianAPI.animation.AnimationPart;
import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.animation.ai.IEntityAIAnimation;
import com.dabigjoe.obsidianAPI.animation.wrapper.IEntityAnimated;
import com.dabigjoe.obsidianAPI.properties.EntityAnimationProperties;
import com.dabigjoe.obsidianAPI.render.ModelObj;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

public class ObsidianAPIUtil
{

	public static float getAnimationFrameTime(long startTimeMillis, float startTimeFrame, int fps, float multiplier)
	{
		return (System.currentTimeMillis() - startTimeMillis)/1000F*fps*multiplier + startTimeFrame;
	}

	public static float getAnimationFrameTime(long now, long startTimeMillis, float startTimeFrame, int fps, float multiplier)
	{
		return (float) ((now - startTimeMillis) / 1000D * fps * multiplier + startTimeFrame);
	}

	public static AnimationSequence createTransition(ModelObj model, String animName, Map<String, float[]> from, Map<String, float[]> to, float duration)
    {
        AnimationSequence seq = new AnimationSequence(model.entityName, "transition_"+ animName);
        seq.setFPS(10);

        for (Map.Entry<String, float[]> entry : from.entrySet())
        {
            float[] fromPart = entry.getKey().equals("entitypos") ? new float[] {0f, entry.getValue()[1], 0f} : entry.getValue();
            float[] toPart = to.get(entry.getKey());

            AnimationPart part = new AnimationPart(0, (int) (duration * 10f), fromPart, toPart, model.getPartFromName(entry.getKey()));
            seq.addAnimation(part);
        }

        return seq;
    }
	
	public static boolean isEntityMoving(EntityLivingBase entity) {
		boolean isMoving;
		if(entity instanceof IEntityAnimated)
			isMoving = ((IEntityAnimated) entity).isMoving();
		else
			isMoving = entity.limbSwingAmount > 0.02F;
		return isMoving;
	}
	
	public static boolean isEntityAITaskActive(EntityLiving entity, String AIName) {
		for(Object obj : entity.tasks.taskEntries) {
			EntityAITaskEntry taskEntry = (EntityAITaskEntry) obj;
			EntityAIBase task = taskEntry.action;
			if(task instanceof IEntityAIAnimation) {
				IEntityAIAnimation animatedTask = (IEntityAIAnimation) task;
				if(animatedTask.getAIName().equals(AIName) && animatedTask.isExecuting())
					return true;
			}
		}
		return false;
	}
	
	public static boolean isAnimatedEntity(Entity entity) {
		return entity != null ? EntityAnimationProperties.get(entity) != null : false;
	}
}
