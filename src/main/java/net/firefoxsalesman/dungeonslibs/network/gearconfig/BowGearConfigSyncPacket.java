package net.firefoxsalesman.dungeonslibs.network.gearconfig;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGearConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public record BowGearConfigSyncPacket(Map<ResourceLocation, BowGearConfig> data) implements CustomPacketPayload {
	public static final Type<BowGearConfigSyncPacket> TYPE = new Type<>(ResourceLocationHelper.modLoc("bow_gear_config_sync"));

	private static final Codec<Map<ResourceLocation, BowGearConfig>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, BowGearConfig.CODEC);

	public static final StreamCodec<ByteBuf, BowGearConfigSyncPacket> STREAM_CODEC = ByteBufCodecs.fromCodec(MAPPER)
			.map(BowGearConfigSyncPacket::new, BowGearConfigSyncPacket::data);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(BowGearConfigSyncPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			BowGearConfigRegistry.BOW_GEAR_CONFIGS.setData(packet.data());
			reloadAllItems();
		});
	}
}
