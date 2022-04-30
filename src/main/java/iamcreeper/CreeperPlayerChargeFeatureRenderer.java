package iamcreeper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class CreeperPlayerChargeFeatureRenderer extends FeatureRenderer<PlayerEntity, EntityModel<PlayerEntity>> {

    private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
    private final CreeperEntityModel<PlayerEntity> model;

    public CreeperPlayerChargeFeatureRenderer(FeatureRendererContext<PlayerEntity, EntityModel<PlayerEntity>> featureRendererContext, EntityModelLoader entityModelLoader) {
        super(featureRendererContext);
        this.model = new CreeperEntityModel<>(entityModelLoader.getModelPart(EntityModelLayers.CREEPER_ARMOR));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!((PlayerInterface) entity).isCharged() || !((PlayerInterface) entity).isCreeper()) {
            return;
        }
        float f = (float) entity.age + tickDelta;
        EntityModel<PlayerEntity> entityModel = this.getEnergySwirlModel();
        entityModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
        this.getContextModel().copyStateTo(entityModel);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEnergySwirl(this.getEnergySwirlTexture(), this.getEnergySwirlX(f) % 1.0f, f * 0.01f % 1.0f));
        entityModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        entityModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0.5f, 0.5f, 0.5f, 1.0f);
    }

    protected float getEnergySwirlX(float partialAge) {
        return partialAge * 0.01f;
    }

    protected Identifier getEnergySwirlTexture() {
        return SKIN;
    }

    protected EntityModel<PlayerEntity> getEnergySwirlModel() {
        return this.model;
    }
}
