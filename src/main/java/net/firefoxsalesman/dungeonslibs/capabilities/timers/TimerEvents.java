package net.firefoxsalesman.dungeonslibs.capabilities.timers;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class TimerEvents {

	@SubscribeEvent
	public static void onLivingUpdate(EntityTickEvent.Post event) {
		if (event.getEntity().level().isClientSide) return;
		Timers timersCapability = TimersHelper.getTimersCapability(event.getEntity());
		timersCapability.tickTimers();
	}
}
