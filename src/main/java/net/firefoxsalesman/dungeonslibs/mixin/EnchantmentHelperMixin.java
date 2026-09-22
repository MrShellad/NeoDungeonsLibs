package net.firefoxsalesman.dungeonslibs.mixin;

import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantments;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantmentsHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

	@Inject(method = "getItemEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"), cancellable = true)
	private static void dungeonslibraries_getItemEnchantmentLevel(Holder<Enchantment> enchantment, ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
		if (itemStack != null && !itemStack.isEmpty()) {
			BuiltInEnchantments cap = BuiltInEnchantmentsHelper.getBuiltInEnchantmentsCapability(itemStack);
			if (cap != null && cap.hasBuiltInEnchantment(enchantment)) {
				int builtIn = cap.getBuiltInItemEnchantmentLevel(enchantment);
				cir.setReturnValue(cir.getReturnValue() + builtIn);
			}
		}
	}
}
