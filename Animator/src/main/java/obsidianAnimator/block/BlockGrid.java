package obsidianAnimator.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import obsidianAnimator.ObsidianAnimator;

public class BlockGrid extends Block {
	
	public BlockGrid() {
		super(Material.GLASS);
		setUnlocalizedName(ObsidianAnimator.MODID + ".grid");
		setRegistryName("grid");
	}

    public boolean renderAsNormalBlock() {
        return false;
    }
	
}
