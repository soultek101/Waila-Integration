package net.darkhax.wailaexamp;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;

public class BlockWailaTestBlock extends Block {

    public BlockWailaTestBlock() {

        super(Material.coral);
        this.setHardness(3.0f);
        this.setBlockName("example");
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.2f, 0.3f, 0.4f, 1.0f, 0.9f, 0.8f);
    }

    @Override
    public int getExpDrop(IBlockAccess world, int metadata, int fortune) {

        return 137;
    }

    @Override
    public boolean isBeaconBase(IBlockAccess worldObj, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {

        return true;
    }
}