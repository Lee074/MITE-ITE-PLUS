package net.xiaoyu233.mitemod.miteite.trans.entity;

import net.minecraft.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.UUID;

@Mixin(EntityWitch.class)
public abstract class EntityWitchTrans extends EntityMonster{
    @Shadow @Final private static UUID field_110184_bp;
    @Shadow @Final private static AttributeModifier field_110185_bq;
    @Shadow @Final private static int[] witchDrops;
    @Shadow private int witchAttackTimer;
    @Shadow private EntityLiving summon_wolf_target;
    @Shadow private int summon_wolf_countdown;
    @Shadow private boolean has_summoned_wolves;
    @Shadow private int curse_random_seed;

    public EntityWitchTrans(World par1World) {
        super(par1World);
    }

    @Shadow protected abstract int summonWolves();

    @Shadow public abstract void setAggressive(boolean par1);

    @Shadow public abstract boolean getAggressive();

    public int getWitchAttackTimer() {
        return witchAttackTimer;
    }

    public int summonWolvesP(){
        return this.summonWolves();
    }

    @Overwrite
    public void onLivingUpdate() {
        if (!this.worldObj.isRemote) {
            if (this.getAggressive()) {
                if (this.witchAttackTimer-- <= 0) {
                    this.setAggressive(false);
                    ItemStack var1 = this.getHeldItemStack();
                    this.setCurrentItemOrArmor(0, (ItemStack)null);
                    if (var1 != null && var1.itemID == Item.potion.itemID) {
                        List var2 = Item.potion.getEffects(var1);
                        if (var2 != null) {

                            for (Object o : var2) {
                                MobEffect var4 = (MobEffect) o;
                                this.addPotionEffect(new MobEffect(var4));
                            }
                        }
                    }

                    this.getEntityAttribute(GenericAttributes.movementSpeed).removeModifier(field_110185_bq);
                }
            } else {
                short var5 = -1;
                if (this.rand.nextFloat() < 0.15F && this.isBurning() && !this.isPotionActive(MobEffectList.fireResistance)) {
                    var5 = 16307;
                } else if (this.rand.nextFloat() < 0.01F && this.getHealth() < this.getMaxHealth()) {
                    var5 = 16341;
                } else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(MobEffectList.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D) {
                    var5 = 16274;
                } else if (this.rand.nextFloat() < 0.25F && this.getAttackTarget() != null && !this.isPotionActive(MobEffectList.moveSpeed) && this.getAttackTarget().getDistanceSqToEntity(this) > 121.0D) {
                    var5 = 16274;
                }

                if (var5 > -1) {
                    this.setCurrentItemOrArmor(0, new ItemStack(Item.potion, 1, var5));
                    this.witchAttackTimer = this.getHeldItemStack().getMaxItemUseDuration();
                    this.setAggressive(true);
                    AttributeInstance var6 = this.getEntityAttribute(GenericAttributes.movementSpeed);
                    var6.removeModifier(field_110185_bq);
                    var6.applyModifier(field_110185_bq);
                }
            }

            if (this.rand.nextFloat() < 7.5E-4F) {
                this.worldObj.setEntityState(this, EnumEntityState.witch_magic);
            }

            if (this.summon_wolf_countdown > 0) {
                if (--this.summon_wolf_countdown == 0) {
                    this.has_summoned_wolves = this.summonWolvesP() > 0 || this.has_summoned_wolves;
                }
            } else if (!this.has_summoned_wolves && this.getLastHarmingEntity() instanceof EntityPlayer) {
                this.summon_wolf_target = (EntityLiving)this.getLastHarmingEntity();
                this.summon_wolf_countdown = 60;
            }
        }

        super.onLivingUpdate();
    }

    @Accessor
    public static AttributeModifier getField_110185_bq(){
        return field_110185_bq;
    }

    public void setWitchAttackTimer(int witchAttackTimer) {
        this.witchAttackTimer = witchAttackTimer;
    }

    public EntityLiving getSummon_wolf_target() {
        return summon_wolf_target;
    }

    public void setSummon_wolf_target(EntityLiving summon_wolf_target) {
        this.summon_wolf_target = summon_wolf_target;
    }

    public int getSummon_wolf_countdown() {
        return summon_wolf_countdown;
    }

    public void setSummon_wolf_countdown(int summon_wolf_countdown) {
        this.summon_wolf_countdown = summon_wolf_countdown;
    }

    public boolean isHas_summoned_wolves() {
        return has_summoned_wolves;
    }

    public void setHas_summoned_wolves(boolean has_summoned_wolves) {
        this.has_summoned_wolves = has_summoned_wolves;
    }

    public int getCurse_random_seed() {
        return curse_random_seed;
    }

    public void setCurse_random_seed(int curse_random_seed) {
        this.curse_random_seed = curse_random_seed;
    }

    @Overwrite
    public void dropFewItems(boolean recently_hit_by_player, DamageSource damage_source) {
        this.dropItemStack(new ItemStack(Item.potion, 1, 16424));

        int num_drops = this.rand.nextInt(5 + damage_source.getLootingModifier()) + 1;

        for(int i = 0; i < num_drops; ++i) {
            int item_id = witchDrops[this.rand.nextInt(witchDrops.length)];
            if (item_id == Block.plantRed.blockID) {
                this.dropItemStack(new ItemStack(Block.plantRed, 1, 2));
            } else if (item_id == Item.potion.itemID) {
                int subtype = this.rand.nextInt(6);
                if (subtype == 0) {
                    subtype = 8227;
                } else if (subtype == 1) {
                    subtype = 8261;
                } else if (subtype == 2) {
                    subtype = 16388;
                } else if (subtype == 3) {
                    subtype = 16426;
                } else if (subtype == 4) {
                    subtype = 16460;
                } else {
                    Minecraft.setErrorMessage("dropFewItems: unhandled subtype " + subtype);
                }

                this.dropItemStack(new ItemStack(item_id, 1, subtype));
            } else {
                this.dropItem(item_id, 1);
            }
        }
    }
}
