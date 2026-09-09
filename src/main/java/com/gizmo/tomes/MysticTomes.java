package com.gizmo.tomes;

import com.gizmo.tomes.handler.AnvilHandler;
import com.gizmo.tomes.handler.ClientLootPackHandler;
import com.gizmo.tomes.handler.LootPackHandler;
import com.gizmo.tomes.handler.TradeHandler;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MysticTomes.MODID)
public class MysticTomes {

	public static final String MODID = "mystictomes";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final TagKey<Enchantment> ALLOWED_ON_TOMES = TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(MODID, "allowed_on_tomes"));
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	public static final DeferredItem<Item> MYSTIC_TOME = ITEMS.register("mystic_tome", () -> new MysticTomeItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY.withTooltip(false)).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddTomeLootModifier>> ADD_TOME = LOOT_MODIFIERS.register("add_mystic_tome", () -> AddTomeLootModifier.CODEC);

	public MysticTomes(IEventBus bus, ModContainer container, Dist dist) {
		container.registerConfig(ModConfig.Type.COMMON, MysticTomesConfig.CONFIG_SPEC);
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);

		ITEMS.register(bus);
		LOOT_MODIFIERS.register(bus);

		bus.addListener(BuildCreativeModeTabContentsEvent.class, event -> {
			if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) {
				event.getParameters().holders().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ALLOWED_ON_TOMES).forEach(enchantment ->
					event.accept(MysticTomeItem.getEnchantedItemStack(enchantment)));
			}
		});

		bus.addListener(AddPackFindersEvent.class, event -> event.addPackFinders(
			ResourceLocation.fromNamespaceAndPath(MODID, "datapack/default_loot"),
			PackType.SERVER_DATA,
			Component.literal("Default Loot Injections"),
			PackSource.WORLD,
			false,
			Pack.Position.TOP)
		);
		bus.addListener(LootPackHandler::generateDefaultLootPack);

		if (dist.isClient()) {
			NeoForge.EVENT_BUS.addListener(ClientLootPackHandler::autoSelectLootPack);
		}
		NeoForge.EVENT_BUS.addListener(TradeHandler::addTradesToLibrarians);
		NeoForge.EVENT_BUS.addListener(TradeHandler::addWildcardTomeToWanderingTrader);

		NeoForge.EVENT_BUS.addListener(AnvilHandler::applyTomesToEnchantments);
		NeoForge.EVENT_BUS.addListener(AnvilHandler::combineTomes);
	}
}
