package obsidianAPI.animation;

import net.minecraft.entity.Entity;

public interface ActionPointCallback
{
    void onActionPoint(Entity entity, String actionPoint);
}
