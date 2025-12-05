package com.jkkivenko.dungeonsandingots.blockentity;

import com.jkkivenko.dungeonsandingots.dungeon.DimensionManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@SuppressWarnings("null")
public class DungeonPortalBlockEntity extends BaseContainerBlockEntity {
    
    // no idea if this is right
    private static final Component DEFAULT_NAME = Component.translatable("dungeonsandingots:container.dungeon_portal"); 

    private NonNullList<ItemStack> items;
    private int dimensionIndex = -1;
    private int dungeonTypeIndex = -1;

    private int numPlayersInDungeon = 0;

    public DungeonPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_PORTAL_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean activateWithKey(Player player, int dungeonTypeIndex) {  

        DimensionManager dimensionManager = DimensionManager.getInstance();
        Level sourceLevel = player.level();

        // There are three cases that can happen when you activate a portal block with a key.
        // 1. The portal block is not currently hosting a dimension, so you're free to use it.
        // 2. The portal block is currently hosting a dimension, but it's the same dungeon type as the key you're using so you can join in.
        // 3. The portal block is currently hosting a dimension and it's not the same one you want to join.
        if (this.dungeonTypeIndex == -1) {
            // This is case #1, so we spin up a new dimension from the DimensionManager.
            this.dungeonTypeIndex = dungeonTypeIndex;
            this.dimensionIndex = dimensionManager.generateDungeon(sourceLevel, this.dungeonTypeIndex, 0);
            dimensionManager.sendPlayerToDungeon(sourceLevel, player, this.dimensionIndex);
            numPlayersInDungeon++;
            // TODO: Save this block position in the player (ala spawnpoint) so we can do stuff when the player respawns (maybe do that inside sendPlayerToDungeon)
        } else if (this.dungeonTypeIndex == dungeonTypeIndex) {
            // This is case #2, where the dungeon already exists and we just have to send the player to it.
            dimensionManager.sendPlayerToDungeon(sourceLevel, player, this.dimensionIndex);
            numPlayersInDungeon++;
        } else {
            // This is case #3, where the wrong dungeon already exists so we can't do anything.
            return false;
        }

        return true;
    }

    // TODO: actually call this function, see above. Also need to change DungeonsAndIngotsEventHandler somehow. Not sure how to do that.
    public void notifyPlayerExited(Level sourceLevel) {
        numPlayersInDungeon--;
        if (numPlayersInDungeon == 0) {
            DimensionManager.getInstance().deleteDungeon(sourceLevel, this.dimensionIndex);
            this.dimensionIndex = -1;
            this.dungeonTypeIndex = -1;
        }
    }




    
    // Container stuff. I hate looking at it.
    // TODO: Serialize dimensionIndex, dungeonTypeIndex, and numPlayersInDungeon

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
    }
    
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }
    
    @Override
    public int getContainerSize(){
        return 54;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return ChestMenu.fiveRows(id, player);
    }
}
