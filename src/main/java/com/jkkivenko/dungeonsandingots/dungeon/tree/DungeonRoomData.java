package com.jkkivenko.dungeonsandingots.dungeon.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

public record DungeonRoomData(StructurePoolElement element, Rotation rotation, BlockPos position){}
