package net.firefoxsalesman.dungeonslibs.items.artifacts;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.firefoxsalesman.dungeonslibs.event.ArtifactEvent;
import net.firefoxsalesman.dungeonslibs.items.artifacts.config.ArtifactGearConfig;
import net.firefoxsalesman.dungeonslibs.items.artifacts.config.ArtifactGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.firefoxsalesman.dungeonslibs.mixin.CooldownAccessor;
import net.firefoxsalesman.dungeonslibs.mixin.ItemCooldownsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.attribute.AttributeRegistry.ARTIFACT_COOLDOWN_MULTIPLIER;
import static net.firefoxsalesman.dungeonslibs.items.ItemTagWrappers.ARTIFACT_REPAIR_ITEMS;

public abstract class ArtifactItem extends Item implements ICurioItem, IReloadableGear {
	protected boolean procOnItemUse = false;
	private ArtifactGearConfig artifactGearConfig;

	public ArtifactItem(Properties properties) {
		super(properties.durability(64));
		reload();
	}

	@Override
	public void reload() {
		artifactGearConfig = ArtifactGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return artifactGearConfig != null ? artifactGearConfig.getDurability() : super.getMaxDamage(stack);
	}

	@Override
	public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
		return true;
	}

	public static void putArtifactOnCooldown(Player playerIn, Item item) {
		int cooldownInTicks = item instanceof ArtifactItem ? ((ArtifactItem) item).getCooldownInSeconds() * 20
				: 0;

		AttributeInstance artifactCooldownMultiplierAttribute = playerIn
				.getAttribute(ARTIFACT_COOLDOWN_MULTIPLIER);
		double attributeModifier = artifactCooldownMultiplierAttribute != null
				? artifactCooldownMultiplierAttribute.getValue()
				: 1.0D;
		playerIn.getCooldowns().addCooldown(item, Math.max(0, (int) (cooldownInTicks * attributeModifier)));
	}

	public static void triggerSynergy(Player player, ItemStack stack) {
		ArtifactEvent.Activated event = new ArtifactEvent.Activated(player, stack);
		net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
	}

	public static void reduceArtifactCooldowns(Player playerEntity, double reductionInSeconds) {
		ItemCooldowns cooldownsObj = playerEntity.getCooldowns();
		Map<Item, ItemCooldowns.CooldownInstance> cooldowns = ((ItemCooldownsAccessor) cooldownsObj).getCooldowns();
		for (Item item : cooldowns.keySet()) {
			if (item instanceof ArtifactItem) {
				int currentTicks = ((ItemCooldownsAccessor) cooldownsObj).getTickCount();
				int expireTicks = ((CooldownAccessor) cooldowns.get(item)).getEndTime();
				int duration = expireTicks - currentTicks;
				playerEntity.getCooldowns().addCooldown(item,
						Math.max(0, duration - (int) (reductionInSeconds * 20)));
			}
		}
	}

	public Rarity getRarity(ItemStack itemStack) {
		return Rarity.RARE;
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
		return repair.is(ARTIFACT_REPAIR_ITEMS) || super.isValidRepairItem(toRepair, repair);
	}

	public InteractionResultHolder<ItemStack> activateArtifact(ArtifactUseContext artifactUseContext) {
		if (artifactUseContext.getPlayer() != null) {
			ItemStack itemStack = artifactUseContext.getItemStack();
			if (artifactUseContext.getPlayer().getCooldowns().isOnCooldown(itemStack.getItem())) {
				return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
			}
		}
		InteractionResultHolder<ItemStack> procResult = procArtifact(artifactUseContext);
		if (procResult.getResult().consumesAction() && artifactUseContext.getPlayer() != null
				&& !artifactUseContext.getLevel().isClientSide) {
			triggerSynergy(artifactUseContext.getPlayer(), artifactUseContext.getItemStack());
		}
		return procResult;
	}

	public abstract InteractionResultHolder<ItemStack> procArtifact(ArtifactUseContext iuc);

	public int getCooldownInSeconds() {
		return artifactGearConfig != null ? artifactGearConfig.getCooldown() : 0;
	}

	public int getDurationInSeconds() {
		return artifactGearConfig != null ? artifactGearConfig.getDuration() : 0;
	}

	public void stopUsingArtifact(LivingEntity livingEntity) {
	}

	@Override
	public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
			ResourceLocation id, ItemStack stack) {
		Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
		if (artifactGearConfig != null) {
			artifactGearConfig.getAttributes().forEach(attributeModifier -> {
				Holder<Attribute> attrHolder = attributeModifier.getAttributeHolder();
				if (attrHolder != null) {
					ResourceLocation modId = ResourceLocationHelper.modLoc("curio_" + slotContext.index() + "_" + attributeModifier.getAttributeResourceLocation().getPath());
					modifiers.put(attrHolder, new AttributeModifier(modId, attributeModifier.getAmount(), attributeModifier.getOperation()));
				}
			});
		}
		return modifiers;
	}

	public Multimap<Holder<Attribute>, AttributeModifier> getArtifactAttributeModifiers() {
		Multimap<Holder<Attribute>, AttributeModifier> modifiers = HashMultimap.create();
		if (artifactGearConfig != null) {
			artifactGearConfig.getAttributes().forEach(attributeModifier -> {
				Holder<Attribute> attrHolder = attributeModifier.getAttributeHolder();
				if (attrHolder != null) {
					ResourceLocation modId = ResourceLocationHelper.modLoc("artifact_" + attributeModifier.getAttributeResourceLocation().getPath());
					modifiers.put(attrHolder, new AttributeModifier(modId, attributeModifier.getAmount(), attributeModifier.getOperation()));
				}
			});
		}
		return modifiers;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, context, list, flag);
		DescriptionHelper.addArtifactDescription(list, stack);
	}
}
