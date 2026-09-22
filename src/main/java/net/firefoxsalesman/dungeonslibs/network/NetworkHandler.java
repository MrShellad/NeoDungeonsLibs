package net.firefoxsalesman.dungeonslibs.network;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.integration.curios.client.message.CuriosArtifactStartMessage;
import net.firefoxsalesman.dungeonslibs.integration.curios.client.message.CuriosArtifactStopMessage;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.ArmorGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.ArtifactGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.BowGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.CrossbowGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.MeleeGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.materials.ArmorMaterialSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.materials.WeaponMaterialSyncPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {

	public static void init() {
	}

	@SubscribeEvent
	public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");

		registrar.playToClient(
				UpdateSoulsMessage.TYPE,
				UpdateSoulsMessage.STREAM_CODEC,
				UpdateSoulsMessage::handle);

		registrar.playToClient(
				ArmorGearConfigSyncPacket.TYPE,
				ArmorGearConfigSyncPacket.STREAM_CODEC,
				ArmorGearConfigSyncPacket::handle);

		registrar.playToClient(
				MeleeGearConfigSyncPacket.TYPE,
				MeleeGearConfigSyncPacket.STREAM_CODEC,
				MeleeGearConfigSyncPacket::handle);

		registrar.playToClient(
				BowGearConfigSyncPacket.TYPE,
				BowGearConfigSyncPacket.STREAM_CODEC,
				BowGearConfigSyncPacket::handle);

		registrar.playToClient(
				CrossbowGearConfigSyncPacket.TYPE,
				CrossbowGearConfigSyncPacket.STREAM_CODEC,
				CrossbowGearConfigSyncPacket::handle);

		registrar.playToClient(
				ArtifactGearConfigSyncPacket.TYPE,
				ArtifactGearConfigSyncPacket.STREAM_CODEC,
				ArtifactGearConfigSyncPacket::handle);

		registrar.playToClient(
				ArmorMaterialSyncPacket.TYPE,
				ArmorMaterialSyncPacket.STREAM_CODEC,
				ArmorMaterialSyncPacket::handle);

		registrar.playToClient(
				WeaponMaterialSyncPacket.TYPE,
				WeaponMaterialSyncPacket.STREAM_CODEC,
				WeaponMaterialSyncPacket::handle);

		registrar.playToClient(
				BreakItemMessage.TYPE,
				BreakItemMessage.STREAM_CODEC,
				BreakItemMessage::handle);

		registrar.playToClient(
				EliteMobMessage.TYPE,
				EliteMobMessage.STREAM_CODEC,
				EliteMobMessage::handle);

		registrar.playToServer(
				SwitchHandMessage.TYPE,
				SwitchHandMessage.STREAM_CODEC,
				SwitchHandMessage::handle);

		registrar.playToServer(
				CuriosArtifactStartMessage.TYPE,
				CuriosArtifactStartMessage.STREAM_CODEC,
				CuriosArtifactStartMessage::handle);

		registrar.playBidirectional(
				CuriosArtifactStopMessage.TYPE,
				CuriosArtifactStopMessage.STREAM_CODEC,
				CuriosArtifactStopMessage::handle);
	}
}
