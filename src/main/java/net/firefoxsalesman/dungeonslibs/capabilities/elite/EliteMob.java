package net.firefoxsalesman.dungeonslibs.capabilities.elite;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

import static net.firefoxsalesman.dungeonslibs.entities.elite.EliteMobConfig.EMPTY_TEXTURE;

public class EliteMob implements INBTSerializable<CompoundTag> {
	private boolean isElite = false;
	private boolean hasSpawned = false;
	private ResourceLocation texture = null;

	public boolean isElite() {
		return isElite;
	}

	public void setElite(boolean elite) {
		isElite = elite;
	}

	public boolean hasSpawned() {
		return hasSpawned;
	}

	public void setHasSpawned(boolean hasSpawned) {
		this.hasSpawned = hasSpawned;
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public void setTexture(ResourceLocation texture) {
		this.texture = texture;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag nbt = new CompoundTag();
		nbt.putBoolean("isElite", isElite);
		nbt.putBoolean("hasSpawned", hasSpawned);
		if (texture != null) {
			nbt.putString("texture", texture.toString());
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		isElite = tag.getBoolean("isElite");
		hasSpawned = tag.getBoolean("hasSpawned");
		if (tag.contains("texture")) {
			texture = ResourceLocation.parse(tag.getString("texture"));
		} else {
			texture = EMPTY_TEXTURE;
		}
	}
}
