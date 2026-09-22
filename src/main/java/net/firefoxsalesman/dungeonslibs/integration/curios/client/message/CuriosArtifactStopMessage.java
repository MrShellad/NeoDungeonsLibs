package net.firefoxsalesman.dungeonslibs.integration.curios.client.message;

import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.capabilities.artifact.ArtifactUsage;
import net.firefoxsalesman.dungeonslibs.capabilities.artifact.ArtifactUsageHelper;
import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactItem;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CuriosArtifactStopMessage implements CustomPacketPayload {
	public static final Type<CuriosArtifactStopMessage> TYPE = new Type<>(ResourceLocationHelper.modLoc("curios_artifact_stop"));
	public static final CuriosArtifactStopMessage INSTANCE = new CuriosArtifactStopMessage();

	public static final StreamCodec<ByteBuf, CuriosArtifactStopMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	public CuriosArtifactStopMessage() {
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(CuriosArtifactStopMessage packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player != null) {
				ArtifactUsage cap = ArtifactUsageHelper.getArtifactUsageCapability(player);
				ItemStack artifactStack = cap.getUsingArtifact();
				if (artifactStack != null && artifactStack.getItem() instanceof ArtifactItem artifactItem) {
					artifactItem.stopUsingArtifact(player);
					cap.stopUsingArtifact();
				}
			}
		});
	}
}
