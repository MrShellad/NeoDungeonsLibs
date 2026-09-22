package net.firefoxsalesman.dungeonslibs.items;

import net.firefoxsalesman.dungeonslibs.utils.RangedAttackHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.utils.RangedAttackHelper.getCrossbowChargeTime;

public class RangedItemModelProperties {

	private static final ResourceLocation PULL_PROPERTY = ResourceLocation.withDefaultNamespace("pull");
	private static final ResourceLocation PULLING_PROPERTY = ResourceLocation.withDefaultNamespace("pulling");
	private static final ResourceLocation CHARGED_PROPERTY = ResourceLocation.withDefaultNamespace("charged");

	public static void init() {
		ItemProperties.register(Items.BOW, PULL_PROPERTY,
				RangedItemModelProperties::getBowPullProperty);
		ItemProperties.register(Items.BOW, PULLING_PROPERTY,
				RangedItemModelProperties::getBowPullingProperty);

		ItemProperties.register(Items.CROSSBOW, PULL_PROPERTY,
				RangedItemModelProperties::getCrossbowPullProperty);
		ItemProperties.register(Items.CROSSBOW, PULLING_PROPERTY,
				RangedItemModelProperties::getCrossbowPullingProperty);
		ItemProperties.register(Items.CROSSBOW, CHARGED_PROPERTY,
				RangedItemModelProperties::getCrossbowChargedProperty);
	}

	public static void addRangedModelProperties(java.util.function.Supplier<? extends Item> itemRegistryObject) {
		if (itemRegistryObject.get() instanceof BowItem) {
			addBowModelProperties(itemRegistryObject);
		} else if (itemRegistryObject.get() instanceof CrossbowItem) {
			addCrossbowModelProperties(itemRegistryObject);
		}
	}

	public static void addBowModelProperties(java.util.function.Supplier<? extends Item> itemRegistryObject) {
		ItemProperties.register(itemRegistryObject.get(), PULL_PROPERTY,
				RangedItemModelProperties::getBowPullProperty);
		ItemProperties.register(itemRegistryObject.get(), PULLING_PROPERTY,
				RangedItemModelProperties::getBowPullingProperty);
	}

	public static void addCrossbowModelProperties(java.util.function.Supplier<? extends Item> itemRegistryObject) {
		ItemProperties.register(itemRegistryObject.get(), PULL_PROPERTY,
				RangedItemModelProperties::getCrossbowPullProperty);
		ItemProperties.register(itemRegistryObject.get(), PULLING_PROPERTY,
				RangedItemModelProperties::getCrossbowPullingProperty);
		ItemProperties.register(itemRegistryObject.get(), CHARGED_PROPERTY,
				RangedItemModelProperties::getCrossbowChargedProperty);
	}

	private static float getCrossbowPullProperty(ItemStack stack, ClientLevel clientWorld,
			LivingEntity livingEntity, int i) {
		if (livingEntity == null || CrossbowItem.isCharged(stack)) {
			return 0.0F;
		} else
			return (stack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks())
					/ getCrossbowChargeTime(livingEntity, stack);
	}

	private static float getCrossbowPullingProperty(ItemStack stack, ClientLevel clientWorld,
			LivingEntity livingEntity, int i) {
		return livingEntity != null && livingEntity.isUsingItem()
				&& livingEntity.getUseItem() == stack && !CrossbowItem.isCharged(stack)
						? 1.0F
						: 0.0F;
	}

	private static float getCrossbowChargedProperty(ItemStack stack, ClientLevel clientWorld,
			LivingEntity livingEntity, int i) {
		return livingEntity != null && CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
	}

	private static float getBowPullProperty(ItemStack stack, ClientLevel clientWorld, LivingEntity livingEntity,
			int i) {
		if (livingEntity == null || livingEntity.getUseItem() != stack) {
			return 0.0F;
		} else {
			return (stack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks())
					/ RangedAttackHelper.getBowChargeTime(livingEntity, livingEntity.getUseItem());
		}
	}

	private static float getBowPullingProperty(ItemStack stack, ClientLevel clientWorld, LivingEntity livingEntity,
			int i) {
		return livingEntity != null && livingEntity.isUsingItem()
				&& livingEntity.getUseItem() == stack
						? 1.0F
						: 0.0F;
	}
}
