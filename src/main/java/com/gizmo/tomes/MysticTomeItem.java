package com.gizmo.tomes;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.List;

public class MysticTomeItem extends Item {

	public MysticTomeItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	public static List<Holder<Enchantment>> getTomeEnchantments(ItemStack stack) {
		if (!stack.is(MysticTomes.MYSTIC_TOME)) return List.of();
		return stack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).keySet().stream().toList();
	}

	public static ItemStack getEnchantedItemStack(Holder<Enchantment> enchantment) {
		ItemStack stack = new ItemStack(MysticTomes.MYSTIC_TOME.get());
		ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY.withTooltip(false));
		enchantments.set(enchantment, enchantment.value().getMaxLevel());
		stack.set(DataComponents.STORED_ENCHANTMENTS, enchantments.toImmutable());
		return stack;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, context, tooltip, tooltipFlag);

		List<Holder<Enchantment>> tomeEnchants = getTomeEnchantments(stack);
		if (!tomeEnchants.isEmpty()) {
			if (tomeEnchants.size() == 1) {
				tooltip.add(formatTooltipWithEnchant("item.mystictomes.mystic_tome.desc", tomeEnchants.getFirst()));
			} else {
				tooltip.add(Component.translatable("item.mystictomes.mystic_tome.desc_multiple").withStyle(ChatFormatting.GRAY));
				for (Holder<Enchantment> enchantment : tomeEnchants) {
					tooltip.add(formatTooltipWithEnchant("item.mystictomes.mystic_tomes.applicable_enchant", enchantment));
				}
			}
		} else {
			tooltip.add(Component.translatable("item.mystictomes.mystic_tome.any").withStyle(ChatFormatting.GRAY));
		}
	}

	private static Component formatTooltipWithEnchant(String base, Holder<Enchantment> enchantment) {
		return Component.translatable(base, enchantment.value().description(), Component.translatable("enchantment.level." + (enchantment.value().getMaxLevel() + MysticTomesConfig.INSTANCE.maxLimitBreaks.get()))).withStyle(ChatFormatting.GRAY);
	}
}
