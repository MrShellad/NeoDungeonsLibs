package net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile;

import net.minecraft.world.entity.Entity;

import static net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities.ENCHANTED_PROJECTILE_ATTACHMENT;

public class EnchantedProjectileHelper {

	public static EnchantedProjectile getEnchantedProjectileCapability(Entity entity) {
		if (entity == null) return new EnchantedProjectile();
		return entity.getData(ENCHANTED_PROJECTILE_ATTACHMENT);
	}
}
