package net.firefoxsalesman.dungeonslibs.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.client.artifactBar.ArtifactsBarRenderer;
import net.firefoxsalesman.dungeonslibs.client.renderer.water.CustomDrownedRenderer;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;

import static net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper.modLoc;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
	/**
	 * Borrowed from Goety
	 */
	@SubscribeEvent
	public static void registerGUI(final RegisterGuiLayersEvent event) {
		event.registerAbove(VanillaGuiLayers.HOTBAR, modLoc("soul_energy_hud"), SoulEnergyGui.OVERLAY);
		event.registerAbove(VanillaGuiLayers.HOTBAR, modLoc("artifact_hud"), ArtifactsBarRenderer.OVERLAY);
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EntityType.DROWNED, CustomDrownedRenderer::new);
	}
}
