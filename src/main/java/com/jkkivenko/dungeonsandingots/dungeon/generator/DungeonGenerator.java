package com.jkkivenko.dungeonsandingots.dungeon.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;

import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.JigsawBlockInfo;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/**
 * DungeonGenerator can be used to generate a hypothetical room layout for a dungeon.
 * A layout generated with DungeonGenerator.generate is valid, meaning that rooms are non-intersecting, there are no rooms with unused branches,
 * the number of rooms is within two given thresholds, and there is exactly one of each of each room in a given pool.
 */
@SuppressWarnings("null")
public class DungeonGenerator {

    private StructureTemplatePool startTemplatePool;
    private StructureTemplatePool regularTemplatePool;
    private StructureTemplatePool exactlyOneTemplatePool;
    private Registry<StructureTemplatePool> templateRegistry;
    private ServerLevel level;
    private RandomSource randomSource;
    private int minRooms;
    private int maxRooms;

    private StructureTemplateManager templateManager;
    private List<DungeonRoomData> rooms;

    public List<DungeonRoomData> getRooms() {
        return rooms;
    }

    public DungeonGenerator(ServerLevel level, StructureTemplatePool startTemplatePool, StructureTemplatePool regularTemplatePool, StructureTemplatePool exactlyOneTemplatePool, int minRooms, int maxRooms) {
        this.startTemplatePool = startTemplatePool;
        this.regularTemplatePool = regularTemplatePool;
        this.exactlyOneTemplatePool = exactlyOneTemplatePool;
        this.minRooms = minRooms;
        this.maxRooms = maxRooms;
        this.level = level;
        this.randomSource = level.getRandom();
        this.templateManager = level.getStructureManager();
        this.templateRegistry = level.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);

    }

    // Builds a hypothetical dungeon !
    public void generate() {
        // Create a list of dungeon rooms
        ArrayList<DungeonRoomData> generatedRooms = new ArrayList<>();
        // The start room. 
        DungeonRoomData startRoom = new DungeonRoomData(startTemplatePool.getRandomTemplate(randomSource), BlockPos.ZERO, Rotation.NONE);
        generatedRooms.add(startRoom);
        generateRecursive(startRoom, generatedRooms, null, 0);
        // generateRecursive will edit generatedRooms until we end up with a valid dungeon floorplan to return.
        this.rooms = generatedRooms;
    }

    public void place() {
        DungeonsAndIngots.LOGGER.debug("FINAL DUNGEON OF LENGTH " + Integer.toString(rooms.size()) + ":");
        int i = 0;
        for (DungeonRoomData roomData : this.rooms) {
            // Okay... brace yourself
            DungeonsAndIngots.LOGGER.debug("ROOM " + Integer.toString(i) + ": " + roomData.toString());
            i++;
            // Serious jank incoming
            // Just revoke my degree at this point...
            // I'm using toString to access a protected field because I'm a monster and I hate good code
            // I'm sorry you had to see this, dear reader.
            // Please forgive me...
            // I have brought great shame upon my family
            // DungeonsAndIngots.LOGGER.debug("Placing room " + roomData.toString());
            String structureName = roomData.element().toString().replace("Single[Left[" + DungeonsAndIngots.MOD_ID + ":", "").replace("]]", "");
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(DungeonsAndIngots.MOD_ID, structureName);
            StructureTemplate structureTemplate = templateManager.get(resourceLocation).get();
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(roomData.rotation());
            structureTemplate.placeInWorld(level, roomData.position(), roomData.position(), settings, randomSource, 0);
            // DungeonsAndIngots.LOGGER.debug("\tSTRUCTURE IS PLACED");
        }
    }

    private boolean generateRecursive(DungeonRoomData mostRecentRoom, ArrayList<DungeonRoomData> existingRooms, JigsawBlockInfo alreadyUsedJigsaw, int roomIndex) {
        // This is a DEPTH-FIRST algorithm, meaning it is very likely to create a very deep dungeon with minimal branching paths even if branches are very common in the room pool.
        // Counterintuitively, this can be mitigated(ish) by increasing the likelihood of rolling a termination room in the room pool, so dungeons are more likely to not hit maxRooms.
        // This isn't ideal, but how do you do a breadth-first generation and still maintian backtracking?
        // So for now this is like, fine.
        // Only YOU can stop infinite recursion!
        // DungeonsAndIngots.LOGGER.debug("NUMBER OF GENERATED ROOMS IS:");
        // DungeonsAndIngots.LOGGER.debug(Integer.toString(existingRooms.size()));
        if (existingRooms.size() > maxRooms) {
            // DungeonsAndIngots.LOGGER.debug("quitting because too many rooms :|");
            return false;
        }
        List<JigsawBlockInfo> jigsawsInThisRoom = mostRecentRoom.element().getShuffledJigsawBlocks(templateManager, BlockPos.ZERO, Rotation.NONE, randomSource);
        // DungeonsAndIngots.LOGGER.debug("ROOM IS:");
        // DungeonsAndIngots.LOGGER.debug(mostRecentRoom.toString());
        // Iterate over every jigsaw block in the most recent room
        for (JigsawBlockInfo jigsawInThisRoom : jigsawsInThisRoom) {
            // This is the pool that the jigsaw block in question is trying to pull from
            StructureTemplatePool jigsawInThisRoomTargetPool = templateRegistry.getOrThrow(jigsawInThisRoom.pool()).value();
            // If the jigsaw block has already been used or it's a mob-spawning jigsaw, just ignore it.
            // DungeonsAndIngots.LOGGER.debug(Integer.toString(alreadyUsedJigsaws.size()));
            if (jigsawEquals(alreadyUsedJigsaw, jigsawInThisRoom)) {
                // DungeonsAndIngots.LOGGER.debug("nathan nathan nathan. The following jigsaw block was skipped:");
                // DungeonsAndIngots.LOGGER.debug(jigsawInThisRoom.info().pos().toString());
                continue;
            }
            if (jigsawInThisRoomTargetPool != regularTemplatePool) {
                continue;
            }
            // Otherwise, we use it to generate a new room and add it to the array
            // First we get some information about this jigsaw block. This is done by getting the raw position and rotation data, then offsetting it so it lines up with the current room.
            BlockPos jigsawInThisRoomPosition = jigsawInThisRoom.info().pos().rotate(mostRecentRoom.rotation()).offset(mostRecentRoom.position());
            Rotation jigsawInThisRoomRotation = getRotationFromAngle(getAngleFromRotation(getRotationFromOrientation(jigsawInThisRoom.info().state().getValue(JigsawBlock.ORIENTATION))) + getAngleFromRotation(mostRecentRoom.rotation()));
            // Then we create a list of possible rooms to place
            List<StructurePoolElement> possibleRooms = jigsawInThisRoomTargetPool.getShuffledTemplates(randomSource);
            // DungeonsAndIngots.LOGGER.debug("\tTHIS ROOM HAS A JIGSAW AT POSITION:");
            // DungeonsAndIngots.LOGGER.debug(jigsawInThisRoomPosition.toString());
            // DungeonsAndIngots.LOGGER.debug("\tAND ROTATION:");
            // DungeonsAndIngots.LOGGER.debug(jigsawInThisRoomRotation.toString());

            // Then we iterate over the list of possible rooms
            boolean foundRoomToPlace = false;
            // Because of a quirk of StructureTemplatePool.getShuffledTemplates, it will often try the same room type multiple times. As an optimization,
            // we keep track of which room types we've already tried so we can skip them later, greatly reducing generation time (kinda)
            HashSet<StructurePoolElement> alreadyCheckedRoomTypes = new HashSet<>();
            for (StructurePoolElement possibleRoom : possibleRooms) {
                if (!alreadyCheckedRoomTypes.add(possibleRoom)) {
                    continue;
                }
                // DungeonsAndIngots.LOGGER.debug("ROOM NUMBER " + Integer.toString(roomIndex) + ", CHILD " + possibleRoom.toString());
                // DungeonsAndIngots.LOGGER.debug("        " + alreadyCheckedRoomTypes.toString());
                // DungeonsAndIngots.LOGGER.debug("I AM:");
                // DungeonsAndIngots.LOGGER.debug(mostRecentRoom.toString());
                // DungeonsAndIngots.LOGGER.debug("TRYING TO PLACE THIS ROOM:");
                // DungeonsAndIngots.LOGGER.debug(possibleRoom.toString());
                List<JigsawBlockInfo> allJigsawsInTargetRoom = possibleRoom.getShuffledJigsawBlocks(templateManager, BlockPos.ZERO, Rotation.NONE, randomSource);
                // Finally, iterate over all the jigsaws in the selected room to find one with a matching name, and use its rotation and offset to calculate room location and rotation
                for (JigsawBlockInfo jigsawInTargetRoom : allJigsawsInTargetRoom) {
                    // This checks if the jigsaw name is correct
                    if (jigsawInThisRoom.target().toString().equals(jigsawInTargetRoom.name().toString())) {
                        BlockPos jigsawInTargetRoomPosition = jigsawInTargetRoom.info().pos();
                        Rotation jigsawInTargetRoomRotation = getRotationFromOrientation(jigsawInTargetRoom.info().state().getValue(JigsawBlock.ORIENTATION));
                        // DungeonsAndIngots.LOGGER.debug("\t\t\tFOUND THAT IS HAS A MATCHING JIGSAW AT POSITION:");
                        // DungeonsAndIngots.LOGGER.debug(jigsawInTargetRoomPosition.toString());
                        // DungeonsAndIngots.LOGGER.debug("\t\t\tAND ROTATION:");
                        // DungeonsAndIngots.LOGGER.debug(jigsawInTargetRoomRotation.toString());
                        // DungeonsAndIngots.LOGGER.debug("\t\t\tALRIGHT, I'M GONNA ADD THAT ONE!");

                        // Now we calculate the new room's position and rotation based on the jigsaw block position and rotation
                        Rotation newRoomRotation = calculateRequiredRotation(jigsawInThisRoomRotation, jigsawInTargetRoomRotation);
                        // DungeonsAndIngots.LOGGER.debug("SOURCE JIGSAW IS " + jigsawInThisRoomRotation.toString() + ", TARGET JIGSAW IS " + jigsawInTargetRoomRotation.toString() + ", CALCULATED ROTATION IS " + newRoomRotation.toString());
                        BlockPos oneBlockForwardOffset = new BlockPos(0, 0, -1).rotate(jigsawInThisRoomRotation);
                        BlockPos newRoomPosition = jigsawInTargetRoomPosition.rotate(newRoomRotation).multiply(-1).offset(jigsawInThisRoomPosition).offset(oneBlockForwardOffset);
                        // DungeonsAndIngots.LOGGER.debug("NEW ROOM WILL BE PLACED AT:");
                        // DungeonsAndIngots.LOGGER.debug(newRoomPosition.toString());
                        // DungeonsAndIngots.LOGGER.debug("WITH ROTATION:");
                        // DungeonsAndIngots.LOGGER.debug(newRoomRotation.toString());

                        // Detect if this room overlaps with any other rooms. This prevents self-intersection, dead-ends, etc.
                        DungeonRoomData newRoom = new DungeonRoomData(possibleRoom, newRoomPosition, newRoomRotation);
                        boolean foundOverlap = false;
                        // int existingRoomNumber = 0; 
                        for (DungeonRoomData existingRoom : existingRooms) {
                            if (this.detectRoomOverlap(existingRoom, newRoom)) {
                                // DungeonsAndIngots.LOGGER.debug("I AM ROOM " + Integer.toString(roomIndex) + " " + mostRecentRoom.element().toString() + " AND I COULD NOT PLACE " + possibleRoom.toString());
                                // DungeonsAndIngots.LOGGER.debug("\tBECAUSE IT OVERLAPPED WITH ROOM " + Integer.toString(existingRoomNumber) + " " + existingRoom.element().toString());
                                // DungeonsAndIngots.LOGGER.debug("\tBTW, THERE ARE " + Integer.toString(existingRooms.size()) + " ROOMS IN THE ARRAY");
                                foundOverlap = true;
                                break;
                            }
                            // existingRoomNumber++;
                        }
                        // If an overlap is detected, try a different orientation of the target room. Maybe it's a 2x1 room or something, you don't know.
                        if (foundOverlap) {
                            continue;
                        }
                        // Create new temporary arrays during the generation step in case we have to backtrack from this room
                        ArrayList<DungeonRoomData> hypotheticalFutureDungeonRooms = new ArrayList<>(existingRooms);
                        hypotheticalFutureDungeonRooms.add(newRoom);
                        // Now we do the recursive step! If it returns true, we concrete-ify the temporary lists and stop trying to find matching jigsaws
                        // We actually want break out of the next for loop too, since we've definitely decided on which room we're placing
                        if (generateRecursive(newRoom, hypotheticalFutureDungeonRooms, jigsawInTargetRoom, roomIndex + 1)) {
                            // Ensures that we don't start accepting branches until we hit a minimum number of rooms, which guarantees dungeon size and length.
                            if (hypotheticalFutureDungeonRooms.size() < minRooms) {
                                // Should always print that the room is "dungeonsandingots:dungeon_1/layer_1/start"
                                // DungeonsAndIngots.LOGGER.debug(newRoom.toString() + " I AM BREAKING BECAUSE WE DIDN'T HIT MINIMUM ROOM LIMIT~~~~~~~~~");
                                break;
                            }
                            // DungeonsAndIngots.LOGGER.debug("REACHED THE END OF A BRANCH, NOTE THAT IN YOUR LOG");
                            existingRooms.clear();
                            existingRooms.addAll(hypotheticalFutureDungeonRooms);
                            foundRoomToPlace = true;
                            break;
                        }
                    }
                }
                if (foundRoomToPlace) {
                    break;
                }
            }
            // This means "I tried every possible room, and none of them found a valid placement, so clearly this path is a dead end and a problem!"
            if (!foundRoomToPlace) {
                return false;
            }
        }
        // If we reached the end of the room's jigsaws without encountering a problem, that means that every jigsaw in this room leads eventually leads to a dead end (which is good!)
        // It could also mean that this room has not jigsaws, which is fine.
        return true;
    }

    private static boolean jigsawEquals(JigsawBlockInfo jigsaw1, JigsawBlockInfo jigsaw2) {
        // Are you proud of me, Prof. Ma?
        if (jigsaw1 == jigsaw2) {
            return true;
        }
        if (jigsaw1 == null || jigsaw2 == null) {
            return false;
        }
        BlockPos jigsaw1BlockPos = jigsaw1.info().pos();
        BlockPos jigsaw2BlockPos = jigsaw2.info().pos();
        int x1 = jigsaw1BlockPos.getX();
        int y1 = jigsaw1BlockPos.getY();
        int z1 = jigsaw1BlockPos.getZ();
        int x2 = jigsaw2BlockPos.getX();
        int y2 = jigsaw2BlockPos.getY();
        int z2 = jigsaw2BlockPos.getZ();
        if (x1 == x2 && y1 == y2 && z1 == z2) {
            return true;
        }
        return false;
    }

    private static Rotation getRotationFromOrientation(FrontAndTop orientation) {
        Rotation rotation;
        if (orientation == FrontAndTop.NORTH_UP) {
            rotation = Rotation.NONE;
        } else if (orientation == FrontAndTop.EAST_UP) {
            rotation = Rotation.CLOCKWISE_90;
        } else if (orientation == FrontAndTop.SOUTH_UP) {
            rotation = Rotation.CLOCKWISE_180;
        } else {
            rotation = Rotation.COUNTERCLOCKWISE_90;
        }
        return rotation;
    }

    private static Rotation calculateRequiredRotation(Rotation sourceRotation, Rotation rotationToBeRotated) {
        int requiredRotationAngle = getAngleFromRotation(sourceRotation) - getAngleFromRotation(rotationToBeRotated) + 180;
        return getRotationFromAngle(requiredRotationAngle);
    }

    private static int getAngleFromRotation(Rotation rot) {
        if (rot == Rotation.NONE) {
            return 0;
        } else if (rot == Rotation.CLOCKWISE_90) {
            return 90;
        } else if (rot == Rotation.CLOCKWISE_180) {
            return 180;
        } else {
            return -90;
        }
    }

    private static Rotation getRotationFromAngle(int angle) {
        int modAngle = ((angle % 360 + 360) % 360); // have to do this because java mod operator is stupid and bad
        if (modAngle == 0) {
            return Rotation.NONE;
        } else if (modAngle == 90) {
            return Rotation.CLOCKWISE_90;
        } else if (modAngle == 180) {
            return Rotation.CLOCKWISE_180;
        } else if (modAngle == 270) {
            return Rotation.COUNTERCLOCKWISE_90;
        } else {
            DungeonsAndIngots.LOGGER.error("Invalid argument for getRotationFromAngle. You can only pass multiples of 90 but you passed " + Integer.toString(angle) + " which was interpreted as " + Integer.toString(modAngle));
            return Rotation.NONE;
        }
    }

    // Can't be static because of templateManager, so jot that down.
    private boolean detectRoomOverlap(DungeonRoomData room1, DungeonRoomData room2) {
        BoundingBox room1BoundingBox = room1.element().getBoundingBox(templateManager, room1.position(), room1.rotation());
        BoundingBox room2BoundingBox = room2.element().getBoundingBox(templateManager, room2.position(), room2.rotation());
        return room1BoundingBox.intersects(room2BoundingBox);
    }
}
