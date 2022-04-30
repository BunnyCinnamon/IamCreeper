package iamcreeper.mixin;

import iamcreeper.Mod;
import iamcreeper.PlayerInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingRenderMixin {

    @Shadow
    protected EntityModel<LivingEntity> model;
    protected EntityModel<LivingEntity> modelCreeper;
    @Unique
    protected LivingEntity livingEntity;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void init(EntityRendererFactory.Context context, EntityModel<LivingEntity> entityModel, float f, CallbackInfo ci) {
        this.modelCreeper = new CreeperEntityModel<>(context.getPart(EntityModelLayers.CREEPER));
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "HEAD"))
    public void init(LivingEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        this.livingEntity = livingEntity;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;model:Lnet/minecraft/client/render/entity/model/EntityModel;", value = "FIELD"), require = 6)
    public EntityModel<LivingEntity> render(LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> instance) {
        if (this.livingEntity instanceof PlayerEntity && ((PlayerInterface) this.livingEntity).isCreeper()) {
            return this.modelCreeper;
        } else {
            return this.model;
        }
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getAnimationCounter(Lnet/minecraft/entity/LivingEntity;F)F", value = "INVOKE"))
    public float getAnimationCounter(LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> instance, LivingEntity entity, float tickDelta) {
        if (this.livingEntity instanceof PlayerEntity && ((PlayerInterface) this.livingEntity).isCreeper()) {
            float g = Mod.getClientFuseTime(entity, tickDelta);
            if ((int)(g * 10.0f) % 2 == 0) {
                return 0.0f;
            }
            return MathHelper.clamp(g, 0.5f, 1.0f);
        } else {
            return getAnimationCounter(entity, tickDelta);
        }
    }

    @Shadow
    protected abstract float getAnimationCounter(LivingEntity entity, float tickDelta);
}
