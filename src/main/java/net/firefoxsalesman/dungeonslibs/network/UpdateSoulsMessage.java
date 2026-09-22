package net.firefoxsalesman.dungeonslibs.network;

import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.capabilities.soulcaster.SoulCaster;
import net.firefoxsalesman.dungeonslibs.capabilities.soulcaster.SoulCasterHelper;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateSoulsMessage(float newAmount) implements CustomPacketPayload {
	public static final Type<UpdateSoulsMessage> TYPE = new Type<>(ResourceLocationHelper.modLoc("update_souls"));

	public static final StreamCodec<ByteBuf, UpdateSoulsMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.FLOAT,
			UpdateSoulsMessage::newAmount,
			UpdateSoulsMessage::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateSoulsMessage packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				SoulCaster soulCasterCap = SoulCasterHelper.getSoulCasterCapability(player);
				soulCasterCap.setSouls(packet.newAmount(), player);
			}
		});
	}
}
