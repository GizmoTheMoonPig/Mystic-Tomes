package com.gizmo.tomes;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import java.util.List;

@Mod(MysticTomes.MODID)
public class MysticTomes {

	public static final String MODID = "mystictomes";

	public static final TagKey<Enchantment> ALLOWED_ON_TOMES = TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(MODID, "allowed_on_tomes"));
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	public static final DeferredItem<Item> MYSTIC_TOME = ITEMS.register("mystic_tome", () -> new MysticTomeItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).component(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY.withTooltip(false)).component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

	public MysticTomes(IEventBus bus, ModContainer container) {
		container.registerConfig(ModConfig.Type.COMMON, MysticTomesConfig.CONFIG_SPEC);
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

		ITEMS.register(bus);

		bus.addListener(BuildCreativeModeTabContentsEvent.class, event -> {
			if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) {
				event.getParameters().holders().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ALLOWED_ON_TOMES).forEach(enchantment ->
					event.accept(MysticTomeItem.getEnchantedItemStack(enchantment)));
			}
		});

		NeoForge.EVENT_BUS.addListener(this::applyTomesToEnchantments);
		NeoForge.EVENT_BUS.addListener(this::combineTomes);
	}

	private void applyTomesToEnchantments(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if (!EnchantmentHelper.hasAnyEnchantments(left) || !right.is(MYSTIC_TOME)) {
			return;
		}

		ItemStack output = left.copy();
		int cost = left.getOrDefault(DataComponents.REPAIR_COST, 0) + right.getOrDefault(DataComponents.REPAIR_COST, 0);

		ItemEnchantments.Mutable outputEnchants = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(left));

		boolean anyApply = false;
		List<Holder<Enchantment>> tomeEnchants = MysticTomeItem.getTomeEnchantments(right);
		if (tomeEnchants.isEmpty() && MysticTomesConfig.INSTANCE.wildcardTome.get()) {
			for (Holder<Enchantment> tomeEnchantment : outputEnchants.keySet()) {
				if (tomeEnchantment.is(ALLOWED_ON_TOMES)) {
					int newLevel = outputEnchants.getLevel(tomeEnchantment) + 1;
					if (newLevel <= tomeEnchantment.value().getMaxLevel() + MysticTomesConfig.INSTANCE.maxLimitBreaks.get()) {
						outputEnchants.upgrade(tomeEnchantment, newLevel);
						cost += newLevel > tomeEnchantment.value().getMaxLevel() ? MysticTomesConfig.INSTANCE.limitBreakCost.get() : MysticTomesConfig.INSTANCE.upgradeCost.get();
						anyApply = true;
					}
				}
			}
		} else {
			for (Holder<Enchantment> tomeEnchantment : tomeEnchants) {
				if (tomeEnchantment.is(ALLOWED_ON_TOMES) && outputEnchants.getLevel(tomeEnchantment) > 0 && outputEnchants.getLevel(tomeEnchantment) < tomeEnchantment.value().getMaxLevel() + MysticTomesConfig.INSTANCE.maxLimitBreaks.get()) {
					int newLevel = outputEnchants.getLevel(tomeEnchantment) + 1;
					outputEnchants.upgrade(tomeEnchantment, newLevel);
					cost += newLevel > tomeEnchantment.value().getMaxLevel() ? MysticTomesConfig.INSTANCE.limitBreakCost.get() : MysticTomesConfig.INSTANCE.upgradeCost.get();
					anyApply = true;
				}
			}
		}

		if (!anyApply) {
			event.setCanceled(true);
		}

		String name = event.getName();
		if (name != null && !name.isEmpty() && (!output.has(DataComponents.CUSTOM_NAME) || !output.getHoverName().getString().equals(name))) {
			output.set(DataComponents.CUSTOM_NAME, Component.literal(name));
			cost++;
		}

		output.set(DataComponents.STORED_ENCHANTMENTS, outputEnchants.toImmutable());
		if (MysticTomesConfig.INSTANCE.addRepairCost.get()) {
			output.set(DataComponents.REPAIR_COST, cost);
		}
		event.setOutput(output);
		event.setCost(cost);
	}

	private void combineTomes(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if (!MysticTomesConfig.INSTANCE.tomeCombining.get()
			|| !left.is(MYSTIC_TOME) || !right.is(MYSTIC_TOME)
			|| !EnchantmentHelper.hasAnyEnchantments(left) || !EnchantmentHelper.hasAnyEnchantments(right)) {
			return;
		}

		ItemStack output = left.copy();
		int cost = left.getOrDefault(DataComponents.REPAIR_COST, 0) + right.getOrDefault(DataComponents.REPAIR_COST, 0);

		ItemEnchantments.Mutable outputEnchants = new ItemEnchantments.Mutable(left.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY));

		boolean anyApply = false;
		List<Holder<Enchantment>> tomeEnchants = MysticTomeItem.getTomeEnchantments(right);
		for (Holder<Enchantment> tomeEnchantment : tomeEnchants) {
			if (outputEnchants.getLevel(tomeEnchantment) == 0) {
				outputEnchants.set(tomeEnchantment, tomeEnchantment.value().getMaxLevel());
				cost += MysticTomesConfig.INSTANCE.combineCost.get();
				anyApply = true;
			}
		}

		if (!anyApply) {
			event.setCanceled(true);
		}

		String name = event.getName();
		if (name != null && !name.isEmpty() && (!output.has(DataComponents.CUSTOM_NAME) || !output.getHoverName().getString().equals(name))) {
			output.set(DataComponents.CUSTOM_NAME, Component.literal(name));
			cost++;
		}

		output.set(DataComponents.STORED_ENCHANTMENTS, outputEnchants.toImmutable());
		if (MysticTomesConfig.INSTANCE.addCombineRepairCost.get()) {
			output.set(DataComponents.REPAIR_COST, cost);
		}
		event.setOutput(output);
		event.setCost(cost);
	}
}
