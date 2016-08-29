package com.nthrootsoftware.mcea.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;

public class BlockBase extends Block
{

	public BlockBase() 
	{
		super(Material.glass);
        this.setBlockTextureName("mod_MCEA:base");
	}

    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
}
