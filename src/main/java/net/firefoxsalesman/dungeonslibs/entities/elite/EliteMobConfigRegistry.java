package net.firefoxsalesman.dungeonslibs.entities.elite;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.data.util.MergeableCodecDataManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class EliteMobConfigRegistry {
	public static final MergeableCodecDataManager<EliteMobConfigList, List<EliteMobConfig>> ELITE_MOB_CONFIGS = new MergeableCodecDataManager<>(
			"elite_mob", EliteMobConfigList.CODEC, EliteMobConfigRegistry::eliteMobMerger);

	public static List<EliteMobConfig> eliteMobMerger(List<EliteMobConfigList> raws) {
		return raws.stream().flatMap(rawList -> rawList.getConfigs().stream()).collect(Collectors.toList());
	}

	public static EliteMobConfig getRandomConfig(ResourceLocation resourceLocation, RandomSource random) {
		List<EliteMobConfig> eliteMobConfigs = ELITE_MOB_CONFIGS.getData().getOrDefault(resourceLocation,
				Collections.emptyList());
		if (eliteMobConfigs.isEmpty()) {
			return null;
		}
		return WeightedRandom.getRandomItem(random, eliteMobConfigs).orElse(null);
	}

	public static boolean eliteMobConfigExists(ResourceLocation resourceLocation) {
		return ELITE_MOB_CONFIGS.getData().containsKey(resourceLocation);
	}

	@SubscribeEvent
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(ELITE_MOB_CONFIGS);
	}
}
