package com.jkkivenko.dungeonsandingots.dungeon;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.DungeonData;
import com.jkkivenko.dungeonsandingots.dungeon.generator.DungeonGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@SuppressWarnings("null")
public class DimensionManager {

    private final int[][] MIN_ROOMS = {{10, 10, 10, 10}, {10, 10, 10, 10}, {10, 10, 10, 10}, {10, 10, 10, 10}};
    private final int[][] MAX_ROOMS = {{25, 25, 25, 25}, {25, 25, 25, 25}, {25, 25, 25, 25}, {25, 25, 25, 25}};

    // startPool[0][1] represents the start pool for the 1st layer of the 0th dungeon (i.e. the burial grounds)
    private boolean poolsInitialized = false;
    private StructureTemplatePool[][] startPool = new StructureTemplatePool[4][4];
    private StructureTemplatePool[][] regularPool = new StructureTemplatePool[4][4];
    private StructureTemplatePool[][] oneOrMorePool = new StructureTemplatePool[4][4];
    private StructureTemplatePool[][] exactlyOnePool = new StructureTemplatePool[4][4];

    private static DimensionManager instance = null;

    private boolean[] dimensionInUse = {false, false, false, false, false, false, false, false, false, false};

    public static DimensionManager getInstance() {
        if (instance == null) {
            instance = new DimensionManager();
        }
        return instance;
    }

    private void initializeTemplatePools(Level level) {
        if (!poolsInitialized) {
            // First get a reference to a registry to lookup all the other things.
            Registry<StructureTemplatePool> templateRegistry = level.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);

            // Get resource locations for the haunted forest
            ResourceLocation startPoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "haunted_forest/forest_path/start");
            ResourceKey<StructureTemplatePool> startPoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, startPoolResourceLocation);
            startPool[0][0] = templateRegistry.getOrThrow(startPoolResourceKey).value();
            
            ResourceLocation regularPoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "haunted_forest/forest_path/all");
            ResourceKey<StructureTemplatePool> regularPoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, regularPoolResourceLocation);
            regularPool[0][0] = templateRegistry.getOrThrow(regularPoolResourceKey).value();
            
            ResourceLocation exactlyOnePoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "haunted_forest/forest_path/end");
            ResourceKey<StructureTemplatePool> exactlyOnePoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, exactlyOnePoolResourceLocation);
            exactlyOnePool[0][0] = templateRegistry.getOrThrow(exactlyOnePoolResourceKey).value();
            
            ResourceLocation oneOrMorePoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "haunted_forest/forest_path/one_or_more");
            ResourceKey<StructureTemplatePool> oneOrMorePoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, oneOrMorePoolResourceLocation);
            oneOrMorePool[0][0] = templateRegistry.getOrThrow(oneOrMorePoolResourceKey).value();

            // TODO: Add pools for the other floors and dungeon types.

            poolsInitialized = true;
        }
    }
    
    public int generateDungeon(Level sourceLevel, int dungeonTypeIndex, int layer) {
        // First, make sure the template pools have been initialized.
        initializeTemplatePools(sourceLevel);
        // Reserve a dimension for use
        int dimensionIndex = reserveDimension();
        // Then, get some useful references.
        MinecraftServer minecraftServer = sourceLevel.getServer();
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_" + Integer.toString(dimensionIndex));
        ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, resourceLocation);
        ServerLevel targetLevel = minecraftServer.getLevel(resourceKey);
        // Setup and use the DungeonGenerator
        DungeonGenerator dungeonGenerator = new DungeonGenerator(
            targetLevel,
            startPool[dungeonTypeIndex][layer],
            regularPool[dungeonTypeIndex][layer],
            oneOrMorePool[dungeonTypeIndex][layer],
            exactlyOnePool[dungeonTypeIndex][layer],
            MIN_ROOMS[dungeonTypeIndex][layer],
            MAX_ROOMS[dungeonTypeIndex][layer]
        );
        dungeonGenerator.generate();
        dungeonGenerator.place();
        List<BoundingBox> boundingBoxes = dungeonGenerator.getRoomBoundingBoxes();
        // Save bounding boxes to disk so we can delete the dungeon when the time comes.
        DungeonData dungeonData = targetLevel.getDataStorage().computeIfAbsent(DungeonData.ID);
        dungeonData.setBoundingBoxes(boundingBoxes);
        // Return the index of the dimension we reserved
        return dimensionIndex;
    }

    public void deleteDungeon(Level sourceLevel, int dimensionIndex) {
        MinecraftServer minecraftServer = sourceLevel.getServer();
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_" + Integer.toString(dimensionIndex));
        ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, resourceLocation);
        ServerLevel targetLevel = minecraftServer.getLevel(resourceKey);
        DungeonData dungeonData = targetLevel.getDataStorage().computeIfAbsent(DungeonData.ID);
        deleteBlocksInBoundingBoxes(targetLevel, dungeonData.getBoundingBoxes());
        freeDimension(dimensionIndex);
    }

    private void deleteBlocksInBoundingBoxes(ServerLevel level, List<BoundingBox> boundingBoxes) {
        if (boundingBoxes != null) {
            for (BoundingBox bBox : boundingBoxes) {
                Iterator<BlockPos> iterator = BlockPos.betweenClosed(bBox.minX(), bBox.minY(), bBox.minZ(), bBox.maxX(), bBox.maxY(), bBox.maxZ()).iterator();
                while(iterator.hasNext()) {
                    BlockPos blockPos = iterator.next();
                    level.removeBlock(blockPos, false); //?? I guess it works
                }
            }
        } else {
            DungeonsAndIngots.LOGGER.debug("Dungeon does not exist yet, so nothing was deleted.");
        }
    }

    public void sendPlayerToDungeon(Level sourceLevel, Player player, int dimensionIndex) {
        sendPlayerToDungeon(sourceLevel, player, 0, 0, 0, dimensionIndex);
    }

    public void sendPlayerToDungeon(Level sourceLevel, Player player, int x, int y, int z, int dimensionIndex) {
        MinecraftServer minecraftServer = sourceLevel.getServer();
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_" + Integer.toString(dimensionIndex));
        ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, resourceLocation);
        ServerLevel targetLevel = minecraftServer.getLevel(resourceKey);
        // you are going to brazil
        player.teleportTo(targetLevel, x, y, z, new HashSet<Relative>(), 0.0f, 0.0f, true);
    }

    private int reserveDimension() {
        for (int i = 0; i < dimensionInUse.length; i++) {
            if (!dimensionInUse[i]) {
                dimensionInUse[i] = true;
                return i;
            }
        }
        DungeonsAndIngots.LOGGER.error("Error: could not reserve a new dimension because the soft limit of dimensions has been reached.");
        return -1;
    }

    
    private boolean freeDimension(int index) {
        boolean oldValue = dimensionInUse[index];
        dimensionInUse[index] = false;
        return oldValue;
    }
}
