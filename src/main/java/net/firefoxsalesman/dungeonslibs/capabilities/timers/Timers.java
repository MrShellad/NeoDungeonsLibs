package net.firefoxsalesman.dungeonslibs.capabilities.timers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class Timers implements INBTSerializable<CompoundTag> {
	private final Map<ResourceLocation, Integer> enchantmentTimers = new HashMap<>();

	public int getEnchantmentTimer(ResourceLocation enchantment) {
		return enchantmentTimers.computeIfAbsent(enchantment, resourceLocation -> -1);
	}

	public int getEnchantmentTimer(Holder<Enchantment> enchantment) {
		return enchantment.unwrapKey().map(k -> getEnchantmentTimer(k.location())).orElse(-1);
	}

	public boolean setEnchantmentTimer(Holder<Enchantment> enchantment, int value) {
		enchantment.unwrapKey().ifPresent(k -> enchantmentTimers.put(k.location(), value));
		return true;
	}

	public boolean setEnchantmentTimer(ResourceLocation enchantment, int value) {
		enchantmentTimers.put(enchantment, value);
		return true;
	}

	public boolean tickTimers() {
		enchantmentTimers.replaceAll((resourceLocation, integer) -> integer > 0 ? integer - 1 : integer);
		return true;
	}

	public Map<ResourceLocation, Integer> getEnchantmentTimers() {
		return enchantmentTimers;
	}

	public static final String ENCHANTS_KEY = "EnchantmentTimers";
	public static final String ENCHANTMENT_KEY = "Enchantment";
	public static final String TIMER_KEY = "Timer";

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		ListTag listnbt = new ListTag();
		this.getEnchantmentTimers().forEach((resourceLocation, timer) -> {
			CompoundTag compoundnbt = new CompoundTag();
			compoundnbt.putString(ENCHANTMENT_KEY, resourceLocation.toString());
			compoundnbt.putInt(TIMER_KEY, timer);
			listnbt.add(compoundnbt);
		});
		tag.put(ENCHANTS_KEY, listnbt);
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		ListTag listNBT = tag.getList(ENCHANTS_KEY, 10);
		for (int i = 0; i < listNBT.size(); ++i) {
			CompoundTag compoundnbt = listNBT.getCompound(i);
			ResourceLocation resourcelocation = ResourceLocation.parse(compoundnbt.getString(ENCHANTMENT_KEY));
			int timer = compoundnbt.getInt(TIMER_KEY);
			this.setEnchantmentTimer(resourcelocation, timer);
		}
	}
}
