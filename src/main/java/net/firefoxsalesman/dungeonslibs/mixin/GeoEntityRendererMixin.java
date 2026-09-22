package net.firefoxsalesman.dungeonslibs.mixin;

import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMob;
import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMobHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;

import static net.firefoxsalesman.dungeonslibs.entities.elite.EliteMobConfig.EMPTY_TEXTURE;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> extends EntityRenderer<T>
		implements GeoRenderer<T> {

	protected GeoEntityRendererMixin(EntityRendererProvider.Context p_i46179_1_) {
		super(p_i46179_1_);
	}

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/resources/ResourceLocation;", at = @At("RETURN"), cancellable = true)
	private void dungeonsLibraries_bindEliteTexture(T instance, CallbackInfoReturnable<ResourceLocation> cir) {
		EliteMob cap = EliteMobHelper.getEliteMobCapability(instance);
		if (cap.isElite() && !cap.getTexture().equals(EMPTY_TEXTURE)) {
			cir.setReturnValue(cap.getTexture());
		}
	}
}
