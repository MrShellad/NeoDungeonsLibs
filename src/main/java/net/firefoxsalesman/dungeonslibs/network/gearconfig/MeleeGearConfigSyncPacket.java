package net.firefoxsalesman.dungeonslibs.network.gearconfig;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public record MeleeGearConfigSyncPacket(Map<ResourceLocation, MeleeGearConfig> data) implements CustomPacketPayload {
	public static final Type<MeleeGearConfigSyncPacket> TYPE = new Type<>(ResourceLocationHelper.modLoc("melee_gear_config_sync"));

	private static final Codec<Map<ResourceLocation, MeleeGearConfig>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, MeleeGearConfig.CODEC);

	public static final StreamCodec<ByteBuf, MeleeGearConfigSyncPacket> STREAM_CODEC = ByteBufCodecs.fromCodec(MAPPER)
			.map(MeleeGearConfigSyncPacket::new, MeleeGearConfigSyncPacket::data);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(MeleeGearConfigSyncPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			MeleeGearConfigRegistry.MELEE_GEAR_CONFIGS.setData(packet.data());
			reloadAllItems();
		});
	}
}
