package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.firefoxsalesman.dungeonslibs.client.renderer.gearconfig.ArmorGearRenderer;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IArmor;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IUniqueGear;
import net.firefoxsalesman.dungeonslibs.mixin.ArmorItemAccessor;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class ArmorGear extends ArmorItem implements IReloadableGear, IArmor, IUniqueGear, GeoItem {
	private static final ResourceLocation DEFAULT_ARMOR_ANIMATIONS = ResourceLocationHelper
			.modLoc("animations/armor/armor_default.animation.json");

	private ItemAttributeModifiers defaultModifiers;
	private ArmorGearConfig armorGearConfig;
	private final ResourceLocation armorSet;
	private final ResourceLocation modelLocation;
	private final ResourceLocation textureLocation;
	private final ResourceLocation animationFileLocation;

	public ArmorGear(Type slotType, Properties properties, ResourceLocation armorSet,
			ResourceLocation modelLocation, ResourceLocation textureLocation,
			ResourceLocation animationFileLocation) {
		super(ArmorMaterials.CHAIN, slotType, properties);
		this.armorSet = armorSet;
		this.modelLocation = modelLocation;
		this.textureLocation = textureLocation;
		this.animationFileLocation = animationFileLocation;
		reload();
	}

	@Override
	public void reload() {
		armorGearConfig = ArmorGearConfigRegistry.getConfig(armorSet);
		if (armorGearConfig == ArmorGearConfig.DEFAULT) {
			armorGearConfig = ArmorGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
		}
		Holder<ArmorMaterial> material = armorGearConfig.getArmorMaterial();
		((ArmorItemAccessor) this).setMaterial(material);

		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		EquipmentSlot slot = getType().getSlot();
		EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(slot);

		ResourceLocation primaryModId = ResourceLocationHelper.modLoc("armor_" + slot.getName());
		builder.add(Attributes.ARMOR, new AttributeModifier(primaryModId,
				material.value().getDefense(getType()), AttributeModifier.Operation.ADD_VALUE), slotGroup);
		builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(primaryModId,
				material.value().toughness(), AttributeModifier.Operation.ADD_VALUE), slotGroup);
		if (material.value().knockbackResistance() > 0) {
			builder.add(Attributes.KNOCKBACK_RESISTANCE,
					new AttributeModifier(primaryModId,
							material.value().knockbackResistance(),
							AttributeModifier.Operation.ADD_VALUE), slotGroup);
		}
		armorGearConfig.getAttributes().forEach(attributeModifier -> {
			Holder<Attribute> attribute = attributeModifier.getAttributeHolder();
			if (attribute != null) {
				ResourceLocation attrId = ResourceLocationHelper.modLoc("armor_attr_" + attributeModifier.getAttributeResourceLocation().getPath());
				builder.add(attribute, new AttributeModifier(attrId,
						attributeModifier.getAmount(), attributeModifier.getOperation()), slotGroup);
			}
		});
		defaultModifiers = builder.build();
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return armorGearConfig != null ? armorGearConfig.getDurabilityForType(getType()) : super.getMaxDamage(stack);
	}

	public ArmorGearConfig getGearConfig() {
		return armorGearConfig;
	}

	@Override
	public boolean isUnique() {
		return armorGearConfig != null && armorGearConfig.isUnique();
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return defaultModifiers != null ? defaultModifiers : super.getDefaultAttributeModifiers(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, list, flag);
		if (armorSet != null) {
			DescriptionHelper.addLoreDescription(list, armorSet);
		} else {
			DescriptionHelper.addLoreDescription(list, BuiltInRegistries.ITEM.getKey(this));
		}
	}

	public Rarity getRarity(ItemStack pStack) {
		return getGearConfig() != null ? getGearConfig().getRarity() : Rarity.COMMON;
	}

	public ResourceLocation getArmorSet() {
		return armorSet;
	}

	protected AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", 20, this::predicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return factory;
	}

	private PlayState predicate(AnimationState<?> event) {
		event.getController().setAnimation(RawAnimation.begin().then("idle", LoopType.LOOP));
		return PlayState.CONTINUE;
	}

	public ResourceLocation getModelLocation() {
		return modelLocation;
	}

	public ResourceLocation getTextureLocation() {
		return textureLocation;
	}

	public ResourceLocation getAnimationFileLocation() {
		return animationFileLocation;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			private GeoArmorRenderer<?> renderer;

			@Override
			public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack,
					EquipmentSlot armorSlot, HumanoidModel<?> _default) {
				if (renderer == null) {
					renderer = new ArmorGearRenderer<>();
				}
				renderer.prepForRender(entityLiving, itemStack, armorSlot, _default);
				return (HumanoidModel<?>) renderer;
			}
		});
	}
}
