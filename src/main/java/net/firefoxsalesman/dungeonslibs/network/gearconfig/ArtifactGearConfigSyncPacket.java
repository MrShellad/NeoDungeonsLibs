package net.firefoxsalesman.dungeonslibs.network.gearconfig;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.items.artifacts.config.ArtifactGearConfig;
import net.firefoxsalesman.dungeonslibs.items.artifacts.config.ArtifactGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public record ArtifactGearConfigSyncPacket(Map<ResourceLocation, ArtifactGearConfig> data) implements CustomPacketPayload {
	public static final Type<ArtifactGearConfigSyncPacket> TYPE = new Type<>(ResourceLocationHelper.modLoc("artifact_gear_config_sync"));

	private static final Codec<Map<ResourceLocation, ArtifactGearConfig>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, ArtifactGearConfig.CODEC);

	public static final StreamCodec<ByteBuf, ArtifactGearConfigSyncPacket> STREAM_CODEC = ByteBufCodecs.fromCodec(MAPPER)
			.map(ArtifactGearConfigSyncPacket::new, ArtifactGearConfigSyncPacket::data);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ArtifactGearConfigSyncPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ArtifactGearConfigRegistry.ARTIFACT_GEAR_CONFIGS.setData(packet.data());
			reloadAllItems();
		});
	}
}
