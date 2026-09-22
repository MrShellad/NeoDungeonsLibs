package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GearConfigAttributeModifier {

	public static final Codec<AttributeModifier.Operation> ATTRIBUTE_MODIFIER_OPERATION_CODEC = Codec.INT
			.flatComapMap(AttributeModifier.Operation.BY_ID::apply, d -> DataResult.success(d.id()));

	public static final Codec<GearConfigAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					ResourceLocation.CODEC.fieldOf("attribute").forGetter(
							GearConfigAttributeModifier::getAttributeResourceLocation),
					Codec.DOUBLE.fieldOf("amount")
							.forGetter(GearConfigAttributeModifier::getAmount),
					ATTRIBUTE_MODIFIER_OPERATION_CODEC.fieldOf("operation")
							.forGetter(GearConfigAttributeModifier::getOperation))
			.apply(instance, GearConfigAttributeModifier::new));

	private final ResourceLocation attributeResourceLocation;
	private final double amount;
	private final AttributeModifier.Operation operation;

	public GearConfigAttributeModifier(ResourceLocation attributeResourceLocation, double amount,
			AttributeModifier.Operation operation) {
		this.attributeResourceLocation = attributeResourceLocation;
		this.amount = amount;
		this.operation = operation;
	}

	public ResourceLocation getAttributeResourceLocation() {
		return attributeResourceLocation;
	}

	public double getAmount() {
		return amount;
	}

	public AttributeModifier.Operation getOperation() {
		return operation;
	}

	public AttributeModifier toAttributeModifier(ResourceLocation id) {
		return new AttributeModifier(id, amount, operation);
	}

	public Holder<Attribute> getAttributeHolder() {
		return BuiltInRegistries.ATTRIBUTE.getHolder(attributeResourceLocation).orElse(null);
	}

	public Attribute getAttribute() {
		return BuiltInRegistries.ATTRIBUTE.get(attributeResourceLocation);
	}
}
