package net.firefoxsalesman.dungeonslibs.network;

import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.Vec3;

public record BreakItemMessage(int entityID, ItemStack stack) implements CustomPacketPayload {
	public static final Type<BreakItemMessage> TYPE = new Type<>(ResourceLocationHelper.modLoc("break_item"));

	public static final StreamCodec<RegistryFriendlyByteBuf, BreakItemMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			BreakItemMessage::entityID,
			ItemStack.STREAM_CODEC,
			BreakItemMessage::stack,
			BreakItemMessage::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(BreakItemMessage packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientLevel world = Minecraft.getInstance().level;
			if (world != null) {
				Entity target = world.getEntity(packet.entityID());
				if (target instanceof LivingEntity livingEntity) {
					ItemStack stack = packet.stack();
					livingEntity.level().playLocalSound(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
							SoundEvents.ITEM_BREAK, livingEntity.getSoundSource(), 0.8F,
							0.8F + livingEntity.level().random.nextFloat() * 0.4F, false);
					for (int i = 0; i < 5; ++i) {
						Vec3 vec3 = new Vec3(((double) livingEntity.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
						vec3 = vec3.xRot(-livingEntity.getXRot() * (float) (Math.PI / 180.0));
						vec3 = vec3.yRot(-livingEntity.getYRot() * (float) (Math.PI / 180.0));
						double d0 = (double) (-livingEntity.getRandom().nextFloat()) * 0.6 - 0.3;
						Vec3 vec31 = new Vec3(((double) livingEntity.getRandom().nextFloat() - 0.5) * 0.3, d0, 0.6);
						vec31 = vec31.xRot(-livingEntity.getXRot() * (float) (Math.PI / 180.0));
						vec31 = vec31.yRot(-livingEntity.getYRot() * (float) (Math.PI / 180.0));
						vec31 = vec31.add(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ());
						livingEntity.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05, vec3.z);
					}
				}
			}
		});
	}
}
