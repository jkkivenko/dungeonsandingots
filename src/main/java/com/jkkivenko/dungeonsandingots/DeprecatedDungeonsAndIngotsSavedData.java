package com.jkkivenko.dungeonsandingots;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

@SuppressWarnings("null")
public class DeprecatedDungeonsAndIngotsSavedData extends SavedData {

    private int oldChunkX;
    private int oldChunkZ;

    public void setOldChunkX(int oldChunkX) {
        this.oldChunkX = oldChunkX;
        setDirty();
    }

    public void setOldChunkZ(int oldChunkZ) {
        this.oldChunkZ = oldChunkZ;
        setDirty();
    }

    public int getOldChunkX() {
        return oldChunkX;
    }

    public int getOldChunkZ() {
        return oldChunkZ;
    }

    public DeprecatedDungeonsAndIngotsSavedData() {
        // Intentionally empty. Probably.
    }

    public DeprecatedDungeonsAndIngotsSavedData(int oldChunkX, int oldChunkZ) {
        this.oldChunkX = oldChunkX;
        this.oldChunkZ = oldChunkZ;
    }

    public static final SavedDataType<DeprecatedDungeonsAndIngotsSavedData> ID = new SavedDataType<>(
        // The identifier of the saved data
        // Used as the path within the level's `data` folder
        "important_words_wiuth_more_words",
        // The initial constructor
        DeprecatedDungeonsAndIngotsSavedData::new,
        // The codec used to serialize the data
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("oldChunkX").forGetter(DeprecatedDungeonsAndIngotsSavedData::getOldChunkX),
            Codec.INT.fieldOf("oldChunkZ").forGetter(DeprecatedDungeonsAndIngotsSavedData::getOldChunkZ)
        ).apply(instance, DeprecatedDungeonsAndIngotsSavedData::new))
    );

}
