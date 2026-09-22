/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.firefoxsalesman.dungeonslibs.event;

import net.firefoxsalesman.dungeonslibs.entities.SoulOrbEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * PlayerSoulEvent is fired whenever an event involving player souls occurs.
 */
public class PlayerSoulEvent extends PlayerEvent {

	public PlayerSoulEvent(Player player) {
		super(player);
	}

	/**
	 * This event is fired after the player collides with a soul orb, but
	 * before the player has been given the soul.
	 * It can be cancelled, and no further processing will be done.
	 */
	public static class PickupSoul extends PlayerSoulEvent implements ICancellableEvent {

		private final SoulOrbEntity orb;

		public PickupSoul(Player player, SoulOrbEntity orb) {
			super(player);
			this.orb = orb;
		}

		public SoulOrbEntity getOrb() {
			return orb;
		}

	}

}
