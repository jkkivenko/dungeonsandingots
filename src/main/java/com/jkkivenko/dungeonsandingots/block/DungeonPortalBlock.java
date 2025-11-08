package com.jkkivenko.dungeonsandingots.block;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.dungeon.DungeonManager;
import com.jkkivenko.dungeonsandingots.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

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
