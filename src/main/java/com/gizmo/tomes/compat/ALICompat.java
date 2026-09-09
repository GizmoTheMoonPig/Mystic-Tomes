package com.gizmo.tomes.compat;

import com.gizmo.tomes.AddTomeLootModifier;
import com.gizmo.tomes.MysticTomes;
import com.gizmo.tomes.handler.TradeHandler;
import com.mojang.datafixers.util.Either;
import com.yanny.aci.api.RangeValue;
import com.yanny.aci.tooltip.TooltipBuilder;
import com.yanny.aci.tooltip.TooltipNode;
import com.yanny.ali.api.*;
import com.yanny.ali.language.Lang;
import com.yanny.ali.neoforge.plugin.IForgePlugin;
import com.yanny.ali.plugin.common.nodes.ItemNode;
import com.yanny.ali.plugin.common.trades.ItemsToItemsNode;
import com.yanny.ali.plugin.glm.GlobalLootModifierUtils;
import com.yanny.ali.plugin.glm.IGlobalLootModifierPlugin;
import com.yanny.ali.plugin.glm.ILootTableIdConditionPredicate;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;

@AliEntrypoint
public class ALICompat implements IForgePlugin {

	@Override
	public String getModId() {
		return MysticTomes.MODID;
	}

	@Override
	public void registerServer(IServerRegistry registry) {
		registry.registerItemListing(TradeHandler.ConvertToTomeTrade.class, (util, trade, tooltip) -> new ItemsToItemsNode(util,
			Either.left(Items.ENCHANTED_BOOK.getDefaultInstance()), new RangeValue(1.0F), TooltipBuilder.keyOnly(Lang.Functions.ENCHANT_RANDOMLY).build(),
			Either.left(Items.EMERALD.getDefaultInstance()), new RangeValue(35.0F, 60.0F), TooltipNode.empty(),
			Either.left(new ItemStack(MysticTomes.MYSTIC_TOME, 1, DataComponentPatch.builder().set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE).build())), new RangeValue(1.0F), TooltipBuilder.keyOnly(Lang.Functions.ENCHANT_RANDOMLY).build(),
			1, 3, 0.2F, tooltip));
	}

	@Override
	public void registerGlobalLootModifier(IGlobalLootModifierPlugin.IRegistry registry, ILootTableIdConditionPredicate predicate) {
		registry.registerGlobalLootModifier(AddTomeLootModifier.class, (util, modifier) ->
			GlobalLootModifierUtils.getLootModifier(
				Arrays.asList(modifier.getConditions()),
				conditions -> List.of(new IOperation.AddOperation((itemStack) -> true, new ItemNode(
					modifier.chance,
					new RangeValue(1.0F),
					new ItemStack(MysticTomes.MYSTIC_TOME, 1, DataComponentPatch.builder().set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE).build()),
					TooltipBuilder.array((b) -> {
						b.add(util.getValueTooltip(util, modifier.options).build(Lang.Branch.ENCHANTMENTS));
						b.add(util.getValueTooltip(util, modifier.exclusions).build(Lang.Branch.EXCLUDE));
						b.showEmpty();
					}, Lang.Functions.ENCHANT_RANDOMLY).build(),
					List.of(),
					Arrays.asList(modifier.getConditions())))),
				predicate));
	}
}
