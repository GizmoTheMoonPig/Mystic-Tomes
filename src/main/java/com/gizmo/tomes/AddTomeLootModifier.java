package com.gizmo.tomes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class AddTomeLootModifier extends LootModifier {

	public static final MapCodec<AddTomeLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> LootModifier.codecStart(inst).and(inst.group(
			RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("options").forGetter(o -> o.options),
			RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("exclusions").forGetter(o -> o.exclusions),
			Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(o -> o.chance)))
		.apply(inst, AddTomeLootModifier::new));

	public final Optional<HolderSet<Enchantment>> options;
	public final Optional<HolderSet<Enchantment>> exclusions;
	public final float chance;

	public AddTomeLootModifier(LootItemCondition[] conditions, float chance) {
		this(conditions, Optional.empty(), Optional.empty(), chance);
	}

	public AddTomeLootModifier(LootItemCondition[] conditions, HolderSet<Enchantment> options, float chance) {
		this(conditions, Optional.of(options), Optional.empty(), chance);
	}

	public AddTomeLootModifier(LootItemCondition[] conditions, Optional<HolderSet<Enchantment>> options, Optional<HolderSet<Enchantment>> exclusions, float chance) {
		super(conditions);
		this.options = options;
		this.exclusions = exclusions;
		this.chance = chance;
	}

	public LootItemCondition[] getConditions() {
		return this.conditions;
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> items, LootContext context) {
		if (context.getRandom().nextFloat() < this.chance) {
			Stream<Holder<Enchantment>> stream = this.options.map(HolderSet::stream).orElseGet(() -> context.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT).holders().map(Function.identity())).filter(holder -> holder.is(MysticTomes.ALLOWED_ON_TOMES));
			if (this.exclusions.isPresent()) {
				stream = stream.filter(enchantmentHolder -> this.exclusions.get().contains(enchantmentHolder));
			}
			List<Holder<Enchantment>> list = stream.toList();
			Optional<Holder<Enchantment>> selectedEnchant = Util.getRandomSafe(list, context.getRandom());
			if (selectedEnchant.isEmpty()) {
				MysticTomes.LOGGER.warn("Could not find a valid enchantment for Mystic Tome loot, discarding");
				return items;
			}
			items.add(MysticTomeItem.getEnchantedItemStack(selectedEnchant.get()));
		}
		return items;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
