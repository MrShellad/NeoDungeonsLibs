package net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments;

import com.google.common.collect.Lists;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.*;

public class BuiltInEnchantments implements INBTSerializable<CompoundTag> {

	public record Entry(ResourceLocation id, int level) {}

	private final Map<ResourceLocation, List<Entry>> enchantments = new HashMap<>();

	private ItemStack boundStack = null;

	public BuiltInEnchantments() {
	}

	public BuiltInEnchantments(ItemStack stack) {
		this.boundStack = stack;
	}

	private void saveIfBound() {
		if (boundStack != null && !boundStack.isEmpty()) {
			net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, boundStack, tag -> {
				CompoundTag nbt = serializeNBT(null);
				if (nbt.contains(ENCHANTS_KEY)) {
					tag.put(ENCHANTS_KEY, nbt.get(ENCHANTS_KEY));
				} else {
					tag.remove(ENCHANTS_KEY);
				}
			});
		}
	}

	public boolean addBuiltInEnchantment(ResourceLocation source, Entry entry) {
		enchantments.computeIfAbsent(source, k -> new ArrayList<>()).add(entry);
		saveIfBound();
		return true;
	}

	public boolean removeBuiltInEnchantment(ResourceLocation source, ResourceLocation enchantmentId) {
		if (!enchantments.containsKey(source)) {
			return false;
		}
		enchantments.get(source).removeIf(entry -> entry.id().equals(enchantmentId));
		saveIfBound();
		return true;
	}

	public boolean setBuiltInEnchantments(ResourceLocation source, List<Entry> entries) {
		enchantments.put(source, new ArrayList<>(entries));
		saveIfBound();
		return true;
	}

	public boolean clearAllBuiltInEnchantments(ResourceLocation source) {
		enchantments.remove(source);
		saveIfBound();
		return true;
	}

	public List<Entry> getBuiltInEnchantments(ResourceLocation source) {
		List<Entry> list = enchantments.get(source);
		if (list == null) {
			return Lists.newArrayList();
		}
		return list;
	}

	public List<Entry> getAllBuiltInEnchantmentInstances() {
		List<Entry> result = new ArrayList<>();
		enchantments.values().forEach(result::addAll);
		return result;
	}

	public Map<ResourceLocation, List<Entry>> getAllBuiltInEnchantmentInstancesPerSource() {
		return enchantments;
	}

	public boolean hasBuiltInEnchantmentForSource(ResourceLocation source) {
		return !getBuiltInEnchantments(source).isEmpty();
	}

	public boolean hasBuiltInEnchantment() {
		return !enchantments.isEmpty();
	}

	public boolean hasBuiltInEnchantment(ResourceLocation enchantmentId) {
		return getAllBuiltInEnchantmentInstances().stream()
				.anyMatch(entry -> entry.id().equals(enchantmentId));
	}

	public boolean hasBuiltInEnchantment(Holder<Enchantment> enchantment) {
		return enchantment.unwrapKey().map(k -> hasBuiltInEnchantment(k.location())).orElse(false);
	}

	public int getBuiltInItemEnchantmentLevel(ResourceLocation enchantmentId) {
		return getAllBuiltInEnchantmentInstances().stream()
				.filter(entry -> entry.id().equals(enchantmentId))
				.map(Entry::level).max(Comparator.naturalOrder())
				.orElse(0);
	}

	public int getBuiltInItemEnchantmentLevel(Holder<Enchantment> enchantment) {
		return enchantment.unwrapKey().map(k -> getBuiltInItemEnchantmentLevel(k.location())).orElse(0);
	}

	public static final String ENCHANTS_KEY = "BuiltInEnchantments";
	public static final String SOURCE_KEY = "source";
	public static final String ENCHANTMENT_DATA_KEY = "data";

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		ListTag listnbt = new ListTag();
		getAllBuiltInEnchantmentInstancesPerSource().forEach((resourceLocation, entries) -> {
			CompoundTag compoundnbt = new CompoundTag();
			compoundnbt.putString(SOURCE_KEY, resourceLocation.toString());
			ListTag enchantmentListnbt = new ListTag();
			entries.forEach(entry -> {
				CompoundTag entryTag = new CompoundTag();
				entryTag.putString("id", entry.id().toString());
				entryTag.putShort("lvl", (short) entry.level());
				enchantmentListnbt.add(entryTag);
			});
			compoundnbt.put(ENCHANTMENT_DATA_KEY, enchantmentListnbt);
			listnbt.add(compoundnbt);
		});
		if (!enchantments.isEmpty()) {
			tag.put(ENCHANTS_KEY, listnbt);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		enchantments.clear();
		if (tag.contains(ENCHANTS_KEY, 10)) {
			ListTag listNBT = tag.getList(ENCHANTS_KEY, 10);
			for (int i = 0; i < listNBT.size(); ++i) {
				CompoundTag compoundnbt = listNBT.getCompound(i);
				ResourceLocation resourcelocation = ResourceLocation.parse(compoundnbt.getString(SOURCE_KEY));
				ListTag entryList = compoundnbt.getList(ENCHANTMENT_DATA_KEY, 10);
				List<Entry> entries = new ArrayList<>();
				for (int j = 0; j < entryList.size(); j++) {
					CompoundTag entryTag = entryList.getCompound(j);
					ResourceLocation id = ResourceLocation.parse(entryTag.getString("id"));
					int lvl = entryTag.getShort("lvl");
					entries.add(new Entry(id, lvl));
				}
				setBuiltInEnchantments(resourcelocation, entries);
			}
		}
	}
}
