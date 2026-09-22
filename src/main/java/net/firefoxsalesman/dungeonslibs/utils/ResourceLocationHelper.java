package net.firefoxsalesman.dungeonslibs.utils;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationHelper {
	public static ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(DungeonsLibs.MOD_ID, path);
	}

	public static ResourceLocation forgeLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath("c", path);
	}

	public static ResourceLocation neoLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath("neoforge", path);
	}

	public static ResourceLocation loc(String namespace, String path) {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

	public static ResourceLocation parse(String location) {
		return ResourceLocation.parse(location);
	}
}
