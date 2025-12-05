package com.jkkivenko.dungeonsandingots.block;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.blockentity.DungeonPortalBlockEntity;
import com.jkkivenko.dungeonsandingots.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

@SuppressWarnings("null")
public class DungeonPortalBlock extends Block implements EntityBlock {

    public DungeonPortalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        Level playerLevel = pPlayer.level();

        if (playerLevel.getBlockEntity(pPos) instanceof DungeonPortalBlockEntity dungeonportalblockentity) {
            if (pStack.getItem() == ModItems.DUNGEON_KEY_1.get()) {
                if (!playerLevel.isClientSide()) {
                    DungeonsAndIngots.LOGGER.debug("You are going to the haunted dungeon!");
                    boolean keyWorked = dungeonportalblockentity.activateWithKey(pPlayer, "haunted_forest");
                    if (keyWorked) {
                        return InteractionResult.SUCCESS;
                    } else {
                        return InteractionResult.FAIL;
                    }
                }
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
        } else {
            // This case should never occur in regular gameplay. IDK!
            DungeonsAndIngots.LOGGER.error("Dungeon portal block does not match block entity. I guess. How did you manage to do that?");
            return InteractionResult.PASS;
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonPortalBlockEntity(pos, state);
    }

    // @Override
    // public RenderShape getRenderShape(BlockState pState) {
    //     return RenderShape.MODEL;
    // }

}
