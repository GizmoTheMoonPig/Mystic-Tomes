package com.gizmo.tomes;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MysticTomesConfig {

	public final ModConfigSpec.BooleanValue wildcardTome;
	public final ModConfigSpec.IntValue upgradeCost;
	public final ModConfigSpec.IntValue limitBreakCost;
	public final ModConfigSpec.BooleanValue addRepairCost;
	public final ModConfigSpec.IntValue maxLimitBreaks;

	public final ModConfigSpec.BooleanValue tomeCombining;
	public final ModConfigSpec.IntValue combineCost;
	public final ModConfigSpec.BooleanValue addCombineRepairCost;

	public static final ModConfigSpec CONFIG_SPEC;
	public static final MysticTomesConfig INSTANCE;

	public MysticTomesConfig(ModConfigSpec.Builder builder) {

		builder.comment("Settings for combining items and Mystic Tomes together").translation("config.mystictomes.tome_item_settings").push("Tome-Item Combination Settings");

		this.wildcardTome = builder
			.translation("config.mystictomes.wildcard_tome")
			.comment("""
				If true, Mystic Tomes without an enchantment defined via the 'minecraft:stored_enchantments' data component will be considered a 'wildcard tome'.
				These tomes will upgrade any and all allowed enchantments on a tool or book.""")
			.define("wildcardTome", true);

		this.upgradeCost = builder
			.translation("config.mystictomes.upgrade_cost")
			.comment("Defines how many levels it should cost to upgrade an enchantment's level using a tome.")
			.defineInRange("upgradeCost", 10, 0, Integer.MAX_VALUE);

		this.limitBreakCost = builder
			.translation("config.mystictomes.limit_break_cost")
			.comment("Defines how many levels it should cost to limit break an enchantment's level using a tome.")
			.defineInRange("limitBreakCost", 20, 0, Integer.MAX_VALUE);

		this.addRepairCost = builder
			.translation("config.mystictomes.add_repair_cost")
			.comment("If true, using a Mystic Tome on an item will add to its repair cost, meaning it will be more expensive to do future operations in an anvil.")
			.define("addRepairCost", false);

		this.maxLimitBreaks = builder
			.translation("config.mystictomes.max_limit_breaks")
			.comment("Defines how many times an enchantment can go above its normal max level using a Mystic Tome.")
			.defineInRange("maxLimitBreaks", 1, 1, Integer.MAX_VALUE);

		builder.pop();

		builder.comment("Settings for combining multiple Mystic Tomes together").translation("config.mystictomes.tome_tome_settings").push("Tome-Tome Combination Settings");

		this.tomeCombining = builder
			.translation("config.mystictomes.tome_combining")
			.comment("If true, Mystic Tomes can be combined together in an anvil to stack their effects together in a single item.")
			.define("tomeCombining", true);

		this.combineCost = builder
			.translation("config.mystictomes.combine_cost")
			.comment("Defines how many levels it should cost to combine tomes together.")
			.defineInRange("combineCost", 5, 0, Integer.MAX_VALUE);

		this.addCombineRepairCost = builder
			.translation("config.mystictomes.add_combine_repair_cost")
			.comment("If true, combining Mystic Tomes will add to their repair cost, meaning it will be more expensive to do future operations in an anvil.")
			.define("addCombineRepairCost", true);

		builder.pop();
	}

	static {
		Pair<MysticTomesConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(MysticTomesConfig::new);
		CONFIG_SPEC = specPair.getRight();
		INSTANCE = specPair.getLeft();
	}
}
