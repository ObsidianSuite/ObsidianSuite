package obsidianAPI;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;

public class EntityAnimationPropertiesClient implements IExtendedEntityProperties
{
	public static final String EXT_PROP_NAME = "ObsidianAnimationClient";
	
    private EntityLivingBase entity;
    private String entityName;
    private AnimationSequence activeAnimation;
    private long animationStartTime;
    private boolean loop;
    private float multiplier = 1f;
	private float frameTime = 0f;	
	private Runnable onFinished;
    private long now;
		
    @Override
    public void init(Entity entity, World world)
    {
        this.entity = (EntityLivingBase) entity;
        entityName = AnimationRegistry.getEntityName(entity.getClass());
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
    
	
	private void setActiveAnimation(ModelObj model, AnimationSequence sequence, boolean loopAnim, float transitionTime) {
    	updateFrameTime();
    	setActiveAnimation(model, sequence, now, loopAnim, transitionTime);
	}
    
    public void setActiveAnimation(ModelObj model, AnimationSequence sequence, long animationStartTime, boolean loopAnim, float transitionTime)
    {    	
        Map<String, float[]> currentValues;
        if (activeAnimation != null)
            currentValues = getCurrentValues(model);
        else
            currentValues = getOriginalValues(model);

        this.animationStartTime = animationStartTime;
        multiplier = 1f;
        onFinished = null;

        if (transitionTime > 0.001f)
        {        	        	
            loop = false;
            
            if(sequence != null)
            	activeAnimation = Util.createTransition(model, sequence.getName(), currentValues, sequence.getPartValuesAtTime(model,0f),transitionTime);
            else
            	activeAnimation = Util.createTransition(model, "idle", currentValues, getOriginalValues(model), transitionTime);
            onFinished = () ->
            {
                this.animationStartTime = animationStartTime;
                onFinished = null;
                loop = sequence != null ? loopAnim : false;
                activeAnimation = sequence;
            };
        }
        else
        {
            this.loop = loopAnim;
            activeAnimation = sequence;
        }
    }

    private void returnToIdle(ModelObj model, float transitionTime)
    {
        if(activeAnimation == null || activeAnimation.getName().equals("Idle"))
        	return;
        setActiveAnimation(model, AnimationRegistry.getAnimation(entityName, "Idle"), true, transitionTime);
    }

    private void returnToIdle(ModelObj model)
    {
    	returnToIdle(model,0.25f);
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

    public void tickAnimation(ModelObj model, float time)
    {
        if (activeAnimation != null)
        {
            if (time > activeAnimation.getTotalTime())
            {
                if (loop)
                    setActiveAnimation(model, AnimationRegistry.getAnimation(entityName, activeAnimation.getName()), true, 0f);
                else if (onFinished != null)
                    onFinished.run();
                else
                    returnToIdle(model);
            }
        }
    }

	public static EntityAnimationPropertiesClient get(Entity e) {
		return (EntityAnimationPropertiesClient) e.getExtendedProperties(EXT_PROP_NAME);
	}

}
