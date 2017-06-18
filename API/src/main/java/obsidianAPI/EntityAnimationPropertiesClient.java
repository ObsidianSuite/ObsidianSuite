package obsidianAPI;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAPI.animation.ActionPointCallback;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.animation.wrapper.IAnimationWrapper;
import obsidianAPI.animation.wrapper.IEntityAnimated;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;

public class EntityAnimationPropertiesClient implements IExtendedEntityProperties
{
	public static final String EXT_PROP_NAME = "ObsidianAnimationClient";

	private final List<ActionPointCallback> actionPointCallbacks = Lists.newLinkedList();
	private EntityLivingBase entity;
	private String entityName;
	private AnimationSequence activeAnimation;
	private long animationStartTime;
	private boolean loop;
	private float multiplier = 1f;

	private long now;

	private int nextFrame = 0;

	private float prevEntityPosX, prevEntityPosZ;

	private Runnable onFinished;

	private float frameTime = 0f;	

	public float previousSwingTime = 0.0F;

	@Override
	public void init(Entity entity, World world)
	{
		this.entity = (EntityLivingBase) entity;
		entityName = AnimationRegistry.getEntityName(entity.getClass());
	}

	public void addActionPointCallback(ActionPointCallback callback)
	{
		actionPointCallbacks.add(callback);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {}

	@Override
	public void loadNBTData(NBTTagCompound compound) {}

	public void updateFrameTime()
	{
		now = System.nanoTime();

		if (activeAnimation == null)
			frameTime = 0f;
		else
			frameTime = Util.getAnimationFrameTime(now, animationStartTime, 0, activeAnimation.getFPS(), multiplier);
	}

	public void updateActiveAnimation() 
	{		
		Queue<IAnimationWrapper> tempQueue = AnimationRegistry.getAnimationListCopy(entityName);
		IAnimationWrapper wrapper;
		while((wrapper = tempQueue.poll()) != null) {
			if(wrapper.isActive((IEntityAnimated) entity))
				break;
		}

		if(wrapper != null) {
			String name = wrapper.getAnimation().getName();
			if(!isAnimationActive(name)) {
				setActiveAnimation(wrapper.getAnimation(), wrapper.getLoops(), wrapper.getTransitionTime());
			}
		}
		else {
			if(!isIdle()) 
				returnToIdle();
		}
	}

	public void setActiveAnimation(AnimationSequence sequence, boolean loopAnim, float transitionTime)
	{    	
//		Map<String, float[]> currentValues;
//		if (activeAnimation != null)
//		{
//			currentValues = getCurrentValues(model);
//		} else
//		{
//			currentValues = getOriginalValues(model);
//		}
//
//		multiplier = 1f;
//		animationStartTime = now;
//		nextFrame = 0;
//		onFinished = null;
//		//TODO add these back in?
//		//	        prevEntityPosX = 0f;
//		//	        prevEntityPosZ = 0f;
//		if (transitionTime > 0.001f)
//		{
//			updateFrameTime();
//
//			loop = false;
//
//			if(sequence != null)
//				activeAnimation = Util.createTransition(model, sequence.getName(), currentValues, sequence.getPartValuesAtTime(model,0f),transitionTime);
//			else
//				activeAnimation = Util.createTransition(model, "idle", currentValues, getOriginalValues(model), transitionTime);
//			onFinished = () ->
//			{
//				animationStartTime = now;
//				nextFrame = 0;
//				onFinished = null;
//				loop = sequence != null ? loopAnim : false;
//				activeAnimation = sequence;
//			};
//		}
//		else
//		{
//			this.loop = loopAnim;
//			activeAnimation = sequence;
//		}
	}

	public void returnToIdle(float transitionTime)
	{
		if(activeAnimation == null || activeAnimation.getName().equals("Idle"))
			return;
		setActiveAnimation(AnimationRegistry.getAnimation(entityName, "Idle"), true, transitionTime);
	}

	public void returnToIdle()
	{
		returnToIdle(0.25f);
	}

	public void setMultiplier(float multiplier)
	{
		if (frameTime > 0)
		{
			animationStartTime = (long) (now - (now - animationStartTime) * (double) this.multiplier / (double) multiplier);
		}
		this.multiplier = multiplier;
	}

	private Map<String, float[]> getOriginalValues(ModelObj model)
	{
		Map<String, float[]> values = Maps.newHashMap();

		for (Part part : model.parts)
		{
			values.put(part.getName(), part.getOriginalValues());
		}

		return values;
	}

	private Map<String, float[]> getCurrentValues(ModelObj model)
	{
		Map<String, float[]> values = Maps.newHashMap();

		float time = getAnimationFrameTime();

		for (Part part : model.parts)
		{
			values.put(part.getName(), activeAnimation.getPartValueAtTime(part, time));
		}

		return values;
	}

	public float getAnimationFrameTime()
	{
		return frameTime;
	}

	public AnimationSequence getActiveAnimation()
	{
		return activeAnimation;
	}

	public void updateAnimation(ModelObj model, float time)
	{
		if (activeAnimation != null)
		{
			Part entityPos = model.getPartFromName("entitypos");
			if (entityPos != null)
			{            	
				float entityPosX = entityPos.getValue(0);
				float entityPosZ = entityPos.getValue(2);

				float strafe = entityPosX - prevEntityPosX;
				float forward = entityPosZ - prevEntityPosZ;

				float f4 = MathHelper.sin(entity.rotationYaw * (float)Math.PI / 180.0F);
				float f5 = MathHelper.cos(entity.rotationYaw * (float)Math.PI / 180.0F);
				entity.setPosition(entity.posX + (double)(strafe * f5 - forward * f4), entity.posY,entity.posZ + (double)(forward * f5 + strafe * f4));

				prevEntityPosX = entityPosX;
				prevEntityPosZ = entityPosZ;
			}

			while (time > nextFrame)
			{
				fireActions(nextFrame);
				nextFrame++;
			}

			if (time > activeAnimation.getTotalTime())
			{
				if (loop)
				{
					setActiveAnimation(AnimationRegistry.getAnimation(entityName, activeAnimation.getName()), true, 0f);
				} else if (onFinished != null)
				{
					onFinished.run();
				}
				//	                else
				//	                {
				//	                    returnToIdle(model);
				//	                }
			}
		}
	}

	private void fireActions(int frame)
	{
		if (activeAnimation != null)
		{
			Collection<String> actions = activeAnimation.getActionPoints(frame);

			for (String action : actions)
			{
				for (ActionPointCallback callback : actionPointCallbacks)
				{
					callback.onActionPoint(entity, action);
				}
			}
		}
	}

	private boolean isAnimationActive(String animationName)
	{		
		if(activeAnimation == null)
			return false;
		return activeAnimation.getName().equals(animationName) || activeAnimation.getName().equals("transition_" + animationName);
	}

	private boolean isIdle()
	{
		return activeAnimation == null || activeAnimation.getName().equals("Idle") || activeAnimation.getName().equals("transition_idle");
	}

	public static EntityAnimationPropertiesClient get(Entity e) {
		return (EntityAnimationPropertiesClient) e.getExtendedProperties(EXT_PROP_NAME);
	}

}
