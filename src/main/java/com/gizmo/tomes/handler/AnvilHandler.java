package com.gizmo.tomes.handler;

import com.gizmo.tomes.MysticTomeItem;
import com.gizmo.tomes.MysticTomes;
import com.gizmo.tomes.MysticTomesConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

import java.util.List;

public class AnvilHandler {

	public static void applyTomesToEnchantments(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if (!EnchantmentHelper.hasAnyEnchantments(left) || !right.is(MysticTomes.MYSTIC_TOME)) {
			return;
		}

		ItemStack output = left.copy();
		int cost = left.getOrDefault(DataComponents.REPAIR_COST, 0) + right.getOrDefault(DataComponents.REPAIR_COST, 0);

		ItemEnchantments.Mutable outputEnchants = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(left));

		boolean anyApply = false;
		List<Holder<Enchantment>> tomeEnchants = MysticTomeItem.getTomeEnchantments(right);
		if (tomeEnchants.isEmpty() && MysticTomesConfig.INSTANCE.wildcardTome.get()) {
			for (Holder<Enchantment> tomeEnchantment : outputEnchants.keySet()) {
				if (tomeEnchantment.is(MysticTomes.ALLOWED_ON_TOMES)) {
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
				if (tomeEnchantment.is(MysticTomes.ALLOWED_ON_TOMES) && outputEnchants.getLevel(tomeEnchantment) > 0 && outputEnchants.getLevel(tomeEnchantment) < tomeEnchantment.value().getMaxLevel() + MysticTomesConfig.INSTANCE.maxLimitBreaks.get()) {
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

	public static void combineTomes(AnvilUpdateEvent event) {
		ItemStack left = event.getLeft();
		ItemStack right = event.getRight();

		if (!MysticTomesConfig.INSTANCE.tomeCombining.get()
			|| !left.is(MysticTomes.MYSTIC_TOME) || !right.is(MysticTomes.MYSTIC_TOME)
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
