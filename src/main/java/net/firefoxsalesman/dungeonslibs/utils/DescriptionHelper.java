package net.firefoxsalesman.dungeonslibs.utils;

import com.google.common.collect.Multimap;
import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantments;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantmentsHelper;
import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfigRegistry.GEAR_CONFIG_BUILTIN_RESOURCELOCATION;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID, value = Dist.CLIENT)
public class DescriptionHelper {

	private static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = new DecimalFormat("#.##",
			DecimalFormatSymbols.getInstance(Locale.ROOT));

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		BuiltInEnchantments cap = BuiltInEnchantmentsHelper
				.getBuiltInEnchantmentsCapability(event.getItemStack());
		List<BuiltInEnchantments.Entry> builtInEnchantments = cap
				.getBuiltInEnchantments(GEAR_CONFIG_BUILTIN_RESOURCELOCATION);
		builtInEnchantments.forEach(entry -> {
			Component name = null;
			if (event.getContext().registries() != null) {
				name = event.getContext().registries().lookup(Registries.ENCHANTMENT)
						.flatMap(r -> r.get(ResourceKey.create(Registries.ENCHANTMENT, entry.id())))
						.map(h -> Enchantment.getFullname(h, entry.level()))
						.orElse(null);
			}
			if (name == null) {
				name = Component.literal(entry.id().getPath() + " " + entry.level());
			}
			event.getToolTip().add(name.copy().withStyle(Style.EMPTY.withColor(0xFF8100)));
		});
	}

	public static void addLoreDescription(List<Component> list, ResourceLocation registryName) {
		list.add(Component.translatable(
				"lore." + registryName.getNamespace() + "." + registryName.getPath())
				.withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC));
	}

	public static void addFullDescription(List<Component> list, ItemStack itemStack) {
		ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
		addLoreDescription(list, registryName);
	}

	public static void addArtifactDescription(List<Component> list, ItemStack itemStack) {
		ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
		if (registryName == null)
			return;
		addLoreDescription(list, registryName);
		addArtifactInfo(list, itemStack);
		addArtifactAttributeInfo(list, itemStack);
	}

	private static void addArtifactAttributeInfo(List<Component> list, ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof ArtifactItem artifactItem))
			return;
		Multimap<Holder<Attribute>, AttributeModifier> multimap = artifactItem.getArtifactAttributeModifiers();
		if (!multimap.isEmpty()) {
			list.add(CommonComponents.EMPTY);
			list.add(Component.translatable("item.modifiers.artifact").withStyle(ChatFormatting.GRAY));

			for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : multimap.entries()) {
				AttributeModifier attributemodifier = entry.getValue();
				double d0 = attributemodifier.amount();

				double d1;
				if (attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE
						&& attributemodifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
					if (entry.getKey().is(Attributes.KNOCKBACK_RESISTANCE)) {
						d1 = d0 * 10.0D;
					} else {
						d1 = d0;
					}
				} else {
					d1 = d0 * 100.0D;
				}

				if (d0 > 0.0D) {
					list.add(Component.translatable(
							"attribute.modifier.plus."
									+ attributemodifier.operation().id(),
							ATTRIBUTE_MODIFIER_FORMAT.format(d1),
							Component.translatable(entry.getKey().value().getDescriptionId()))
							.withStyle(ChatFormatting.BLUE));
				} else if (d0 < 0.0D) {
					d1 *= -1.0D;
					list.add(Component.translatable(
							"attribute.modifier.take."
									+ attributemodifier.operation().id(),
							ATTRIBUTE_MODIFIER_FORMAT.format(d1),
							Component.translatable(entry.getKey().value().getDescriptionId()))
							.withStyle(ChatFormatting.RED));
				}
			}
		}
	}

	public static void addArtifactInfo(List<Component> list, ItemStack itemStack) {
		if (itemStack.getItem() instanceof ArtifactItem) {

			list.add(Component.translatable(
					"artifact.dungeonslibs.base")
					.withStyle(ChatFormatting.DARK_AQUA));

			ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
			list.add(Component.translatable(
					"ability." + registryName.getNamespace() + "." + registryName.getPath())
					.withStyle(ChatFormatting.GREEN));

			ArtifactItem artifactItem = (ArtifactItem) itemStack.getItem();
			int durationInSeconds = artifactItem.getDurationInSeconds();
			int cooldownInSeconds = artifactItem.getCooldownInSeconds();

			if (durationInSeconds > 0) {
				list.add(Component.translatable(
						"artifact.dungeonslibs.duration", durationInSeconds)
						.withStyle(ChatFormatting.BLUE));
			}
			if (cooldownInSeconds > 0) {
				list.add(Component.translatable(
						"artifact.dungeonslibs.cooldown", cooldownInSeconds)
						.withStyle(ChatFormatting.BLUE));
			}
		}
	}
}
