package com.jkkivenko.dungeonsandingots.dungeon.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public record DungeonRoomData(StructurePoolElement element, BlockPos position, Rotation rotation) {

    public String toString() {
        return element.toString() + " at position " + position.toShortString() + " with rotation " + rotation.toString();
    }

}
