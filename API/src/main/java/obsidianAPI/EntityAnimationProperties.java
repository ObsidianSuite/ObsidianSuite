package obsidianAPI;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
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

	private float prevEntityPosX;
	private float prevEntityPosZ;

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

        prevEntityPosX = 0f;
        prevEntityPosZ = 0f;
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

        float time = getAnimationFrameTime();

        for (Part part : model.parts)
        {
            prevValues.put(part.getName(), activeAnimation.getPartValueAtTime(part, time));
        }
    }

    public float getAnimationFrameTime()
    {
        return Util.getAnimationFrameTime(getAnimationStartTime(), 0, activeAnimation.getFPS(), multiplier);
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
            Part part = model.getPartFromName("entitypos");
            if (part != null)
            {
                float entityPosX = part.getValue(0);
                float entityPosZ = part.getValue(2);

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
