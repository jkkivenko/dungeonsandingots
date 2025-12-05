package com.jkkivenko.dungeonsandingots;

import com.jkkivenko.dungeonsandingots.dungeon.DimensionManager;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = DungeonsAndIngots.MOD_ID)
public class DungeonsAndIngotsEventHandler {
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        DimensionManager dimensionManager = DimensionManager.getInstance();
        dimensionManager.onPlayerRespawn(player);
        // TODO: see DungeonPortalBlockEntity.
    }
}
