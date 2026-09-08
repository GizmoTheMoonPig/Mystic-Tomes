package com.gizmo.tomes.mixin;

import com.gizmo.tomes.MysticTomesConfig;
import com.gizmo.tomes.handler.TradeHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {

	public WanderingTraderMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
		super(entityType, level);
	}

	//I'm not doing this via the WanderingTradesEvent because that event only allows you to add to existing pools on the trader.
	//I want tome trades to generate independently of whatever trades the trader is already offering, essentially appending them to the end of the trade list
	@Inject(method = "updateTrades", at = @At("TAIL"), remap = false)
	public void addTomeTrades(CallbackInfo ci) {
		if (MysticTomesConfig.INSTANCE.wanderingTraderTrades.get() > 0) {
			for (int i = 0; i < MysticTomesConfig.INSTANCE.wanderingTraderTrades.get(); i++) {
				this.getOffers().add(new TradeHandler.ConvertToTomeTrade().getOffer(this, this.getRandom()));
			}
		}
	}
}
