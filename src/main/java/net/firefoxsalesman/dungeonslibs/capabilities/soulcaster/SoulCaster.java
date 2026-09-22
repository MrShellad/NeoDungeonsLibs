package net.firefoxsalesman.dungeonslibs.capabilities.soulcaster;

import net.firefoxsalesman.dungeonslibs.integration.goety.GoetyCompat;
import net.firefoxsalesman.dungeonslibs.utils.ModHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

import static net.firefoxsalesman.dungeonslibs.attribute.AttributeRegistry.SOUL_CAP;

public class SoulCaster implements INBTSerializable<CompoundTag> {

	private float souls;

	public SoulCaster() {
		this.souls = 0;
	}

	/**
	 * Do not call this method directly or you will be fired!
	 * Use {@link SoulCasterHelper#getSouls(net.minecraft.world.entity.Entity)} instead, because it supports
	 * Goety.
	 */
	public float getSouls() {
		return souls;
	}

	public void addSouls(float amount, LivingEntity living) {
		if (ModHelper.hasGoety() && living instanceof Player player) {
			GoetyCompat.increaseSouls(player, (int) amount);
		} else {
			setSouls(this.getSouls() + amount, living);
		}
	}

	public void setSouls(float amount, @Nullable LivingEntity living) {
		if (ModHelper.hasGoety() && living instanceof Player player) {
			GoetyCompat.setSouls(player, 0);
			GoetyCompat.setSouls(player, (int) amount);
		} else if (living != null) {
			this.souls = Mth.clamp(amount, 0, (float) living.getAttributeValue(SOUL_CAP));
		} else {
			this.souls = Math.max(amount, 0);
		}
	}

	@Nullable
	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("souls", this.getSouls());
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		this.setSouls(tag.getFloat("souls"), null);
	}
}
