package iamcreeper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CreeperEffect extends StatusEffect {

    protected CreeperEffect(StatusEffectCategory statusEffectCategory, int i) {
        super(statusEffectCategory, i);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!entity.world.isClient() && entity instanceof PlayerEntity) {
            ((PlayerInterface) entity).setCreeper(true);
            if(amplifier > 0) {
                ((PlayerInterface) entity).ignite();
            }
        }
        if(!entity.world.isClient() && entity instanceof CreeperEntity) {
            if(amplifier > 0) {
                ((CreeperEntity) entity).ignite();
            }
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        if(!entity.world.isClient() && entity instanceof PlayerEntity) {
            ((PlayerInterface) entity).setCreeper(false);
        }
    }
}
