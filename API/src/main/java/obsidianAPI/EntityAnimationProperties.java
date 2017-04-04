package obsidianAPI;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAPI.animation.ActionPointCallback;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;
import obsidianAPI.render.ModelObj;
import obsidianAPI.render.part.Part;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class EntityAnimationProperties implements IExtendedEntityProperties
{
    private final List<ActionPointCallback> actionPointCallbacks = Lists.newLinkedList();
    private Entity entity;
    private String entityName;
    private AnimationSequence activeAnimation;
    private long animationStartTime;
    private final Map<String, float[]> prevValues = Maps.newHashMap();
    private boolean loop;
    private float multiplier = 1f;

	private int nextFrame = 0;

    @Override
    public void init(Entity entity, World world)
    {
        this.entity = entity;
        entityName = AnimationRegistry.getEntityName(entity.getClass());
    }

    public void addActionPointCallback(ActionPointCallback callback)
    {
        actionPointCallbacks.add(callback);
    }

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {

    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {

    }

	public Map<String, float[]> getPrevValues()
    {
		return prevValues;
	}

    public void setActiveAnimation(ModelObj model, String binding, boolean loop)
    {
        if (activeAnimation != null)
        {
            setPrevValuesFromActiveAnimation(model);
        }
        else
        {
            setPrevValuesToOriginal(model);
        }

        this.loop = loop;
        multiplier = 1f;
        activeAnimation = AnimationRegistry.getAnimation(entityName, binding);
        animationStartTime = System.nanoTime();
        nextFrame = 0;
    }

    public void clearAnimation(ModelObj model)
    {
        if (activeAnimation != null)
        {
            setPrevValuesFromActiveAnimation(model);

            multiplier = 1f;
            activeAnimation = null;
            animationStartTime = System.nanoTime();
        }
    }

    public float getMultiplier()
    {
        return multiplier;
    }

    public void setMultiplier(float multiplier)
    {
        this.multiplier = multiplier;
    }

    private void setPrevValuesToOriginal(ModelObj model)
    {
        prevValues.clear();

        for (Part part : model.parts)
        {
            prevValues.put(part.getName(), part.getOriginalValues());
        }
    }

    private void setPrevValuesFromActiveAnimation(ModelObj model)
    {
        prevValues.clear();

        float time = Util.getAnimationFrameTime(getAnimationStartTime(), 0, activeAnimation.getFPS(), getMultiplier());

        for (Part part : model.parts)
        {
            prevValues.put(part.getName(), activeAnimation.getPartValueAtTime(part, time));
        }
    }

    public AnimationSequence getActiveAnimation()
    {
        return activeAnimation;
    }

    public long getAnimationStartTime()
    {
        return animationStartTime;
    }

    public void updateFrameTime(ModelObj model, float time)
    {
        if (activeAnimation != null)
        {
            while (time > nextFrame)
            {
                fireActions(nextFrame);
                nextFrame++;
            }

            if (time > activeAnimation.getTotalTime())
            {
                if (loop)
                {
                    setActiveAnimation(model, activeAnimation.getName(), true);
                }
                else
                {
                    clearAnimation(model);
                }
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
}
