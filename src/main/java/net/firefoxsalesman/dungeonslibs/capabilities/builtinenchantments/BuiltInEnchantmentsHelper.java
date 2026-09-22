package net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class BuiltInEnchantmentsHelper {

	public static BuiltInEnchantments getBuiltInEnchantmentsCapability(ItemStack itemStack) {
		if (itemStack == null || itemStack.isEmpty()) return new BuiltInEnchantments();
		BuiltInEnchantments cap = new BuiltInEnchantments(itemStack);
		CustomData customData = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
		if (customData.contains(BuiltInEnchantments.ENCHANTS_KEY)) {
			cap.deserializeNBT(null, customData.copyTag());
		}
		return cap;
	}
}
