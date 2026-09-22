package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ArmorSet {
	private final ResourceLocation registryName;
	private final Supplier<? extends Item> head;
	private final Supplier<? extends Item> chest;
	private final Supplier<? extends Item> legs;
	private final Supplier<? extends Item> feet;

	public ArmorSet(ResourceLocation registryName, Supplier<? extends Item> head, Supplier<? extends Item> chest,
			Supplier<? extends Item> legs, Supplier<? extends Item> feet) {
		this.registryName = registryName;
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.feet = feet;
	}

	public ResourceLocation getRegistryName() {
		return registryName;
	}

	public Supplier<? extends Item> getHead() {
		return head;
	}

	public Supplier<? extends Item> getChest() {
		return chest;
	}

	public Supplier<? extends Item> getLegs() {
		return legs;
	}

	public Supplier<? extends Item> getFeet() {
		return feet;
	}
}
