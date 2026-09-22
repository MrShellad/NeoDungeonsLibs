package net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile;

import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantments;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantmentsHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

public class EnchantedProjectile implements INBTSerializable<CompoundTag> {
	public static final String ENCHANTMENT_DATA_KEY = "EnchantmentInstance";
	private final Map<ResourceLocation, Integer> enchantments = new HashMap<>();

	public void setEnchantments(ItemStack itemStack) {
		enchantments.clear();
		ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
		for (Holder<Enchantment> holder : itemEnchantments.keySet()) {
			int lvl = itemEnchantments.getLevel(holder);
			holder.unwrapKey().ifPresent(key -> enchantments.put(key.location(), lvl));
		}
		BuiltInEnchantments cap = BuiltInEnchantmentsHelper.getBuiltInEnchantmentsCapability(itemStack);
		cap.getAllBuiltInEnchantmentInstancesPerSource().values().forEach(list -> {
			for (var entry : list) {
				enchantments.merge(entry.id(), entry.level(), Integer::sum);
			}
		});
	}

	public int getEnchantmentLevel(ResourceLocation id) {
		return enchantments.getOrDefault(id, 0);
	}

	public int getEnchantmentLevel(Holder<Enchantment> enchantment) {
		return enchantment.unwrapKey().map(k -> getEnchantmentLevel(k.location())).orElse(0);
	}

	public int getEnchantmentLevel(ResourceKey<Enchantment> key) {
		return getEnchantmentLevel(key.location());
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag nbt = new CompoundTag();
		ListTag enchantmentListnbt = new ListTag();
		enchantments.forEach((id, lvl) -> {
			CompoundTag data = new CompoundTag();
			data.putString("id", id.toString());
			data.putShort("lvl", lvl.shortValue());
			enchantmentListnbt.add(data);
		});
		nbt.put(ENCHANTMENT_DATA_KEY, enchantmentListnbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		enchantments.clear();
		ListTag list = tag.getList(ENCHANTMENT_DATA_KEY, 10);
		for (int i = 0; i < list.size(); i++) {
			CompoundTag data = list.getCompound(i);
			ResourceLocation id = ResourceLocation.parse(data.getString("id"));
			int lvl = data.getShort("lvl");
			enchantments.put(id, lvl);
		}
	}
}
