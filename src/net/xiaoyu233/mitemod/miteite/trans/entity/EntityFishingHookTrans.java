package net.xiaoyu233.mitemod.miteite.trans.entity;

import net.minecraft.*;
import net.xiaoyu233.mitemod.miteite.util.Configs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityFishingHook.class)
public abstract class EntityFishingHookTrans extends Entity {
   @Shadow
   public EntityPlayer angler;
   @Shadow
   public Entity bobber;
   @Shadow
   public int shake;
   @Shadow
   private boolean inGround;
   @Shadow
   private int ticksCatchable;

   public EntityFishingHookTrans(World par1World) {
      super(par1World);
   }

   @Redirect(method = "catchFish",
           at = @At(value = "NEW",
                   target = "(Lnet/minecraft/World;DDDI)Lnet/minecraft/EntityExperienceOrb;"))
   private EntityExperienceOrb ctorFishingExp(World par1World, double par2, double par4, double par6, int par8){
      return new EntityExperienceOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5D, this.angler.posZ + 0.5D,Configs.GameMechanics.FISHING_XP_SCALE.get());
   }

   @Overwrite
   private boolean checkForBite() {
      int x = MathHelper.floor_double(this.posX);
      int y = MathHelper.floor_double(this.posY - 0.20000000298023224);
      int z = MathHelper.floor_double(this.posZ);
      if (BlockFluids.isFullWaterBlock(this.worldObj, x, y, z, false) && this.worldObj.isAirBlock(x, y + 1, z)) {
         int dx = this.rand.nextInt(7) - 3;
         int dy = -this.rand.nextInt(4);
         int dz = this.rand.nextInt(7) - 3;
         if (!this.isFishInhabitedWaterBlock(x + dx, y + dy, z + dz)) {
            return false;
         } else {
            Vec3D fish_hook_position = this.worldObj.getVec3((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F));
            Vec3D fish_position = this.worldObj.getVec3((double)((float)(x + dx) + 0.5F), (double)((float)(y + dy) + 0.5F), (double)((float)(z + dz) + 0.5F));
            if (!this.worldObj.checkForLineOfPhysicalReach(fish_hook_position, fish_position)) {
               return false;
            } else {
               int time_of_day = this.worldObj.getAdjustedTimeOfDay();
               float time_factor = (float)Math.min(Math.abs(time_of_day - 5500), Math.abs(time_of_day - 17500)) / 600.0F;
               int chance_in = this.worldObj.isBlueMoon(true) ? 120 : MathHelper.clamp_int((int)(120.0F * time_factor), 60, 240);
               if (this.worldObj.canLightningStrikeAt(x, y + 1, z)) {
                  chance_in /= 2;
               }

               if (this.worldObj.areSkillsEnabled() && !this.angler.hasSkill(Skill.FISHING)) {
                  chance_in *= 2;
               }

               int fortune = EnchantmentManager.getFishingFortuneModifier(this.angler);

               for(int i = 0; i < fortune; ++i) {
                  chance_in = chance_in * 9 / 10;
               }

               if (this.angler.inventory.getHotbarSlotContainItem(Item.wormRaw) >= 0) {
                  chance_in = (int)((float)chance_in * 0.5F);
               }

               return this.rand.nextInt(chance_in) == 0;
            }
         }
      } else {
         return false;
      }
   }

   @Shadow
   private boolean isFishInhabitedWaterBlock(int x, int y, int z) {
      if (BlockFluids.isFullWaterBlock(this.worldObj, x, y, z, false)) {
         return true;
      } else {
         return this.worldObj.getBlock(x, y, z) == Block.waterStill && this.worldObj.getBlockMetadata(x, y, z) == 8;
      }
   }


}
