package net.firefoxsalesman.dungeonslibs.capabilities.playerrewards;

import net.minecraft.world.entity.player.Player;

import static net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities.PLAYER_REWARDS_ATTACHMENT;

public class PlayerRewardsHelper {

	public static PlayerRewards getPlayerRewardsCapability(Player playerEntity) {
		if (playerEntity == null) return new PlayerRewards();
		return playerEntity.getData(PLAYER_REWARDS_ATTACHMENT);
	}
}
