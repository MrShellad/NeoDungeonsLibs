package net.firefoxsalesman.dungeonslibs.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;

import java.util.Map;

@Mixin(ItemCooldowns.class)
public interface ItemCooldownsAccessor {
	@Accessor("tickCount")
	int getTickCount();

	@Accessor("cooldowns")
	Map<Item, ItemCooldowns.CooldownInstance> getCooldowns();
}
