package net.firefoxsalesman.dungeonslibs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;

public class Codecs {

	public static final Codec<BuiltInEnchantments.Entry> ENCHANTMENT_DATA_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("enchantment").forGetter(BuiltInEnchantments.Entry::id),
			Codec.INT.fieldOf("level").forGetter(BuiltInEnchantments.Entry::level)
	).apply(instance, BuiltInEnchantments.Entry::new));

	public static final Codec<Rarity> ITEM_RARITY_CODEC = Codec.STRING.flatComapMap(Rarity::valueOf,
			d -> DataResult.success(d.name()));
}
