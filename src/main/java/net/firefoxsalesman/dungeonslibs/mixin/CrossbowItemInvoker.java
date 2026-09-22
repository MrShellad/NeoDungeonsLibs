package net.firefoxsalesman.dungeonslibs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CrossbowItem.class)
public interface CrossbowItemInvoker {

	@Invoker("tryLoadProjectiles")
	static boolean callTryLoadProjectiles(LivingEntity livingEntity, ItemStack stack) {
		throw new RuntimeException("Invoker failed to mixin");
	}
}
