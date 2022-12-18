package net.xiaoyu233.mitemod.miteite.trans.item;

import net.minecraft.*;
import net.xiaoyu233.mitemod.miteite.item.Items;
import net.xiaoyu233.mitemod.miteite.item.Materials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemBucket.class)
public class ItemBucketMixin extends ItemVessel{
    public ItemBucketMixin(int id, Material vessel_material, Material contents_material, int standard_volume, int max_stack_size_empty, int max_stack_size_full, String texture) {
        super(id, vessel_material, contents_material, standard_volume, max_stack_size_empty, max_stack_size_full, texture);
    }

    @Shadow
    public ItemVessel getPeerForContents(Material material) {
        return null;
    }

    @Shadow
    public ItemVessel getPeerForVesselMaterial(Material material) {
        return null;
    }
    @Shadow
    public Block getBlockForContents() {
            return null;
    }

    @Overwrite
    public int getBurnTime(ItemStack item_stack) {
        if(item_stack.hasMaterial(Materials.vibranium)){
            return this.contains(Material.lava) ? 12800 : 0;
        }
        return this.contains(Material.lava) ? 3200 : 0;
    }

    @Overwrite
    public boolean tryPlaceContainedLiquid(World world, EntityPlayer player, int x, int y, int z, boolean allow_placement_of_source_block) {
        if (this.isEmpty()) {
            Minecraft.setErrorMessage("tryPlaceContainedLiquid: bucket is empty");
            return false;
        } else {
            Material material_in_bucket = this.getContents();
            if (material_in_bucket == null) {
                Minecraft.setErrorMessage("tryPlaceContainedLiquid: material in bucket is null");
                return false;
            } else {
                Material target_block_material = world.getBlockMaterial(x, y, z);
                if (target_block_material.isSolid()) {
                    return false;
                } else {
                    boolean placement_prevented = false;
                    if (material_in_bucket.canDouseFire() && world.getBlock(x, y, z) == Block.fire) {
                        if (!world.isRemote) {
                            world.douseFire(x, y, z, (Entity)null);
                        }

                        placement_prevented = true;
                    } else if (material_in_bucket == Material.water && world.provider.isHellWorld) {
                        if (!world.isRemote) {
                            world.blockFX(EnumBlockFX.steam, x, y, z);
                        }

                        placement_prevented = true;
                    }

                    if (!placement_prevented) {
                        if (player != null && !player.inCreativeMode() && material_in_bucket == target_block_material) {
                            return true;
                        }

                        if (!world.isRemote) {
                            WorldServer world_server = (WorldServer)world;
                            if (!target_block_material.isSolid() && !target_block_material.isLiquid() && !world.isAirBlock(x, y, z)) {
                                world.destroyBlock((new BlockBreakInfo(world, x, y, z)).setFlooded((BlockFluids)this.getBlockForContents()), true);
                            }

                            if (material_in_bucket == Material.water && world.getBlockMaterial(x, y, z) == Material.lava) {
                                world.tryConvertLavaToCobblestoneOrObsidian(x, y, z);
                            } else {
                                if (material_in_bucket == Material.water && world.getBlock(x, y - 1, z) == Block.mantleOrCore) {
                                    world.blockFX(EnumBlockFX.steam, x, y, z);
                                    return true;
                                }


                                if (material_in_bucket == Material.lava && world.getBlockMaterial(x, y, z) == Material.water) {
                                    world.tryConvertWaterToCobblestone(x, y, z);
                                } else {
                                    if (player == null || !player.inCreativeMode()) {
                                        if (material_in_bucket == Material.water) {
                                            if (!allow_placement_of_source_block) {
                                                world.scheduleBlockChange(x, y, z, Block.waterStill.blockID, this.getBlockForContents().blockID, 1, 16);
                                            } else if (!player.inCreativeMode()) {
                                                if(this.hasMaterial(Materials.vibranium)){
                                                    player.addExperience(0);
                                                }else {
                                                    player.addExperience(-100);
                                                }
                                            }
                                        } else if (material_in_bucket == Material.lava) {
                                            if (!allow_placement_of_source_block) {
                                                world.scheduleBlockChange(x, y, z, Block.lavaMoving.blockID, this.getBlockForContents().blockID, 1, 48);
                                            } else if (!player.inCreativeMode()) {
                                                if(this.hasMaterial(Materials.vibranium)){
                                                    player.addExperience(0);
                                                }else {
                                                    player.addExperience(-100);
                                                }
                                            }
                                        }
                                    }

                                    world.setBlock(x, y, z, this.getBlockForContents().blockID, 0, 3);
                                }
                            }
                        }
                    }

                    return true;
                }
            }
        }
    }

