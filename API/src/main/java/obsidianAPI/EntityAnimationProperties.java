package obsidianAPI;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAPI.animation.ActionPointCallback;
import obsidianAPI.animation.AnimationPart;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.animation.wrapper.IAnimationWrapper;
import obsidianAPI.event.AnimationEvent;
import obsidianAPI.event.AnimationEvent.AnimationEventType;
import obsidianAPI.network.AnimationNetworkHandler;
import obsidianAPI.network.MessageAnimationStart;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.ModelAnimated;

public class EntityAnimationProperties implements IExtendedEntityProperties
{
	public static final String EXT_PROP_NAME = "ObsidianAnimation";

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
	private final List<ActionPointCallback> actionPointCallbacks = Lists.newLinkedList();
	
	private float prevEntityPosX, prevEntityPosZ;

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
	
	private void updateFrameTime()
	{
		now = System.nanoTime();

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
		
		if(sendPacket)
			AnimationNetworkHandler.network.sendToAll(new MessageAnimationStart(entity, activeAnimation, animationStartTime, loopAnim, transitionTime));
		
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
				float[] values = AnimationSequence.getPartValueAtTime(entityPosAnimations, MathHelper.floor_float(frameTime));
				
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
				nextFrame++;
			}

			if (frameTime > activeAnimationLength)
			{
				ObsidianAPI.EVENT_BUS.dispatchAnimationEvent(new AnimationEvent(AnimationEventType.END, entityName, activeAnimation));
				if (loop)
					setActiveAnimation(AnimationRegistry.getAnimation(entityName, activeAnimation), true, 0f, false);
				else if(onFinished != null)
					onFinished.run();
				else
					returnToIdle();
			}
		}
	}

	private void fireActions(int frame)
	{
		if (activeAnimation != null)
		{
			if(activeAnimationActionPoints != null) {
				Collection<String> actions = activeAnimationActionPoints.get(frame);
				if(actions != null) {
					for (String action : actions)
					{
						for (ActionPointCallback callback : actionPointCallbacks)
						{
							callback.onActionPoint(entity, action);
						}
					}
				}
			}
		}
	}

	private boolean isAnimationActive(String animationName)
	{		
		if(activeAnimation == null)
			return animationName.equals("Idle");
		return activeAnimation.equals(animationName) || activeAnimation.equals("transition_" + animationName);
	}


	private boolean isIdle()
	{
		return activeAnimation == null || activeAnimation.equals("Idle") || activeAnimation.equals("transition_idle");
	}

	public static EntityAnimationProperties get(Entity e) {
		return (EntityAnimationProperties) e.getExtendedProperties(EXT_PROP_NAME);
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

}
