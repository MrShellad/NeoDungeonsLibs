package net.firefoxsalesman.dungeonslibs.network.materials;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.items.materials.weapon.DungeonsWeaponMaterial;
import net.firefoxsalesman.dungeonslibs.items.materials.weapon.WeaponMaterials;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;
import java.util.stream.Collectors;

import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public record WeaponMaterialSyncPacket(Map<ResourceLocation, Tier> data) implements CustomPacketPayload {
	public static final Type<WeaponMaterialSyncPacket> TYPE = new Type<>(ResourceLocationHelper.modLoc("weapon_material_sync"));

	private static final Codec<Map<ResourceLocation, Tier>> MAPPER = Codec.unboundedMap(ResourceLocation.CODEC,
			DungeonsWeaponMaterial.CODEC);

	public static final StreamCodec<ByteBuf, WeaponMaterialSyncPacket> STREAM_CODEC = ByteBufCodecs.fromCodec(MAPPER)
			.map(WeaponMaterialSyncPacket::new, WeaponMaterialSyncPacket::data);

	public WeaponMaterialSyncPacket(Map<ResourceLocation, Tier> data) {
		this.data = data.entrySet().stream().filter(entry -> entry.getValue() instanceof DungeonsWeaponMaterial)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(WeaponMaterialSyncPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			WeaponMaterials.WEAPON_MATERIALS.setData(packet.data());
			reloadAllItems();
		});
	}
}
