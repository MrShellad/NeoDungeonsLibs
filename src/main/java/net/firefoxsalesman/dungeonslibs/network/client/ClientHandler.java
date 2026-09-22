package net.firefoxsalesman.dungeonslibs.network.client;

import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMob;
import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMobHelper;
import net.firefoxsalesman.dungeonslibs.network.EliteMobMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientHandler {
	public static void handleEliteMobMessage(EliteMobMessage message,
			IPayloadContext context) {
		context.enqueueWork(() -> {
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.level() != null) {
				Entity entity = Minecraft.getInstance().player.level().getEntity(message.entityId());
				if (entity instanceof LivingEntity) {
					EliteMob cap = EliteMobHelper.getEliteMobCapability(entity);
					cap.setElite(message.isElite());
					cap.setTexture(message.texture());
					if (cap.isElite()) {
						entity.refreshDimensions();
					}
				}
			}
		});
	}
}
