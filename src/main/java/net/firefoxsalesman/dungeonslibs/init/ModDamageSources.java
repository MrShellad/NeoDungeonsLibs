package net.firefoxsalesman.dungeonslibs.init;

import javax.annotation.Nullable;

import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * This is utterly stolen from Goety.
 * Another thanks to Polarice for putting his mod under the MIT license
 */
public class ModDamageSources {
	public static ResourceKey<DamageType> ELECTRIC_SHOCK = create("electric_shock");

	public static ResourceKey<DamageType> create(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocationHelper.modLoc(name));
	}

	public static DamageSource source(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker,
			@Nullable Entity indirectAttacker) {
		return new DamageSource(
				level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type),
				attacker, indirectAttacker);
	}

}
