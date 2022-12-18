package net.xiaoyu233.mitemod.miteite.block;

import net.minecraft.*;

public class BlockCharcoal extends BlockOreBlock {
    public BlockCharcoal(int par1) {
        super(par1, Material.coal);
        this.getMinHarvestLevel(2);
        this.setBlockHardness(1.2F);
    }

    @Override
    public int dropBlockAsEntityItem(BlockBreakInfo info) {
        return this.dropBlockAsEntityItem(info, Item.coal.itemID, 1, 9, 1.0F);
    }
}
