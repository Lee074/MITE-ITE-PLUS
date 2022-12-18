package net.xiaoyu233.mitemod.miteite.trans.gui;

import net.minecraft.*;
import net.xiaoyu233.mitemod.miteite.item.Materials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(axs.class)
public class GuiFurnaceMixin extends awy {
    private static final String BOOST_INFO = LocaleI18n.translateToLocal("container.furnace.boost");
    @Shadow
    private TileEntityFurnace u;
    @Shadow
    protected void a(float v, int i, int i1) {}

    public GuiFurnaceMixin(Container par1Container) {
        super(par1Container);
    }

    @Overwrite
    protected void b(int par1, int par2) {
        String var3 = this.u.hasCustomName() ? this.u.getCustomNameOrUnlocalized() : bkb.a(this.u.getCustomNameOrUnlocalized());
        this.o.b(var3, 29, 5, 4210752);
        this.o.b(bkb.a("container.inventory"), 8, this.d - 96 + 3, 4210752);
        if (this.u.getFurnaceBlock().blockMaterial == Materials.vibranium) {
            this.o.b(BOOST_INFO +  "50%", 91, 7, 11141290);
        }
    }


}
