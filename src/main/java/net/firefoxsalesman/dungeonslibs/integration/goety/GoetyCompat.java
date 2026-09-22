package net.firefoxsalesman.dungeonslibs.integration.goety;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.utils.ModHelper;
import net.minecraft.world.entity.player.Player;

import java.lang.reflect.Method;

public class GoetyCompat {
	private static Method GET_SOULS_METHOD;
	private static Method INCREASE_SOULS_METHOD;
	private static Method SET_SOULS_METHOD;
	private static boolean INITIALIZED = false;

	private static void init() {
		if (INITIALIZED) return;
		INITIALIZED = true;
		if (ModHelper.hasGoety()) {
			try {
				Class<?> seHelperClass = Class.forName("com.Polarice3.Goety.utils.SEHelper");
				for (Method m : seHelperClass.getMethods()) {
					if (m.getName().equals("getSoulAmountInt") && m.getParameterCount() == 1) {
						GET_SOULS_METHOD = m;
					} else if (m.getName().equals("increaseSouls") && m.getParameterCount() == 2) {
						INCREASE_SOULS_METHOD = m;
					} else if (m.getName().equals("setSoulsAmount") && m.getParameterCount() == 2) {
						SET_SOULS_METHOD = m;
					}
				}
			} catch (Throwable t) {
				DungeonsLibs.LOGGER.warn("Failed to initialize Goety reflection hooks", t);
			}
		}
	}

	public static int getSoulAmount(Player player) {
		init();
		if (GET_SOULS_METHOD != null) {
			try {
				return (int) GET_SOULS_METHOD.invoke(null, player);
			} catch (Throwable ignored) {}
		}
		return 0;
	}

	public static void increaseSouls(Player player, int amount) {
		init();
		if (INCREASE_SOULS_METHOD != null) {
			try {
				INCREASE_SOULS_METHOD.invoke(null, player, amount);
			} catch (Throwable ignored) {}
		}
	}

	public static void setSouls(Player player, int amount) {
		init();
		if (SET_SOULS_METHOD != null) {
			try {
				SET_SOULS_METHOD.invoke(null, player, amount);
			} catch (Throwable ignored) {}
		}
	}
}
