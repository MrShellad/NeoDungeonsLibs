package net.firefoxsalesman.dungeonslibs.network.materials;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.firefoxsalesman.dungeonslibs.items.materials.armor.DungeonsArmorMaterial;
import net.firefoxsalesman.dungeonslibs.items.materials.armor.DungeonsArmorMaterials;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public record ArmorMaterialSyncPacket(Map<ResourceLocation, DungeonsArmorMaterial> data) implements CustomPacketPayload {
	public static final Type<ArmorMaterialSyncPacket> TYPE = new Type<>(ResourceLocationHelper.modLoc("armor_material_sync"));

	private static final Codec<Map<ResourceLocation, DungeonsArmorMaterial>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, DungeonsArmorMaterial.CODEC);

	public static final StreamCodec<ByteBuf, ArmorMaterialSyncPacket> STREAM_CODEC = ByteBufCodecs.fromCodec(MAPPER)
			.map(ArmorMaterialSyncPacket::new, ArmorMaterialSyncPacket::data);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ArmorMaterialSyncPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			DungeonsArmorMaterials.ARMOR_MATERIALS.setData(packet.data());
			reloadAllItems();
		});
	}
}
