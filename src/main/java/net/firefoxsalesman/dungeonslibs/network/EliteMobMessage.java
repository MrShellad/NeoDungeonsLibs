package net.firefoxsalesman.dungeonslibs.network;

import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.network.client.ClientHandler;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EliteMobMessage(int entityId, boolean isElite, ResourceLocation texture) implements CustomPacketPayload {
	public static final Type<EliteMobMessage> TYPE = new Type<>(ResourceLocationHelper.modLoc("elite_mob"));

	public static final StreamCodec<ByteBuf, EliteMobMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			EliteMobMessage::entityId,
			ByteBufCodecs.BOOL,
			EliteMobMessage::isElite,
			ResourceLocation.STREAM_CODEC,
			EliteMobMessage::texture,
			EliteMobMessage::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(EliteMobMessage message, IPayloadContext context) {
		ClientHandler.handleEliteMobMessage(message, context);
	}
}
