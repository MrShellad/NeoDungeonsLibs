package net.firefoxsalesman.dungeonslibs.capabilities.artifact;

import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class ArtifactUsage implements INBTSerializable<CompoundTag> {

	private ItemStack usingArtifact = null;
	private int usingArtifactRemaining = 0;

	public boolean isUsingArtifact() {
		return usingArtifact != null;
	}

	public boolean isSameUsingArtifact(ItemStack itemStack) {
		return usingArtifact != null && itemStack != null && ItemStack.matches(itemStack, usingArtifact);
	}

	public boolean startUsingArtifact(ItemStack itemStack, LivingEntity entity) {
		if (usingArtifact != null || !(itemStack.getItem() instanceof ArtifactItem))
			return false;
		usingArtifact = itemStack;
		usingArtifactRemaining = entity != null ? itemStack.getUseDuration(entity) : 72000;
		return true;
	}

	public boolean startUsingArtifact(ItemStack itemStack) {
		return startUsingArtifact(itemStack, null);
	}

	public boolean stopUsingArtifact() {
		usingArtifact = null;
		usingArtifactRemaining = 0;
		return true;
	}

	public ItemStack getUsingArtifact() {
		return usingArtifact;
	}

	public int getUsingArtifactRemaining() {
		return usingArtifactRemaining;
	}

	public void setUsingArtifactRemaining(int usingArtifactRemaining) {
		this.usingArtifactRemaining = usingArtifactRemaining;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		if (usingArtifact != null) {
			tag.put("item", usingArtifact.saveOptional(provider));
			tag.putInt("remaining", usingArtifactRemaining);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		if (tag.contains("item")) {
			usingArtifact = ItemStack.parseOptional(provider, tag.getCompound("item"));
			usingArtifactRemaining = tag.getInt("remaining");
		}
	}
}
