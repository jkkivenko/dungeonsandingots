package com.jkkivenko.dungeonsandingots.dungeon;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.DungeonsAndIngotsSavedData;
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
public class DungeonManager {

    private static final int DUNGEON_1_MIN_ROOMS = 10;
    private static final int DUNGEON_1_MAX_ROOMS = 25;

    public static void generateAndSendPlayerToDungeon(Player player, String dungeonName) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, dungeonName);
        ResourceKey<Level> resourceKey = ResourceKey.create(Registries.DIMENSION, resourceLocation);
        // Gets the origin server. 
        Level level = player.level();
        // This method is meant to be executed on the server side only.
        if (!level.isClientSide()){
            // Gets the ServerLevel corresponding to the target dimension, not the current dimension
            MinecraftServer minecraftServer = level.getServer();
            ServerLevel targetLevel = minecraftServer.getLevel(resourceKey);
            // Sends the player to the dungeon. We do this early to display the "Generating Terrain..." screen while the dungeon generates.
            sendPlayerToDungeon(player, 0, 8, 0, targetLevel);
            // Delete the old dungeon by iterating through its bounding boxes.
            DungeonsAndIngotsSavedData dnisd = targetLevel.getDataStorage().computeIfAbsent(DungeonsAndIngotsSavedData.ID);
            deleteDungeon(targetLevel, dnisd.getBoundingBoxes());
            // Now it's time to generate the dungeon. First we get references to the TemplatePool objects.
            Registry<StructureTemplatePool> templateRegistry = targetLevel.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
            // This pool is for possible starting rooms.
            ResourceLocation startPoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_1/layer_1/start");
            ResourceKey<StructureTemplatePool> startPoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, startPoolResourceLocation);
            StructureTemplatePool startPool = templateRegistry.getOrThrow(startPoolResourceKey).value();
            // This pool is for regular rooms.
            ResourceLocation regularPoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_1/layer_1/all");
            ResourceKey<StructureTemplatePool> regularPoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, regularPoolResourceLocation);
            StructureTemplatePool regularPool = templateRegistry.getOrThrow(regularPoolResourceKey).value();
            // This pool is for rooms that must only appear once, like ending rooms.
            ResourceLocation exactlyOnePoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_1/layer_1/end");
            ResourceKey<StructureTemplatePool> exactlyOnePoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, exactlyOnePoolResourceLocation);
            StructureTemplatePool exactlyOnePool = templateRegistry.getOrThrow(exactlyOnePoolResourceKey).value();
            // This pool is for rooms that must appear at least once but can appear multiple times, like checkpoint rooms.
            ResourceLocation oneOrMorePoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_1/layer_1/one_or_more");
            ResourceKey<StructureTemplatePool> oneOrMorePoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, oneOrMorePoolResourceLocation);
            StructureTemplatePool oneOrMorePool = templateRegistry.getOrThrow(oneOrMorePoolResourceKey).value();
            // Actually generates the jigsaw
            List<BoundingBox> boundingBoxes = generateDungeon(targetLevel, startPool, regularPool, oneOrMorePool, exactlyOnePool, DUNGEON_1_MIN_ROOMS, DUNGEON_1_MAX_ROOMS);
            dnisd.setBoundingBoxes(boundingBoxes);
            // Once this finishes, the CPU is freed up, the teleport finishes, and the "Generating Terrain..." screen disappears.
        }
    }

    private static List<BoundingBox> generateDungeon(ServerLevel targetLevel, StructureTemplatePool startPool, StructureTemplatePool regularPool, StructureTemplatePool oneOrMoreTemplatePool, StructureTemplatePool exactlyOnePool, int minRooms, int maxRooms) {
        DungeonGenerator dungeonGenerator = new DungeonGenerator(targetLevel, startPool, regularPool, oneOrMoreTemplatePool, exactlyOnePool, minRooms, maxRooms);
        dungeonGenerator.generate();
        dungeonGenerator.place();
        return dungeonGenerator.getRoomBoundingBoxes();
    }

    private static void deleteDungeon(ServerLevel level, List<BoundingBox> boundingBoxes) {
        if (boundingBoxes != null) {
            for (BoundingBox bBox : boundingBoxes) {
                Iterator<BlockPos> iterator = BlockPos.betweenClosed(bBox.minX(), bBox.minY(), bBox.minZ(), bBox.maxX(), bBox.maxY(), bBox.maxZ()).iterator();
                while(iterator.hasNext()) {
                    BlockPos blockPos = iterator.next();
                    level.removeBlock(blockPos, false); //??
                }
            }
        } else {
            DungeonsAndIngots.LOGGER.debug("Dungeon does not exist yet, so nothing was deleted.");
        }
    }

    private static void sendPlayerToDungeon(Player player, double x, double y, double z, ServerLevel targetLevel) {
        // Sending you to brazil
        player.teleportTo(targetLevel, x, y, z, new HashSet<Relative>(), 0.0f, 0.0f, true);
    }
}
