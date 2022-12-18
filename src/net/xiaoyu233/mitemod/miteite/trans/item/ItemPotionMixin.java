package net.xiaoyu233.mitemod.miteite.trans.item;

import net.minecraft.Item;
import net.minecraft.ItemPotion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemPotion.class)
public class ItemPotionMixin extends Item {

    @Inject(method = "<init>",at = @At("RETURN"))
    private void inject(CallbackInfo callback){
        this.setMaxStackSize(3);
    }
}
