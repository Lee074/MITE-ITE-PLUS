package net.xiaoyu233.mitemod.miteite.trans.item;


import net.minecraft.Item;
import net.minecraft.ItemCoal;
import net.minecraft.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemCoal.class)
public class ItemCoalTrans extends Item {

    @Overwrite
    public int getBurnTime(ItemStack item_stack) {
        return 2560;
    }

}
