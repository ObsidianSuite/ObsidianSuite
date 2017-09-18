package com.dabigjoe.obsidianAPI.properties;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import com.dabigjoe.obsidianAPI.ObsidianAPI;
import com.dabigjoe.obsidianAPI.ObsidianAPIUtil;
import com.dabigjoe.obsidianAPI.animation.AnimationPart;
import com.dabigjoe.obsidianAPI.animation.AnimationSequence;
import com.dabigjoe.obsidianAPI.animation.wrapper.IAnimationWrapper;
import com.dabigjoe.obsidianAPI.event.AnimationEvent;
import com.dabigjoe.obsidianAPI.event.AnimationEvent.AnimationEventType;
import com.dabigjoe.obsidianAPI.network.AnimationNetworkHandler;
import com.dabigjoe.obsidianAPI.network.MessageAnimationStart;
import com.dabigjoe.obsidianAPI.registry.AnimationRegistry;
import com.dabigjoe.obsidianAPI.render.ModelAnimated;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;

public class EntityAnimationProperties implements IAnimationProperties
{
	
	private EntityLivingBase entity;
	private String entityName;

	private long now;
	private long animationStartTime;
	private float frameTime = 0f;	
	private int nextFrame = 0;
	private float multiplier = 1f;

	private String activeAnimation;
	private float activeAnimationLength;
	private int activeAnimationFPS;
	private Map<Integer, Set<String>> activeAnimationActionPoints;
	private TreeMap<Integer, AnimationPart> entityPosAnimations;
	private boolean loop;
	private Runnable onFinished;
	
	private float prevEntityPosX, prevEntityPosZ;

	@Override
	public void init(Entity entity)
	{
		this.entity = (EntityLivingBase) entity;
		entityName = AnimationRegistry.getEntityName(entity.getClass());
		now = System.currentTimeMillis();
		animationStartTime = now;
	}
	
	private void updateFrameTime()
	{
		now = System.currentTimeMillis();
		if (activeAnimation == null)
			frameTime = 0f;
		else
			frameTime = ObsidianAPIUtil.getAnimationFrameTime(now, animationStartTime, 0, activeAnimationFPS, multiplier);
	}

