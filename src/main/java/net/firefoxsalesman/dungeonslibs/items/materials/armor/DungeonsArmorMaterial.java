package net.firefoxsalesman.dungeonslibs.items.materials.armor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class DungeonsArmorMaterial {

	public static final Codec<DungeonsArmorMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(DungeonsArmorMaterial::getName),
			Codec.INT.fieldOf("durability").forGetter(DungeonsArmorMaterial::getDurabilityMultiplier),
			Codec.INT.listOf().fieldOf("damage_reduction_amounts").forGetter(DungeonsArmorMaterial::getDamageReductionAmounts),
			Codec.INT.fieldOf("enchantability").forGetter(DungeonsArmorMaterial::getEnchantmentValue),
			ResourceLocation.CODEC.fieldOf("repair_item").forGetter(DungeonsArmorMaterial::getRepairItemResourceLocation),
			BuiltInRegistries.SOUND_EVENT.holderByNameCodec().fieldOf("equip_sound").forGetter(DungeonsArmorMaterial::getEquipSound),
			Codec.FLOAT.fieldOf("toughness").forGetter(DungeonsArmorMaterial::getToughness),
			Codec.FLOAT.fieldOf("knockback_resistance").forGetter(DungeonsArmorMaterial::getKnockbackResistance),
			ArmorMaterialBaseType.CODEC.fieldOf("base_type").forGetter(DungeonsArmorMaterial::getBaseType))
			.apply(instance, DungeonsArmorMaterial::new));

	private final String name;
	private final int durabilityMultiplier;
	private final List<Integer> damageReductionAmounts;
	private final int enchantability;
	private final ResourceLocation repairItemResourceLocation;
	private final Holder<SoundEvent> equipSound;
	private final float toughness;
	private final float knockbackResistance;
	private final ArmorMaterialBaseType baseType;
	private final Holder<ArmorMaterial> armorMaterialHolder;

	public DungeonsArmorMaterial(String name, int durabilityMultiplier, List<Integer> damageReductionAmounts,
			int enchantability, ResourceLocation repairItemResourceLocation, Holder<SoundEvent> equipSound,
			float toughness, float knockbackResistance, ArmorMaterialBaseType baseType) {
		this(name, durabilityMultiplier, damageReductionAmounts, enchantability, repairItemResourceLocation,
				equipSound, toughness, knockbackResistance, baseType, null);
	}

	public DungeonsArmorMaterial(String name, int durabilityMultiplier, List<Integer> damageReductionAmounts,
			int enchantability, ResourceLocation repairItemResourceLocation, Holder<SoundEvent> equipSound,
			float toughness, float knockbackResistance, ArmorMaterialBaseType baseType,
			Holder<ArmorMaterial> customHolder) {
		this.name = name;
		this.durabilityMultiplier = durabilityMultiplier;
		this.damageReductionAmounts = damageReductionAmounts;
		this.enchantability = enchantability;
		this.repairItemResourceLocation = repairItemResourceLocation;
		this.equipSound = equipSound;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.baseType = baseType;

		if (customHolder != null) {
			this.armorMaterialHolder = customHolder;
		} else {
			Map<ArmorItem.Type, Integer> defenseMap = new EnumMap<>(ArmorItem.Type.class);
			int b = damageReductionAmounts.size() > 0 ? damageReductionAmounts.get(0) : 0;
			int l = damageReductionAmounts.size() > 1 ? damageReductionAmounts.get(1) : 0;
			int c = damageReductionAmounts.size() > 2 ? damageReductionAmounts.get(2) : 0;
			int h = damageReductionAmounts.size() > 3 ? damageReductionAmounts.get(3) : 0;
			defenseMap.put(ArmorItem.Type.BOOTS, b);
			defenseMap.put(ArmorItem.Type.LEGGINGS, l);
			defenseMap.put(ArmorItem.Type.CHESTPLATE, c);
			defenseMap.put(ArmorItem.Type.HELMET, h);
			defenseMap.put(ArmorItem.Type.BODY, c);

			Supplier<Ingredient> repairSupplier = () -> {
				Item item = BuiltInRegistries.ITEM.get(repairItemResourceLocation);
				if (item != null && item != Items.AIR) {
					return Ingredient.of(item);
				}
				return Ingredient.of(Items.IRON_INGOT);
			};

			ResourceLocation loc = ResourceLocation.tryParse(name);
			if (loc == null) {
				loc = ResourceLocation.fromNamespaceAndPath("dungeonslibs", name.toLowerCase());
			}
			List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(loc));
			ArmorMaterial mat = new ArmorMaterial(defenseMap, enchantability, equipSound, repairSupplier, layers, toughness, knockbackResistance);
			this.armorMaterialHolder = Holder.direct(mat);
		}
	}

	public static DungeonsArmorMaterial fromVanilla(Holder<ArmorMaterial> vanillaHolder, int durabilityMultiplier,
			ArmorMaterialBaseType baseType, ResourceLocation id) {
		ArmorMaterial mat = vanillaHolder.value();
		List<Integer> def = List.of(
				mat.getDefense(ArmorItem.Type.BOOTS),
				mat.getDefense(ArmorItem.Type.LEGGINGS),
				mat.getDefense(ArmorItem.Type.CHESTPLATE),
				mat.getDefense(ArmorItem.Type.HELMET));
		return new DungeonsArmorMaterial(id.toString(), durabilityMultiplier, def, mat.enchantmentValue(),
				ResourceLocation.parse("minecraft:iron_ingot"), mat.equipSound(), mat.toughness(),
				mat.knockbackResistance(), baseType, vanillaHolder);
	}

	public String getName() {
		return name;
	}

	public int getDurabilityMultiplier() {
		return durabilityMultiplier;
	}

	public int getDurabilityForType(ArmorItem.Type type) {
		return type.getDurability(durabilityMultiplier);
	}

	public List<Integer> getDamageReductionAmounts() {
		return damageReductionAmounts;
	}

	public int getDefenseForType(ArmorItem.Type type) {
		return armorMaterialHolder.value().getDefense(type);
	}

	public int getEnchantmentValue() {
		return enchantability;
	}

	public ResourceLocation getRepairItemResourceLocation() {
		return repairItemResourceLocation;
	}

	public Holder<SoundEvent> getEquipSound() {
		return equipSound;
	}

	public float getToughness() {
		return toughness;
	}

	public float getKnockbackResistance() {
		return knockbackResistance;
	}

	public ArmorMaterialBaseType getBaseType() {
		return baseType;
	}

	public Holder<ArmorMaterial> toHolder() {
		return armorMaterialHolder;
	}

	public ArmorMaterial toArmorMaterial() {
		return armorMaterialHolder.value();
	}
}
