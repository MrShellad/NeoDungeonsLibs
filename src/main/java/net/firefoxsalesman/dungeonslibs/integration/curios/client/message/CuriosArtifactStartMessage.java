package net.firefoxsalesman.dungeonslibs.integration.curios.client.message;

import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactItem;
import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactUseContext;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

public record CuriosArtifactStartMessage(int slot, BlockHitResult hitResult) implements CustomPacketPayload {
	public static final Type<CuriosArtifactStartMessage> TYPE = new Type<>(ResourceLocationHelper.modLoc("curios_artifact_start"));

	public static final StreamCodec<ByteBuf, CuriosArtifactStartMessage> STREAM_CODEC = StreamCodec.of(
			(buf, msg) -> {
				ByteBufCodecs.VAR_INT.encode(buf, msg.slot);
				// Encode simple block hit result: block pos, direction, etc.
				net.minecraft.network.FriendlyByteBuf fbb = new net.minecraft.network.FriendlyByteBuf(buf);
				fbb.writeBlockHitResult(msg.hitResult);
			},
			buf -> {
				int slot = ByteBufCodecs.VAR_INT.decode(buf);
				net.minecraft.network.FriendlyByteBuf fbb = new net.minecraft.network.FriendlyByteBuf(buf);
				BlockHitResult bhr = fbb.readBlockHitResult();
				return new CuriosArtifactStartMessage(slot, bhr);
			}
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(CuriosArtifactStartMessage packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (context.player() instanceof ServerPlayer player) {
				CuriosApi.getCuriosInventory(player).ifPresent(iCuriosItemHandler -> {
					Optional<ICurioStacksHandler> artifactStackHandler = iCuriosItemHandler.getStacksHandler("artifact");
					if (artifactStackHandler.isPresent()) {
						ItemStack artifact = artifactStackHandler.get().getStacks().getStackInSlot(packet.slot());
						if (!artifact.isEmpty() && artifact.getItem() instanceof ArtifactItem artifactItem) {
							ArtifactUseContext iuc = new ArtifactUseContext(
									player.level(),
									player,
									artifact,
									packet.hitResult());
							artifactItem.activateArtifact(iuc);
						}
					}
				});
			}
		});
	}
}
