package net.firefoxsalesman.dungeonslibs.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.firefoxsalesman.dungeonslibs.utils.RangedAttackHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;getPowerForTime(I)F"), method = "releaseUsing")
	private float libraries_releaseUsing_getPowerForTime(int useTime, Operation<Float> original,
			ItemStack itemStack, Level level, LivingEntity livingEntity, int useTimeRemaining) {
		return RangedAttackHelper.getBowArrowVelocity(livingEntity, itemStack, useTime);
	}

	@Inject(method = "shootProjectile", at = @At("HEAD"))
	private void libraries_shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity,
			float inaccuracy, float angle, LivingEntity target, CallbackInfo ci) {
		if (projectile instanceof AbstractArrow arrow) {
			RangedAttackHelper.multiplyRangedDamage(shooter, arrow);
		}
	}
}
