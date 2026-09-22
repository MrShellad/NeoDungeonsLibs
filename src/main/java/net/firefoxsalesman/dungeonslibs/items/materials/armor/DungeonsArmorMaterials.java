package net.firefoxsalesman.dungeonslibs.items.materials.armor;

import net.firefoxsalesman.dungeonslibs.data.util.DefaultsCodecJsonDataManager;
import net.firefoxsalesman.dungeonslibs.network.materials.ArmorMaterialSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.NetworkHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class DungeonsArmorMaterials {

	public static final DefaultsCodecJsonDataManager<DungeonsArmorMaterial> ARMOR_MATERIALS = new DefaultsCodecJsonDataManager<>(
			"material/armor", DungeonsArmorMaterial.CODEC);

	public static void setupVanillaMaterials() {
		addDefaultArmorMaterial(ArmorMaterials.LEATHER, 5, ArmorMaterialBaseType.LEATHER,
				ResourceLocation.parse("minecraft:leather"));
		addDefaultArmorMaterial(ArmorMaterials.CHAIN, 15, ArmorMaterialBaseType.METAL,
				ResourceLocation.parse("minecraft:chainmail"));
		addDefaultArmorMaterial(ArmorMaterials.IRON, 15, ArmorMaterialBaseType.METAL,
				ResourceLocation.parse("minecraft:iron"));
		addDefaultArmorMaterial(ArmorMaterials.GOLD, 7, ArmorMaterialBaseType.METAL,
				ResourceLocation.parse("minecraft:gold"));
		addDefaultArmorMaterial(ArmorMaterials.DIAMOND, 33, ArmorMaterialBaseType.GEM,
				ResourceLocation.parse("minecraft:diamond"));
		addDefaultArmorMaterial(ArmorMaterials.TURTLE, 25, ArmorMaterialBaseType.LEATHER,
				ResourceLocation.parse("minecraft:turtle"));
		addDefaultArmorMaterial(ArmorMaterials.NETHERITE, 37, ArmorMaterialBaseType.METAL,
				ResourceLocation.parse("minecraft:netherite"));
	}

	public static void addDefaultArmorMaterial(Holder<ArmorMaterial> material, int durabilityMultiplier,
			ArmorMaterialBaseType baseType, ResourceLocation resourceLocation) {
		ARMOR_MATERIALS.addDefault(resourceLocation,
				DungeonsArmorMaterial.fromVanilla(material, durabilityMultiplier, baseType, resourceLocation));
	}

	public static DungeonsArmorMaterial getDungeonsArmorMaterial(ResourceLocation resourceLocation) {
		return ARMOR_MATERIALS.getData().get(resourceLocation);
	}

	public static Holder<ArmorMaterial> getArmorMaterial(ResourceLocation resourceLocation) {
		DungeonsArmorMaterial dam = ARMOR_MATERIALS.getData().get(resourceLocation);
		if (dam != null) {
			return dam.toHolder();
		}
		return BuiltInRegistries.ARMOR_MATERIAL
				.getHolder(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ARMOR_MATERIAL, resourceLocation))
				.<Holder<ArmorMaterial>>map(h -> h)
				.orElse(ArmorMaterials.IRON);
	}

	public static boolean ArmorMaterialExists(ResourceLocation resourceLocation) {
		return ARMOR_MATERIALS.getData().containsKey(resourceLocation)
				|| BuiltInRegistries.ARMOR_MATERIAL.containsKey(resourceLocation);
	}

	public static Collection<ResourceLocation> armorMaterialsKeys() {
		return ARMOR_MATERIALS.getData().keySet();
	}

	public static Collection<DungeonsArmorMaterial> getArmorMaterials(ArmorMaterialBaseType baseType) {
		return ARMOR_MATERIALS.getData().values().stream()
				.filter(material -> material.getBaseType() == baseType)
				.collect(Collectors.toList());
	}

	public static ArmorMaterialSyncPacket toPacket(Map<ResourceLocation, DungeonsArmorMaterial> map) {
		return new ArmorMaterialSyncPacket(map);
	}

	public static void subscribe() {
		ARMOR_MATERIALS.subscribeAsSyncable(DungeonsArmorMaterials::toPacket);
	}
}
