package com.jkkivenko.dungeonsandingots.block;

import java.util.HashSet;
import java.util.Iterator;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.DungeonsAndIngotsSavedData;
import com.jkkivenko.dungeonsandingots.dungeon.DungeonManager;
import com.jkkivenko.dungeonsandingots.dungeon.DungeonManager;
import com.jkkivenko.dungeonsandingots.item.ModItems;
import com.mojang.serialization.Codec;

import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.Relative;

@SuppressWarnings("null")
public class DungeonPortalBlock extends Block {

    public DungeonPortalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        Level playerLevel = pPlayer.level();
        if (playerLevel instanceof ServerLevel) {
            if (pStack.getItem() == ModItems.DUNGEON_KEY_1.get()) {
                DungeonsAndIngots.LOGGER.debug("You are going to the haunted dungeon!");
                DungeonManager.generateAndSendPlayerToDungeon(pPlayer, "dungeon_1");
            }
        }
        return InteractionResult.CONSUME;
    }

    // @Override
    // public RenderShape getRenderShape(BlockState pState) {
    //     return RenderShape.MODEL;
    // }

}
