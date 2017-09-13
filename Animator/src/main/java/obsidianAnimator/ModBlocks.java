package obsidianAnimator;

import net.minecraftforge.fml.common.registry.GameRegistry;
import obsidianAnimator.block.BlockBase;
import obsidianAnimator.block.BlockGrid;

public class ModBlocks {

    @GameRegistry.ObjectHolder(ObsidianAnimator.MODID + ":base")
    public static BlockBase base;
    
    @GameRegistry.ObjectHolder(ObsidianAnimator.MODID + ":grid")
    public static BlockGrid grid;
	
}
