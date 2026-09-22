package net.firefoxsalesman.dungeonslibs.client.models;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import net.firefoxsalesman.dungeonslibs.entities.SummonSpotEntity;
import static net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper.modLoc;

public class SummonSpotModel<T extends SummonSpotEntity> extends GeoModel<T> {

	@Override
	public ResourceLocation getAnimationResource(T entity) {
		if (entity.getSummonType() == 0) {
			return modLoc("animations/illusioner_summon_spot.animation.json");
		} else if (entity.getSummonType() == 1) {
			return modLoc("animations/wildfire_summon_spot.animation.json");
		} else if (entity.getSummonType() == 2) {
			return modLoc("animations/illusioner_summon_spot.animation.json");
		} else if (entity.getSummonType() == 3) {
			return modLoc("animations/illusioner_summon_spot.animation.json");
		} else {
			return modLoc("animations/illusioner_summon_spot.animation.json");
		}
	}

	@Override
	public ResourceLocation getModelResource(T entity) {
		if (entity.getSummonType() == 0) {
			return modLoc("geo/illusioner_summon_spot.geo.json");
		} else if (entity.getSummonType() == 1) {
			return modLoc("geo/wildfire_summon_spot.geo.json");
		} else if (entity.getSummonType() == 2) {
			return modLoc("geo/illusioner_summon_spot.geo.json");
		} else if (entity.getSummonType() == 3) {
			return modLoc("geo/illusioner_summon_spot.geo.json");
		} else {
			return modLoc("geo/illusioner_summon_spot.geo.json");
		}
	}

	@Override
	public ResourceLocation getTextureResource(T entity) {
		// TODO We don't actually have an illusioner_summon_spot texture
		if (entity.getSummonType() == 0) {
			return modLoc("textures/entity/illusioner_summon_spot.png");
		} else if (entity.getSummonType() == 1) {
			return modLoc("textures/entity/wildfire_summon_spot.png");
		} else if (entity.getSummonType() == 2) {
			return modLoc("textures/entity/necromancer_summon_spot.png");
		} else if (entity.getSummonType() == 3) {
			return modLoc("textures/entity/mage_summon_spot.png");
		} else {
			return modLoc("textures/entity/illusioner_summon_spot.png");
		}
	}
}
