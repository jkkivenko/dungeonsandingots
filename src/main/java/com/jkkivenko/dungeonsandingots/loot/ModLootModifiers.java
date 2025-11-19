package com.jkkivenko.dungeonsandingots.loot;

import java.util.function.Supplier;

import com.jkkivenko.dungeonsandingots.DungeonsAndIngots;
import com.mojang.serialization.MapCodec;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@SuppressWarnings("null")
public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DungeonsAndIngots.MOD_ID);

    public static final Supplier<MapCodec<AddItemModifier>> ADD_ITEM =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("add_item", () -> AddItemModifier.CODEC);
}