package com.jkkivenko.dungeonsandingots;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

@SuppressWarnings("null")
public class DungeonData extends SavedData {

    private String boundingBoxesStringified;
    private String nothingIdk = ""; // UNUSED. I'm sorry I couldn't find out how to store only one string... I am maybe stupid

    public String getBoundingBoxesStringified() {
        return boundingBoxesStringified;
    }
    
    public void setBoundingBoxesStringified(String boundingBoxesStringified) {
        this.boundingBoxesStringified = boundingBoxesStringified;
        this.setDirty();
    }

    public List<BoundingBox> getBoundingBoxes() {
        return listifyString(boundingBoxesStringified);
    }

    public void setBoundingBoxes(List<BoundingBox> boundingBoxes) {
        this.boundingBoxesStringified = stringifyList(boundingBoxes);
        this.setDirty();
    }


    public DungeonData() {
        // Intentionally empty. Probably.
    }
    
    public DungeonData(List<BoundingBox> boundingBoxes) {
        this.boundingBoxesStringified = stringifyList(boundingBoxes);
    }

    public DungeonData(String boundingBoxesStringified, String nothingIdk) {
        this.boundingBoxesStringified = boundingBoxesStringified;
    }

    public static final SavedDataType<DungeonData> ID = new SavedDataType<>(
        // The identifier of the saved data
        // Used as the path within the level's `data` folder
        "dungeon_saved_data",
        // The initial constructor
        DungeonData::new,
        // The codec used to serialize the data
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("boundingBoxesStringified").forGetter(sd -> sd.boundingBoxesStringified),
            Codec.STRING.fieldOf("nothingIdk").forGetter(sd -> sd.nothingIdk)
        ).apply(instance, DungeonData::new))

    );

    private String stringifyList(List<BoundingBox> boundingBoxes) {
        String str = "";
        for (BoundingBox boundingBox : boundingBoxes) {
            str += stringifyBoundingBox(boundingBox) + "#####";
        }
        return str;
    }

    private String stringifyBoundingBox(BoundingBox boundingBox) {
        return boundingBox.minX() + ", " + 
        boundingBox.minY() + ", " + 
        boundingBox.minZ() + ", " + 
        boundingBox.maxX() + ", " + 
        boundingBox.maxY() + ", " + 
        boundingBox.maxZ();
    }

    private List<BoundingBox> listifyString(String boundingBoxesString) {
        if (boundingBoxesString != null) {
            List<BoundingBox> l = new ArrayList<>();
            for (String str : boundingBoxesString.split("#####")) {
                l.add(boundingBoxifyString(str));
            }
            return l;
        }
        return null;
    }

    private BoundingBox boundingBoxifyString(String boundingBoxString) {
        if (boundingBoxString != null) {
            String[] coordinates = boundingBoxString.split(", ");
            return new BoundingBox(
                Integer.parseInt(coordinates[0]),
                Integer.parseInt(coordinates[1]),
                Integer.parseInt(coordinates[2]),
                Integer.parseInt(coordinates[3]),
                Integer.parseInt(coordinates[4]),
                Integer.parseInt(coordinates[5]));
        }
        return null;
    }
}
