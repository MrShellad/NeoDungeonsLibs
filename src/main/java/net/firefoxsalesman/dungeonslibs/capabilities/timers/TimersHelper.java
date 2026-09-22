package net.firefoxsalesman.dungeonslibs.capabilities.timers;

import net.minecraft.world.entity.Entity;

import static net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities.TIMERS_ATTACHMENT;

public class TimersHelper {

	public static Timers getTimersCapability(Entity entity) {
		if (entity == null) return new Timers();
		return entity.getData(TIMERS_ATTACHMENT);
	}
}