    @Overwrite
    public static ItemVessel getPeer(Material vessel_material, Material contents) {
        if (contents == null) {
            if (vessel_material == Material.copper) {
                return Item.bucketCopperEmpty;
            } else if (vessel_material == Material.silver) {
                return Item.bucketSilverEmpty;
            } else if (vessel_material == Material.gold) {
                return Item.bucketGoldEmpty;
            } else if (vessel_material == Material.iron) {
                return Item.bucketIronEmpty;
            } else if (vessel_material == Material.mithril) {
                return Item.bucketMithrilEmpty;
            } else if (vessel_material == Material.adamantium) {
                return Item.bucketAdamantiumEmpty;
            } else if (vessel_material == Materials.vibranium) {
                return Items.VIBRANIUM_BUCKET_EMPTY;
            } else {
                return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalEmpty : null;
            }
        } else if (contents == Material.water) {
            if (vessel_material == Material.copper) {
                return Item.bucketCopperWater;
            } else if (vessel_material == Material.silver) {
                return Item.bucketSilverWater;
            } else if (vessel_material == Material.gold) {
                return Item.bucketGoldWater;
            } else if (vessel_material == Material.iron) {
                return Item.bucketIronWater;
            } else if (vessel_material == Material.mithril) {
                return Item.bucketMithrilWater;
            } else if (vessel_material == Material.adamantium) {
                return Item.bucketAdamantiumWater;
            } else if (vessel_material == Materials.vibranium) {
                return Items.VIBRANIUM_BUCKET_WATER;
            } else {
                return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalWater : null;
            }
        } else if (contents == Material.lava) {
            if (vessel_material == Material.copper) {
                return Item.bucketCopperLava;
            } else if (vessel_material == Material.silver) {
                return Item.bucketSilverLava;
            } else if (vessel_material == Material.gold) {
                return Item.bucketGoldLava;
            } else if (vessel_material == Material.iron) {
                return Item.bucketIronLava;
            } else if (vessel_material == Material.mithril) {
                return Item.bucketMithrilLava;
            } else if (vessel_material == Material.adamantium) {
                return Item.bucketAdamantiumLava;
            } else if (vessel_material == Materials.vibranium) {
                return Items.VIBRANIUM_BUCKET_LAVA;
            } else {
                return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalLava : null;
            }
        } else if (contents == Material.milk) {
            if (vessel_material == Material.copper) {
                return Item.bucketCopperMilk;
            } else if (vessel_material == Material.silver) {
                return Item.bucketSilverMilk;
            } else if (vessel_material == Material.gold) {
                return Item.bucketGoldMilk;
            } else if (vessel_material == Material.iron) {
                return Item.bucketIronMilk;
            } else if (vessel_material == Material.mithril) {
                return Item.bucketMithrilMilk;
            } else if (vessel_material == Material.adamantium) {
                return Item.bucketAdamantiumMilk;
            } else if (vessel_material == Materials.vibranium) {
                return Items.VIBRANIUM_BUCKET_EMPTY;
            } else {
                return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalMilk : null;
            }
        } else if (contents == Material.stone) {
            if (vessel_material == Material.copper) {
                return Item.bucketCopperStone;
            } else if (vessel_material == Material.silver) {
                return Item.bucketSilverStone;
            } else if (vessel_material == Material.gold) {
                return Item.bucketGoldStone;
            } else if (vessel_material == Material.iron) {
                return Item.bucketIronStone;
            } else if (vessel_material == Material.mithril) {
                return Item.bucketMithrilStone;
            } else if (vessel_material == Material.adamantium) {
                return Item.bucketAdamantiumStone;
            } else if (vessel_material == Materials.vibranium) {
                return Items.VIBRANIUM_BUCKET_LAVA;
            } else {
                return vessel_material == Material.ancient_metal ? Item.bucketAncientMetalStone : null;
            }
        } else {
            return null;
        }
    }
}
