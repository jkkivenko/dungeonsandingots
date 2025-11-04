package com.jkkivenko.dungeonsandingots.item;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.jkkivenko.dungeonsandingots.block.ModBlocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

@SuppressWarnings("null")
public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "dungeonsandingots" namespace
    public static final Items ITEMS = DeferredRegister.createItems(DungeonsAndIngots.MOD_ID);

    // Creates a new BlockItem with the id "dungeonsandingots:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> DUNGEON_PORTAL_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("dungeon_portal_block", ModBlocks.DUNGEON_PORTAL_BLOCK);

    public static final DeferredItem<Item> DUNGEON_KEY_1 = ITEMS.registerSimpleItem("dungeon_key_1");
}

/*
 * Dungeon Key 1
 * Wolf Pelt
 * Wolf Tooth
 * Rusty Sword
 * Rusty Arrow
 * Tooth Arrow
 * Hide Cap
 * Hide Tunic
 * Hide Pants
 * Hide Boots
 * Serrated Blade
 * Wild Spellbook
 * Ectoplasm
 * Haunted Cloth
 * Bone Shard
 * Undead Blade
 * Haunted Robes
 * Drew's Trident (or maybe just make this a regular trident with enchantments)
 * Haunted Headscarf
 * Haunted Spellbook
 * Chipped Axe
 * Haunted Crosbow (or maybe just make this a regular crossbow with enchantments)
 * Femur
 * Bone Club
 * Rotten Heart
 * Decaying Energy
 * Spine Whip
 * 
 */