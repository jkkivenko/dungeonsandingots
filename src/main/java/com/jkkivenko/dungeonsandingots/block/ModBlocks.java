package com.jkkivenko.dungeonsandingots.block;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("null")
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DungeonsAndIngots.MOD_ID);

    public static final DeferredBlock<DungeonPortalBlock> DUNGEON_PORTAL_BLOCK = BLOCKS.registerBlock(
        "dungeon_portal_block",
        DungeonPortalBlock::new,
        (props) -> props
        .destroyTime(1.5f)
        .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<Block> RITUAL_STONE_BLOCK = BLOCKS.registerSimpleBlock(
        "ritual_stone_block",
        (props) -> props
        .destroyTime(2.0f)
        .requiresCorrectToolForDrops()
    );

    public static final DeferredBlock<Block> DUNGEON_BRICK_BLOCK = BLOCKS.registerSimpleBlock(
        "dungeon_brick_block",
        (props) -> props
        .destroyTime(-1)
        .sound(SoundType.DEEPSLATE_BRICKS)
    );
    
}