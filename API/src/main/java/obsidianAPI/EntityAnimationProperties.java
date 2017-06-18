package obsidianAPI;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

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
import obsidianAPI.render.ModelAnimated;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;

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
	private boolean loop;
	private Runnable onFinished;
	private final List<ActionPointCallback> actionPointCallbacks = Lists.newLinkedList();

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
			frameTime = Util.getAnimationFrameTime(now, animationStartTime, 0, activeAnimationFPS, multiplier);
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
		if(sequence != null) {
			activeAnimation = sequence.getName();
			activeAnimationLength = sequence.getTotalTime();
			activeAnimationFPS = sequence.getFPS();
			activeAnimationActionPoints = sequence.getAllActionPoints();
		}
		else {
			activeAnimation = null;
			activeAnimationActionPoints = null;
		}

		multiplier = 1f;
		animationStartTime = now;
		nextFrame = 0;
		onFinished = null;
		if (transitionTime > 0.001f)
		{
			updateFrameTime();

			loop = false;

			activeAnimation = "transition_" + activeAnimation;
			activeAnimationFPS = 10;
			activeAnimationLength = transitionTime*activeAnimationFPS;

			onFinished = () ->
			{
				animationStartTime = now;
				nextFrame = 0;
				onFinished = null;
				loop = sequence != null ? loopAnim : false;
				activeAnimation = sequence != null ? sequence.getName() : null;
				System.out.println(activeAnimation);
			};
		}
		else
		{
			this.loop = loopAnim;
			activeAnimation = sequence != null ? sequence.getName() : null;
		}

		System.out.println(activeAnimation);
	}

	private void returnToIdle(float transitionTime)
	{
		if(activeAnimation == null || activeAnimation.equals("Idle"))
			return;
		setActiveAnimation(AnimationRegistry.getAnimation(entityName, "Idle"), true, transitionTime);
	}

	private void returnToIdle()
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

	public void runAnimationTick()
	{
		updateFrameTime();
		if (activeAnimation != null)
		{
			while (frameTime > nextFrame)
			{
				fireActions(nextFrame);
				nextFrame++;
			}

			if (frameTime > activeAnimationLength)
			{
				if (loop)
				{
					setActiveAnimation(AnimationRegistry.getAnimation(entityName, activeAnimation), true, 0f);
				} else if (onFinished != null)
				{
					onFinished.run();
				}
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

	public String getEntityName() {
		return entityName;
	}

}
