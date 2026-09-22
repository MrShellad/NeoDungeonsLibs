package net.firefoxsalesman.dungeonslibs.mixin;

import net.firefoxsalesman.dungeonslibs.utils.RangedAttackHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

	@Inject(at = @At("RETURN"), method = "getChargeDuration", cancellable = true)
	private static void dungeonslibraries_getChargeDuration(ItemStack stack, LivingEntity shooter, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue((int) RangedAttackHelper.getCrossbowChargeTime(shooter, stack));
	}

	@Inject(method = "createProjectile", at = @At("RETURN"))
	private void dungeonslibraries_createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit,
			CallbackInfoReturnable<Projectile> cir) {
		Projectile projectile = cir.getReturnValue();
		if (projectile instanceof AbstractArrow arrow) {
			RangedAttackHelper.multiplyRangedDamage(shooter, arrow);

			int powerLevel = RangedAttackHelper.getEnchantmentLevel(level, Enchantments.POWER, weapon);
			if (powerLevel > 0) {
				arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
			}

			int flameLevel = RangedAttackHelper.getEnchantmentLevel(level, Enchantments.FLAME, weapon);
			if (flameLevel > 0) {
				arrow.igniteForSeconds(5);
			}
		}
	}
}
