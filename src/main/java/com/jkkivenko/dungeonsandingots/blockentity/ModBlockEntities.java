package com.jkkivenko.dungeonsandingots.blockentity;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.block.ModBlocks;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@SuppressWarnings("null")
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DungeonsAndIngots.MOD_ID);

    public static final Supplier<BlockEntityType<DungeonPortalBlockEntity>> DUNGEON_PORTAL_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
        "dungeon_portal_block_entity",
        // The block entity type.
        () -> new BlockEntityType<>(
                DungeonPortalBlockEntity::new,
                false,
                ModBlocks.DUNGEON_PORTAL_BLOCK.get()
        )
    );
}
