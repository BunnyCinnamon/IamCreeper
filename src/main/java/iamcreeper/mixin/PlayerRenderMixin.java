package iamcreeper.mixin;

import iamcreeper.Mod;
import iamcreeper.PlayerInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(value= EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRenderMixin {

    private static final Identifier TEXTURE = new Identifier("textures/entity/creeper/creeper.png");

    @Redirect(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;setModelPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)V", value = "INVOKE"))
    public void render(PlayerEntityRenderer instance, AbstractClientPlayerEntity player) {
        if (player != null && ((PlayerInterface) player).isCreeper()) {

        } else {
            setModelPose(player);
        }
    }

    @Inject(method = "scale(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At(value = "HEAD"), cancellable = true)
    public void scale(AbstractClientPlayerEntity entity, MatrixStack matrices, float amount, CallbackInfo ci) {
        if (entity != null && ((PlayerInterface) entity).isCreeper()) {
            float g = Mod.getClientFuseTime(entity, MinecraftClient.getInstance().getTickDelta());
            float h = 1.0f + MathHelper.sin(g * 100.0f) * g * 0.01f;
            g = MathHelper.clamp(g, 0.0f, 1.0f);
            g *= g;
            g *= g;
            float i = (1.0f + g * 0.4f) * h;
            float j = (1.0f + g * 0.1f) / h;
            matrices.scale(i, j, i);
            ci.cancel();
        }
    }

    @Redirect(method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;", at = @At(target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTexture()Lnet/minecraft/util/Identifier;", value = "INVOKE"))
    public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        if (abstractClientPlayerEntity != null && ((PlayerInterface) abstractClientPlayerEntity).isCreeper()) {
            return PlayerRenderMixin.TEXTURE;
        } else {
            return abstractClientPlayerEntity.getSkinTexture();
        }
    }

    @Redirect(method = "renderRightArm", at = @At(target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V", value = "INVOKE"))
    public void renderArmA(PlayerEntityRenderer instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        if (player != null && ((PlayerInterface) player).isCreeper()) {

        } else {
            renderArm(matrices, vertexConsumers, light, player, arm, sleeve);
        }
    }

    @Redirect(method = "renderLeftArm", at = @At(target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;renderArm(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/model/ModelPart;)V", value = "INVOKE"))
    public void renderArmB(PlayerEntityRenderer instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        if (player != null && ((PlayerInterface) player).isCreeper()) {

        } else {
            renderArm(matrices, vertexConsumers, light, player, arm, sleeve);
        }
    }

    @Shadow
    protected abstract void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve);

    @Shadow
    protected abstract void setModelPose(AbstractClientPlayerEntity player);
}
