package com.gizmo.tomes.handler;

import com.gizmo.tomes.AddTomeLootModifier;
import com.gizmo.tomes.MysticTomes;
import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Optional;
import java.util.function.Function;

public class LootPackHandler {

	public static void generateDefaultLootPack(GatherDataEvent event) {
		DataGenerator.PackGenerator loot = event.getGenerator().getPackGenerator(true, MysticTomes.MODID, "datapack/default_loot");

		loot.addProvider(output -> new GlobalLootModifierProvider(output, event.getLookupProvider(), MysticTomes.MODID) {

			@Override
			protected void start() {
				Function<ResourceKey<LootTable>, LootItemCondition[]> conditions = table -> new LootItemCondition[]{LootTableIdCondition.builder(table.location()).build()};
				var lookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

				this.add("ancient_city", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.ANCIENT_CITY), HolderSet.direct(lookup.getOrThrow(Enchantments.SWIFT_SNEAK)), 0.25F));
				this.add("bastion", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.BASTION_OTHER), HolderSet.direct(lookup.getOrThrow(Enchantments.SOUL_SPEED)), 0.25F));
				this.add("buried_treasure", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.BURIED_TREASURE), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 1.0F));
				this.add("desert_pyramid", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.DESERT_PYRAMID), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 0.25F));
				this.add("dungeon", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.SIMPLE_DUNGEON), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 0.25F));
				this.add("end_city", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.END_CITY_TREASURE), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 1.0F));
				this.add("jungle_temple", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.JUNGLE_TEMPLE), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 0.25F));
				this.add("mansion", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.WOODLAND_MANSION), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 0.5F));
				this.add("mineshaft", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.ABANDONED_MINESHAFT), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 0.25F));
				this.add("outpost", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.PILLAGER_OUTPOST), HolderSet.direct(
					lookup.getOrThrow(Enchantments.PIERCING),
					lookup.getOrThrow(Enchantments.POWER),
					lookup.getOrThrow(Enchantments.PUNCH),
					lookup.getOrThrow(Enchantments.PROJECTILE_PROTECTION)), 0.25F));
				this.add("shipwreck", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.SHIPWRECK_TREASURE), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 0.25F));
				this.add("stronghold", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.STRONGHOLD_LIBRARY), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 0.25F));
				this.add("trial_chamber_normal", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.TRIAL_CHAMBERS_REWARD), lookup.getOrThrow(EnchantmentTags.ON_RANDOM_LOOT), 0.25F));
				this.add("trial_chamber_ominous", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS),
					Optional.empty(),
					Optional.of(HolderSet.direct(lookup.getOrThrow(Enchantments.SOUL_SPEED), lookup.getOrThrow(Enchantments.SWIFT_SNEAK))), 1.0F));
				this.add("underwater_ruin", new AddTomeLootModifier(conditions.apply(BuiltInLootTables.UNDERWATER_RUIN_BIG), HolderSet.direct(
					lookup.getOrThrow(Enchantments.RESPIRATION),
					lookup.getOrThrow(Enchantments.LUCK_OF_THE_SEA),
					lookup.getOrThrow(Enchantments.LURE),
					lookup.getOrThrow(Enchantments.LOYALTY),
					lookup.getOrThrow(Enchantments.RIPTIDE),
					lookup.getOrThrow(Enchantments.IMPALING)), 0.25F));
			}
		});

		loot.addProvider(output -> new PackMetadataGenerator(output).add(PackMetadataSection.TYPE, new PackMetadataSection(Component.translatable("datapack.mystictomes.default_loot.description"), DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA))));
	}
}
