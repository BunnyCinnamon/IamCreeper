package iamcreeper.mixin;

import iamcreeper.CreeperPlayerChargeFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.CreeperChargeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class ModelOverlayMixin extends LivingEntityRenderer<PlayerEntity, EntityModel<PlayerEntity>> implements FeatureRendererContext<PlayerEntity, EntityModel<PlayerEntity>> {

    public ModelOverlayMixin(EntityRendererFactory.Context context, EntityModel<PlayerEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void init(EntityRendererFactory.Context context, boolean bl, CallbackInfo ci) {
        this.addFeature(new CreeperPlayerChargeFeatureRenderer(this, context.getModelLoader()));
    }
}
