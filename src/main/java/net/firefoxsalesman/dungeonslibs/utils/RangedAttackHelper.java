package net.firefoxsalesman.dungeonslibs.utils;

import net.firefoxsalesman.dungeonslibs.event.BowEvent;
import net.firefoxsalesman.dungeonslibs.event.CrossbowEvent;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGear;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.CrossbowGear;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ChargedProjectiles;

import static net.firefoxsalesman.dungeonslibs.attribute.AttributeRegistry.RANGED_DAMAGE_MULTIPLIER;

public class RangedAttackHelper {

	public static int getEnchantmentLevel(@Nullable Level level, ResourceKey<Enchantment> key, ItemStack stack) {
		if (level == null || stack.isEmpty()) return 0;
		return level.holderLookup(Registries.ENCHANTMENT)
				.get(key)
				.map(h -> EnchantmentHelper.getItemEnchantmentLevel(h, stack))
				.orElse(0);
	}

	public static float getBowArrowVelocity(LivingEntity livingEntity, ItemStack stack, int charge) {
		float bowChargeTime = RangedAttackHelper.getBowChargeTime(livingEntity, stack);
		if (bowChargeTime <= 0) {
			bowChargeTime = 1;
		}
		float arrowVelocity = (float) charge / bowChargeTime;
		arrowVelocity = (arrowVelocity * arrowVelocity + arrowVelocity * 2.0F) / 3.0F;
		float velocityLimit = 1.0F;
		BowEvent.Overcharge overchargeEvent = new BowEvent.Overcharge(livingEntity, stack, 0);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(overchargeEvent);
		int overchargeLevel = overchargeEvent.getCharges();
		if (overchargeLevel > 0) {
			velocityLimit += overchargeLevel;
		}
		if (arrowVelocity > velocityLimit) {
			arrowVelocity = velocityLimit;
		}

		BowEvent.Velocity velocityEvent = new BowEvent.Velocity(livingEntity, stack, arrowVelocity);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(velocityEvent);
		return velocityEvent.getVelocity();
	}

	public static float getBowChargeTime(LivingEntity livingEntity, ItemStack stack) {
		float defaultChargeTime = stack.getItem() instanceof BowGear
				? ((BowGear) stack.getItem()).getDefaultChargeTime()
				: 20.0F;
		int quickChargeLevel = getEnchantmentLevel(livingEntity != null ? livingEntity.level() : null, Enchantments.QUICK_CHARGE, stack);
		float minTime = 1;
		BowEvent.ChargeTime event = new BowEvent.ChargeTime(livingEntity, stack, defaultChargeTime);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
		return Math.max(event.getChargeTime() - 5 * quickChargeLevel, minTime);
	}

	public static float getVanillaCrossbowChargeTime(@Nullable LivingEntity livingEntity, ItemStack stack) {
		int quickChargeLevel = getEnchantmentLevel(livingEntity != null ? livingEntity.level() : null, Enchantments.QUICK_CHARGE, stack);
		float minTime = 1;
		CrossbowEvent.ChargeTime event = new CrossbowEvent.ChargeTime(livingEntity, stack, 25.0F);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
		return Math.max(event.getChargeTime() - 5 * quickChargeLevel, minTime);
	}

	public static float getCrossbowChargeTime(@Nullable LivingEntity livingEntity, ItemStack stack) {
		float chargeTime;
		if (stack.getItem() instanceof CrossbowGear crossbowGear) {
			chargeTime = crossbowGear.getCrossbowChargeTime(livingEntity, stack);
		} else {
			chargeTime = getVanillaCrossbowChargeTime(livingEntity, stack);
		}
		return chargeTime;
	}

	public static float getCrossbowArrowVelocity(@Nullable LivingEntity livingEntity, ItemStack stack) {
		float baseVelocity = 3.15F;
		ChargedProjectiles charged = stack.get(DataComponents.CHARGED_PROJECTILES);
		if (charged != null && charged.contains(Items.FIREWORK_ROCKET)) {
			baseVelocity = 1.6F;
		}
		CrossbowEvent.Velocity event = new CrossbowEvent.Velocity(livingEntity, stack, baseVelocity);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
		return event.getVelocity();
	}

	public static float getAngleForProjectileByIndex(int projectileIndex) {
		int indexScale = projectileIndex / 2;
		return 10.0F * (projectileIndex % 2 != 0 ? indexScale + 1 : indexScale * -1.0F);
	}

	public static void multiplyRangedDamage(LivingEntity shooter, AbstractArrow arrow) {
		AttributeInstance rangedDamageMultiplier = shooter.getAttribute(RANGED_DAMAGE_MULTIPLIER);
		if (rangedDamageMultiplier != null) {
			arrow.setBaseDamage(arrow.getBaseDamage() * (rangedDamageMultiplier.getValue()));
		}
	}

	public static void createBowArrow(ItemStack bowStack, Level world, Player player, ItemStack projectileStack,
			float powerForTime, int arrowIndex, boolean isInfiniteArrow) {
		ArrowItem arrowitem = (ArrowItem) (projectileStack.getItem() instanceof ArrowItem
				? projectileStack.getItem()
				: Items.ARROW);
		AbstractArrow arrow = arrowitem.createArrow(world, projectileStack, player, bowStack);
		multiplyRangedDamage(player, arrow);
		arrow.shootFromRotation(player, player.getXRot(),
				player.getYRot() + getAngleForProjectileByIndex(arrowIndex), 0.0F, powerForTime * 3.0F,
				1.0F);

		if (powerForTime >= 1.0F) {
			arrow.setCritArrow(true);
		}
		int powerLevel = getEnchantmentLevel(world, Enchantments.POWER, bowStack);
		if (powerLevel > 0) {
			arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
		}

		int flameLevel = getEnchantmentLevel(world, Enchantments.FLAME, bowStack);
		if (flameLevel > 0) {
			arrow.igniteForSeconds(5);
		}

		bowStack.hurtAndBreak(1, player, player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);
		if (isInfiniteArrow
				|| player.getAbilities().instabuild && isSpecialArrow(projectileStack)
				|| arrowIndex > 0) {
			arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		}
		world.addFreshEntity(arrow);
	}

	private static boolean isSpecialArrow(ItemStack projectileStack) {
		return projectileStack.is(Items.SPECTRAL_ARROW) || projectileStack.is(Items.TIPPED_ARROW);
	}
}
