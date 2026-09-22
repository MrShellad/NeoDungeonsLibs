package net.firefoxsalesman.dungeonslibs.capabilities.elite;

import net.minecraft.world.entity.Entity;

import static net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities.ELITE_MOB_ATTACHMENT;

public class EliteMobHelper {

	public static EliteMob getEliteMobCapability(Entity entity) {
		if (entity == null) return new EliteMob();
		return entity.getData(ELITE_MOB_ATTACHMENT);
	}
}
