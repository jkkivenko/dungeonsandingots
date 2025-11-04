package com.jkkivenko.dungeonsandingots.dungeon;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.DungeonsAndIngotsSavedData;
import com.jkkivenko.dungeonsandingots.dungeon.tree.DungeonTree;

import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;

@SuppressWarnings("null")
public class DungeonManager {

    private static final int DUNGEON_1_MAX_DEPTH = 15;

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
            // A jigsaw in the same chunk will always generate the same dungeon. To fix this, we pick a random chunk out of a 128 chunk area,
            // and start the dungeon in that area. That way we have 16384 possible dungeons, whiuch is hopefully enough.
            // The fact that we're using targetLevel.getRandom() is irrelevant. I just chose it because it's an easily-accessible RandomSource.
            RandomSource randomSource = targetLevel.getRandom();
            int chunkX = randomSource.nextIntBetweenInclusive(-64, 64) * 16;
            int chunkZ = randomSource.nextIntBetweenInclusive(-64, 64) * 16;
            BlockPos generationPos = new BlockPos(chunkX, 64, chunkZ);
            // Sends the player to the dungeon. We do this early to display the "Generating Terrain..." screen while the dungeon generates.
            sendPlayerToDungeon(player, chunkX, 66, chunkZ, targetLevel);
            // Clears out the old dungeon. Realistically, this should scan somehow for the outside of the structure.
            // Instead, we just retrieve the old dungeon from data storage and delete it.
            DimensionDataStorage dungeonDataStorage = targetLevel.getDataStorage();
            DungeonsAndIngotsSavedData dnisd;
            dnisd = dungeonDataStorage.computeIfAbsent(DungeonsAndIngotsSavedData.ID);
            int oldChunkX = dnisd.getOldChunkX(); 
            int oldChunkZ = dnisd.getOldChunkZ();
            deleteDungeon(targetLevel, new BlockPos(oldChunkX, 72, oldChunkZ), 200, 17);
            // After that's done, we update the data storage to represent the new values for next time.
            dnisd.setOldChunkX(chunkX);
            dnisd.setOldChunkZ(chunkZ);
            // Time to generate the jigsaw.
            // First, gets a reference to the target jigsaw. This is the "target pool" field in a jigsaw block
            ResourceLocation poolResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "dungeon_1/layer_1/start");
            ResourceKey<StructureTemplatePool> poolResourceKey = ResourceKey.create(Registries.TEMPLATE_POOL, poolResourceLocation);
            Registry<StructureTemplatePool> templateRegistry = targetLevel.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
            Holder<StructureTemplatePool> poolHolder = templateRegistry.getOrThrow(poolResourceKey);
            StructureTemplatePool pool = poolHolder.value();
            // this is the "target name" field in a jigsaw block
            ResourceLocation targetResourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, "jig");
            // Actually generates the jigsaw;
            // TODO: REMOVE ME!
            JigsawPlacement.generateJigsaw(targetLevel, poolHolder, targetResourceLocation, DUNGEON_1_MAX_DEPTH, generationPos, false);
            generateDungeon(targetLevel, pool, targetResourceLocation, DUNGEON_1_MAX_DEPTH, chunkX, chunkZ);
            // Once this finishes, the CPU is freed up, the teleport finishes, and the "Generating Terrain..." screen disappears.
        }
    }

    private static void generateDungeon(ServerLevel targetLevel, StructureTemplatePool pool, ResourceLocation targetResourceLocation, int maxDepth, int x, int z) {
        // So, we need to create the DungeonTree.
        DungeonTree tree = new DungeonTree(pool);
        tree.generate(maxDepth);
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
