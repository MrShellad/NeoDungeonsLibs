package net.firefoxsalesman.dungeonslibs.client.renderer.layers;

import net.firefoxsalesman.dungeonslibs.client.ConvenientModel;
import net.firefoxsalesman.dungeonslibs.client.KeyframeEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class GenericEyeLayer<T extends Entity & KeyframeEntity, M extends ConvenientModel<T>> extends EyesLayer<T, M> {
	private RenderType eyes;

	public GenericEyeLayer(RenderLayerParent<T, M> parent, ResourceLocation path) {
		super(parent);
		eyes = RenderType.eyes(path);
	}

	public RenderType renderType() {
		return eyes;
	}

}
