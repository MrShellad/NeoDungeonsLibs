package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.firefoxsalesman.dungeonslibs.items.interfaces.IComboWeapon;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IMeleeWeapon;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IUniqueGear;
import net.firefoxsalesman.dungeonslibs.mixin.TieredItemAccessor;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;

public class MeleeGear extends TieredItem
		implements IMeleeWeapon, IComboWeapon, IReloadableGear, IUniqueGear {

	private ItemAttributeModifiers defaultModifiers;
	private MeleeGearConfig meleeGearConfig;
	private float attackDamage;

	public MeleeGear(Item.Properties properties) {
		super(Tiers.WOOD, properties);
		reload();
	}

	@Override
	public void reload() {
		meleeGearConfig = MeleeGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
		((TieredItemAccessor) this).setTier(meleeGearConfig.getWeaponMaterial());

		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		meleeGearConfig.getAttributes().forEach(attributeModifier -> {
			Holder<Attribute> attribute = attributeModifier.getAttributeHolder();
			if (attribute != null) {
				ResourceLocation id;
				if (attribute.is(ATTACK_DAMAGE)) {
					id = BASE_ATTACK_DAMAGE_ID;
					attackDamage = (float) attributeModifier.getAmount() + getTier().getAttackDamageBonus();
					builder.add(attribute, new AttributeModifier(id,
							attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
				} else if (attribute.is(ATTACK_SPEED)) {
					id = BASE_ATTACK_SPEED_ID;
					builder.add(attribute, new AttributeModifier(id,
							attributeModifier.getAmount(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
				} else {
					id = ResourceLocationHelper.modLoc("weapon_attr_" + attributeModifier.getAttributeResourceLocation().getPath());
					builder.add(attribute, new AttributeModifier(id,
							attributeModifier.getAmount(), attributeModifier.getOperation()), EquipmentSlotGroup.MAINHAND);
				}
			}
		});
		defaultModifiers = builder.build();
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return getTier().getUses();
	}

	public MeleeGearConfig getGearConfig() {
		return meleeGearConfig;
	}

	@Override
	public int getComboLength(ItemStack stack, LivingEntity attacker) {
		return getGearConfig().getComboLength();
	}

	@Override
	public boolean isUnique() {
		return meleeGearConfig != null && meleeGearConfig.isUnique();
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return defaultModifiers != null ? defaultModifiers : super.getDefaultAttributeModifiers(stack);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, list, flag);
		DescriptionHelper.addFullDescription(list, stack);
	}

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return getGearConfig() != null && getGearConfig().isDisablesShield();
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
		return true;
	}

	public float getDamage() {
		return attackDamage;
	}

	public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		return !pPlayer.isCreative();
	}

	@Override
	public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos,
			LivingEntity livingEntity) {
		if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
			itemStack.hurtAndBreak(1, livingEntity, EquipmentSlot.MAINHAND);
		}

		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return state.is(Blocks.COBWEB) || state.is(BlockTags.LEAVES);
	}

	@Override
	public float getDestroySpeed(ItemStack itemStack, BlockState pBlockState) {
		if (pBlockState.is(Blocks.COBWEB) || pBlockState.is(BlockTags.LEAVES)) {
			return 15.0F;
		} else {
			MapColor m = pBlockState.getBlock().defaultMapColor();
			PushReaction p = pBlockState.getPistonPushReaction();
			return !(m.equals(MapColor.PLANT) && p.equals(PushReaction.DESTROY)) ? 1.0F : 1.5F;
		}
	}

	public Rarity getRarity(ItemStack pStack) {
		return getGearConfig() != null ? getGearConfig().getRarity() : Rarity.COMMON;
	}
}
