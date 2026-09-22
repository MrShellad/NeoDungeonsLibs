package net.firefoxsalesman.dungeonslibs.capabilities.artifact;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities.ARTIFACT_USAGE_ATTACHMENT;

public class ArtifactUsageHelper {

	public static ArtifactUsage getArtifactUsageCapability(Entity entity) {
		if (entity == null) return new ArtifactUsage();
		return entity.getData(ARTIFACT_USAGE_ATTACHMENT);
	}

	public static boolean startUsingArtifact(Player playerIn, ArtifactUsage cap, ItemStack itemstack) {
		return cap.startUsingArtifact(itemstack, playerIn);
	}
}
