package com.jkkivenko.dungeonsandingots.item;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.block.ModBlocks;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

@SuppressWarnings("null")
public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "dungeonsandingots" namespace
    public static final Items ITEMS = DeferredRegister.createItems(DungeonsAndIngots.MOD_ID);

    // Make a couple of different tool types for use later.
    public static final ToolMaterial BONE = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1000, 2.0f, 0.0f, 1, ItemTags.DIAMOND_TOOL_MATERIALS);
    public static final ToolMaterial RUSTY = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1000, 3.0f, 0.0f, 1, ItemTags.DIAMOND_TOOL_MATERIALS);

    // Block items
    public static final DeferredItem<BlockItem> DUNGEON_PORTAL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_PORTAL_BLOCK);
    public static final DeferredItem<BlockItem> RITUAL_STONE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.RITUAL_STONE_BLOCK);
    public static final DeferredItem<BlockItem> DUNGEON_BRICK_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.DUNGEON_BRICK_BLOCK);

    // Non-block items
    public static final DeferredItem<Item> DUNGEON_KEY_1 = ITEMS.registerSimpleItem("dungeon_key_1");
    public static final DeferredItem<Item> WOLF_PELT = ITEMS.registerSimpleItem("wolf_pelt");
    public static final DeferredItem<Item> SERRATED_BLADE = ITEMS.registerSimpleItem(
        "serrated_blade",
        (props) -> props
        .sword(BONE, 6.0f, -2.4f)
    );
    public static final DeferredItem<Item> RUSTY_SWORD = ITEMS.registerSimpleItem(
        "rusty_sword",
        (props) -> props
        .sword(RUSTY, 5.0f, -2.4f)
    );
    public static final DeferredItem<Item> RUSTY_AXE = ITEMS.registerSimpleItem(
        "rusty_axe",
        (props) -> props
        .axe(RUSTY, 6.5f, -3.1f)
    );
}

/*
 * 
 */