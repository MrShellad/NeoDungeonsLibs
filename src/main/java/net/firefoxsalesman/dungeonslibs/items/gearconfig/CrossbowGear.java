package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.firefoxsalesman.dungeonslibs.event.CrossbowEvent;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IRangedWeapon;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IUniqueGear;
import net.firefoxsalesman.dungeonslibs.mixin.CrossbowItemInvoker;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;

public class CrossbowGear extends CrossbowItem implements IRangedWeapon, IReloadableGear, IUniqueGear {
	private ItemAttributeModifiers defaultModifiers;
	private BowGearConfig crossbowGearConfig;
	private boolean startSoundPlayed = false;
	private boolean midLoadSoundPlayed = false;

	public CrossbowGear(Properties builder) {
		super(builder.durability(384));
		reload();
	}

	@Override
	public void reload() {
		crossbowGearConfig = CrossbowGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		crossbowGearConfig.getAttributes().forEach(attributeModifier -> {
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
		return crossbowGearConfig != null ? crossbowGearConfig.getDurability() : super.getMaxDamage(stack);
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return defaultModifiers != null ? defaultModifiers : super.getDefaultAttributeModifiers(stack);
	}

	public float getDefaultChargeTime() {
		return crossbowGearConfig != null ? crossbowGearConfig.getDefaultChargeTime() : 20.0F;
	}

	@Override
	public void onUseTick(Level world, LivingEntity livingEntity, ItemStack stack, int timeLeft) {
		if (!world.isClientSide) {
			float chargeTime = (float) (stack.getUseDuration(livingEntity) - timeLeft)
					/ getCrossbowChargeTime(livingEntity, stack);
			if (chargeTime < 0.2F) {
				this.startSoundPlayed = false;
				this.midLoadSoundPlayed = false;
			}

			if (chargeTime >= 0.2F && !this.startSoundPlayed && chargeTime < 1.0F) {
				this.startSoundPlayed = true;
				SoundEvent startSound = SoundEvents.CROSSBOW_LOADING_START.value();
				world.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
						startSound, SoundSource.PLAYERS, 0.5F, 1.0F);
			}

			if (chargeTime >= 0.5F && !this.midLoadSoundPlayed && chargeTime < 1.0F) {
				this.midLoadSoundPlayed = true;
				SoundEvent midSound = SoundEvents.CROSSBOW_LOADING_MIDDLE.value();
				world.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
						midSound, SoundSource.PLAYERS, 0.5F, 1.0F);
			}
		}
	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity livingEntity, int timeLeft) {
		float chargeTime = getCrossbowChargeTime(livingEntity, stack) + 3 - timeLeft;
		float getCharge = getCrossbowCharge(livingEntity, chargeTime, stack);
		if (getCharge >= 1.0F && !isCharged(stack)
				&& CrossbowItemInvoker.callTryLoadProjectiles(livingEntity, stack)) {
			SoundSource soundSource = livingEntity instanceof Player ? SoundSource.PLAYERS
					: SoundSource.HOSTILE;
			worldIn.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
					SoundEvents.CROSSBOW_LOADING_END.value(), soundSource, 1.0F,
					1.0F / (livingEntity.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
		}
	}

	public float getCrossbowCharge(LivingEntity livingEntity, float useTime, ItemStack stack) {
		float crossbowChargeTime = getCrossbowChargeTime(livingEntity, stack);
		float charge = useTime / crossbowChargeTime;
		if (charge > 1.0F) {
			charge = 1.0F;
		}
		return charge;
	}

	public float getCrossbowChargeTime(@Nullable LivingEntity livingEntity, ItemStack stack) {
		float defaultCharge = getDefaultChargeTime();
		CrossbowEvent.ChargeTime event = new CrossbowEvent.ChargeTime(livingEntity, stack, defaultCharge);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
		float baseTime = event.getChargeTime();
		float modifiedTime = livingEntity != null
				? EnchantmentHelper.modifyCrossbowChargingTime(stack, livingEntity, baseTime / 20.0F) * 20.0F
				: baseTime;
		return Math.max(modifiedTime, 1.0F);
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return (int) getCrossbowChargeTime(entity, stack) + 3;
	}

	public Rarity getRarity(ItemStack pStack) {
		return getGearConfig() != null ? getGearConfig().getRarity() : Rarity.COMMON;
	}

	@Override
	public boolean useOnRelease(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isUnique() {
		return crossbowGearConfig != null && crossbowGearConfig.isUnique();
	}

	public BowGearConfig getGearConfig() {
		return crossbowGearConfig;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, list, flag);
		DescriptionHelper.addFullDescription(list, stack);
	}
}
