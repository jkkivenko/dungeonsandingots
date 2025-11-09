package com.jkkivenko.dungeonsandingots.dungeon;

import java.util.HashSet;
import java.util.Iterator;
import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.dungeon.generator.DungeonGenerator;
import net.minecraft.commands.arguments.blocks.BlockInput;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
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
            // Clears out the old dungeon. Realistically, this should scan somehow for the outside of the structure.
            // Instead, we just clear a 200-wide area around (0,0)
            // TODO: Fix deleting the dungeon. Why does it do nothing????
            deleteDungeon(targetLevel, new BlockPos(0, 17, 0), 200, 17);
            // Time to generate the dungeon. First we get references to the TemplatePool objects.
            Registry<StructureTemplatePool> templateRegistry = targetLevel.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
            // This pool is for possible starting rooms
            ResourceLocation startPoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_1/layer_1/start");
            ResourceKey<StructureTemplatePool> startPoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, startPoolResourceLocation);
            StructureTemplatePool startPool = templateRegistry.getOrThrow(startPoolResourceKey).value();
            // This pool is for regular rooms
            ResourceLocation regularPoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_1/layer_1/all");
            ResourceKey<StructureTemplatePool> regularPoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, regularPoolResourceLocation);
            StructureTemplatePool regularPool = templateRegistry.getOrThrow(regularPoolResourceKey).value();
            // This pool is for rooms that must only appear once, like ending rooms
            ResourceLocation exactlyOnePoolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_1/layer_1/end");
            ResourceKey<StructureTemplatePool> exactlyOnePoolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, exactlyOnePoolResourceLocation);
            StructureTemplatePool exactlyOnePool = templateRegistry.getOrThrow(exactlyOnePoolResourceKey).value();

            // Actually generates the jigsaw
            // JigsawPlacement.generateJigsaw(targetLevel, poolHolder, targetName, DUNGEON_1_MAX_DEPTH, generationPos, false);
            // SinglePoolElement
            generateDungeon(targetLevel, startPool, regularPool, exactlyOnePool, DUNGEON_1_MIN_ROOMS, DUNGEON_1_MAX_ROOMS);
            // Once this finishes, the CPU is freed up, the teleport finishes, and the "Generating Terrain..." screen disappears.
        }
    }

    private static void generateDungeon(ServerLevel targetLevel, StructureTemplatePool startPool, StructureTemplatePool regularPool, StructureTemplatePool exactlyOnePool, int minRooms, int maxRooms) {
        DungeonGenerator dungeonGenerator = new DungeonGenerator(targetLevel, startPool, regularPool, exactlyOnePool, minRooms, maxRooms);
        dungeonGenerator.generate();
        dungeonGenerator.place();
    }

    private static void deleteDungeon(ServerLevel level, BlockPos center, int xzDistance, int yDistance) {
        // Create an iterator to iterate over all blocks in the area
        Iterator<BlockPos> blockIterator = BlockPos.withinManhattan(center, xzDistance, xzDistance, yDistance).iterator();
        // Creating a BlockState object is like impossible, so instead just copy the BlockState of a known block of air. This is stupid.
        BlockState airBlockState = level.getBlockState(new BlockPos(-10, -10, -10));
        // Creates a new BlockInput that represents a block of air with no properties...?
        BlockInput airBlockInput = new BlockInput(airBlockState, new HashSet<Property<?>>(), null);
        // Iterate over every block and replace them with the air block
        while(blockIterator.hasNext()) {
            BlockPos blockPos = blockIterator.next();
            airBlockInput.place(level, blockPos, 258); // what is 258? nobody knows!
        }
    }

    private static void sendPlayerToDungeon(Player player, double x, double y, double z, ServerLevel targetLevel) {
        // Sending you to brazil
        player.teleportTo(targetLevel, x, y, z, new HashSet<Relative>(), 0.0f, 0.0f, true);
    }
}
