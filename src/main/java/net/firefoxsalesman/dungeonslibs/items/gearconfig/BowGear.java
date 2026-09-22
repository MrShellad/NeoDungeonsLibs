package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.firefoxsalesman.dungeonslibs.items.interfaces.IRangedWeapon;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IUniqueGear;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;

public class BowGear extends BowItem implements IRangedWeapon, IReloadableGear, IUniqueGear {

	private ItemAttributeModifiers defaultModifiers;
	private BowGearConfig bowGearConfig;

	public BowGear(Properties builder) {
		super(builder.durability(384));
		reload();
	}

	@Override
	public void reload() {
		bowGearConfig = BowGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		bowGearConfig.getAttributes().forEach(attributeModifier -> {
			Holder<Attribute> attribute = attributeModifier.getAttributeHolder();
			if (attribute != null) {
				ResourceLocation id;
				if (attribute.is(ATTACK_DAMAGE)) {
					id = BASE_ATTACK_DAMAGE_ID;
				} else if (attribute.is(ATTACK_SPEED)) {
					id = BASE_ATTACK_SPEED_ID;
				} else {
					id = ResourceLocationHelper.modLoc("weapon_attr_" + attributeModifier.getAttributeResourceLocation().getPath());
				}
				builder.add(attribute, new AttributeModifier(id,
						attributeModifier.getAmount(), attributeModifier.getOperation()), EquipmentSlotGroup.MAINHAND);
			}
		});
		defaultModifiers = builder.build();
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return bowGearConfig != null ? bowGearConfig.getDurability() : super.getMaxDamage(stack);
	}

	public float getDefaultChargeTime() {
		return bowGearConfig != null ? bowGearConfig.getDefaultChargeTime() : 20.0F;
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return defaultModifiers != null ? defaultModifiers : super.getDefaultAttributeModifiers(stack);
	}

	public Rarity getRarity(ItemStack pStack) {
		return getGearConfig() != null ? getGearConfig().getRarity() : Rarity.COMMON;
	}

	@Override
	public boolean isUnique() {
		return bowGearConfig != null && bowGearConfig.isUnique();
	}

	public BowGearConfig getGearConfig() {
		return bowGearConfig;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, list, flag);
		DescriptionHelper.addFullDescription(list, stack);
	}
}
