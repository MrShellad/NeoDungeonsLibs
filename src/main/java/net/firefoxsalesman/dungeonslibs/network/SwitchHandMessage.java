package net.firefoxsalesman.dungeonslibs.network;

import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.combat.DualWieldHandler;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SwitchHandMessage() implements CustomPacketPayload {
	public static final Type<SwitchHandMessage> TYPE = new Type<>(ResourceLocationHelper.modLoc("switch_hand"));
	public static final StreamCodec<ByteBuf, SwitchHandMessage> STREAM_CODEC = StreamCodec.unit(new SwitchHandMessage());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SwitchHandMessage packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player player = context.player();
			if (player instanceof ServerPlayer serverPlayer) {
				DualWieldHandler.switchHand(serverPlayer);
			}
		});
	}
}
