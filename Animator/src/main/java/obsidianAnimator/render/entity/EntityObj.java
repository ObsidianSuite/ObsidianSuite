package obsidianAnimator.render.entity;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityObj extends EntityMob
{
	private ItemStack heldItem = ItemStack.EMPTY;
	private String entityType;
	
	public EntityObj(World world)
	{
		super(world);
	}
	
	public EntityObj(World world, String type) 
	{
		super(world);
		entityType = type;
	}

	public void setCurrentItem(ItemStack par1ItemStack) 
	{
		 this.heldItem = par1ItemStack;
	}
	
    @Override
	public ItemStack getHeldItem(EnumHand hand)
	{
		return heldItem;
	}
    
	@Override
	public ItemStack getHeldItemMainhand() {
		return heldItem;
	}

	@Override
	public ItemStack getHeldItemOffhand() {
		return heldItem;
	}

	public String getType() 
	{
		return entityType;
	}

}
