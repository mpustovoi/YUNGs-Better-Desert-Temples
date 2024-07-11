package com.yungnickyoung.minecraft.betterdeserttemples.world.processor;

import com.mojang.serialization.MapCodec;
import com.yungnickyoung.minecraft.betterdeserttemples.BetterDesertTemplesCommon;
import com.yungnickyoung.minecraft.betterdeserttemples.module.StructureProcessorModule;
import com.yungnickyoung.minecraft.betterdeserttemples.world.ArmorStandChances;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Gives armor stands random armor depending on the type of armor
 * they are already wearing.
 */
@ParametersAreNonnullByDefault
public class ArmorStandProcessor extends StructureProcessor {
    public static final ArmorStandProcessor INSTANCE = new ArmorStandProcessor();
    public static final MapCodec<StructureProcessor> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public StructureTemplate.StructureEntityInfo processEntity(LevelReader levelReader,
                                                               BlockPos structurePiecePos,
                                                               StructureTemplate.StructureEntityInfo localEntityInfo,
                                                               StructureTemplate.StructureEntityInfo globalEntityInfo,
                                                               StructurePlaceSettings structurePlaceSettings,
                                                               StructureTemplate template) {
        if (globalEntityInfo.nbt.getString("id").equals("minecraft:armor_stand")) {
            ListTag armorItems = globalEntityInfo.nbt.getList("ArmorItems", 10);
            RandomSource randomSource = structurePlaceSettings.getRandom(globalEntityInfo.blockPos);

            // Type depends on the helmet and nothing else
            String helmet;
            try {
                helmet = ((CompoundTag) armorItems.get(3)).get("id").toString();
            } catch (Exception e) {
                BetterDesertTemplesCommon.LOGGER.info("Unable to randomize armor stand at {}. Missing helmet?", globalEntityInfo.blockPos);
                return globalEntityInfo;
            }

            // Iron helmet indicates we should use the armory pool. Otherwise, use the wardrobe pool.
            boolean isArmory = helmet.equals("\"minecraft:iron_helmet\"");

            CompoundTag newNBT = globalEntityInfo.nbt.copy();
            ListTag armorItemsList = newNBT.getList("ArmorItems", 10);

            // Boots
            String bootsString = isArmory
                    ? ForgeRegistries.ITEMS.getKey(ArmorStandChances.get().getArmoryBoots(randomSource)).toString()
                    : ForgeRegistries.ITEMS.getKey(ArmorStandChances.get().getWardrobeBoots(randomSource)).toString();
            if (!bootsString.equals("minecraft:air")) {
                armorItemsList.getCompound(0).putString("id", bootsString);
                armorItemsList.getCompound(0).putByte("Count", (byte) 1);
                armorItemsList.getCompound(0).put("tag", Util.make(new CompoundTag(), compoundTag -> compoundTag.putInt("Damage", 0)));
            }

            // Leggings
            String leggingsString = isArmory
                    ? ForgeRegistries.ITEMS.getKey(ArmorStandChances.get().getArmoryLeggings(randomSource)).toString()
                    : ForgeRegistries.ITEMS.getKey(ArmorStandChances.get().getWardrobeLeggings(randomSource)).toString();
            if (!leggingsString.equals("minecraft:air")) {
                armorItemsList.getCompound(1).putString("id", leggingsString);
                armorItemsList.getCompound(1).putByte("Count", (byte) 1);
                armorItemsList.getCompound(1).put("tag", Util.make(new CompoundTag(), compoundTag -> compoundTag.putInt("Damage", 0)));
            }

            // Chestplate
            String chestplateString = isArmory
                    ? ForgeRegistries.ITEMS.getKey(ArmorStandChances.get().getArmoryChestplate(randomSource)).toString()
                    : ForgeRegistries.ITEMS.getKey(ArmorStandChances.get().getWardrobeChestplate(randomSource)).toString();
            if (!chestplateString.equals("minecraft:air")) {
                armorItemsList.getCompound(2).putString("id", chestplateString);
                armorItemsList.getCompound(2).putByte("Count", (byte) 1);
                armorItemsList.getCompound(2).put("tag", Util.make(new CompoundTag(), compoundTag -> compoundTag.putInt("Damage", 0)));
            }

            // Helmet
            String helmetString = isArmory
                    ? ForgeRegistries.ITEMS.getKey(ArmorStandChances.get().getArmoryHelmet(randomSource)).toString()
                    : ForgeRegistries.ITEMS.getKey(ArmorStandChances.get().getWardrobeHelmet(randomSource)).toString();
            if (!helmetString.equals("minecraft:air")) {
                armorItemsList.getCompound(3).putString("id", helmetString);
                armorItemsList.getCompound(3).putByte("Count", (byte) 1);
                armorItemsList.getCompound(3).put("tag", Util.make(new CompoundTag(), compoundTag -> compoundTag.putInt("Damage", 0)));
            }

            globalEntityInfo = new StructureTemplate.StructureEntityInfo(globalEntityInfo.pos, globalEntityInfo.blockPos, newNBT);
        }
        return globalEntityInfo;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader levelReader,
                                                             BlockPos jigsawPiecePos,
                                                             BlockPos jigsawPieceBottomCenterPos,
                                                             StructureTemplate.StructureBlockInfo blockInfoLocal,
                                                             StructureTemplate.StructureBlockInfo blockInfoGlobal,
                                                             StructurePlaceSettings structurePlacementData) {
        return blockInfoGlobal;
    }

    protected StructureProcessorType<?> getType() {
        return StructureProcessorModule.ARMOR_STAND_PROCESSOR;
    }
}