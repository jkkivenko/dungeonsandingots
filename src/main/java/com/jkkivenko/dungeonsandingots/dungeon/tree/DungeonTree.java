package com.jkkivenko.dungeonsandingots.dungeon.tree;

import java.util.ArrayList;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@SuppressWarnings("null")
public class DungeonTree {

    private StructureTemplatePool templatePool;
    private ArrayList<DungeonRoomData> generatedRooms;
    private RandomSource randomSource;

    public DungeonTree(StructureTemplatePool templatePool, RandomSource randomSource) {
        this.templatePool = templatePool;
        this.randomSource = randomSource;
    }

    public void generate(int maxDepth) {
        int numRoomsGenerated = 0;
        while (numRoomsGenerated < maxDepth) {
            StructurePoolElement nextRoom = templatePool.getRandomTemplate(randomSource);

        }
    }

}
