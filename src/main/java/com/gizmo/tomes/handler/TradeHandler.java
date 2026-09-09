package com.gizmo.tomes.handler;

import com.gizmo.tomes.MysticTomeItem;
import com.gizmo.tomes.MysticTomes;
import com.gizmo.tomes.MysticTomesConfig;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class TradeHandler {

	public static void addTradesToLibrarians(VillagerTradesEvent event) {
		if (event.getType() == VillagerProfession.LIBRARIAN && MysticTomesConfig.INSTANCE.maxLevelLibrarianTrades.get()) {
			event.getTrades().get(5).add(new ConvertToTomeTrade());
			event.getTrades().get(5).add(new ConvertToTomeTrade());
		}
	}

	public static void addWildcardTomeToWanderingTrader(WandererTradesEvent event) {
		if (MysticTomesConfig.INSTANCE.wildcardTome.get() && MysticTomesConfig.INSTANCE.wanderingTraderWildcardTome.get()) {
			event.getRareTrades().add(new VillagerTrades.ItemsForEmeralds(MysticTomes.MYSTIC_TOME.get(), 64, 1, 1, 5));
		}
	}

	public static class ConvertToTomeTrade implements VillagerTrades.ItemListing {
		@Nullable
		@Override
		public MerchantOffer getOffer(Entity trader, RandomSource random) {
			List<Holder.Reference<Enchantment>> validEnchants = trader.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).holders().filter(enchantment -> enchantment.is(EnchantmentTags.TRADEABLE) && enchantment.is(MysticTomes.ALLOWED_ON_TOMES)).toList();
			Holder.Reference<Enchantment> targetEnchant = Util.getRandomSafe(validEnchants, random).orElse(null);

			if (targetEnchant == null) {
				MysticTomes.LOGGER.warn("Could not find a valid enchantment for Mystic Tome trade, discarding");
				return null;
			}

			ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(targetEnchant, targetEnchant.value().getMaxLevel()));
			ItemStack outputTome = MysticTomeItem.getEnchantedItemStack(targetEnchant);
			return new MerchantOffer(
				new ItemCost(enchantedBook.getItemHolder(), 1, DataComponentPredicate.allOf(enchantedBook.getComponents())),
				Optional.of(new ItemCost(Items.EMERALD, 35 + random.nextInt(26))), //35-60 emeralds
				outputTome, 1, 3, 0.2F
			);
		}
	}
}
