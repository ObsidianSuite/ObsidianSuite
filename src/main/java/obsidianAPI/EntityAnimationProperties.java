package obsidianAPI;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import obsidianAPI.animation.ActionPointCallback;
import obsidianAPI.animation.AnimationSequence;
import obsidianAPI.registry.AnimationRegistry;

import java.util.Collection;
import java.util.List;

public class EntityAnimationProperties implements IExtendedEntityProperties
{
    private final List<ActionPointCallback> actionPointCallbacks = Lists.newLinkedList();
    private Entity entity;
    private String entityName;
    private AnimationSequence activeAnimation;
    private long animationStartTime;

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

    public void setActiveAnimation(String binding)
    {
        activeAnimation = AnimationRegistry.getAnimation(entityName, binding);
        animationStartTime = System.nanoTime();
        nextFrame = 0;
    }

    public void clearAnimation()
    {
        activeAnimation = null;
    }

    public AnimationSequence getActiveAnimation()
    {
        return activeAnimation;
    }

    public long getAnimationStartTime()
    {
        return animationStartTime;
    }

    public void updateFrameTime(float time)
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
                clearAnimation();
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
