package com.gizmo.tomes;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MysticTomesConfig {

	public final ModConfigSpec.BooleanValue defaultLootInjections;
	public final ModConfigSpec.BooleanValue maxLevelLibrarianTrades;
	public final ModConfigSpec.IntValue wanderingTraderTrades;
	public final ModConfigSpec.BooleanValue wanderingTraderWildcardTome;

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

		this.defaultLootInjections = builder
			.translation("config.mystictomes.default_loot_injections")
			.comment("""
				If true, Mystic Tomes will automatically be injected into a bunch of vanilla loot tables via a builtin datapack.
				This datapack can already be disabled or enabled on world creation, this config just specifies what the default state of the pack should be.
				It's recommended to turn this off if you're adding your own loot injections.""")
			.define("defaultLootInjections", true);

		this.maxLevelLibrarianTrades = builder
			.translation("config.mystictomes.max_level_librarian_trades")
			.comment("""
				If true, max level librarians will generate up to 2 Mystic Tome trades.
				These trades will require a max level book of the specified enchantment and a bunch of emeralds to make a tome.
				NOTE: in future versions this will become a data driven thing that will be included in a default datapack like the loot modifiers are.""")
			.define("maxLevelLibrarianTrades", true);

		this.wanderingTraderTrades = builder
			.translation("config.mystictomes.wandering_trader_trades")
			.comment("""
				Defines how many Mystic Tomes will generate as trades on wandering traders.
				These trades will require a max level book of the specified enchantment and a bunch of emeralds to make a tome.
				NOTE: in future versions this will become a data driven thing that will be included in a default datapack like the loot modifiers are.""")
			.defineInRange("wanderingTraderTrades", 0, 0, Integer.MAX_VALUE);

		this.wanderingTraderWildcardTome = builder
			.translation("config.mystictomes.wandering_trader_wildcard_tome")
			.comment("If true, and if wildcard tomes are enabled, wandering traders will have a chance of selling one.")
			.define("wanderingTraderWildcardTome", true);

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