	public void updateActiveAnimation() 
	{
		Queue<IAnimationWrapper> tempQueue = AnimationRegistry.getAnimationListCopy(entityName);
		IAnimationWrapper wrapper;
		while((wrapper = tempQueue.poll()) != null) {
			if(wrapper.isActive(entity))
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
		setActiveAnimation(sequence, loopAnim, transitionTime, true);
	}
	
	private void setActiveAnimation(AnimationSequence sequence, boolean loopAnim, float transitionTime, boolean sendPacket)
	{    			
		if(sequence != null) {
			activeAnimation = sequence.getName();
			activeAnimationLength = sequence.getTotalTime();
			activeAnimationFPS = sequence.getFPS();
			activeAnimationActionPoints = sequence.getAllActionPoints();
			entityPosAnimations = sequence.getPartAnimationMap("entitypos");
		}
		else {
			activeAnimation = null;
			activeAnimationActionPoints = null;
			entityPosAnimations = null;
		}

		multiplier = 1f;
		animationStartTime = now;
		nextFrame = 0;

		activeAnimation = sequence != null ? sequence.getName() : null;
		
		if(sendPacket) {
			ObsidianAPI.EVENT_BUS.dispatchAnimationEvent(new AnimationEvent(AnimationEventType.START, entityName, activeAnimation, entity));
			AnimationNetworkHandler.network.sendToAll(new MessageAnimationStart(entity, activeAnimation, loopAnim, transitionTime));
		}
		
		prevEntityPosX = 0f;
		prevEntityPosZ = 0f;
		if (transitionTime > 0.01f)
		{
			loop = false;
			activeAnimation = "transition_" + activeAnimation;
			activeAnimationLength = transitionTime;
			onFinished = () ->
			{
				onFinished = null;
				updateFrameTime();
				setActiveAnimation(sequence, loopAnim, 0, false);
			};
		}
		else
			this.loop = loopAnim;		
	}

	private void returnToIdle(float transitionTime)
	{
		if(activeAnimation == null || activeAnimation.equals("Idle"))
			return;
		setActiveAnimation(AnimationRegistry.getAnimation(entityName, "Idle"), true, transitionTime);
	}

	private void returnToIdle()
	{
		returnToIdle(ModelAnimated.DEF_TRANSITION_TIME);
	}

	public void setMultiplier(float multiplier)
	{
		if (frameTime > 0)
		{
			animationStartTime = (long) (now - (now - animationStartTime) * (double) this.multiplier / (double) multiplier);
		}
		this.multiplier = multiplier;
	}

	public void runAnimationTick()
	{
		updateFrameTime();
		if (activeAnimation != null)
		{
			if(entityPosAnimations != null) {
				float[] values = AnimationSequence.getPartValueAtTime(entityPosAnimations, MathHelper.floor(frameTime));
				
				float entityPosX = values[0];
				float entityPosZ = values[2];
								
				float strafe = entityPosX - prevEntityPosX;
				float forward = entityPosZ - prevEntityPosZ;
				
				float f4 = MathHelper.sin(entity.rotationYaw * (float)Math.PI / 180.0F);
				float f5 = MathHelper.cos(entity.rotationYaw * (float)Math.PI / 180.0F);
				
				double posX = entity.posX + (double)(strafe * f5 - forward * f4);
				double posY = entity.posY;
				double posZ = entity.posZ + (double)(forward * f5 + strafe * f4);	
				
				if(entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {
					EntityLivingBase elb = (EntityLivingBase) entity;
					elb.setPositionAndUpdate(posX, posY, posZ);
				}
				
				prevEntityPosX = entityPosX;
				prevEntityPosZ = entityPosZ;
			}

			while (frameTime > nextFrame)
			{
				fireActions(nextFrame);
				ObsidianAPI.EVENT_BUS.dispatchAnimationEvent(new AnimationEvent(nextFrame, entityName, activeAnimation, entity));
				nextFrame++;
			}

			if (frameTime > activeAnimationLength)
			{
				ObsidianAPI.EVENT_BUS.dispatchAnimationEvent(new AnimationEvent(AnimationEventType.END, entityName, activeAnimation, entity));
				if (loop)
					setActiveAnimation(AnimationRegistry.getAnimation(entityName, activeAnimation), true, 0f, false);
				else if(onFinished != null)
					onFinished.run();
				else
					returnToIdle();
			}
		}
	}

	private void fireActions(int frame) {
		if (activeAnimation != null) {
			if(activeAnimationActionPoints != null) {
				Collection<String> actions = activeAnimationActionPoints.get(frame);
				if(actions != null) {
					for (String actionName : actions)
						ObsidianAPI.EVENT_BUS.dispatchAnimationEvent(new AnimationEvent(actionName, entityName, activeAnimation, entity));
				}
			}
		}
	}

	private boolean isAnimationActive(String animationName) {		
		if(activeAnimation == null)
			return false;
		return activeAnimation.equals(animationName) || activeAnimation.equals("transition_" + animationName);
	}


	private boolean isIdle() {
		return activeAnimation == null || activeAnimation.equals("Idle") || activeAnimation.equals("transition_idle");
	}
	
	public String getActiveAnimation() {
		return activeAnimation;
	}

	public String getEntityName() {
		return entityName;
	}
	
	public long getAnimationStartTime() {
		return animationStartTime;
	}
	
	public boolean getLoopAnim() {
		return this.loop;
	}

	public static EntityAnimationProperties get(Entity entity2) {
		return (EntityAnimationProperties) EntityAnimationPropertiesProvider.get(entity2, Side.SERVER);
	}

}
