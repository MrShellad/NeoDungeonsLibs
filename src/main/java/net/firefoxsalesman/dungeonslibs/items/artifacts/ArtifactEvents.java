package net.firefoxsalesman.dungeonslibs.items.artifacts;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.capabilities.artifact.ArtifactUsage;
import net.firefoxsalesman.dungeonslibs.capabilities.artifact.ArtifactUsageHelper;
import net.firefoxsalesman.dungeonslibs.integration.curios.CuriosIntegration;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class ArtifactEvents {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		ArtifactUsage cap = ArtifactUsageHelper.getArtifactUsageCapability(player);
		if (cap.isUsingArtifact() && cap.getUsingArtifact().getItem() instanceof ArtifactItem) {
			cap.getUsingArtifact().getItem().onUseTick(player.level(), player,
					cap.getUsingArtifact(), cap.getUsingArtifactRemaining());
			cap.setUsingArtifactRemaining(cap.getUsingArtifactRemaining() - 1);
		}
	}

	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		stopUsingAllArtifacts(event.getEntity());
	}

	@SubscribeEvent
	public static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
		stopUsingAllArtifacts(event.getEntity());

	}

	@SubscribeEvent
	public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
		stopUsingAllArtifacts(event.getEntity());
	}

	private static void stopUsingAllArtifacts(Player player) {
		CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent(iCuriosItemHandler -> {
			Optional<ICurioStacksHandler> artifactStackHandler = iCuriosItemHandler
					.getStacksHandler("artifact");
			if (artifactStackHandler.isPresent()) {
				int slots = artifactStackHandler.get().getStacks().getSlots();
				for (int slot = 0; slot < slots; slot++) {
					ItemStack artifact = artifactStackHandler.get().getStacks()
							.getStackInSlot(slot);
					if (!artifact.isEmpty() && artifact.getItem() instanceof ArtifactItem) {
						((ArtifactItem) artifact.getItem()).stopUsingArtifact(player);
					}
				}
			}
		});
	}
}
