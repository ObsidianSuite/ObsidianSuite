package obsidianAnimations.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIWanderAlways extends EntityAIBase {

	private EntityCreature entity;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private double speed;
	private static final String __OBFID = "CL_00001608";

	public EntityAIWanderAlways(EntityCreature p_i1648_1_, double p_i1648_2_)
	{
		this.entity = p_i1648_1_;
		this.speed = p_i1648_2_;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		Vec3 vec3;

		do {
			vec3 = RandomPositionGenerator.findRandomTarget(this.entity, 100, 7);
		}
		while(vec3 == null);

		this.xPosition = vec3.xCoord;
		this.yPosition = vec3.yCoord;
		this.zPosition = vec3.zCoord;
		return true;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting()
	{
		return !this.entity.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{
		this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
	}

}
