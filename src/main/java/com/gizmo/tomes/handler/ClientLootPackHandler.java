package com.gizmo.tomes.handler;

import com.gizmo.tomes.MysticTomesConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GenericMessageScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.List;

public class ClientLootPackHandler {

	public static void autoSelectLootPack(ScreenEvent.Opening event) {
		if (!event.isCanceled() && event.getNewScreen() instanceof CreateWorldScreen screen && MysticTomesConfig.INSTANCE.defaultLootInjections.get()) {
			if (event.getCurrentScreen() instanceof GenericMessageScreen message && message.getTitle().equals(Component.translatable("createWorld.preparing"))) {
				//waow
				screen.minecraft = Minecraft.getInstance();
				var repo = screen.getDataPackSelectionSettings(screen.getUiState().getSettings().dataConfiguration());
				if (repo != null) {
					PackRepository packRepo = repo.getSecond();
					List<Pack> selected = new ArrayList<>(packRepo.getSelectedPacks());
					packRepo.getAvailablePacks().forEach(pack -> {
						if (pack.getTitle().getString().equals("Default Loot Injections")) {
							selected.add(pack);
						}
					});
					packRepo.setSelected(selected.stream().map(Pack::getId).toList());

					List<String> enabled = ImmutableList.copyOf(packRepo.getSelectedIds());
					List<String> disabled = packRepo.getAvailableIds().stream().filter(id -> !enabled.contains(id)).collect(ImmutableList.toImmutableList());
					WorldDataConfiguration worlddataconfiguration = new WorldDataConfiguration(new DataPackConfig(enabled, disabled), screen.getUiState().getSettings().dataConfiguration().enabledFeatures());
					screen.applyNewPackConfig(packRepo, worlddataconfiguration, configuration -> {});
				}
			}
		}
	}
}
