package com.jkkivenko.dungeonsandingots.block;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("null")
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(DungeonsAndIngots.MOD_ID);

    public static final DeferredBlock<Block> DUNGEON_PORTAL_BLOCK = BLOCKS.register("dungeon_portal_block",
        registryName -> new DungeonPortalBlock(BlockBehaviour.Properties.of()
            .setId(ResourceKey.create(Registries.BLOCK, registryName))
            .destroyTime(2.0f)
            .explosionResistance(10.0f)
            .sound(SoundType.STONE)
        )
    );
}