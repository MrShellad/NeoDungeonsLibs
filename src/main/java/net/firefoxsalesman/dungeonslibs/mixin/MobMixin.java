package net.firefoxsalesman.dungeonslibs.mixin;

import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGear;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin {

	@Shadow
	public abstract boolean canReplaceEqualItem(ItemStack pCandidate, ItemStack pExisting);

	@Shadow
	public abstract double getApproximateAttackDamageWithItem(ItemStack stack);

	@Inject(method = "canReplaceCurrentItem", at = @At("RETURN"), cancellable = true)
	private void dungeonslibraries_canReplaceCurrentItem(ItemStack pCandidate, ItemStack pExisting,
			CallbackInfoReturnable<Boolean> cir) {
		boolean candIsWeapon = pCandidate.getItem() instanceof SwordItem || pCandidate.getItem() instanceof MeleeGear;
		boolean existIsWeapon = pExisting.getItem() instanceof SwordItem || pExisting.getItem() instanceof MeleeGear;

		if (candIsWeapon && !existIsWeapon) {
			cir.setReturnValue(true);
		} else if (candIsWeapon && existIsWeapon) {
			double candDamage = getApproximateAttackDamageWithItem(pCandidate);
			double existDamage = getApproximateAttackDamageWithItem(pExisting);
			if (candDamage != existDamage) {
				cir.setReturnValue(candDamage > existDamage);
			} else {
				cir.setReturnValue(canReplaceEqualItem(pCandidate, pExisting));
			}
		}
	}
}
