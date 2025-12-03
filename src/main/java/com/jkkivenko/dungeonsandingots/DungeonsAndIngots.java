package com.jkkivenko.dungeonsandingots;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.jkkivenko.dungeonsandingots.block.ModBlocks;
import com.jkkivenko.dungeonsandingots.item.ModItems;
import com.jkkivenko.dungeonsandingots.loot.ModLootModifiers;
import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(DungeonsAndIngots.MOD_ID)
@SuppressWarnings("null")
public class DungeonsAndIngots {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "dungeonsandingots";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final Supplier<CreativeModeTab> DUNGEONS_AND_INGOTS_TAB = CREATIVE_MODE_TABS.register("dungeons_and_ingots_tab", () -> CreativeModeTab.builder()
        .icon(() -> new ItemStack(ModItems.DUNGEON_PORTAL_BLOCK_ITEM.get()))
        .title(Component.translatable("creativetab.dungeonsandingots"))
        .displayItems((itemDisplayParameters, output) -> {
            output.accept(ModItems.DUNGEON_PORTAL_BLOCK_ITEM);
            output.accept(ModItems.RITUAL_STONE_BLOCK_ITEM);
            output.accept(ModItems.DUNGEON_KEY_1);
            output.accept(ModItems.SERRATED_BLADE);
            output.accept(ModItems.RUSTY_SWORD);
            output.accept(ModItems.RUSTY_AXE);
            output.accept(ModItems.WOLF_PELT);
            output.accept(ModItems.DUNGEON_BRICK_BLOCK_ITEM);
        })
        .build()
    );

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public DungeonsAndIngots(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so blocks get registered
        ModBlocks.BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so changes to loot tables get registered
        ModLootModifiers.GLOBAL_LOOT_MODIFIER_SERIALIZERS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (DungeonsandIngots) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
        }

        LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

        Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
